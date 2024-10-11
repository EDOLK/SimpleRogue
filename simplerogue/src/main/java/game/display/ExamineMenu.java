package game.display;

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
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.DropsXP;
import game.gamelogic.Examinable;
import game.gamelogic.HasAccuracy;
import game.gamelogic.HasDodge;
import game.gamelogic.HasResistances;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public class ExamineMenu extends Menu{

    public ExamineMenu(Examinable examinable){
        super();

        String description = examinable.getDescription();
        String name = examinable.getName();
        Tile tile = examinable.getTile();

        Panel panel = PanelBuilder.newBuilder()
            .withSize(screen.getWidth()/3, screen.getHeight()/3)
            .build();

        int panelWidthOffset = 0;
        int panelHeightOffset = 0;

        Header nameHeader = HeaderBuilder.newBuilder()
            .withText(name)
            .withSize(panel.getWidth()-4, 1)
            .withPosition(3,1)
            .build();

        panel.addComponent(nameHeader);

        String weightString = null;
        
        int descriptionHeight = panel.getHeight() - 3;

        Button equipmentButton = null;

        Component equipmentButtonReference = null;
        
        Panel InfoPanel = null;
        
        if (examinable instanceof Entity entity){
            name = entity.getName();
            weightString = "Weight: " + Integer.toString(entity.getWeight()) + " (" + Integer.toString(entity.getBaseWeight()) + ")";
            descriptionHeight -= 3;
            
            if (entity instanceof Armored || entity instanceof Armed){
                descriptionHeight -= 2;
                equipmentButton = ButtonBuilder.newBuilder()
                    .withText("Equipment")
                    .build();
                equipmentButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                    Display.setMenu(EquipmentMenu.createExamineEquipmentMenu(entity));
                    return UIEventResponse.processed();
                });
            }
            
            InfoPanel = PanelBuilder.newBuilder()
                .withSize(15, panel.getHeight())
                .build();
            
            panelWidthOffset = -7;
            
            int pos = 1;
            
            Header hpHeader = HeaderBuilder.newBuilder()
                .withText("HP: " + entity.getHP() + "/" + entity.getMaxHP())
                .withPosition(1, pos)
                .build();

            InfoPanel.addComponent(hpHeader);
            
            pos += 2;
            
            if (entity instanceof HasAccuracy hasAccuracy){

                Header accuracyHeader = HeaderBuilder.newBuilder()
                    .withText("ACCURACY: " + (hasAccuracy.getAccuracy() > 0 ? "+" : "-") + hasAccuracy.getAccuracy())
                    .withPosition(1, pos)
                    .build();
                
                InfoPanel.addComponent(accuracyHeader);

                pos += 2;

            }
            
            if (entity instanceof HasDodge hasDodge){

                Header dodgeHeader = HeaderBuilder.newBuilder()
                    .withText("DODGE: " + (hasDodge.getDodge() > 0 ? "+" : "-") + hasDodge.getDodge())
                    .withPosition(1, pos)
                    .build();
                
                InfoPanel.addComponent(dodgeHeader);

                pos += 2;

            }
            
            if (entity instanceof DropsXP dropsXP){
                
                Header xpHeader = HeaderBuilder.newBuilder()
                    .withText("XP: " + dropsXP.dropXP())
                    .withPosition(1, pos)
                    .build();
                
                InfoPanel.addComponent(xpHeader);

                pos += 2;

            }
            
            if (entity instanceof HasResistances hasResistances){

                Button resistancesButton = ButtonBuilder.newBuilder()
                    .withText("Resistance")
                    .withPosition(1, pos)
                    .build();
                
                InfoPanel.addComponent(resistancesButton);
                
                resistancesButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                    Display.setMenu(new ResistanceMenu(hasResistances));
                    return UIEventResponse.processed();
                });
                
                pos += 2;
            }

        }
        
        if (examinable instanceof Item item){
            weightString = "Weight: " + Integer.toString(item.getWeight());
            descriptionHeight -= 3;
        }

        Paragraph descriptionParagraph = ParagraphBuilder.newBuilder()
            .withText(description)
            .withSize(panel.getWidth()-2, descriptionHeight)
            .withPosition(1, 3)
            .build();

        panel.addComponent(descriptionParagraph);
        
        equipmentButtonReference = descriptionParagraph;
        
        if (weightString != null){
            Header weightHeader = HeaderBuilder.newBuilder()
                .withText(weightString)            
                .withSize(weightString.length(), 1)
                .withPosition(Position.bottomLeftOf(descriptionParagraph).plus(Position.create(0, 1)))
                .build();
            panel.addComponent(weightHeader);
            equipmentButtonReference = weightHeader;
        }
        
        if (equipmentButton != null){
            equipmentButton.moveTo(Position.bottomLeftOf(equipmentButtonReference).plus(Position.create(0, 1)));
            panel.addComponent(equipmentButton);
        }
        
        panel.moveTo(Position.create((screen.getWidth()/2 - panel.getWidth()/2)+panelWidthOffset, (screen.getHeight()/2 - panel.getHeight()/2)+panelHeightOffset));
        screen.addComponent(panel);
        
        if (InfoPanel != null){
            InfoPanel.moveTo(Position.topRightOf(panel).plus(Position.create(1, 0)));
            screen.addComponent(InfoPanel);
        }

        screen.draw(tile, Position.topLeftOf(panel).plus(Position.create(1, 1)));
    }
    
}
