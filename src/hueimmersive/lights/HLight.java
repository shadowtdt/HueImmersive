package hueimmersive.lights;

import hueimmersive.interfaces.ILight;


public abstract class HLight implements ILight
{
	public HLight(int id)
	{
		throw new UnsupportedOperationException("HLight");
	}

	public boolean isOn() throws Exception
	{
		throw new UnsupportedOperationException("isOn");
	}

	public void setOn(boolean on)
	{
		throw new UnsupportedOperationException("setOn");
	}

	public void turnOn()
	{
		throw new UnsupportedOperationException("turnOn");
	}

	public void turnOff()
	{
		throw new UnsupportedOperationException("turnOff");
	}

	public String getName()
	{
		throw new UnsupportedOperationException("getName");
	}

	public String getUniqueID()
	{
		throw new UnsupportedOperationException("getUniqueID");
	}
}
