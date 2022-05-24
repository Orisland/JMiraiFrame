package org.orisland;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.orisland.wows.command.Account;
import org.orisland.wows.command.Bind;
import org.orisland.wows.command.PluginController;

import static org.orisland.wows.DataInit.init;

@Slf4j
public final class WowsPlugin extends JavaPlugin {
    public static final WowsPlugin INSTANCE = new WowsPlugin();
    private WowsPlugin() {
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

        CommandManager.INSTANCE.registerCommand(Account.INSTANCE, false);
        CommandManager.INSTANCE.registerCommand(Bind.INSTANCE, false);
        CommandManager.INSTANCE.registerCommand(PluginController.INSTANCE, false);

        GlobalEventChannel.INSTANCE.registerListenerHost(new Handler());
    }
}