package invoker54.arsgears.network.message.edited;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateBookGUI {
    public CompoundNBT tag;

    public PacketUpdateBookGUI(PacketBuffer buf) {
        this.tag = buf.readNbt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(this.tag);
    }

    public PacketUpdateBookGUI(CompoundNBT tag) {
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
            if (ClientUtil.mC.screen instanceof ModGuiSpellBook) {
                ((ModGuiSpellBook)ClientUtil.mC.screen).spell_book_tag = this.tag;
            }

        });
        ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }
}
