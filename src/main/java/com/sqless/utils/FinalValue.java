package com.sqless.utils;

/**
 * Una clase "contenedora" de valores. Especialmente útil para cuando uno quiere
 * setear un valor dentro de una clase anónima y no puede por la limitación de
 * que toda variable referenciada dentro de una clase anónima debe ser final.
 *
 * @author Morgan
 * @param <T>
 */
public class FinalValue<T> {

    private T value;

    public void set(T t) {
        this.value = t;
    }

    public T get() {
        return value;
    }
}
