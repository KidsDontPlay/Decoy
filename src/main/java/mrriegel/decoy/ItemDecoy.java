package mrriegel.decoy;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDecoy extends Item {

	public ItemDecoy() {
		super();
		this.setRegistryName("decoy");
		this.setUnlocalizedName(getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return EnumActionResult.PASS;
		if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
			return EnumActionResult.FAIL;
		IBlockState iblockstate = worldIn.getBlockState(pos);
		pos = pos.offset(facing);
		double d0 = 0.0D;

		if (facing == EnumFacing.UP && iblockstate.getBlock() instanceof BlockFence) {
			d0 = 0.5D;
		}

		EntityDecoy e = new EntityDecoy(worldIn);
		e.setPosition(pos.getX() + .5, pos.getY() + d0, pos.getZ() + .5);
		e.setHome(e.getPosition());
		worldIn.spawnEntityInWorld(e);
		if (!playerIn.capabilities.isCreativeMode) {
			stack.stackSize--;
			if (stack.stackSize <= 0)
				stack = null;
			playerIn.openContainer.detectAndSendChanges();
		}

		return EnumActionResult.SUCCESS;
	}

}
