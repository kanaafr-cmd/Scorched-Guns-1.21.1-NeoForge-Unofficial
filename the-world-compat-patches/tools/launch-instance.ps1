$ErrorActionPreference = 'Stop'

$Install = 'C:\Users\AlexM\curseforge\minecraft\Install'
$GameDir = "C:\Users\AlexM\curseforge\minecraft\Instances\The World As it's known"
$VersionName = 'neoforge-21.1.228'
$McVersion = '1.21.1'
$Java = Join-Path $Install 'runtime\java-runtime-delta\windows-x64\java-runtime-delta\bin\java.exe'
if (!(Test-Path -LiteralPath $Java)) {
    $Java = 'java'
}

$Vanilla = Get-Content -LiteralPath (Join-Path $Install "versions\$McVersion\$McVersion.json") -Raw | ConvertFrom-Json
$Neo = Get-Content -LiteralPath (Join-Path $Install "versions\$VersionName\$VersionName.json") -Raw | ConvertFrom-Json
$Separator = ';'

function Test-Rules($Rules) {
    if ($null -eq $Rules) {
        return $true
    }
    $Allowed = $false
    foreach ($Rule in $Rules) {
        $OsOk = $true
        if ($Rule.os -and $Rule.os.name -and $Rule.os.name -ne 'windows') {
            $OsOk = $false
        }
        if ($OsOk) {
            $Allowed = ($Rule.action -eq 'allow')
        }
    }
    return $Allowed
}

$Libraries = [System.Collections.Generic.List[string]]::new()
foreach ($Library in @($Vanilla.libraries) + @($Neo.libraries)) {
    if (!(Test-Rules $Library.rules)) {
        continue
    }
    if ($Library.downloads -and $Library.downloads.artifact -and $Library.downloads.artifact.path) {
        $Path = Join-Path (Join-Path $Install 'libraries') ($Library.downloads.artifact.path -replace '/', '\')
        if ((Test-Path -LiteralPath $Path) -and !$Libraries.Contains($Path)) {
            $Libraries.Add($Path)
        }
    }
}

$ClientJar = Join-Path $Install "versions\$VersionName\$VersionName.jar"
if (Test-Path -LiteralPath $ClientJar) {
    $Libraries.Add($ClientJar)
}
$Classpath = ($Libraries -join $Separator)

$Replacements = @{
    '${natives_directory}' = (Join-Path $Install 'natives')
    '${launcher_name}' = 'codex'
    '${launcher_version}' = '1.0'
    '${classpath}' = $Classpath
    '${classpath_separator}' = $Separator
    '${library_directory}' = (Join-Path $Install 'libraries')
    '${version_name}' = $VersionName
    '${auth_player_name}' = 'Dev'
    '${version_type}' = 'release'
    '${game_directory}' = $GameDir
    '${assets_root}' = (Join-Path $Install 'assets')
    '${assets_index_name}' = $Vanilla.assetIndex.id
    '${auth_uuid}' = '00000000000000000000000000000000'
    '${auth_access_token}' = '0'
    '${clientid}' = '0'
    '${auth_xuid}' = '0'
    '${user_type}' = 'legacy'
    '${user_properties}' = '{}'
    '${resolution_width}' = '1280'
    '${resolution_height}' = '720'
    '${quickPlayPath}' = ''
    '${quickPlaySingleplayer}' = ''
    '${quickPlayMultiplayer}' = ''
    '${quickPlayRealms}' = ''
}

function Expand-LauncherArgs($ArgsList) {
    $Out = [System.Collections.Generic.List[string]]::new()
    foreach ($Arg in $ArgsList) {
        if ($Arg -is [string]) {
            $Value = $Arg
            foreach ($Key in $Replacements.Keys) {
                $Value = $Value.Replace($Key, $Replacements[$Key])
            }
            $Out.Add($Value)
        } elseif ($Arg.rules -and (Test-Rules $Arg.rules)) {
            foreach ($Nested in $Arg.value) {
                $Value = [string]$Nested
                foreach ($Key in $Replacements.Keys) {
                    $Value = $Value.Replace($Key, $Replacements[$Key])
                }
                $Out.Add($Value)
            }
        }
    }
    return $Out
}

$JvmArgs = [System.Collections.Generic.List[string]]::new()
$JvmArgs.Add('-Xmx12G')
$JvmArgs.Add('-Xms1G')
foreach ($Arg in (Expand-LauncherArgs $Vanilla.arguments.jvm)) {
    $JvmArgs.Add($Arg)
}
foreach ($Arg in (Expand-LauncherArgs $Neo.arguments.jvm)) {
    $JvmArgs.Add($Arg)
}
$JvmArgs.Add('-cp')
$JvmArgs.Add($Classpath)

$GameArgs = [System.Collections.Generic.List[string]]::new()
foreach ($Arg in (Expand-LauncherArgs $Vanilla.arguments.game)) {
    $GameArgs.Add($Arg)
}
foreach ($Arg in (Expand-LauncherArgs $Neo.arguments.game)) {
    $GameArgs.Add($Arg)
}

$AllArgs = [System.Collections.Generic.List[string]]::new()
foreach ($Arg in $JvmArgs) {
    $AllArgs.Add($Arg)
}
$AllArgs.Add($Neo.mainClass)
foreach ($Arg in $GameArgs) {
    $AllArgs.Add($Arg)
}

Write-Host "Launching $VersionName with $($Libraries.Count) libraries."
Push-Location $GameDir
try {
    & $Java @AllArgs 2>&1 | Tee-Object -FilePath (Join-Path $GameDir 'codex-launch.log')
    Write-Host "EXIT=$LASTEXITCODE"
} finally {
    Pop-Location
}
