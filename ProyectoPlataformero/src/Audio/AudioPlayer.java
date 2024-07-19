package Audio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

	//Música
	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;
	public static int LEVEL_3 = 3;

	//Efectos de sonido
	public static int DIE = 0;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK_ONE = 4;

	private Clip[] songs, effects;
	private int currentSongId;
	private float volume = 0.5f; //Es para regular el volumen con el que inicia el juego
	private boolean songMute, effectMute;

	public AudioPlayer() {
		loadSongs();
		loadEffects();
		playSong(MENU_1);
	}

	//Carga las canciones
	private void loadSongs() {  
		String[] names = { "menu", "level1", "level2", "level3" };
		songs = new Clip[names.length];
		for (int i = 0; i < songs.length; i++)
			songs[i] = getClip(names[i]);
	}

	//Carga los efectos de sonido
	private void loadEffects() {
		String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack" };
		effects = new Clip[effectNames.length];
		for (int i = 0; i < effects.length; i++)
			effects[i] = getClip(effectNames[i]);

		updateEffectsVolume();

	}

	//Extrae el audio dependiendo del nombre del archivo
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

	public void setVolume(float volume) { //Controlar el volumen 
		this.volume = volume;
		updateSongVolume();
		updateEffectsVolume();
	}

	public void stopSong() { // Detener una canción
		if (songs[currentSongId].isActive())
			songs[currentSongId].stop(); //Si hay una cancion reproduciendose se para
	}

	public void setLevelSong(int lvlIndex) { // Asignar música por nivel
	    if (lvlIndex % 3 == 0) {
	        playSong(LEVEL_3);  // Nueva canción para los niveles múltiplos de 3
	    } else if (lvlIndex % 2 == 0) {
	        playSong(LEVEL_1);  // Canción para los niveles múltiplos de 2 (pero no de 3)
	    } else {
	        playSong(LEVEL_2);  // Canción para todos los demás niveles
	    }
	}

	public void lvlCompleted() {
		stopSong();
		playEffect(LVL_COMPLETED);
	}

	public void playAttackSound() {
	    int attackSoundIndex = 4; // Índice del efecto de sonido específico para el ataque

	    // Verifica que el arreglo de efectos tenga suficientes elementos
	    if (effects.length > attackSoundIndex) 
	        playEffect(attackSoundIndex);
	}

	public void playEffect(int effect) {
	    if (effect >= 0 && effect < effects.length) {
	        if (effects[effect].getMicrosecondPosition() > 0) 
	            effects[effect].setMicrosecondPosition(0); // se resetea el efecto desde el inicio del mismo
	        effects[effect].start(); // se reproduce el efecto
	    }
	}

	public void playSong(int song) {
		stopSong();

		currentSongId = song; //se le cambia el id por uno nuevo
		updateSongVolume();
		songs[currentSongId].setMicrosecondPosition(0); //Se resetea la cancion
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY); // se reproduce la cancion y se mantiene en un bucle
	}

	public void toggleSongMute() {
		this.songMute = !songMute;
		for (Clip c : songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}
	}

	public void toggleEffectMute() {
		this.effectMute = !effectMute;
		for (Clip c : effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute);
		}
		if (!effectMute)
			playEffect(JUMP);
	}

	private void updateSongVolume() {

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
