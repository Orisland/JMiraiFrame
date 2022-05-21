package org.orisland;

import Tool.FileTool;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

import static org.orisland.wows.DataInit.init;

@Slf4j
public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();
    private Plugin() {
//        引入日志插件前置
        super(new JvmPluginDescriptionBuilder("org.orisland.plugin", "0.10")
                .name("Wows")
                .author("Orisland")
                .dependsOn("net.mamoe.mirai-slf4j-bridge", true)
                .build());
    }


    @SneakyThrows
    @Override
    public void onEnable() {
        log.info("Wows Plugin Loaded!");
        init();

        CommandManager.INSTANCE.registerCommand(Mycommand.INSTANCE, false);

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}