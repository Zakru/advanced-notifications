package com.github.zakru.advancednotifications.condition;

import com.github.zakru.advancednotifications.notification.InventoryComparator;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

public class CheckerAdapter extends TypeAdapter<InventoryChecker.Pointer>
{
	@Override
	public void write(JsonWriter out, InventoryChecker.Pointer o) throws IOException
	{
		out.value(Arrays.asList(InventoryChecker.CHECKERS).indexOf(o.object));
	}

	@Override
	public InventoryChecker.Pointer read(JsonReader in) throws IOException
	{
		return new InventoryChecker.Pointer(InventoryChecker.CHECKERS[in.nextInt()]);
	}
}
