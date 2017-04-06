package org.jaxygen.maven.tadamplugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.jaxygen.maven.tadamplugin.utils.SoundsRegistry;

/**
 * Goal which makes a sound at the end of compilation.
 *
 */
@Mojo(name = "tadam", defaultPhase = LifecyclePhase.INITIALIZE)
public class TadamMojo
        extends AbstractMojo {



    private static SoundsRegistry soundsRegistory = new SoundsRegistry();
    private static boolean hookRegistered = false;
    private Date startTime;
    
    
    public void execute()
            throws MojoExecutionException {
        if (!hookRegistered) {
            startTime = new Date();
            getLog().info("Initializing TADAM plugin, so keep easy, and wait for a sound");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    playSound();
                }
            });
            hookRegistered = true;
        }
    }

    private void playSound() {
        AudioInputStream audioInputStream = null;
        InputStream is = null;
        try {
            getLog().info("Done my Lord!");

            final String path = TadamMojo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(path);
            final URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});

            final String soundFileName = soundsRegistory.getSoundByCompilationTime(startTime, new Date());

            is = loader.getResourceAsStream(soundFileName);

            InputStream isProxy = new InputStream() {
                InputStream is = loader.getResourceAsStream(soundFileName);

                @Override
                public int read() throws IOException {
                    return is.read();
                }

                @Override
                public synchronized void reset() throws IOException {
                    is = loader.getResourceAsStream(soundFileName);
                }
            };

            DataLine.Info info;
            audioInputStream = AudioSystem.getAudioInputStream(isProxy);
            AudioFormat format = audioInputStream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            getLog().debug("LineInfo: " + info);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000 + 20);

        } catch (Exception ex) {
            Logger.getLogger(TadamMojo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (audioInputStream != null) {
                    audioInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TadamMojo.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TadamMojo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String... args) {
        try {
            new TadamMojo().execute();
        } catch (MojoExecutionException ex) {
            Logger.getLogger(TadamMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
