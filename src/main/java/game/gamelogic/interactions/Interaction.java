package game.gamelogic.interactions;

import java.util.function.Function;
import java.util.function.Predicate;

import game.gamelogic.HasName;
import game.gameobjects.entities.Entity;

public interface Interaction extends HasName{

    public InteractionResult doInteract(Entity interactor);

    default boolean isDisabled(Entity interactor){
        return false;
    }

    default boolean isAvailable(Entity interactor){
        return true;
    }

    public class Builder{

        public Function<Entity, InteractionResult> onInteract = (entity) -> InteractionResult.create();
        public Predicate<Entity> isDisabled = (entity) -> false;
        public Predicate<Entity> isAvailable = (entity) -> true;
        public String name = "";

        public Builder(){}

        public Builder withOnInteract(Function<Entity, InteractionResult> onInteract){
            this.onInteract = onInteract;
            return this;
        }

        public Builder withIsDisabled(Predicate<Entity> isDisabled){
            this.isDisabled = isDisabled;
            return this;
        }

        public Builder withIsAvailable(Predicate<Entity> isAvailable){
            this.isAvailable = isAvailable;
            return this;
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Interaction build(){
            return new Interaction(){
                @Override
                public String getName() {
                    return Builder.this.name;
                }

                @Override
                public InteractionResult doInteract(Entity interactor) {
                    return Builder.this.onInteract.apply(interactor);
                }

                @Override 
                public boolean isDisabled(Entity interactor) {
                    return Builder.this.isDisabled.test(interactor);
                }

                @Override 
                public boolean isAvailable(Entity interactor) {
                    return Builder.this.isAvailable.test(interactor);
                }
            };
        }
    }
}
