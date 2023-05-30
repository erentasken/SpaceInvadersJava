package Main;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {
	Clip clip1;
	Clip clip2;
	Clip clip3;
	Clip clip4;
	AudioInputStream audioStream1;
	AudioInputStream audioStream2;
	AudioInputStream audioStream3;
	boolean runOnce = true;
	
	URL gameSoundPath = getClass().getResource("/icons/gameOver/video-playback.wav");
	public SoundManager() {
        URL gameSoundURL = getClass().getResource("/sounds/video-playback.wav");
        try {
            audioStream1 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip1 = AudioSystem.getClip();
            clip1.open(audioStream1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/sounds/gameover.wav");
        try {
            audioStream1 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip2 = AudioSystem.getClip();
            clip2.open(audioStream1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/sounds/gunshot.wav");
        try {
        	audioStream2 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip3 = AudioSystem.getClip();
            clip3.open(audioStream2);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/sounds/enemyGunshot.wav");
        try {
        	audioStream3 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip4 = AudioSystem.getClip();
            clip4.open(audioStream3);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
	
	public void enemyGunShotSound() {
		clip4.setMicrosecondPosition(0);
        clip4.start();
    }
	
	public void gunShotSound() {
		clip3.setMicrosecondPosition(0);
        clip3.start();
    }
	
	public void startGameOverSound() {
		if(runOnce) {
			runOnce = false;
			System.out.println("i am running");
			clip2.start();
			decreaseVolume(clip2);
			decreaseVolume(clip2);
		}
	}
	
	public void startGameSound() {
		clip1.start();
		decreaseVolume(clip1);
		decreaseVolume(clip1);
	}
	
	public void stopGameSound() {
		clip1.stop();
	}
	
    private static void decreaseVolume(Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float currentVolume = gainControl.getValue();
        float decreaseAmount = 5.0f; // Adjust this value to decrease the volume by a different amount

        float newVolume = currentVolume - decreaseAmount;
        if (newVolume < gainControl.getMinimum()) {
            newVolume = gainControl.getMinimum();
        }
        gainControl.setValue(newVolume);
        System.out.println("Decreased volume by " + decreaseAmount + " dB. New volume: " + newVolume + " dB");
    }

    private static void increaseVolume(Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float currentVolume = gainControl.getValue();
        float increaseAmount = 5.0f; // Adjust this value to increase the volume by a different amount

        float newVolume = currentVolume + increaseAmount;
        if (newVolume > gainControl.getMaximum()) {
            newVolume = gainControl.getMaximum();
        }
        gainControl.setValue(newVolume);
        System.out.println("Increased volume by " + increaseAmount + " dB. New volume: " + newVolume + " dB");
    }

    private static void playSoundAgainWhenFinished(Clip clip) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.setMicrosecondPosition(0);
                clip.start();
            }
        });

        clip.start();
    }
}
