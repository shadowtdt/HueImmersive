package hueimmersive;

import hueimmersive.interfaces.ILink;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public abstract class Link implements ILink
{
	private final String baseAPIurl;

	public Link(String baseAPIurl)
	{
		this.baseAPIurl = baseAPIurl;
	}

	public final String getBaseAPIurl()
	{
		return baseAPIurl;
	}

	public final JsonObject GET(String APIurl) throws Exception
	{
		URL url = new URL(getBaseAPIurl() + APIurl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		String input = null;

		if (canConnect(connection))
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			input = reader.readLine();
			reader.close();

			return extractJsonObject(input);
		}
		else
		{
			return null;
		}
	}

	public final JsonObject PUT(String APIurl, JsonObject data) throws Exception
	{
		URL url = new URL(getBaseAPIurl() + APIurl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);

		String input = null;

		if (canConnect(connection))
		{
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(data.toString());
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			input = reader.readLine();
			reader.close();
		}

		return extractJsonObject(input);
	}

	public final JsonObject POST(String APIurl, JsonObject data) throws Exception
	{
		URL url = new URL(getBaseAPIurl() + APIurl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		String input = null;

		if (canConnect(connection))
		{
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(data.toString());
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			input = reader.readLine();
			reader.close();
		}

		return extractJsonObject(input);
	}

	public final JsonObject DELETE() throws Exception
	{
		throw new UnsupportedOperationException("DELETE");
	}

	public final boolean canConnect(HttpURLConnection connection)
	{
		try
		{
			connection.setConnectTimeout(400);
			connection.connect();

			return true;
		}
		catch (Exception e)
		{
			Debug.exception(e);

			return false;
		}
	}
}
