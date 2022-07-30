package invoker54.arsgears.client.screen;

import invoker54.arsgears.init.ContainerInit;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class GearContainer extends Container {
    private static final Logger LOGGER = LogManager.getLogger();
    public Inventory tempInv;

    //region player inventory variables
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    //region GEAR inventory variables
    private static final int GEAR_INVENTORY_ROW_COUNT = 1;
    private static final int GEAR_INVENTORY_COLUMN_COUNT = 1;
    private static final int GEAR_INVENTORY_TOTAL_COUNT = GEAR_INVENTORY_COLUMN_COUNT * GEAR_INVENTORY_ROW_COUNT;
    //endregion


    private IntReferenceHolder foodData;
    private IntReferenceHolder countData;
    public int totalNeededFood;
    public int totalNeededCount;
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
        tempInv.addListener((container) -> calculateNeededFood(playerInventory.player));
        if (ContainerInit.gearContainerType == null)
            throw new IllegalStateException("Must initialise containerBasicContainerType before constructing a ContainerBasic!");

        //region First the player inventory
        PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);  // wrap the IInventory in a Forge IItemHandler.
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 176;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            // Not actually necessary - can use Slot(playerInventory) instead of SlotItemHandler(playerInventoryForge)
            addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 118;
        // Add the rest of the player's inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new SlotItemHandler(playerInventoryForge, slotNumber,  xpos, ypos));
            }
        }
        //endregion

        //region Next the Gear Inventory
        final int GEAR_INVENTORY_XPOS = 80;
        final int GEAR_INVENTORY_YPOS = 25;
        addSlot(new Slot(tempInv, 0,  GEAR_INVENTORY_XPOS, GEAR_INVENTORY_YPOS));
        //endregion

        //This is the totalNeededfood
        foodData = addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return totalNeededFood;
            }

            @Override
            public void set(int value) {
                totalNeededFood = value;
            }
        });

        //This is the totalNeededCount
        countData = addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return totalNeededCount;
            }

            @Override
            public void set(int value) {
                totalNeededCount = value;
            }
        });
    }

    // Vanilla calls this method every tick to make sure the player is still able to access the inventory, and if not closes the gui
    // Called on the SERVER side only

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory)
    // At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot
    // returns ItemStack.EMPTY if the source slot is empty, or if none of the the source slot item could be moved
    //   otherwise, returns a copy of the source stack
    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex < VANILLA_SLOT_COUNT) {
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

    public void calculateNeededFood(PlayerEntity player){
        LOGGER.debug("HEY IMMA RECALCULATE THE FOOD THING.");
        totalNeededFood = 0;
        totalNeededCount = 0;
        ItemStack foodStack = slots.get(slots.size() - 1).getItem();

        if (foodStack.isEmpty()) return;

        if(!foodStack.getItem().isEdible()) return;

        int foodValue = foodStack.getItem().getFoodProperties().getNutrition();

        //Grab the utility gear
        ItemStack utilityGear = player.getMainHandItem();
        if(!(utilityGear.getItem() instanceof UtilGearItem))utilityGear = player.getOffhandItem();
        //Grab its capability as well
        int tier = ((GearTier)((UtilGearItem) utilityGear.getItem()).getTier()).ordinal();

        int damage = utilityGear.getDamageValue();
        LOGGER.debug("Mt damage is: " + damage);

        while (totalNeededFood < damage && totalNeededCount < foodStack.getCount()){
            totalNeededCount += 1;
            totalNeededFood = (int) ((foodValue * (5/(tier+1)) * ((float)foodValue/6)) * totalNeededCount);
        }
        LOGGER.debug("Total needed count is: " + totalNeededCount);
        LOGGER.debug("Total needed food is: " + totalNeededFood);

        this.broadcastChanges();
    }
    
}
