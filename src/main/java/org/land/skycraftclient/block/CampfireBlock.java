package org.land.skycraftclient.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.land.skycraftclient.register.SkyBlockRegister;

import java.util.Map;

public class CampfireBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public CampfireBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.isSneaking()) {
                // 웅크리고 우클릭 시 캠프 회수
                return removeCamp(world, pos, player);
            } else {
                // 일반 우클릭 시 불 켜고 끄기
                boolean isLit = state.get(LIT);
                world.setBlockState(pos, state.with(LIT, !isLit));
                if (!isLit) {
                    world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.CONSUME;
    }

    private ActionResult removeCamp(World world, BlockPos pos, PlayerEntity player) {
        // 캠프파이어 위치를 기준으로 텐트 구조물 찾기
        BlockPos tentPos = pos.up();
        for (CampStructure.TentType type : CampStructure.TentType.values()) {
            if (isTentStructure(world, tentPos, type)) {
                CampStructure.removeTent(world, tentPos, type);
                world.removeBlock(pos, false); // 캠프파이어 제거

                // 캠프 키트 아이템 반환
                ItemStack campKit = new ItemStack(getCampKitForTentType(type));
                if (!player.giveItemStack(campKit)) {
                    world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), campKit));
                }

                player.sendMessage(Text.literal("캠프가 성공적으로 회수되었습니다."), false);
                return ActionResult.SUCCESS;
            }
        }
        player.sendMessage(Text.literal("이 위치에서 캠프를 찾을 수 없습니다."), false);
        return ActionResult.FAIL;
    }

    private boolean isTentStructure(World world, BlockPos pos, CampStructure.TentType type) {
        for (Map.Entry<BlockPos, BlockState> entry : type.getStructureBlocks(pos).entrySet()) {
            if (!world.getBlockState(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private Item getCampKitForTentType(CampStructure.TentType type) {
        switch (type) {
            case SMALL:
                return SkyBlockRegister.SMALL_CAMP_KIT;
            case MEDIUM:
                return SkyBlockRegister.MEDIUM_CAMP_KIT;
            case LARGE:
                return SkyBlockRegister.LARGE_CAMP_KIT;
            default:
                return SkyBlockRegister.SMALL_CAMP_KIT;
        }
    }
}
