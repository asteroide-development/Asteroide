package spigey.asteroide.modules;

import com.mojang.datafixers.util.Either;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportSender;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Map;

public class AutoReportModule extends Module {
    public AutoReportModule() { super(AsteroideAddon.CATEGORY, "Auto-Report", "Automatically reports signed messages to Mojang"); }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<List<String>> reportPhrases = sgGeneral.add(new StringListSetting.Builder()
        .name("Phrases to report")
        .description("If a message contains one of these case-insensitive strings, it will automatically get reported")
        .defaultValue(List.of("nigg", "fuck", "tard", "kill yourself", "kys"))
        .build()
    );
    private Setting<Boolean> ignoreSpaces = sgGeneral.add(new BoolSetting.Builder()
        .name("Ignore Spaces")
        .description("Ignores spaces when scanning for phrases to report a message")
        .defaultValue(true)
        .visible(() -> !reportPhrases.get().isEmpty())
        .build()
    );
    private Setting<AbuseReportReason> reportReason = sgGeneral.add(new EnumSetting.Builder<AbuseReportReason>()
        .name("Report Reason")
        .description("Which reason to use when reporting a player")
        .defaultValue(AbuseReportReason.HATE_SPEECH)
        .build()
    );
    private Setting<String> additionalNotes = sgGeneral.add(new StringSetting.Builder()
        .name("Report Details")
        .description("Additional Notes to add when reporting a player. This is optional")
        .defaultValue("hurt my feelings and made me cry :(")
        .build()
    );
    private Setting<Friends> friendsMode = sgGeneral.add(new EnumSetting.Builder<Friends>()
        .name("Friends Mode")
        .description("Whether to report friends, not report them or ignore this condition entirely")
        .defaultValue(Friends.ExcludeFriends)
        .build()
    );
    private Setting<Boolean> whitelistSelf = sgGeneral.add(new BoolSetting.Builder()
        .name("Block Self-Reports")
        .description("Stops this module from reporting yourself")
        .defaultValue(true)
        .build()
    );

    private boolean tryReport = false;

    private boolean shouldReport(ReceivedMessage.ChatMessage message){
        if(whitelistSelf.get() && message.isSentFrom(mc.getSession().getUuidOrNull())) return false;
        String content = message.getContent().getString().toLowerCase();
        if(ignoreSpaces.get()) content = content.replaceAll(" ", "");
        if(
            meteordevelopment.meteorclient.systems.friends.Friends.get().get(message.profile().getName()) != null &&
            friendsMode.get() == Friends.ExcludeFriends
        ) return false;
        for(String phrase : reportPhrases.get().stream().filter(item -> !item.isBlank()).toList()) if(content.contains(phrase)) return true;
        return false;
    }

    private boolean shouldReport(String message){
        message = message.toLowerCase();
        if(ignoreSpaces.get()) message = message.replaceAll(" ", "");
        for(String phrase : reportPhrases.get().stream().filter(item -> !item.isBlank()).toList()) if(message.contains(phrase)) return true;
        return false;
    }

    // This should fix a race condition(?) of log.getMaxIndex() containing the wrong message when two are received at once
    private Map<ReceivedMessage.ChatMessage, Integer> findEntry(ChatLog chatLog){
        for(int i = chatLog.getMaxIndex(); i > Math.max(chatLog.getMaxIndex() - 5, chatLog.getMinIndex()); i--){
            ChatLogEntry entry = chatLog.get(i);
            if(entry == null) continue;
            if(!(entry instanceof ReceivedMessage.ChatMessage message)) continue;
            if(shouldReport(message)) return Map.of(message, i);
        }
        return null;
    }

    @EventHandler
    private void messageReceived(ReceiveMessageEvent event){
        if(!shouldReport(event.getMessage().getString())) return;
        this.tryReport = true;
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!this.tryReport) return;
        this.tryReport = false;

        AbuseReportSender sender = mc.getAbuseReportContext().getSender();

        Map<ReceivedMessage.ChatMessage, Integer> entryBundle = findEntry(mc.getAbuseReportContext().getChatLog());
        if(entryBundle == null) return;
        ReceivedMessage.ChatMessage entry = entryBundle.keySet().iterator().next();

        ChatAbuseReport.Builder report = new ChatAbuseReport.Builder(entry.profile().getId(), sender.getLimits());
        report.setReason(reportReason.get());
        if(!additionalNotes.get().isEmpty()) report.setOpinionComments(additionalNotes.get());
        report.toggleMessageSelection(entryBundle.get(entry));
        report.setAttested(true);
        Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> result = report.build(mc.getAbuseReportContext());
        result.ifLeft((built) -> {
            sender.send(built.id(), built.reportType(), built.report())
                .thenRunAsync(() -> info(String.format("Successfully reported %s!", entry.profile().getName())))
                .exceptionally(throwable -> { error(throwable.getMessage()); return null; });
        }).ifRight(reason -> { error(reason.message().getString()); });
    }

    private enum Friends {
        OnlyFriends,
        ExcludeFriends,
        IgnoreFriends
    }
}
