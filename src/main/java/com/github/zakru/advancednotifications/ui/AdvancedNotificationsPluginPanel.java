package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.*;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.notification.EmptyNotification;
import com.github.zakru.advancednotifications.notification.ItemNotification;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.notification.NotificationGroup;
import com.github.zakru.advancednotifications.ui.notification.NotificationGroupPanel;
import com.github.zakru.advancednotifications.ui.notification.NotificationPanel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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

	@Getter
	private Condition draggingCondition;
	@Getter
	private DraggableContainer<Condition> draggingFromCondition;
	@Getter
	@Setter
	private DropSpace<Condition> dragHoveringCondition;

	private DropSpaceSystem<Condition> conditionSystem = new DropSpaceSystem<Condition>() {

		@Override
		public Condition getDragging() {
			return draggingCondition;
		}

		@Override
		public void setDragging(Condition c, DraggableContainer from) {
			draggingCondition = c;
			draggingFromCondition = from;
		}

		@Override
		public DraggableContainer getDraggingFrom() {
			return draggingFromCondition;
		}

		@Override
		public DropSpace getDragHovering() {
			return dragHoveringCondition;
		}

		@Override
		public void setDragHovering(DropSpace space) {
			dragHoveringCondition = space;
		}
	};

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
				plugin.getDraggableItems().add(new ItemNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Empty Space")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getDraggableItems().add(new EmptyNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Group")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getDraggableItems().add(new NotificationGroup(plugin));
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
		notificationView.add(new DropSpace<>(this, plugin, index++));
		for (final Notification notif : plugin.getDraggableItems())
		{
			NotificationPanel<?> panel = NotificationPanel.buildPanel(notif, this, plugin, conditionSystem);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(new DropSpace<>(this, plugin, index++));
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
