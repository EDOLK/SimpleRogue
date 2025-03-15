package game.floorgeneration;

import java.util.List;

import game.gameobjects.Space;

interface RoomBlueprint{
    public List<Space> getRoomSpaces();
    public List<Space> getInteriorSpaces();
    public List<Space> getConnectorSpaces();
}
