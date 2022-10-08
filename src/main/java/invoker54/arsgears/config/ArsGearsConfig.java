package invoker54.arsgears.config;

import invoker54.arsgears.ArsGears;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ArsGearsConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static Boolean useCombatItems;
    public static Boolean useUtilityItems;
    public static Boolean useSpellbook;
    public static Boolean disableCooldown;
    public static Double coolDownMultiplier;
    public static Double coolDownValueChange;
    public static Integer upgradeValue;
//    public static Boolean startWithCOMBAT;
//    public static Boolean startWithUTILITY;

    private static boolean isDirty = false;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void bakeCommonConfig(){
        //System.out.println("SYNCING CONFIG SHTUFF");
        useCombatItems = COMMON.useCombatItems.get();
        useUtilityItems = COMMON.useUtilityItems.get();
        useSpellbook = COMMON.useSpellbook.get();
        disableCooldown = COMMON.disableCooldown.get();
        coolDownMultiplier = COMMON.coolDownMultiplier.get();
        coolDownValueChange = COMMON.coolDownValueChange.get();
        upgradeValue = COMMON.upgradeValue.get();
//            startWithCOMBAT = COMMON.startWithCOMBAT.get();
//            startWithUTILITY = COMMON.startWithUTILITY.get();
    }

    @SubscribeEvent
    public static void onConfigChanged(final ModConfig.ModConfigEvent eventConfig){
        //System.out.println("What's the config type? " + eventConfig.getConfig().getType());
        if(eventConfig.getConfig().getSpec() == ArsGearsConfig.COMMON_SPEC){
            LOGGER.debug("CONFIG CHANGED, SENDING DATA TO PLAYERS NOW.");
            bakeCommonConfig();
            markDirty(true);
        }
    }

    public static void markDirty(boolean dirty){
        isDirty = dirty;
    }
    public static boolean isDirty(){
        return isDirty;
    }

    public static class CommonConfig {

        //This is how to make a config value
        //public static final ForgeConfigSpec.ConfigValue<Integer> exampleInt;
//        public final ForgeConfigSpec.ConfigValue<Integer> timeLeft;
        public final ForgeConfigSpec.ConfigValue<Boolean> useCombatItems;
        public final ForgeConfigSpec.ConfigValue<Boolean> useUtilityItems;
        public final ForgeConfigSpec.ConfigValue<Boolean> useSpellbook;
        public final ForgeConfigSpec.ConfigValue<Boolean> disableCooldown;
        public final ForgeConfigSpec.ConfigValue<Double> coolDownMultiplier;
        public final ForgeConfigSpec.ConfigValue<Double> coolDownValueChange;
        public final ForgeConfigSpec.ConfigValue<Integer> upgradeValue;
//        public final ForgeConfigSpec.ConfigValue<Boolean> startWithCOMBAT;
//        public final ForgeConfigSpec.ConfigValue<Boolean> startWithUTILITY;


        public CommonConfig(ForgeConfigSpec.Builder builder) {
            //This is what goes on top inside of the config
            builder.push("Ars Gears Config");
            //This is how you place a variable in the config file
            //exampleInt = BUILDER.comment("This is an integer. Default value is 3.").define("Example Integer", 54);
            useCombatItems = builder.comment("If you can use Combat items similar to the COMBAT Gear items").define("Allow Combat Items", false);
            useUtilityItems = builder.comment("If you can use Utility items similar to the UTILITY Gear items").define("Allow Utility Items", false);
            useSpellbook = builder.comment("If you can use the Spellbook for casting").define("Allow Spellbook", false);
            disableCooldown = builder.comment("Disables the cooldown feature").define("Disable Cooldown", false);
            coolDownMultiplier = builder.comment("Affects how long a cooldown last, 1 is default").defineInRange("Cooldown Multiplier", 1F, 0.01F, Integer.MAX_VALUE);
            coolDownValueChange = builder.comment("Increase/Decrease how long a cooldown should last in seconds (Not affected by Cooldown Multiplier)").defineInRange("Cooldown Shift", 0F, -Integer.MAX_VALUE, Integer.MAX_VALUE);
            upgradeValue = builder.comment("Affects how much each upgrade branch in total costs, 612 is default (which is 21 lvls)").defineInRange("Upgrade Cost", 612, 1, Integer.MAX_VALUE);
//            startWithCOMBAT = builder.comment("If players should start with a COMBAT Gear on first join.").defineInRange("Upgrade Cost", 612, 1, Integer.MAX_VALUE);
//            startWithUTILITY = builder.comment("If players should start with a UTILITY Gear on first join.").defineInRange("Upgrade Cost", 612, 1, Integer.MAX_VALUE);
            builder.pop();
        }

    }

}
