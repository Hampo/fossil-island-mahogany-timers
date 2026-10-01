package org.zhbot.fossil_island_mahogany_timers;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.Notifier;

public class HardwoodPatch {
    private static final long RESPAWN_MILLIS = 40 * 600;

    private final Notifier notifier;
    private final FossilIslandMahoganyTimersConfig config;
    @Getter
    private final GameObject object;

    private final ObjectComposition objectComposition;
    private int lastImposterId = -1;
    private long treeStumpSpawnTime = -1;

    public HardwoodPatch(final Client client, final Notifier notifier, final FossilIslandMahoganyTimersConfig config, final GameObject object)
    {
        this.notifier = notifier;
        this.config = config;
        this.object = object;

        this.objectComposition = client.getObjectDefinition(object.getId());
    }

    public void onGameTick()
    {
        final var imposter = objectComposition.getImpostor();
        if (imposter == null)
        {
            lastImposterId = -1;
            return;
        }

        final var imposterId = imposter.getId();
        if (imposterId == lastImposterId)
            return;

        if (imposterId != ObjectID.MAHOGANY_TREE_STUMP)
        {
            treeStumpSpawnTime = -1;
        }
        else
        {
            treeStumpSpawnTime = System.currentTimeMillis();
            if (lastImposterId == ObjectID.MAHOGANY_TREE_FULLYGROWN)
                notifier.notify(config.notifyOnChop(), "Mahogany tree chopped.");
        }

        lastImposterId = imposterId;
    }

    public double getProgress()
    {
        if (treeStumpSpawnTime == -1)
            return -1;

        final var ms = System.currentTimeMillis() - treeStumpSpawnTime;
        return (double)ms / RESPAWN_MILLIS;
    }
}
