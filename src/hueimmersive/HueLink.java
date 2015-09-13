package hueimmersive;

import com.google.gson.JsonObject;


public final class HueLink extends Link
{
	public HueLink(String baseAPIurl)
	{
		super(baseAPIurl);
	}

	public ResponseType getResponseType(JsonObject response)
	{
		throw new UnsupportedOperationException("getResponseType");
	}

	public JsonObject extractJsonObject(String response)
	{
		throw new UnsupportedOperationException("extractJObject");
	}
}
