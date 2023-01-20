package invoker54.arsgears.commands;

import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.UpgradeRune;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import static invoker54.arsgears.init.ItemInit.*;

public class SetGearCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(
                Commands.literal("arsgear")
                        .requires((commandSource -> commandSource.hasPermission(2)))
                        .then(Commands.literal("settier")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                //For combat
                                                .then(Commands.literal("combat")
                                                        .then(Commands.literal("wood")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.WOOD,false)))
                                                        .then(Commands.literal("stone")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.STONE,false)))
                                                        .then(Commands.literal("iron")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.IRON,false)))
                                                        .then(Commands.literal("diamond")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.DIAMOND,false)))
                                                        .then(Commands.literal("arcane")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.ARCANE,false)))
                                                )

                                                //For Utility
                                                .then(Commands.literal("utility")
                                                        .then(Commands.literal("wood")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.WOOD,true)))
                                                        .then(Commands.literal("stone")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.STONE,true)))
                                                        .then(Commands.literal("iron")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.IRON,true)))
                                                        .then(Commands.literal("diamond")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.DIAMOND,true)))
                                                        .then(Commands.literal("arcane")
                                                                .executes(commandContext -> setTier(commandContext, GearTier.ARCANE,true)))
                                                )
                                        )

                        )
        );
    }

    private static int setTier(CommandContext<CommandSource> commandContext, GearTier tier, boolean utility) throws CommandSyntaxException {
        ServerPlayerEntity caller = EntityArgument.getPlayer(commandContext, "player");
        PlayerDataCap playerCap = PlayerDataCap.getCap(caller);

        if (utility){
            ItemStack gearStack = playerCap.getUtilityGear() == ItemStack.EMPTY ? new ItemStack(ItemInit.WOOD_PAXEL) : playerCap.getUtilityGear();
            GearCap gearCap = GearCap.getCap(gearStack);

            switch (tier){
                case WOOD:
                    //Starter Paxel
                    gearCap.getTag(0).putString("id", WOOD_PAXEL.getRegistryName().toString());
                    //Starter Fishing Rod
                    gearCap.getTag(1).putString("id", WOOD_FISHING_ROD.getRegistryName().toString());
                    //Starter Hoe
                    gearCap.getTag(2).putString("id", WOOD_HOE.getRegistryName().toString());
                    playerCap.upgradeUtilityGear(gearStack);
                    break;
                case STONE:
                    ((UpgradeRune)ItemInit.UTILITY_RUNE_1).transformGear(gearStack, gearCap, playerCap);
                    break;
                case IRON:
                    ((UpgradeRune)ItemInit.UTILITY_RUNE_2).transformGear(gearStack, gearCap, playerCap);
                    break;
                case DIAMOND:
                    ((UpgradeRune)ItemInit.UTILITY_RUNE_3).transformGear(gearStack, gearCap, playerCap);
                    break;
                case ARCANE:
                    ((UpgradeRune)ItemInit.UTILITY_RUNE_4).transformGear(gearStack, gearCap, playerCap);
                    break;
            }
            PortUtil.sendMessageNoSpam(caller, new TranslationTextComponent("ars_gears.chat.gear_utility_set").append(tier.name()));
        }
        else {
            ItemStack gearStack = playerCap.getCombatGear() == ItemStack.EMPTY ? new ItemStack(ItemInit.WOODEN_MOD_SWORD) : playerCap.getCombatGear();
            GearCap gearCap = GearCap.getCap(gearStack);

            gearStack.getOrCreateTag().putInt("mode", 1);
            switch (tier){
                case WOOD:
                    //Starter Sword
                    gearCap.getTag(0).putString("id", WOODEN_MOD_SWORD.getRegistryName().toString());
                    //Starter Bow
                    gearCap.getTag(1).putString("id", WOODEN_MOD_BOW.getRegistryName().toString());
                    //Starter Mirror
                    gearCap.getTag(2).putString("id", WOODEN_MOD_MIRROR.getRegistryName().toString());
                    playerCap.upgradeCombatGear(gearStack);
                    break;
                case STONE:
                    ((UpgradeRune)ItemInit.COMBAT_RUNE_1).transformGear(gearStack, gearCap, playerCap);
                    break;
                case IRON:
                    ((UpgradeRune)ItemInit.COMBAT_RUNE_2).transformGear(gearStack, gearCap, playerCap);
                    break;
                case DIAMOND:
                    ((UpgradeRune)ItemInit.COMBAT_RUNE_3).transformGear(gearStack, gearCap, playerCap);
                    break;
                case ARCANE:
                    ((UpgradeRune)ItemInit.COMBAT_RUNE_4).transformGear(gearStack, gearCap, playerCap);
                    break;
            }

            PortUtil.sendMessageNoSpam(caller, new TranslationTextComponent("ars_gears.chat.gear_combat_set").append(tier.name()));
        }
        return 1;
    }




}
