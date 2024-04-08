package item.on.chest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import item.on.chest.MapUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

@Mixin(Block.class)
public class ClearSavedChestOnDestructionMixin {
    
    /*
     * Clears saved chest at the broken chest's position.
     * 
     * Warning: Not doing this presents the following issues:
     * - Theoretically could store too many values.
     * - Chests in the same position but in different worlds display the same last saved item.
     */
    @Inject(at = @At("HEAD"), method = "onBroken")
	public void clearSavedChestsAfterDestruction(WorldAccess world, BlockPos pos, BlockState state, CallbackInfo info) {
        if(world.isClient()){
            System.out.println("[Item On Chest/Client] Chest destroyed, position cleared from memory");
            MapUtils.chests.remove(pos);
        }
    }

}
