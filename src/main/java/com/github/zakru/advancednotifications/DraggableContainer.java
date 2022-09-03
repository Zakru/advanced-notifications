package com.github.zakru.advancednotifications;

import com.github.zakru.advancednotifications.notification.Notification;

import java.util.List;

public interface DraggableContainer<T>
{
	List<T> getDraggableItems();
	default Notification getRoot() { return null; }
}
