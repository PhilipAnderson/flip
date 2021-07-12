package flip.raspberry;

import flip.common.entities.trial.ToneDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

import javafx.scene.media.AudioClip;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public final class TonePlayer {

    Logger LOG = LoggerFactory.getLogger(getClass());

    private final Map<ToneDefinition, AudioClip> toneCache = new ConcurrentHashMap<>();

    @Value("${flip.tone.cache.directory}")
    String toneCacheDirectory;

    AudioClip currentTone;


    public synchronized void play(ToneDefinition tone) {
        if (currentTone != null && currentTone.isPlaying()) {
            LOG.debug("Not playing tone due to tone already being played.");
            return;
        }

        LOG.debug("Playing tone {}.", tone);

        AudioClip clip = toneCache.computeIfAbsent(tone, this::createTone);
        currentTone = clip;
        currentTone.play();
    }

    private AudioClip createTone(ToneDefinition tone) {

        String toneFilename = toneCacheDirectory + "/" + tone.getFrequency() + "-" + tone.getDuration() + ".wav";

        if (!(new File(toneFilename).exists())) {
            String ffmpegDef = "sine=frequency=" + tone.getFrequency() + ":duration=" + tone.getDuration() / 1000.0;

            LOG.info("Creating tone: {}", ffmpegDef);

            try {
                Process process = new ProcessBuilder("ffmpeg", "-f", "lavfi", "-i", ffmpegDef, toneFilename) .start();
                process.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return new AudioClip("file:" + toneFilename);
    }
}
