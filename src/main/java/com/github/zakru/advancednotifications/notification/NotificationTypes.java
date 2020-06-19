package com.github.zakru.advancednotifications.notification;

import java.util.HashMap;
import java.util.Map;

public class NotificationTypes
{
	public static final Map<String, NotificationType<?>> REGISTRY = new HashMap<>();

	public static final NotificationType<ItemNotification> ITEM = new NotificationType<>(ItemNotification::new, ItemNotification.class);
	public static final NotificationType<EmptyNotification> EMPTY = new NotificationType<>(EmptyNotification::new, EmptyNotification.class);
	public static final NotificationType<NotificationGroup> GROUP = new NotificationType<>(NotificationGroup::new, NotificationGroup.class);

	public static void registerAll()
	{
		REGISTRY.put("item", ITEM);
		REGISTRY.put("empty", EMPTY);
		REGISTRY.put("group", GROUP);
	}
}
