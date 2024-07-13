package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;
import static utilz.Constants.Directions.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private boolean moving = false, attacking = false;
	private boolean left, right, jump;
	private int[][] lvlData;
	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;

	// Saltando / Gravedad
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

	// Status de la Barra UI (User Interface = Interfaz de Usuario)
	private BufferedImage statusBarImg;

	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);
	private int healthWidth = healthBarWidth;

	private int powerBarWidth = (int) (104 * Game.SCALE);
	private int powerBarHeight = (int) (2 * Game.SCALE);
	private int powerBarXStart = (int) (44 * Game.SCALE);
	private int powerBarYStart = (int) (34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private int powerMaxValue = 200;
	private int powerValue = powerMaxValue;

	private int flipX = 0;
	private int flipW = 1;

	private boolean attackChecked;
	private Playing playing;

	private int tileY = 0;

	private boolean powerAttackActive;
	private int powerAttackTick;
	private int powerGrowSpeed = 15;
	private int powerGrowTick;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		loadAnimations();
		initHitbox(20, 27);
		initAttackBox();
	}

	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (35 * Game.SCALE), (int) (20 * Game.SCALE));
		resetAttackBox();
	}

	public void update() {
		updateHealthBar();
		updatePowerBar();

		if (currentHealth <= 0) {
			if (state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);

				//Si el jugador muere en el aire
				if (!isEntityOnFloor(hitbox, lvlData)) {
					inAir = true;
					airSpeed = 0;
				}
			} else if (aniIndex == getSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			} else {
				updateAnimationTick();

				//Caida si esta en el aire
				if (inAir)
					if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
						hitbox.y += airSpeed;
						airSpeed += GRAVITY;
					} else
						inAir = false;

			}

			return;
		}

		updateAttackBox();

		if (state == HIT) {
			if (aniIndex <= getSpriteAmount(state) - 3)
				pushBack(pushBackDir, lvlData, 1.25f);
			updatePushBackDrawOffset();
		} else
			updatePos();

		if (moving) {
			checkPotionTouched();
			checkSpikesTouched();
			checkInsideWater();
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
			if (powerAttackActive) {
				powerAttackTick++;
				if (powerAttackTick >= 35) {
					powerAttackTick = 0;
					powerAttackActive = false;
				}
			}
		}

		if (attacking || powerAttackActive)
			checkAttack();

		updateAnimationTick();
		setAnimation();
	}

	private void checkInsideWater() {
		if (isEntityInWater(hitbox, playing.getLevelManager().getCurrentLevel().getLevelData()))
			currentHealth = 0;
	}

	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;

		if (powerAttackActive)
			attackChecked = false;

		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}

	private void setAttackBoxOnRightSide() {
		attackBox.x = hitbox.x + hitbox.width - (int) (Game.SCALE * 5);
	}

	private void setAttackBoxOnLeftSide() {
		attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
	}

	private void updateAttackBox() {
		if (right && left) {
			if (flipW == 1) {
				setAttackBoxOnRightSide();
			} else {
				setAttackBoxOnLeftSide();
			}

		} else if (right || (powerAttackActive && flipW == 1))
			setAttackBoxOnRightSide();
		else if (left || (powerAttackActive && flipW == -1))
			setAttackBoxOnLeftSide();

		attackBox.y = hitbox.y + (Game.SCALE * 10);
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}

	private void updatePowerBar() {
		powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);

		powerGrowTick++;
		if (powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX, (int) (hitbox.y - yDrawOffset + (int) (pushDrawOffset)), width * flipW, height, null);
