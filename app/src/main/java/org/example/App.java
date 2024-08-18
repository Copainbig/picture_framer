package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    private final static int FILE_NOT_FOUND = 1;
    private final static int ERROR_CREATING_OUTPUT_FILE = 2;
    private final static int ERROR_CREATING_OUTPUT_DIRECTORIES = 3;
    private final static String OUTPUT_FOLDER_NAME = "framed_outputs";
    private final static String OUTPUT_IMAGES_SUFFIX = "framed";
    public static void main(String[] args) {
        String input = args[0];
        Path outputDirectoryPath = getOutputPath(input);

        createDirectoriesIfNeeded(outputDirectoryPath);

        List<String> inputs = new ArrayList();

        File inputFile = new File(input);
        if(inputFile.isDirectory()) {
            inputs.addAll(Stream.of(inputFile.listFiles())
                    .filter(file -> !file.isDirectory())
                    .filter(file -> file.getName().endsWith(".jpg"))
                    .map(file -> file.toString())
                    .collect(Collectors.toList()));

        } else {
            inputs.add(input);
        }

        int imagesCount = inputs.size();
        int count = 1;
        for(String inputFilePath : inputs) {
            showProgression(imagesCount, count);
            String outputFilePath = outputDirectoryPath.resolve(getOutputFileName(inputFilePath)).toString();
            addFrameToImage(inputFilePath, outputFilePath);
            count++;
        }

        System.out.println("\n PROCESSING SUCCESSFUL! Images stored at " + outputDirectoryPath);
    }

    private static void showProgression(int totalCount, int doneCount) {
        int total_length = 100;
        int donePercentage = (total_length / totalCount) * doneCount;
        StringBuilder str = new StringBuilder();
        while(str.length() <= donePercentage) str.append('█');
        while(str.length() < 100) str.append('▁');
        String output = "\r Progress : " + str + " | (" + doneCount + "/" + totalCount + ")";
        try {
            System.out.write(output.getBytes());
        } catch (IOException exception) {
            // TODO
        }
    }

    private static Path getOutputPath(String inputPathStr) {
        Path inputPath = Paths.get(inputPathStr);
        if(!new File(inputPathStr).isDirectory()) inputPath = inputPath.getParent();
        return inputPath.resolve(OUTPUT_FOLDER_NAME);
    }

    private static String getOutputFileName(String inputFile) {
        Path inputPath = Paths.get(inputFile);
        String fileNameWithoutExtension = inputPath.getFileName().toString()
                .substring(0, inputPath.getFileName().toString().lastIndexOf('.'));
        return fileNameWithoutExtension + OUTPUT_IMAGES_SUFFIX + ".jpg";
    }

    private static void createDirectoriesIfNeeded(Path path) {
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException exception) {
                System.out.println("Issue encountered when trying to generate the output directories. Exiting.");
                System.exit(ERROR_CREATING_OUTPUT_DIRECTORIES);
            }
        }
    }

    private static void addFrameToImage(String input, String output) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(input));
        } catch(IOException exception) {
            System.out.println("File not found at specified path. Exiting.");
            System.exit(FILE_NOT_FOUND);
        }

        int height = img.getHeight();
        int width = img.getWidth();

        int frameSquareSide = Math.round(Math.max(height, width)*1.04f);

        BufferedImage outputImage = new BufferedImage(frameSquareSide, frameSquareSide, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = outputImage.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, frameSquareSide, frameSquareSide);

        int x = Math.round((frameSquareSide-width)/2);
        int y = Math.round((frameSquareSide-height)/2);


        g.drawImage(img, x, y, null);
        g.dispose();

        try {
            ImageIO.write(outputImage, "jpg", new FileOutputStream(output));
        } catch (IOException exception) {
            System.out.println("Issue encountered when trying to generate the output file. Exiting.");
            System.exit(ERROR_CREATING_OUTPUT_FILE);
        }
        img.flush();
    }
}