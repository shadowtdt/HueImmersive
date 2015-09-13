package hueimmersive;

import hueimmersive.interfaces.IBridge;
import hueimmersive.interfaces.ILink;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.*;


public class HueBridge implements IBridge
{
	public static String internalipaddress = Settings.Bridge.getInternalipaddress();

	public static final String username = "hueimmersiveuser";
	public static final String devicetype = "hueimmersive";
	
	public static ArrayList<HueLight> lights = new ArrayList<HueLight>();
	
	public HueBridge() throws Exception
	{
		if (internalipaddress != null)
		{
			connect();
		}
		else
		{
			find();
		}
	}
	
	public HueLight getLight(int lightID)
	{
		for (HueLight light : lights)
		{
			if(light.id == lightID)
			{
				return light;
			}
		}
		return null;
	}

	public ILink getLink()
	{
		throw new UnsupportedOperationException("getLink");
	}

	public void register() throws Exception
	{
		Debug.info(null, "create new user...");
		Main.ui.setConnectState(3);

		final Timer timer = new Timer();
		TimerTask addUserLoop = new TimerTask()
		{
			String body = "{\"devicetype\": \"" + devicetype + "\", \"username\": \"" + username + "\"}";
			int tries = 0;
			public void run()
			{
				try // to register a new bridge user (user must press the link button)
				{
					tries++;
					JsonObject response = Request.POST("http://" + internalipaddress + "/api/", body);
					if (Request.responseCheck(response) == "success")
					{
						timer.cancel();
						timer.purge();
						Debug.info(null, "new user created");
						login();
					}
					else if (tries > 20) // abort after serval tries
					{
						timer.cancel();
						timer.purge();
						Main.ui.setConnectState(4);
						Debug.info(null, "link button not pressed");
					}
				}
				catch (Exception e)
				{
					Debug.exception(e);
				}
			}
		};
		timer.scheduleAtFixedRate(addUserLoop, 1500, 1500);
	}

	public void login() throws Exception // try to login
	{
		JsonObject response = Request.GET("http://" + internalipaddress + "/api/" + username);
		if (Request.responseCheck(response) == "data")
		{
			Debug.info(null, "login successfull");
			
			debug();
			
			findLights();
			
			Main.ui.setConnectState(2);
		}
		else if (Request.responseCheck(response) == "error")
		{
			register();
		}
	}

	public void find() throws Exception
	{
		Debug.info(null, "setup new connection...");

		Main.ui.loadConnectionInterface();
		Main.ui.setConnectState(1);

		final Timer timer = new Timer();
		TimerTask addUserLoop = new TimerTask()
		{
			int tries = 0;
			public void run()
			{
				try // to get the bridge ip
				{
					JsonObject response = Request.GET("https://www.meethue.com/api/nupnp");

					if (response != null)
					{
						timer.cancel();
						timer.purge();

						internalipaddress = response.get("internalipaddress").getAsString();

						Settings.Bridge.setInternalipaddress(internalipaddress);

						Debug.info(null, "bridge found");

						login();
					}
				}
				catch (Exception e)
				{
					Debug.exception(e);
				}

				if (tries > 6) // abort after serval tries
				{
					try
					{
						timer.cancel();
						timer.purge();
						Main.ui.setConnectState(4);
						Debug.info(null, "connection to bridge timeout");
					}
					catch (Exception e)
					{
						Debug.exception(e);
					}
				}

				tries++;
			}
		};
		timer.scheduleAtFixedRate(addUserLoop, 0, 1500);
	}

	public void connect() throws Exception
	{
		Debug.info(null, "try fast connect...");

		JsonObject response = Request.GET("http://" + internalipaddress + "/api/" + username);

		if (Request.responseCheck(response) == "data")
		{
			Debug.info(null, "fast connect successfull");

			debug();

			findLights();
			Main.ui.loadMainInterface();
		}
		else
		{
			Debug.info(null, "can't find bridge");

			find();
		}
	}

	public void debug() throws Exception
	{
		JsonObject response = Request.GET("http://" + internalipaddress + "/api/" + username + "/config/");
		
		Debug.info("bridge infos", 
				"name: " + response.get("name").getAsString(), 
				"ipaddress: " + response.get("ipaddress").getAsString(), 
				"timezone: " + response.get("timezone").getAsString(),
				"swversion: " + response.get("swversion").getAsString(),
				"apiversion: " + response.get("apiversion").getAsString());
	}
	
	public void findLights() throws Exception
	{
		Debug.info(null, "get lights...");
		JsonObject response = Request.GET("http://" + internalipaddress + "/api/" + username + "/lights/");
		
		for (int i = 1; i < 50; i++)
		{
			if (response.has(String.valueOf(i)))
			{
				JsonObject state = response.getAsJsonObject(String.valueOf(i)).getAsJsonObject("state");
				if (state.has("on") && state.has("hue") && state.has("sat") && state.has("bri"))
				{
					lights.add(new HueLight(i));
				}
			}
		}

		Debug.info(null, lights.size() + " lights found");
	}
}
