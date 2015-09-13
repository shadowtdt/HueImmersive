package hueimmersive;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public final class HueLink extends Link
{
	public HueLink(String baseAPIurl)
	{
		super(baseAPIurl);
	}

	public ResponseType getResponseType(JsonObject response)
	{
		ResponseType result;

		if(response == null)
		{
			result = ResponseType.NULL;
			//Debug.info("NULL");
		}
		else if (response.has("success"))
		{
			result = ResponseType.SUCCESS;
			//Debug.info("SUCCESS");
		}
		else if(response.has("error"))
		{
			result = ResponseType.ERROR;
			//Debug.info("ERROR");
		}
		else
		{
			result = ResponseType.DATA;
			//Debug.info("DATA");
		}

		return result;
	}

	public JsonObject extractJsonObject(String response)
	{
		JsonElement element = new JsonParser().parse(response);

		JsonObject object = new JsonObject();

		if(element.isJsonArray())
		{
			object = element.getAsJsonArray().get(0).getAsJsonObject();
		}
		else if(element.isJsonObject())
		{
			object = element.getAsJsonObject();
		}

		return object;
	}
}
