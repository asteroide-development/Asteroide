package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import java.util.ArrayList;
import java.util.List;

public class MultiCommandCommandBlockModule extends Module {
    public MultiCommandCommandBlockModule() {
        super(AsteroideAddon.CATEGORY, "multi-command", "Gives you a command block with multiple commands inside");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<MultiCommandCommandBlockModule.Profile> profile = sgGeneral.add(new EnumSetting.Builder<MultiCommandCommandBlockModule.Profile>()
        .name("profile")
        .description("Profile.")
        .defaultValue(MultiCommandCommandBlockModule.Profile.Profile1)
        .build()
    );
    private final Setting<List<String>> commands1 = sgGeneral.add(new StringListSetting.Builder().name("commands1").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile1).build());
    private final Setting<List<String>> commands2 = sgGeneral.add(new StringListSetting.Builder().name("commands2").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile2).build());
    private final Setting<List<String>> commands3 = sgGeneral.add(new StringListSetting.Builder().name("commands3").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile3).build());
    private final Setting<List<String>> commands4 = sgGeneral.add(new StringListSetting.Builder().name("commands4").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile4).build());
    private final Setting<List<String>> commands5 = sgGeneral.add(new StringListSetting.Builder().name("commands5").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile5).build());
    private final Setting<List<String>> commands6 = sgGeneral.add(new StringListSetting.Builder().name("commands6").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile6).build());
    private final Setting<List<String>> commands7 = sgGeneral.add(new StringListSetting.Builder().name("commands7").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile7).build());
    private final Setting<List<String>> commands8 = sgGeneral.add(new StringListSetting.Builder().name("commands8").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile8).build());
    private final Setting<List<String>> commands9 = sgGeneral.add(new StringListSetting.Builder().name("commands9").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile9).build());
    private final Setting<List<String>> commands10 = sgGeneral.add(new StringListSetting.Builder().name("commands10").description("Commands for the multi-command command block").defaultValue("say Asteroide on fucking Crack!", "give @a bedrock 64").visible(() -> profile.get() == Profile.Profile10).build());
    // now to the actual magic
    // I used a modified version of CommandGamerPro's code converted to Java, thanks commandgamerpro
    @Override
    public void onActivate() {
        if(profile.get() == Profile.Profile1) util.MultiCommandBlock(new ArrayList<>(commands1.get()));
        if(profile.get() == Profile.Profile2) util.MultiCommandBlock(new ArrayList<>(commands2.get()));
        if(profile.get() == Profile.Profile3) util.MultiCommandBlock(new ArrayList<>(commands3.get()));
        if(profile.get() == Profile.Profile4) util.MultiCommandBlock(new ArrayList<>(commands4.get()));
        if(profile.get() == Profile.Profile5) util.MultiCommandBlock(new ArrayList<>(commands5.get()));
        if(profile.get() == Profile.Profile6) util.MultiCommandBlock(new ArrayList<>(commands6.get()));
        if(profile.get() == Profile.Profile7) util.MultiCommandBlock(new ArrayList<>(commands7.get()));
        if(profile.get() == Profile.Profile8) util.MultiCommandBlock(new ArrayList<>(commands8.get()));
        if(profile.get() == Profile.Profile9) util.MultiCommandBlock(new ArrayList<>(commands9.get()));
        if(profile.get() == Profile.Profile10) util.MultiCommandBlock(new ArrayList<>(commands10.get()));
    }

    private enum Profile{
        Profile1,
        Profile2,
        Profile3,
        Profile4,
        Profile5,
        Profile6,
        Profile7,
        Profile8,
        Profile9,
        Profile10
    }
}
