package carPartsStore;

import java.util.List;

public class Constants {
    static public final String AUTH_PATH = "/auth";
    static public final String CAR_MAKES_PATH = "/car-makes";
    static public final String CAR_MODELS_PATH = "/car-models";
    static public final String CAR_PARTS_PATH = "/car-parts";

    static public final List<String> PATHS = List.of(CAR_MAKES_PATH, CAR_MODELS_PATH, CAR_PARTS_PATH);
}
