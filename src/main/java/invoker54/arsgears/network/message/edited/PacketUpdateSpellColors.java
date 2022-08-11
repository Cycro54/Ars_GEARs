package invoker54.arsgears.network.message.edited;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.event.item.combatgear.CombatGearItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateSpellColors {

   String spellRecipe;
   int castSlot;
   String spellName;
   int r;
   int g;
   int b;

    public PacketUpdateSpellColors(){}

    public PacketUpdateSpellColors(int castSlot, int r, int g, int b){
        this.castSlot = castSlot;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    //Decoder
    public PacketUpdateSpellColors(PacketBuffer buf){
        castSlot = buf.readInt();
        r = buf.readInt();
        g = buf.readInt();
        b = buf.readInt();
    }

    public PacketUpdateSpellColors(int slot, double red, double green, double blue) {
        this(slot, (int)red, (int) green, (int) blue);
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeInt(castSlot);
        buf.writeInt(r);
        buf.writeInt(g);
        buf.writeInt(b);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                ItemStack stack = ArsUtil.getHeldItem(ctx.get().getSender(), CombatGearItem.class);
                if(!stack.isEmpty()){
                    CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
                    SpellBook.setSpellColor(tag, new ParticleColor.IntWrapper(r, g, b), castSlot);
                    stack.setTag(tag);
                    SpellBook.setMode(tag, castSlot);
                    //Old code
//                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()), new PacketUpdateBookGUI(tag));
//                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()),
//                            new PacketOpenSpellBook(stack.getTag(), ((SpellBook) stack.getItem()).tier.ordinal(), SpellBook.getUnlockedSpellString(tag)));
                    //Imma just run this stuff on the client instead.

                }
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
