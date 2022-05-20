package org.orisland;

import Tool.FileTool;
import lombok.SneakyThrows;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;


public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("org.orisland.plugin", "1.0-SNAPSHOT")
                .name("Pic")
                .author("Orisland")
                .build());
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        getLogger().info("Wows Plugin Loaded!");

        CommandManager.INSTANCE.registerCommand(Mycommand.INSTANCE, false);
        getLogger().info(FileTool.initFile() ? "初始化完成" : "无需初始化");

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}