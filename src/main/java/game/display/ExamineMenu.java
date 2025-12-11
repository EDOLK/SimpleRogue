package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.builder.component.ParagraphBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.component.Paragraph;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.DropsXP;
import game.gamelogic.Examinable;
import game.gamelogic.HasAccuracy;
import game.gamelogic.HasDodge;
import game.gamelogic.HasResistances;
import game.gamelogic.HasVulnerabilities;
import game.gameobjects.DisplayableTile;
import game.gameobjects.enchantments.Enchantment;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.terrains.Terrain;

public class ExamineMenu extends Menu{

    private Examinable examinable;

    public ExamineMenu(Examinable examinable){
        super();
        this.examinable = examinable;

        String description = examinable.getDescription();
        String name = examinable.getName();
        String type = null;
        Tile tile = Tile.defaultTile();
        if (examinable instanceof DisplayableTile dt) {
            tile = dt.getTile();
        }

        Panel panel = PanelBuilder.newBuilder()
            .withPreferredSize((int)(screen.getWidth()/2.5), (int)(screen.getHeight()/2.5))
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "Examine")
            )
            .build();

        int panelWidthOffset = 0;
        int panelHeightOffset = 0;

        Header nameHeader = HeaderBuilder.newBuilder()
            .withText(name)
            .withPreferredSize(panel.getWidth()-5, 1)
            .withPosition(3,1)
            .build();
        panel.addComponent(nameHeader);

        String weightString = null;
        
        int descriptionHeight = panel.getHeight() - 5;

        List<Component> infoComponents = new ArrayList<>();
        
        int pos = 1;

        int infoWidth = 15;

        switch (examinable) {
            case Entity entity ->{
                type = "Entity";
                weightString = "Weight: " + Integer.toString(entity.getWeight()) + " (" + Integer.toString(entity.getBaseWeight()) + ")";
                descriptionHeight -= 3;
                
                Header hpHeader = HeaderBuilder.newBuilder()
                    .withText("HP: " + entity.getHP() + "/" + entity.getMaxHP())
                    .withPosition(1, pos)
                    .build();

                infoWidth = compare(hpHeader, infoWidth);

                infoComponents.add(hpHeader);
                
                pos += 2;

            }
            case Terrain terrain ->{
                type = "Terrain";
            }
            case Item item ->{
                weightString = "Weight: " + Integer.toString(item.getWeight());
                descriptionHeight -= 3;
                switch (item) {
                    case Weapon weapon -> {
                        type = "Weapon";
                        Header damageHeader = HeaderBuilder.newBuilder()
                            .withText("DAMAGE: " + weapon.getMinDamage() + " - " + weapon.getMaxDamage())
                            .withPosition(1,pos)
                            .build();
                        infoWidth = compare(damageHeader, infoWidth);
                        infoComponents.add(damageHeader);
                        pos += 2;
                        Header damageTypeHeader = HeaderBuilder.newBuilder()
                            .withText("TYPE: " + weapon.getDamageType())
                            .withPosition(1,pos)
                            .build();
                        infoWidth = compare(damageTypeHeader, infoWidth);
                        infoComponents.add(damageTypeHeader);
                        pos += 2;
                        if (weapon.getEnchantment() != null) {
                            Button enchantmentButton = ButtonBuilder.newBuilder()
                                .withText("Enchantment")
                                .withPosition(1,pos)
                                .build();
                            enchantmentButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
                                Display.setMenu(new ExamineMenu(weapon.getEnchantment()));
                                return UIEventResponse.processed();
                            });
                            infoWidth = compare(enchantmentButton, infoWidth);
                            infoComponents.add(enchantmentButton);
                            pos += 2;
                        }
                    }
                    case Armor armor -> {
                        type = "Armor";
                        if (armor.getEnchantment() != null) {
                            Button enchantmentButton = ButtonBuilder.newBuilder()
                                .withText("Enchantment")
                                .withPosition(1,pos)
                                .build();
                            enchantmentButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
                                Display.setMenu(new ExamineMenu(armor.getEnchantment()));
                                return UIEventResponse.processed();
                            });
                            infoWidth = compare(enchantmentButton, infoWidth);
                            infoComponents.add(enchantmentButton);
                            pos += 2;
                        }
                    }
                    default -> {
                        type = "Item";
                    }
                }
            }
            case Enchantment<?> enchantment -> {
                type = "Enchantment";
            }
            default -> {

            }
        }
        
        if (examinable instanceof HasAccuracy hasAccuracy && hasAccuracy.getAccuracy() != 0){
            Header accuracyHeader = HeaderBuilder.newBuilder()
                .withText("ACCURACY: " + (hasAccuracy.getAccuracy() > 0 ? "+" : "") + hasAccuracy.getAccuracy())
                .withPosition(1, pos)
                .build();
            infoWidth = compare(accuracyHeader, infoWidth);
            infoComponents.add(accuracyHeader);
            pos += 2;
        }
        
        if (examinable instanceof HasDodge hasDodge && hasDodge.getDodge() != 0){
            Header dodgeHeader = HeaderBuilder.newBuilder()
                .withText("DODGE: " + (hasDodge.getDodge() > 0 ? "+" : "") + hasDodge.getDodge())
                .withPosition(1, pos)
                .build();
            infoWidth = compare(dodgeHeader, infoWidth);
            infoComponents.add(dodgeHeader);
            pos += 2;
        }
        
        if (examinable instanceof DropsXP dropsXP && dropsXP.dropXP() != 0){
            Header xpHeader = HeaderBuilder.newBuilder()
                .withText("XP: " + dropsXP.dropXP())
                .withPosition(1, pos)
                .build();
            infoWidth = compare(xpHeader, infoWidth);
            infoComponents.add(xpHeader);
            pos += 2;
        }

        if (examinable instanceof Entity entity && (entity instanceof Armored || entity instanceof Armed)){

            Button equipmentButton = ButtonBuilder.newBuilder()
                .withText("Equipment")
                .withPosition(1, pos)
                .build();
            equipmentButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                Display.setMenu(EquipmentMenu.createExamineEquipmentMenu(entity));
                return UIEventResponse.processed();
            });
            infoWidth = compare(equipmentButton, infoWidth);
            infoComponents.add(equipmentButton);
            pos += 2;
        }
        
        if (examinable instanceof HasResistances hasResistances && !hasResistances.getResistances().isEmpty()){

            Button resistancesButton = ButtonBuilder.newBuilder()
                .withText("Resistance")
                .withPosition(1, pos)
                .build();
            
            infoWidth = compare(resistancesButton, infoWidth);

            infoComponents.add(resistancesButton);
            
            resistancesButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                Display.setMenu(new ResistanceMenu(hasResistances));
                return UIEventResponse.processed();
            });
            
            pos += 2;
        }

        if (examinable instanceof HasVulnerabilities hasVulnerabilities && !hasVulnerabilities.getVulnerabilities().isEmpty()){

            Button vulnerabilitiesButton = ButtonBuilder.newBuilder()
                .withText("Vulnerability")
                .withPosition(1, pos)
                .build();
            
            infoWidth = compare(vulnerabilitiesButton, infoWidth);

            infoComponents.add(vulnerabilitiesButton);
            
            vulnerabilitiesButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                Display.setMenu(new VulnerabilityMenu(hasVulnerabilities));
                return UIEventResponse.processed();
            });
            
            pos += 2;
        }

        Paragraph descriptionParagraph = ParagraphBuilder.newBuilder()
            .withText(description)
            .withPreferredSize(panel.getWidth()-4, descriptionHeight)
            .withPosition(1, 3)
            .build();

        panel.addComponent(descriptionParagraph);
        
        if (weightString != null){
            Header weightHeader = HeaderBuilder.newBuilder()
                .withText(weightString)            
                .withPreferredSize(weightString.length(), 1)
                .withPosition(Position.bottomLeftOf(descriptionParagraph).minus(Position.create(1,0)))
                .build();
            panel.addComponent(weightHeader);
        }

        Panel infoPanel = null;
        
        if (!infoComponents.isEmpty()) {

            panelWidthOffset = -(int)Math.ceil(infoWidth/2);

            infoPanel = PanelBuilder.newBuilder()
                .withPreferredSize(infoWidth, panel.getHeight())
                .withDecorations(
                    type != null ? ComponentDecorations.box(BoxType.SINGLE,type) : null
                )
                .build();
            for (Component component : infoComponents) {
                infoPanel.addComponent(component);
            }
        }

        panel.moveTo(Position.create((screen.getWidth()/2 - panel.getWidth()/2)+panelWidthOffset, (screen.getHeight()/2 - panel.getHeight()/2)+panelHeightOffset));
        screen.addComponent(panel);

        if (infoPanel != null) {
            infoPanel.moveTo(Position.topRightOf(panel).plus(Position.create(1, 0)));
            screen.addComponent(infoPanel);
        }

        screen.draw(tile, Position.topLeftOf(panel).plus(Position.create(2, 2)));
    }

    private int compare(Component component, int compareTo){
        if (component.getWidth() + 4 > compareTo) {
            return component.getWidth() + 4;
        }
        return compareTo;
    }

    @Override
    public Menu refresh() {
        return new ExamineMenu(this.examinable);
    }
    
}
