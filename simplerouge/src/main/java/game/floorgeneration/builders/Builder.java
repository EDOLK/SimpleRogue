package game.floorgeneration.builders;

import game.floorgeneration.Factory;

public class Builder<T>{
    
    protected T t;

    public Builder(T t){
        this.t = t;
    }
    
    public Builder(Class<? extends T> tClass, Object... args){
        this.t = Factory.create(Factory.castWildcard(tClass), args);
    }

    public T build(){
        return this.t;
    }

}
