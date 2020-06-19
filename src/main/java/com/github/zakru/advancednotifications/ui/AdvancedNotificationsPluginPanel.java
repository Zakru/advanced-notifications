package com.github.zakru.advancednotifications.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.EmptyNotification;
import com.github.zakru.advancednotifications.ItemNotification;
import com.github.zakru.advancednotifications.Notification;
import com.github.zakru.advancednotifications.NotificationGroup;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

public class AdvancedNotificationsPluginPanel extends PluginPanel implements DropSpaceSystem<Notification>
{
	private static final ImageIcon ADD_ICON;
	private static final ImageIcon ADD_HOVER_ICON;

	private final AdvancedNotificationsPlugin plugin;

	private final JPanel notificationView;

	@Getter
	private Notification dragging;
	@Getter
	private DraggableContainer<Notification> draggingFrom;
	@Getter
	@Setter
	private DropSpace<Notification> dragHovering;

	static
	{
		final BufferedImage addIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "add_icon.png");
		ADD_ICON = new ImageIcon(addIcon);
		ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
	}

	public AdvancedNotificationsPluginPanel(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JLabel title = new JLabel("Notifications");
		title.setForeground(Color.WHITE);

		JPopupMenu addPopup = new JPopupMenu();
		addPopup.add(new JMenuItem(new AbstractAction("Inventory")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getItems().add(new ItemNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Empty Space")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getItems().add(new EmptyNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Group")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getItems().add(new NotificationGroup(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));

		JLabel addNotification = new JLabel(ADD_ICON);
		addNotification.setToolTipText("Add a notification");
		addNotification.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				addPopup.show(addNotification, e.getX(), e.getY());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addNotification.setIcon(ADD_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addNotification.setIcon(ADD_ICON);
			}
		});

		northPanel.add(title, BorderLayout.WEST);
		northPanel.add(addNotification, BorderLayout.EAST);

		notificationView = new JPanel();
		notificationView.setLayout(new BoxLayout(notificationView, BoxLayout.Y_AXIS));

		add(northPanel, BorderLayout.NORTH);
		add(notificationView, BorderLayout.CENTER);
	}

	public void rebuild()
	{
		notificationView.removeAll();

		int index = 0;
		notificationView.add(new DropSpace<Notification>(this, plugin, index++));
		for (final Notification notif : plugin.getItems())
		{
			NotificationPanel<?> panel = NotificationPanel.buildPanel(notif, this, plugin);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(new DropSpace<Notification>(this, plugin, index++));
			}
		}

		repaint();
		revalidate();

		for (Component n : notificationView.getComponents())
		{
			if (n instanceof NotificationGroupPanel) ((NotificationGroupPanel)n).resetScroll();
		}
	}

	@Override
	public void setDragging(Notification n, DraggableContainer<Notification> from) {
		dragging = n;
		draggingFrom = from;
	}
}
