package org.orisland.wows;

import Tool.FileTool;
import Tool.HttpClient;
import Tool.JsonTool;
import Tool.YmlTool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.WowsPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static Tool.YmlTool.ReadYamlToBoolean;
import static Tool.YmlTool.ReadYamlToString;
import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.dataPack.PlayerData.updateAccountLocalDataAuto;
import static org.orisland.wows.dataPack.ShipData.saveShipInfo;

@Slf4j
public class DataInit {
    //配置
    private static final String config = configDir + "config.yml";

    /**
     * 插件初始化
     */
    public static void init(){
        if (CronUtil.getScheduler().isStarted())
            CronUtil.stop();
        initFile();
        initRetry();
        initAppId();
        initShipExpectedUpdate();
        initShipExpectedData();
        initApiLanguage();
        initShipInfo();
        initBind();
        initDataRefresh();
        initMaxSaveData();
        initAdmin();
        log.info("wows插件配置文件装载完成!");
    }

    /**
     * 配置文件初始化
     */
    public static void initFile(){
        log.info(FileTool.newFile(dataDir + "playerData" + File.separator) ? "创建用户数据文件夹!" : "用户数据文件夹已存在!");
        if (!FileTool.fileExists(configDir + "config.yml")){
            log.info("wows配置文件初始化!");
            log.info("请关闭bot填入appid后重启!");
            InputStream resourceAsStream = WowsPlugin.class.getClassLoader().getResourceAsStream("config.yml");
            FileUtil.writeFromStream(resourceAsStream, configDir + "config.yml");
            FileTool.newFile(dataDir + "playerData" + File.separator + "asia");
            FileTool.newFile(dataDir + "playerData" + File.separator + "eu");
            FileTool.newFile(dataDir + "playerData" + File.separator + "na");
            FileTool.newFile(dataDir + "playerData" + File.separator + "ru");
            FileTool.newFile(dataDir + "playerData" + File.separator + "origin");
            FileUtil.writeUtf8String("{}", dataDir + "Bind.json");
        }
    }

    /**
     * 获取配置文件中的appid
     */
    public static void initAppId(){
        String appid = YmlTool.ReadYamlToString(config, "appid");
        if (!appid.equals("") && !appid.equals("null")){
            ApiConfig.APPID = appid;
            log.info("appid:{}初始化完成!",appid);
        }else {
            log.error("错误!appid为空,Wows plugin初始化失败!");
        }
    }

    public static void initShipExpectedUpdate(){
        if (ReadYamlToBoolean(config, "updateShipExpected")){
            log.info("开始更新船只期望数据！");
            int count = 0;
            while (count <= reTry){
                try {
                    initExpectData();
                    return;
                }catch (Exception e){
                    log.info("异常计数:{}", ++count);
                }
            }

        }else {
            log.info("船只期望数据更新已跳过！");
        }
    }

