package com.github.zakru.advancednotifications;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class NotificationAdapter extends TypeAdapter<Notification>
{
	private final AdvancedNotificationsPlugin plugin;
	private final Gson gson;

	public NotificationAdapter(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
		gson = new GsonBuilder()
			.registerTypeAdapter(Notification.class, this)
			.registerTypeAdapter(InventoryComparator.Pointer.class, new ComparatorAdapter())
			.create();
	}

	@Override
	public void write(JsonWriter out, Notification o) throws IOException
	{
		JsonObject jo = outTyped(out, o).getAsJsonObject();
		jo.addProperty("type", idOf(o));
		gson.toJson(jo, out);
	}

	@Override
	public Notification read(JsonReader in) throws IOException
	{
		JsonObject jo = gson.fromJson(in, JsonObject.class);
		int notificationType = jo.get("type").getAsInt();
		jo.remove("type");

		Notification notification = ofType(jo, notificationType);

		notification.setPlugin(plugin);
		return notification;
	}

	private Notification ofType(JsonElement in, int type) throws IOException
	{
		switch (type)
		{
			case 0:
				return gson.fromJson(in, ItemNotification.class);
			case 1:
				return gson.fromJson(in, EmptyNotification.class);
			case 2:
				return gson.fromJson(in, NotificationGroup.class);
			default:
				return null;
		}
	}

	private JsonElement outTyped(JsonWriter out, Notification o) throws IOException
	{
		if (o instanceof ItemNotification) return gson.toJsonTree(o, ItemNotification.class);
		else if (o instanceof EmptyNotification) return gson.toJsonTree(o, EmptyNotification.class);
		else if (o instanceof NotificationGroup) return gson.toJsonTree(o, NotificationGroup.class);
		throw new RuntimeException("Unexpected notification type " + o.getClass());
	}

	private int idOf(Notification o)
	{
		if (o instanceof ItemNotification) return 0;
		if (o instanceof EmptyNotification) return 1;
		if (o instanceof NotificationGroup) return 2;
		return -1;
	}
}
