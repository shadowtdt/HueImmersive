package hueimmersive.interfaces;

import com.google.gson.JsonObject;

import java.net.HttpURLConnection;


public interface ILink
{
	enum ResponseType
	{
		DATA,
		SUCCESS,
		ERROR
	}

	String getBaseAPIurl();

	JsonObject GET(String APIurl);

	JsonObject PUT(String APIurl, JsonObject data);

	JsonObject POST(String APIurl, JsonObject data);

	JsonObject DELETE();

	boolean canConnect(HttpURLConnection connection);

	JsonObject extractJsonObject(String response);

	ResponseType getResponseType(JsonObject response);
}
