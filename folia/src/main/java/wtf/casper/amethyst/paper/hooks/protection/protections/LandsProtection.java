package wtf.casper.amethyst.paper.hooks.protection.protections;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import wtf.casper.amethyst.paper.AmethystFolia;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

public class LandsProtection implements IProtection {
    private LandsIntegration landsIntegration;

    @Override
    public boolean canBuild(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

    @Override
    public boolean canInteract(Player player, Location location) {

        if (!landsIntegration.isClaimed(location)) {
            return true;
        }

        Area areaByLoc = landsIntegration.getAreaByLoc(location);

        if (areaByLoc == null) {
            return true;
        }

        if (location.getBlock().getState() instanceof InventoryHolder) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_CONTAINER);
        }
        if (location.getBlock().getState() instanceof Door) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_DOOR);
        }
        if (location.getBlock().getState() instanceof TrapDoor) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_TRAPDOOR);
        } else {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_GENERAL);
        }
    }

    @Override
    public boolean isClaimed(Location location) {
        return landsIntegration.isClaimed(location);
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.ATTACK_ANIMAL) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.ATTACK_MONSTER);
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("Lands");
    }

    @Override
    public void enable() {
        landsIntegration = new LandsIntegration(AmethystFolia.getInstance());
    }

    @Override
    public void disable() {

    }
}
