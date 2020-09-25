package xianxian.mc.starocean;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class EnumMatcher {
    @Nullable
    public static <T extends Enum<T>> T matchIgnoreCase(Class<T> enumClass, String name) {
        try {
            Method valuesMethod = enumClass.getDeclaredMethod("values");
            T[] values = (T[]) valuesMethod.invoke(null);
            for (int i = 0; i < values.length; i++) {
                T value = values[i];
                if (name.equalsIgnoreCase(value.name()))
                    return value;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T extends Enum<T>> T matchIgnoreCase(Supplier<T[]> valueSupplier, String name) {
        T[] values = valueSupplier.get();
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            if (name.equalsIgnoreCase(value.name()))
                return value;
        }
        return null;
    }
}
