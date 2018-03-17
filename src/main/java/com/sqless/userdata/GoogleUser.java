package com.sqless.userdata;

public class GoogleUser {
    private String id;
    private String nombre;
    private String email;

    public GoogleUser(String id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "USUARIO DE GOOGLE: " + id + " - " + nombre + " - " + email;
    }
}
