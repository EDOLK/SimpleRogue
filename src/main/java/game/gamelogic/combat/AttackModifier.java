package game.gamelogic.combat;

@FunctionalInterface
public interface AttackModifier {
    public void modifyAttack(Attack attack);
}
