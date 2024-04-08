package item.on.chest.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters.CharsetConverter;
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
	BlockPos chestPos = null;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void init(T handler, PlayerInventory inventory, Text title, CallbackInfo info) {
		this.inventory = inventory;
    }

	@Inject(at = @At("HEAD"), method = "render")
	private void updateRenderOnChestScreenMixin(DrawContext matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if(handler instanceof GenericContainerScreenHandler){
			ItemStack toRender = MapUtils.getMostCommonItemStack(((GenericContainerScreenHandler)handler).getInventory());

			HitResult hit = inventory.player.raycast(5, delta, false);

			if(hit instanceof BlockHitResult hitResult){
				chestPos = hitResult.getBlockPos();
				
				BlockState state = MinecraftClient.getInstance().world.getBlockState(chestPos);
				if(state.getBlock() instanceof ChestBlock block){
					ChestType type = state.get(Properties.CHEST_TYPE);

					Direction facing = block.getFacing(state);

					if(type == ChestType.LEFT){
						BlockPos pos = MapUtils.getFirstChestFromSecondChest(facing, chestPos);
						MapUtils.lastOpened = pos;
						MapUtils.chests.put(pos, toRender);
					}
					else{
						MapUtils.lastOpened = chestPos;
						MapUtils.chests.put(chestPos, toRender);
					}
				}
			}
		}
	}
}
