package com.github.zakru.advancednotifications;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class NotificationGroup extends Notification implements DraggableContainer<Notification>
{
	@Getter
	@Setter
	private String name = "Group";
	@Getter
	@Setter
	private boolean collapsed = false;
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
	public NotificationGroup clone() {
		NotificationGroup n = new NotificationGroup(getPlugin());
		n.name = name + " copy";
		n.collapsed = collapsed;

		for (Notification n1 : notifications) n.notifications.add(n1.clone());

		return n;
	}

	@Override
	public List<Notification> getItems() {
		return notifications;
	}
}
