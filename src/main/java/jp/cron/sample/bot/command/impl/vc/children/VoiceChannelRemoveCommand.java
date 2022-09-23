package jp.cron.sample.bot.command.impl.vc.children;

import jp.cron.sample.bot.command.Command;
import jp.cron.sample.bot.command.CommandReplier;
import jp.cron.sample.data.entity.VoiceChannelEntity;
import jp.cron.sample.data.repo.VoiceChannelRepository;
import jp.cron.sample.profile.Profile;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;

public class VoiceChannelRemoveCommand extends Command {
    VoiceChannelRepository repo;
    public VoiceChannelRemoveCommand(Profile profile, VoiceChannelRepository repo) {
        super(profile, "remove", "ボイスチャンネルを削除します。");
        this.ownerCommand = true;
        this.repo = repo;

        this.options = new ArrayList<>();
        this.options.add(new OptionData(OptionType.CHANNEL, "channel", "ボイスチャンネル", true));
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
        GuildChannel ch = event.getOption("channel").getAsGuildChannel();
        if (!(ch instanceof VoiceChannel)) {
            replier.reply("ボイスチャンネルを指定してください。");
            return;
        }
        VoiceChannel vc = (VoiceChannel) ch;

        repo.deleteById(vc.getId());

        replier.reply(":o: 削除しました。");

    }
}
