package org.orisland;


import Tool.fileTool;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

import static net.mamoe.mirai.utils.LoggerAdapters.useLog4j2;

@Slf4j
public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("org.orisland.plugin", "1.0-SNAPSHOT")
                .name("Pic")
                .author("Orisland")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        useLog4j2();

        //给玩家创建文件夹
        log.info(fileTool.initFile() ? "初始化完成" : "无需初始化，或初始化失败.");

        CommandManager.INSTANCE.registerCommand(Mycommand.INSTANCE, false);

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}