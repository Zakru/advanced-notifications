package com.github.zakru.advancednotifications.ui.condition;

import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.condition.InventoryChecker;
import com.github.zakru.advancednotifications.condition.ItemCondition;
import com.github.zakru.advancednotifications.notification.InventoryComparator;
import com.github.zakru.advancednotifications.notification.ItemNotification;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.ui.DropSpaceSystem;
import com.github.zakru.advancednotifications.ui.notification.NotificationPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ItemConditionPanel extends ConditionPanel<ItemCondition>
{
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
	private final JSpinner countSpinner = new JSpinner(spinnerModel);

	public ItemConditionPanel(ItemCondition condition, DropSpaceSystem<Condition> system, DraggableContainer<Condition> container)
	{
		super(condition, system, container);
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		DefaultTypePanel typePanel = new DefaultTypePanel(this, "Inventory Condition");
		typePanel.addDefaultVisualListener();

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		contentPanel.setOpaque(false);

		JTextField nameField = new JTextField(condition.getItem());
		nameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}
		});

		JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		paramsPanel.setOpaque(false);

		JLabel countLabel = new JLabel("Count ");
		countLabel.setForeground(Color.WHITE);

		JComboBox<InventoryChecker> comparatorBox = new JComboBox<>(InventoryChecker.CHECKERS);
		comparatorBox.setSelectedItem(condition.getChecker().object);
		comparatorBox.setPreferredSize(new Dimension(50, 20));
		comparatorBox.setMaximumRowCount(9);
		comparatorBox.addItemListener(e -> {
			condition.getChecker().object = (InventoryChecker)comparatorBox.getSelectedItem();
			condition.getPlugin().updateConfig();
			countSpinner.setVisible(condition.getChecker().object.takesParam());
		});

		countSpinner.setValue(condition.getCheckerParam());
		countSpinner.setPreferredSize(new Dimension(64, 20));
		countSpinner.setVisible(condition.getChecker().object.takesParam());
		countSpinner.addChangeListener(e -> {
			condition.setCheckerParam((Integer)countSpinner.getValue());
			condition.getPlugin().updateConfig();
		});

		paramsPanel.add(countLabel);
		paramsPanel.add(comparatorBox);
		paramsPanel.add(countSpinner);

		contentPanel.add(nameField, BorderLayout.NORTH);
		contentPanel.add(paramsPanel, BorderLayout.SOUTH);

		add(typePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	private void updateItem(JTextField field)
	{
		condition.setItem(field.getText());
		condition.getPlugin().updateConfig();
	}
}
