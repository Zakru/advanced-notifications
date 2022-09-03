package com.github.zakru.advancednotifications.condition;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.notification.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ConditionAdapter extends TypeAdapter<Condition>
{
	private final AdvancedNotificationsPlugin plugin;
	private final Gson gson;

	public ConditionAdapter(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
		gson = new GsonBuilder()
			.registerTypeAdapter(Condition.class, this)
			.registerTypeAdapter(InventoryChecker.Pointer.class, new CheckerAdapter())
			.create();
	}

	@Override
	public void write(JsonWriter out, Condition o) throws IOException
	{
		if (o != null) {
			JsonObject jo = gson.toJsonTree(o).getAsJsonObject();
			jo.addProperty("type", idOf(o));
			gson.toJson(jo, out);
		} else {
			out.nullValue();
		}
	}

	@Override
	public Condition read(JsonReader in) throws IOException
	{
		JsonObject jo = gson.fromJson(in, JsonObject.class);
		String conditionType = jo.get("type").getAsString();
		jo.remove("type");

		Condition condition = ofType(jo, conditionType);

		condition.setPlugin(plugin);
		return condition;
	}

	private Condition ofType(JsonElement in, String type) throws IOException
	{
		ConditionType<?> t = ConditionTypes.REGISTRY.entrySet().stream().filter(e -> e.getKey().equals(type)).map(Map.Entry::getValue).findAny().orElse(null);
		if (t != null) return gson.fromJson(in, t.getTargetClass());
		throw new RuntimeException("Unknown condition type " + type);
	}

	private String idOf(Condition o)
	{
		Optional<String> id = ConditionTypes.REGISTRY.entrySet().stream().filter(e -> e.getValue().getTargetClass() == o.getClass()).map(Map.Entry::getKey).findAny();
		if (id.isPresent()) return id.get();

		throw new RuntimeException("Unknown condition class " + o.getClass());
	}
}
