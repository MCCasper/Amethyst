package wtf.casper.amethyst.bungee;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import wtf.casper.amethyst.bungee.listener.PlayerListener;
import wtf.casper.amethyst.bungee.mq.RedisManager;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.storageapi.libs.boostedyaml.YamlDocument;
import wtf.casper.storageapi.libs.boostedyaml.settings.dumper.DumperSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.general.GeneralSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.loader.LoaderSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;

@Getter
public class AmethystBungee extends Plugin {

    private YamlDocument config;
    private RedisManager redisManager;

    @Override
    public void onEnable() {
        AmethystCore.init();

        config = getYamlDocument("config.yml");
        redisManager = new RedisManager(this);
        new PlayerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path) {
        return getYamlDocument(path, GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        return YamlDocument.create(new File(getDataFolder(), path),
                getResourceAsStream(path),
                generalSettings,
                loaderSettings,
                dumperSettings,
                updaterSettings);
    }
}
