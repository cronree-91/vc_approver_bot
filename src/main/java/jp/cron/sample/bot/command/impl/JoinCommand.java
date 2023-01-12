package jp.cron.sample.bot.command.impl;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import jp.cron.sample.bot.command.Command;
import jp.cron.sample.bot.command.CommandReplier;
import jp.cron.sample.data.entity.VoiceChannelEntity;
import jp.cron.sample.data.repo.VoiceChannelRepository;
import jp.cron.sample.profile.Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinCommand extends Command {
    @Autowired
    VoiceChannelRepository repo;
    @Autowired
    EventWaiter waiter;

    Profile profile;

    @Autowired
    public JoinCommand(Profile profile) {
        super(profile, "join", "VCに参加リクエストを送信します。");
        this.profile = profile;
        this.guildOnly = true;
    }

    @Override
    protected void process(SlashCommandEvent event, CommandReplier replier) {
        SelectionMenu.Builder menuBuilder= SelectionMenu.create("join")
                .setPlaceholder("参加するVCを選択してください。");

        if (repo.count()==0) {
            replier.reply("VCが登録されていません。\n/vc addコマンドを用いてVCを登録するように管理者に伝えてください。");
            return;
        }

        for (VoiceChannelEntity entity: repo.findAll()) {
            menuBuilder.addOption(entity.name, entity.id);
        }

        SelectionMenu menu = menuBuilder.build();

        MessageBuilder mb = new MessageBuilder()
                .setContent("** **")
                .setActionRows(ActionRow.of(menu));

        event.getHook().sendMessage(mb.build()).queue();

//        waiter.waitForEvent(SelectionMenuEvent.class,
//                e -> true,
//                e -> {
//            System.out.println(e);
//            System.out.println(e.getComponentId());
//            System.out.println(e.getUser().getId());
//            System.out.println(event.getUser().getId());
//
//            System.out.println(
//                    e.getComponentId().equals("join") && e.getUser().getId().equals(event.getUser().getId())
//            );
//                });

        waiter.waitForEvent(
                SelectionMenuEvent.class,
                e -> e.getComponentId().equals("join") && e.getUser().getId().equals(event.getUser().getId()),
                e -> {
                    try {
                        SelectOption opt = e.getInteraction().getSelectedOptions().get(0);
                        VoiceChannelEntity entity = repo.findById(opt.getValue()).orElse(null);
                        if (entity == null) {
                            e.reply("エラーが発生しました。").setEphemeral(true).queue();
                            return;
                        }

                        apply(entity, event.getMember(), event.getChannel());

                        e.reply("参加リクエストを送信しました。\n待機用VCに入ってお待ち下さい。").queue();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
        );
    }

    private void apply(VoiceChannelEntity entity, Member member, MessageChannel ch) {
        TextChannel applyCh = member.getGuild().getTextChannelById(profile.applyChannel);

        MessageBuilder mb = new MessageBuilder();
        mb.setContent("");

        mb.append("<@").append(profile.ownerId).append(">");

        for (String s : profile.coOwnersId) {
            mb.append("<@").append(s).append(">");
        }



        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(member.getUser().getAsTag(), null, member.getUser().getAvatarUrl());
        eb.setTitle("参加リクエスト");
        eb.setDescription("参加リクエストが届きました。");
        eb.addField("VC名", entity.name, false);
        eb.addField("VC", "<#"+entity.id+">", false);
        eb.addField("リクエスト者", member.getAsMention(), false);

        mb.setActionRows(
                ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "accept", "承認"),
                        Button.of(ButtonStyle.DANGER, "reject", "拒否"))
        );

        mb.setEmbeds(eb.build());

        Message msg = applyCh.sendMessage(mb.build()).complete();

        waiter.waitForEvent(ButtonClickEvent.class,
                e -> (e.getComponentId().equals("accept") || e.getComponentId().equals("reject")) && e.getMessage().getId().equals(msg.getId()),
                e -> {
                    try {
                        if (e.getComponentId().equals("accept")) {
                            e.reply("承認しました。").queue();

                            Member mem = e.getGuild().getMemberById(member.getId());


                            VoiceChannel vc = mem.getGuild().getVoiceChannelById(entity.id);
                            if (vc == null) {
                                ch.sendMessage(mem.getAsMention()+" エラーが発生しました。").queue();
                                return;
                            }


                            VoiceChannel joiningCh = e.getGuild().getMember(mem.getUser()).getVoiceState().getChannel();
                            if (joiningCh == null) {
                                ch.sendMessage(mem.getAsMention()+" 待機用VCに参加していません。").queue();
                                return;
                            }

                            ch.sendMessage(mem.getAsMention()+" リクエストが承認されました。").queue();

                            e.getGuild().moveVoiceMember(mem, vc).queue();

                        } else {
                            e.reply("拒否しました。").queue();

                            ch.sendMessage(member.getAsMention()+" リクエストが拒否されました。").queue();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

    }
}
