package Audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;
	public static int LEVEL_3 = 3;
	
	public static int DIE = 0;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK = 4;

	private Clip[] songs, effects;
	private int currentSongId;
	private float volume = 1f;
	private boolean songMute, effectMute;
	private Random rand = new Random();
	
	public AudioPlayer() {
		loadSongs();
		loadEffects();
		playSong(MENU_1);
	}
	
	private void loadSongs() {
		String[] names = {"menu", "level1", "level2", "level3"};
		songs = new Clip[names.length];
		for (int i = 0; i < songs.length; i++)
			songs[i] = getClip(names[i]);
	}
	
	private void loadEffects() {
		String[] effectNames = {"die", "jump", "gameover", "lvlcompleted", "attack"};
		effects = new Clip[effectNames.length];
		for (int i = 0; i < effects.length; i++)
			effects[i] = getClip(effectNames[i]);
		
		updateEffectsVolume();
		
	}
	
	private Clip getClip(String name) {
		URL url = getClass().getResource("/audio/" + name + ".wav");
		AudioInputStream audio;
		
		try {
			audio = AudioSystem.getAudioInputStream(url);
			Clip c = AudioSystem.getClip();
			c.open(audio);
			return c;
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

			e.printStackTrace();
		}
		
		return null;

	}
	
	public void setVolume(float volume) {
		this.volume = volume;
		updateSongVolume();
		updateEffectsVolume();
	}
	
	public void stopSong() {
		if (songs[currentSongId].isActive()) 
			songs[currentSongId].stop(); //Se para si hay una cancion reproduciendose
 
	}
	
	public void setLevelSong(int lvlIndex) {
	    if (lvlIndex % 3 == 0) {
	        playSong(LEVEL_3);  // Nueva canción para los niveles múltiplos de 3
	    } else if (lvlIndex % 2 == 0) {
	        playSong(LEVEL_2);  // Canción para los niveles múltiplos de 2 
	    } else {
	        playSong(LEVEL_1);  // Canción para todos los demás niveles
	    }
	}
	
	public void lvlCompleted() {
		stopSong();
		playEffect(LVL_COMPLETED);
	}
	
	public void playAttackSound() {
		int start = 4;
		start += rand.nextInt(1);
		playEffect(start);
	}
	
	public void playEffect(int effect) {
		if (effects[effect].getMicrosecondPosition() > 0) //si el efecto (accion) ya ha empezado
			effects[effect].setMicrosecondPosition(0); // se resetea el efecto desde el inicio del mismo
		effects[effect].start(); // se reproduce el efecto
	}
	
	public void playSong (int song) {
		stopSong(); 
		
		currentSongId = song; //se le cambia el id por uno nuevo
		updateSongVolume();
		songs[currentSongId].setMicrosecondPosition(0); //se resetea la cancion
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY); //se reproduce la cancion y se mantiene en un bucle
	}
	
	public void toggleSongMute() {
		this.songMute = !songMute;
		for(Clip c : songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}
	}
	
	public void toggleEffectMute() {
		this.effectMute = !effectMute;
		for(Clip c : effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute);
		}
		if (!effectMute)
			playEffect(JUMP);
	}

	private void updateSongVolume() {
		//Controlador del volumen
		FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volume) + gainControl.getMinimum();
		gainControl.setValue(gain);
	}
	
	private void updateEffectsVolume() {
		for (Clip c : effects) {
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volume) + gainControl.getMinimum();
			gainControl.setValue(gain);
		}
	}
	
}
