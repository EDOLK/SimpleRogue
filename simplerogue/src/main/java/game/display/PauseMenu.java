package game.display;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

public class PauseMenu extends Menu{

    public PauseMenu(){
        super();

        Panel pausePanel = PanelBuilder.newBuilder()
            .withPosition(screen.getWidth()/2 - 8, screen.getHeight()/2 - 8)
            .withSize(16,16)
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "Paused")
            )
            .build();

        screen.addComponent(pausePanel);

        Button continueButton = ButtonBuilder.newBuilder()
            .withPosition(pausePanel.getWidth()/2 - 6, (int)(pausePanel.getHeight()/4))
            .withText("continue")
            .build();

        continueButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            Display.revertMenu();
            return UIEventResponse.processed();
        });

        pausePanel.addComponent(continueButton);
        
        Button quitButton = ButtonBuilder.newBuilder()
            .withPosition(pausePanel.getWidth()/2 - 4, (int)(pausePanel.getHeight()/1.5))
            .withText("quit")
            .build();
        
        quitButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            System.exit(0);
            return UIEventResponse.processed();
        });

        pausePanel.addComponent(quitButton);
    }

    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        if (event.getCode() == KeyCode.ESCAPE){
            Display.revertMenu();
            return UIEventResponse.processed();
        }
        return UIEventResponse.pass();
    }
    
}
