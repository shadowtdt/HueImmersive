package hueimmersive.interfaces;

import java.awt.Color;


public interface ILight
{
    boolean isOn() throws Exception;
    void setOn(boolean on) throws Exception;

    void setColor(Color color) throws Exception;
    Color getColor() throws Exception;

    void storeColor() throws Exception;
    void restoreColor() throws Exception;

    String getName();
    String getUniqueID();
}
