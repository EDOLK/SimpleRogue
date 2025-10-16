package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

public class BurntCorpse extends Item {

    public BurntCorpse(Corpse corpse){
        setCharacter(corpse.getCharacter());
        setFgColor(TileColor.create(122, 20, 20, 255));
        setBgColor(TileColor.transparent());
        setName("Burnt Corpse of " + corpse.getOriginalEntityName());
        setDescription("The Burnt Corpse of a " + corpse.getOriginalEntityName() + ".");
        setTileName("Generic Corpse");
        setWeight(corpse.getWeight());
    }

}
