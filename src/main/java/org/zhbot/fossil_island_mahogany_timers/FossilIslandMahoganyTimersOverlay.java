package org.zhbot.fossil_island_mahogany_timers;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class FossilIslandMahoganyTimersOverlay extends Overlay {
    private final Client client;
    private final FossilIslandMahoganyTimersPlugin plugin;
    private final FossilIslandMahoganyTimersConfig config;

    @Inject
    private FossilIslandMahoganyTimersOverlay(Client client, FossilIslandMahoganyTimersPlugin plugin, FossilIslandMahoganyTimersConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isInFossilIslandHardwoodArea())
            return null;

        final var patches = plugin.getFossilIslandHardwoodPatches();
        if (patches.isEmpty())
            return null;

        final var progressPieComponent = new ProgressPieComponent();
        progressPieComponent.setFill(config.progressColour());
        progressPieComponent.setBorderColor(config.progressColour().darker());

        for (final var patch : patches)
        {
            final var progress = patch.getProgress();
            if (progress < 0)
                continue;

            final var object = patch.getObject();
            final var localPoint = object.getLocalLocation();
            final var point = Perspective.localToCanvas(client, localPoint, object.getPlane(), 0);
            if (point == null)
                continue;

            progressPieComponent.setProgress(progress);
            progressPieComponent.setPosition(point);
            progressPieComponent.render(graphics);
        }

        return null;
    }
}
