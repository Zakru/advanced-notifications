package com.github.zakru.advancednotifications;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AdvancedNotificationsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AdvancedNotificationsPlugin.class);
		RuneLite.main(args);
	}
}