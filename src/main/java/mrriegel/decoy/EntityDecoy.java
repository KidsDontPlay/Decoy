package mrriegel.storagenetwork;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityDecoy extends EntityCreature {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer> createKey(EntityDecoy.class, DataSerializers.VARINT);
	private static final DataParameter<BlockPos> HOME = EntityDataManager.<BlockPos> createKey(EntityDecoy.class, DataSerializers.BLOCK_POS);

	public EntityDecoy(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 0.9F);
		this.enablePersistence();
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		if (!super.processInteract(player, hand, stack)) {
			if (!this.worldObj.isRemote && player.isSneaking()) {
				this.dropItem(Items.BAKED_POTATO, 1);
				this.setDead();
				return true;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityMob.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.tasks.addTask(6, new EntityAIGoHome(this, 1.2, 8));

	}

	public static class EntityAIGoHome extends EntityAIBase {
		private EntityDecoy entity;
		private double xPosition;
		private double yPosition;
		private double zPosition;
		private double speed;
		private int executionChance;

		public EntityAIGoHome(EntityDecoy creatureIn, double speedIn, int chance) {
			this.entity = creatureIn;
			this.speed = speedIn;
			this.executionChance = chance;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
				return false;
			}

			BlockPos vec3d = entity.getHome();

			if (vec3d == null) {
				return false;
			} else {
				this.xPosition = vec3d.getX();
				this.yPosition = vec3d.getY();
				this.zPosition = vec3d.getZ();
				return true;
			}
		}

		@Override
		public boolean continueExecuting() {
			return !this.entity.getNavigator().noPath();
		}

		@Override
		public void startExecuting() {
			this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(COLOR, new Color((float) Math.random(), (float) Math.random(), (float) Math.random()).getRGB());
		this.dataManager.register(HOME, BlockPos.fromLong(Long.MAX_VALUE));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000000.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0);
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_PIG_HURT;
	}

	@SubscribeEvent
	public void creeperEx2(ExplosionEvent.Detonate e) {
		if (e.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper && e.getExplosion().getExplosivePlacedBy().getDistanceToEntity(this) < 113) {
			e.getExplosion().clearAffectedBlockPositions();
		}
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
		setHealth(getMaxHealth());
		if (getHome().equals(BlockPos.fromLong(Long.MAX_VALUE)))
			setHome(getPosition());

	}

}

