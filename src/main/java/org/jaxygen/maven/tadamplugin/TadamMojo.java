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

/**
 * Goal which touches a timestamp file.
 *
 * @deprecated Don't use!
 */
@Mojo(name = "tadam", defaultPhase = LifecyclePhase.INSTALL)
public class TadamMojo
        extends AbstractMojo {

    private static boolean hookRegistered = false;

    public void execute()
            throws MojoExecutionException {
        if (!hookRegistered) {
            getLog().info("Initialize TADAM plugin");
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
            getLog().info("Done my lord!");

            final String path = TadamMojo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(path);
            final URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});

            is = loader.getResourceAsStream(DEFAULT_SOUND_FILE_NAME);

            InputStream isProxy = new InputStream() {

                InputStream is = loader.getResourceAsStream(DEFAULT_SOUND_FILE_NAME);

                @Override
                public int read() throws IOException {
                    return is.read();
                }

                @Override
                public synchronized void reset() throws IOException {
                    is = loader.getResourceAsStream(DEFAULT_SOUND_FILE_NAME);
                }
            };

            DataLine.Info info;
            audioInputStream = AudioSystem.getAudioInputStream(isProxy);
            AudioFormat format = audioInputStream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            System.out.println("LineInfo: " + info);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(1000);
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
    private static final String DEFAULT_SOUND_FILE_NAME = "pssst-2.wav";

    public static void main(String... args) {
        try {
            new TadamMojo().execute();
        } catch (MojoExecutionException ex) {
            Logger.getLogger(TadamMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
