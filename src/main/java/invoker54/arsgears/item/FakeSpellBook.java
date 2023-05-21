package invoker54.arsgears.item;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.capability.player.PlayerDataCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class FakeSpellBook extends SpellBook {

    public FakeSpellBook(Tier tier){
        super(tier);
    }

    public FakeSpellBook(Properties properties, Tier tier) {
        super(properties, tier);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity playerIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide()) return;

        PlayerEntity player = (PlayerEntity) playerIn;
        PlayerDataCap playerCap = PlayerDataCap.getCap(player);
        if (playerCap == null) return;

        //If the player goes off the spellBook, turn it back to the combat gear
        boolean flag = !isSelected;

        if (flag && stack.getItem() instanceof  FakeSpellBook){
            player.inventory.removeItem(stack);
            player.inventory.add(playerCap.getCombatGear());
        }
    }

    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        player.sendMessage(new TranslationTextComponent("ars_gears.chat.fake_book.scribe") ,Util.NIL_UUID);
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //You can't cast with this item.
        playerIn.sendMessage(new TranslationTextComponent("ars_gears.chat.fake_book.cast") ,Util.NIL_UUID);
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }
}
