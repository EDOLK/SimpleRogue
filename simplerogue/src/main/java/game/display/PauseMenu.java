package game.display;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;

public class PauseMenu extends Menu{

    private Panel pausePanel;

    public PauseMenu(){
        super();

        this.pausePanel = PanelBuilder.newBuilder()
            .withPosition(screen.getWidth()/2 - 8, screen.getHeight()/2 - 8)
            .withSize(15,17)
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "Paused")
            )
            .build();

        screen.addComponent(this.pausePanel);

        Button continueButton = ButtonBuilder.newBuilder()
            .withText("continue")
            .build();

        continueButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            Display.revertMenu();
            return UIEventResponse.processed();
        });

        Button restartButton = ButtonBuilder.newBuilder()
            .withText("restart")
            .build();

        restartButton.handleComponentEvents(ComponentEventType.ACTIVATED,
        (event) -> {
            Dungeon.initialize(50,50);
            FloorMenu floorMenu = new FloorMenu();
            Display.setRootMenu(floorMenu);
            Display.setAndForgetMenus(floorMenu);
            return UIEventResponse.processed();
        });

        Button quitButton = ButtonBuilder.newBuilder()
            .withText("quit")
            .build();
        
        quitButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            System.exit(0);
            return UIEventResponse.processed();
        });

        attachButtons(continueButton, restartButton, quitButton);

    }

    private void attachButtons(Button... buttons){
        int divisor = buttons.length+1;
        int yOffset = (pausePanel.getHeight()-2) / divisor;
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            button.moveTo(
                Position.create(
                    ((pausePanel.getWidth()-2)/2)-(button.getWidth()/2),
                    (yOffset)*(i+1)
                )
            );
            pausePanel.addComponent(button);
        }
    }
    
}
