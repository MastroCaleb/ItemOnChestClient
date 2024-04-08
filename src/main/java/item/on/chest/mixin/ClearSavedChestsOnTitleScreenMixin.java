package item.on.chest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import item.on.chest.MapUtils;
import net.minecraft.client.gui.screen.TitleScreen;

@Mixin(TitleScreen.class)
public class ClearSavedChestsOnTitleScreenMixin {
    
    /*
     * Clears saved chest positions and itemstacks.
     * 
     * Warning: Not doing this presents the following issues:
     * - Theoretically could store too many values.
     * - Chests in the same position but in different worlds display the same last saved item.
     */
    @Inject(at = @At("HEAD"), method = "init")
	public void clearSavedChestsOnTitleScreen(CallbackInfo info) {
        System.out.println("[Item On Chest/Client] Memory cleared!");
        MapUtils.chests.clear();
        MapUtils.lastItemStack = null;
        MapUtils.lastOpened = null;
    }

}
