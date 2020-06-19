package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.*;
import com.github.zakru.advancednotifications.notification.EmptyNotification;
import com.github.zakru.advancednotifications.notification.ItemNotification;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.notification.NotificationGroup;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class NotificationPanel<N extends Notification> extends JPanel implements MouseListener
{
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	static
	{
		final BufferedImage deleteIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteIcon);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteIcon, 0.53f));
	}

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
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				panel.system.setDragging(panel.notification, panel.container);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				if (panel.system.getDragHovering() == null)
				{
					panel.system.setDragging(null, null);
				}
				else
				{
					DropSpaceSystem<Notification> system = panel.system;
					DropSpace<Notification> space = system.getDragHovering();
					Notification notif = panel.notification;

					// Check if this is a container and the target is inside it
					if (!(notif instanceof DraggableContainer
						&& space.getContainer() instanceof Notification
						&& (notif == space.getContainer()
							|| containerContains((DraggableContainer<Notification>)notif, (Notification)space.getContainer())))
					)
					{
						if (system.getDraggingFrom() != space.getContainer())
						{
							system.getDraggingFrom().getItems().remove(system.getDragging());
							space.getContainer().getItems().add(space.getIndex(), system.getDragging());
						}
						else
						{
							List<Notification> notifications = panel.container.getItems();
							int originalIndex = notifications.indexOf(panel.notification);
							notifications.remove(panel.notification);
							int index = space.getIndex();
							if (index > originalIndex) index = index - 1;

							notifications.add(index, panel.notification);
						}
						panel.plugin.updateConfig();
						panel.plugin.rebuildPluginPanel();
					}

					space.setBackground(ColorScheme.DARK_GRAY_COLOR);
					system.setDragging(null, null);
					system.setDragHovering(null);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private static boolean containerContains(DraggableContainer<Notification> parent, Notification child) {
			if (parent.getItems().contains(child)) return true;

			for (Notification n : parent.getItems())
				if (n instanceof DraggableContainer && containerContains((DraggableContainer<Notification>)n, child)) return true;

			return false;
		}
	}

	protected static class DefaultTypePanel extends JPanel
	{
		private static final Border TYPE_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(8, 8, 8, 8));

		public DefaultTypePanel(NotificationPanel<?> panel, String typeName)
		{
			super(new BorderLayout());
			setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			setOpaque(false);
			setBorder(TYPE_BORDER);
			addMouseListener(new DragStarter(panel));
			addMouseListener(panel);

			JLabel typeLabel = new JLabel(typeName);
			typeLabel.setForeground(Color.WHITE);

			add(typeLabel, BorderLayout.WEST);
			add(createDefaultActions(panel), BorderLayout.EAST);
		}

		public void addDefaultVisualListener()
		{
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON1)
					{
						((DefaultTypePanel)e.getComponent()).setOpaque(true);
						e.getComponent().repaint();
					}
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON1)
					{
						((DefaultTypePanel)e.getComponent()).setOpaque(false);
						e.getComponent().repaint();
					}
				}
			});
		}
	}

	protected final N notification;
	protected final DraggableContainer<Notification> container;
	protected final DropSpaceSystem<Notification> system;
	protected final AdvancedNotificationsPlugin plugin;

	private JPopupMenu menuPopup = new JPopupMenu();
	{
		menuPopup.add(new JMenuItem(new AbstractAction("Clone")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				container.getItems().add(notification.clone());
				plugin.rebuildPluginPanel();
			}
		}));
	}

	public NotificationPanel(N notification, DropSpaceSystem<Notification> system, DraggableContainer<Notification> container)
	{
		this.notification = notification;
		this.system = system;
		this.container = container;
		plugin = notification.getPlugin();

		addMouseListener(this);
	}

	public static NotificationPanel<?> buildPanel(Notification notif, DropSpaceSystem<Notification> system, DraggableContainer<Notification> container)
	{
		if (notif instanceof ItemNotification) return new ItemNotificationPanel((ItemNotification)notif, system, container);
		if (notif instanceof EmptyNotification) return new EmptyNotificationPanel((EmptyNotification)notif, system, container);
		if (notif instanceof NotificationGroup) return new NotificationGroupPanel((NotificationGroup)notif, system, container);

		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e)
	{
		handleClick(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		handleClick(e);
	}

	private void handleClick(MouseEvent e)
	{
		if (e.isPopupTrigger() && !e.isConsumed())
		{
			menuPopup.show(this, e.getX(), e.getY());
			e.consume();
		}
	}
	
	protected static JPanel createDefaultActions(NotificationPanel<?> panel) {
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		actions.setOpaque(false);
		actions.setBorder(BorderFactory.createEmptyBorder(0, -4, 0, -4));

		JLabel deleteButton = new JLabel(DELETE_ICON);
		deleteButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					panel.container.getItems().remove(panel.notification);
					panel.notification.getPlugin().updateConfig();
					panel.notification.getPlugin().rebuildPluginPanel();
				}
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
		
		return actions;
	}
}
