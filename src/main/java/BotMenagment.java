import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static datenbanken.Prefixf.getPrefix;
import static datenbanken.Prefixf.setPrefix;
import static datenbanken.botchannelf.getBotchannel;
import static datenbanken.botchannelf.setBotchannel;
import static datenbanken.id.setId;
import static datenbanken.persConfig.getconfig;
import static datenbanken.role.*;
public class BotMenagment extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        try {
if(!e.getAuthor().isBot() && e.getChannel().getType().isGuild()) {

    String[] data = e.getMessage().getContentRaw().split(" ");
    if (data[0].equals(getPrefix(e.getGuild().getId()))) {
        switch (data[1]) {
            case "help":
                e.getChannel().sendMessage(embed("help").build()).queue();

                break;
            case "Download":
                e.getChannel().sendMessage(embed("Download").build()).queue();
                break;
            case "Development":
                e.getChannel().sendMessage("here you go: " + getconfig("dev")).queue();
                break;
            case "sync":
                e.getMember().getUser().openPrivateChannel().complete().sendMessage("for synchronization send me your client id").queue();
        }
        if (e.getMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || e.getMember().getId().equals("573942456069128207")) {


            switch (data[1]) {
                // nimmt commands an und speichert sie dementsprechend in die datenbaken ab
                case "setBotChannel":
                    setBotchannel(e.getGuild().getId(), data[2]);
                    e.getChannel().sendMessage("Botchannel changed").queue();
                    break;
                case "setPrefix":
                    setPrefix(e.getGuild().getId(), data[2]);
                    e.getChannel().sendMessage("Prefix changed to: " + data[2]).queue();
                    break;
                case "rolepermission":
               int perm = 0;
               switch (data[2]){
                   case "Nothing":
                       perm = 0;
                       break;
                   case "Dj":
                       perm = 1;
                       break;
                   case "Moderation" :
                       perm = 2;
                       break;
                   default:
                       e.getChannel().sendMessage("false permission level").queue();
               }
               for(Role r: e.getMessage().getMentionedRoles()){
                   setrolepermission(r.getId(),String.valueOf(perm));
               }
                    e.getChannel().sendMessage("permission set").queue();
            }
        } else {
            switch (data[1]) {
                case "setBotChannel":
                case "setPrefix":
                case "rolepermission":
                    e.getChannel().sendMessage("you have not the permission to do that").queue();

            }
        }
    }
}
        } catch (IllegalStateException ex){
            System.out.println("excaption catched:");
            ex.printStackTrace();
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent e) {
        System.out.println("private:" +e.getChannel().getHistory().retrievePast(1).complete().get(0).getContentRaw());
        if(e.getChannel().getHistory().retrievePast(2).complete().get(1).getContentRaw().equals("for synchronization send me your client id")&& ! e.getAuthor().isBot()){
//kümmert sich um die sync
    setId(e.getMessage().getContentRaw(), e.getAuthor().getId());
e.getChannel().sendMessage("you have got synchronized").queue();
}
        if (e.getAuthor().getId().equals("573942456069128207") && e.getMessage().getContentRaw().contains("news:")){
            //schickt news rein
            int i = 0;
            for (Guild g: e.getJDA().getGuilds()){
                i++;
                if (getBotchannel(g.getId(), false) == null ){
                    for (TextChannel c : g.getTextChannels()){
                        String[] newsinput = e.getMessage().getContentRaw().replaceAll("news:", "").split("///");
                        c.sendMessage(embed(newsinput[0], newsinput[1], c).build()).queue();
                        break;
                    }
                } else {
                TextChannel c = g.getTextChannelById( getBotchannel(g.getId(), false));
             String[] newsinput = e.getMessage().getContentRaw().replaceAll("news:", "").split("&&&");
                c.sendMessage(embed(newsinput[0], newsinput[1], c).build()).queue();
                }
                if (i == e.getJDA().getGuilds().toArray().length){
                    break;
                }
            }
        }


    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent e) {
        try {
            Thread.sleep(10000l);
        }catch (Exception exe){}
        Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage("Thanks for adding me! Please add the Botchannel. My default Prefix is:Jarbor?  This are my commands:\n" + getconfig("help" )
        + "\nYou can download the jar file here:\n" +  getconfig("Download")).queue();
        //sendet eine Nachricht bei join

    }
    //erstelt das embed
    public static EmbedBuilder embed(String typ) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(typ, null);
        eb.setColor(Color.green);
        String ds = getconfig(typ).replaceAll("%%%", "\n");
        eb.setDescription(ds);
        return eb;
    }
    //erstelt das embed (überladen)
    public static EmbedBuilder embed(String title, String typ, TextChannel c) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title, null);
            eb.setColor(Color.green);
            try {
             String  ds  = getconfig(typ).replaceAll("%%%", "\n");
                eb.setDescription(ds);
            }catch (NullPointerException e){
              String ds = getconfig(typ);
                eb.setDescription(ds); }
            return eb;
        }catch (Exception e){
c.sendMessage("error wihle building").queue();
            c.sendMessage(Arrays.toString(e.getStackTrace())).queue();
        } return null;}}