    /**
     * 船只pr期望数据
     */
    public static void initExpectData() {
        JsonNode urlByJson = null;
        try {
            urlByJson = HttpClient.getUrlByJson(ApiConfig.Wows_Pr);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (new File(dataDir + File.separator + "ShipExpected.json").exists()){
            FileUtil.del(dataDir + File.separator + "ShipExpected.json");
            FileTool.saveFile(urlByJson, dataDir, "ShipExpected.json");
        }else {
            FileTool.saveFile(urlByJson, dataDir, "ShipExpected.json");
        }

        log.info("船只预期数据已装载！");
    }

    /**
     * 读取本地的船只期望数据
     */
    public static void initShipExpectedData(){
        try {
            ApiConfig.ShipExpected = JsonTool.mapper.readTree(FileUtil.readString(dataDir + File.separator + "ShipExpected.json", StandardCharsets.UTF_8));
            log.info("船只预期数据更新时间为:{}", DateUtil.format(DateUtil.date(ApiConfig.ShipExpected.get("time").asLong() * 1000), "YYYY-MM-dd"));
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        log.info("本地船只预期数据加载完成！");
    }

    /**
     * 自定义api语言
     * 默认语言为英语
     * ps:汉语是动物园不建议使用汉语
     */
    public static void initApiLanguage(){
        String apiLanguage = YmlTool.ReadYamlToString(config, "apiLanguage");
        if (apiLanguage.equals("")){
            log.warn("初始化语言失败，使用默认英语！");
        }else {
            ApiConfig.API_LANGUAGE = apiLanguage;
            log.info("api语言配置为:{}", API_LANGUAGE);
        }
    }

    /**
     * 导入本地数据
     */
    public static void initShipInfo(){
        boolean useLocalShipInfo = ReadYamlToBoolean(config, "useLocalShipInfo");
        if (useLocalShipInfo){
            try {
                if (FileUtil.exist(dataDir + "ships_cn.json")){
                    LocalShipInfo = JsonTool.mapper.readTree(FileUtil.readUtf8String(dataDir + "ships_cn.json")).get("data");
                }else {
                    InputStream resourceAsStream = WowsPlugin.class.getClassLoader().getResourceAsStream("ships_cn.json");
                    String s = IoUtil.readUtf8(resourceAsStream);
                    LocalShipInfo = JsonTool.mapper.readTree(s).get("data");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("本地ship_info加载完成!已导入{}条船数据！", LocalShipInfo.size());
        }else {
            log.info("本地ship_info加载取消!所有船只数据将从官方获取！");
        }
    }

    /**
     * 初始化qq号与accountid与区服的绑定数据
     */
    public static void initBind(){
        boolean useBind = ReadYamlToBoolean(config, "useBind");
        if (useBind){
            try {
                Bind = JsonTool.mapper.readTree(FileUtil.readUtf8String(dataDir + "Bind.json"));
                log.info("绑定数据已读入！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            log.info("绑定读入被禁止！");
    }

    /**
     * 自动数据刷新
     */
    public static void initDataRefresh(){
        boolean refreshData = ReadYamlToBoolean(config, "refreshData");
        if (refreshData){
            String refreshTime = YmlTool.ReadYamlToString(config, "refreshTime");
            int hour = Integer.parseInt(refreshTime.split(":")[0]);
            int minute = Integer.parseInt(refreshTime.split(":")[1]);
            String command = String.format("%s %s * * *", minute, hour);
            CronUtil.schedule(command, new Task() {
                @Override
                public void execute() {
                    int count = 0;
                    while (count <= 20){
                        try {
                            if (ReadYamlToBoolean(config, "updateShipInfoAuto"))
                                saveShipInfo();
                            updateAccountLocalDataAuto();
                            initShipExpectedUpdate();
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                            log.error("第{}次访问错误!", ++count);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }

                }
            });
            CronUtil.start();
            log.info("数据刷新定时任务:{}:{}已启动！", hour, minute);
        }else {
            log.info("自动数据刷新初始化被跳过！");
        }
    }

    /**
     * 初始化最大数据保存量
     */
    public static void initMaxSaveData(){
        Long maxPlayerData = Long.valueOf(ReadYamlToString(config, "maxPlayerData"));
        maxSavePlayerData = maxPlayerData.intValue();
        log.info("最大玩家数据存储上限:{}",maxPlayerData);
    }

    /**
     * 配置最大重试次数
     */
    public static void initRetry(){
        Long retry = Long.valueOf(ReadYamlToString(config, "retry"));
        reTry = retry;
        log.info("最大数据读取重试次数:{}", retry);
    }

    /**
     * 配置管理员
     */
    public static void initAdmin(){
        String admin = ReadYamlToString(config, "admin");
        if (admin.equals("")){
            log.info("管理员为空！");
        }else {
            Admin = admin.split(",");
            log.info("管理员已配置{}", admin);
        }

    }
}