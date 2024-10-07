package io.github.copainbig.pictureframer;

import io.github.copainbig.pictureframer.framercore.FramerOptions;
import io.github.copainbig.pictureframer.framercore.ImageFramer;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class App {
        public static void main(String[] args) {
        Options cliOptions = new Options();
        for(Option option : Constants.cliOptions){
            cliOptions.addOption(option);
        }

        CommandLineParser parser = new DefaultParser(false);
        try {
            FramerOptions framerOptions = new FramerOptions(parser.parse(cliOptions, args));
            ImageFramer framer = new ImageFramer();
            for(File inputFile : framerOptions.getInputFiles()){
                // TODO display progress bar ?
                framer.addFrameToImage(inputFile, framerOptions.getOutputFile(inputFile), framerOptions.getMarginRatio(), framerOptions.getBackgroundColor());
            }
            System.out.println("\n PROCESSING SUCCESSFUL! Images stored at " + framerOptions.getOutputDirectory());
        } catch (ParseException parseException) {
            System.err.println(parseException);
            System.err.println("Error when parsing command line arguments. Exiting.");
            System.exit(-1);
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println(fileNotFoundException);
            System.exit(-1);
        } catch (IllegalArgumentException illegalArgumentException) {
            System.err.println(illegalArgumentException);
            System.exit(-1);
        } catch (IOException ioException) {
            System.err.println(ioException);
            System.exit(-1);
        }
        System.exit(0);
    }
}