package de.leetgeeks.jgl.leapx.sound;

import de.leetgeeks.jgl.util.ResourceUtil;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 24.07.2015
 * Time: 22:36
 */
public class AudioEngine implements LineListener {
    private int bufferId;

    private SoundSystem mySoundSystem;

    public AudioEngine() {
    }

    public void init() throws Exception {
        // Load wav data into a buffer.
        /*bufferId = AL10.alGenBuffers();

        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            throw new Exception("Unable to allocate OpenAL buffer");*/

        try
        {
            SoundSystemConfig.addLibrary( LibraryJavaSound.class );
            SoundSystemConfig.setCodec( "wav", CodecWav.class );
            SoundSystemConfig.setCodec( "ogg", CodecJOgg.class );
        }
        catch( SoundSystemException e )
        {
            System.err.println("error linking with the pluggins" );
        }


        mySoundSystem = new SoundSystem();

        // Create a new ambient, looping source called "Cool Music":
        final File soundFile = ResourceUtil.getResourceFile("/sounds/ambient.wav", this.getClass());
        //final File soundFile2 = ResourceUtil.getResourceFile("/sounds/explosion.wav", this.getClass());
        mySoundSystem.backgroundMusic( "Background music", "ambient.wav", true );

        // Play the source:
        mySoundSystem.play("Background music");

    }

    public void explosion() {
        mySoundSystem.quickPlay(false, "explosion.wav", false,
                20, 0, 0,
                SoundSystemConfig.ATTENUATION_ROLLOFF,
                SoundSystemConfig.getDefaultRolloff()
        );
    }

    @Override
    public void update(LineEvent event) {

    }
}
