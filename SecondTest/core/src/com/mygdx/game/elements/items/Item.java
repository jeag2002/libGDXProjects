package com.mygdx.game.elements.items;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.SecondTestGDX;
import com.mygdx.game.elements.DynElementPart;
import com.mygdx.game.elements.ElementDefinitionObject;
import com.mygdx.game.elements.players.DynamicCollPlayerObject;
import com.mygdx.game.enums.DynamicElementPartType;
import com.mygdx.game.enums.ElementDataEnum;
import com.mygdx.game.enums.ElementEnum;
import com.mygdx.game.enums.PlayerMovementsEnum;
import com.mygdx.game.enums.SpawnType;
import com.mygdx.game.logic.GameLogicElementInformation;
import com.mygdx.game.logic.elements.SpawnObject;
import com.mygdx.game.logic.elements.SpawnPool;
import com.mygdx.game.screens.GamePlayScreen;
import com.mygdx.game.utils.NewItem;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Item extends DynamicCollPlayerObject implements SpawnObject{

	private static final int INDEX_SUBITEM = 0;
	private static final float TRANSITION_BETWEEN_ANIM = 0.05f;
	private static final float TRANSITION_PULSE_BONUS = 0.05f;
	
	private static final int TRANSITION_SPAWN = 15;
	private static final int TRANSITION_SPAWN_LIGHT = 2;
	
	private static final int POSITION_1 = 0;
	private static final int POSITION_2 = 1;
	private static final int POSITION_3 = 2;
	private static final int POSITION_4 = 4;
	
		
	private GamePlayScreen gPS;
	
	private SpawnType type;
	private SpawnType subType;
	
    private SpawnPool spawnPool;
	
	private boolean isSpawned;
	
	private ArrayList<DynElementPart> item_parts;
	
	private PointLight light;
	private float timer;
	private float timer_spawn;
	private float timer_spawn_flash;
	private int index;
	
	private Random latchSpawn;
	private boolean isSpawnable;
	private boolean latchBackground;
	
	private boolean latchSpawnEnemy;
	
	private float posXBonus = 0;
	private float posYBonus = 0;
	private int positionBonus = 0;
	
	private ElementDefinitionObject eDO;
	
	private int index_X;
    private int index_Y;
	
    private Texture textureShield;
    private Sprite spriteShield;
    
	
	public Item(SpawnPool spawnPool, SpawnType type, World world, GamePlayScreen gPS) {
		super(world,type);
		
		this.gPS = gPS;
		this.type = type;
		this.timer = 0;
		this.timer_spawn = 0;
		this.timer_spawn_flash = 0;
		
		this.index = 0;
		this.spawnPool = spawnPool;
		this.isSpawned = false;
		this.latchSpawn = new Random();
		this.isSpawnable = false;
		this.latchBackground = false;
		this.item_parts = new ArrayList<DynElementPart>();
		
		this.latchSpawnEnemy = false;
		textureShield = SecondTestGDX.resources.get(SecondTestGDX.resources.dot_b_light,Texture.class);
	    spriteShield = new Sprite(textureShield);
		
	}	
	
	public SpawnType getSubType() {
		return this.subType;
	}
	
	public void init (RayHandler rayHandler, SpawnType subtype, float iniPositionX, float iniPositionY, float width,  float height) {
		
		this.subType = subtype;
		
		
		setAnimation();
		setAnimationPart();
		
		
		this.eDO = null;
		
		if (subType.equals(SpawnType.Item_Mine)) {
			ElementDataEnum eDU = ElementDataEnum.getBySpawnType(subtype);
			eDO = new ElementDefinitionObject.Builder().setScore(eDU.getScore()).build();
		}else if (subType.equals(SpawnType.Item_PlatformEnemy)) {
			int spawn = this.latchSpawn.nextInt(2);
			if (spawn == 1) {this.isSpawnable = true;}
			ElementDataEnum eDU = ElementDataEnum.getBySpawnType(subtype);
			eDO = new ElementDefinitionObject.Builder().setScore(eDU.getScore()).build();
		}
		

		if ( 
			subType.equals(SpawnType.Item_Bonus_Shield) || 
			subType.equals(SpawnType.Item_Bonus_Bullet) || 
			subType.equals(SpawnType.Item_Bonus_Gun) ||
			subType.equals(SpawnType.Item_Bonus_Life) ||
			subType.equals(SpawnType.Item_Bonus_Nuke) ||
			subType.equals(SpawnType.Item_Bonus_Score)){
			setSize(width, height);
			setPosition(iniPositionX, iniPositionY);
		}else{
			setPosition(iniPositionX, iniPositionY);
			setSize(width, height);
		}
		setPosition(iniPositionX, iniPositionY);
	
        this.index_X = Math.round(iniPositionX)/SecondTestGDX.tileWidth_TL;
        this.index_Y = Math.round(iniPositionY)/SecondTestGDX.tileHeight_TL;
		
		
		setPositionPart(iniPositionX, iniPositionY);
		
		
		setSpeed(0,0);
		
		this.setSpawnSubTypeColl(subType);
		
		if (subType.equals(SpawnType.Item_PlatformPlayer) || 
			subType.equals(SpawnType.Item_PlatformEnemy) || 
			subType.equals(SpawnType.Item_Bonus_Shield) || 
			subType.equals(SpawnType.Item_Bonus_Bullet) || 
			subType.equals(SpawnType.Item_Bonus_Gun) || 
			subType.equals(SpawnType.Item_Bonus_Life) || 
			subType.equals(SpawnType.Item_Bonus_Nuke) ||
			subType.equals(SpawnType.Item_Bonus_Score)){ 
			createCollisionObject(getX(),getY(),getWidth(),getHeight(),BodyType.DynamicBody, true);
		}else {
			createCollisionObject(getX(),getY(),getWidth(),getHeight(),BodyType.DynamicBody, false);
		}
		
		
		if (subType.equals(SpawnType.Item_PlatformPlayer) || subType.equals(SpawnType.Item_PlatformEnemy)  || subType.equals(SpawnType.Item_PlatformEndLevel)) {
			this.light = new PointLight(rayHandler, 20, Color.WHITE, 1, 0, 0);
			this.light.setSoftnessLength(0f);
			this.light.attachToBody(this.getBody());
		}else if (subType.equals(SpawnType.Item_Bonus_Shield) || 
				subType.equals(SpawnType.Item_Bonus_Bullet) || 
				subType.equals(SpawnType.Item_Bonus_Gun) ||
				subType.equals(SpawnType.Item_Bonus_Life) ||
				subType.equals(SpawnType.Item_Bonus_Nuke) ||
				subType.equals(SpawnType.Item_Bonus_Score)
				) {
				
			posXBonus = getX();
			posYBonus = getY();
			positionBonus = this.POSITION_1;
			
		}
	}
	
	
	public void setPositionPart(float X, float Y) {
		
		if ( 
			subType.equals(SpawnType.Item_Bonus_Shield) || 
			subType.equals(SpawnType.Item_Bonus_Bullet) || 
			subType.equals(SpawnType.Item_Bonus_Gun) ||
			subType.equals(SpawnType.Item_Bonus_Life) ||
			subType.equals(SpawnType.Item_Bonus_Nuke) ||
			subType.equals(SpawnType.Item_Bonus_Score)) {
			
			item_parts.get(INDEX_SUBITEM).getSprite().setOriginCenter();
			item_parts.get(INDEX_SUBITEM).getSprite().setOriginBasedPosition(getX() + getWidth()/2, getY() + getHeight() / 2);
		
		}else if(subType.equals(SpawnType.Item_PlatformPlayer) || 
			subType.equals(SpawnType.Item_PlatformEnemy) || 
			subType.equals(SpawnType.Item_PlatformEndLevel) 
			) {
			
			item_parts.get(INDEX_SUBITEM).getSprite().setOriginCenter();
			item_parts.get(INDEX_SUBITEM).getSprite().setOriginBasedPosition(getX() + getWidth()/2, getY() + getHeight() / 2);
		}
		
	}
	
	
	public void setAnimationPart() {
		
		if (subType.equals(SpawnType.Item_PlatformPlayer) || subType.equals(SpawnType.Item_PlatformEnemy) || subType.equals(SpawnType.Item_PlatformEndLevel)) {
			
			DynElementPart badge = new DynElementPart(DynamicElementPartType.BADGE);
			
			if (subType.equals(SpawnType.Item_PlatformPlayer)) {
				badge.init(GameLogicElementInformation.dot_red, 0);
			}else if (subType.equals(SpawnType.Item_PlatformEnemy)) {
				badge.init(GameLogicElementInformation.dot_blue, 0);
			}else if (subType.equals(SpawnType.Item_PlatformEndLevel)) {
				badge.init(GameLogicElementInformation.dot_end, 0);
			}
			
			badge.setSize(ElementEnum.PLATFORM_DOT_R.getWidthShow(), ElementEnum.PLATFORM_DOT_R.getHeightShow());
	    	item_parts.add(badge);
			
		}else if (subType.equals(SpawnType.Item_Bonus_Life) || 
				subType.equals(SpawnType.Item_Bonus_Shield) || 
				subType.equals(SpawnType.Item_Bonus_Bullet) || 
				subType.equals(SpawnType.Item_Bonus_Gun) ||
				subType.equals(SpawnType.Item_Bonus_Nuke) ||
				subType.equals(SpawnType.Item_Bonus_Score)) {
			
			DynElementPart badge = new DynElementPart(DynamicElementPartType.BONUS);
			
			
			Texture[] border = {SecondTestGDX.resources.get(SecondTestGDX.resources.border_bonus,Texture.class)};
			badge.init(border, 0);
			badge.setSize(64, 64);
			
	    	item_parts.add(badge);
			
			
			
		}
		
	}
	
	
	public void setAnimation() {
		
		if (subType.equals(SpawnType.Item_PlatformPlayer) || subType.equals(SpawnType.Item_PlatformEnemy) || subType.equals(SpawnType.Item_PlatformEndLevel)) {
			
			Texture[] platformTXT = GameLogicElementInformation.platform;
    		init(platformTXT,0);
			
		}else if (subType.equals(SpawnType.Item_Mine)) {
			
			Texture[] mine = GameLogicElementInformation.Enemy_03;
			init(mine, 0);
			
		}else if (subType.equals(SpawnType.Item_Bonus_Life) || 
				subType.equals(SpawnType.Item_Bonus_Shield) || 
				subType.equals(SpawnType.Item_Bonus_Bullet) || 
				subType.equals(SpawnType.Item_Bonus_Gun) ||
				subType.equals(SpawnType.Item_Bonus_Nuke) ||
				subType.equals(SpawnType.Item_Bonus_Score)) {
			
		
			if (subType.equals(SpawnType.Item_Bonus_Life)) {
				Texture[] bonus = GameLogicElementInformation.bonus_life;
				init(bonus, 0);
			}else if (subType.equals(SpawnType.Item_Bonus_Shield)) {
				Texture[] bonus = GameLogicElementInformation.bonus_shield;
				init(bonus, 0);
			}else if (subType.equals(SpawnType.Item_Bonus_Gun)) {
				Texture[] bonus = GameLogicElementInformation.bonus_gun;
				init(bonus, 0);
			}else if (subType.equals(SpawnType.Item_Bonus_Bullet)) {
				Texture[] bonus = GameLogicElementInformation.bonus_ammo;
				init(bonus, 0);
			}else if (subType.equals(SpawnType.Item_Bonus_Score)) {
				Texture[] bonus = GameLogicElementInformation.bonus_score;
				init(bonus, 0);
			}else if (subType.equals(SpawnType.Item_Bonus_Nuke)) {
				Texture[] bonus = GameLogicElementInformation.bonus_nuke;
				init(bonus, 0);
			}
						
		}
	}
	
	
	
	
	@Override
	public void AnimationByMovement(PlayerMovementsEnum movement, float moveStepX, float moveStepY, boolean isAccX,
			boolean isAccY) {
	}

	@Override
	public void AnimationByTime(float delta) {
	}

	@Override
	public void AnimationLoop(float delta, boolean loop) {
		
		
		
		if (subType.equals(SpawnType.Item_Mine)) {
			this.timer += delta;
			if (timer > TRANSITION_BETWEEN_ANIM) {
				timer = 0;
				index++;
				if (index >= GameLogicElementInformation.Enemy_03.length) {index = 0;}
				setTextureToSpriteByIndex(index); 	
			}	
		}else if (subType.equals(SpawnType.Item_Bonus_Life) || 
				subType.equals(SpawnType.Item_Bonus_Shield) || 
				subType.equals(SpawnType.Item_Bonus_Bullet) || 
				subType.equals(SpawnType.Item_Bonus_Gun) ||
				subType.equals(SpawnType.Item_Bonus_Nuke) ||
				subType.equals(SpawnType.Item_Bonus_Score) ) {
			
			this.timer += delta;
			if (timer > TRANSITION_PULSE_BONUS) {
				
				timer = 0;
				
				if (this.positionBonus == this.POSITION_1) {
					this.setPosition(this.posXBonus-2, this.posYBonus-2);
					this.positionBonus = this.POSITION_2;
				}else if (this.positionBonus == this.POSITION_2) {
					this.setPosition(this.posXBonus-2, this.posYBonus+2);
					this.positionBonus = this.POSITION_3;
				}else if (this.positionBonus == this.POSITION_3) {
					this.setPosition(this.posXBonus+2, this.posYBonus+2);
					this.positionBonus = this.POSITION_4;
				}else {
					this.setPosition(this.posXBonus+2, this.posYBonus-2);
					this.positionBonus = this.POSITION_1;
				}
				
				
				
			
			}
		}
	}


	@Override
	public void setSpawned(boolean spawned) {
		this.isSpawned = spawned;
	}

	@Override
	public boolean isSpawned() {
		return this.isSpawned;
	}


	@Override
	public void setPool(SpawnPool pool) {
		this.spawnPool = pool;
	}


	@Override
	public void kill(SpawnPool pool) {
		dispose();
		super.setPosition(SecondTestGDX.screenWidth, 0);
		
	}
	
	
	private NewItem generationTANK() {
		
		SpawnType tankType = SpawnType.Item;
		
		int typeTankIndex = latchSpawn.nextInt(3);
		
		if (typeTankIndex == 0) {tankType = SpawnType.Tank_Level_1;}
		else if (typeTankIndex == 1) {tankType = SpawnType.Tank_Level_2;}
		else if (typeTankIndex == 2) {tankType = SpawnType.Tank_Level_3;}
		
		NewItem sE = new NewItem(SpawnType.Enemy_02, tankType, getX(), getY(), SecondTestGDX.tilePlayerWidth_TL, SecondTestGDX.tilePlayerHeight_TL, 0,0);
		return sE;
	}
	
	
	
	public void generateSpawn(float delta) {
		if (subType.equals(SpawnType.Item_PlatformEnemy)) {
			this.timer_spawn += delta;
			if (this.timer_spawn >= TRANSITION_SPAWN) {
				this.timer_spawn = 0;
				if (this.isSpawnable) {
					NewItem sE = generationTANK();
					gPS.getGamePlay().getGameLogic().getSpawnPool().getCreatedBodiesWithCollision().add(sE);
				}
			}
			
			this.timer_spawn_flash += delta;
			if (this.timer_spawn_flash >= TRANSITION_SPAWN_LIGHT) {
				this.timer_spawn_flash = 0;
				latchSpawnEnemy = !latchSpawnEnemy;
			}
			
			
		}
	}
	
	@Override
	public void update(float delta, float boostFactor) {
		generateSpawn(delta);
		setCollisionRef(getX(),getY());
		AnimationLoop(delta, true);
		
		if (subType.equals(SpawnType.Item_Bonus_Life) || 
		subType.equals(SpawnType.Item_Bonus_Shield) ||
		subType.equals(SpawnType.Item_Bonus_Gun) ||  
		subType.equals(SpawnType.Item_Bonus_Bullet) ||
		subType.equals(SpawnType.Item_Bonus_Nuke) ||
		subType.equals(SpawnType.Item_Bonus_Score)) {
			item_parts.get(INDEX_SUBITEM).AnimationLoop(delta, true);
		}
		
	}
	
	
	public void drawLight(SpriteBatch sb) {
		if (subType.equals(SpawnType.Item_PlatformEnemy)) {
			  this.spriteShield.setOriginBasedPosition(getX()+getWidth()/2, getY()+getHeight()/2);
			  if (latchSpawnEnemy) {
				  this.spriteShield.draw(sb);
			  }
		}
	}
	

	@Override
	public void draw(SpriteBatch sb) {
    	super.draw(sb);
    	drawLight(sb);
    	if (subType.equals(SpawnType.Item_PlatformPlayer) || 
    		subType.equals(SpawnType.Item_PlatformEnemy) || 
    		subType.equals(SpawnType.Item_PlatformEndLevel) ||
    		subType.equals(SpawnType.Item_Bonus_Life) || 
    		subType.equals(SpawnType.Item_Bonus_Shield) ||
    		subType.equals(SpawnType.Item_Bonus_Gun) ||  
    		subType.equals(SpawnType.Item_Bonus_Bullet) ||
    		subType.equals(SpawnType.Item_Bonus_Nuke) ||
    		subType.equals(SpawnType.Item_Bonus_Score)) {
    		item_parts.get(INDEX_SUBITEM).draw(sb);
    	}
	}


	@Override
	public Body getBox2DBody() {
		return super.getBody();
	}	
	
	
	public void dispose() {
		if (this.light != null) {this.light.remove();}
	}

	@Override
	public SpawnType getType() {
		return this.type;
	}
	
	public ElementDefinitionObject getStatsDynElement() {
		return this.eDO;
	}
	
	public int getIndex_X() {
		return index_X;
	}

	public void setIndex_X(int index_X) {
		this.index_X = index_X;
	}

	public int getIndex_Y() {
		return index_Y;
	}

	public void setIndex_Y(int index_Y) {
		this.index_Y = index_Y;
	}
	
	
}
