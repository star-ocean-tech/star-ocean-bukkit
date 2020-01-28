package xianxian.mc.starocean.autobroadcast;

import java.util.List;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.BaseComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.MessageManager;

public class Messager extends MessageManager {
    private BaseComponent prefix;

    public Messager(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    protected List<BaseComponent> getPrefix() {
        return Lists.newArrayList(prefix);
    }

    /* package */ void setPrefix(BaseComponent prefix) {
        this.prefix = prefix;
    }

}
