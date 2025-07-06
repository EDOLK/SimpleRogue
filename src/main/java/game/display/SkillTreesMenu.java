package game.display;

import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;
import game.gameobjects.entities.Entity;

public class SkillTreesMenu extends Menu {
    public SkillTreesMenu(Entity entity) {
        super();
        Display.populateMenu(
            this,
            (l) -> {
                Display.setMenu(new SkillTreeMenu(l,entity));
                return UIEventResponse.processed();
            },
            "Classes",
            Dungeon.getAvailableSkillTrees()
        );
    }
}
