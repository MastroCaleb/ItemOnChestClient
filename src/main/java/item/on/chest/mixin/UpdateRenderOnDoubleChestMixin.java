package item.on.chest.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import item.on.chest.MapUtils;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Mixin(ChestBlock.class)
public class UpdateRenderOnDoubleChestMixin {

    private static ChestBlockEntity chestBlockEntity;
    private static ChestBlockEntity chestBlockEntity2;

    private static NamedScreenHandlerFactory screenHandlerFactory = new NamedScreenHandlerFactory(){

        @Override
        @Nullable
        public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {

                MapUtils.lastOpened = chestBlockEntity.getPos();

                MapUtils.chests.put(chestBlockEntity.getPos(), MapUtils.getMostCommonItemStack(inventory));
                MapUtils.chests.put(chestBlockEntity2.getPos(), MapUtils.getMostCommonItemStack(inventory));

                chestBlockEntity.checkLootInteraction(playerInventory.player);
                chestBlockEntity2.checkLootInteraction(playerInventory.player);
                return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
            }
            return null;
        }

        @Override
        public Text getDisplayName() {
            if (chestBlockEntity.hasCustomName()) {
                return chestBlockEntity.getDisplayName();
            }
            if (chestBlockEntity2.hasCustomName()) {
                return chestBlockEntity2.getDisplayName();
            }
            return Text.translatable("container.chestDouble");
        }
    };

    @SuppressWarnings("unused")
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>>(){

        @Override
        public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            UpdateRenderOnDoubleChestMixin.chestBlockEntity = chestBlockEntity;
            UpdateRenderOnDoubleChestMixin.chestBlockEntity2 = chestBlockEntity2;
            return Optional.of(screenHandlerFactory);
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };

}
