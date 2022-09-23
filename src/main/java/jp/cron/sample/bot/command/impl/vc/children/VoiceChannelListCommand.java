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
import java.util.List;

public class VoiceChannelListCommand extends Command {
    VoiceChannelRepository repo;
    public VoiceChannelListCommand(Profile profile, VoiceChannelRepository repo) {
        super(profile, "list", "ボイスチャンネルの一覧を表示します。");
        this.ownerCommand = true;
        this.repo = repo;
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
        List<VoiceChannelEntity> entities = repo.findAll();

        StringBuilder sb = new StringBuilder();

        for (VoiceChannelEntity entity : entities) {
            sb.append("<#").append(entity.id).append("> : ").append(entity.name).append("\n");
        }

        if (sb.toString().isEmpty())
            sb.append("登録はありません。\n/vc addで登録してください。");

        replier.reply(sb.toString());

    }
}
