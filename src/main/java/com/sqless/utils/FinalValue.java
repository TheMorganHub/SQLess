package com.sqless.utils;

/**
 * Una clase "contenedora" de valores. Especialmente útil para cuando uno quiere
 * setear un valor dentro de una clase anónima y no puede por la limitación de
 * que toda variable referenciada dentro de una clase anónima debe ser final.
 *
 * @author Morgan
 * @param <V>
 */
public class FinalValue<V> {

    private V value;

    public FinalValue() {
    }

    public FinalValue(V value) {
        this.value = value;
    }

    public void set(V v) {
        this.value = v;
    }

    public V get() {
        return value;
    }
}
