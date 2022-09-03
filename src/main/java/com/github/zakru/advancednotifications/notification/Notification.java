package com.github.zakru.advancednotifications.notification;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.condition.Condition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public abstract class Notification
{
	@Getter
	@Setter
	private transient AdvancedNotificationsPlugin plugin;

	@Getter
	@Setter
	private boolean enabled = true;

	@Getter
	@Setter
	private transient boolean configuring = false;

	@Getter
	@Setter
	private Condition condition = null;

	@Getter
	private final transient DraggableContainer<Condition> conditionContainer = new DraggableContainer<Condition>() {

		@Override
		public List<Condition> getDraggableItems() {
			return Collections.singletonList(condition);
		}

		@Override
		public Notification getRoot() {
			return Notification.this;
		}
	};

	public Notification(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void tryNotify(Object event)
	{
		if (!enabled || (condition != null && !condition.isFulfilled())) return;

		notify(event);
	}

	protected abstract void notify(Object event);

	protected void doNotification(String message)
	{
		plugin.getNotifier().notify(message);
	}

	public abstract Notification clone();
}
