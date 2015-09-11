package hueimmersive;

import hueimmersive.interfaces.IBridge;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;


public class Control
{
	public static boolean immersiveProcessIsActive = false;
	private Timer captureLoop;
	private int transitionTime = 5;
	
	private double lastAutoSwitchBri;

	public static IBridge bridge;
	
	public Control() throws Exception
	{
		bridge = new HueBridge();

		if (Main.arguments.contains("force-on") && !Main.arguments.contains("force-off"))
		{
			turnAllLightsOn();
		}
		if (Main.arguments.contains("force-off") && !Main.arguments.contains("force-on"))
		{
			turnAllLightsOff();
		}
		if (Main.arguments.contains("force-start") && !Main.arguments.contains("force-off"))
		{
			startImmersiveProcess();
		}
	}
	
	public void setLight(HueLight light, Color color) throws Exception // calculate color and send it to light
	{		
		float[] colorHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null); // unmodified HSB color
		Color lightColor = Color.getHSBColor(colorHSB[0], Math.max(0f, Math.min(1f, colorHSB[1] * (Main.ui.slider_Saturation.getValue() / 100f))), (float)(colorHSB[2] * (Main.ui.slider_Brightness.getValue() / 100f) * (Settings.Light.getBrightness(light) / 100f))); // modified color
		
		double[] xy = HueColor.translate(lightColor, Settings.getBoolean("gammacorrection")); // xy color
		int bri = Math.round(Color.RGBtoHSB(lightColor.getRed(), lightColor.getGreen(), lightColor.getBlue(), null)[2] * 255); // brightness
		
		String APIurl = "http://" + HueBridge.internalipaddress + "/api/" + HueBridge.username + "/lights/" + light.id + "/state";
		String data = "{\"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri + ", \"transitiontime\":" + transitionTime + "}";
		
		// turn light off automatically if the brightness is very low
		double autoSwitchThreshold = Settings.getInteger("autoswitchthreshold") / 100.0;
		if (Settings.getBoolean("autoswitch"))
		{
			if (colorHSB[2] > Math.max((lastAutoSwitchBri + autoSwitchThreshold) * 1.25, autoSwitchThreshold * 1.02) && !light.isOn())
			{
				data = "{\"on\":true, \"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri + ", \"transitiontime\":" + Math.round(transitionTime * 0.45) + "}";
			}
			else if (colorHSB[2] < autoSwitchThreshold && light.isOn())
			{
				data = "{\"on\":false, \"transitiontime\":" + Math.round(transitionTime * 0.45) + "}";
				lastAutoSwitchBri = colorHSB[2] - autoSwitchThreshold;
			}
		}
		else if (!Settings.getBoolean("autoswitch") && !light.isOn())
		{
			data = "{\"on\":true, \"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri + ", \"transitiontime\":" + Math.round(transitionTime * 0.45) + "}";
		}
		
		Request.PUT(APIurl, data);
	}
	
	public void startImmersiveProcess() throws Exception
	{
		Main.ui.button_Off.setEnabled(false);
		Main.ui.button_On.setEnabled(false);
		
		Main.ui.button_Stop.setEnabled(true);
		Main.ui.button_Start.setEnabled(false);
		Main.ui.button_Once.setEnabled(false);
		
		lastAutoSwitchBri = 0.0;
		
		for(HueLight light : HueBridge.lights)
		{
			light.storeLightColor();
		}
		
		// create a loop to execute the immersive process
		captureLoop = new Timer();
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				try
				{
					ImmersiveProcess.execute();
				}
				catch (Exception e)
				{
					Debug.exception(e);
				}
			}
		};
		
		immersiveProcessIsActive = true;
		captureLoop.scheduleAtFixedRate(task, 0, Math.round(transitionTime * 100 * 0.68));
	}
	
	public void stopImmersiveProcess() throws Exception
	{
		captureLoop.cancel();
		captureLoop.purge();
		
		immersiveProcessIsActive = false;
		
		Main.ui.setupOnOffButton();
		
		Main.ui.button_Stop.setEnabled(false);
		Main.ui.button_Start.setEnabled(true);
		Main.ui.button_Once.setEnabled(true);
		
		Thread.sleep(250);
		ImmersiveProcess.setStandbyOutput();
		
		if (Settings.getBoolean("restorelight"))
		{
			Thread.sleep(750);
			for(HueLight light : HueBridge.lights)
			{
				light.restoreLightColor();
			}
		}
	}
	
	public void onceImmersiveProcess() throws Exception
	{
		Main.ui.button_Stop.setEnabled(false);
		Main.ui.button_Start.setEnabled(true);
		Main.ui.button_Once.setEnabled(true);
		
		ImmersiveProcess.execute();
	}
	
	public void turnAllLightsOn() throws Exception
	{
		for(HueLight light : HueBridge.lights)
		{
			if (Settings.Light.getActive(light))
			{
				light.turnOn();
			}
		}
		Main.ui.setupOnOffButton();
	}

	public void turnAllLightsOff() throws Exception
	{
		for(HueLight light : HueBridge.lights)
		{
			if (Settings.Light.getActive(light))
			{
				light.turnOff();
			}
		}
		Main.ui.setupOnOffButton();
	}

}
