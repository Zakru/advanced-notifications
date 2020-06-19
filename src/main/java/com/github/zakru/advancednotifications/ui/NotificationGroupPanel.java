package com.github.zakru.advancednotifications.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.notification.NotificationGroup;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

public class NotificationGroupPanel extends NotificationPanel<NotificationGroup>
{
	private static final ImageIcon RENAME_ICON;
	private static final ImageIcon RENAME_HOVER_ICON;

	private static final ImageIcon COLLAPSE_ICON;
	private static final ImageIcon COLLAPSE_HOVER_ICON;
	private static final ImageIcon EXPAND_ICON;
	private static final ImageIcon EXPAND_HOVER_ICON;

	private final JTextField nameLabel;
	private final JLabel rename;

	static
	{
		final BufferedImage renameIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "rename_icon.png");
		RENAME_ICON = new ImageIcon(renameIcon);
		RENAME_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(renameIcon, 0.53f));

		final BufferedImage collapseIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "collapse_icon.png");
		COLLAPSE_ICON = new ImageIcon(collapseIcon);
		COLLAPSE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(collapseIcon, 0.53f));

		final BufferedImage expandIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "expand_icon.png");
		EXPAND_ICON = new ImageIcon(expandIcon);
		EXPAND_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(expandIcon, 0.53f));
	}

	public NotificationGroupPanel(NotificationGroup notification, DropSpaceSystem<Notification> system, DraggableContainer<Notification> container)
	{
		super(notification, system, container);
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		northPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		northPanel.addMouseListener(new DragStarter(this));
		northPanel.addMouseListener(this);
		northPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					northPanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					northPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				}
			}
		});

		nameLabel = new JTextField();
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setDisabledTextColor(Color.WHITE);
		nameLabel.setEnabled(false);
		nameLabel.setBorder(null);
		nameLabel.setBackground(null);
		nameLabel.setOpaque(false);
		nameLabel.setText(notification.getName());
		nameLabel.addActionListener(e -> finishRename());
		nameLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				for (MouseListener l : northPanel.getMouseListeners()) l.mousePressed(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				for (MouseListener l : northPanel.getMouseListeners()) l.mouseReleased(e);
			}
		});
		nameLabel.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				finishRename();
			}
		});

		JPanel actions = createDefaultActions(this);

		rename = new JLabel(RENAME_ICON);
		rename.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					nameLabel.setEnabled(true);
					nameLabel.requestFocusInWindow();
					nameLabel.selectAll();
					rename.setEnabled(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				rename.setIcon(RENAME_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				rename.setIcon(RENAME_ICON);
			}
		});

		actions.add(rename, 0);

		JLabel collapseOrExpand = new JLabel(notification.isCollapsed() ? EXPAND_ICON : COLLAPSE_ICON);
		collapseOrExpand.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					notification.setCollapsed(!notification.isCollapsed());
					notification.getPlugin().updateConfig();
					notification.getPlugin().rebuildPluginPanel();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				collapseOrExpand.setIcon(notification.isCollapsed() ? EXPAND_HOVER_ICON : COLLAPSE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				collapseOrExpand.setIcon(notification.isCollapsed() ? EXPAND_ICON : COLLAPSE_ICON);
			}
		});

		northPanel.add(collapseOrExpand, BorderLayout.WEST);
		northPanel.add(nameLabel, BorderLayout.CENTER);
		northPanel.add(actions, BorderLayout.EAST);

		add(northPanel, BorderLayout.NORTH);

		if (!notification.isCollapsed())
		{
			JPanel notificationView = new JPanel();
			notificationView.setLayout(new BoxLayout(notificationView, BoxLayout.Y_AXIS));
			notificationView.setOpaque(false);
			notificationView.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 5, 0, 0, ColorScheme.DARKER_GRAY_COLOR),
				BorderFactory.createEmptyBorder(0, 5, 0, 0))
			);

			int index = 0;
			notificationView.add(new DropSpace<Notification>(system, notification, index++));
			for (final Notification notif : notification.getItems())
			{
				NotificationPanel<?> panel = NotificationPanel.buildPanel(notif, system, notification);
				if (panel != null)
				{
					notificationView.add(panel);
					notificationView.add(new DropSpace<Notification>(system, notification, index++));
				}
			}

			add(notificationView, BorderLayout.CENTER);
		}
	}

	public void resetScroll()
	{
		nameLabel.setScrollOffset(0);
	}

	private void finishRename()
	{
		if (!nameLabel.isEnabled()) return;

		nameLabel.setEnabled(false);
		nameLabel.requestFocusInWindow();
		notification.setName(nameLabel.getText());
		plugin.updateConfig();
		rename.setEnabled(true);
	}
}
