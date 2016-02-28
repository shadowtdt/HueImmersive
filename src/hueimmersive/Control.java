package hueimmersive;

import hueimmersive.interfaces.IBridge;
import hueimmersive.interfaces.ILight;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;


public class Control
{
	public static boolean immersiveProcessIsActive = false;
	private Timer captureLoop;
	
	private double lastAutoSwitchBri;

	public static IBridge bridge;
	
	public Control() throws Exception
	{
		bridge = new HueBridge();

		Main.ui.loadMainInterface();

		if (Main.arguments.contains("force-on") && !Main.arguments.contains("force-off"))
		{
			setAllActiveLightsOn(true);
		}
		if (Main.arguments.contains("force-off") && !Main.arguments.contains("force-on"))
		{
			setAllActiveLightsOn(false);
		}
		if (Main.arguments.contains("force-start") && !Main.arguments.contains("force-off"))
		{
			startImmersiveProcess();
		}
	}
	
	public void setLight(ILight light, Color color) throws Exception // calculate color and send it to light
	{		
		float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null); // unmodified HSB color
		color = Color.getHSBColor(hsbColor[0], Math.max(0f, Math.min(1f, hsbColor[1] * (Main.ui.slider_Saturation.getValue() / 100f))), (float)(hsbColor[2] * (Main.ui.slider_Brightness.getValue() / 100f) * (Settings.Light.getBrightness(light) / 100f))); // modified color
		
		light.setColor(color);

		/*
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
		*/
	}
	
	public void startImmersiveProcess() throws Exception
	{
		Main.ui.button_Off.setEnabled(false);
		Main.ui.button_On.setEnabled(false);
		
		Main.ui.button_Stop.setEnabled(true);
		Main.ui.button_Start.setEnabled(false);
		Main.ui.button_Once.setEnabled(false);
		
		lastAutoSwitchBri = 0.0;
		
		for(ILight light : bridge.getLights())
		{
			light.storeColor();
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
		captureLoop.scheduleAtFixedRate(task, 0, Math.round(Settings.getInteger("refreshdelay")));
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
			for(ILight light : bridge.getLights())
			{
				light.restoreColor();
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
	
	public void setAllActiveLightsOn(boolean on) throws Exception
	{
		for(ILight light : bridge.getLights())
		{
			if (Settings.Light.getActive(light))
			{
				light.setOn(on);
			}
		}
		Main.ui.setupOnOffButton();
	}
}
