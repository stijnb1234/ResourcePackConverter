package com.agentdid127.resourcepack.library.pack;

import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.library.utilities.BomDetector;
import com.agentdid127.converter.util.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class Pack {
    protected static final String CONVERTED_SUFFIX = "_converted";

    protected Path path;
    protected Handler handler;

    public Pack(Path path) {
        this.path = path;
        handler = this.createHandler();
    }

    public Handler createHandler() {
        return new Handler(this);
    }

    /**
     * Checks the type of pack it is.
     * 
     * @param path Pack Path
     * @return The Pack information
     */
    public static Pack parse(Path path) {
        if (!path.toString().contains(Pack.CONVERTED_SUFFIX))
            if (path.toFile().isDirectory() && path.resolve("pack.mcmeta").toFile().exists())
                return new Pack(path);
            else if (path.toString().endsWith(".zip"))
                return new ZipPack(path);
        return null;
    }

    public Path getOriginalPath() {
        return this.path;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public Path getWorkingPath() {
        return this.path.getParent().resolve(this.getFileName() + Pack.CONVERTED_SUFFIX);
    }

    public String getFileName() {
        return this.path.getFileName().toString();
    }

    @Override
    public String toString() {
        return "ResourcePack{" +
                "path=" + this.path +
                '}';
    }

    public static class Handler {
        protected Pack pack;

        public Handler(Pack pack) {
            this.pack = pack;
        }

        /**
         * Deletes existing conversions and sets up pack for conversion
         * 
         * @throws IOException Issues with conversion
         */
        public void setup() throws IOException {
            if (this.pack.getWorkingPath().toFile().exists()) {
                Logger.log("  Deleting existing conversion");
                Util.deleteDirectoryAndContents(this.pack.getWorkingPath());
            }

            Logger.log("  Copying existing pack");
            Util.copyDir(this.pack.getOriginalPath(), this.pack.getWorkingPath());

          Handler.bomRemover(this.pack.getWorkingPath());
        }

        static void bomRemover(Path workingPath) throws IOException {
            BomDetector bom = new BomDetector(
                    workingPath.toString(),
                    ".txt", ".json", ".mcmeta", ".properties", ".lang");
            int count = bom.findBOMs().size();
            if (count > 0)
                Logger.log("Removing BOMs from " + count + " files.");
            bom.removeBOMs();
        }

        public void finish() throws IOException {
        }

        @Override
        public String toString() {
            return "Handler{" +
                    "pack=" + this.pack +
                    '}';
        }
    }
}
