package io.github.copainbig.pictureframer.framercore;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFramer {
    // TODO for now we only handly input and outputs as JPG files.
    private static final String OUTPUT_EXTENSION = "jpg";
    public ImageFramer() {
    }

    public void addFrameToImage(File inputFile, File outputFile, float marginRatio, Color backgroundColor)
            throws IOException {
        BufferedImage img = null;
        try{
            img = ImageIO.read(inputFile);
        } catch (IOException ioException) {
            throw new IOException("File not found at specified path", ioException);
        }
        int height = img.getHeight();
        int width = img.getWidth();
        int frameSquareSide = Math.round(Math.max(height, width) * marginRatio);

        BufferedImage outputImage = new BufferedImage(frameSquareSide, frameSquareSide, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = outputImage.createGraphics();
        g.setPaint(backgroundColor);
        g.fillRect(0, 0, frameSquareSide, frameSquareSide);
        int x = Math.round((frameSquareSide-width)/2);
        int y = Math.round((frameSquareSide-height)/2);
        g.drawImage(img, x, y, null);
        g.dispose();
        try {
            ImageIO.write(outputImage, OUTPUT_EXTENSION, new FileOutputStream(outputFile));
        } catch (IOException ioException) {
            String error = String.format("Issue encountered when trying to create the output file : %s.", outputFile.getPath());
            throw new IOException(error, ioException);
        }
        outputImage.flush();
        img.flush();
    }
}