//		drawHitbox(g, lvlOffset);
//		drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		//Fondo UI
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

		//Barra de Salud
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

		//Barra de poder
		g.setColor(Color.yellow);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= getSpriteAmount(state)) { //Terminacion de la animacion del ataque 1
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
				if (state == HIT) {
					newState(IDLE);
					airSpeed = 0f;
					if (!isFloor(hitbox, 0, lvlData))
						inAir = true;
				}
			}
		}
	}

	private void setAnimation() {
		int startAni = state;

		if (state == HIT)
			return;

		if (moving)
			state = RUNNING;
		else
			state = IDLE;

		if (inAir) {
			if (airSpeed < 0)
				state = JUMP;
			else
				state = FALLING;
		}

		if (powerAttackActive) {
			state = ATTACK;
			aniIndex = 1;
			aniTick = 0;
			return;
		}

		if (attacking) {
			state = ATTACK;
			if (startAni != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
		if (startAni != state)
			resetAniTick();
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;

		if (jump)
			jump();

		if (!inAir)
			if (!powerAttackActive)
				if ((!left && !right) || (right && left))
					return;

		float xSpeed = 0;

		//Atiende el caso de cuando dos teclas son presionadas a la vez, y cuando se deja de presionar una de estas
		if (left && !right) {
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right && !left) {
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		}

		if (powerAttackActive) {
			if ((!left && !right) || (left && right)) {
				if (flipW == -1)
					xSpeed = -walkSpeed;
				else
					xSpeed = walkSpeed;
			}

			xSpeed *= 3;
		}

		if (!inAir)
			if (!isEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir && !powerAttackActive) {
			if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;
	}

	private void jump() {
		if (inAir)
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		if (canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			hitbox.x += xSpeed;
		else {
			hitbox.x = getEntityXPosNextToWall(hitbox, xSpeed);
			if (powerAttackActive) {
				powerAttackActive = false;
				powerAttackTick = 0;
			}
		}
	}

	// Cambia la salud del jugador en una cantidad especificada
	public void changeHealth(int value) {
	    // Si el valor es negativo (daño), verifica si el estado es HIT
		if (value < 0) {
			if (state == HIT)
		        // Si el estado es HIT, no hace nada y retorna
				return;
			else
	            // Si el estado NO es HIT, cambia el estado a HIT
				newState(HIT);
		}

		currentHealth += value;
		currentHealth = Math.max(Math.min(currentHealth, maxHealth), 0); // Asegura que la salud actual esté dentro de los límites
	    // Math.min asegura que currentHealth no sea mayor que maxHealth
	    // Math.max asegura que currentHealth no sea menor que 0
	}

	// Metodo igual al anterior, pero considerando un enemigo
	public void changeHealth(int value, Enemy e) {
		if (state == HIT)
			return;
		changeHealth(value);
		pushBackOffsetDir = UP; // Configura la dirección del empuje hacia arriba
		pushDrawOffset = 0;

		 // Determina la dirección del empuje con base en la posición del enemigo
		if (e.getHitbox().x < hitbox.x)
			pushBackDir = RIGHT; //Derecha
		else
			pushBackDir = LEFT; //Izquierda
	}

	public void kill() {
		currentHealth = 0;
	}

	public void changePower(int value) {
		powerValue += value;
		powerValue = Math.max(Math.min(powerValue, powerMaxValue), 0);
	    // Math.min asegura que powerValue no sea mayor que powerMaxValue
	    // Math.max asegura que powerValue no sea menor que 0
	}

	private void loadAnimations() {
		BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[7][8];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);

		statusBarImg = LoadSave.getSpriteAtlas(LoadSave.STATUS_BAR);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}

	public void setAttacking(boolean attacking) { //Inicio de la animacion del ataque
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		airSpeed = 0f;
		state = IDLE;
		currentHealth = maxHealth;
		powerAttackActive = false;
		powerAttackTick = 0;
		powerValue = powerMaxValue;

		hitbox.x = x;
		hitbox.y = y;
		resetAttackBox();

		if (!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	private void resetAttackBox() {
		if (flipW == 1)
			setAttackBoxOnRightSide();
		else
			setAttackBoxOnLeftSide();
	}

	public int getTileY() {
		return tileY;
	}

	public void powerAttack() {
		if (powerAttackActive)
			return;
		if (powerValue >= 60) {
			powerAttackActive = true;
			changePower(-60);
		}

	}

}