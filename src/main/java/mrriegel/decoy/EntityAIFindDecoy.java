package mrriegel.decoy;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class EntityAIFindDecoy extends EntityAIBase {
	private EntityLiving entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private double speed;

	public EntityAIFindDecoy(EntityLiving creatureIn, double speedIn) {
		this.entity = creatureIn;
		this.speed = speedIn;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		int range = ConfigHandler.range;
		List<EntityDecoy> mobs = entity.worldObj.getEntitiesWithinAABB(EntityDecoy.class, new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
		if (mobs.isEmpty())
			return false;

		Vec3d vec3d = mobs.get(0).getPositionVector();

		if (vec3d == null) {
			return false;
		} else {
			this.xPosition = vec3d.xCoord;
			this.yPosition = vec3d.yCoord;
			this.zPosition = vec3d.zCoord;
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
		if (ConfigHandler.ignorePlayer)
			this.entity.setAttackTarget(null);
	}
}
