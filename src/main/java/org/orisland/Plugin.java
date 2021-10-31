package org.orisland;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.PluginFileExtensions;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

//import static Tool.OSSTool.OSSset;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("org.orisland.plugin", "0.3")
                .name("Pic")
                .author("Orisland")
                .build());
    }

//    @SneakyThrows
    @Override
    public void onEnable() {
        System.out.println(Plugin.INSTANCE.getConfigFolderPath());
        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());


        CommandManager.INSTANCE.registerCommand(Mycommand.INSTANCE, false);


    }
}