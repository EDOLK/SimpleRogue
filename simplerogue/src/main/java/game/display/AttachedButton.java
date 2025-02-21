package game.display;

import org.hexworks.cobalt.core.api.UUID;
import org.hexworks.cobalt.databinding.api.property.Property;
import org.hexworks.cobalt.databinding.api.value.ObservableValue;
import org.hexworks.cobalt.events.api.Subscription;
import org.hexworks.zircon.api.behavior.Boundable;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.ComponentStyleSet;
import org.hexworks.zircon.api.component.data.ComponentState;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Rect;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.StyleSet;
import org.hexworks.zircon.api.resource.TilesetResource;
import org.hexworks.zircon.api.uievent.ComponentEvent;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.MouseEvent;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.internal.component.InternalComponent;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AttachedButton <T> implements Button {
    private Button button;
    private T attachment;
    
    public AttachedButton(Button button, T attachment) {
        this.button = button;
        this.attachment = attachment;
    }

    public Button getButton() {
        return button;
    }

    public T getAttachment() {
        return attachment;
    }

    public boolean acceptsFocus() {
        return button.acceptsFocus();
    }
    public void clearCustomStyle() {
        button.clearCustomStyle();
    }
    public void clearFocus() {
        button.clearFocus();
    }
    public boolean containsBoundable(Boundable arg0) {
        return button.containsBoundable(arg0);
    }
    public boolean containsPosition(Position arg0) {
        return button.containsPosition(arg0);
    }
    public Position getAbsolutePosition() {
        return button.getAbsolutePosition();
    }
    public ComponentState getComponentState() {
        return button.getComponentState();
    }
    public ObservableValue<ComponentState> getComponentStateValue() {
        return button.getComponentStateValue();
    }
    public ComponentStyleSet getComponentStyleSet() {
        return button.getComponentStyleSet();
    }
    public Property<? extends ComponentStyleSet> getComponentStyleSetProperty() {
        return button.getComponentStyleSetProperty();
    }
    public Position getContentOffset() {
        return button.getContentOffset();
    }
    public Size getContentSize() {
        return button.getContentSize();
    }
    public StyleSet getCurrentStyle() {
        return button.getCurrentStyle();
    }
    public Property<Boolean> getDisabledProperty() {
        return button.getDisabledProperty();
    }
    public boolean getHasFocus() {
        return button.getHasFocus();
    }
    public ObservableValue<Boolean> getHasFocusValue() {
        return button.getHasFocusValue();
    }
    public int getHeight() {
        return button.getHeight();
    }
    public Property<Boolean> getHiddenProperty() {
        return button.getHiddenProperty();
    }
    public UUID getId() {
        return button.getId();
    }
    public Position getPosition() {
        return button.getPosition();
    }
    public Rect getRect() {
        return button.getRect();
    }
    public ObservableValue<Rect> getRectValue() {
        return button.getRectValue();
    }
    public Rect getRelativeBounds() {
        return button.getRelativeBounds();
    }
    public Position getRelativePosition() {
        return button.getRelativePosition();
    }
    public Size getSize() {
        return button.getSize();
    }
    public String getText() {
        return button.getText();
    }
    public Property<String> getTextProperty() {
        return button.getTextProperty();
    }
    public ColorTheme getTheme() {
        return button.getTheme();
    }
    public Property<ColorTheme> getThemeProperty() {
        return button.getThemeProperty();
    }
    public TilesetResource getTileset() {
        return button.getTileset();
    }
    public Property<TilesetResource> getTilesetProperty() {
        return button.getTilesetProperty();
    }
    public int getWidth() {
        return button.getWidth();
    }
    public int getX() {
        return button.getX();
    }
    public int getY() {
        return button.getY();
    }
    public Subscription handleComponentEvents(ComponentEventType arg0,
            Function1<? super ComponentEvent, ? extends UIEventResponse> arg1) {
        return button.handleComponentEvents(arg0, arg1);
    }
    public Subscription handleKeyboardEvents(KeyboardEventType arg0,
            Function2<? super KeyboardEvent, ? super UIEventPhase, ? extends UIEventResponse> arg1) {
        return button.handleKeyboardEvents(arg0, arg1);
    }
    public Subscription handleMouseEvents(MouseEventType arg0,
            Function2<? super MouseEvent, ? super UIEventPhase, ? extends UIEventResponse> arg1) {
        return button.handleMouseEvents(arg0, arg1);
    }
    public boolean intersects(Boundable arg0) {
        return button.intersects(arg0);
    }
    public boolean isDisabled() {
        return button.isDisabled();
    }
    public boolean isHidden() {
        return button.isHidden();
    }
    public boolean moveBy(Position arg0) {
        return button.moveBy(arg0);
    }
    public boolean moveDownBy(int arg0) {
        return button.moveDownBy(arg0);
    }
    public boolean moveLeftBy(int arg0) {
        return button.moveLeftBy(arg0);
    }
    public boolean moveRightBy(int arg0) {
        return button.moveRightBy(arg0);
    }
    public boolean moveTo(Position arg0) {
        return button.moveTo(arg0);
    }
    public boolean moveUpBy(int arg0) {
        return button.moveUpBy(arg0);
    }
    public Subscription onActivated(Function1<? super ComponentEvent, Unit> arg0) {
        return button.onActivated(arg0);
    }
    public Subscription onDeactivated(Function1<? super ComponentEvent, Unit> arg0) {
        return button.onDeactivated(arg0);
    }
    public Subscription onFocusGiven(Function1<? super ComponentEvent, Unit> arg0) {
        return button.onFocusGiven(arg0);
    }
    public Subscription onFocusTaken(Function1<? super ComponentEvent, Unit> arg0) {
        return button.onFocusTaken(arg0);
    }
    public Subscription processComponentEvents(ComponentEventType arg0, Function1<? super ComponentEvent, Unit> arg1) {
        return button.processComponentEvents(arg0, arg1);
    }
    public Subscription processKeyboardEvents(KeyboardEventType arg0,
            Function2<? super KeyboardEvent, ? super UIEventPhase, Unit> arg1) {
        return button.processKeyboardEvents(arg0, arg1);
    }
    public Subscription processMouseEvents(MouseEventType arg0,
            Function2<? super MouseEvent, ? super UIEventPhase, Unit> arg1) {
        return button.processMouseEvents(arg0, arg1);
    }
    public boolean requestFocus() {
        return button.requestFocus();
    }
    public void resetState() {
        button.resetState();
    }
    public void setComponentStyleSet(ComponentStyleSet arg0) {
        button.setComponentStyleSet(arg0);
    }
    public void setDisabled(boolean arg0) {
        button.setDisabled(arg0);
    }
    public void setHidden(boolean arg0) {
        button.setHidden(arg0);
    }
    public void setText(String arg0) {
        button.setText(arg0);
    }
    public void setTheme(ColorTheme arg0) {
        button.setTheme(arg0);
    }
    public void setTileset(TilesetResource arg0) {
        button.setTileset(arg0);
    }
    public InternalComponent asInternalComponent() {
        return button.asInternalComponent();
    }
    
}
