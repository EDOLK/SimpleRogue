package game.gameobjects.items.armor;

public enum ArmorType {
    HEAD,CHEST_OUTER,CHEST_INNER,HANDS,LEGS,FEET,;

    @Override
    public String toString(){
        String string = this.name().toLowerCase();
        char[] characters = string.toCharArray();
        characters[0]=Character.toUpperCase(characters[0]);
        string = String.valueOf(characters);
        string = string.replace('_', ' ');
        return string;
    }
}
