package com.github.zakru.advancednotifications;

import java.util.ArrayList;
import java.util.List;

public class NotificationGroup extends Notification implements DraggableContainer
{
	private final List<Notification> notifications = new ArrayList<>();

	public NotificationGroup(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
	}

	@Override
	protected void notify(Object event)
	{
		for (Notification n : notifications) n.tryNotify(event);
	}

	@Override
	public List<Notification> getNotifications()
	{
		return notifications;
	}
}
