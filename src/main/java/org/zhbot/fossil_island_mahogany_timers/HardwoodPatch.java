package org.zhbot.fossil_island_mahogany_timers;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;

public class HardwoodPatch {
    private static final long RESPAWN_MILLIS = 40 * 600;
    private static final int GROWN_STATE = 39;
    private static final int STUMP_STATE = 40;

    private final Notifier notifier;
    private final FossilIslandMahoganyTimersConfig config;

    @Getter
    private final GameObject object;
    private final int varbitID;

    private int lastState = -1;
    private long treeStumpSpawnTime = -1;

    public HardwoodPatch(final Client client, final Notifier notifier, final FossilIslandMahoganyTimersConfig config, final GameObject object)
    {
        this.notifier = notifier;
        this.config = config;
        this.object = object;

        final var objectComposition = client.getObjectDefinition(object.getId());
        varbitID = objectComposition.getVarbitId();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (event.getVarbitId() != varbitID)
            return;

        final var state = event.getValue();

        if (state != STUMP_STATE)
        {
            treeStumpSpawnTime = -1;
        }
        else
        {
            treeStumpSpawnTime = System.currentTimeMillis();
            if (lastState == GROWN_STATE)
                notifier.notify(config.notifyOnChop(), "Mahogany tree chopped.");
        }

        lastState = state;
    }

    public double getProgress()
    {
        if (treeStumpSpawnTime == -1)
            return -1;

        final var ms = System.currentTimeMillis() - treeStumpSpawnTime;
        return (double)ms / RESPAWN_MILLIS;
    }
}
