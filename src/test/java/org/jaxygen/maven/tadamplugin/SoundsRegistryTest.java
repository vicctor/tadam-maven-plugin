package org.jaxygen.maven.tadamplugin;

import java.util.Date;
import org.assertj.core.api.Assertions;
import org.jaxygen.maven.tadamplugin.utils.SoundsRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Artur
 */
public class SoundsRegistryTest {

    private SoundsRegistry soundsRegistry;
    
    public SoundsRegistryTest() {
    }
    
    @Before
    public void beforeTest() {
        soundsRegistry = new SoundsRegistry();
    }

    @Test
    public void shall_returnFirstMessageWhenCompilationIsVerryShort() {
        // given
        Date startTime = new Date();
        Date endTime = new Date();

        // when
        String resutl = soundsRegistry.getSoundByCompilationTime(startTime, endTime);
        
        // then
        Assertions.assertThat(resutl)
                .isEqualTo(soundsRegistry.DEFAULT_SHORT_SOUND_FILE_NAME);
    }
    
    @Test
    public void shall_returnFirstMessageWhenCompilationIsShortButCloseToLong() {
        // given
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + soundsRegistry.SHORT_COMPILATION_MAX_TIME_MS - 1);

        // when
        String resutl = soundsRegistry.getSoundByCompilationTime(startTime, endTime);
        
        // then
        Assertions.assertThat(resutl)
                .isEqualTo(soundsRegistry.DEFAULT_SHORT_SOUND_FILE_NAME);
    }

    @Test
    public void shall_returnFirstMessageWhenExactlyLimitExceeded() {
        // given
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + soundsRegistry.SHORT_COMPILATION_MAX_TIME_MS);

        // when
        String resutl = soundsRegistry.getSoundByCompilationTime(startTime, endTime);
        
        // then
        Assertions.assertThat(resutl)
                .isEqualTo(soundsRegistry.DEFAULT_SHORT_SOUND_FILE_NAME);
    }

    
    @Test
    public void shall_returnFirstMessageWhenCompilationIsLittleBitLongerThanShort() {
        // given
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + soundsRegistry.SHORT_COMPILATION_MAX_TIME_MS + 1);

        // when
        String resutl = soundsRegistry.getSoundByCompilationTime(startTime, endTime);
        
        // then
        Assertions.assertThat(resutl)
                .isEqualTo(soundsRegistry.DEFAULT_LONG_SOUND_FILE_NAME);
    }

}
