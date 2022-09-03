package com.github.zakru.advancednotifications.ui.notification;

import com.github.zakru.advancednotifications.*;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.condition.EmptyCondition;
import com.github.zakru.advancednotifications.condition.ItemCondition;
import com.github.zakru.advancednotifications.notification.EmptyNotification;
import com.github.zakru.advancednotifications.notification.ItemNotification;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.notification.NotificationGroup;
import com.github.zakru.advancednotifications.ui.DropSpace;
import com.github.zakru.advancednotifications.ui.DropSpaceSystem;
import com.github.zakru.advancednotifications.ui.EnabledButton;
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

	private static final ImageIcon CONFIGURE_ICON;
	private static final ImageIcon CONFIGURE_HOVER_ICON;
	private static final ImageIcon CONFIGURE_ACTIVE_ICON;
	private static final ImageIcon CONFIGURE_ACTIVE_HOVER_ICON;

	static
	{
		final BufferedImage deleteIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteIcon);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteIcon, 0.53f));

		final BufferedImage configureIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "configure_icon.png");
		CONFIGURE_ICON = new ImageIcon(configureIcon);
		CONFIGURE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(configureIcon, 0.53f));

		final BufferedImage configureActiveIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "configure_active_icon.png");
		CONFIGURE_ACTIVE_ICON = new ImageIcon(configureActiveIcon);
		CONFIGURE_ACTIVE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(configureActiveIcon, 0.53f));
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
							system.getDraggingFrom().getDraggableItems().remove(system.getDragging());
							space.getContainer().getDraggableItems().add(space.getIndex(), system.getDragging());
						}
						else
						{
							List<Notification> notifications = panel.container.getDraggableItems();
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
			if (parent.getDraggableItems().contains(child)) return true;

			for (Notification n : parent.getDraggableItems())
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
				container.getDraggableItems().add(notification.clone());
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

	public static NotificationPanel<?> buildPanel(
		Notification notif,
		DropSpaceSystem<Notification> system,
		DraggableContainer<Notification> container,
		DropSpaceSystem<Condition> conditionSystem
	)
	{
		if (notif instanceof ItemNotification) return new ItemNotificationPanel((ItemNotification)notif, system, container, conditionSystem);
		if (notif instanceof EmptyNotification) return new EmptyNotificationPanel((EmptyNotification)notif, system, container, conditionSystem);
		if (notif instanceof NotificationGroup) return new NotificationGroupPanel((NotificationGroup)notif, system, container, conditionSystem);

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
					panel.container.getDraggableItems().remove(panel.notification);
					panel.plugin.updateConfig();
					panel.plugin.rebuildPluginPanel();
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

		JLabel configureButton = new JLabel(panel.notification.getCondition() != null ? CONFIGURE_ACTIVE_ICON : CONFIGURE_ICON);
		if (panel.notification.isConfiguring())
		{
			configureButton.setOpaque(true);
			configureButton.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
		}
		configureButton.setToolTipText(panel.notification.getCondition() != null ? "Toggle condition configuration" : "Add condition");
		JPopupMenu addPopup = createAddConditionMenu(panel);
		configureButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (panel.notification.getCondition() == null)
					addPopup.show(configureButton, e.getX(), e.getY());
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (panel.notification.getCondition() != null)
				{
					if (e.getButton() == MouseEvent.BUTTON1)
					{
						panel.notification.setConfiguring(!panel.notification.isConfiguring());
						panel.plugin.rebuildPluginPanel();
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				configureButton.setIcon(panel.notification.getCondition() != null ? CONFIGURE_ACTIVE_HOVER_ICON : CONFIGURE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				configureButton.setIcon(panel.notification.getCondition() != null ? CONFIGURE_ACTIVE_ICON : CONFIGURE_ICON);
			}
		});

		actions.add(configureButton);
		actions.add(new EnabledButton(panel.plugin, panel.notification));
		actions.add(deleteButton);
		
		return actions;
	}

	protected static JPopupMenu createAddConditionMenu(NotificationPanel<?> panel) {
		JPopupMenu addPopup = new JPopupMenu();
		addPopup.add(new JMenuItem(new AbstractAction("Inventory")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.notification.setCondition(new ItemCondition(panel.plugin));
				panel.notification.setConfiguring(true);
				panel.plugin.updateConfig();
				panel.plugin.rebuildPluginPanel();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Empty Space")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.notification.setCondition(new EmptyCondition(panel.plugin));
				panel.notification.setConfiguring(true);
				panel.plugin.updateConfig();
				panel.plugin.rebuildPluginPanel();
			}
		}));
		/*addPopup.add(new JMenuItem(new AbstractAction("Group")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.notification.setCondition().add(new ConditionGroup(panel.notification.getPlugin()));
				panel.notification.getPlugin().updateConfig();
				panel.notification.getPlugin().rebuildPluginPanel();
			}
		}));*/

		return addPopup;
	}
}
