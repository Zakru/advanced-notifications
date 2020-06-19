package com.github.zakru.advancednotifications.notification;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import lombok.Getter;

public class NotificationType<T extends Notification>
{
	public interface Provider<T extends Notification>
	{
		T provide(AdvancedNotificationsPlugin plugin);
	}

	private final Provider<T> provider;
	@Getter
	private final Class<T> targetClass;

	public NotificationType(Provider<T> provider, Class<T> targetClass)
	{
		this.provider = provider;
		this.targetClass = targetClass;
	}

	public T provide(AdvancedNotificationsPlugin plugin)
	{
		return provider.provide(plugin);
	}
}
