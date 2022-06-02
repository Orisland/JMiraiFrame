package org.orisland;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("org.orisland.Jeff", "1.0")
                .name("Jeff")
                .author("Orisland")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");

        CommandManager.INSTANCE.registerCommand(Mycommand.INSTANCE, false);

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}