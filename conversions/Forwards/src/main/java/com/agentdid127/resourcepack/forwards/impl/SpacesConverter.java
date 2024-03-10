package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpacesConverter extends RPConverter {
    public SpacesConverter(PackConverter packConverter) {
        super(packConverter, "SpacesConverter", 1);
    }

    /**
     * Runs findFiles
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path assets = this.pack.getWorkingPath().resolve("assets");
        if (!assets.toFile().exists())
            return;
      this.findFiles(assets);
    }

    /**
     * Recursively finds files to fix Spaces
     * 
     * @param path
     * @throws IOException
     */
    protected void findFiles(Path path) throws IOException {
        if (path.toFile().exists()) {
            File directory = new File(path.toString());
            File[] fList = directory.listFiles();
            for (File file : fList) {
                String dir = this.fixSpaces(file.toPath());
                if (file.isDirectory())
                  this.findFiles(Paths.get(dir));
            }
        }
    }

    /**
     * Replaces spaces in files with underscores
     * 
     * @param path
     * @return
     * @throws IOException
     */
    protected String fixSpaces(Path path) throws IOException {
        if (!path.getFileName().toString().contains(" "))
            return path.toString();

        String noSpaces = path.getFileName().toString().replaceAll(" ", "_");

        Boolean ret = Util.renameFile(path, noSpaces);
        if (ret == null)
            return "null";

        if (ret && PackConverter.DEBUG) {
            Logger.log("      Renamed: " + path.getFileName().toString() + "->" + noSpaces);
            return path.getParent() + File.separator + noSpaces;
        } else if (!ret) {
            System.err.println("      Failed to rename: " + path.getFileName().toString() + "->" + noSpaces);
            return path.getParent() + File.separator + noSpaces;
        }

        return null;
    }
}
