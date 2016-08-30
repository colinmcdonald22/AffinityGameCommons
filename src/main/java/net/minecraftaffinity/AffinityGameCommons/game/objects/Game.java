package net.minecraftaffinity.AffinityGameCommons.game.objects;

import net.minecraftaffinity.AffinityCommons.user.UserManager;
import net.minecraftaffinity.AffinityCommons.utils.*;
import net.minecraftaffinity.AffinityGameCommons.AffinityGameCommons;
import net.minecraftaffinity.AffinityGameCommons.abilities.objects.Ability;
import net.minecraftaffinity.AffinityGameCommons.game.GameManager;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameState;
import net.minecraftaffinity.AffinityGameCommons.game.enums.GameToggle;
import net.minecraftaffinity.AffinityGameCommons.game.listeners.FollowGameTogglesListener;
import net.minecraftaffinity.AffinityGameCommons.game.listeners.NoTeamPvPListener;
import net.minecraftaffinity.AffinityGameCommons.game.listeners.LobbyListener;
import net.minecraftaffinity.AffinityGameCommons.kits.objects.Kit;
import net.minecraftaffinity.AffinityGameCommons.map.objects.Map;
import net.minecraftaffinity.AffinityGameCommons.map.MapManager;
import net.minecraftaffinity.AffinityGameCommons.scoreboards.presets.ScoreboardLobbyPresets;
import net.minecraftaffinity.AffinityGameCommons.teams.enums.FriendlyFireSetting;
import net.minecraftaffinity.AffinityGameCommons.teams.objects.Team;
import net.minecraftaffinity.AffinityGameCommons.utils.GameUtils;
import net.minecraftaffinity.AffinityGameCommons.utils.KitUtils;
import net.minecraftaffinity.AffinityGameCommons.utils.TeamUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 04, 01 2014
 * Programmed for the AffinityGameAPI project.
 */
public abstract class Game implements Listener {

    //***************************//

    private GameState state = GameState.WAITING_FOR_PLAYERS;

    private Map map = null;
    private Set<GameToggle> enabledToggles = new HashSet<GameToggle>();
    private Kit[] kits;
    private Team[] teams;

    private HashMap<String, Kit> playerKits = new HashMap<String, Kit>();
    private HashMap<String, Team> playerTeams = new HashMap<String, Team>();
    private HashMap<String, Boolean> players = new HashMap<String, Boolean>();
    private Set<String> specs = new HashSet<String>();

    private long started = -1;
    private long ended = -1;

    private String name;
    private String[] description;

    private int neededPlayers = 100;

    private Listener followGameToggles = new FollowGameTogglesListener();
    private Listener noTeamPvP = new NoTeamPvPListener();
    private Listener lobbyListener = new LobbyListener();

    //***************************//

    public Game(String name, String[] description, Kit[] kits, Team[] teams) {
        this.name = name;
        this.description = description;
        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents(lobbyListener, AffinityGameCommons.getInstance());

        LoggingUtils.log("Game", "Loading game " + name + ".");

        this.kits = kits;
        this.teams = teams;
        this.map = MapManager.getInstance().findAppropriateMap(this);

        LoggingUtils.log("Game", "Selected the map " + map.getName() + " by " + map.getAuthors() + " for this round.");

        for (Team team : teams) {
            LoggingUtils.log("Team", "Loaded team " + team.getName() + ".");
            team.setHost(this);
        }

        for (Kit kit : kits) {
            LoggingUtils.log("Kit", "Loaded kit " + kit.getName() + ".");
            kit.setHost(this);

            if (kit.getAbilities() != null) {
                for (Ability ability : kit.getAbilities()) {
                    LoggingUtils.log("Kit", "Added ability " + ability.getName() + " for kit " + kit.getName() + ".");

                    ability.setHost(kit);

                    if (ability instanceof Listener) {
                        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents((Listener) ability, AffinityGameCommons.getInstance());
                    }
                }
            }
        }

        LoggingUtils.log("Map", "Loading map file...");

        map.initWorld();

        LoggingUtils.log("Map", "Map file loaded.");
        LoggingUtils.log("Map", "Loaded cuboids: " + map.getMapRegions().keySet());
        LoggingUtils.log("Map", "Loaded locations: " + map.getMapLocations().keySet());

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            GameUtils.selectDefaultTeam(this, player, false, true);
            GameUtils.selectDefaultKit(this, player, false, true);

            players.put(player.getName(), true);
        }

