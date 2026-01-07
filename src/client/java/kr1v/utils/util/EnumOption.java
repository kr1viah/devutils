package kr1v.utils.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import kr1v.utils.interfaces.IValueBacked;

import java.util.function.Function;

public class EnumOption<T extends Enum<T>> implements IConfigOptionListEntry, IValueBacked<T> {
    private final T[] constants;
    private final Function<T, String> displayNameProvider;
    private T value;

    public EnumOption(Class<T> enumClass, T initial) {
        this(enumClass, initial, null);
    }

    public EnumOption(Class<T> enumClass, T initial, Function<T, String> displayNameProvider) {
        this.constants = enumClass.getEnumConstants();
        if (this.constants == null) throw new IllegalArgumentException("Not an enum class: " + enumClass);
        this.value = initial;
        this.displayNameProvider = displayNameProvider == null ? Enum::name : displayNameProvider;
    }

    @Override
    public String getStringValue() {
        return value.name();
    }

    @Override
    public String getDisplayName() {
        return displayNameProvider.apply(value);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int idx = -1;
        for (int i = 0; i < constants.length; i++) {
            if (constants[i].equals(value)) idx = i;
        }
        if (idx == -1) return this;
        int next = forward ? (idx + 1) % constants.length : (idx - 1 + constants.length) % constants.length;
        value = constants[next];
        return this;
    }

    @Override
    public IConfigOptionListEntry fromString(String valueStr) {
        for (T val : constants) {
            if (valueStr.compareToIgnoreCase(val.name()) == 0) {
                value = val;
                return this;
            }
        }
        return null;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
