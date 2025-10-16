package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

public class SkeletonCorpse extends Item {

    public SkeletonCorpse(Corpse corpse){
        setCharacter(corpse.getCharacter());
        setFgColor(TileColor.create(150, 150, 150, 255));
        setBgColor(TileColor.transparent());
        setName("Skeleton of " + corpse.getOriginalEntityName());
        setDescription("The skeleton of a " + corpse.getOriginalEntityName() + ".");
        setTileName("Generic Corpse");
        setWeight(corpse.getWeight()/4);
    }
}
