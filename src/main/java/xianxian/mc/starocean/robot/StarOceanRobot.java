
package xianxian.mc.starocean.robot;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class StarOceanRobot extends Module implements Listener {
    private static String splitPattern = "[\\[\\]]";
    private static String hello = "";

    public StarOceanRobot(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("新手引导机器人(小星^_^)");
    }

    @EventHandler
    public void onChatReceived(AsyncPlayerChatEvent event) {
        String text = event.getMessage();
        String name = event.getPlayer().getDisplayName();
        text = text.trim();
        String question = null;
        if (text.startsWith("小星星")) {
            question = text.replace("小星星", "");
        } else if (text.startsWith("@小星星")) {
            question = text.replace("@小星星", "");
        } else if (text.startsWith("@小星")) {
            question = text.replace("@小星", "");
        } else if (text.startsWith("小星")) {
            question = text.replace("小星", "");
        } else {
            return;
        }
        question = question.trim();

        if (question.length() == 0) {
            XiaoIRobot.ask(name, "你好", (response) -> {
                if (response != null) {
                    String content = response.getContent();
                    if (content.startsWith("/")) {
                        getMessager().broadcastMessage(new TextComponent("检测到回答中含有会被执行的指令，已过滤"));
                        content = content.replace("/", "");
                    }
                    getMessager().broadcastMessage(new TextComponent(parseResponse(content)));
                } else {
                    getMessager().broadcastMessage(new TextComponent("我在，有事吗"));
                }
            });
            return;
        }
        XiaoIRobot.ask(name, question, (response) -> {
            if (response != null) {
                String content = response.getContent();
                if (content.equals("默认回复")) {
                    getMessager().broadcastMessage(new TextComponent("问的速度太快了QAQ"));
                    return;
                }
                if (content.startsWith("/")) {
                    getMessager().broadcastMessage(new TextComponent("检测到回答中含有会被执行的指令，已过滤"));
                    content = content.replace("/", "");
                }
                getMessager().broadcastMessage(new TextComponent(parseResponse(content)));
            } else {
                getMessager().broadcastMessage(new TextComponent("操作失败QAQ"));
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            TextComponent component = new TextComponent("欢迎新玩家" + event.getPlayer().getDisplayName() + "加入星海生存服务器");
            component.setColor(ChatColor.LIGHT_PURPLE);
            component.setUnderlined(true);
            component.setBold(true);
            getMessager().broadcastMessage(component);
        }
        String person = event.getPlayer().getDisplayName();
        XiaoIRobot.ask(person, "欢迎语", (response) -> {
            if (response != null && !response.getContent().equals("重复回复")) {
                String content = response.getContent();
                hello = content;
                TextComponent part1 = new TextComponent("Hi！" + person + " ");
                part1.setColor(ChatColor.GOLD);
                TextComponent component = new TextComponent();
                component.addExtra(part1);
                BaseComponent[] parts = parseResponse(hello);
                for (BaseComponent part : parts) {
                    component.addExtra(part);
                }
                getMessager().sendMessageTo(event.getPlayer(), component);
            } else {
                TextComponent part1 = new TextComponent("Hi！" + person + " ");
                part1.setColor(ChatColor.GOLD);
                TextComponent component = new TextComponent();
                component.addExtra(part1);
                BaseComponent[] parts = parseResponse(hello);
                for (BaseComponent part : parts) {
                    component.addExtra(part);
                }
                getMessager().sendMessageTo(event.getPlayer(), component);

            }
        });
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        CommandRobot staroceanrobot = new CommandRobot(this);
        staroceanrobot.registerDefaultPermission();
        plugin.getCommandManager().registerCommand(staroceanrobot);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

    /* package */ static BaseComponent[] parseResponse(String content) {
        String[] parts = content.split(splitPattern);
        List<BaseComponent> components = Lists.newArrayList();
        for (String part : parts) {
            if (part.startsWith("suggest=")) {
                part = part.substring(8);

                if (part.startsWith("小星"))
                    part = part.substring(2);
                else if (part.startsWith("@小星"))
                    part = part.substring(3);
                part = part.trim();

                TextComponent textComponent = new TextComponent(part);
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "小星 " + part));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[] { new TextComponent(new TextComponent("点击对小星发起提问")) }));
                textComponent.setColor(ChatColor.LIGHT_PURPLE);
                textComponent.setBold(true);
                components.add(textComponent);
                continue;
            }

            TextComponent textComponent = new TextComponent(part);
            textComponent.setColor(ChatColor.GREEN);
            components.add(textComponent);
        }

        return components.toArray(new BaseComponent[0]);
    }
}
