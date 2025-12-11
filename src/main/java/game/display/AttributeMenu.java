package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.LabelBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.component.AttachedComponent;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Label;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.display.KeyMap.Action;
import game.gamelogic.Attribute;
import game.gamelogic.HasAttributes;

public class AttributeMenu extends Menu {

    private Label pointLabel;
    private List<AttributeEntry> entries = new ArrayList<>();
    private WrappedInteger points;
    private HasAttributes hasAttributes;

    public AttributeMenu(HasAttributes hasAttributes){
        super();
        this.points = new WrappedInteger(hasAttributes.getAttributePoints());
        this.hasAttributes = hasAttributes;
        int y = 0;
        Panel panel = PanelBuilder.newBuilder()
            .withPosition(Position.create(this.screen.getWidth()/2 - (this.screen.getWidth()/3/2), this.screen.getHeight()/2 - (this.screen.getHeight()/3/2)))
            .withPreferredSize(this.screen.getWidth()/3, this.screen.getHeight()/3)
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Attributes"))
            .build();
        Header pointHeader = HeaderBuilder.newBuilder()
            .withPosition(Position.create(0,y))
            .withText("Points: ")
            .build();
        pointLabel = LabelBuilder.newBuilder()
            .withText(String.valueOf(points.getValue()))
            .withPosition(Position.bottomRightOf(pointHeader).minus(Position.create(0,1)))
            .build();
        panel.addComponent(pointHeader);
        panel.addComponent(pointLabel);
        y += 2;
        for (Attribute attribute : Attribute.values()) {
            AttributeEntry attributeEntry = new AttributeEntry(attribute, hasAttributes.getAttribute(attribute), y);
            entries.add(attributeEntry);
            attributeEntry.addComponents(panel);
            y += 2;
        }

        Button resetButton = ButtonBuilder.newBuilder()
            .withText("Reset")
            .withPosition(0, panel.getHeight()-3)
            .build();

        resetButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            for (AttributeEntry entry : entries) {
                points.setValue(points.getValue() + entry.addedValue);
                entry.addedValue = 0;
            }
            this.updateLabelsAndToggleButtons();
            return UIEventResponse.processed();
        });
        panel.addComponent(resetButton);

        Button applyButton = ButtonBuilder.newBuilder()
            .withText("Apply")
            .withPosition(panel.getWidth()-9, panel.getHeight()-3)
            .build();

        applyButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            doApply(hasAttributes, points);
            return UIEventResponse.processed();
        });

        panel.addComponent(applyButton);

        this.screen.addComponent(panel);
    }

    public void updateLabelsAndToggleButtons(){
        for (AttributeEntry entry : this.entries) {
            entry.updateSelf();
        }
    }

    public void doApply(HasAttributes hasAttributes, WrappedInteger points){
        for (AttributeEntry entry : entries) {
            hasAttributes.setAttribute(entry.attribute, (entry.value + entry.addedValue));
            entry.addedValue = 0;
            entry.value = hasAttributes.getAttribute(entry.attribute);
        }
        hasAttributes.setAttributePoints(points.getValue());
        this.updateLabelsAndToggleButtons();
    }

    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase){
        if (Display.getKeyMap().getAction(event.getCode()) == Action.ESCAPE){
            doApply(hasAttributes, points);
        }
        return super.handleKeyboardEvent(event, phase);
    };

    private class AttributeEntry{
        private Button decrementButton;
        private Button incrementButton;
        private Header attributeHeader;
        private Label attributeLabel;
        private int value;
        private int addedValue = 0;
        private Attribute attribute;

        public AttributeEntry(Attribute attribute, int value, int yOffset){

            this.value = value;

            this.attribute = attribute;

            attributeHeader = HeaderBuilder.newBuilder()
                .withPosition(0,yOffset)
                .withText(attribute.shortHand + ": ")
                .build();

            decrementButton = ButtonBuilder.newBuilder()
                .withText("<<")
                .withPosition(Position.bottomRightOf(attributeHeader).minus(Position.create(0,1)))
                .withDecorations()
                .build();

            decrementButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                if (addedValue <= 0 || decrementButton.isDisabled()) {
                    return UIEventResponse.pass();
                } else if (addedValue > 0) {
                    points.increment();
                    addedValue--;
                }
                AttributeMenu.this.updateLabelsAndToggleButtons();
                return UIEventResponse.processed();
            });

            attributeLabel = LabelBuilder.newBuilder()
                .withText(String.valueOf(value + addedValue))
                .withPosition(Position.bottomRightOf(decrementButton).minus(Position.create(0,1)))
                .withPreferredSize(3,1)
                .build();

            incrementButton = ButtonBuilder.newBuilder()
                .withText(">>")
                .withPosition(Position.bottomRightOf(attributeLabel).minus(Position.create(0,1)))
                .withDecorations()
                .build();

            incrementButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                if (incrementButton.isHidden() || points.getValue() <= 0) {
                    return UIEventResponse.pass();
                } else if (points.getValue() > 0){
                    points.decrement();
                    addedValue++;
                }
                AttributeMenu.this.updateLabelsAndToggleButtons();
                return UIEventResponse.processed();
            });

        }

        public void updateSelf(){
            attributeLabel.setText(String.valueOf(value + addedValue));
            AttributeMenu.this.pointLabel.setText(String.valueOf(AttributeMenu.this.points.getValue()));
            decrementButton.setDisabled(addedValue <= 0);
            incrementButton.setDisabled(AttributeMenu.this.points.getValue() <= 0);
        }

        public AttachedComponent[] addComponents(Container containter){
            AttachedComponent[] comps = new AttachedComponent[4];
            comps[0] = containter.addComponent(attributeHeader);
            comps[1] = containter.addComponent(decrementButton);
            decrementButton.setDisabled(addedValue <= 0);
            comps[2] = containter.addComponent(attributeLabel);
            comps[3] = containter.addComponent(incrementButton);
            incrementButton.setDisabled(points.getValue() <= 0);
            return comps;
        }
    }

    private class WrappedInteger{
        private int value;
        public WrappedInteger(int i) {
            this.value = i;
        }
        public void setValue(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
        public void increment(){
            this.value = this.value + 1;
        }
        public void decrement(){
            this.value = this.value - 1;
        }
    }

}
