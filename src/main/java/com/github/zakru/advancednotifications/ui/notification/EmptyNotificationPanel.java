package com.github.zakru.advancednotifications.ui.notification;

import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.notification.EmptyNotification;
import com.github.zakru.advancednotifications.notification.InventoryComparator;
import com.github.zakru.advancednotifications.notification.Notification;

import com.github.zakru.advancednotifications.ui.DropSpaceSystem;
import com.github.zakru.advancednotifications.ui.condition.ConditionPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class EmptyNotificationPanel extends NotificationPanel<EmptyNotification>
{
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
	private final JSpinner countSpinner = new JSpinner(spinnerModel);

	public EmptyNotificationPanel(
		EmptyNotification notification,
		DropSpaceSystem<Notification> system,
		DraggableContainer<Notification> container,
		DropSpaceSystem<Condition> conditionSystem
	)
	{
		super(notification, system, container);
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		DefaultTypePanel typePanel = new DefaultTypePanel(this, "Empty Space");
		typePanel.addDefaultVisualListener();

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		contentPanel.setOpaque(false);

		JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		paramsPanel.setOpaque(false);

		JLabel countLabel = new JLabel("Count ");
		countLabel.setForeground(Color.WHITE);

		JComboBox<InventoryComparator> comparatorBox = new JComboBox<>(InventoryComparator.COMPARATORS);
		comparatorBox.setSelectedItem(notification.getComparator().object);
		comparatorBox.setPreferredSize(new Dimension(50, 20));
		comparatorBox.setMaximumRowCount(9);
		comparatorBox.addItemListener(e -> {
			notification.getComparator().object = (InventoryComparator)comparatorBox.getSelectedItem();
			notification.getPlugin().updateConfig();
			countSpinner.setVisible(notification.getComparator().object.takesParam());
		});

		countSpinner.setValue(notification.getComparatorParam());
		countSpinner.setPreferredSize(new Dimension(64, 20));
		countSpinner.setVisible(notification.getComparator().object.takesParam());
		countSpinner.addChangeListener(e -> {
			notification.setComparatorParam((Integer)countSpinner.getValue());
			notification.getPlugin().updateConfig();
		});

		paramsPanel.add(countLabel);
		paramsPanel.add(comparatorBox);
		paramsPanel.add(countSpinner);

		contentPanel.add(paramsPanel, BorderLayout.SOUTH);

		add(typePanel, BorderLayout.NORTH);
		if (notification.isConfiguring())
		{
			ConditionPanel cond = ConditionPanel.buildPanel(notification.getCondition(), conditionSystem, notification.getConditionContainer());
			cond.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR));
			add(cond, BorderLayout.CENTER);
		}
		add(contentPanel, BorderLayout.SOUTH);
	}
}
