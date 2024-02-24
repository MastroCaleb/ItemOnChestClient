package item.on.chest.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import item.on.chest.MapUtils;

@SuppressWarnings("all")
@Mixin(HandledScreen.class)
public class UpdateRenderOnChestScreenMixin<T extends ScreenHandler>{

	HandledScreen screen = ((HandledScreen)(Object)this);
	@Shadow @Final T handler;
	PlayerInventory inventory;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void init(T handler, PlayerInventory inventory, Text title, CallbackInfo info) {
		this.inventory = inventory;
    }

	@Inject(at = @At("HEAD"), method = "render")
	private void updateRenderOnChestScreenMixin(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if(handler instanceof GenericContainerScreenHandler){
			ItemStack toRender = MapUtils.getMostCommonItemStack(((GenericContainerScreenHandler)handler).getInventory());

			HitResult hit = inventory.player.raycast(5, delta, false);

			if(hit instanceof BlockHitResult hitResult){
				BlockPos chestPos = hitResult.getBlockPos();
				MapUtils.lastOpened = chestPos;
				MapUtils.chests.put(chestPos, toRender);
			}
		}
	}
}
