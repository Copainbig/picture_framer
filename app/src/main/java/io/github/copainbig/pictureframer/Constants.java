package io.github.copainbig.pictureframer;

import lombok.Getter;
import org.apache.commons.cli.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Constants {
    public final static String INPUT_OPTION_SHORT_OPT = "i";
    private final static String INPUT_OPTION_DESCRIPTION = "Input source, if it is a valid image image, only this file will be frame, otherwise, all the images found in the directory will be framed. Default : \".\"";
    public final static String OUTPUT_OPTION_SHORT_OPT = "o";
    private final static String OUTPUT_OPTION_DESCRIPTION = "Output directory. Default :\"./framed\"";
    public final static String BACKGROUND_OPTION_SHORT_OPT = "b";
    private final static String BACKGROUND_OPTION_DESCRIPTION = "Background HEX colod code. Default : \"#FFFFFF\"";
    private final static String JPG_EXTENSION = "jpg";
    private Constants() {}

    public static final List<Option> cliOptions = new ArrayList(){{
        add(new Option(INPUT_OPTION_SHORT_OPT, "input", true, INPUT_OPTION_DESCRIPTION));
        add(new Option(OUTPUT_OPTION_SHORT_OPT, "output", true, OUTPUT_OPTION_DESCRIPTION));
        add(new Option(BACKGROUND_OPTION_SHORT_OPT, "background", true, BACKGROUND_OPTION_DESCRIPTION));
    }};

    public static final List<Option> SUPPORTED_FILE_FORMATS = new ArrayList(){{
        add(JPG_EXTENSION);
    }};
}
