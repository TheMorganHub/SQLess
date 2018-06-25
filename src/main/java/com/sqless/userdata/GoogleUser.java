package com.sqless.userdata;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GoogleUser {

    private String id;
    private String nombre;
    private String email;
    private ImageIcon profilePicture;

    public GoogleUser(String id, String nombre, String email, String imgUrl) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.profilePicture = getImageFromUrl(imgUrl);
    }

    private ImageIcon getImageFromUrl(String imgUrl) {
        ImageIcon image = null;
        try {
            URL url = new URL(imgUrl);
            BufferedImage c = ImageIO.read(url);
            Image scaled = c.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
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

    public ImageIcon getProfilePicture() {
        return profilePicture;
    }

    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}
