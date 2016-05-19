package hueimmersive.interfaces;

import java.awt.*;


public interface ILight {
    boolean isOn() throws Exception;

    void setOn(boolean on) throws Exception;

    Color getColor() throws Exception;

    void setColor(Color color) throws Exception;

    void storeColor() throws Exception;

    void restoreColor() throws Exception;

    String getName();

    String getUniqueID();
}
