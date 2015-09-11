package hueimmersive.interfaces;


public interface IColor
{
    void set(float h, float s, float b);

    void getTranslatetColor(boolean useGammaCorrection); // should return data
}
