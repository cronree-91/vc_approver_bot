package jp.cron.sample.bot.command.impl.vc;

import jp.cron.sample.bot.command.Command;
import jp.cron.sample.bot.command.CommandReplier;
import jp.cron.sample.bot.command.impl.vc.children.VoiceChannelAddCommand;
import jp.cron.sample.bot.command.impl.vc.children.VoiceChannelListCommand;
import jp.cron.sample.bot.command.impl.vc.children.VoiceChannelRemoveCommand;
import jp.cron.sample.data.repo.VoiceChannelRepository;
import jp.cron.sample.profile.Profile;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VoiceChannelCommand extends Command {

    @Autowired
    public VoiceChannelCommand(Profile profile, VoiceChannelRepository repository) {
        super(profile, "vc", "vc");
        this.ownerCommand = true;
        this.children = new Command[]{
                new VoiceChannelAddCommand(profile, repository),
                new VoiceChannelListCommand(profile, repository),
                new VoiceChannelRemoveCommand(profile, repository)
        };
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
    }
}
