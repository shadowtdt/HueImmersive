package hueimmersive;

import hueimmersive.interfaces.ILink;

import com.google.gson.JsonObject;

import java.net.HttpURLConnection;


public abstract class Link implements ILink
{
	private String baseAPIurl;

	public Link(String baseAPIurl)
	{
		this.baseAPIurl = baseAPIurl;
	}

	public final String getBaseAPIurl()
	{
		return baseAPIurl;
	}

	public final JsonObject GET(String APIurl)
	{
		throw new UnsupportedOperationException("GET");
	}

	public final JsonObject PUT(String APIurl, JsonObject data)
	{
		throw new UnsupportedOperationException("PUT");
	}

	public final JsonObject POST(String APIurl, JsonObject data)
	{
		throw new UnsupportedOperationException("POST");
	}

	public final JsonObject DELETE()
	{
		throw new UnsupportedOperationException("DELETE");
	}

	public final boolean canConnect(HttpURLConnection connection)
	{
		throw new UnsupportedOperationException("connectTest");
	}
}
