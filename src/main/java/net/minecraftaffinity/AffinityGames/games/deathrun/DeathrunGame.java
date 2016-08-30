package net.minecraftaffinity.AffinityGames.games.deathrun;

/**
 * Programmed by macguy8 (Colin McDonald)
 * Creation Date: 02, 02 2014
 * Programmed for the AffinityGames project.
 */
public class DeathrunGame {

    /*

    @TeamColor(chat = ChatColor.GRAY, dye = DyeColor.SILVER)
    @TeamFriendlyFire(FriendlyFireSetting.DISALLOW)
    @DefaultTeam
    Team runnerTeam = new Team("Runner", "You should try not to die...");
    @TeamColor(chat = ChatColor.RED, dye = DyeColor.RED)
    @TeamFriendlyFire(FriendlyFireSetting.DISALLOW)
    Team deathTeam = new Team("Death", "You represent death itself.");
    @DefaultKit({"Runner"})
    @UsableBy({"Runner"})
    Kit runnerKit = new DeathrunPlayerKit();
    @DefaultKit({"Death"})
    @UsableBy({"Death"})
    Kit deathKit = new DeathrunPlayerKit();

    boolean finalArenaInitiated = false;

    @Override
    public void updateScoreboard() {
        LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();

        values.put(ChatColor.GREEN.toString() + ChatColor.BOLD + "Runners", String.valueOf(runnerTeam.getPlayerCount()));
        values.put(ChatColor.RED.toString() + ChatColor.BOLD + "Deaths", String.valueOf(deathTeam.getPlayerCount()));

        ScoreboardUtils.getInstance().setGlobalScoreboard(new BasicDBObject("Title", ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Deathrun").append("Values", values), true);
    }

    @Override
    public void loadGame() {

    }

    @Override
    public void onGameStart() {

    }

    @Override
    public boolean canPlayerRespawn(Player player) {
        return (false);
    }

    @Override
    public void onPlayerDeath(Player player) {
        updateScoreboard();

        if (runnerTeam.getPlayerCount() == 0) {
            endGame(deathTeam);
        } else if (deathTeam.getPlayerCount() == 0) {
            endGame(runnerTeam);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (hasPlayer(event.getPlayer().getName(), true)) {
            event.setCancelled(true);

            if (event.hasBlock()) {
                if (event.getPlayer().getItemInHand().getType() == Material.BEACON && UserManager.getInstance().getUser(event.getPlayer()).getRank().isGreaterOrEqualTo(PermissionsRank.DEVELOPER)) {
                    Block block = event.getClickedBlock().getRelative(BlockFace.UP);

                    block.setType(Material.SIGN_POST);

                    Sign sign = (Sign) block.getState();

                    sign.setLine(0, String.valueOf(event.getClickedBlock().getX()));
                    sign.setLine(1, String.valueOf(event.getClickedBlock().getY()));
                    sign.setLine(2, String.valueOf(event.getClickedBlock().getZ()));
                    sign.setLine(3, event.getPlayer().getItemInHand().getItemMeta().getDisplayName());

                    sign.update();
                }

                if (TeamUtils.getInstance().getTeamFromPlayer(event.getPlayer()).equals(deathTeam) && event.getClickedBlock().getType() == Material.WOOL && event.getClickedBlock().getData() == DyeColor.LIME.getWoolData()) {
                    event.getClickedBlock().setData(DyeColor.RED.getWoolData());

                    Block b = event.getClickedBlock();

                    while (b.getType() != Material.REDSTONE_LAMP_OFF) {
                        if (b.getLocation().getBlockY() <= 0) {
                            return;
                        }

                        b = b.getRelative(BlockFace.DOWN);
                    }

                    final Block b2 = b;

                    b2.setType(Material.REDSTONE_BLOCK);

                    AffinityGameCommons.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AffinityGameCommons.getInstance(), new Runnable() {


                        public void run() {
                            b2.setType(Material.REDSTONE_LAMP_OFF);
                        }

                    }, 20L);
                }

            }
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() < event.getOldCurrent()) {
            return;
        }

        if (event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK && event.getBlock().getRelative(BlockFace.DOWN, 2).getType() == Material.SIGN_POST) {
            Sign s = (Sign) event.getBlock().getRelative(BlockFace.DOWN, 2).getState();
            final Block target = s.getWorld().getBlockAt(Integer.valueOf(s.getLine(0)), Integer.valueOf(s.getLine(1)), Integer.valueOf(s.getLine(2)));
            final BlockState previous = target.getState();
            String[] line4Data1 = s.getLine(3).split(" - ");
            final String[] line4Data2 = line4Data1[0].split(":");

            AffinityGameCommons.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AffinityGameCommons.getInstance(), new Runnable() {

                public void run() {
                    target.getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, target.getTypeId());
                    target.setType(Material.matchMaterial(line4Data2[0]));

                    if (line4Data2.length == 2) {
                        target.setData(Byte.valueOf(line4Data2[1]));
                    }
                }

            }, 2L);

            if (!line4Data1[1].equals("-1")) {
                AffinityGameCommons.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(AffinityGameCommons.getInstance(), new Runnable() {

                    public void run() {
                        previous.update(true);
                        previous.update(true);
                    }

                }, Long.valueOf(line4Data1[1]));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (!valid(event.getPlayer()) || !TeamUtils.getInstance().getTeamFromPlayer(event.getPlayer()).equals(runnerTeam)) {
            return;
        }

        if (event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() != Material.REDSTONE_BLOCK) {
            return;
        }

        if (!finalArenaInitiated) {
            finalArenaInitiated = true;
            message(ChatColor.GOLD.toString() + ChatColor.GREEN + event.getPlayer().getName() + " has reached the final arena! The final battle between the death and the runner will start!");

            event.getPlayer().teleport(getMap().getLocationRandom("RunnerFinalArena"));
            event.getPlayer().getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));

            for (Player player : AffinityGameCommons.getInstance().getServer().getOnlinePlayers()) {
                if (TeamUtils.getInstance().getTeamFromPlayer(player).equals(deathTeam)) {
                    player.teleport(getMap().getLocationRandom("DeathFinalArena"));

                    player.getInventory().addItem(new ItemStack(Material.STONE_HOE));
                }
            }
        } else {
            message(ChatColor.YELLOW.toString() + ChatColor.GREEN + event.getPlayer().getName() + " has also arrived at the final arena!");

            event.getPlayer().teleport(getMap().getLocationRandom("RunnerFinalArena"));
            event.getPlayer().getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
        }
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        updateScoreboard();

        if (runnerTeam.getPlayerCount() == 0) {
            endGame(deathTeam);
        } else if (deathTeam.getPlayerCount() == 0) {
            endGame(runnerTeam);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        event.getEntity().remove();
    }

    */

}