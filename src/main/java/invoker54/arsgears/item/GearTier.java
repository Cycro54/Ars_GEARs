package invoker54.arsgears.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum GearTier implements IItemTier {
    WOOD(0,119,1,0.0F,0,() ->
    {
        return Ingredient.of(ItemTags.PLANKS);
    }),

    STONE(1,263,3,1.0F,0,() ->

    {
        return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
    }),

    IRON(2,599,5,2.0F,0,() ->

    {
        return Ingredient.of(Items.IRON_INGOT);
    }),

    DIAMOND(3,1059,7,3.0F,0,() ->

    {
        return Ingredient.of(Items.DIAMOND);
    }),

    ARCANE(8,1561,9,4.0F,0,() ->

    {
        return Ingredient.of(Items.NETHERITE_INGOT);
    });

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;

    private GearTier(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        this.harvestLevel = harvestLevelIn;
        this.maxUses = maxUsesIn;
        this.efficiency = efficiencyIn;
        this.attackDamage = attackDamageIn;
        this.enchantability = enchantabilityIn;
        this.repairMaterial = new LazyValue<>(repairMaterialIn);
    }

    @Override
    public int getUses() {
        return maxUses;
    }

    @Override
    public float getSpeed() {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }

    @Override
    public int getLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}