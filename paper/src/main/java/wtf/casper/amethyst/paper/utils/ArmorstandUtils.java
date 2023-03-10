package wtf.casper.amethyst.paper.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.persistence.PersistentDataType;
import wtf.casper.amethyst.paper.AmethystPlugin;

public class ArmorstandUtils extends AmethystListener<AmethystPlugin> {

    private static final NamespacedKey armorstandKey = new NamespacedKey("amethyst", "amethyst_armorstand");

    public ArmorstandUtils(AmethystPlugin plugin) {
        super(plugin);
    }

    public static void markArmorstand(ArmorStand armorStand) {
        armorStand.getPersistentDataContainer().set(armorstandKey, PersistentDataType.BYTE, (byte) 1);
    }

    public static void unMarkArmorstand(ArmorStand armorStand) {
        armorStand.getPersistentDataContainer().remove(armorstandKey);
    }

    public static boolean isArmorstand(ArmorStand armorStand) {
        return armorStand.getPersistentDataContainer().has(armorstandKey, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onArmorStandHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand && isArmorstand((ArmorStand) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand && isArmorstand((ArmorStand) event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand && isArmorstand((ArmorStand) event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandVehicle(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof ArmorStand && isArmorstand((ArmorStand) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
