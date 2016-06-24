package mrriegel.decoy;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.refreshConfig(event.getSuggestedConfigurationFile());
		GameRegistry.register(Decoy.decoy);
		GameRegistry.addShapedRecipe(new ItemStack(Decoy.decoy), "pgp", "gbg", "pgp", 'p', Items.PORKCHOP, 'g', Items.GOLD_INGOT, 'b', Items.BRICK);
	}

	public void init(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityDecoy.class, "decoy", 0, Decoy.instance, 80, 3, false);
		MinecraftForge.EVENT_BUS.register(this);

	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	@SubscribeEvent
	public void creeperEx2(ExplosionEvent.Start e) {
		if (ConfigHandler.negateExplosion && e.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper) {
			double range = ConfigHandler.range;
			List<EntityDecoy> mobs = e.getWorld().getEntitiesWithinAABB(EntityDecoy.class, new AxisAlignedBB(e.getExplosion().getExplosivePlacedBy().posX - range, e.getExplosion().getExplosivePlacedBy().posY - range, e.getExplosion().getExplosivePlacedBy().posZ - range, e.getExplosion().getExplosivePlacedBy().posX + range, e.getExplosion().getExplosivePlacedBy().posY + range, e.getExplosion().getExplosivePlacedBy().posZ + range));
			if (!mobs.isEmpty())
				e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void join(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof IMob && e.getEntity() instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) e.getEntity();
			boolean contain1 = false;
			if (ConfigHandler.ignorePlayer) {
				for (EntityAITasks.EntityAITaskEntry en : el.targetTasks.taskEntries)
					if (en.action instanceof EntityAIFindDecoy) {
						contain1 = true;
						break;
					}
				if (!contain1)
					el.targetTasks.addTask(-2, new EntityAIFindDecoy(el, 1.2));
			}
			boolean contain2 = false;
			for (EntityAITasks.EntityAITaskEntry en : el.tasks.taskEntries)
				if (en.action instanceof EntityAIFindDecoy) {
					contain2 = true;
					break;
				}
			if (!contain2) {
				el.tasks.addTask(4, new EntityAIWatchClosest(el, EntityDecoy.class, 8.0F));
				el.tasks.addTask(ConfigHandler.ignorePlayer ? -2 : 4, new EntityAIFindDecoy(el, 1.2));
			}
		}
	}

}
