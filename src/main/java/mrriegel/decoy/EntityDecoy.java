package mrriegel.decoy;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityDecoy extends EntityCreature {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer> createKey(EntityDecoy.class, DataSerializers.VARINT);
	private static final DataParameter<BlockPos> HOME = EntityDataManager.<BlockPos> createKey(EntityDecoy.class, DataSerializers.BLOCK_POS);

	public EntityDecoy(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 0.9F);
		this.enablePersistence();
		this.isImmuneToFire = true;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		if (!super.processInteract(player, hand, stack)) {
			if (!this.worldObj.isRemote && player.isSneaking()) {
				this.dropItem(Decoy.decoy, 1);
				this.setDead();
			}
		}
		return true;
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityMob.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		if (ConfigHandler.decoyHome)
			this.tasks.addTask(6, new EntityAIGoHome(this, 1.2, 2));

	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(COLOR, new Color((float) Math.random(), (float) Math.random(), (float) Math.random()).getRGB());
		this.dataManager.register(HOME, BlockPos.ORIGIN);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(40.0D);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_PIG_HURT;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", getColor());
		compound.setLong("home", getHome().toLong());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setColor(compound.getInteger("color"));
		setHome(BlockPos.fromLong(compound.getLong("home")));
	}

	public int getColor() {
		return this.dataManager.get(COLOR);
	}

	public void setColor(int color) {
		this.dataManager.set(COLOR, color);
	}

	public BlockPos getHome() {
		return this.dataManager.get(HOME);
	}

	public void setHome(BlockPos pos) {
		this.dataManager.set(HOME, pos);
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (ConfigHandler.maxAge > 0 && this.ticksExisted / 20 >= ConfigHandler.maxAge)
			setDead();
		setHealth(getMaxHealth());
	}

}
