param(
  [string]$InstancePath = "C:\Users\AlexM\curseforge\minecraft\Instances\The World As it's known",
  [string]$JarName = "ScorchedGuns-0.5.4-1.21.1.jar"
)

$ErrorActionPreference = "Stop"

$modsDir = Join-Path $InstancePath "mods"
$logsDir = Join-Path $InstancePath "logs"
$latestLog = Join-Path $logsDir "latest.log"
$srcJar = Join-Path $PSScriptRoot "build\libs\$JarName"
$dstJar = Join-Path $modsDir $JarName
$backupDir = Join-Path $modsDir "_backup_scguns"

Write-Host "== Scorched Guns Dev Loop ==" -ForegroundColor Cyan
Write-Host "Project: $PSScriptRoot"
Write-Host "Instance: $InstancePath"
Write-Host ""

Write-Host "[1/4] Building..." -ForegroundColor Yellow
.\gradlew.bat build --stacktrace
if (!(Test-Path $srcJar)) {
  throw "Built jar not found: $srcJar"
}

Write-Host "[2/4] Backing up old jar + deploying new jar..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path $backupDir | Out-Null
if (Test-Path $dstJar) {
  $ts = Get-Date -Format "yyyyMMdd-HHmmss"
  Copy-Item $dstJar (Join-Path $backupDir "$($JarName).$ts.bak") -Force
}
Copy-Item $srcJar $dstJar -Force

$f = Get-Item $dstJar
Write-Host "Deployed: $($f.FullName)"
Write-Host "Size: $($f.Length)  LastWrite: $($f.LastWriteTime)"
Write-Host ""

Write-Host "[3/4] Waiting for you to launch Minecraft..." -ForegroundColor Yellow
Write-Host "After the game loads (or crashes), close it, then press ENTER here."
[void][System.Console]::ReadLine()

Write-Host "[4/4] Collecting log signals..." -ForegroundColor Yellow
if (Test-Path $latestLog) {
  $outDir = Join-Path $PSScriptRoot "run"
  New-Item -ItemType Directory -Force -Path $outDir | Out-Null
  $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
  $snap = Join-Path $outDir "latest-$stamp.log"
  $scgunsOnly = Join-Path $outDir "scguns-only-$stamp.log"
  $scgunsFocus = Join-Path $outDir "scguns-focus-$stamp.log"
  Copy-Item $latestLog $snap -Force

  $allLines = Get-Content $latestLog
  $scgunsLines = $allLines | Select-String -Pattern "\[scguns/" -CaseSensitive:$false | ForEach-Object { $_.Line }
  $focusLines = $allLines |
    Select-String -Pattern "scguns|ads|aim|reload|gun_bench|blueprint|recipe|structure|worldgen|geckolib|attachment" -CaseSensitive:$false |
    ForEach-Object { $_.Line }

  $scgunsLines | Set-Content -Path $scgunsOnly -Encoding UTF8
  $focusLines | Set-Content -Path $scgunsFocus -Encoding UTF8

  Write-Host ""
  Write-Host "Saved log snapshot: $snap" -ForegroundColor Green
  Write-Host "Saved scguns-only log: $scgunsOnly" -ForegroundColor Green
  Write-Host "Saved scguns-focus log: $scgunsFocus" -ForegroundColor Green
  Write-Host ""
  Write-Host "---- Recent scguns/errors from latest.log ----" -ForegroundColor Cyan

  Get-Content $scgunsFocus -Tail 250 |
    Select-String -Pattern "scguns|error|exception|failed|datapack|recipe|worldgen|structure|geckolib|reload|aim|ads" -CaseSensitive:$false |
    Select-Object -Last 200 |
    ForEach-Object { $_.Line }
} else {
  Write-Warning "latest.log not found at: $latestLog"
}

Write-Host ""
Write-Host "Done. Re-run .\dev-loop.ps1 after each code change." -ForegroundColor Green
