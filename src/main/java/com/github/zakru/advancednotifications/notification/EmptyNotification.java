package com.github.zakru.advancednotifications.notification;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.InventoryEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class EmptyNotification extends Notification
{
	@Getter
	@Setter
	private InventoryComparator.Pointer comparator = new InventoryComparator.Pointer(InventoryComparator.COMPARATORS[0]);
	@Getter
	@Setter
	private int comparatorParam = 0;

	public EmptyNotification(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void notify(Object event)
	{
		if (!(event instanceof InventoryEvent)) return;

		InventoryEvent e = (InventoryEvent)event;

		if (e.getItemID() == -1 && comparator.object.shouldNotify(e.getPreviousCount(), e.getCount(), comparatorParam))
		{
			doNotification(comparator.object.notification("empty space", comparatorParam));
		}
	}

	@Override
	public EmptyNotification clone()
	{
		EmptyNotification n = new EmptyNotification(getPlugin());
		n.comparator = new InventoryComparator.Pointer(comparator.object);
		n.comparatorParam = comparatorParam;
		return n;
	}
}
