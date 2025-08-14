package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.LabelBuilder;
import org.hexworks.zircon.api.builder.component.ParagraphBuilder;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.AttachedComponent;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Label;
import org.hexworks.zircon.api.component.Paragraph;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.skilltrees.SkillTree;
import game.gamelogic.skilltrees.SkillTree.SkillEntry;
import game.gamelogic.skilltrees.UsesSkillTrees;
import game.gameobjects.entities.Entity;
import kotlin.Pair;

public class SkillTreeMenu extends Menu {

    private SkillTree tree;
    private Entity entity;
    private VBox entryBox;
    private List<AttachedComponent> attachments = new ArrayList<>();
    private Header pointHeader;

    public SkillTreeMenu(SkillTree tree, Entity entity) {
        super();
        this.tree = tree;
        this.entity = entity;
        List<SkillEntry> entryList = new ArrayList<>(tree.getSkillEntries());
        Container entryContainer = Display.createFittedContainer(
            this.screen,
            "Skills",
            entryList
        );
        List<Pair<Button,SkillEntry>> pairs = Display.populateContainer(
            entryContainer,
            (entry) -> {
                if (!entry.hasRequirements(entity)) {
                    return UIEventResponse.pass();
                }
                entry.apply(entity);
                tree.removeSkillEntry(entry);
                if (entity instanceof UsesSkillTrees ust) {
                    ust.getSkillLevels().computeIfPresent(tree, (k,v) -> {
                        return ++v;
                    });
                    ust.getSkillLevels().putIfAbsent(tree, 1);
                    ust.setSkillTreePoints(ust.getSkillTreePoints() - entry.getCost());
                }
                Display.refreshMenu();
                return UIEventResponse.processed();
            },
            entryList
        );
        for (Pair<Button, SkillEntry> pair : pairs) {
            Button button = pair.getFirst();
            button.handleComponentEvents(ComponentEventType.FOCUS_GIVEN, (e) -> {
                return handleHover(entity, entryContainer, pair);
            });
            button.handleMouseEvents(MouseEventType.MOUSE_ENTERED, (e, p) -> {
                return handleHover(entity, entryContainer, pair);
            });
        }
        this.screen.addComponent(entryContainer);
        if (entity instanceof UsesSkillTrees ust) {
            this.pointHeader = HeaderBuilder.newBuilder()
                .withText("Points: " + ust.getSkillTreePoints())
                .withPosition(Position.bottomLeftOf(entryContainer))
                .build();
            this.screen.addComponent(this.pointHeader);
        }
    }

    private UIEventResponse handleHover(Entity entity, Container entryContainer, Pair<Button, SkillEntry> pair) {
        if (entryBox == null) {
            generateSkillEntryBox(pair.getSecond(), entity);
            int xOffset = entryBox.getWidth()/2;
            entryContainer.moveBy(Position.create(xOffset*-1, 0));
            entryBox.moveTo(Position.topRightOf(entryContainer));
            this.screen.addComponent(entryBox);
        }
        populateEntryPanel(pair.getSecond(), entity);
        this.pointHeader.moveTo(Position.bottomLeftOf(entryContainer));
        return UIEventResponse.processed();
    }

    private void generateSkillEntryBox(SkillEntry entry, Entity entity){

        this.entryBox = VBoxBuilder.newBuilder()
            .withSize((int)(screen.getWidth()/2.5), (int)(screen.getHeight()/2.5))
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, entry.getName())
            )
            .withSpacing(1)
            .build();

    }

    private void populateEntryPanel(SkillEntry entry, Entity entity){

        this.entryBox.setTitle(entry.getName());

        Pair<List<String>,List<String>> checks = entry.checkRequirements(entity);
        List<String> fulfilled = checks.getFirst();
        List<String> unFulfilled = checks.getSecond();

        for (AttachedComponent comp : attachments) {
            comp.detach();
        }

        attachments.clear();

        Paragraph p = ParagraphBuilder.newBuilder()
            .withText(entry.getDescription())
            .withSize(
                entryBox.getWidth()-2,
                entryBox.getHeight() - ((fulfilled.size() + unFulfilled.size())*2)-2
            )
            .build();

        attachments.add(entryBox.addComponent(p));

        for (String string : fulfilled) {
            Header stringHeader = HeaderBuilder.newBuilder()
                .withText(string)
                .build();
            attachments.add(entryBox.addComponent(stringHeader));
        }

        for (String string : unFulfilled) {
            Label stringLabel = LabelBuilder.newBuilder()
                .withText(string)
                .build();
            attachments.add(entryBox.addComponent(stringLabel));
        }

    }

    @Override
    public Menu refresh() {
        return new SkillTreeMenu(tree, entity);
    }

}
