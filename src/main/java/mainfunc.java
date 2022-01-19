import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import datenbanken.Prefixf;
import datenbanken.id;
import datenbanken.persConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static datenbanken.Prefixf.PrefixreadFromFile;
import static datenbanken.botchannelf.BotchannelreadFromFile;
import static datenbanken.botchannelf.getBotchannel;
import static datenbanken.persConfig.configreadFromFile;
import static datenbanken.persConfig.getconfig;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static datenbanken.role.getpermission;


class mainstart {
    public static void main(String[] args) throws Exception {

        Prefixf.init("jarbor?", "Prefix.txt");
        datenbanken.botchannelf.init(null, "BotChannel.txt");
        datenbanken.persConfig.init(null, "Configs.txt");
        datenbanken.role.init("0", "role.txt");
        datenbanken.role.rolereadFromFile();
        BotchannelreadFromFile();
        configreadFromFile();
        PrefixreadFromFile();
        // intialisirt und liest Datein aus
        try {
            //JARBOR ID ODMxMTczNTc5MDU1NDMxNzIw.YHRYtw.-Y6daau494XuFSZ_rlM90KW7md8
            //test ID ODEyOTYyODM4MTMzODY2NTM4.YDIYpA.yNhk54nBcVJSAPMeOgfBO57lXn4
            JDA jda = JDABuilder.create(persConfig.getconfig("token"), GUILD_MESSAGES, GUILD_VOICE_STATES, DIRECT_MESSAGES)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                    .addEventListeners(new mainfunc())
                    .addEventListeners(new BotMenagment())
                    .build();
            //jda bot wird gebaut
            mainfunc.input(jda);
            //führt input methode aus und übergibt jda
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class mainfunc extends ListenerAdapter {
    private static AudioPlayerManager playerManager;
    private static Map<Long, GuildMusicManager> musicManagers;

    public mainfunc() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
//lavaplayer stuff idk was das bedeutet
    }

    private static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
        //lavaplayer stuff idk was das bedeutet
    }

    public static void input(JDA jda) throws IOException, NullPointerException {
        GreetServer server = new GreetServer();
        Scanner inputraw = new Scanner(System.in);
        id.init("ids.txt");
        id.idreadFromFile();
//startet server
        while (true) {
            String input = server.start(Integer.parseInt(getconfig("port")));

            System.out.println("input:" + input);
            try {
                // checkt die version des clients
                String[] command = input.split(" ", 4);
                if (command[2].contains("v1.")) {
                    if (!command[2].equals("v1.1")) {
                        command[2] = null;
                        System.out.println("input not right version");
                    }
                } else if (command[3].contains("v1.")) {
                    if (!command[3].equals("v1.1")) {
                        command[3] = null;
                        System.out.println("input not right version");}
                    } else if (command[4].contains("v1.")) {
                        if (!command[4].equals("v1.1")) {
                            command[4] = null;
                            System.out.println("input not right version");
                        }
                } else {
                    command[0] = null;
                }

                int lastg = Objects.requireNonNull(jda.getUserById(id.getid(command[0], false))).getMutualGuilds().toArray().length - 1;

                for (Guild g : Objects.requireNonNull(jda.getUserById(id.getid(command[0], false))).getMutualGuilds()) {
                    if (jda.getGuilds().contains(g)) {
                        System.out.println("GUild: " + g);
                        int lastv = g.getVoiceChannels().toArray().length - 1;
                        for (VoiceChannel v : g.getVoiceChannels()) {
                            System.out.println("gescant: " + v);
                            int lastm = v.getMembers().toArray().length - 1;
                            for (Member m : v.getMembers()) {
                                // die ganzen for schleifen suchen nachdem Voicechannel in dem der USer ist
                                if (m.getUser().equals(jda.getUserById(id.getid(command[0], false)))) {
                                    TextChannel c;
                                    String cs = getBotchannel(g.getId(), true);
                                    System.out.println("erkannt: " + v);
                                    // nimmt den ersten channel wen kein botchannel defeniert ist
                                    if (cs == null) {
                                        for (TextChannel ct : g.getTextChannels()) {
                                            cs = ct.getId();
                                            ct.sendMessage("pls set your botchannel").queue();
                                            break;
                                        }
                                    }
                                    int perm = 0;
                                    c = g.getTextChannelById(cs);

                                    for (Role r : m.getRoles()) {
                                        // scannt nach der berechtigung des Useres
                                        System.out.println(r);
                                        String rperm = getpermission(r.getId(), true);
                                        int rpermint =  Integer.parseInt(rperm);
                                        System.out.println(rpermint);
                                        if (perm < rpermint) {
                                            perm = rpermint;
                                        }
                                        if (perm == 2 | r.equals(m.getRoles().get(m.getRoles().toArray().length - 1))) {
                                            break;
                                        }
                                    }

                                    System.out.println(perm);
                                    switch (String.valueOf(perm)) {
                                        case "2":
                                            System.out.println("power 2");
                                            Member mo = g.getMemberById(command[2]);
                                            switch (command[1]) {
                                                //fehler filter
                                                case "skip":
                                                case "clear": break;
                                                // moderiert die voice channels
                                                case "mute":
                                                    mo.mute(true).queue();
                                                    System.out.println(mo.getUser().getName() + " muted");
                                                    break;
                                                case "deaf":
                                                    mo.deafen(true).queue();
                                                    System.out.println(mo.getUser().getName() + " deafed");
                                                    break;
                                                case "kill":
                                                    g.kickVoiceMember(mo).queue();
                                                    System.out.println(mo.getUser().getName() + " killed");
                                                    break;
                                                case "move":
                                                    g.moveVoiceMember(mo, g.getAfkChannel()).queue();
                                                    System.out.println(mo.getUser().getName() + " moved");
                                                    break;
                                            }
                                        case "1":
                                            System.out.println("power 1");
                                            switch (command[1]) {
                                                // für Musikabspilen da
                                                case "skip":
                                                    skipTrack(g.getTextChannelsByName(c.getName(), true).get(0), true);
                                                    break;
                                                case "clear":

                                                    int i = 0;
                                                    // skiped 1000 mal den track
                                                    while (i < 1000) {
                                                        try {
                                                            i++;
                                                            skipTrack(c, false);
                                                        } catch (NullPointerException e) {
                                                            break;
                                                        }
                                                    }
                                                    System.out.println("1000 tracks cleared");
                                                    c.sendMessage("tracks cleared").queue();
                                                    break;
                                                case "disconnect":
                                                    jda.getDirectAudioController().disconnect(g);
                                                    break;
                                                    //fehler filter
                                                case "kill":
                                                case "move":
                                                case "deaf":
                                                case "mute":
                                                    break;
                                                default:
                                                    //spielt musik
                                                    try {
                                                        if (command[2].contains("v1.")) {
                                                            loadAndPlay(c, command[1], v, 0);
                                                        } else {
                                                            //überprüft ob es eine auswahl in der Playlist gab
                                                            loadAndPlay(c, command[1], v, Integer.parseInt(command[2]));
                                                        }
                                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                                        loadAndPlay(c, command[1], v, 0);
                                                    }
                                            }
                                        case "0":
                                            //wenn man keine erlaubniss hat
                                    }
                                    break;

                                }
                                if (m.equals(v.getMembers().get(lastm))) {
                                    break;
                                }
                            }

                            if (v.equals(g.getVoiceChannels().get(lastv))) {
                                break;
                            }
                        }
                    }
                    if (g.equals(jda.getUserById(id.getid(command[0], false)).getMutualGuilds().get(lastg))) {
                        break;
                    }
                }
                server.stop();
                //stopt den Server
            } catch (Exception e) {
                System.out.println("Exeption:");
                e.printStackTrace();
                server.stop();
            }
        }
    }


   // wieder nur api kramm
    private static void loadAndPlay(final TextChannel channel, final String trackUrl, VoiceChannel voiceChannel, int trackposition) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        System.out.println("play and loaded");

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(channel.getGuild(), musicManager, track, voiceChannel);
            }


            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                System.out.println(trackposition);

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + "  playlist " + playlist.getName()).queue();



                int i = 0;
                for (AudioTrack track : playlist.getTracks()) {
                    i++;
                    if (!(i < trackposition)) {
                        System.out.println(track);
                        play(channel.getGuild(), musicManager, track, voiceChannel);
                    }
                }

            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private static void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, VoiceChannel voiceChannel) {
        System.out.println("play");
        connectToFirstVoiceChannel(guild.getAudioManager(), voiceChannel);
        musicManager.scheduler.queue(track);
    }

    private static void skipTrack(TextChannel channel, Boolean msg) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
        if (msg) {
            channel.sendMessage("Skipped to next track.").queue();
        }
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);

        }
    }
}