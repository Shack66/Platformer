package Objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entities.Enemy;
import Entities.Player;
import Gamestates.Playing;
import Levels.Level;
import Main.Game;
import Utils.LoadSave;
import static Utils.Constants.ObjectConstants.*;

public class ObjectManager {

	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs, treeImgs;
	private BufferedImage[] grassImgs;
	private BufferedImage spikeImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;

	private Level currentLevel;
	
	public ObjectManager(Playing playing) {
		this.playing = playing;
		currentLevel = playing.getLevelManager().getCurrentLevel();
		loadImgs();
	}
	
	public void checkSpikesTouched(Player p) {
		for (Spike s : currentLevel.getSpikes())
			if (s.getHitbox().intersects(p.getHitbox()))
				p.kill();		
	}
	
	public void checkSpikesTouched(Enemy e) {
		for (Spike s : currentLevel.getSpikes())
			if (s.getHitbox().intersects(e.getHitbox()))
				e.hurt(200);		
	}
	
	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	}
	
	public void applyEffectToPlayer(Potion p) {
		if (p.getObjType() == RED_POTION)
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		else
			playing.getPlayer().changePower(BLUE_POTION_VALUE);
	}
	
	public void checkObjectHit(Rectangle2D.Float attackbox) {
		for (GameContainer gc : containers)
			if (gc.isActive() && !gc.doAnimation) {
				if (gc.getHitbox().intersects(attackbox)) {
					gc.setAnimation(true);
					int type = 0;
					if (gc.getObjType() == BARREL)
						type = 1;
					potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
					return;
				}
			}
	}
	
	public void loadObjects(Level newLevel) {
		currentLevel = newLevel;
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
	}

	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.getSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7];
		
		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

		BufferedImage containerSprite = LoadSave.getSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];
		
		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
		
		spikeImg = LoadSave.getSpriteAtlas(LoadSave.TRAP_ATLAS);
		
		treeImgs = new BufferedImage[2][4];
		BufferedImage treeOneImg = LoadSave.getSpriteAtlas(LoadSave.TREE_ONE_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[0][i] = treeOneImg.getSubimage(i * 39, 0, 39, 92);
		
		BufferedImage treeTwoImg = LoadSave.getSpriteAtlas(LoadSave.TREE_TWO_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[1][i] = treeTwoImg.getSubimage(i * 62, 0, 62, 54);
		
		BufferedImage grassTemp = LoadSave.getSpriteAtlas(LoadSave.GRASS_ATLAS);
		grassImgs = new BufferedImage[2];
		for (int i = 0; i < grassImgs.length; i++)
			grassImgs[i] = grassTemp.getSubimage(i * 32, 0, 32, 32);
	}
	
	public void update(int[][] lvlData, Player player) {
		updateBackgroundTrees();
		for (Potion p : potions)
			if (p.isActive())
				p.update();
		
		for (GameContainer gc : containers)
			if (gc.isActive())
				gc.update();
	}
	
	private void updateBackgroundTrees() {
		for (BackgroundTree bt : currentLevel.getTrees())
			bt.update();
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawGrass(g, xLvlOffset);
	}
		
	private void drawGrass(Graphics g, int xLvlOffset) {
		for (Grass grass : currentLevel.getGrass())
			g.drawImage(grassImgs[grass.getType()], grass.getX() - xLvlOffset, grass.getY(), (int) (32 * Game.SCALE), (int) (32 * Game.SCALE), null);
	}
	
	public void drawBackgroundTrees(Graphics g, int xLvlOffset) {
		for (BackgroundTree bt : currentLevel.getTrees()) {
			
			int type = bt.getType();
			if (type == 9)
				type = 8;
			g.drawImage(treeImgs[type - 7][bt.getAniIndex()], bt.getX() - xLvlOffset + getTreeOffsetX(bt.getType()), 
					(int) (bt.getY() + getTreeOffsetY(bt.getType())), getTreeWidth(bt.getType()), getTreeHeight(bt.getType()), null);
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for (Spike s : currentLevel.getSpikes())
			g.drawImage(spikeImg, (int) (s.getHitbox().x - xLvlOffset), (int) (s.getHitbox().y - s.getyDrawOffset()), 
					SPIKE_WIDTH, SPIKE_HEIGHT, null);
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for (GameContainer gc : containers)
			if (gc.isActive()) {
				int type = 0;
				if (gc.getObjType() == BARREL) 
					type = 1;
					g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), 
							(int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
			}		
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for (Potion p : potions)
			if (p.isActive()) {
				int type = 0;
				if (p.getObjType() == RED_POTION)
						type = 1;
				g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), 
						(int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT, null);
			}
	}
	
	public void resetAllObjects() {
		loadObjects(playing.getLevelManager().getCurrentLevel());
		for (Potion p : potions)
			p.reset();
		for (GameContainer gc : containers)
			gc.reset();
	}
}
