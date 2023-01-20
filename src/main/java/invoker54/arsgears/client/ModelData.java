package invoker54.arsgears.client;

import invoker54.arsgears.ArsGears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ModelData {

    public static float getData(ItemStack itemStack, String data){
        CompoundNBT dataTag = check(itemStack);

        return dataTag.getFloat(data);
    }

    public static float setData(ItemStack itemStack, String data, float newValue, boolean add){
        CompoundNBT dataTag = check(itemStack);

        newValue = add ? dataTag.getFloat(data) + newValue : newValue;

        dataTag.putFloat(data, newValue);

        return dataTag.getFloat(data);
    }

    public static CompoundNBT check(ItemStack itemStack){
        CompoundNBT subTag = itemStack.getOrCreateTag();
        if (!subTag.contains(ArsGears.MOD_ID + "MDT")){
            subTag.put(ArsGears.MOD_ID + "MDT", new CompoundNBT());
        }

        return (CompoundNBT) subTag.get(ArsGears.MOD_ID + "MDT");
    }

//    public static float getData(CompoundNBT subTag, String data){
//        check(uuid);
//
//        return modelData.get(uuid).getFloat(data);
//    }
//
//    public static void setData(UUID uuid, String data, float newValue){
//        check(uuid);
//
//        modelData.get(uuid).putFloat(data, newValue);
//    }
//
//    private static void check(UUID uuid){
//        if (!modelData.containsKey(uuid)){
//            modelData.put(uuid, new CompoundNBT());
//        }
//    }
}
