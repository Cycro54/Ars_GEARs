package invoker54.arsgears.client.keybind;

import invoker54.arsgears.ArsGears;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class CustomKeybind {
    public KeyBinding keyBind;
    public IClicked iClicked;

    public CustomKeybind(String name, int key, IClicked iClicked){
        keyBind = new KeyBinding("key." + ArsGears.MOD_ID + "." + name, key,"key.category." + ArsGears.MOD_ID);
        //keyBind = new KeyBinding(name, key,"XP Shop");
        ClientRegistry.registerKeyBinding(keyBind);
        this.iClicked = iClicked;
    }

    public void pressed(){
        iClicked.onClick();
    }

    public interface IClicked {
        void onClick();
    }
}
