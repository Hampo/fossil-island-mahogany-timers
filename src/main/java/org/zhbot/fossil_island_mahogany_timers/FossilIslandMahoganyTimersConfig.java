package org.zhbot.fossil_island_mahogany_timers;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(FossilIslandMahoganyTimersConfig.group)
public interface FossilIslandMahoganyTimersConfig extends Config
{
	String group = "fossil-island-mahogany-timers";

	@ConfigItem(
			keyName = "notifyOnChop",
			name = "Notify on Chop",
			description = "Send a client notification when tree chopped.",
			position = 0
	)
	default Notification notifyOnChop()
	{
		return Notification.ON;
	}

	@ConfigItem(
			keyName = "notifyOnFull",
			name = "Notify on Full",
			description = "Send notification when inventory is full.",
			position = 1
	)
	default Notification notifyOnFull()
	{
		return Notification.ON;
	}

	@Alpha
	@ConfigItem(
			keyName = "progressColour",
			name = "Colour",
			description = "The colour for the progress pie chart.",
			position = 2
	)
	default Color progressColour()
	{
		return Color.GREEN;
	}
}
