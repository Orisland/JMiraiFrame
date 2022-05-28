package org.orisland.wows.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.orisland.WowsPlugin;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;

import static org.orisland.wows.dataPack.BindData.bindQQAccountId;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.StringToMeaningful.StringToServer;

@Slf4j
public class Bind extends JCompositeCommand {

    public static final Bind INSTANCE = new Bind();
    public static final ObjectMapper mapper =  new ObjectMapper();

    private Bind(){
        super(WowsPlugin.INSTANCE, "wws-bind", new String[]{"wb"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"bindid", "bi"})
    @Description("绑定用户通过id")
    public void bindId(CommandSenderOnMessage sender, String accountid, String StringServer) throws JsonProcessingException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        MessageChain chain = null;
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        ApiConfig.Server server = StringToServer(StringServer);
        StringBuilder stringBuilder = new StringBuilder();

        int count = 0;
        while (count <= ApiConfig.reTry){
            try {
                org.orisland.wows.doMain.Bind bind = findAccountId(qq);
                if (server == null){
                    chain = new MessageChainBuilder()
                            .append(new PlainText("区服错误！"))
                            .append(quoteReply)
                            .build();
                    sender.sendMessage(chain);
                    return;
                }

                SinglePlayer singlePlayer = AccountIdToAccountInfo(accountid, server);
                if (singlePlayer == null){
                    chain = new MessageChainBuilder()
                            .append(new PlainText(String.format("uid:%s不存在！", accountid)))
                            .append(quoteReply)
                            .build();
                }else {
                    if (bind != null){
                        chain = new MessageChainBuilder()
                                .append(String.format("该用户已经绑定账号[%s]%s-%s！", bind.getServer(), bind.getAccountName(), bind.getAccountId()))
                                .append(quoteReply)
                                .build();
                    }else {
                        bindQQAccountId(qq, String.valueOf(singlePlayer.getAccount_id()), server);
                        chain = new MessageChainBuilder()
                                .append(String.format("绑定账号[%s]%s-%s完成！", server, singlePlayer.getNickname(), singlePlayer.getAccount_id()))
                                .append(quoteReply)
                                .build();
                    }
                }
                sender.sendMessage(chain);
                return;
            }catch (Exception e){
                stringBuilder.append(e.getMessage());
                log.error("访问出错:{}", ++count);
            }
        }


    }

    @SubCommand({"bindname", "bn", "绑定"})
    @Description("绑定用户通过name")
    public void bindName(CommandSenderOnMessage sender, String accountName, String StringServer){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        ApiConfig.Server server = StringToServer(StringServer);

        StringBuilder stringBuilder = new StringBuilder();

        int count = 0;
        while (count <= ApiConfig.reTry){
            try {
                if (server == null){
                    chain = new MessageChainBuilder()
                            .append(new PlainText("区服错误！"))
                            .append(quoteReply)
                            .build();
                    sender.sendMessage(chain);
                    return;
                }

                SinglePlayer singlePlayer = NickNameToAccountInfo(accountName, server);
                org.orisland.wows.doMain.Bind bind = findAccountId(qq);

                if (singlePlayer == null){
                    chain = new MessageChainBuilder()
                            .append(new PlainText("用户名不存在！"))
                            .append(quoteReply)
                            .build();
                }else {
                    if (bind != null){
                        chain = new MessageChainBuilder()
                                .append(new PlainText(String.format("该用户已经绑定账号[%s]%s-%s！", bind.getServer(), bind.getAccountName(), bind.getAccountId())))
                                .append(quoteReply)
                                .build();
                    }else {
                        if (findBindAccountId(String.valueOf(singlePlayer.getAccount_id()), qq)){
                            bindQQAccountId(qq, String.valueOf(singlePlayer.getAccount_id()), server);
                            chain = new MessageChainBuilder()
                                    .append(new PlainText(String.format("绑定账号[%s]%s-%s完成！", server, singlePlayer.getNickname(), singlePlayer.getAccount_id())))
                                    .append(quoteReply)
                                    .build();
                        }else {
                            chain = new MessageChainBuilder()
                                    .append(String.format("[%s]%s-%s已被绑定，请核实账号信息！", server, singlePlayer.getNickname(), singlePlayer.getAccount_id()))
                                    .append(quoteReply)
                                    .build();
                        }
                    }
                }
                sender.sendMessage(chain);
                return;
            }catch (Exception e){
                e.printStackTrace();
                stringBuilder.append(e.getMessage());
                log.error("访问出错:{}", ++count);
            }
        }

        chain = new MessageChainBuilder()
                .append("绑定出错！")
                .append("\r")
                .append(stringBuilder)
                .append(quoteReply)
                .build();
        sender.sendMessage(chain);
    }

    @SubCommand({"updatebind", "ub", "更新绑定"})
    @Description("更新绑定")
    public void updateBind(CommandSenderOnMessage sender, String accountName, String StringServer){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        ApiConfig.Server server = StringToServer(StringServer);
        StringBuilder stringBuilder = new StringBuilder();

        int count = 0;
        while (count <= ApiConfig.reTry){
            org.orisland.wows.doMain.Bind bind = findAccountId(qq);
            if (server == null){
                chain = new MessageChainBuilder()
                        .append(new PlainText("区服错误！"))
                        .append(quoteReply)
                        .build();
                sender.sendMessage(chain);
                return;
            }
            try {
                SinglePlayer singlePlayer = NickNameToAccountInfo(accountName, server);

                if (singlePlayer == null){
                    chain = new MessageChainBuilder()
                            .append(new PlainText("用户名不存在！"))
                            .append(quoteReply)
                            .build();
                }else {
                    if (bind == null){
                        chain = new MessageChainBuilder()
                                .append("该用户未绑定任何账号！")
                                .append(quoteReply)
                                .build();
                    }else {
                        if (findBindAccountId(String.valueOf(singlePlayer.getAccount_id()), qq)){
                            bindQQAccountId(qq, String.valueOf(singlePlayer.getAccount_id()), server);
                            chain = new MessageChainBuilder()
                                    .append(new PlainText(String.format("绑定账号[%s]%s-%s完成！", server, singlePlayer.getNickname(), singlePlayer.getAccount_id())))
                                    .append(quoteReply)
                                    .build();
                        }else {
                            chain = new MessageChainBuilder()
                                    .append(String.format("[%s]%s-%s已被绑定，请核实账号信息！", server, singlePlayer.getNickname(), singlePlayer.getAccount_id()))
                                    .append(quoteReply)
                                    .build();
                        }
                    }
                }
                sender.sendMessage(chain);
                return;

            }catch (Exception e){
                stringBuilder.append(e.getMessage());
                log.error("访问出错:{}", ++count);
            }
        }

        chain = new MessageChainBuilder()
                .append("绑定出错！")
                .append("\r")
                .append(stringBuilder)
                .append(quoteReply)
                .build();
        sender.sendMessage(chain);
    }
}
