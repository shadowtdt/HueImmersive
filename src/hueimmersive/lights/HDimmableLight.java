package hueimmersive.lights;

import com.google.gson.JsonObject;
import hueimmersive.HueBridge;

import java.awt.*;


public class HDimmableLight extends HLight {
    private int[] storedColor = new int[1];

    public HDimmableLight(int id, HueBridge bridge) throws Exception
    {
        super(id, bridge);
    }

    public Color getColor() throws Exception
    {
        JsonObject response = bridge.getLink().GET("/lights/" + id);

        float bri = response.get("state").getAsJsonObject().get("bri").getAsFloat() / 255.0f;

        return Color.getHSBColor(0, 0, bri);
    }

    public void setColor(Color color) throws Exception
    {
        float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        int bri = Math.round(hsbColor[2] * 255);

        JsonObject data = new JsonObject();
        data.addProperty("bri", bri);

        bridge.getLink().PUT("/lights/" + id + "/state/", data);
    }

    public void storeColor() throws Exception
    {
        JsonObject response = bridge.getLink().GET("/lights/" + id);

        storedColor[0] = response.get("state").getAsJsonObject().get("bri").getAsInt();
    }

    public void restoreColor() throws Exception
    {
        JsonObject data = new JsonObject();
        data.addProperty("bri", storedColor[0]);
        data.addProperty("transitiontime", 1);

        bridge.getLink().PUT("/lights/" + id + "/state/", data);
    }
}
