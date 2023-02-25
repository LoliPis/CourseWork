package com.corsework.coursework.services;

import java.io.File;
import java.nio.file.Path;

public interface FileService {
    boolean saveToFile(String json, String dataFileName);

    String readFromFile(String dataFileName);

    File getDataFile(String dataFileName);

    Path createTempFile(String suffix);

    boolean cleanDataFile(String dataFileName);
}
