package hueimmersive.interfaces;


public interface ILight
{
    boolean isOn() throws Exception;

    void setOn(boolean on);
    void turnOn();
    void turnOff();

    void setColor(IColor color);
    IColor getColor();

    void storeColor();
    void restoreColor();

    String getName();
    String getUniqueID();
}
