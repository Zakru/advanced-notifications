package com.github.zakru.advancednotifications.ui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.github.zakru.advancednotifications.DraggableContainer;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

public class DropSpace<T> extends JPanel
{
	private final MouseAdapter listener = new MouseAdapter()
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			@SuppressWarnings("unchecked")
			DropSpace<T> space = (DropSpace<T>)e.getComponent();
			if (space.system.getDragging() != null)
			{
				space.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
				space.system.setDragHovering(space);
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			@SuppressWarnings("unchecked")
			DropSpace<T> space = (DropSpace<T>)e.getComponent();
			if (space.system.getDragging() != null)
			{
				space.setBackground(ColorScheme.DARK_GRAY_COLOR);
				space.system.setDragHovering(null);
			}
		}
	};

	private final DropSpaceSystem<T> system;
	@Getter
	private final DraggableContainer<T> container;
	@Getter
	private final int index;

	public DropSpace(DropSpaceSystem<T> system, DraggableContainer<T> container, int index)
	{
		this.system = system;
		this.container = container;
		this.index = index;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setPreferredSize(new Dimension(0,10));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		addMouseListener(listener);
	}
}
