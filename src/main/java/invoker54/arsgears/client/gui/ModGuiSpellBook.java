package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.FamiliarCap;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliarCap;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GuiColorScreen;
import com.hollingsworth.arsnouveau.client.gui.book.GuiFamiliarScreen;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.client.gui.button.*;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class ModGuiSpellBook extends BaseBook {
    private static final Logger LOGGER = LogManager.getLogger();

    public int numLinks = 10;
    public SpellBook spellBook;
    public ArsNouveauAPI api;

    private int selected_cast_slot;
    public TextFieldWidget spell_name;
    public NoShadowTextField searchBar;
    public CompoundNBT spell_book_tag;
    public ModGuiSpellSlot selected_slot;
    public int max_spell_tier; // Used to load spells that are appropriate tier
    List<ModCraftingButton> craftingCells = new ArrayList<>();
    public List<AbstractSpellPart> unlockedSpells;
    public List<AbstractSpellPart> castMethods;
    public List<AbstractSpellPart> augments;
    public List<AbstractSpellPart> displayedGlyphs;
    public List<AbstractSpellPart> allEffects;
    public List<ModGlyphButton> castMethodButtons;
    public List<ModGlyphButton> augmentButtons;
    public List<ModGlyphButton> effectButtons;
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

    int gearCycle;

    public ModGuiSpellBook(CompoundNBT tag, int tier, String unlockedSpells, int gearCycle, int selected_cast_slot) {
        super();
        this.api = ArsNouveauAPI.getInstance();

        this.max_spell_tier = tier;
        this.spell_book_tag = tag;
        this.unlockedSpells = SpellRecipeUtil.getSpellsFromString(unlockedSpells);
        this.gearCycle = gearCycle;
        this.selected_cast_slot = selected_cast_slot;

        this.castMethods = new ArrayList<>();
        this.augments = new ArrayList<>();
        this.displayedGlyphs = new ArrayList<>();
        allEffects = new ArrayList<>();


        this.displayedGlyphs = this.unlockedSpells;
        this.castMethodButtons = new ArrayList<>();
        this.augmentButtons = new ArrayList<>();
        this.effectButtons = new ArrayList<>();
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                api.getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(tier)
        );
    }

    public static void open(ItemStack gearStack){
        //The spell book tag (where all the spell book data is stored)
        CompoundNBT spell_book_tag = gearStack.getOrCreateTag();
        //Tier of my combat gear (minus 1 since I also don't want the player casting on STONE tier)
        int tier = (((CombatGearItem)gearStack.getItem()).getTier().ordinal() - 1);
        //Unlocked Spells
        String unlockedSpells = SpellBook.getUnlockedSpellString(spell_book_tag);
        //The currently selected item on the combat gear
        int gearCyle = CombatGearCap.getCap(gearStack).getSelectedItem();

        int cast_slot = SpellBook.getMode(gearStack.getOrCreateTag());

        Minecraft.getInstance().setScreen(new ModGuiSpellBook(spell_book_tag, tier, unlockedSpells, gearCyle, cast_slot));
    }

    @Override
    public void init() {
        super.init();
        //This is the currently selected spell
        int selected_slot_ind = SpellBook.getMode(spell_book_tag);
        if(selected_slot_ind == 0) selected_slot_ind = 1;

        //Glyph slots for the spell
        for (int i = 0; i < numLinks; i++) {
            String icon = null;
            String spell_id = "";
            int offset = i >= 5 ? 14 : 0;
            ModCraftingButton cell = new ModCraftingButton(this,bookLeft + 19 + 24 * i + offset, bookTop + FULL_HEIGHT - 47, i, this::onCraftingSlotClick);
            //ModGlyphButton glyphButton = new ModGlyphButton(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, )
            addButton(cell);
            craftingCells.add(cell);
        }
        updateCraftingSlots(selected_slot_ind);

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
        for(int i = 1; i <= max_spell_tier; i++){
            ModGuiSpellSlot slot = new ModGuiSpellSlot(this,bookLeft + 281, bookTop +1 + 15 * i, getActualSlot(i));
            if(getActualSlot(i) == selected_slot_ind) {
                selected_slot = slot;
                selected_cast_slot = getActualSlot(i);
                LOGGER.debug("THE ACTUAL SLOT IS " + getActualSlot(i));
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

    public int getActualSlot(int a){
        return (3 * gearCycle) + a;
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
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 20;
        boolean foundForms = false;
        boolean foundAugments = false;
        boolean foundEffects = false;
        List<AbstractSpellPart> sorted = new ArrayList<>();
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractCastMethod).collect(Collectors.toList()));
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractAugment).collect(Collectors.toList()));
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractEffect).collect(Collectors.toList()));
        int perPage = 58;
        sorted = sorted.subList(perPage * page, Math.min(sorted.size(), perPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int row_offset = page == 0 ? 2 : 0;


        for(int i = 0; i < sorted.size(); i++){
            AbstractSpellPart part = sorted.get(i);
            if(!foundForms && part instanceof AbstractCastMethod) {
                foundForms = true;
                adjustedRowsPlaced += 1;
                totalRowsPlaced += 1;
                formTextRow = page != 0 ? 0 : totalRowsPlaced;
                adjustedXPlaced = 0;
            }

            if(!foundAugments && part instanceof AbstractAugment){
                foundAugments = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                augmentTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            } else if(!foundEffects && part instanceof AbstractEffect){
                foundEffects = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                effectTextRow = page != 0 ? 0 :totalRowsPlaced - 1;
                adjustedXPlaced = 0;
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

            ModGlyphButton cell = new ModGlyphButton(this, xStart + xOffset, yPlace, false, part.getIcon(), part.tag);
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
        ((ModCraftingButton) button).clear();
        validate();
    }

    public void onGlyphClick(Button button){
        ModGlyphButton button1 = (ModGlyphButton) button;

        if (button1.validationErrors.isEmpty()) {
            for (ModCraftingButton b : craftingCells) {
                if (b.resourceIcon.equals("")) {
                    b.resourceIcon = button1.resourceIcon;
                    b.spellTag = button1.spell_id;
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
        for (int i = 0; i < craftingCells.size(); i++) {
            ModCraftingButton slot = craftingCells.get(i);
            slot.spellTag = "";
            slot.resourceIcon = "";
            if (spell_recipe != null && i < spell_recipe.size()){
                slot.spellTag = spell_recipe.get(i).getTag();
                slot.resourceIcon = spell_recipe.get(i).getIcon();
            }
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
        if (validationErrors.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (ModCraftingButton slot : craftingCells) {
                ids.add(slot.spellTag);
            }
            NetworkHandler.INSTANCE.sendToServer(
                    new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getValue()));
        }
    }

    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        if(formTextRow >= 1) {
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.form").getString(), formTextRow > 6 ? 154 : 20 ,  5 + 18 * (formTextRow + (formTextRow == 1 ? 0 : 1)), -8355712);
        }
        if(effectTextRow >= 1) {

            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.effect").getString(), effectTextRow > 6 ? 154 : 20,  5 + 18 * (effectTextRow  + 1), -8355712);
        }
        if(augmentTextRow >= 1) {
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20,  5 + 18 * (augmentTextRow + 1), -8355712);
        }
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_name_paper.png"), 16, 179, 0, 0, 109, 15,109,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15,72,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15,47,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        if (validationErrors.isEmpty()) {
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
    private void validate() {
        List<AbstractSpellPart> recipe = new LinkedList<>();
        int firstBlankSlot = -1;

        // Reset the crafting slots and build the recipe to validate
        for (int i = 0; i < craftingCells.size(); i++) {
            ModCraftingButton b = craftingCells.get(i);
            b.validationErrors.clear();
            if (b.spellTag.isEmpty()) {
                // The validator can cope with null. Insert it to preserve glyph indices.
                recipe.add(null);
                // Also note where we found the first blank.  Used later for the glyph buttons.
                if (firstBlankSlot < 0) firstBlankSlot = i;
            } else {
                recipe.add(api.getSpell_map().get(b.spellTag));
            }
        }

        // Validate the crafting slots
        List<SpellValidationError> errors = spellValidator.validate(recipe);
        for (SpellValidationError ve : errors) {
            // Attach errors to the corresponding crafting slot (when applicable)
            if (ve.getPosition() >= 0 && ve.getPosition() <= craftingCells.size()) {
                ModCraftingButton b = craftingCells.get(ve.getPosition());
                b.validationErrors.add(ve);
            }
        }
        this.validationErrors = errors;

        // Validate the glyph buttons
        // Trim the spell to the first gap, if there is a gap
        if (firstBlankSlot >= 0) {
            recipe = new ArrayList<>(recipe.subList(0, firstBlankSlot));
        }

        for(ModGlyphButton button : glyphButtons){
            validateGlyphButton(recipe, button);
        }
    }

    private void validateGlyphButton(List<AbstractSpellPart> recipe, ModGlyphButton glyphButton) {
        // Start from a clean slate
        glyphButton.validationErrors.clear();

        // Simulate adding the glyph to the current spell
        recipe.add(api.getSpell_map().get(glyphButton.spell_id));

        // Filter the errors to ones referring to the simulated glyph
        glyphButton.validationErrors.addAll(
                spellValidator.validate(recipe).stream()
                        .filter(ve -> ve.getPosition() >= recipe.size() - 1).collect(Collectors.toList())
        );

        // Remove the simulated glyph to make room for the next one
        recipe.remove(recipe.size() - 1);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        spell_name.setSuggestion(spell_name.getValue().isEmpty() ? new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name").getString() : "");
    }
}
