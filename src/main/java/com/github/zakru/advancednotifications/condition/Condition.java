package com.github.zakru.advancednotifications.condition;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.notification.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class Condition
{
	@Getter
	@Setter
	private transient AdvancedNotificationsPlugin plugin;

	public Condition(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
	}

	public abstract boolean isFulfilled();

	public abstract Condition clone();
}
