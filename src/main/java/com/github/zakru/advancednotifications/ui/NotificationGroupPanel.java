package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.Notification;
import com.github.zakru.advancednotifications.NotificationGroup;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotificationGroupPanel extends NotificationPanel<NotificationGroup>
{
	public NotificationGroupPanel(NotificationGroup notification, DraggableContainer container)
	{
		super(notification, container);
		setLayout(new BorderLayout());
		setOpaque(false);
		DefaultTypePanel typePanel = new DefaultTypePanel(this, "Group");
		typePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		typePanel.setOpaque(true);
		typePanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				typePanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				typePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});

		JPanel notificationView = new JPanel();
		notificationView.setLayout(new BoxLayout(notificationView, BoxLayout.Y_AXIS));
		notificationView.setOpaque(false);
		notificationView.setBorder(new EmptyBorder(0, 10, 0, 0));

		int index = 0;
		notificationView.add(new DropSpace(plugin, notification, index++));
		for (final Notification notif : notification.getNotifications())
		{
			NotificationPanel<?> panel = NotificationPanel.buildPanel(plugin, notif);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(new DropSpace(plugin, notification, index++));
			}
		}

		add(typePanel, BorderLayout.NORTH);
		add(notificationView, BorderLayout.CENTER);
	}
}
