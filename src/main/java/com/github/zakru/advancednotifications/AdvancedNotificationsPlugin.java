package com.github.zakru.advancednotifications;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.github.zakru.advancednotifications.ui.AdvancedNotificationsPluginPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import joptsimple.internal.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Advanced Notifications",
	tags = {"notifications", "inventory", "item"},
	description = "An advanced notifications system"
)
public class AdvancedNotificationsPlugin extends Plugin implements DraggableContainer<Notification>
{
	private static final String CONFIG_GROUP = "advancednotifications";
	private static final String CONFIG_KEY = "notifications";
	private static final String CONFIG_KEY_FORMAT = "format";
	private static final String ICON_FILE = "panel_icon.png";
	private static final String PLUGIN_NAME = "Advanced Notifications";

	private static final int FORMAT_CURRENT_VERSION = 1;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private Client client;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private ItemManager itemManager;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private Notifier notifier;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	private AdvancedNotificationsPluginPanel pluginPanel;
	private NavigationButton navigationButton;
	private Item[] previousItems;

	private List<Notification> notifications;

	@Override
	protected void startUp() throws Exception
	{
		notifications = new ArrayList<>();
		previousItems = null;

		int version = 0;
		String versionString = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_FORMAT);
		try
		{
			if (!Strings.isNullOrEmpty(versionString)) version = Integer.parseInt(versionString);
		}
		catch (NumberFormatException e) {}
		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY), version);

		pluginPanel = new AdvancedNotificationsPluginPanel(this);
		pluginPanel.rebuild();

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);

		navigationButton = NavigationButton.builder()
			.tooltip(PLUGIN_NAME)
			.icon(icon)
			.priority(5)
			.panel(pluginPanel)
			.build();

		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navigationButton);

		notifications = null;
		pluginPanel = null;
		navigationButton = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (notifications.isEmpty() && event.getGroup().equals(CONFIG_GROUP) && event.getKey().equals(CONFIG_KEY))
		{
			loadConfig(event.getNewValue(), FORMAT_CURRENT_VERSION);
		}
	}

	private void loadConfig(String json, int version)
	{
		if (Strings.isNullOrEmpty(json))
		{
			notifications = new ArrayList<>();
			return;
		}

		if (version == 0)
		{
			Gson gson = new GsonBuilder()
				.registerTypeAdapter(Notification.class, new LegacyNotificationAdapter(this))
				.create();

			notifications = gson.fromJson(json, new TypeToken<ArrayList<Notification>>(){}.getType());
		}
		else if (version == 1)
		{
			Gson gson = new GsonBuilder()
				.registerTypeAdapter(Notification.class, new NotificationAdapter(this))
				.create();

			notifications = gson.fromJson(json, new TypeToken<ArrayList<Notification>>(){}.getType());
		}
	}

	private void notify(Object event)
	{
		for (Notification n : notifications)
		{
			n.tryNotify(event);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			Item[] items = event.getItemContainer().getItems();
			if (previousItems == null)
			{
				previousItems = items;
				return;
			}

			if (client.getWidget(WidgetInfo.BANK_CONTAINER) == null)
			{
				Set<Integer> uniqueItems = new HashSet<>();
				addUniqueItems(uniqueItems, items);
				addUniqueItems(uniqueItems, previousItems);

				for (int id : uniqueItems)
				{
					notify(new InventoryEvent(id, countItems(items, id), countItems(previousItems, id)));
				}
			}

			previousItems = items;
		}
	}

	private void addUniqueItems(Set<Integer> set, Item[] items)
	{
		for (Item i : items) set.add(i.getId());
	}

	private int countItems(Item[] items, int id)
	{
		int c = 0;
		for (Item i : items) if (i.getId() == id) c += Math.max(i.getQuantity(), 1);
		return c;
	}

	public void updateConfig()
	{
		if (notifications.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY_FORMAT);
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
			return;
		}

		configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY_FORMAT);
		final Gson gson = new GsonBuilder()
			.registerTypeAdapter(Notification.class, new NotificationAdapter(this))
			.create();
		final String json = gson.toJson(notifications, new TypeToken<ArrayList<Notification>>(){}.getType());
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_FORMAT, FORMAT_CURRENT_VERSION);
	}

	public void rebuildPluginPanel()
	{
		pluginPanel.rebuild();
	}

	@Override
	public List<Notification> getItems()
	{
		return notifications;
	}
}
