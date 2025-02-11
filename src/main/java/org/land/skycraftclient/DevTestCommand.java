package org.land.skycraftclient;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DevTestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("devtest")
            .requires(source -> source.hasPermissionLevel(2)) // OP 권한 필요
            .then(CommandManager.literal("heal")
                .executes(DevTestCommand::executeHeal))
            .then(CommandManager.literal("defense")
                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                    .executes(DevTestCommand::executeSetDefense)))
            .then(CommandManager.literal("invulnerable")
                .executes(DevTestCommand::executeToggleInvulnerable)));
    }

    private static int executeHeal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        player.setHealth(player.getMaxHealth());
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevel(5.0f);
        context.getSource().sendFeedback(() -> Text.literal("체력과 허기가 완전히 회복되었습니다."), false);
        return 1;
    }

    private static int executeSetDefense(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        int defenseValue = IntegerArgumentType.getInteger(context, "value");
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(defenseValue);
        context.getSource().sendFeedback(() -> Text.literal("방어력이 " + defenseValue + "로 설정되었습니다."), false);
        return 1;
    }

    private static int executeToggleInvulnerable(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean newInvulnerableState = !player.isInvulnerable();
        player.setInvulnerable(newInvulnerableState);
        String stateText = newInvulnerableState ? "활성화" : "비활성화";
        context.getSource().sendFeedback(() -> Text.literal("무적 모드가 " + stateText + "되었습니다."), false);
        return 1;
    }
}
