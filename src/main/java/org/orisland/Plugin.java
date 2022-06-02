package org.orisland;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.orisland.Command.JokeCommand;

import static org.orisland.DataInit.init;

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
        getLogger().info("Jeff loaded!");

        init();

        CommandManager.INSTANCE.registerCommand(JokeCommand.INSTANCE, false);

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}