package org.staroceanmc.bukkit;

public enum Modules {
    RECORD_ITEM_FRAME("xianxian.mc.starocean.recorditemframe.RecordItemFrame"),
    ANTI_TRAMPLING("xianxian.mc.starocean.antitrampling.AntiTrampling"),
    PLAYER_INTERACTING("xianxian.mc.starocean.playerinteract.PlayerInteracting"),
    PROTOCOL_SUPPORT_FEATURES("xianxian.mc.starocean.protocolsupport.ProtocolSupportFeatures"),
    STAR_OCEAN_ROBOT("xianxian.mc.starocean.robot.StarOceanRobot"),
    CMI_FEATURES("xianxian.mc.starocean.cmifeatures.CMIFeatures"),
    LUCK_PERMS_FEATURES("xianxian.mc.starocean.luckperms.LuckPermsFeatures"),
    VALIDATE_UUID("xianxian.mc.starocean.validateuuid.ValidateUUID"),
    PLACEHOLDERAPI_FEATURES("xianxian.mc.starocean.placeholderapi.PlaceHolderAPIFeatures"),
    MINESWEEPER("xianxian.mc.starocean.minesweeper.MinesweeperModule");

    private final String className;

    private Modules(String className) {
        this.className = className;
    }

    public String className() {
        return className;
    }

    @Override
    public String toString() {
        return className;
    }
}
