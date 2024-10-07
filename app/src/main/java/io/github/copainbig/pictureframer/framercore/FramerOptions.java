package io.github.copainbig.pictureframer.framercore;

import io.github.copainbig.pictureframer.Constants;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FramerOptions {
    private final static Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private final static String OUTPUT_FOLDER_NAME = "framed_outputs";
    private static final float DEFAULT_MARGIN_RATIO = 1.04f;
    private static final String FRAMED_FILE_PREFIX = "framed_";

    @Getter
    private List<File> inputFiles;
    @Getter
    private Path outputDirectory;
    @Getter
    private Map<File, File> inputFileToOutputFile;
    @Getter
    private Color backgroundColor;
    @Getter
    private float marginRatio;

    public FramerOptions(CommandLine cmd) throws IllegalArgumentException, IOException {
        validate(cmd);
        this.inputFiles = getInputFilesFromCommandLine(cmd);
        this.outputDirectory = getOutputDirectoryFromCommandLine(cmd);
        this.backgroundColor = getBackGroundColorFromCommandLine(cmd);
        this.marginRatio = DEFAULT_MARGIN_RATIO;
        inputFileToOutputFile = new HashMap();
        for(File input: inputFiles) {
            String filename = this.outputDirectory.toString() + File.separator + FRAMED_FILE_PREFIX + input.getName();
            inputFileToOutputFile.put(input, new File(filename));
        }
    }

    private void validate(CommandLine cmd) throws IllegalArgumentException {
        if(!cmd.hasOption(Constants.INPUT_OPTION_SHORT_OPT) || Objects.isNull(cmd.getOptionValue(Constants.INPUT_OPTION_SHORT_OPT)))
            throw new IllegalArgumentException("\"input\" argument is mandatory and seems to be missing or invalid");
    }

    private List<File> getInputFilesFromCommandLine(CommandLine cmd) throws FileNotFoundException{
        List<File> result = new ArrayList();
        File inputFile = new File(cmd.getOptionValue(Constants.INPUT_OPTION_SHORT_OPT));
        if(!inputFile.exists())
            throw new FileNotFoundException("The provided \"input\" file or directory doesn't seem to exist");
        if(inputFile.isDirectory()) {
            result.addAll(Stream.of(inputFile.listFiles())
                    .filter(file -> !file.isDirectory())
                    .filter(file -> Constants.SUPPORTED_FILE_FORMATS.contains(FilenameUtils.getExtension(file.getName().toLowerCase())))
                    .collect(Collectors.toList()));
        } else result.add(inputFile);
        return result;
    }

    private Path getOutputDirectoryFromCommandLine(CommandLine cmd) throws IllegalArgumentException, IOException {
        Path result = null;
        if(cmd.hasOption(Constants.OUTPUT_OPTION_SHORT_OPT)){
            String outputPathStr = cmd.getOptionValue(Constants.OUTPUT_OPTION_SHORT_OPT);
            if(!new File(outputPathStr).isDirectory())
                throw new IllegalArgumentException("\"output\" has to be a directory");
            result = Paths.get(outputPathStr).resolve(OUTPUT_FOLDER_NAME);
        } else {
            String inputPathStr = cmd.getOptionValue(Constants.INPUT_OPTION_SHORT_OPT);
            Path inputPath = Paths.get(inputPathStr);
            if(!new File(inputPathStr).isDirectory()) inputPath = inputPath.getParent();
            result = inputPath.resolve(OUTPUT_FOLDER_NAME);
        }
        createDirectoriesIfNeeded(result);
        return result;
    }

    private static void createDirectoriesIfNeeded(Path path) throws IOException{
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ioException) {
                String error = String.format("Issue encountered when trying to generate the output directorie(s).");
                throw new IllegalArgumentException(error, ioException);
            }
        }
    }

    private Color getBackGroundColorFromCommandLine(CommandLine cmd) throws NumberFormatException{
        if(cmd.hasOption(Constants.BACKGROUND_OPTION_SHORT_OPT)) return Color.decode(
                cmd.getOptionValue(Constants.BACKGROUND_OPTION_SHORT_OPT));
        return DEFAULT_BACKGROUND_COLOR;
    }

    public File getOutputFile(File inputFile) {
        if(!inputFileToOutputFile.containsKey(inputFile)) {
            String error = String.format("File %s is not a valid input file", inputFile);
            throw new IllegalArgumentException(error);
        }
        return inputFileToOutputFile.get(inputFile);
    }
}
