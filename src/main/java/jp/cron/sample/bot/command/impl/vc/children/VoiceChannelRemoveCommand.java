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
        this.options.add(new OptionData(OptionType.STRING, "channel", "ボイスチャンネルID", true));
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
        String rawChId = event.getOption("channel").getAsString();

        repo.deleteById(rawChId);

        replier.reply(":o: 削除しました。");

    }
}
