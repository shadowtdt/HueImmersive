package hueimmersive.lights;

import com.google.gson.JsonObject;
import hueimmersive.HueBridge;
import hueimmersive.Settings;
import hueimmersive.interfaces.ILight;


public abstract class HLight implements ILight {
    protected final HueBridge bridge;

    protected final int id;
    protected final String name;
    protected final String uniqueid;

    public HLight(int id, HueBridge bridge) throws Exception
    {
        this.id = id;
        this.bridge = bridge;

        JsonObject response = bridge.getLink().GET("/lights/" + id);

        this.name = response.get("name").getAsString();
        this.uniqueid = response.get("uniqueid").getAsString();

        Settings.Light.check(this);
    }

    public final boolean isOn() throws Exception
    {
        JsonObject response = bridge.getLink().GET("/lights/" + id);

        return response.get("state").getAsJsonObject().get("on").getAsBoolean();
    }

    public final void setOn(boolean on) throws Exception
    {
        JsonObject data = new JsonObject();
        data.addProperty("on", on);

        bridge.getLink().PUT("/lights/" + id + "/state/", data);
    }

    public final String getName()
    {
        return name;
    }

    public final String getUniqueID()
    {
        return uniqueid;
    }

    public final int getID()
    {
        return id;
    }
}
