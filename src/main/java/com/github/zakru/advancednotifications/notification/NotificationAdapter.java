package com.github.zakru.advancednotifications.notification;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.condition.CheckerAdapter;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.condition.ConditionAdapter;
import com.github.zakru.advancednotifications.condition.InventoryChecker;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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
			.registerTypeAdapter(Condition.class, new ConditionAdapter(plugin))
			.create();
	}

	@Override
	public void write(JsonWriter out, Notification o) throws IOException
	{
		JsonObject jo = gson.toJsonTree(o).getAsJsonObject();
		jo.addProperty("type", idOf(o));
		gson.toJson(jo, out);
	}

	@Override
	public Notification read(JsonReader in) throws IOException
	{
		JsonObject jo = gson.fromJson(in, JsonObject.class);
		String notificationType = jo.get("type").getAsString();
		jo.remove("type");

		Notification notification = ofType(jo, notificationType);

		notification.setPlugin(plugin);
		return notification;
	}

	private Notification ofType(JsonElement in, String type) throws IOException
	{
		NotificationType<?> t = NotificationTypes.REGISTRY.entrySet().stream().filter(e -> e.getKey().equals(type)).map(Map.Entry::getValue).findAny().orElse(null);
		if (t != null) return gson.fromJson(in, t.getTargetClass());
		throw new RuntimeException("Unknown notification type " + type);
	}

	private String idOf(Notification o)
	{
		Optional<String> id = NotificationTypes.REGISTRY.entrySet().stream().filter(e -> e.getValue().getTargetClass() == o.getClass()).map(Map.Entry::getKey).findAny();
		if (id.isPresent()) return id.get();

		throw new RuntimeException("Unknown notification class " + o.getClass());
	}
}
