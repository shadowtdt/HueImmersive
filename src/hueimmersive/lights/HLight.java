package hueimmersive.lights;

import hueimmersive.interfaces.ILight;


public abstract class HLight implements ILight
{
	public HLight(int id)
	{
		throw new UnsupportedOperationException("HLight");
	}

	public final boolean isOn() throws Exception
	{
		throw new UnsupportedOperationException("isOn");
	}

	public final void setOn(boolean on)
	{
		throw new UnsupportedOperationException("setOn");
	}

	public final void turnOn()
	{
		throw new UnsupportedOperationException("turnOn");
	}

	public final void turnOff()
	{
		throw new UnsupportedOperationException("turnOff");
	}

	public final String getName()
	{
		throw new UnsupportedOperationException("getName");
	}

	public final String getUniqueID()
	{
		throw new UnsupportedOperationException("getUniqueID");
	}

	public final int getID()
	{
		throw new UnsupportedOperationException("getUniqueID");
	}
}
