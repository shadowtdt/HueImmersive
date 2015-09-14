package hueimmersive;

import hueimmersive.interfaces.IBridge;
import hueimmersive.interfaces.ILink;
import hueimmersive.lights.HColorLight;
import hueimmersive.lights.HDimmableLight;
import hueimmersive.lights.HLight;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.JsonObject;


public final class HueBridge implements IBridge
{
	final int maxFindAttempts = 6;
	final int findPeriod = 1500;

	final int maxRegisterAttempts = 20;
	final int registerPeriod = 1500;

	public static String internalipaddress = Settings.Bridge.getInternalipaddress();

	public static final String username = "hueimmersiveuser";
	public static final String devicetype = "hueimmersive";
	
	public static ArrayList<HLight> lights = new ArrayList<HLight>();

	private final HueLink link = new HueLink();
	
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

	public ILink getLink()
	{
		return link;
	}

	public void register() throws Exception
	{
		Debug.info(null, "create new user...");

		Main.ui.setConnectState(3);

		JsonObject data = new JsonObject();
		data.addProperty("devicetype", devicetype);
		data.addProperty("username", username);

		final Timer timer = new Timer();
		TimerTask registerLoop = new TimerTask()
		{
			int attempt = 0;
			public void run()
			{
				try // to register a new bridge user (user must press the link button)
				{
					attempt++;
					JsonObject response = getLink().POST("http://" + internalipaddress + "/api/", data);
					if (getLink().getResponseType(response) == ILink.ResponseType.SUCCESS)
					{
						Debug.info(null, "new user created");

						timer.cancel();
						timer.purge();

						login();
					}
					else if (attempt > maxRegisterAttempts) // abort after serval attempt
					{
						Debug.info(null, "link button not pressed");

						timer.cancel();
						timer.purge();

						Main.ui.setConnectState(4);
					}
				}
				catch (Exception e)
				{
					Debug.exception(e);
				}
			}
		};
		timer.scheduleAtFixedRate(registerLoop, registerPeriod, registerPeriod);
	}

	public void login() throws Exception // try to login
	{
		JsonObject response = getLink().GET("http://" + internalipaddress + "/api/" + username);
		if (getLink().getResponseType(response) == ILink.ResponseType.DATA)
		{
			Debug.info(null, "login successfull");

			getLink().setBaseAPIurl("http://" + internalipaddress + "/api/" + username);

			debug();
			
			findLights();
			
			Main.ui.setConnectState(2);
		}
		else if (getLink().getResponseType(response) == ILink.ResponseType.ERROR)
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
		TimerTask findLoop = new TimerTask()
		{
			int attempt = 0;
			public void run()
			{
				try // to get the bridge ip
				{
					JsonObject response = getLink().GET("https://www.meethue.com/api/nupnp");

					if (response != null)
					{
						Debug.info(null, "bridge found");

						timer.cancel();
						timer.purge();

						internalipaddress = response.get("internalipaddress").getAsString();

						Settings.Bridge.setInternalipaddress(internalipaddress);

						login();
					}
				}
				catch (Exception e)
				{
					Debug.exception(e);
				}

				if (attempt > maxFindAttempts) // abort after serval attempts
				{
					try
					{
						Debug.info(null, "connection to bridge timeout");

						timer.cancel();
						timer.purge();

						Main.ui.setConnectState(4);
					}
					catch (Exception e)
					{
						Debug.exception(e);
					}
				}

				attempt++;
			}
		};
		timer.scheduleAtFixedRate(findLoop, 0, findPeriod);
	}

	public void connect() throws Exception
	{
		Debug.info(null, "try fast connect...");

		JsonObject response = getLink().GET("http://" + internalipaddress + "/api/" + username);

		if (getLink().getResponseType(response) == ILink.ResponseType.DATA)
		{
			Debug.info(null, "fast connect successfull");

			getLink().setBaseAPIurl("http://" + internalipaddress + "/api/" + username);

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
	
	public void findLights() throws Exception
	{
		Debug.info(null, "get lights...");

		JsonObject response = getLink().GET("/lights");
		
		for (int i = 1; i < 50; i++)
		{
			if (response.has(String.valueOf(i)))
			{
				JsonObject state = response.getAsJsonObject(String.valueOf(i)).getAsJsonObject("state");
				if (state.has("on") && state.has("hue") && state.has("sat") && state.has("bri"))
				{
					lights.add(new HColorLight(i, this));
				}
				else if (state.has("on") && state.has("bri"))
				{
					lights.add(new HDimmableLight(i, this));
				}
			}
		}

		Debug.info(null, lights.size() + " lights found");
	}

	public HLight getLight(int id)
	{
		for (HLight light : lights)
		{
			if(light.getID() == id)
			{
				return light;
			}
		}
		return null;
	}

	public void debug() throws Exception
	{
		JsonObject response = getLink().GET("/config");

		Debug.info("bridge infos",
				"name: " + response.get("name").getAsString(),
				"ipaddress: " + response.get("ipaddress").getAsString(),
				"timezone: " + response.get("timezone").getAsString(),
				"swversion: " + response.get("swversion").getAsString(),
				"apiversion: " + response.get("apiversion").getAsString());
	}
}
