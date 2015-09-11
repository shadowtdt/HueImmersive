package hueimmersive;

import com.google.gson.JsonObject;


public class HueLight
{
	public final int id;
	public final String name;
	public final String uniqueid;
	private int[] storedLightColor = new int[3];
	
	public HueLight(int LightID) throws Exception
	{
		id = LightID;
		
		JsonObject response = Request.GET("http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id);
		name = response.get("name").getAsString();
		uniqueid = response.get("uniqueid").getAsString();
		
		Settings.Light.check(this);
	}
	
	public boolean isOn() throws Exception
	{
		JsonObject response = Request.GET("http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id);
		
		return response.get("state").getAsJsonObject().get("on").getAsBoolean();
	}
	
	public void turnOn() throws Exception
	{
		String APIurl = "http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id + "/state/";
		String data = "{\"on\": true}";
		
		Request.PUT(APIurl, data);
	}
	
	public void turnOff() throws Exception
	{
		String APIurl = "http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id + "/state/";
		String data = "{\"on\": false}";
		
		Request.PUT(APIurl, data);
	}
	
	public void storeLightColor() throws Exception
	{
		JsonObject response = Request.GET("http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id);

		storedLightColor[0] = response.get("state").getAsJsonObject().get("hue").getAsInt();
		storedLightColor[1] = response.get("state").getAsJsonObject().get("sat").getAsInt();
		storedLightColor[2] = response.get("state").getAsJsonObject().get("bri").getAsInt();
	}
	
	public void restoreLightColor() throws Exception
	{
		String APIurl = "http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + id + "/state/";
		String data = "{\"hue\":" + storedLightColor[0] + ", \"sat\":" + storedLightColor[1] + ", \"bri\":" + storedLightColor[2] + ", \"transitiontime\":1}";
		
		Request.PUT(APIurl, data);
	}
}
