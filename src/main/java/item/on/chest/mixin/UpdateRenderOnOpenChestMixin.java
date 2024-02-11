package item.on.chest.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import item.on.chest.MapUtils;

@Mixin(ChestBlockEntity.class)
public class UpdateRenderOnOpenChestMixin {

	ChestBlockEntity chestBlockEntity = ((ChestBlockEntity)(Object)this);

	@Inject(at = @At("RETURN"), method = "createScreenHandler", cancellable = true)
	private void updateRenderOnOpenChestMixin(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> info) {
		GenericContainerScreenHandler screenHandler = (GenericContainerScreenHandler)info.getReturnValue();
		ItemStack toRender = MapUtils.getMostCommonItemStack(screenHandler.getInventory());
		BlockPos pos = chestBlockEntity.getPos();

		MapUtils.lastOpened = pos;
		MapUtils.chests.put(pos, toRender);
	}

	
}
