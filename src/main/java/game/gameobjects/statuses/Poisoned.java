package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.builder.component.ParagraphBuilder;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.graphics.StyleSet;
import org.hexworks.zircon.api.modifier.Modifier;

import game.display.Display;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;
import game.gameobjects.entities.PlayerEntity;

public class Poisoned extends Status implements Behavable, FiltersIn{

    private int minDamage;
    private int maxDamage;
    private int turns;

    public Poisoned(int minDamage, int maxDamage, int turns) {
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.turns = turns;
        setCharacter(' ');
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(50, 200, 50, 100));
        HashSet<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifiers.blink());
        setModifiers(modifiers);
        setDescriptor("Poisoned");
    }

    @Override
    public int behave() {
        if (owner.getHP() > 0){
            owner.dealDamage(randomNumber(minDamage, maxDamage), DamageType.POISON);
        }
        turns--;
        if (turns <= 0){
            owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Poisoned) {
            return true;
        }
        return false;
    }

    @Override
    public void onStatusAdd() {
        if (this.owner instanceof PlayerEntity) {
            StyleSet style = Display.getLogStyleSet();
            style = style
                .withForegroundColor(style.getForegroundColor().withGreen(255));
            Display.log(
                ParagraphBuilder.newBuilder()
                    .withText("You are poisoned!")
                    .withComponentStyleSet(
                        Display.composeComponentStyleSet(style)
                    )
            );
        }
    }

}
