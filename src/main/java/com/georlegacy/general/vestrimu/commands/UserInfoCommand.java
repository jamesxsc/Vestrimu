package com.georlegacy.general.vestrimu.commands;

import com.georlegacy.general.vestrimu.core.Command;
import com.georlegacy.general.vestrimu.core.objects.enumeration.CommandAccessType;
import com.georlegacy.general.vestrimu.util.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        super(new String[]{"userinfo", "ui"}, "Shows information of a user", "[mention]|[id]", CommandAccessType.USER_ANY, false);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();

        ArrayList<String> args = new ArrayList<String>(Arrays.asList(message.getContentRaw().split(" ")));
        args.remove(0);

        Member member;
        if (!args.isEmpty() && args.get(0).equals(!message.getMentionedMembers().isEmpty() ? message.getMentionedMembers().get(0).getAsMention().replaceFirst("@!", "@").replaceFirst("@", "@!") : null))
            member = message.getMentionedMembers().get(0);
        else
            try {
                member = event.getGuild().getMemberById(args.get(0));
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                member = event.getMember();
            }

        User user = member.getUser();

        List<Role> roleList = member.getRoles();

        StringBuilder roleBuilder = new StringBuilder();
        roleList.forEach(role -> roleBuilder.append(role.getAsMention() + " "));

        EmbedBuilder eb = new EmbedBuilder();
        eb
                .setTitle("User Information")
                .setThumbnail(user.getAvatarUrl())
                .addField("Name", member.getEffectiveName() + " (" + user.getName() + "#" + user.getDiscriminator() + ")", true)
                .addField("ID", user.getId(), true)
                .addField("Status", (member.getOnlineStatus() == OnlineStatus.ONLINE ? "<:online:469796710923894787> `ONLINE`" :
                        member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB ? "<:dnd:469796710881951744> `DO NOT DISTURB`" :
                                member.getOnlineStatus() == OnlineStatus.IDLE ? "<:idle:469796710760054795> `IDLE`" :
                                        "<:offline:469796710860718080> `OFFLINE`"), true);
        if (!member.getActivities().isEmpty()) {
            for (Activity activity : member.getActivities()) {
                //todo
                eb.addField(activity.getType().name(), "`" + activity.getName() + "`" + (activity.isRich() ? "<:richpresence:469804127900270602>" : ""), true);
            }
        }
        eb
                .addField("Account Type", user.isBot() ? "Bot" : "User", true)
                .addField("Avatar", "[`Current Avatar`](" + user.getAvatarUrl() + ")\n[`Default Avatar`](" + user.getDefaultAvatarUrl() + " \"Default Avatar\")", true)
                .addField("Roles", roleBuilder.toString(), false)
                .addField("Account Created", user.getTimeCreated().toLocalDateTime().atZone(ZoneId.of("GMT")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z")), true)
                .addField("Joined Server", member.getTimeJoined().toLocalDateTime().atZone(ZoneId.of("GMT")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z")), true)
                .setColor(Constants.VESTRIMU_PURPLE)
                .setFooter("Vestrimu", Constants.ICON_URL);

        channel.sendMessage(eb.build()).queue();
    }

}
