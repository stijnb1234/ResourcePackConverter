package com.agentdid127.resourcepack.library.utilities;

import com.agentdid127.converter.util.Logger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class ImageConverter {
    // Instance Variables
    protected int width;
    protected int height;
    protected int defaultW = 1;
    protected int defaultH = 1;
    protected BufferedImage image;
    protected BufferedImage newImage;
    protected Path location;
    protected Graphics2D g2d;
    protected int wMultiplier = 1;
    protected int hMultiplier = 1;

    // Default Constructor
    public ImageConverter(int defaultWIn, int defaultHIn, Path locationIn) throws IOException {
      this.image = ImageIO.read(locationIn.toFile());
        if (this.isPowerOfTwo(this.image.getWidth()) && this.isPowerOfTwo(this.image.getHeight())) {
          this.newImage = this.image;
          this.location = locationIn;
          this.defaultW = defaultWIn;
          this.defaultH = defaultHIn;
          this.width = this.image.getWidth();
          this.height = this.image.getHeight();
          this.wMultiplier = this.width / this.defaultW;
          this.hMultiplier = this.height / this.defaultH;
            // Make sure to not have 0 multiplier or cause issues!
          this.wMultiplier = this.wMultiplier == 0 ? 1 : this.wMultiplier;
          this.hMultiplier = this.hMultiplier == 0 ? 1 : this.hMultiplier;
        } else {
            Logger.log("File is not a power of 2. Converting image to be so.");
          this.newImage = new BufferedImage((int) Math.ceil(Math.log(this.image.getWidth()) / Math.log(2)),
                    (int) Math.ceil(Math.log(this.image.getHeight()) / Math.log(2)), this.image.getType());
          this.width = (int) Math.ceil(Math.log(this.image.getWidth()) / Math.log(2));
          this.defaultW = defaultWIn;
          this.defaultH = defaultHIn;
          this.height = (int) Math.ceil(Math.log(this.image.getHeight()) / Math.log(2));
            Graphics2D g = this.newImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(this.image, 0, 0, this.width, this.height, 0, 0, this.image.getWidth(), this.image.getHeight(), null);
            g.dispose();
          this.defaultW = defaultWIn;
          this.width = this.image.getWidth();
          this.height = this.image.getHeight();
          this.location = locationIn;
          this.wMultiplier = this.width / this.defaultW;
          this.hMultiplier = this.height / this.defaultH;
            // Make sure to not have 0 multiplier or cause issues!
          this.wMultiplier = this.wMultiplier == 0 ? 1 : this.wMultiplier;
          this.hMultiplier = this.hMultiplier == 0 ? 1 : this.hMultiplier;
        }
    }

    public boolean fileIsPowerOfTwo() {
        return (this.isPowerOfTwo(this.image.getWidth()) && this.isPowerOfTwo(this.image.getHeight()));
    }

    public void setImage(int defaultWIn, int defaultHIn) throws IOException {
      this.image = this.newImage;
        if (this.isPowerOfTwo(this.image.getWidth()) && this.isPowerOfTwo(this.image.getHeight())) {
          this.defaultW = defaultWIn;
          this.width = this.image.getWidth();
          this.defaultH = defaultHIn;
          this.height = this.image.getHeight();
          this.wMultiplier = this.image.getWidth() / this.defaultW;
          this.hMultiplier = this.image.getHeight() / this.defaultH;
        } else
            Logger.log("File is not a power of 2");
    }

    // Creates a new Image to store
    public void newImage(int newWidth, int newHeight) {
      this.newImage = new BufferedImage(newWidth * this.wMultiplier, newHeight * this.hMultiplier, BufferedImage.TYPE_INT_ARGB);
      this.g2d = (Graphics2D) this.newImage.getGraphics();
    }

    public void addImage(Path imagePath, int x, int y) throws IOException {
        if (!imagePath.toFile().exists())
            return;
        BufferedImage image = ImageIO.read(imagePath.toFile());
      this.g2d.drawImage(image, x * this.wMultiplier, y * this.hMultiplier, null);
    }

    // Takes part of an image and stores it in the new image
    public void subImage(int x, int y, int x2, int y2, int storex, int storey) {
        int x3;
        int y3;
        int width2 = x2 * this.wMultiplier - x * this.wMultiplier;
        int height2 = y2 * this.hMultiplier - y * this.hMultiplier;
        x3 = x == 0 ? 0 : x * this.wMultiplier;
        y3 = y == 0 ? 0 : y * this.hMultiplier;
        BufferedImage part = this.subImage2(x3, y3, width2, height2);
      this.g2d.drawImage(part, storex * this.wMultiplier, storey * this.hMultiplier, null);
    }

    public void subImage(int x, int y, int x2, int y2) {
      this.subImage(x, y, x2, y2, 0, 0);
    }

    // Takes a part of an image and flips it either horizontally or vertically
    public void subImage(int x, int y, int x2, int y2, int storex, int storey, boolean flip) {
        int x3;
        int y3;
        int width2 = x2 * this.wMultiplier - x * this.wMultiplier;
        int height2 = y2 * this.hMultiplier - y * this.hMultiplier;
        x3 = x == 0 ? 0 : x * this.wMultiplier;
        y3 = y == 0 ? 0 : y * this.hMultiplier;
        BufferedImage part = this.subImage2(x3, y3, width2, height2);
      this.g2d.drawImage(ImageConverter.createFlipped(part, flip), storex * this.wMultiplier, storey * this.hMultiplier, null);
    }

    // Only allows for the number 1 and flips it both horizontally and vertically
    public void subImage(int x, int y, int x2, int y2, int storex, int storey, int flip) {
        int x3;
        int y3;
        int width2 = x2 * this.wMultiplier - x * this.wMultiplier;
        int height2 = y2 * this.hMultiplier - y * this.hMultiplier;
        x3 = x == 0 ? 0 : x * this.wMultiplier;
        y3 = y == 0 ? 0 : y * this.hMultiplier;
        BufferedImage part = this.subImage2(x3, y3, width2, height2);
      this.g2d.drawImage(ImageConverter.createFlipped(part, flip), storex * this.wMultiplier, storey * this.hMultiplier, null);
    }

    public void colorize(Color rgb) {
      this.g2d.setPaint(rgb);
      this.g2d.drawImage(this.image, 0, 0, null);
      this.g2d.fillRect(0, 0, this.newImage.getWidth(), this.newImage.getHeight());
    }

    public void grayscale() {
        BufferedImage gray = new BufferedImage(this.newImage.getWidth(), this.newImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        gray.createGraphics().drawImage(this.image, 0, 0, null);
      this.newImage = gray;
    }

    private static BufferedImage toRGB(BufferedImage i) {
        BufferedImage rgb = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
        rgb.createGraphics().drawImage(i, 0, 0, null);
        return rgb;
    }

    // Allows for the flip to happen
    private static BufferedImage createFlipped(BufferedImage image2, int flip) {
        AffineTransform at = new AffineTransform();
        if (flip != 1)
            return image2;
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image2.getHeight()));
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image2.getWidth(), 0));
        return ImageConverter.createTransformed(image2, at);
    }

    // Does the flip for the image (boolean version)
    private static BufferedImage createFlipped(BufferedImage image2, boolean flip) {
        AffineTransform at = new AffineTransform();
        if (flip) {
            at.concatenate(AffineTransform.getScaleInstance(1, -1));
            at.concatenate(AffineTransform.getTranslateInstance(0, -image2.getHeight()));
        } else {
            at.concatenate(AffineTransform.getScaleInstance(-1, 1));
            at.concatenate(AffineTransform.getTranslateInstance(-image2.getWidth(), 0));
        }
        return ImageConverter.createTransformed(image2, at);
    }

    // Transforms the BufferedImage
    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    // Stores the image
    public boolean store() throws IOException {
        ImageIO.write(this.newImage, "png", this.location.toFile());
        return true;
    }

    public boolean store(Path locationIn) throws IOException {
        ImageIO.write(this.newImage, "png", locationIn.toFile());
        return true;
    }

    // Gets a sub image
    private BufferedImage subImage2(int x, int y, int width, int height) {
        return this.image.getSubimage(x, y, width, height);
    }

    // Returns the Width and Height variables
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    // Returns the Width Multiplier and Height Multiplier variables
    public int getWidthMultiplier() {
        return this.wMultiplier;
    }

    public int getHeightMultiplier() {
        return this.hMultiplier;
    }

    // Detects if file is a power of two.
    private boolean isPowerOfTwo(int n) {
        return n > 0 && n == Math.pow(2, Math.round(Math.log(n) / Math.log(2)));
    }

    // Detects if file is a square
    public boolean isSquare() {
        return this.width == this.height;
    }
}
