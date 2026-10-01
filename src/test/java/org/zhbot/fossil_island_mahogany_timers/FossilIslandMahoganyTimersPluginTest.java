package org.zhbot.fossil_island_mahogany_timers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FossilIslandMahoganyTimersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FossilIslandMahoganyTimersPlugin.class);
		RuneLite.main(args);
	}
}