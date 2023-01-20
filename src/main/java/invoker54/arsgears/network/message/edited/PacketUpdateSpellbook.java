package invoker54.arsgears.network.message.edited;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.network.NetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateSpellbook {
    String spellRecipe;
    int cast_slot;
    String spellName;

    public PacketUpdateSpellbook(String spellRecipe, int cast_slot, String spellName) {
        this.spellRecipe = spellRecipe;
        this.cast_slot = cast_slot;
        this.spellName = spellName;
    }

    public PacketUpdateSpellbook(PacketBuffer buf) {
        this.spellRecipe = buf.readUtf(32767);
        this.cast_slot = buf.readInt();
        this.spellName = buf.readUtf(32767);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.spellRecipe);
        buf.writeInt(this.cast_slot);
        buf.writeUtf(this.spellName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
            if (((NetworkEvent.Context)ctx.get()).getSender() != null) {
                ItemStack stack = ArsUtil.getHeldGearCap(ctx.get().getSender(), false, false);
                if (stack != null && this.spellRecipe != null) {
                    CompoundNBT tag = stack.getOrCreateTag();
                    SpellBook.setRecipe(tag, this.spellRecipe, this.cast_slot);
                    SpellBook.setSpellName(tag, this.spellName, this.cast_slot);
                    SpellBook.setMode(tag, this.cast_slot);
                    stack.setTag(tag);
                    NetworkHandler.sendToPlayer(ctx.get().getSender(), new PacketUpdateBookGUI(tag));
                }
            }

        });
        ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }
}
