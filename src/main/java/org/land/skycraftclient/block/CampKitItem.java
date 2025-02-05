package org.land.skycraftclient.block;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.land.skycraftclient.register.SkyBlockRegister;

public class CampKitItem extends Item {
    private final CampStructure.TentType tentType;

    public CampKitItem(Item.Settings settings, CampStructure.TentType tentType) {
        super(settings);
        this.tentType = tentType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().up();
        PlayerEntity player = context.getPlayer();

        if (!world.isClient) {
            if (CampStructure.canPlaceTent(world, pos, tentType)) {
                CampStructure.createTent(world, pos, tentType, player);
                world.setBlockState(pos.down(), SkyBlockRegister.CAMPFIRE_BLOCK.getDefaultState());

                if (!player.isCreative()) {
                    context.getStack().decrement(1);
                }

                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.literal("이 위치에 텐트를 설치할 수 없습니다."), false);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.CONSUME;
    }

    public static ActionResult removeCamp(World world, BlockPos pos, PlayerEntity player) {
        for (CampStructure.TentType type : CampStructure.TentType.values()) {
            if (CampStructure.canPlaceTent(world, pos, type)) {
                continue; // 이미 비어있는 경우 다음 유형 확인
            }
            CampStructure.removeTent(world, pos, type);
            world.removeBlock(pos.down(), false); // 캠프파이어 제거

            // 캠프 키트 아이템 반환
            ItemStack campKit = new ItemStack(SkyBlockRegister.SMALL_CAMP_KIT);
            if (!player.giveItemStack(campKit)) {
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), campKit));
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
