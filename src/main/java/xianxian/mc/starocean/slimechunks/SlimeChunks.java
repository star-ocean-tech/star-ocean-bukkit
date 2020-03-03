package xianxian.mc.starocean.slimechunks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.vault.VaultFeatures;

public class SlimeChunks extends Module {
    private boolean costEnabled;
    private double costValue;
    private int range;

    private VaultFeatures vault = null;

    private final Map<Player, BukkitRunnable> scheduledSearches = new HashMap<>();

    public SlimeChunks(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        if (getPlugin().getModuleManager().isModuleLoaded(VaultFeatures.class)) {
            vault = getPlugin().getModuleManager().getLoadedModule(VaultFeatures.class);
        } else {
            costEnabled = false;
        }
        return true;
    }

    @Override
    public void prepare() {
        reload();

        CommandSlimeChunks slimechunks = new CommandSlimeChunks(this);
        getPlugin().getCommandManager().registerCommand(slimechunks);
    }

    @Override
    public void disable() {

    }

    public void search(Player player) {
        Questioner questioner = new Questioner(this, player, new Callback() {

            @Override
            public void onYes() {
                if (costEnabled) {
                    Economy economy = vault.getVaultEconomy();
                    if (economy.has(player, costValue)) {
                        if (!ensureResponse(economy.withdrawPlayer(player, costValue))) {
                            getMessager().sendMessageTo(player, ChatColor.RED + "在付款时出现了异常，请联系管理");
                            return;
                        }
                    } else {
                        getMessager().sendMessageTo(player, ChatColor.RED + "你没有足够的金额");
                        return;
                    }
                }
                int totalCount = ((range + 1) * (range + 1)) * 4;
                Chunk center = player.getLocation().getChunk();
                getMessager().sendMessageTo(player, ChatColor.GREEN + "尝试在以"+center.getX()+", "+center.getZ()+"为中心的" + totalCount + "个区块中搜索史莱姆区块");
                SearchRunnable runnable = new SearchRunnable(SlimeChunks.this, player, center,
                        range, (r) -> {
                            getMessager().sendMessageTo(player,
                                    ChatColor.BLACK + "==================");
                            r.getLines().forEach(s -> getMessager().sendMessageTo(player, s));
                            getMessager().sendMessageTo(player,
                                    ChatColor.BLACK + "==================");
                            getMessager().sendMessageTo(player,
                                    ChatColor.GREEN + "在" + totalCount + "个区块中找到了" + r.getCount() + "个史莱姆区块");
                        });
                runnable.runTaskAsynchronously(getPlugin());
            }

            @Override
            public void onNo() {
                getMessager().sendMessageTo(player,
                        ChatColor.GREEN + "已取消搜索");
            }
        });
        questioner.startConversation();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitRunnable runnable = this.scheduledSearches.remove(event.getPlayer());
        if (runnable != null && !runnable.isCancelled()) {
            runnable.cancel();
        }
    }

    private boolean ensureResponse(EconomyResponse response) {
        if (!response.transactionSuccess())
            logger().severe("Transaction failed because " + response.errorMessage + ", amount: " + response.amount
                    + ", balance: " + response.balance);
        return response.transactionSuccess();
    }

    @Override
    public void reload() {
        reloadConfig();
        FileConfiguration config = getConfig();
        config.addDefault("cost.enabled", true);
        config.addDefault("cost.value", 15000D);
        config.addDefault("range", 6);
        saveConfig();

        this.costEnabled = config.getBoolean("cost.enabled");
        this.costValue = config.getDouble("cost.value");
        this.range = config.getInt("range");

        if (vault == null && costEnabled) {
            logger().severe("Unable to find Vault, cost disabled");
            costEnabled = false;
        }

    }

    private class SearchRunnable extends BukkitRunnable {
        private final SlimeChunks module;
        private final Player player;
        private final Chunk center;
        private int count;
        private final int range;

        private final World world;
        private final Complete callback;

        private final int startX;
        private final int startZ;
        private final int endX;
        private final int endZ;

        public SearchRunnable(SlimeChunks module, Player player, Chunk center, int range, Complete callback) {
            this.module = module;
            this.player = player;
            this.center = center;
            this.range = range;
            this.world = center.getWorld();
            this.callback = callback;

            this.startX = center.getX() - range;
            this.startZ = center.getZ() - range;
            this.endX = center.getX() + range;
            this.endZ = center.getZ() + range;
        }

        @Override
        public void run() {
            List<String> lines = new ArrayList<>();

            for (int z = startZ; z <= endZ; z++) {
                StringBuilder builder = new StringBuilder();
                for (int x = startX; x <= endX; x++) {
                    Chunk chunk = this.world.getChunkAtAsync(x, z).join();
                    boolean isCenter = x == center.getX() && z == center.getZ();
                    if (chunk.isSlimeChunk()) {
                        if (isCenter)
                            builder.append(ChatColor.GREEN).append(ChatColor.BOLD).append(ChatColor.STRIKETHROUGH).append(ChatColor.UNDERLINE).append("+");
                        else
                            builder.append(ChatColor.GREEN).append("+");
                        count++;
                    } else
                        if (isCenter)
                            builder.append(ChatColor.GRAY).append(ChatColor.BOLD).append(ChatColor.STRIKETHROUGH).append(ChatColor.UNDERLINE).append("+");
                        else
                            builder.append(ChatColor.GRAY).append("+");
                            
                }
                lines.add(builder.toString());
            }

            module.getPlugin().getServer().getScheduler().callSyncMethod(module.getPlugin(), () -> {
                callback.complete(new Result(lines, count));
                return null;
            });
        }
    }

    public interface Complete {
        void complete(Result result);
    }

    public static class Result {
        private final List<String> lines;
        private final int count;

        public Result(List<String> lines, int count) {
            super();
            this.lines = lines;
            this.count = count;
        }

        public List<String> getLines() {
            return lines;
        }

        public int getCount() {
            return count;
        }

    }

    public class Questioner {
        private Module module;
        private Player player;
        private Callback callback;

        public Questioner(Module module, Player player, Callback callback) {
            this.module = module;
            this.player = player;
            this.callback = callback;
        }

        public void startConversation() {
            Conversation conversation = new ConversationFactory(module.getPlugin()).withFirstPrompt(new AskPrompt())
                    .addConversationAbandonedListener((event) -> {
                        Object oValid = event.getContext().getSessionData("Yes");
                        if (oValid != null && oValid.equals(true)) {
                            callback.onYes();
                        } else {
                            callback.onNo();
                        }
                    }).buildConversation(player);
            conversation.begin();
        }

        public class AskPrompt extends ValidatingPrompt {

            @Override
            @NotNull
            public String getPromptText(@NotNull ConversationContext context) {
                return ChatColor.GREEN + "搜索一次将会花费" + vault.getVaultEconomy().format(costValue)
                        + ", 输入\"yes\"开始搜索，或输入\"no\"取消";
            }

            @Override
            protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
                try {
                    if (input.equalsIgnoreCase("yes")) {
                        context.setSessionData("Yes", true);
                        return true;
                    }

                    if (input.equalsIgnoreCase("no")) {
                        context.setSessionData("Yes", false);
                        return true;
                    }

                } catch (Exception e) {
                    return false;
                }
                return false;
            }

            @Override
            protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context,
                    @NotNull String input) {
                return Prompt.END_OF_CONVERSATION;
            }
        }
    }

    public interface Callback {
        void onYes();

        void onNo();
    }
}
