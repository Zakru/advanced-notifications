package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.Notification;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class NotificationPanel<N extends Notification> extends JPanel
{
	protected static class DragStarter extends MouseAdapter
	{
		private final NotificationPanel<?> panel;

		public DragStarter(NotificationPanel<?> panel)
		{
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			panel.plugin.setDragging(panel.notification, panel.container);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (panel.plugin.getDragHovering() == null)
			{
				panel.plugin.setDragging(null, null);
			}
			else
			{
				AdvancedNotificationsPlugin plugin = panel.plugin;
				DropSpace space = plugin.getDragHovering();

				if (plugin.getDraggingFrom() != space.getContainer())
				{
					plugin.getDraggingFrom().getNotifications().remove(plugin.getDragging());
					space.getContainer().getNotifications().add(space.getIndex(), plugin.getDragging());
				}
				else
				{
					List<Notification> notifications = panel.container.getNotifications();
					int originalIndex = notifications.indexOf(panel.notification);
					notifications.remove(panel.notification);
					int index = space.getIndex();
					if (index > originalIndex) index = index - 1;

					notifications.add(index, panel.notification);
				}

				space.setBackground(ColorScheme.DARK_GRAY_COLOR);
				plugin.setDragging(null, null);
				plugin.updateConfig();
				plugin.rebuildPluginPanel();
			}
		}
	}

	protected static class DefaultTypePanel extends JPanel
	{
		private static final ImageIcon DELETE_ICON;
		private static final ImageIcon DELETE_HOVER_ICON;

		private static final Border TYPE_BORDER = new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			new EmptyBorder(8, 8, 8, 8));

		static
		{
			final BufferedImage addIcon
				= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
			DELETE_ICON = new ImageIcon(addIcon);
			DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
		}

		public DefaultTypePanel(NotificationPanel<?> panel, String typeName)
		{
			super(new BorderLayout());
			setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			setOpaque(false);
			setBorder(TYPE_BORDER);
			addMouseListener(new DragStarter(panel));
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					setOpaque(true);
					repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
					setOpaque(false);
					repaint();
				}
			});

			JLabel typeLabel = new JLabel(typeName);
			typeLabel.setForeground(Color.WHITE);

			JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
			actions.setOpaque(false);

			JLabel deleteButton = new JLabel(DELETE_ICON);
			deleteButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					panel.notification.getPlugin().getNotifications().remove(panel.notification);
					panel.notification.getPlugin().updateConfig();
					panel.notification.getPlugin().rebuildPluginPanel();
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					deleteButton.setIcon(DELETE_HOVER_ICON);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					deleteButton.setIcon(DELETE_ICON);
				}
			});

			actions.add(new EnabledButton(panel.notification.getPlugin(), panel.notification));
			actions.add(deleteButton);

			add(typeLabel, BorderLayout.WEST);
			add(actions, BorderLayout.EAST);
		}
	}

	protected final N notification;
	protected final DraggableContainer container;
	protected final AdvancedNotificationsPlugin plugin;

	public NotificationPanel(N notification, DraggableContainer container)
	{
		this.notification = notification;
		this.container = container;
		plugin = notification.getPlugin();
	}
}
