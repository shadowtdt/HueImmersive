package hueimmersive.interfaces;

import java.awt.Color;


public interface ILight
{
    boolean isOn() throws Exception;
    void setOn(boolean on);

    void turnOn();
    void turnOff();

    void setColor(Color color);
    Color getColor();

    void storeColor();
    void restoreColor();

    String getName();
    String getUniqueID();
}
