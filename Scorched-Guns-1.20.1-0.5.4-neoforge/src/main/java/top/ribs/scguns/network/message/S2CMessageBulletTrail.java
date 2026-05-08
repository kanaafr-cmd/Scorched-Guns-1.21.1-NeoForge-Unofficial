package top.ribs.scguns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.client.network.ClientPlayHandler;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.network.BufferUtil;

/**
 * Author: MrCrayfish
 */
public class S2CMessageBulletTrail extends PlayMessage<S2CMessageBulletTrail>
{
    private int[] entityIds;
    private Vec3[] positions;
    private Vec3[] motions;
    private ItemStack item;
    private int trailColor;
    private double trailLengthMultiplier;
    private int life;
    private double gravity;
    private int shooterId;
    private boolean enchanted;
    private ParticleOptions particleData;
    private boolean visible = true;
    private double trailThickness = 1.0D;

    public S2CMessageBulletTrail() {}

    public S2CMessageBulletTrail(ProjectileEntity[] spawnedProjectiles, Gun.Projectile projectileProps, int shooterId, ParticleOptions particleData)
    {
        this(spawnedProjectiles, projectileProps, shooterId, particleData, projectileProps.isVisible());
    }

    public S2CMessageBulletTrail(ProjectileEntity[] spawnedProjectiles, Gun.Projectile projectileProps, int shooterId, ParticleOptions particleData, boolean visible)
    {
        this.positions = new Vec3[spawnedProjectiles.length];
        this.motions = new Vec3[spawnedProjectiles.length];
        this.entityIds = new int[spawnedProjectiles.length];
        for(int i = 0; i < spawnedProjectiles.length; i++)
        {
            ProjectileEntity projectile = spawnedProjectiles[i];
            this.positions[i] = projectile.position();
            this.motions[i] = projectile.getDeltaMovement();
            this.entityIds[i] = projectile.getId();
        }
        this.item = spawnedProjectiles[0].getItem();
        this.enchanted = spawnedProjectiles[0].getWeapon().isEnchanted();
        this.trailColor = this.enchanted ? 0x9C71FF : projectileProps.getTrailColor();
        this.trailLengthMultiplier = projectileProps.getTrailLengthMultiplier();
        this.life = projectileProps.getLife();
        this.gravity = spawnedProjectiles[0].getModifiedGravity(); //It's possible that projectiles have different gravity
        this.shooterId = shooterId;
        this.particleData = particleData;
        this.visible = visible;
        this.trailThickness = projectileProps.getTrailThickness();
    }

    public S2CMessageBulletTrail(int[] entityIds, Vec3[] positions, Vec3[] motions, ItemStack item, int trailColor, double trailLengthMultiplier, int life, double gravity, int shooterId, boolean enchanted, ParticleOptions particleData)
    {
        this(entityIds, positions, motions, item, trailColor, trailLengthMultiplier, life, gravity, shooterId, enchanted, particleData, true, 1.0D);
    }

    public S2CMessageBulletTrail(int[] entityIds, Vec3[] positions, Vec3[] motions, ItemStack item, int trailColor, double trailLengthMultiplier, int life, double gravity, int shooterId, boolean enchanted, ParticleOptions particleData, boolean visible, double trailThickness)
    {
        this.entityIds = entityIds;
        this.positions = positions;
        this.motions = motions;
        this.item = item;
        this.trailColor = trailColor;
        this.trailLengthMultiplier = trailLengthMultiplier;
        this.life = life;
        this.gravity = gravity;
        this.shooterId = shooterId;
        this.enchanted = enchanted;
        this.particleData = particleData;
        this.visible = visible;
        this.trailThickness = trailThickness;
    }

    @Override
    public void encode(S2CMessageBulletTrail message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityIds.length);
        for(int i = 0; i < message.entityIds.length; i++)
        {
            buffer.writeInt(message.entityIds[i]);
            BufferUtil.writeVec3(buffer, message.positions[i]);
            BufferUtil.writeVec3(buffer, message.motions[i]);
        }
        BufferUtil.writeItemStackToBufIgnoreTag(buffer, message.item);
        buffer.writeVarInt(message.trailColor);
        buffer.writeDouble(message.trailLengthMultiplier);
        buffer.writeInt(message.life);
        buffer.writeDouble(message.gravity);
        buffer.writeInt(message.shooterId);
        buffer.writeBoolean(message.enchanted);
        buffer.writeBoolean(message.visible);
        buffer.writeDouble(message.trailThickness);
        buffer.writeVarInt(BuiltInRegistries.PARTICLE_TYPE.getId(message.particleData.getType()));
        writeParticle(buffer, message.particleData);
    }

    @Override
    public S2CMessageBulletTrail decode(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        int[] entityIds = new int[size];
        Vec3[] positions = new Vec3[size];
        Vec3[] motions = new Vec3[size];
        for(int i = 0; i < size; i++)
        {
            entityIds[i] = buffer.readInt();
            positions[i] = BufferUtil.readVec3(buffer);
            motions[i] = BufferUtil.readVec3(buffer);
        }
        ItemStack item = BufferUtil.readItemStackFromBufIgnoreTag(buffer);
        int trailColor = buffer.readVarInt();
        double trailLengthMultiplier = buffer.readDouble();
        int life = buffer.readInt();
        double gravity = buffer.readDouble();
        int shooterId = buffer.readInt();
        boolean enchanted = buffer.readBoolean();
        boolean visible = buffer.readBoolean();
        double trailThickness = buffer.readDouble();
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.byId(buffer.readVarInt());
        if (type == null) type = ParticleTypes.CRIT;
        ParticleOptions particleData = this.readParticle(buffer, type);
        return new S2CMessageBulletTrail(entityIds, positions, motions, item, trailColor, trailLengthMultiplier, life, gravity,shooterId, enchanted, particleData, visible, trailThickness);
    }

    @Override
    public void handle(S2CMessageBulletTrail message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageBulletTrail(message));
        context.setHandled(true);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf buffer, ParticleType<T> type)
    {
        return type.streamCodec().decode((net.minecraft.network.RegistryFriendlyByteBuf) buffer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void writeParticle(FriendlyByteBuf buffer, ParticleOptions particleData) {
        ((ParticleType) particleData.getType()).streamCodec().encode((net.minecraft.network.RegistryFriendlyByteBuf) buffer, particleData);
    }

    public int getCount()
    {
        return this.entityIds.length;
    }

    public int[] getEntityIds()
    {
        return this.entityIds;
    }

    public Vec3[] getPositions()
    {
        return this.positions;
    }

    public Vec3[] getMotions()
    {
        return this.motions;
    }

    public int getTrailColor()
    {
        return this.trailColor;
    }

    public double getTrailLengthMultiplier()
    {
        return this.trailLengthMultiplier;
    }

    public int getLife()
    {
        return this.life;
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    public double getGravity()
    {
        return this.gravity;
    }

    public int getShooterId()
    {
        return this.shooterId;
    }

    public boolean isEnchanted()
    {
        return this.enchanted;
    }

    public ParticleOptions getParticleData()
    {
        return this.particleData;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public double getTrailThickness()
    {
        return this.trailThickness;
    }
}
