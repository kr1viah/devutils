package kr1v.utils.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import kr1v.utils.interfaces.IValueBacked;

import java.util.function.Function;

public class CycleOption<T> implements IConfigOptionListEntry, IValueBacked<T> {
    private final T[] options;
    private final Function<T, String> displayNameProvider;
    private T value;
    private int currentIndex;

    private CycleOption(T[] options) {
        this(null, null, options);
    }

    private CycleOption(Function<T, String> displayNameProvider, T[] options) {
        this(options.length > 0 ? options[0] : null, displayNameProvider, options);
    }

    private CycleOption(T initial, Function<T, String> displayNameProvider, T[] options) {
        if (options == null) throw new IllegalArgumentException("options must not be null");
        this.options = options;
        this.value = initial;
        this.displayNameProvider = displayNameProvider == null ? (t -> t == null ? "" : t.toString()) : displayNameProvider;
        if (this.value == null && this.options.length > 0) {
            this.value = this.options[0];
        }
    }

    @SafeVarargs
    public static <T> IConfigOptionListEntry of(T... options) {
        return new CycleOption<>(options);
    }

    @SafeVarargs
    public static <T> IConfigOptionListEntry of(T initial, Function<T, String>displayNameProvider, T... options) {
        return new CycleOption<>(initial, displayNameProvider, options);
    }

    @Override
    public String getStringValue() {
        return value == null ? "" : value.toString();
    }

    @Override
    public String getDisplayName() {
        return displayNameProvider.apply(value);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        if (forward) {
            currentIndex = (currentIndex + 1) % options.length;
        } else {
            currentIndex =  (currentIndex - 1 + options.length) % options.length;
        }
        value = options[currentIndex];
        return this;
    }

    @Override
    public IConfigOptionListEntry fromString(String valueStr) {
        if (valueStr == null) return null;
        for (T opt : options) {
            if (opt == null) {
                if (valueStr.isEmpty()) {
                    this.value = null;
                    return this;
                }
                continue;
            }
            String s = opt.toString();
            if (valueStr.equalsIgnoreCase(s)) {
                this.value = opt;
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
