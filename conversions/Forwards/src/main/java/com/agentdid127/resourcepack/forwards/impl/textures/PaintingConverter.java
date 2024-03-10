package com.agentdid127.resourcepack.forwards.impl.textures;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.utilities.ImageConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class PaintingConverter extends RPConverter {
    public PaintingConverter(PackConverter packConverter) {
        super(packConverter, "PaintingConverter", 1);
    }

    /**
     * Remaps painting image to multiple images.
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path paintingPath = this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator
                + "textures" + File.separator + "painting" + File.separator);
        if (!paintingPath.toFile().exists())
            return;

        // 16x16
      this.painting(paintingPath, "kebab.png", 0, 0, 1, 1);
      this.painting(paintingPath, "aztec.png", 16, 0, 1, 1);
      this.painting(paintingPath, "alban.png", 32, 0, 1, 1);
      this.painting(paintingPath, "aztec2.png", 48, 0, 1, 1);
      this.painting(paintingPath, "bomb.png", 64, 0, 1, 1);
      this.painting(paintingPath, "plant.png", 80, 0, 1, 1);
      this.painting(paintingPath, "wasteland.png", 96, 0, 1, 1);
      this.painting(paintingPath, "back.png", 192, 0, 1, 1);

        // 32x16
      this.painting(paintingPath, "pool.png", 0, 32, 2, 1);
      this.painting(paintingPath, "courbet.png", 32, 32, 2, 1);
      this.painting(paintingPath, "sea.png", 64, 32, 2, 1);
      this.painting(paintingPath, "sunset.png", 96, 32, 2, 1);
      this.painting(paintingPath, "creebet.png", 128, 32, 2, 1);

        // 16x3
      this.painting(paintingPath, "wanderer.png", 0, 64, 1, 2);
      this.painting(paintingPath, "graham.png", 16, 64, 1, 2);

        // 64x48
      this.painting(paintingPath, "skeleton.png", 192, 64, 4, 3);
      this.painting(paintingPath, "donkey_kong.png", 192, 112, 4, 3);

        // 64x32
      this.painting(paintingPath, "fighters.png", 0, 96, 4, 2);

        // 32x32
      this.painting(paintingPath, "match.png", 0, 128, 2, 2);
      this.painting(paintingPath, "bust.png", 32, 128, 2, 2);
      this.painting(paintingPath, "stage.png", 64, 128, 2, 2);
      this.painting(paintingPath, "void.png", 96, 128, 2, 2);
      this.painting(paintingPath, "skull_and_roses.png", 128, 128, 2, 2);
      this.painting(paintingPath, "wither.png", 160, 128, 2, 2);

        // 64x64
      this.painting(paintingPath, "pointer.png", 0, 192, 4, 4);
      this.painting(paintingPath, "pigscene.png", 64, 192, 4, 4);
      this.painting(paintingPath, "burning_skull.png", 128, 192, 4, 4);

        if (paintingPath.resolve("paintings_kristoffer_zetterstrand.png").toFile().exists())
            paintingPath.resolve("paintings_kristoffer_zetterstrand.png").toFile().delete();
    }

    private void painting(Path paintingPath, String name, int x, int y, int scaleX, int scaleY) throws IOException {
        int defaultW = 16, defaultH = 16;
        if (paintingPath.resolve("paintings_kristoffer_zetterstrand.png").toFile().exists()) {
            ImageConverter normal = new ImageConverter(256, 256,
                    paintingPath.resolve("paintings_kristoffer_zetterstrand.png"));
            if (!normal.fileIsPowerOfTwo())
                return;
            normal.newImage(defaultW * scaleX, defaultH * scaleY);
            normal.subImage(x, y, x + defaultW * scaleX, y + defaultH * scaleY, 0, 0);
            normal.store(paintingPath.resolve(name));
        }
    }
}
