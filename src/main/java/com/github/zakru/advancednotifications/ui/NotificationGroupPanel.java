package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.Notification;
import com.github.zakru.advancednotifications.NotificationGroup;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class NotificationGroupPanel extends NotificationPanel<NotificationGroup>
{
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	private static final ImageIcon RENAME_ICON;
	private static final ImageIcon RENAME_HOVER_ICON;

	private final JTextField nameLabel;
	private final JLabel rename;

	static
	{
		final BufferedImage deleteIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteIcon);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteIcon, 0.53f));

		final BufferedImage renameIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "rename_icon.png");
		RENAME_ICON = new ImageIcon(renameIcon);
		RENAME_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(renameIcon, 0.53f));
	}

	public NotificationGroupPanel(NotificationGroup notification, DraggableContainer container)
	{
		super(notification, container);
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		northPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		northPanel.addMouseListener(new DragStarter(this));
		northPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				northPanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				northPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
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

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		actions.setOpaque(false);
		actions.setBorder(BorderFactory.createEmptyBorder(0, -4, 0, -4));

		rename = new JLabel(RENAME_ICON);
		rename.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				nameLabel.setEnabled(true);
				nameLabel.requestFocusInWindow();
				nameLabel.selectAll();
				rename.setEnabled(false);
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

		JLabel deleteButton = new JLabel(DELETE_ICON);
		deleteButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				container.getNotifications().remove(notification);
				notification.getPlugin().updateConfig();
				notification.getPlugin().rebuildPluginPanel();
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

		actions.add(rename);
		actions.add(new EnabledButton(notification.getPlugin(), notification));
		actions.add(deleteButton);

		northPanel.add(nameLabel, BorderLayout.CENTER);
		northPanel.add(actions, BorderLayout.EAST);

		JPanel notificationView = new JPanel();
		notificationView.setLayout(new BoxLayout(notificationView, BoxLayout.Y_AXIS));
		notificationView.setOpaque(false);
		notificationView.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		int index = 0;
		notificationView.add(new DropSpace(plugin, notification, index++));
		for (final Notification notif : notification.getNotifications())
		{
			NotificationPanel<?> panel = NotificationPanel.buildPanel(notification, notif);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(new DropSpace(plugin, notification, index++));
			}
		}

		add(northPanel, BorderLayout.NORTH);
		add(notificationView, BorderLayout.CENTER);
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