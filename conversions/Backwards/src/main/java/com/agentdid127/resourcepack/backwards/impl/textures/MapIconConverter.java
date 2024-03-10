package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.library.pack.Pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapIconConverter extends RPConverter {
    protected Map<Long, Long> mapping = new HashMap<>();

    public MapIconConverter(PackConverter packConverter) {
        super(packConverter, "MapIconConverter", 1);
        this.mapping.put(this.pack(0, 0), this.pack(0, 0));
        this.mapping.put(this.pack(8, 0), this.pack(8, 0));
        this.mapping.put(this.pack(16, 0), this.pack(16, 0));
        this.mapping.put(this.pack(24, 0), this.pack(24, 0));
        this.mapping.put(this.pack(32, 0), this.pack(0, 8));
        this.mapping.put(this.pack(40, 0), this.pack(8, 8));
        this.mapping.put(this.pack(48, 0), this.pack(16, 8));
        this.mapping.put(this.pack(56, 0), this.pack(24, 8));
        this.mapping.put(this.pack(64, 0), this.pack(0, 16));
        this.mapping.put(this.pack(72, 0), this.pack(8, 16));
    }

    /**
     * Converts maps
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path imagePath = this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator
                + "textures" + File.separator + "map" + File.separator + "backwards/map_icons.png");
        if (!imagePath.toFile().exists())
            return;

        BufferedImage newImage = Util.readImageResource("/backwards/map_icons.png");
        if (newImage == null)
            throw new NullPointerException();
        Graphics2D g2d = (Graphics2D) newImage.getGraphics();

        BufferedImage image = ImageIO.read(imagePath.toFile());
        int scale = image.getWidth() / 32;

        for (int x = 0; x <= 32 - 8; x += 8) {
            for (int y = 0; y <= 32 - 8; y += 8) {
                Long mapped = this.mapping.get(this.pack(x, y));
                if (mapped == null)
                    continue;

                int newX = (int) (mapped >> 32);
                int newY = (int) (long) mapped;
                Logger.log("      Mapping " + x + "," + y + " to " + newX + "," + newY);

                g2d.drawImage(image.getSubimage(x * scale, y * scale, 8 * scale, 8 * scale), newX * scale, newY * scale,
                        null);
            }
        }

        ImageIO.write(newImage, "png", imagePath.toFile());
    }

    protected long pack(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }
}
