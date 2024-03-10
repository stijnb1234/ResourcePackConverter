package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.utilities.ImageConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class PaintingConverter extends RPConverter {
    private Path paintingPath;
    private ImageConverter normal;
    ArrayList<String> paintings = new ArrayList<>();

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
      this.paintingPath = this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "painting" + File.separator);
        if (!this.paintingPath.toFile().exists()) return;

        File[] paintingFiles = this.paintingPath.toFile().listFiles();
        String filename = "";
        for (File file : paintingFiles) {
            if (file.getName().endsWith(".png")) {
                filename = file.getName();
                break;
            }
        }

      this.normal = new ImageConverter(16, 16, this.paintingPath.resolve(filename));
      this.normal.newImage(256, 256);

        // 16x16
      this.painting(this.paintingPath, "kebab.png", 0, 0, 1, 1);
      this.painting(this.paintingPath, "aztec.png", 16, 0, 1, 1);
      this.painting(this.paintingPath, "alban.png", 32, 0, 1, 1);
      this.painting(this.paintingPath, "aztec2.png", 48, 0, 1, 1);
      this.painting(this.paintingPath, "bomb.png", 64, 0, 1, 1);
      this.painting(this.paintingPath, "plant.png", 80, 0, 1, 1);
      this.painting(this.paintingPath, "wasteland.png", 96, 0, 1, 1);
      this.painting(this.paintingPath, "back.png", 192, 0, 1, 1);

        // 32x16
      this.painting(this.paintingPath, "pool.png", 0, 32, 2, 1);
      this.painting(this.paintingPath, "courbet.png", 32, 32, 2, 1);
      this.painting(this.paintingPath, "sea.png", 64, 32, 2, 1);
      this.painting(this.paintingPath, "sunset.png", 96, 32, 2, 1);
      this.painting(this.paintingPath, "creebet.png", 128, 32, 2, 1);

        // 16x3
      this.painting(this.paintingPath, "wanderer.png", 0, 64, 1, 2);
      this.painting(this.paintingPath, "graham.png", 16, 64, 1, 2);

        // 64x48
      this.painting(this.paintingPath, "skeleton.png", 192, 64, 4, 3);
      this.painting(this.paintingPath, "donkey_kong.png", 192, 112, 4, 3);

        // 64x32
      this.painting(this.paintingPath, "fighters.png", 0, 96, 4, 2);

        // 32x32
      this.painting(this.paintingPath, "match.png", 0, 128, 2, 2);
      this.painting(this.paintingPath, "bust.png", 32, 128, 2, 2);
      this.painting(this.paintingPath, "stage.png", 64, 128, 2, 2);
      this.painting(this.paintingPath, "void.png", 96, 128, 2, 2);
      this.painting(this.paintingPath, "skull_and_roses.png", 128, 128, 2, 2);
      this.painting(this.paintingPath, "wither.png", 160, 128, 2, 2);

        // 64x64
      this.painting(this.paintingPath, "pointer.png", 0, 192, 4, 4);
      this.painting(this.paintingPath, "pigscene.png", 64, 192, 4, 4);
      this.painting(this.paintingPath, "burning_skull.png", 128, 192, 4, 4);

      this.normal.store(this.paintingPath.resolve("paintings_kristoffer_zetterstrand.png"));
        for (String item : this.paintings)
            Files.deleteIfExists(this.paintingPath.resolve(item));
    }

    private void painting(Path paintingPath, String name, int x, int y, int scaleX, int scaleY) throws IOException {
        if (paintingPath.resolve(name).toFile().exists()) {
          this.normal.addImage(paintingPath.resolve(name), x, y);
          this.paintings.add(name);
        }
    }
}
