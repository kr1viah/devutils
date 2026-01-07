package kr1v.utils.interfaces;

public interface IValueBacked<T> {
    T getValue();
    void setValue(T value);
}
