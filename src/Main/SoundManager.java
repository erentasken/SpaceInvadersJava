package Main;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {
	private Clip clip1;
	private Clip clip2;
	private Clip clip3;
	private Clip clip4;
	private Clip clip5;
	private Clip clip6;
	private AudioInputStream audioStream1;
	private AudioInputStream audioStream2;
	private AudioInputStream audioStream3;
	private AudioInputStream audioStream4;
	private AudioInputStream audioStream5;
	private boolean runOnce = true;
	private static boolean gameOver= false;
	
	public SoundManager() {
        URL gameSoundURL = getClass().getResource("/resources/sounds/video-playback.wav");
        try {
            audioStream1 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip1 = AudioSystem.getClip();
            clip1.open(audioStream1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/resources/sounds/gameover.wav");
        try {
            audioStream1 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip2 = AudioSystem.getClip();
            clip2.open(audioStream1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/resources/sounds/gunshot.wav");
        try {
        	audioStream2 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip3 = AudioSystem.getClip();
            clip3.open(audioStream2);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/resources/sounds/enemyGunshot.wav");
        try {
        	audioStream3 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip4 = AudioSystem.getClip();
            clip4.open(audioStream3);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/resources/sounds/damageTaken.wav");
        try {
        	audioStream4 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip5 = AudioSystem.getClip();
            clip5.open(audioStream4);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        gameSoundURL = getClass().getResource("/resources/sounds/hitSound.wav");
        try {
        	audioStream5 = AudioSystem.getAudioInputStream(gameSoundURL);
            clip6 = AudioSystem.getClip();
            clip6.open(audioStream5);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
	
	public void hitSound() {
		clip6.setMicrosecondPosition(0);
		clip6.start();
	}
	
	public void damageTaken() {
		clip5.setMicrosecondPosition(0);
		clip5.start();
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
		gameOver=true;
		stopGameSound();
		if(runOnce) {
			runOnce = false;
			clip2.start();
			decreaseVolume(clip2);
			decreaseVolume(clip2);
		}
	}
	
	public void startGameSound() {
		clip1.setMicrosecondPosition(0);
		clip1.start();
		decreaseVolume(clip1);
		decreaseVolume(clip1);
		gameOver=false;
		playSoundAgainWhenFinished(clip1);
		
	}
	
	public void stopGameSound() {
		clip1.stop();
		gameOver=true;
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
    }

    private static void playSoundAgainWhenFinished(Clip clip) {
        clip.addLineListener(event -> {
        	if(gameOver) {
        		clip.stop();
        		return;
        	}
            if (event.getType() == LineEvent.Type.STOP) {
                clip.setMicrosecondPosition(0);
                clip.start();
            }
        });

        clip.start();
    }
}
