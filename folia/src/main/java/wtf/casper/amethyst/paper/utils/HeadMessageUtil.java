package wtf.casper.amethyst.paper.utils;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Author: @Demonly, @Casper
 * Head Image util.
 * Gets head image from crafatar, converts to BufferedImage, and then converts pixels into lined TextComponents.
 */

public class HeadMessageUtil {

    private static BufferedImage getHeadBufferedImage(UUID uuid) throws IOException {
        return getHeadBufferedImage(uuid, 8);
    }

    private static BufferedImage getHeadBufferedImage(UUID uuid, int size) throws IOException {
        URL url = new URL("https://crafatar.com/avatars/" + uuid + ".png?size=" + size);

        BufferedImage image = ImageIO.read(url);

        // We need to flip and rotate due to ImageIO's shitty nature.
        return flipAndRotate(image);
    }

    private static BufferedImage getFileBufferedImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        // We need to flip and rotate due to ImageIO's shitty nature.
        return flipAndRotate(image);
    }

    private static Color[][] getPixelColors(BufferedImage image) {
        Color[][] colors = new Color[image.getHeight()][image.getWidth()];

        for (int x = 0; x < image.getHeight(); x++) {
            for (int y = 0; y < image.getWidth(); y++) {
                int pixel = image.getRGB(x, y);

                Color color = new Color(pixel, true);

                colors[x][y] = color;
            }
        }

        return colors;
    }

    private static BufferedImage flipAndRotate(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();

        // rotate 90 clockwise
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-Math.PI / 2, w / 2.0, h / 2.0);
        AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage rotated = op.filter(image, null);

        // flip vertically
        affineTransform = AffineTransform.getScaleInstance(1, -1);
        affineTransform.translate(0, -h);
        op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return op.filter(rotated, null);
    }

    public static CompletableFuture<List<TextComponent>> getHeadMessage(UUID uuid, int size) {
        return CompletableFuture.supplyAsync(() -> {
            List<TextComponent> lines = new ArrayList<>();

            try {
                Color[][] headColors = getPixelColors(getHeadBufferedImage(uuid, size));

                for (Color[] headColor : headColors) {
                    TextComponent line = Component.empty();

                    for (Color color : headColor) {
                        line = line.append(Component.text("█")
                                .color(TextColor.color(color.getRed(), color.getGreen(), color.getBlue())));
                    }

                    lines.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lines;
        });
    }

    private static String centerMessage(String message, int len) {
        if (message == null || message.equals("")) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int CENTER_PX = 154;
        int toCompensate = CENTER_PX - halvedMessageSize - len;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public static void sendHeadMessage(Player player, int size) {
        UUID uuid = player.getUniqueId();

        getHeadMessage(uuid, size).whenCompleteAsync((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                for (TextComponent line : lines) {
                    player.sendMessage(line);
                }
            }
        });
    }

    public static void sendHeadMessage(Player player, List<String> message) {
        UUID uuid = player.getUniqueId();

        int headCount = 0;
        for (String s : message) {
            if (s.contains("{head}")) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                player.sendMessage(MineDown.parse(s));
            }
            return;
        }

        getHeadMessage(uuid, headCount).whenCompleteAsync((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                PlaceholderReplacer replacer = new PlaceholderReplacer().center("center");
                int headI = 0;
                for (String s : message) {
                    if (s.contains("{head}")) {
                        s = s.replace("{head}", MineDown.stringify(lines.get(headI)));
                        if (s.contains("{center}")) {
                            int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - MineDown.stringify(lines.get(headI)).length();
                            s = MineDown.stringify(lines.get(headI)) + centerMessage(s.replace("{center}", ""), len);
                        }
                        headI++;
                    }
                    player.sendMessage(MineDown.parse(replacer.parse(s)));
                }
            }
        });
    }

    public static void broadcastHeadMessage(UUID player, List<String> message) {

        int headCount = 0;
        for (String s : message) {
            if (s.contains("{head}")) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                Bukkit.broadcast(MineDown.parse(s));
            }
            return;
        }

        getHeadMessage(player, headCount).whenCompleteAsync((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                PlaceholderReplacer replacer = new PlaceholderReplacer().center("center");
                int headI = 0;
                for (String s : message) {
                    if (s.contains("{head}")) {
                        s = s.replace("{head}", MineDown.stringify(lines.get(headI)));
                        if (s.contains("{center}")) {
                            int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - MineDown.stringify(lines.get(headI)).length();
                            s = MineDown.stringify(lines.get(headI)) + centerMessage(s.replace("{center}", ""), len);
                        }
                        headI++;
                    }
                    Bukkit.broadcast(MineDown.parse(replacer.parse(s)));
                }
            }
        });
    }

    public static CompletableFuture<List<TextComponent>> getFileImageMessage(File file) {
        return CompletableFuture.supplyAsync(() -> {
            List<TextComponent> lines = new ArrayList<>();

            Color[][] headColors;
            try {
                headColors = getPixelColors(getFileBufferedImage(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Color[] headColor : headColors) {
                TextComponent line = Component.empty();

                for (Color color : headColor) {
                    line = line.append(Component.text("█")
                            .color(TextColor.color(color.getRed(), color.getGreen(), color.getBlue())));
                }

                lines.add(line);
            }
            return lines;
        });
    }

    public static CompletableFuture<List<String>> getFileImageMessageStr(File file) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> lines = new ArrayList<>();

            Color[][] headColors;
            try {
                headColors = getPixelColors(getFileBufferedImage(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Color[] headColor : headColors) {
                StringBuilder line = new StringBuilder();

                for (Color color : headColor) {
                    line.append(StringUtilsPaper.colorify("&#" + Integer.toHexString(color.getRGB()).substring(2) + "█"));
                }

                lines.add(line.toString());
            }
            return lines;
        });
    }

    public static void sendFileImageMessage(Player player, File file) {
        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                for (String line : lines) {
                    player.sendMessage(line);
                }
            }
        });
    }

    public static void sendFileImageMessage(Player player, File file, List<String> message, PlaceholderReplacer replacer) {
        int headCount = 0;
        for (String s : message) {
            if (s.contains("{image}")) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                player.sendMessage(StringUtilsPaper.colorify(replacer.parse(s)));
            }
            return;
        }

        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                int headI = 0;
                for (String s : message) {
                    if (s.contains("{image}")) {
                        if (s.contains("{center}")) {
                            int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines.get(headI).length();
                            s = lines.get(headI) + centerMessage(s.replace("{image}", "").replace("{center}", ""), len);
                        } else {
                            s = s.replace("{image}", lines.get(headI));
                        }
                        headI++;
                    }
                    player.sendMessage(StringUtilsPaper.colorify(replacer.parse(s)));
                }
            }
        });
    }

    public static void broadcastFileImageMessage(File file, List<String> message, PlaceholderReplacer replacer) {
        int headCount = 0;
        for (String s : message) {
            if (s.contains("{image}")) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                Bukkit.broadcastMessage(StringUtilsPaper.colorify(replacer.parse(s)));
            }
            return;
        }

        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            int headI = 0;
            for (String s : message) {
                if (s.contains("{image}")) {
                    if (s.contains("{center}")) {
                        int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines.get(headI).length();
                        s = lines.get(headI) + centerMessage(s.replace("{image}", "").replace("{center}", ""), len);
                    } else {
                        s = s.replace("{image}", lines.get(headI));
                    }
                    headI++;
                }
                Bukkit.broadcastMessage(StringUtilsPaper.colorify(replacer.parse(s)));
            }
        });
    }
}