        onGameLoad();

        KitUtils.renderKits(this);
        TeamUtils.renderTeams(this);

        if (AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length >= neededPlayers) {
            LoggingUtils.log("Game", "Enough players detected to start. Countdown scoreboard being displayed.");
            startCountdown(false, 20);
        } else {
            LoggingUtils.log("Game", "Not enough players detected to start. Lobby scoreboard being displayed.");
            ScoreboardLobbyPresets.updateWaitingScoreboard(this);
        }
    }

    //***************************//

    public void startCountdown(final boolean forced, final int length) {
        state = GameState.IN_COUNTDOWN;
        LoggingUtils.log("Game", "Starting countdown...");

        new BukkitRunnable() {

            int time = length;

            public void run() {
                time--;

                if ((time % 5 == 0 && time < 20) || (time < 5)) {
                    for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 1F);
                    }
                }


                if (!forced && AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length < neededPlayers) {
                    state = GameState.WAITING_FOR_PLAYERS;
                    LoggingUtils.log("Game", "Stopping game countdown - Less players than needed");

                    for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                        player.setExp(0F);
                    }

                    ScoreboardLobbyPresets.updateWaitingScoreboard(Game.this);
                    AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatUtils.GRAY + "There are no longer enough players online to start the game. The game countdown has been reset.");
                    cancel();
                    return;
                }

                if (time == 0) {
                    startGame();
                    cancel();
                    return;
                }

                ScoreboardLobbyPresets.updateLobbyScoreboard(Game.this, time);
            }

        }.runTaskTimer(AffinityGameCommons.getInstance(), 0L, 20L);
    }

    @SuppressWarnings("deprecation")
    public void startGame() {
        started = System.currentTimeMillis();
        state = GameState.IN_GAME;
        LoggingUtils.log("Game", "Starting game...");

        HandlerList.unregisterAll(lobbyListener);
        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents(followGameToggles, AffinityGameCommons.getInstance());
        AffinityGameCommons.getInstance().getServer().getPluginManager().registerEvents(noTeamPvP, AffinityGameCommons.getInstance());

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);
            player.teleport(getSpawnLocation(player));
            giveSpawnItems(player);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "This game of " + ChatColor.YELLOW.toString() + ChatColor.BOLD + getName() + ChatColor.AQUA.toString() + ChatColor.BOLD + " is commencing...");

        for (Kit kit : kits) {
            //if (kit.getSelectionEntity() != null) {
                //kit.getSelectionEntity().despawn(DespawnReason.CUSTOM);
            //}
        }

        updateScoreboard();
        onGameStart();

        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
    }

    public void endGame(List<Player> places) {
        for (int i = 0; i < 4; i++) {
            AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");

        for (int i = 1; i <= 3; i++) {
            if (places.size() >= i) {
                AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + places.get(i - 1).getName() + ChatColor.WHITE + ChatColor.BOLD + " - " + GameUtils.getRanking(i));
            }
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("");

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            if (places.contains(player)) {
                player.sendMessage(ChatColor.BOLD + "You ranked " + ChatColor.AQUA.toString() + ChatColor.BOLD + GameUtils.ordinal(places.indexOf(player) + 1) + ChatColor.WHITE + ChatColor.BOLD + " place.");
            }
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");

        endGame();
    }

    public void endGame(Team winner) {
        for (int i = 0; i < 4; i++) {
            AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage(winner.getColor().toString() + ChatColor.BOLD + "The " + winner.getDisplayName() + (winner.getName().endsWith(" Team") ? "" : " Team") + winner.getColor().toString() + ChatColor.BOLD + " has won the game!");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");

        endGame();
    }

    public void endGame(Player winner) {
        for (int i = 0; i < 4; i++) {
            AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        }

        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + winner.getName() + ChatColor.WHITE + ChatColor.BOLD + " has won the game!");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
        AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");

        endGame();
    }

    public void endGame() {
        ended = System.currentTimeMillis();
        state = GameState.POST_GAME;

        for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
            player.setGameMode(GameMode.CREATIVE);
        }

        enabledToggles.clear();

        new BukkitRunnable() {

            int original = 10;
            int time = original;

            public void run() {
                if (time == 0) {
                    returnToLobby();
                    cancel();
                    return;
                } else if (time == (original / 2)) {
                    for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                    }

                    for (int i = 0; i < 4; i++) {
                        AffinityGameCommons.getInstance().getServer().broadcastMessage("");
                    }

                    AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
                    AffinityGameCommons.getInstance().getServer().broadcastMessage("");
                    AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Map: " + ChatColor.WHITE + getMap().getName());
                    AffinityGameCommons.getInstance().getServer().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Map Author: " + ChatColor.WHITE + getMap().getAuthorsFormatted());
                    AffinityGameCommons.getInstance().getServer().broadcastMessage("");
                    AffinityGameCommons.getInstance().getServer().broadcastMessage("=================================");
                }

                ScoreboardLobbyPresets.updateEndingScoreboard(Game.this, time);
                time -= 1;
            }
        }.runTaskTimer(AffinityGameCommons.getInstance(), 0L, 20L);
    }

    public void returnToLobby() {
        state = GameState.POST_GAME;

        HandlerList.unregisterAll(followGameToggles);
        HandlerList.unregisterAll(noTeamPvP);
        HandlerList.unregisterAll(Game.this);

        if (ended != -1) {
            ended = System.currentTimeMillis();
        }

        specs.clear();

        for (final Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

            UserManager.getInstance().getUser(player).setNametagColor(null);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);

            player.teleport(MapManager.getInstance().getLobbyMap().getLocationRandom("Spawn").add(0.5, 0.5, 0.5));
        }

        map.reset();
        GameManager.getInstance().endCurrentGame();
    }

    //***************************//

    public boolean canDamage(Player player1, Player player2) {
        if (!playerTeams.get(player1.getName()).getName().equals(playerTeams.get(player2.getName()).getName())) {
            return (true);
        }

        return (playerTeams.get(player1.getName()).getFriendlyFireSetting() == FriendlyFireSetting.ALLOW);
    }

    public boolean hasPlayer(String player, boolean stillAlive) {
        return (players.containsKey(player) && (stillAlive ? players.get(player) : true));
    }

    public Set<String> getPlayers(boolean stillAlive) {
        HashSet<String> players = new HashSet<String>();

        for (java.util.Map.Entry<String, Boolean> entry : this.players.entrySet()) {
            if (stillAlive && !entry.getValue()) {
                continue;
            }

            players.add(entry.getKey());
        }

        return (players);
    }

    public boolean valid(Player player) {
        return (state == GameState.IN_GAME && players.containsKey(player.getName()) && players.get(player.getName()));
    }

    public boolean canJoinTeam(Player player, Team team) {
        return (TeamUtils.countPlayers(team) <= AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length / teams.length + 1);
    }

    //***************************//

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin2(PlayerJoinEvent event) {
        switch (state) {
            case IN_COUNTDOWN:
            case WAITING_FOR_PLAYERS:
                if (state == GameState.WAITING_FOR_PLAYERS) {
                    if (AffinityGameCommons.getInstance().getServer().getOnlinePlayers().length >= neededPlayers) {
                        startCountdown(false, 20);
                    } else {
                        ScoreboardLobbyPresets.updateWaitingScoreboard(this);
                    }
                }

                PlayerUtils.resetInventory(event.getPlayer(), GameMode.SURVIVAL);
                event.getPlayer().teleport(MapManager.getInstance().getLobbyMap().getLocationRandom("Spawn").add(0.5, 0.5, 0.5));

                players.put(event.getPlayer().getName(), true);
                GameUtils.selectDefaultTeam(this, event.getPlayer(), false, true);
                GameUtils.selectDefaultKit(this, event.getPlayer(), false, true);

                LoggingUtils.log("Game", "Assigned " + event.getPlayer().getName() + " to team " + playerTeams.get(event.getPlayer().getName()).getName() + " and kit " + playerKits.get(event.getPlayer().getName()).getName() + ".");
                break;
            case IN_GAME:
            case POST_GAME:
                event.getPlayer().teleport(getSpectatorSpawnLocation(event.getPlayer()));
                setAsSpec(event.getPlayer());

                if (state == GameState.IN_GAME) {
                    event.getPlayer().sendMessage(ChatUtils.GRAY + "This game of " + ChatUtils.encapsulate(getName()) + " is currently in progress. You are now spectating the game.");
                } else {
                    event.getPlayer().sendMessage(ChatUtils.GRAY + "This game of " + ChatUtils.encapsulate(getName()) + " is currently ending. You are now spectating the game.");
                }

                break;
        }
    }

    @EventHandler
    public void onPlayerQuit2(PlayerQuitEvent event) {
        playerTeams.remove(event.getPlayer().getName());
        playerKits.remove(event.getPlayer().getName());

        if (state == GameState.WAITING_FOR_PLAYERS) {
            ScoreboardLobbyPresets.updateWaitingScoreboard(this);
        }

        if (valid(event.getPlayer())) {
            onPlayerDisconnect(event.getPlayer());
            players.put(event.getPlayer().getName(), false);

            if (isToggleEnabled(GameToggle.ENABLE_ITEM_DROPS_ON_LOGOUT)) {
                for (ItemStack item : event.getPlayer().getInventory().getContents()) {
                    if (item != null) {
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
                    }
                }

                for (ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
                    if (item != null) {
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath2(final PlayerDeathEvent event) {
        if (valid(event.getEntity())) {
            if (!isToggleEnabled(GameToggle.ENABLE_ITEM_DROPS_ON_DEATH)) {
                event.getDrops().clear();
            }

            event.setDroppedExp(0);

            boolean respawnAllowed = canPlayerRespawn(event.getEntity());

            if (!respawnAllowed) {
                players.put(event.getEntity().getName(), false);
                PlayerUtils.resetInventory(event.getEntity(), GameMode.CREATIVE);

                setAsSpec(event.getEntity());
                event.getEntity().teleport(getSpectatorSpawnLocation(event.getEntity()));

                playerTeams.remove(event.getEntity().getName());
                playerKits.remove(event.getEntity().getName());
            } else {
                PlayerUtils.resetInventory(event.getEntity(), GameMode.SURVIVAL);
                event.getEntity().teleport(getSpawnLocation(event.getEntity()));

                new BukkitRunnable() {

                    public void run() {
                        giveSpawnItems(event.getEntity());
                    }

                }.runTaskLater(AffinityGameCommons.getInstance(), 1L);

            }

            onPlayerDeath(event.getEntity());

            if (state != GameState.IN_GAME) {
                event.setDeathMessage(null);
            }
        }
    }

    //***************************//

    public void setAsSpec(Player player) {
        PlayerUtils.resetInventory(player, GameMode.CREATIVE);
        UserManager.getInstance().getUser(player).setNametagColor(ChatColor.AQUA);
        specs.add(player.getName());
    }

    public boolean isSpec(Player player) {
        return (specs.contains(player.getName()));
    }

    //***************************//

    public void onGameLoad() {}
    public void onGameStart() {}
    public void onGameEnd() {}

    public void updateScoreboard() {}

    public void giveSpawnItems(Player player) {
        if (playerKits.containsKey(player.getName())) {
            playerKits.get(player.getName()).equipPlayer(player);
        }
    }

    public Location getSpawnLocation(Player player) {
        return (teams.length == 1 ? map.getLocationRandom("Spawn") : map.getLocationRandom(playerTeams.get(player.getName()).getName() + "Spawn"));
    }

    public Location getSpectatorSpawnLocation(Player player) {
        return (map.getMapLocations().containsKey("SpectatorSpawn") ? player.getLocation().add(0, 2, 0) : map.getLocationRandom("SpectatorSpawn"));
    }

    public boolean canPlayerRespawn(Player player) {
        return (true);
    }

    public void onPlayerDisconnect(Player player) {}
    public void onPlayerDeath(Player player) {}

    //***************************//

    public Map getMap() {
        return (map);
    }

    public GameState getState() {
        return (state);
    }

    public String getName() {
        return (name);
    }

    public void enableToggle(GameToggle toggle) {
        enabledToggles.add(toggle);
    }

    public void disableToggle(GameToggle toggle) {
        enabledToggles.remove(toggle);
    }

    public boolean isToggleEnabled(GameToggle toggle) {
        return (enabledToggles.contains(toggle));
    }

    public HashMap<String, Kit> getKitSelections() {
        return (playerKits);
    }

    public HashMap<String, Team> getTeamSelections() {
        return (playerTeams);
    }

    public Kit[] getKits() {
        return (kits);
    }

    public Team[] getTeams() {
        return (teams);
    }

    public Kit getKit(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return (kit);
            }
        }

        return (null);
    }

    public Team getTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return (team);
            }
        }

        return (null);
    }

    public Team getTeam(ChatColor color) {
        for (Team team : teams) {
            if (team.getColor() == color) {
                return (team);
            }
        }

        return (null);
    }

    public int getNeededPlayers() {
        return (neededPlayers);
    }

    //***************************//

}