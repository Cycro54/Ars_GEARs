package invoker54.arsgears.client.gui.container;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.init.ContainerInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GearContainer extends Container {
    private static final Logger LOGGER = LogManager.getLogger();
    public final Inventory tempInv;

    //region player inventory variables
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT - 1;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    //region GEAR inventory variables
    private static final int GEAR_INVENTORY_ROW_COUNT = 2;
    private static final int GEAR_INVENTORY_COLUMN_COUNT = 3;
    private static final int GEAR_INVENTORY_TOTAL_COUNT = GEAR_INVENTORY_COLUMN_COUNT * GEAR_INVENTORY_ROW_COUNT;
    //endregion
    final PlayerEntity player;

    public List<Slot> playerSlots = new ArrayList<>();
    private final ItemStack gearStack;


//    private IntReferenceHolder foodData;
//    private IntReferenceHolder countData;
    public int repairValue;
    public int[] foodToEat = new int[6];
    //endregion

    public static GearContainer createContainer(int containerID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        // on the client side there is no parent TileEntity to communicate with, so we:
        // 1) use a dummy inventory
        // 2) use "do nothing" lambda functions for canPlayerAccessInventory and markDirty
        return new GearContainer(containerID, playerInventory);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    /**
     * Creates a container suitable for server side or client side
     * @param containerID ID of the container
     * @param playerInventory the inventory of the player
     */
    public GearContainer(int containerID, PlayerInventory playerInventory) {
        super(ContainerInit.gearContainerType, containerID);
        tempInv = new Inventory(GEAR_INVENTORY_TOTAL_COUNT);
        tempInv.addListener((container) -> calculateNeededFood());
        tempInv.startOpen(playerInventory.player);
        player = playerInventory.player;
        gearStack = player.getMainHandItem();
        if (ContainerInit.gearContainerType == null)
            throw new IllegalStateException("Must initialise containerBasicContainerType before constructing a ContainerBasic!");

        //region First the player inventory
//        PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);  // wrap the IInventory in a Forge IItemHandler.
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 118;
        // Add the player's inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                if (slotNumber == playerInventory.findSlotMatchingItem(player.getMainHandItem())) continue;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
//                playerSlots.add(addSlot(new SlotItemHandler(playerInventoryForge, slotNumber,  xpos, ypos)));
                playerSlots.add(addSlot(new Slot(playerInventory, slotNumber,  xpos, ypos)));
            }
        }
        //endregion

        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 176;
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            if (slotNumber == playerInventory.findSlotMatchingItem(player.getMainHandItem())) continue;
            // Not actually necessary - can use Slot(playerInventory) instead of SlotItemHandler(playerInventoryForge)
//            playerSlots.add(addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS)));
            playerSlots.add(addSlot(new Slot(playerInventory, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS)));
        }

        //region Next the Gear Inventory
        final int GEAR_INVENTORY_XPOS = 62;
        final int GEAR_INVENTORY_YPOS = 19;

        // Add the gear inventory finally
        for (int y = 0; y < GEAR_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < GEAR_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = y * GEAR_INVENTORY_COLUMN_COUNT + x;
                int xpos = GEAR_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = GEAR_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new foodSlot(tempInv, slotNumber,  xpos, ypos));
            }
        }
        //endregion

    }

    // Vanilla calls this method every tick to make sure the player is still able to access the inventory, and if not closes the gui
    // Called on the SERVER side only

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory)
    // At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot
    // returns ItemStack.EMPTY if the source slot is empty, or if none of the source slot item could be moved
    //   otherwise, returns a copy of the source stack
    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            //This will move the itemstack to
            if (slotIndex < VANILLA_SLOT_COUNT) {
//                if (!itemstack1.getItem().isEdible()) return ItemStack.EMPTY;
                if (!this.moveItemStackTo(itemstack1, VANILLA_SLOT_COUNT, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.clearContainer(player, player.level, tempInv);
    }

    public void calculateNeededFood() {
        repairValue = 0;
        Arrays.fill(foodToEat, 0);

        //Grab its capability as well
        int tier = GearCap.getCap(gearStack).getTier().ordinal();
        int damage = gearStack.getDamageValue();
        // LOGGER.debug("My damage is: " + damage);

        for (int a = 0; a < foodToEat.length; a++){
            ItemStack itemToInspect = tempInv.getItem(a);
            if (itemToInspect.isEmpty()) continue;
            if (!itemToInspect.isEdible()) continue;

            int foodValue = itemToInspect.getItem().getFoodProperties().getNutrition();
            int totalFoodValue = 0;
            while (repairValue + totalFoodValue < damage && foodToEat[a] < itemToInspect.getCount()) {
                foodToEat[a] += 1;
                totalFoodValue = (int) ((foodValue * (5 / (tier + 1)) * ((float) foodValue / 6)) * foodToEat[a]);
                LOGGER.debug("whats the food value? " + totalFoodValue);
            }
            repairValue += totalFoodValue;
            if (repairValue >= damage) break;
        }

        // LOGGER.debug("Total needed count is: " + Arrays.toString(foodToEat));
        // LOGGER.debug("Total needed food is: " + repairValue);
        this.broadcastChanges();
    }

    public static class foodSlot extends Slot{

        public foodSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem().isEdible();
        }
    }
}
