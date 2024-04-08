package item.on.chest;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MapUtils {
    public static Map<BlockPos, ItemStack> chests = new HashMap<BlockPos, ItemStack>();
    public static BlockPos lastOpened = null;
    public static ItemStack lastItemStack = null;

    public static ItemStack getMostCommonItemStack(Inventory inventory){
		ItemStack displayStack = new ItemStack(Items.AIR);
		int itemCount = 0;

		if(inventory.isEmpty()) return displayStack;

		for (int i = 0; i<inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
			if(!itemStack.isEmpty()){
				if(itemCount < inventory.count(itemStack.getItem())){
                    displayStack = itemStack;
					itemCount = inventory.count(itemStack.getItem());
				}
			}
		}

		return displayStack;
	}

	public static BlockPos getFirstChestFromSecondChest(Direction facing, BlockPos chestPos){
		BlockPos pos = null;
		if(facing == Direction.SOUTH){
			pos = new BlockPos(chestPos.getX(), chestPos.getY(), chestPos.getZ()+1);
		}
		else if(facing == Direction.NORTH){
			pos = new BlockPos(chestPos.getX(), chestPos.getY(), chestPos.getZ()-1);
		}
		else if(facing == Direction.WEST){
			pos = new BlockPos(chestPos.getX()-1, chestPos.getY(), chestPos.getZ());
		}
		else{
			pos = new BlockPos(chestPos.getX()+1, chestPos.getY(), chestPos.getZ());
		}
		return pos;
	}
}
