package org.orisland.wows;

import Tool.FileTool;
import Tool.HttpClient;
import Tool.JsonTool;
import Tool.YmlTool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static Tool.YmlTool.ReadYamlToBoolean;
import static org.orisland.wows.ApiConfig.*;

@Slf4j
public class DataInit {
    //配置
    private static final String config = configDir + "config.yml";

    /**
     * 插件初始化
     */
    public static void init(){
        initFile();
        initAppId();
        initShipExpectedUpdate();
        initShipExpectedData();
        initApiLanguage();
        initShipInfo();
        initBind();
        dataRefresh();
    }

    /**
     * 配置文件初始化
     */
    public static void initFile(){
        log.info(FileTool.newFile(dataDir + "playerData" + File.separator) ? "创建用户数据文件夹!" : "用户数据文件夹已存在!");
        if (!FileTool.fileExists(configDir + "config.yml")){
            log.info("wows配置文件初始化!");
            log.info("请关闭bot填入appid后重启!");
            FileUtil.copy(String.valueOf(ResourceUtil.getResource("config.yml")), configDir, true);
            FileTool.newFile(dataDir + "playerData" + File.separator);
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
            initExpectData();
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
                LocalShipInfo = JsonTool.mapper.readTree(FileUtil.readString(String.valueOf(ResourceUtil.getResource("ships_cnFix.json").toString()), StandardCharsets.UTF_8));
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
    public static void dataRefresh(){
        boolean refreshData = ReadYamlToBoolean(config, "refreshData");
        if (refreshData){
            String refreshTime = YmlTool.ReadYamlToString(config, "refreshTime");
            int hour = Integer.parseInt(refreshTime.split(":")[0]);
            int minute = Integer.parseInt(refreshTime.split(":")[1]);
            String command = String.format("%s %s * * *", minute, hour);
            CronUtil.schedule(command, new Task() {
                @Override
                public void execute() {
                    System.out.println("az");
                }
            });
            CronUtil.start();
            log.info("定时任务:{}:{}已启动！", hour, minute);
        }else {
            log.info("自动数据刷新初始化被跳过！");
        }
    }


}