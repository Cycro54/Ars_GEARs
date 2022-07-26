package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.FamiliarCap;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliarCap;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GuiFamiliarScreen;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.spell.validation.BaseSpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.*;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static invoker54.arsgears.item.combatgear.CombatGearItem.bowInt;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class ModGuiSpellBook extends BaseBook {
    private static final Logger LOGGER = LogManager.getLogger();

    public int numLinks;
    public int maxAugmentStack;
    public SpellBook spellBook;
    public ArsNouveauAPI api;
    public boolean sneakHeld = false;
    public Spell currSpell;

    public int selected_cast_slot;
    public TextFieldWidget spell_name;
    public NoShadowTextField searchBar;
    public CompoundNBT spell_book_tag;
    public ModGuiSpellSlot selected_slot;
    public int max_spell_tier; // Used to load spells that are appropriate tier
    public List<ModCraftingButton> craftingCells = new ArrayList<>();
    public List<AbstractSpellPart> unlockedSpells;
    public List<AbstractSpellPart> castMethods;
    public List<AbstractSpellPart> augments;
    public List<AbstractSpellPart> displayedGlyphs;
    public List<AbstractSpellPart> allEffects;
//    public List<ModGlyphButton> castMethodButtons;
//    public List<ModGlyphButton> augmentButtons;
//    public List<ModGlyphButton> effectButtons;
    public List<ModGlyphButton> glyphButtons = new ArrayList<>();
    public int page = 0;
    public List<SpellValidationError> validationErrors;
    ChangePageButton nextButton;
    ChangePageButton previousButton;
    ISpellValidator spellValidator;
    public String previousString = "";
    int formTextRow = 0;
    int augmentTextRow = 0;
    int effectTextRow = 0;
    ItemStack gearStack;


    public ModGuiSpellBook(ItemStack gearStack, int selected_cast_slot) {
        super();
        this.api = ArsNouveauAPI.getInstance();

        //Tier of my combat gear (minus 1 since I also don't want the player casting on STONE tier)
        this.max_spell_tier = (CombatGearCap.getCap(gearStack).getTier().ordinal() - 1);
        if (ClientUtil.mC.player.isCreative()) this.max_spell_tier = 3;
        //The spell book tag (where all the spell book data is stored)
        this.spell_book_tag = gearStack.getOrCreateTag();
        this.gearStack = gearStack;
        //Unlocked Spells
        String stringSpells = SpellBook.getUnlockedSpellString(gearStack.getOrCreateTag());
        this.unlockedSpells = SpellRecipeUtil.getSpellsFromString(stringSpells);
        if (ClientUtil.mC.player.isCreative()) this.unlockedSpells = new ArrayList<>(ArsNouveauAPI.getInstance().getSpell_map().values());
        this.selected_cast_slot = selected_cast_slot;
        this.castMethods = new ArrayList<>();
        this.augments = new ArrayList<>();
        allEffects = new ArrayList<>();


        this.displayedGlyphs = this.unlockedSpells;
//        this.castMethodButtons = new ArrayList<>();
//        this.augmentButtons = new ArrayList<>();
//        this.effectButtons = new ArrayList<>();
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                api.getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(max_spell_tier)
        );
    }

    public static void open(ItemStack gearStack){
        int cast_slot = SpellBook.getMode(gearStack.getOrCreateTag());

        Minecraft.getInstance().setScreen(new ModGuiSpellBook(gearStack, cast_slot));
    }

    @Override
    public void init() {
        super.init();

        LOGGER.debug("What is craftingCells size? " + this.craftingCells.size());
        LOGGER.debug("What is the Max Spell Tier? " + this.max_spell_tier);

        //This is the currently selected spell
        int selected_slot_ind = SpellBook.getMode(spell_book_tag);
        if(selected_slot_ind == 0) selected_slot_ind = 1;

        //Max amount of times a augment may stack onto itself (This can only be opened at tier 2)
        //Also will tell us how many crafting slots are allowed.
        maxAugmentStack = max_spell_tier;
        numLinks = 4 + (2 * max_spell_tier);

        //Craft slots for the spell
        if (craftingCells.isEmpty()) {
            for (int i = 0; i < numLinks; i++) {
//                    String icon = null;
//                    String spell_id = "";
                //ModGlyphButton glyphButton = new ModGlyphButton(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, )
                int x = bookLeft + 19 + 24 * i + (i >= 5 ? -(24 * 5) : 0);
                int y = (bookTop + FULL_HEIGHT - 70) + (i >= 5 ? 24 : 0);
                ModCraftingButton cell = new ModCraftingButton(this, x, y, i, this::onCraftingSlotClick);
                    addButton(cell);
                    craftingCells.add(cell);
                }
            updateCraftingSlots(selected_slot_ind);
        }
        else {
            for (int a = 0; a < craftingCells.size(); a++){
                addButton(craftingCells.get(a));
                craftingCells.get(a).x = (bookLeft + 19 + 24 * a + (a >= 5 ? -(24 * 5) : 0));
                craftingCells.get(a).y = (bookTop + FULL_HEIGHT - 70) + (a >= 5 ? 24 : 0);
            }
        }
//        addCastMethodParts();
//        addAugmentParts();
//        addEffectParts(0);
        layoutAllGlyphs(0);
        addButton(new ModCreateSpellButton(this, bookRight - 71, bookBottom - 13, this::onCreateClick));
        addButton(new ModGuiImageButton(bookRight - 126, bookBottom - 13, 0,0,41, 12, 41, 12, "textures/gui/clear_icon.png", this::clear));

        spell_name = new NoShadowTextField(minecraft.font, bookLeft + 32, bookTop + FULL_HEIGHT - 11,
                88, 12, null, new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name"));
        spell_name.setBordered(false);
        spell_name.setTextColor(12694931);

        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop +2,
                54, 12, null, new TranslationTextComponent("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };


        int mode = SpellBook.getMode(spell_book_tag);
        mode = mode == 0 ? 1 : mode;
        spell_name.setValue(SpellBook.getSpellName(spell_book_tag, mode));
        if(spell_name.getValue().isEmpty())
            spell_name.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name").getString());

        if(searchBar.getValue().isEmpty())
            searchBar.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);
//
        addButton(spell_name);
        addButton(searchBar);
        // Add spell slots (these are the tabs of the book on the right)
        for(int i = 1; i <= max_spell_tier + 1; i++){
            ModGuiSpellSlot slot = new ModGuiSpellSlot(this,bookLeft + 281, bookTop +1 + 15 * i, i);
            if(i == selected_slot_ind) {
                selected_slot = slot;
                selected_cast_slot = i;
                LOGGER.debug("THE ACTUAL SLOT IS " + i);
                slot.isSelected = true;
            }
            addButton(slot);
        }

        addButton(new ModGuiImageButton(bookLeft - 15, bookTop + 22, 0, 0, 23, 20, 23,20, "textures/gui/worn_book_bookmark.png",this::onDocumentationClick)
                .withTooltip(this, new TranslationTextComponent("ars_nouveau.gui.notebook")));
        addButton(new ModGuiImageButton(bookLeft - 15, bookTop + 46, 0, 0, 23, 20, 23,20, "textures/gui/color_wheel_bookmark.png",this::onColorClick)
                .withTooltip(this, new TranslationTextComponent("ars_nouveau.gui.color")));
        addButton(new ModGuiImageButton(bookLeft - 15, bookTop + 70, 0, 0, 23, 20, 23,20, "textures/gui/summon_circle_bookmark.png",this::onFamiliarClick)
                .withTooltip(this, new TranslationTextComponent("ars_nouveau.gui.familiar")));
        this.nextButton = addButton(new ChangePageButton(bookRight -20, bookBottom -10, true, this::onPageIncrease, true));
        this.previousButton = addButton(new ChangePageButton(bookLeft - 5 , bookBottom -10, false, this::onPageDec, true));

        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;

        validate();
    }

    public void resetPageState(){
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(0);
        validate();
    }

    public void onSearchChanged(String str){
        if(str.equals(previousString))
            return;
        previousString = str;

        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedGlyphs = new ArrayList<>();
            // Filter Effects
            for (AbstractSpellPart spellPart : unlockedSpells) {
                if (spellPart.getLocaleName().toLowerCase().contains(str.toLowerCase())) {
                    displayedGlyphs.add(spellPart);
                }
            }
            // Set visibility of Cast Methods and Augments
            for(Widget w : buttons) {
                if(w instanceof ModGlyphButton) {
                    if (((ModGlyphButton) w).spell_id != null) {
                        AbstractSpellPart part = api.getSpell_map().get(((ModGlyphButton) w).spell_id);
                        if (part != null) {
                            w.visible = part.getLocaleName().toLowerCase().contains(str.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.search").getString());
            displayedGlyphs = unlockedSpells;
            for(Widget w : buttons){
                if(w instanceof ModGlyphButton ) {
                    w.visible = true;
                }
            }
        }
        resetPageState();
    }

    public void updateNextPageButtons(){
        if(displayedGlyphs.size() < 58){
            nextButton.visible = false;
            nextButton.active = false;
        }else{
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    private void layoutAllGlyphs(int page){
        clearButtons(glyphButtons);
        formTextRow = 0;
        augmentTextRow = 0;
        effectTextRow = 0;
        final int PER_ROW = 6;
        final int MAX_ROWS = 5;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 20;
        //boolean foundForms = false;
        boolean foundAugments = false;
        boolean foundEffects = false;
        List<AbstractSpellPart> sorted = new ArrayList<>();
        //sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractCastMethod).collect(Collectors.toList()));
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractAugment).collect(Collectors.toList()));
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractEffect).collect(Collectors.toList()));
        int perPage = 58;
        sorted = sorted.subList(perPage * page, Math.min(sorted.size(), perPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int row_offset = page == 0 ? 2 : 0;

        for(int i = 0; i < sorted.size(); i++){
            AbstractSpellPart part = sorted.get(i);
            if (CombatGearItem.isBanned(part, true)){
                continue;
            }

//            if(!foundForms && part instanceof AbstractCastMethod) {
//                foundForms = true;
//                adjustedRowsPlaced += 1;
//                totalRowsPlaced += 1;
//                formTextRow = page != 0 ? 0 : totalRowsPlaced;
//                adjustedXPlaced = 0;
//            }

            if(!foundAugments && part instanceof AbstractAugment){
                foundAugments = true;
                adjustedRowsPlaced += 1;
                totalRowsPlaced += 1;
                augmentTextRow = page != 0 ? 0 : totalRowsPlaced;
                adjustedXPlaced = 0;
//                foundAugments = true;
//                adjustedRowsPlaced += row_offset;
//                totalRowsPlaced += row_offset;
//                augmentTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
//                adjustedXPlaced = 0;
            } else if(!foundEffects && part instanceof AbstractEffect){
                foundEffects = true;
                if (foundAugments) {
                    nextPage = true;
                    totalRowsPlaced = 0;
                    adjustedXPlaced = 0;
                    adjustedRowsPlaced = 1;
                    effectTextRow = MAX_ROWS + 1;
                }
                else {
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                effectTextRow = page != 0 ? 0 :totalRowsPlaced - 1;
                adjustedXPlaced = 0;
                }
            }else{
                if(adjustedXPlaced >= PER_ROW){
                    adjustedRowsPlaced++;
                    totalRowsPlaced++;
                    adjustedXPlaced = 0;
                }
            }
            if(adjustedRowsPlaced > MAX_ROWS){
                if(nextPage){
                    break;
                }
                nextPage = true;
                adjustedXPlaced = 0;
                adjustedRowsPlaced = 0;
            }
            int xOffset = 20 * ((adjustedXPlaced ) % PER_ROW) + (nextPage ? 134 :0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;

            ModGlyphButton cell = new ModGlyphButton(this, xStart + xOffset, yPlace, false, part.getIcon(), part.tag, (part instanceof AbstractAugment));
            addButton(cell);
            glyphButtons.add(cell);
            adjustedXPlaced++;
        }
    }
//
//    private void addCastMethodParts() {
//        layoutParts(castMethods, castMethodButtons, bookLeft + 20, bookTop + 34, 2);
//    }
//
//    private void addAugmentParts() {
//        layoutParts(augments, augmentButtons, bookLeft + 20, bookTop + 88, 3);
//    }
//
//    private void addEffectParts(int page) {
//        List<AbstractSpellPart> displayedEffects = this.displayedGlyphs.subList(36 * page, Math.min(this.displayedGlyphs.size(), 36 * (page + 1)));
//        layoutParts(displayedEffects, effectButtons, bookLeft + 154, bookTop + 34, 6);
//    }

    public void clearButtons( List<ModGlyphButton> glyphButtons){
        for (ModGlyphButton b : glyphButtons) {
            buttons.remove(b);
            children.remove(b);
        }
        glyphButtons.clear();
    }

//    private void layoutParts(List<AbstractSpellPart> parts, List<ModGlyphButton> glyphButtons, int xStart, int yStart, int maxRows) {
//        // Clear out the old buttons
//        clearButtons(glyphButtons);
//        final int PER_ROW = 6;
//        int toLayout = Math.min(parts.size(), PER_ROW * maxRows);
//        for (int i = 0; i < toLayout; i++) {
//            AbstractSpellPart part = parts.get(i);
//            int xOffset = 20 * (i % PER_ROW);
//            int yOffset = (i / PER_ROW) * 18;
//            ModGlyphButton cell = new ModGlyphButton(this, xStart + xOffset, yStart + yOffset, false, part.getIcon(), part.tag);
//            glyphButtons.add(cell);
//            addButton(cell);
//        }
//    }

    public void onPageIncrease(Button button){
        page++;
        if(displayedGlyphs.size() < 58 * (page + 1)){
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
        validate();
    }

    public void onPageDec(Button button){
        page--;
        if(page == 0){
            previousButton.active = false;
            previousButton.visible = false;
        }

        if(displayedGlyphs.size() > 58 * (page + 1)){
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
        validate();
    }

    public void onDocumentationClick(Button button){
        PatchouliAPI.get().openBookGUI(Registry.ITEM.getKey(ItemsRegistry.wornNotebook));
    }

    public void onColorClick(Button button){
        ParticleColor.IntWrapper color = SpellBook.getSpellColor(spell_book_tag, selected_cast_slot);
        Minecraft.getInstance().setScreen(new ModGuiColorScreen(color.r, color.g, color.b, selected_cast_slot));
    }

    public void onFamiliarClick(Button button){
        Collection<String> familiarHolders = new ArrayList<>();
        IFamiliarCap cap = FamiliarCap.getFamiliarCap(ArsNouveau.proxy.getPlayer()).orElse(null);
        if(cap != null){
            familiarHolders = cap.getUnlockedFamiliars();
        }
        Collection<String> finalFamiliarHolders = familiarHolders;
        Minecraft.getInstance().setScreen(new GuiFamiliarScreen(api, ArsNouveauAPI.getInstance().getFamiliarHolderMap().values().stream().filter(f -> finalFamiliarHolders.contains(f.id)).collect(Collectors.toList())));
    }

    public void onCraftingSlotClick(Button button){
        ModCraftingButton craftButton = (ModCraftingButton) button;

        LOGGER.debug("is the player holding shift down? " + ClientUtil.mC.options.keyShift.isDown());
        if (craftButton.stack == 1 || sneakHeld) craftButton.clear();
        else craftButton.stack--;

        validate();
    }

    public void onGlyphClick(Button button){
        ModGlyphButton button1 = (ModGlyphButton) button;

        if (button1.validationErrors.isEmpty()) {
            if (button1.isAugment) {
                for (int a = craftingCells.size() - 1; a > 0; a--) {
                    ModCraftingButton b = craftingCells.get(a);
                    if (!Objects.equals(b.resourceIcon, "") && !b.isAugment) break;

                    if (Objects.equals(b.resourceIcon, button1.resourceIcon)
                            && b.stack < maxAugmentStack) {
                        b.stack++;
                        validate();
                        return;
                    }
                }
            }

            for (ModCraftingButton b : craftingCells) {
                if (b.resourceIcon.equals("")) {
                    b.resourceIcon = button1.resourceIcon;
                    b.spellTag = button1.spell_id;
                    b.stack++;
                    b.isAugment = button1.isAugment;
                    validate();
                    return;
                }
            }
        }
    }

    public void onSlotChange(Button button){
        this.selected_slot.isSelected = false;
        this.selected_slot = (ModGuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selected_cast_slot = this.selected_slot.slotNum;
        updateCraftingSlots(this.selected_cast_slot);
        spell_name.setValue(SpellBook.getSpellName(spell_book_tag, this.selected_cast_slot));
        validate();
    }

    public void updateCraftingSlots(int bookSlot){
        //Crafting slots
        List<AbstractSpellPart> spell_recipe = this.spell_book_tag != null ? SpellBook.getRecipeFromTag(spell_book_tag, bookSlot).recipe : null;
        if (spell_recipe != null) LOGGER.debug("SPELL RECIPE SIZE IS " + spell_recipe.size());
        int spellIndex = 0;
        for (int i = 0; i < craftingCells.size(); i++) {
            //This wipes any data on the current slot
            ModCraftingButton slot = craftingCells.get(i);
            slot.clear();
            LOGGER.debug("IS THE SLOT CLEARED? " + (slot.stack == 0));

            boolean flag = false;
            while (!flag && spellIndex < spell_recipe.size()) {
                if (spell_recipe.get(spellIndex) instanceof AbstractCastMethod) {
                    spellIndex++;
                    continue;
                }

                flag = true;
            }
            if (!flag) continue;

            //Assign this slot a Spell part using Spell index
            slot.spellTag = spell_recipe.get(spellIndex).getTag();
            slot.resourceIcon = spell_recipe.get(spellIndex).getIcon();
            slot.isAugment = (spell_recipe.get(spellIndex) instanceof AbstractAugment);
            LOGGER.debug("SPELL INDEX SIZE BEFORE STACK COUNT " + spellIndex);
            //If there are Spell parts that equal the current spell part, stack em till the limit is reached or it's the end of the spell
            for (; spellIndex < spell_recipe.size(); spellIndex++){
                //If the spell parts don't equal, break
                LOGGER.debug("IS STACKING SPELL ICON EQUAL TO THIS SPELL ICON? " + Objects.equals(spell_recipe.get(spellIndex).getIcon(), slot.resourceIcon));
                if (!Objects.equals(spell_recipe.get(spellIndex).getIcon(), slot.resourceIcon)) break;
                //If we already reached the maxAugmentStack, break
                //LOGGER.debug("What's the current stack: " + (slot.stack) + ", what's the max? " + (maxAugmentStack));
                LOGGER.debug("Did I hit the max? " + (slot.stack == this.maxAugmentStack));
                if (slot.stack == this.maxAugmentStack) break;
                if (slot.stack == 1 && spell_recipe.get(spellIndex) instanceof AbstractEffect) break;
                LOGGER.debug("Stack size: " + (slot.stack + 1));
                //Increase the slot stack amount
                slot.stack++;
            }
            LOGGER.debug("SPELL INDEX SIZE AFTER STACK COUNT " + spellIndex);
        }
    }

    public void clear(Button button){
        boolean allWereEmpty = true;

        for (ModCraftingButton slot : craftingCells) {
            if(!slot.spellTag.equals("")) allWereEmpty = false;
            slot.clear();
        }

        if (allWereEmpty) spell_name.setValue("");

        validate();
    }

    public void onCreateClick(Button button) {
        validate();
        float cooldown = CombatGearItem.getCooldown(minecraft.player, gearStack.getOrCreateTag(), selected_cast_slot, true);
        if (validationErrors.isEmpty() && cooldown <= 0) {
            List<String> ids = new ArrayList<>();
            for (ModCraftingButton slot : craftingCells) {
                for (int a = 0; a < slot.stack; a++) {
                    ids.add(slot.spellTag);
                }
            }
            //This is where I add the method of casting
            CombatGearCap gearCap = CombatGearCap.getCap(ClientUtil.mC.player.getMainHandItem());
            switch (gearCap.getSelectedItem()){
                default:
                    ids.add(0, MethodTouch.INSTANCE.getTag());
                    break;
                case 1:
                    ids.add(0, MethodProjectile.INSTANCE.getTag());
                    break;
                case 2:
                    ids.add(0, MethodSelf.INSTANCE.getTag());
                    break;
            }

            NetworkHandler.INSTANCE.sendToServer(
                    new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getValue()));
        }
    }

    @Override
    public boolean keyPressed(int keyPressed, int xMouse, int yMouse) {
        if (keyPressed == minecraft.options.keyShift.getKey().getValue()){
            sneakHeld = true;
        }
        
        return super.keyPressed(keyPressed, xMouse, yMouse);
    }

    @Override
    public boolean keyReleased(int keyReleased, int xMouse, int yMouse) {
        if (keyReleased == minecraft.options.keyShift.getKey().getValue()){
            sneakHeld = false;
        }
        
        return super.keyReleased(keyReleased, xMouse, yMouse);
    }

    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
//        if(formTextRow >= 1) {
//            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.form").getString(), formTextRow > 6 ? 154 : 20 ,  5 + 18 * (formTextRow + (formTextRow == 1 ? 0 : 1)), -8355712);
//        }
        if(effectTextRow >= 1) {
//            int effectY = 5 + 18 * (effectTextRow + 1 - (effectTextRow >= 5 ? 5 : 0));
            int effectY = 5 + 18 * (effectTextRow + 1 - (effectTextRow >= 5 ? 6 : 0));
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.effect").getString(), effectTextRow > 5 ? 154 : 20, effectY, -8355712);
        }
        if(augmentTextRow >= 1) {
//            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20,  5 + 18 * (augmentTextRow + 1), -8355712);
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20,  5 + 18 * (augmentTextRow + (augmentTextRow == 1 ? 0 : 1)), -8355712);
        }
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_name_paper.png"), 16, 179, 0, 0, 109, 15,109,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15,72,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15,47,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);

        PlayerEntity player = ClientUtil.mC.player;
        float coolDown = CombatGearItem.getCooldown(player, gearStack.getOrCreateTag(), selected_cast_slot, true);

        if (validationErrors.isEmpty() && coolDown <= 0) {
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.create"), 233, 183, -8355712);
        } else {
            // Color code chosen to match GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            ITextComponent textComponent = new TranslationTextComponent("ars_nouveau.spell_book_gui.create")
                    .withStyle(s -> s.setStrikethrough(true).withColor(Color.parseColor("#FFB2B2")));
            // The final argument to draw desaturates the above color from the text component
            minecraft.font.draw(stack, textComponent, 233, 183, -8355712);
        }
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.clear").getString(), 177, 183, -8355712);
    }

    /**
     * Validates the current spell as well as the potential for adding each glyph.
     */
    public void validate() {
//        List<AbstractSpellPart> recipe = new LinkedList<>();
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();

        int firstBlankSlot = -1;
        int spellIndex = 0;
        // Reset the crafting slots and build the recipe to validate
        for (int i = 0; i < craftingCells.size(); i++) {
            ModCraftingButton craftButton = craftingCells.get(i);
            craftButton.validationErrors.clear();

            if (craftButton.spellTag.isEmpty()) {
                // The validator can cope with null. Insert it to preserve glyph indices.
                recipe.add(null);
                // Also note where we found the first blank.  Used later for the glyph buttons.
                if (firstBlankSlot < 0) firstBlankSlot = spellIndex;

                spellIndex++;
            } else {
                //This will make sure the stacked spell glyphs will be counted
                for (int a = 0; a < craftButton.stack; a++) {
                    recipe.add(api.getSpell_map().get(craftButton.spellTag));
                    spellIndex++;
                }
            }
        }

        // Validate the crafting slots
        List<SpellValidationError> errors = spellValidator.validate(recipe);
        LOGGER.error("Where are the errors located? ");
        if (errors.size() != 0) {
            spellIndex = 0;
            for (int a = 0; a < craftingCells.size(); a++) {
                ModCraftingButton craftButton = craftingCells.get(a);
                LOGGER.info("Crafting cell: " + a);

                for (int b = 0; b < craftButton.stack; b++) {

                    if (errors.get(errors.size() - 1).getPosition() < spellIndex) break;

                    for (int c = 0; c < errors.size(); c++) {
                        SpellValidationError error = errors.get(c);

                        if (error.getPosition() == spellIndex) {
                            craftButton.validationErrors.add(error);
                            break;
                        }
                    }

                    spellIndex++;
                }
                LOGGER.debug("END CRAFTING CELL: " + a);

                if (errors.get(errors.size() - 1).getPosition() < spellIndex) break;
            }
        }
        this.validationErrors = errors;
        //Go through the crafting slots one more time for checking banned glyphs
        for (int i = 0; i < craftingCells.size(); i++) {
            ModCraftingButton craftButton = craftingCells.get(i);

            if (craftButton.spellTag.isEmpty()) {
                spellIndex++;
            } else {
                AbstractSpellPart spellPart = api.getSpell_map().get(craftButton.spellTag);

                if (CombatGearItem.isBanned(spellPart, true)){
                    SpellValidationError error = new BaseSpellValidationError(spellIndex, spellPart, "banned_glyph");
                    craftButton.validationErrors.add(error);
                    this.validationErrors.add(error);
                }
                //This will make sure the stacked spell glyphs will be counted
                for (int a = 0; a < craftButton.stack; a++) {
                    spellIndex++;
                }
            }
        }

        List<AbstractSpellPart> copyRecipe = new LinkedList<>(recipe);
        copyRecipe.removeIf(Predicate.isEqual(null));

        CombatGearCap cap = CombatGearCap.getCap(ArsUtil.getHeldGearCap(minecraft.player, false, false));
        //This adds the automatically added method spell part
        switch (cap.getSelectedItem()){
            default:
                copyRecipe.add(0, MethodTouch.INSTANCE);
                break;
            case 1:
                copyRecipe.add(0, MethodProjectile.INSTANCE);
                break;
            case 2:
                copyRecipe.add(0, MethodSelf.INSTANCE);
                break;
        }

        this.currSpell = new Spell(copyRecipe);
        this.currSpell.setCost(CombatGearItem.SpellM.getInitialCost(this.currSpell, cap.getSelectedItem(), gearStack));
        // Validate the glyph buttons
        // Trim the spell to the first gap, if there is a gap
        if (firstBlankSlot >= 0) {
            recipe = new ArrayList<>(recipe.subList(0, firstBlankSlot));
        }
        LOGGER.debug("THIS IS WHAT's IN THE NEW SPELL LIST");
        LOGGER.debug(recipe);

        for(ModGlyphButton button : glyphButtons){
            validateGlyphButton(recipe, button);
        }
    }

    private void validateGlyphButton(List<AbstractSpellPart> recipe, ModGlyphButton glyphButton) {
        // Start from a clean slate
        glyphButton.validationErrors.clear();

        // Simulate adding the glyph to the current spell
        AbstractSpellPart spellPart = api.getSpell_map().get(glyphButton.spell_id);
        recipe.add(spellPart);

        // Filter the errors to ones referring to the simulated glyph
        glyphButton.validationErrors.addAll(
                spellValidator.validate(recipe).stream()
                        .filter(ve -> ve.getPosition() >= recipe.size() - 1).collect(Collectors.toList())
        );

        //This is my own personal check
        if (CombatGearItem.isBanned(spellPart, true)){
            glyphButton.validationErrors.add(new BaseSpellValidationError(recipe.size(), spellPart, "banned_glyph"));
        }

        // Remove the simulated glyph to make room for the next one
        recipe.remove(recipe.size() - 1);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        drawCastInfo(ms);
        spell_name.setSuggestion(spell_name.getValue().isEmpty() ? new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name").getString() : "");
    }
    public void drawCastInfo(MatrixStack ms){
        int x = bookRight - 100;
        int y = bookBottom - 55;
        int ySpacing = 11;
        CombatGearCap cap = CombatGearCap.getCap(ArsUtil.getHeldGearCap(ClientUtil.mC.player, false, false));
        boolean bowSelected = (cap.getSelectedItem() == bowInt);

        //Your mana
        int maxMana = ManaCapability.getMana(ClientUtil.mC.player).resolve().get().getMaxMana();
        font.draw(ms, "Your Mana: " + (maxMana), x, y, TextFormatting.DARK_AQUA.getColor());
        y += ySpacing;
        //How much the spell will cost
        int cost = currSpell.getCastingCost();
        int castColor = maxMana < cost ? TextFormatting.DARK_RED.getColor() : TextFormatting.DARK_GREEN.getColor();
        font.draw(ms, "Mana Cost: " + (cost), x, y, castColor);
        y += ySpacing;
        //And finally its cooldown
        float cooldown = CombatGearItem.calcCooldown(cap.getSelectedItem(), currSpell, false);
        font.draw(ms, "Cooldown: " + (cooldown), x, y, TextFormatting.DARK_GRAY.getColor());
    }
}
