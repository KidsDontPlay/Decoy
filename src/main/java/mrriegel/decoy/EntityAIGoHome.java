package mrriegel.decoy;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIGoHome extends EntityAIBase {
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
