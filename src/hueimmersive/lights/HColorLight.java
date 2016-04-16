package hueimmersive.lights;

import hueimmersive.HueBridge;
import hueimmersive.HueColor;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.awt.Color;


public class HColorLight extends HLight
{
	private int[] storedColor = new int[3];

	public HColorLight(int id, HueBridge bridge) throws Exception
	{
		super(id, bridge);
	}

	public void setColor(Color color) throws Exception
	{
		float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		double[] xyColor = HueColor.translate(color, true);

		int bri = Math.round(hsbColor[2] * 255); // brightness

		JsonArray xy = new JsonArray();
		xy.add(new JsonPrimitive(xyColor[0]));
		xy.add(new JsonPrimitive(xyColor[1]));

		JsonObject data = new JsonObject();
		data.add("xy", xy);
		data.addProperty("bri", bri);

		bridge.getLink().PUT("/lights/" + id + "/state/", data);
	}

	public Color getColor() throws Exception
	{
		throw new UnsupportedOperationException("getColor");
	}

	public void storeColor() throws Exception
	{
		JsonObject response = bridge.getLink().GET("/lights/" + id);

		storedColor[0] = response.get("state").getAsJsonObject().get("hue").getAsInt();
		storedColor[1] = response.get("state").getAsJsonObject().get("sat").getAsInt();
		storedColor[2] = response.get("state").getAsJsonObject().get("bri").getAsInt();
	}

	public void restoreColor() throws Exception
	{
		JsonObject data = new JsonObject();
		data.addProperty("hue", storedColor[0]);
		data.addProperty("sat", storedColor[1]);
		data.addProperty("bri", storedColor[2]);
		data.addProperty("transitiontime", 1);

		bridge.getLink().PUT("/lights/" + id + "/state/", data);
	}
}
