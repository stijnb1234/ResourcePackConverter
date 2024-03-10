package com.agentdid127.resourcepack.backwards.impl.textures;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.library.utilities.ImageConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Converts the Compass Image to work in older versions of the game.
 */
public class CompassConverter extends RPConverter {
    private final int from;
    private final int to;
    private Path items;

  /**
   * Main Constructor for a Compass Converter.
   * @param packConverter Pack Converter to use.
   * @param from Version we are converting from.
   * @param to Version we are converting to.
   */
    public CompassConverter(PackConverter packConverter, int from, int to) {
        super(packConverter, "CompassConverter", 1);
        this.from = from;
        this.to = to;
    }

  /**
   * Converts the Compass files.
   * @throws IOException if the images fail.
   */
  @Override
    public void convert() throws IOException {
        String itemsT = "items";
        if (this.to > Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
          itemsT = "item";
        }
        Path compassPath = this.pack.getWorkingPath().resolve(
            "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator
                + itemsT + File.separator + "compass.png");
      this.items = compassPath.getParent();
        if (compassPath.toFile().exists()) {
          ImageConverter imageConverter = new ImageConverter(16, 16 * 32, compassPath);
          if (imageConverter.fileIsPowerOfTwo()) {

            for (int i = 0; i < 32; i++) {
              int h = i * 16;
              String it = String.valueOf(i);
              if (i < 10) {
                it = "0" + it;
              } // if
              imageConverter.newImage(16, 16);
              imageConverter.subImage(0, h, 16, h + 16);
              imageConverter.store(this.items.resolve(it + ".png"));
            } // for

            if (this.items.resolve("compass.png.mcmeta").toFile().exists()) {
              this.items.resolve("compass.png.mcmeta").toFile().delete();
            } // if
            compassPath.toFile().delete();
          } // for
        } // if
    } // convert
} // CompassConverter
