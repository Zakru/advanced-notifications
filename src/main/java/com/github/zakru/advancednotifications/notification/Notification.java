package com.github.zakru.advancednotifications.notification;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.condition.Condition;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class Notification implements Cloneable
{
	@Getter
	@Setter
	private transient AdvancedNotificationsPlugin plugin;

	@Getter
	@Setter
	private  boolean enabled = true;

	//@Getter
	//private List<Condition> conditions = new ArrayList<>();

	public Notification(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void tryNotify(Object event)
	{
		if (!enabled) return;// || conditions.stream().anyMatch(c -> !c.isFulfilled())) return;

		notify(event);
	}

	protected abstract void notify(Object event);

	protected void doNotification(String message)
	{
		plugin.getNotifier().notify(message);
	}

	public abstract Notification clone();
}
