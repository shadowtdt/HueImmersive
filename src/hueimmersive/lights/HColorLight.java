package hueimmersive.lights;

import java.awt.Color;


public class HColorLight extends HLight
{
	public HColorLight(int id)
	{
		super(id);
	}

	public void setColor(Color color)
	{
		throw new UnsupportedOperationException("setColor");
	}

	public Color getColor()
	{
		throw new UnsupportedOperationException("getColor");
	}

	public void storeColor()
	{
		throw new UnsupportedOperationException("storeColor");
	}

	public void restoreColor()
	{
		throw new UnsupportedOperationException("restoreColor");
	}
}
