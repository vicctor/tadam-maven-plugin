package org.jaxygen.maven.tadamplugin.utils;

import java.util.Date;

/**
 *
 * @author Artur
 */
public class SoundsRegistry {

        public static final String DEFAULT_SHORT_SOUND_FILE_NAME = "pssst-2.wav";
        public static final String DEFAULT_LONG_SOUND_FILE_NAME = "hey-sweetness-2.wav";
        public static int SHORT_COMPILATION_MAX_TIME_MS = 30 * 1000;
        private static final String SOUNDS[] = {
            DEFAULT_SHORT_SOUND_FILE_NAME,
            DEFAULT_LONG_SOUND_FILE_NAME
        };

        public String getSoundByCompilationTime(Date startTime, Date date) {
            long diff = date.getTime() - startTime.getTime() - SHORT_COMPILATION_MAX_TIME_MS;
            int index = 1;
            if (diff <= 0) {
                index = 0;
            }
            return SOUNDS[index];
        }
    }