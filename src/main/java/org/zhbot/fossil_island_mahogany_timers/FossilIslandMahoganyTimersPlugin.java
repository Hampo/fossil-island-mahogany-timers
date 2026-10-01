package org.zhbot.fossil_island_mahogany_timers;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.*;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Fossil Island Mahogany Timers",
	description = "A plugin to add respawn timers to the Fossil Island mahogany trees.",
	tags = {"fossil", "island", "farming", "hardwood", "mahogany"}
)
public class FossilIslandMahoganyTimersPlugin extends Plugin
{
	private static final WorldArea FOSSIL_ISLAND_HARDWOOD_AREA = new WorldArea(3695, 3789, 49, 53, 0);
	private static final Set<Integer> FOSSIL_ISLAND_HARDWOOD_PATCH_IDS = Set.of(
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_1,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_2,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_3
		);
	private static final String INVENTORY_FULL_MESSAGE = "Your inventory is too full to hold any more mahogany logs.";

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	@Inject
	private FossilIslandMahoganyTimersConfig config;

	@Inject
	private FossilIslandMahoganyTimersOverlay overlay;

	@Getter
	private boolean inFossilIslandHardwoodArea = false;

	@Getter
	private final Set<HardwoodPatch> fossilIslandHardwoodPatches = new HashSet<>();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOADING)
			return;

		fossilIslandHardwoodPatches.clear();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final var player = client.getLocalPlayer();
		if (player == null)
		{
			inFossilIslandHardwoodArea = false;
			return;
		}

		final var location = player.getWorldLocation();
		if (location == null)
		{
			inFossilIslandHardwoodArea = false;
			return;
		}

		inFossilIslandHardwoodArea = FOSSIL_ISLAND_HARDWOOD_AREA.contains2D(location);

		for (final var patch : fossilIslandHardwoodPatches)
			patch.onGameTick();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		final var object = event.getGameObject();
		if (object == null)
			return;

		if (!FOSSIL_ISLAND_HARDWOOD_PATCH_IDS.contains(object.getId()))
			return;

		fossilIslandHardwoodPatches.add(new HardwoodPatch(client, notifier, config, object));
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		final var object = event.getGameObject();
		if (object == null)
			return;

		if (!FOSSIL_ISLAND_HARDWOOD_PATCH_IDS.contains(object.getId()))
			return;

		fossilIslandHardwoodPatches.removeIf(x -> x.getObject() == object);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!inFossilIslandHardwoodArea)
			return;

		if (!config.notifyOnFull())
			return;

		if (event.getType() != ChatMessageType.GAMEMESSAGE)
			return;

		if (!event.getMessage().equals(INVENTORY_FULL_MESSAGE))
			return;

		notifier.notify(config.notifications(), "Inventory is full of mahogany logs.");
	}

	@Provides
	FossilIslandMahoganyTimersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FossilIslandMahoganyTimersConfig.class);
	}
}
