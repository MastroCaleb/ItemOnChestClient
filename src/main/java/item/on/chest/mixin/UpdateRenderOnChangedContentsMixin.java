package item.on.chest.mixin;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import item.on.chest.MapUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

@Mixin(ScreenHandler.class)
public abstract class UpdateRenderOnChangedContentsMixin {

    @Shadow @Final private DefaultedList<ItemStack> trackedStacks;
    @Shadow @Final private List<ScreenHandlerListener> listeners;

    BlockPos pos;

    @SuppressWarnings("unused")
    private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
        ItemStack itemStack = this.trackedStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copySupplier.get();
            this.trackedStacks.set(slot, itemStack2);
            for (ScreenHandlerListener screenHandlerListener : this.listeners) {
                if(((ScreenHandler)(Object)this) instanceof GenericContainerScreenHandler container){
                    if(pos == null) {
                        pos = MapUtils.lastOpened;
                    }
                    MapUtils.chests.put(pos, MapUtils.getMostCommonItemStack(container.getInventory()));
                }
                screenHandlerListener.onSlotUpdate(((ScreenHandler)(Object)this), slot, itemStack2);
            }
        }
    }
    
}
