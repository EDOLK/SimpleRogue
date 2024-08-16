package game.floorgeneration;

import java.lang.reflect.Constructor;

public final class Factory{

    public static <T> T create(Class<T> clazz, Object... args){
        for (Constructor<?> constructor : clazz.getConstructors()) {
            try {
                return clazz.cast(constructor.newInstance(args));
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> castWildcard(Class<? extends T> wildcardClass){
        return (Class<T>)wildcardClass;
    }

}
