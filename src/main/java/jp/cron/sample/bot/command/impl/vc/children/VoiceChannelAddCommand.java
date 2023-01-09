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

public class VoiceChannelAddCommand extends Command {
    VoiceChannelRepository repo;
    public VoiceChannelAddCommand(Profile profile, VoiceChannelRepository repo) {
        super(profile, "add", "ボイスチャンネルを登録します。");
        this.ownerCommand = true;
        this.repo = repo;

        this.options = new ArrayList<>();
        this.options.add(new OptionData(OptionType.STRING, "channel", "ボイスチャンネルのID", true));
        this.options.add(new OptionData(OptionType.STRING, "name", "名前", true));
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
        String rawChId = event.getOption("channel").getAsString();

        Long chId = 0L;
        try {
            chId = Long.parseLong(rawChId);
        } catch (NumberFormatException e) {
            replier.reply("ボイスチャンネルのIDを指定してください。");
            return;
        }

        VoiceChannel ch = event.getGuild().getVoiceChannelById(chId);
        if (ch==null) {
            replier.reply("ボイスチャンネルが見つかりません。");
            return;
        }

        String name = event.getOption("name").getAsString();

        VoiceChannelEntity entity = new VoiceChannelEntity();
        entity.id = ch.getId();
        entity.name = name;

        repo.save(entity);

        replier.reply(":o: 登録しました。");

    }
}
