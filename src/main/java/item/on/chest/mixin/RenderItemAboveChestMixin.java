package item.on.chest.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext.Default;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import item.on.chest.MapUtils;

@SuppressWarnings("all")
@Mixin(ChestBlockEntityRenderer.class)
public class RenderItemAboveChestMixin<T extends BlockEntity> {

	@Inject(at = @At("TAIL"), method = "render")
	private void renderItemAboveChestMixin(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexCosnumers, int light, int overlay, CallbackInfo info) {
		renderItem(entity, tickDelta, matrices, vertexCosnumers, light, overlay);
	}

	public void renderItem(T blockEntity, float tickDelta,MatrixStack matrices, VertexConsumerProvider vertexCosnumers, int light, int overlay){
		World world = blockEntity.getWorld();
		if(world == null || blockEntity.isRemoved()) return;
			
		BlockState state = world.getBlockState(blockEntity.getPos());
		if(!(state.getBlock() instanceof ChestBlock)) return;

		ChestBlock block = (ChestBlock)state.getBlock();

		DoubleBlockProperties.Type type = block.getDoubleBlockType(state);
		if(block.getDoubleBlockType(state) == DoubleBlockProperties.Type.SECOND) return;

		ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;

		MinecraftClient minecraft = MinecraftClient.getInstance();

		ItemStack activeItem = MapUtils.chests.get(chestBlockEntity.getPos());

		if(activeItem == null) return;

		if(!chestBlockEntity.canPlayerUse(minecraft.player)) return;
			
		matrices.push();

		BakedModel model = minecraft.getItemRenderer().getModel(activeItem, world, null, 0);
		Vector3f translate = model.getTransformation().ground.translation;
			
		Direction facing = block.getFacing(state);

		double yPos = activeItem.getItem() instanceof BlockItem ? 0.9 : 1.1;
		float size = activeItem.getItem() instanceof BlockItem ? 1.5f : 1.25f;

		if(block.getDoubleBlockType(state) == DoubleBlockProperties.Type.FIRST){
			if(facing == Direction.EAST){
				matrices.translate(translate.x() + 1, translate.y() + yPos, translate.z() + 0.5);
			}
			else if(facing == Direction.WEST){
				matrices.translate(translate.x() + 0, translate.y() + yPos, translate.z() + 0.5);
			}
			else if(facing == Direction.NORTH){
				matrices.translate(translate.x() + 0.5, translate.y() + yPos, translate.z() + 0);
			}
			else{
				matrices.translate(translate.x() + 0.5, translate.y() + yPos, translate.z() + 1);
			}
		}
		else{
			matrices.translate(translate.x() + 0.5, translate.y() + yPos, translate.z() + 0.5);
		}

		matrices.scale(size, size, size);
		
		int age = getAge();
		float rotation = ((age + tickDelta) / 25.0f + 6.0f) * 100;
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		minecraft.getItemRenderer().renderItem(activeItem, ModelTransformationMode.GROUND, false, matrices, vertexCosnumers, light, overlay, model);
		matrices.pop();
		
	}

	private void translatePosition(DoubleBlockProperties.Type type, MatrixStack matrices, Vector3f translate, double xFirst, double zFirst, double xSecond, double zSecond){
		if(type == DoubleBlockProperties.Type.SINGLE){
			matrices.translate(translate.x() + 0.5, translate.y() + 1.1, translate.z() + 0.5);
		}
		else if(type == DoubleBlockProperties.Type.FIRST){
			matrices.translate(translate.x() + xFirst, translate.y() + 1.1, translate.z() + zFirst);
		}
		else if(type == DoubleBlockProperties.Type.SECOND){
			matrices.translate(translate.x() + xSecond, translate.y() + 1.1, translate.z() + zSecond);
		}
	}

	private int getAge(){
		return (int)(MinecraftClient.getInstance().world.getTime() % 314);
	}
}
