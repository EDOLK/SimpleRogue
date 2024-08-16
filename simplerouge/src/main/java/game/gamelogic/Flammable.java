package game.gamelogic;

import game.gameobjects.terrains.Fire;

public interface Flammable {
    public int getFuelValue();
    public void onBurn(Fire fire);
}
