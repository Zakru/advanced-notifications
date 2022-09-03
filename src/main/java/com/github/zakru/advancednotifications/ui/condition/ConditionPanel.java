package com.github.zakru.advancednotifications.ui.condition;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.condition.Condition;
import com.github.zakru.advancednotifications.condition.EmptyCondition;
import com.github.zakru.advancednotifications.condition.ItemCondition;
import com.github.zakru.advancednotifications.notification.Notification;
import com.github.zakru.advancednotifications.ui.DropSpace;
import com.github.zakru.advancednotifications.ui.DropSpaceSystem;
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

public abstract class ConditionPanel<C extends Condition> extends JPanel implements MouseListener
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
		private final ConditionPanel<?> panel;

		public DragStarter(ConditionPanel<?> panel)
		{
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				panel.system.setDragging(panel.condition, panel.container);
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
					DropSpaceSystem<Condition> system = panel.system;
					DropSpace<Condition> space = system.getDragHovering();
					Condition cond = panel.condition;

					// Check if this is a container and the target is inside it
					if (!(cond instanceof DraggableContainer
						&& space.getContainer() instanceof Condition
						&& (cond == space.getContainer()
							|| containerContains((DraggableContainer<Condition>)cond, (Condition)space.getContainer())))
					)
					{
						if (system.getDraggingFrom() != space.getContainer())
						{
							system.getDraggingFrom().getDraggableItems().remove(system.getDragging());
							space.getContainer().getDraggableItems().add(space.getIndex(), system.getDragging());
						}
						else
						{
							List<Condition> conditions = panel.container.getDraggableItems();
							int originalIndex = conditions.indexOf(panel.condition);
							conditions.remove(panel.condition);
							int index = space.getIndex();
							if (index > originalIndex) index = index - 1;

							conditions.add(index, panel.condition);
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
		private static boolean containerContains(DraggableContainer<Condition> parent, Condition child) {
			if (parent.getDraggableItems().contains(child)) return true;

			for (Condition c : parent.getDraggableItems())
				if (c instanceof DraggableContainer && containerContains((DraggableContainer<Condition>)c, child)) return true;

			return false;
		}
	}

	protected static class DefaultTypePanel extends JPanel
	{
		private static final Border TYPE_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(8, 8, 8, 8));

		public DefaultTypePanel(ConditionPanel<?> panel, String typeName)
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

	protected final C condition;
	protected final DraggableContainer<Condition> container;
	protected final DropSpaceSystem<Condition> system;
	protected final AdvancedNotificationsPlugin plugin;

	private JPopupMenu menuPopup = new JPopupMenu();
	{
		menuPopup.add(new JMenuItem(new AbstractAction("Clone")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				container.getDraggableItems().add(condition.clone());
				plugin.rebuildPluginPanel();
			}
		}));
	}

	public ConditionPanel(C condition, DropSpaceSystem<Condition> system, DraggableContainer<Condition> container)
	{
		this.condition = condition;
		this.system = system;
		this.container = container;
		plugin = condition.getPlugin();

		addMouseListener(this);
	}

	public static ConditionPanel<?> buildPanel(Condition cond, DropSpaceSystem<Condition> system, DraggableContainer<Condition> container)
	{
		if (cond instanceof ItemCondition) return new ItemConditionPanel((ItemCondition)cond, system, container);
		if (cond instanceof EmptyCondition) return new EmptyConditionPanel((EmptyCondition)cond, system, container);
		//if (notif instanceof NotificationGroup) return new NotificationGroupPanel((NotificationGroup)cond, system, container);

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
	
	protected static JPanel createDefaultActions(ConditionPanel<?> panel) {
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
					Notification root = panel.container.getRoot();
					if (root != null)
					{
						root.setCondition(null);
						root.setConfiguring(false);
					}
					else panel.container.getDraggableItems().remove(panel.condition);
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

		actions.add(deleteButton);
		
		return actions;
	}
}
