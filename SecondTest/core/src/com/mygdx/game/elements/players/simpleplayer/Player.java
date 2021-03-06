package com.mygdx.game.elements.players.simpleplayer;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.SecondTestGDX;
import com.mygdx.game.elements.DynElementPart;
import com.mygdx.game.elements.ElementDefinitionObject;
import com.mygdx.game.elements.players.ShootPlayerObject;
import com.mygdx.game.enums.ElementEnum;
import com.mygdx.game.enums.PlayerMovementsEnum;
import com.mygdx.game.enums.DynamicElementPartType;
import com.mygdx.game.enums.ElementDataEnum;
import com.mygdx.game.enums.SpawnType;
import com.mygdx.game.logic.GameLogicElementInformation;
import com.mygdx.game.logic.GameLogicInformation;
import com.mygdx.game.logic.elements.SpawnPool;
import com.mygdx.game.logic.map.SimpleMapGeneration;
import com.mygdx.game.screens.GamePlayScreen;
import com.mygdx.game.utils.DrawUtils;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;


public class Player extends ShootPlayerObject{
	
	
	private static final float INTERVAL_BETWEEN_SHOOT = 0.1f;
	
	private static final int INDEX_TRACK_LEFT = 0;
	private static final int INDEX_TRACK_RIGHT = 1;
	private static final int INDEX_GUN = 2;
	
	private static final int INDEX_EXHAUST_LEFT = 3;
	private static final int INDEX_EXHAUST_RIGHT = 4;
	
	private PlayerMovementsEnum orientationUP;
	private PlayerMovementsEnum orientationDOWN;
	private PlayerMovementsEnum orientationLEFT;
	private PlayerMovementsEnum orientationRIGHT;
	private PlayerMovementsEnum orientationA;
	private PlayerMovementsEnum orientationS;
	private PlayerMovementsEnum orientationSHOOT;
	private PlayerMovementsEnum orientationMOUSEMOVE;
	
	
    private GamePlayScreen gPS;
    private DrawUtils dU;
    private ArrayList<DynElementPart> player_parts;
    private SpawnType type;
    private ElementEnum cannonType;
    
    

	private Sound sfxShot;
    private float sfxShotVolume; 
    
    private Sound sfxFlame;
    private float sfxFlameVolume; 
    
    private Sound sfxMissile;
    private float sfxMissileVolume;
    
    private Sound sfxGrenade;
    private float sfxGrenadeVolume;
    
	private PointLight myLight_point;
	private ConeLight myLight_cone;
    
    private float speed;
    
    private float angle;
    private float angleTurret;
    
    private float time;
    
    private Vector2 movement = new Vector2();
    private Vector2 position = new Vector2();
    private Vector2 direction = new Vector2();
    
    private boolean collDetection;
    
    private ElementDefinitionObject eDO;
    
    private Texture textureShield;
    private Sprite spriteShield;
   
    
    public Player(SpawnPool spawnPool, SpawnType type, World world, GamePlayScreen gPS) {
    	super(spawnPool, type, world);
    	
    	ElementDataEnum eDU = ElementDataEnum.getBySpawnType(type);
    	this.eDO = new ElementDefinitionObject.Builder().setRandomLife(eDU.getLife()).setRandomShield(eDU.getShield()).setRandomAmmo(eDU.getAmmo()).setScore(0).build();
    	
    	this.gPS = gPS;
    	
    	this.type = type;
    	this.cannonType = eDO.getCannon();
    	
    	this.orientationUP= PlayerMovementsEnum.IDLE;
    	this.orientationDOWN= PlayerMovementsEnum.IDLE;
    	this.orientationLEFT= PlayerMovementsEnum.IDLE;
    	this.orientationRIGHT= PlayerMovementsEnum.IDLE;
    	this.orientationA= PlayerMovementsEnum.IDLE;
    	this.orientationS= PlayerMovementsEnum.IDLE;
    	this.orientationSHOOT= PlayerMovementsEnum.IDLE;
    	this.orientationMOUSEMOVE = PlayerMovementsEnum.IDLE;
    	
    	
    	this.dU = new DrawUtils();
    	this.player_parts = new ArrayList<DynElementPart>();
    	this.angle = 0.0f;
    	this.angleTurret = 0.0f;
    	
    	this.time = 0.0f;
    	
    	this.collDetection = false;
    	this.sfxShotVolume = 0.97f;
    	this.sfxFlameVolume = 0.25f;
    	this.sfxMissileVolume = 0.25f;
    	this.sfxGrenadeVolume = 0.25f;
    
       	
	    setShotSound("sounds/laser4.mp3", sfxShotVolume);
	    setFlameSound("sounds/flamethrow.mp3", sfxFlameVolume); 
	    setMissileSound("sounds/Missile.mp3", sfxMissileVolume);
	    setGrenadeSound("sounds/grenade.mp3", sfxGrenadeVolume); 
	    
	    super.resetGuns();
		
	    textureShield = SecondTestGDX.resources.get(SecondTestGDX.resources.border_bonus,Texture.class);
    	spriteShield = new Sprite(textureShield);
	    
    	initShootingEngine(SpawnType.MissilePlayer);
    	setShootingActive(true);
    	
    }
    
    
    public Player(SpawnPool spawnPool, SpawnType type, ElementEnum cannon, World world, GamePlayScreen gPS) {
    	super(spawnPool, type, world);
    	
    	ElementDataEnum eDU = ElementDataEnum.getBySpawnType(type);
    	this.eDO = new ElementDefinitionObject.Builder().setRandomLife(eDU.getLife()).setRandomLife(eDU.getShield()).setRandomAmmo(eDU.getAmmo()).setScore(0).build();
    	
    	this.gPS = gPS;
    	
    	this.type = type;
    	this.cannonType = cannon;
    	
    	this.orientationUP= PlayerMovementsEnum.IDLE;
    	this.orientationDOWN= PlayerMovementsEnum.IDLE;
    	this.orientationLEFT= PlayerMovementsEnum.IDLE;
    	this.orientationRIGHT= PlayerMovementsEnum.IDLE;
    	this.orientationA= PlayerMovementsEnum.IDLE;
    	this.orientationS= PlayerMovementsEnum.IDLE;
    	this.orientationSHOOT= PlayerMovementsEnum.IDLE;
    	this.orientationMOUSEMOVE = PlayerMovementsEnum.IDLE;
    	
    	
    	this.dU = new DrawUtils();
    	this.player_parts = new ArrayList<DynElementPart>();
    	this.angle = 0.0f;
    	this.angleTurret = 0.0f;
    	
    	this.time = 0.0f;
    	
    	this.collDetection = false;
    	this.sfxShotVolume = 0.97f;
    	this.sfxFlameVolume = 0.25f;
    	this.sfxMissileVolume = 0.25f;
    	this.sfxGrenadeVolume = 0.25f;
    
       	
	    setShotSound("sounds/laser4.mp3", sfxShotVolume);
	    setFlameSound("sounds/flamethrow.mp3", sfxFlameVolume); 
	    setMissileSound("sounds/Missile.mp3", sfxMissileVolume);
	    setGrenadeSound("sounds/grenade.mp3", sfxGrenadeVolume); 
	    
	    super.resetGuns();
		
	    textureShield = SecondTestGDX.resources.get(SecondTestGDX.resources.border_bonus,Texture.class);
    	spriteShield = new Sprite(textureShield);
	    
    	initShootingEngine(SpawnType.MissilePlayer);
    	setShootingActive(true);
    	
    }
    
	
    
    public boolean isCollDetection() {
		return collDetection;
	}

	public void setCollDetection(boolean collDetection) {
		this.collDetection = collDetection;
	}
	

	public void setLocationAndSize(RayHandler rayHandler, float iniPositionX, float iniPositionY, float width, float height) {
		
		setAnimation();
		setAnimationParts(iniPositionX,iniPositionY,width,height);
		
		setSize(width, height);
		setPosition(iniPositionX, iniPositionY);
		
		position.set(iniPositionX, iniPositionY);	
		direction.set((float)Math.cos(Math.toRadians(angle+270)), (float)Math.sin(Math.toRadians(angle+270))).nor();
		
		setSpeed(0, 0);
		createCollisionObject(getX(),getY(),getWidth(),getHeight(),BodyType.DynamicBody);
		
		this.setShootingRayHandler(rayHandler);
		
		this.spriteShield.setSize(128, 128);
		this.spriteShield.setOriginCenter();
		this.spriteShield.setOriginBasedPosition(getX()+getWidth()/2, getY()+getHeight()/2);

		
		
		//LIGHT PLAYER
		//////////////////////////////////////////////////////////////////////////////////////////////
		this.myLight_point = new PointLight(rayHandler, 20, Color.WHITE, 1, 0, 0);
		this.myLight_cone = new ConeLight(rayHandler, 20, Color.WHITE, 25, 0, 0, 0, 9);
		
		this.myLight_point.setSoftnessLength(1f);
		this.myLight_cone.setSoftnessLength(1f);
		
		this.myLight_point.attachToBody(getBody());
		this.myLight_cone.attachToBody(getBody(), 0, 0, 90.0f);
		//////////////////////////////////////////////////////////////////////////////////////////////
	}
    

    public void setAnimation() {
    	
    	if (type.equals(SpawnType.Player_01)) {
    		Texture[] hullTXT = GameLogicElementInformation.hullPlayer01Text;
    		init(hullTXT,0);
    	}
    }
    
    public void setAnimationParts(float iniPositionX, float iniPositionY, float width, float height) {
    	
    	
    	
    	DynElementPart track_left = new DynElementPart(DynamicElementPartType.TRACK_LEFT_PLAYER);
    	track_left.init(GameLogicElementInformation.trackPlayerText, 0);
    	track_left.setSize(ElementEnum.TRACK_01.getWidthShow(), ElementEnum.TRACK_01.getHeightShow());
    	track_left.setPosition(iniPositionX+8, iniPositionY);
    	player_parts.add(track_left);
    	
    	DynElementPart track_right = new DynElementPart(DynamicElementPartType.TRACK_RIGHT_PLAYER);
    	track_right.init(GameLogicElementInformation.trackPlayerText, 0);
    	track_right.setSize(ElementEnum.TRACK_01.getWidthShow(), ElementEnum.TRACK_01.getHeightShow());
    	track_right.setPosition(iniPositionX+48-8, iniPositionY);
    	player_parts.add(track_right);
    	
    	
    	DynElementPart gun = new DynElementPart(DynamicElementPartType.GUN_PLAYER);
    	Texture gunTXT[] = null;
    	if (cannonType.equals(ElementEnum.GUN_PLAYER_1_A)) {				//-->single shoot
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01AText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_A.getWidthShow(), ElementEnum.GUN_PLAYER_1_A.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2), iniPositionY+8);
        	
    	}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_B)) {		 	//-->double shoot
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01BText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_B.getWidthShow(), ElementEnum.GUN_PLAYER_1_B.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_B.getWidthShow()/2), iniPositionY+8);
        	
    	}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_C)) {		   //--> missile
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01CText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_C.getWidthShow(), ElementEnum.GUN_PLAYER_1_C.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_C.getWidthShow()/2), iniPositionY+8);
        	
    	}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_D)) {			//--> flame	
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01DText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_D.getWidthShow(), ElementEnum.GUN_PLAYER_1_D.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_D.getWidthShow()/2), iniPositionY+8);
        	
    	}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_E)) {			//--> pulse
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01EText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_E.getWidthShow(), ElementEnum.GUN_PLAYER_1_E.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_E.getWidthShow()/2), iniPositionY+8);
    	
    	}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_F)) {			//--> grenade
    		
    		gunTXT = GameLogicElementInformation.cannonPlayer01FText;
    		gun.init(gunTXT, 0);
    		gun.setSize(ElementEnum.GUN_PLAYER_1_F.getWidthShow(), ElementEnum.GUN_PLAYER_1_F.getHeightShow());
        	gun.setPosition(iniPositionX+(width/2)-(ElementEnum.GUN_PLAYER_1_F.getWidthShow()/2), iniPositionY+8);
    		
    	}
    	
    	player_parts.add(gun);
    	
    	
    	DynElementPart exhaust_left = new DynElementPart(DynamicElementPartType.EXHAUST_LEFT_PLAYER);
    	exhaust_left.init(GameLogicElementInformation.ExhaustPlayerText, 6);
    	exhaust_left.setSize(ElementEnum.EXHAUST_01.getWidthShow(), ElementEnum.EXHAUST_01.getHeightShow());
    	exhaust_left.setPosition(iniPositionX+width/2-16, iniPositionY-ElementEnum.EXHAUST_01.getHeightShow()+8);
    	player_parts.add(exhaust_left);
    	
    	DynElementPart exhaust_right = new DynElementPart(DynamicElementPartType.EXHAUST_RIGHT_PLAYER);
    	exhaust_right.init(GameLogicElementInformation.ExhaustPlayerText, 6);
    	exhaust_right.setSize(ElementEnum.EXHAUST_01.getWidthShow(), ElementEnum.EXHAUST_01.getHeightShow());
    	exhaust_right.setPosition(iniPositionX+width/2, iniPositionY-ElementEnum.EXHAUST_01.getHeightShow()+8);
    	player_parts.add(exhaust_right);
    }
    
    
    public void update(float delta) {
    	movement(delta);
    	super.update(delta);
    }
    
    
    public void movement(float delta) {
    	
    		if (!gPS.getGamePlay().isPlayerDied()) {
    	
			    if (orientationUP.equals(PlayerMovementsEnum.UP)) {
			    		if (Gdx.input.isKeyPressed(Keys.UP) || (Gdx.input.isKeyPressed(Keys.W))) {
			    			movement(delta, -1);
			    			gPS.getGamePlay().update(getX(),getY());
			    			animatedTracks(delta,true,true); 
			    		    animatedExhaust(delta,true,true);
			    		}
			    }
			    	
			    if (orientationDOWN.equals(PlayerMovementsEnum.DOWN)) {
			    		if (Gdx.input.isKeyPressed(Keys.DOWN) || (Gdx.input.isKeyPressed(Keys.S))) {	
			    			movement(delta, 1);		    			
			    			gPS.getGamePlay().update(getX(),getY());
			    			animatedTracks(delta, true, true);
			    			animatedExhaust(delta,false,false);
			    		}
		
			    }
		    	
		    	if (orientationLEFT.equals(PlayerMovementsEnum.LEFT)) {	
		    		if (Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.input.isKeyPressed(Keys.A))) {
		    			angle +=  GameLogicInformation.speedUpFactor * GameLogicInformation.bgSpeed * delta;
		    			rotate();
		    			animatedTracks(delta, true, false);
		    			animatedExhaust(delta,false,true);
		    		}
	
		    	}
		    	
		    	if (orientationRIGHT.equals(PlayerMovementsEnum.RIGHT)) {
		    		if (Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.input.isKeyPressed(Keys.D))) {
		    			angle -=  GameLogicInformation.speedUpFactor * GameLogicInformation.bgSpeed * delta;
		    			rotate();
		    			animatedTracks(delta, false, true);
		    			animatedExhaust(delta,true, false);
		    		}
	
		    	}
		    	
		    	if (orientationS.equals(PlayerMovementsEnum.TURRETCLOCKWISE)) {
		    		if (Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT) || Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
		    			this.angleTurret -= GameLogicInformation.speedUpFactor * GameLogicInformation.bgSpeed * delta;
		    			rotateTurret();
		    		}
		    	}
		    	
		    	if (orientationA.equals(PlayerMovementsEnum.TURRETANTICLOCKWISE)) {
		    		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) {
		    			this.angleTurret += GameLogicInformation.speedUpFactor * GameLogicInformation.bgSpeed * delta;
		    			rotateTurret();
		    		}
		    	}
		    	
		    	if (orientationSHOOT.equals(PlayerMovementsEnum.SHOOT)) {
		    		
		    		if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isButtonPressed(Input.Buttons.LEFT) && SecondTestGDX.isMouseEnabled) {
		    			if (this.eDO.getAmmo() > 0) {
		    				shootGeneration(delta);
		    				
		    			}
		    		}
		    	}
		    	
		    	if (orientationMOUSEMOVE.equals(PlayerMovementsEnum.MOUSEMOVED)) {
			    	if ((SecondTestGDX.isMouseEnabled) && (!Gdx.input.isKeyPressed(Keys.A)) && (!Gdx.input.isKeyPressed(Keys.S))) {
			    		rotateTurretMouse();
			    		orientationMOUSEMOVE = PlayerMovementsEnum.IDLE;
			    	}
		    	}
		    	
		    	if (!Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Keys.DOWN) && !Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT)) {
		    		
		    		super.setCollisionVel(0.0f, 0.0f);
		    		animatedExhaust(delta, false, false);
		    		
		    		if (gPS.getGamePlay().getMapGenerationEngine().getTypeMap() == SimpleMapGeneration.TYPE_WINTER) {
		    			movementIce();
		     		 }
		    		
		    	}
		    	
		    	gPS.getGamePlay().update(getX(), getY());
    		}
	    	
    }
    
    
    public void shootGeneration(float delta) {
    	
    	time += delta;
		float speedGun = 800.0f;
		
		if (time  >= INTERVAL_BETWEEN_SHOOT) {
			
			float shootAngle = 0.0f;
			time = 0.0f;
			
			if (SecondTestGDX.isMouseEnabled) {shootAngle = angleTurret + 90;} else {shootAngle = angle+angleTurret+90;}
			
			//float x = (float) ((getX() + getWidth()/2 - ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2) + 50.0 * Math.cos(shootAngle*MathUtils.degRad)); 
			//float y = (float) ((getY()) + 50.0 * Math.sin(shootAngle*MathUtils.degRad));
			
			float x = 0.0f;
			float y = 0.0f;
			
			if (cannonType.equals(ElementEnum.GUN_PLAYER_1_C)) {
				x = (float) ((getX() + 16 + getWidth()/2 - ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2) + 50.0 * Math.cos(shootAngle*MathUtils.degRad)); 
				y = (float) (getY() + 16 + 50.0 * Math.sin(shootAngle*MathUtils.degRad));
			}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_D) || cannonType.equals(ElementEnum.GUN_PLAYER_1_E)) {
				
				x = (float) ((getX() + 8 + getWidth()/2 - ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2) + 60.0 * Math.cos(shootAngle*MathUtils.degRad)); 
				y = (float) (getY() + 8 + 60.0 * Math.sin(shootAngle*MathUtils.degRad));
				
			}else {
				x = (float) ((getX() + getWidth()/2 - ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2) + 50.0 * Math.cos(shootAngle*MathUtils.degRad)); 
				y = (float) ((getY()) + 50.0 * Math.sin(shootAngle*MathUtils.degRad));
			}
			
			if (this.eDO.getAmmo() > 0) {
				if (cannonType.equals(ElementEnum.GUN_PLAYER_1_A)) {
					this.addGun(SpawnType.Missile_Laser, shootAngle, speedGun, x , y, 0, 0, ElementEnum.LASER.getWidthShow(), ElementEnum.LASER.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-1);
					sfxShot.play();
				}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_B)) {
					this.addGun(SpawnType.Missile_Laser, shootAngle, speedGun, x-5 , y, 0, 0, ElementEnum.LASER.getWidthShow(), ElementEnum.LASER.getHeightShow());
					this.addGun(SpawnType.Missile_Laser, shootAngle, speedGun, x+5 , y, 0, 0, ElementEnum.LASER.getWidthShow(), ElementEnum.LASER.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-2);
					sfxShot.play();
				}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_C)){
					this.addGun(SpawnType.Missile_Missile, shootAngle+180, (-1)*speedGun, x, y, 0, 0, ElementEnum.MISSILE_1.getWidthShow(), ElementEnum.MISSILE_1.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-1);
					sfxMissile.play(sfxMissileVolume);
				}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_D)){
					this.addGun(SpawnType.Missile_Flame, shootAngle, speedGun, x , y, 0, 0, ElementEnum.FLAME_1.getWidthShow(), ElementEnum.FLAME_1.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-1);
					sfxFlame.play(sfxFlameVolume);
				}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_E)) {
					this.addGun(SpawnType.Missile_Pulse, shootAngle, speedGun, x , y, 0, 0, ElementEnum.PULSE.getWidthShow(), ElementEnum.PULSE.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-1);
					sfxShot.play();
				}else if (cannonType.equals(ElementEnum.GUN_PLAYER_1_F)) {
					this.addGun(SpawnType.Missile_Grenade, shootAngle, speedGun, x , y, 0, 0, ElementEnum.GRENADE.getWidthShow(), ElementEnum.GRENADE.getHeightShow());
					this.eDO.setAmmo(this.eDO.getAmmo()-1);
					sfxGrenade.play();
				}
				
				this.setShootEvent(true);
			}
		
		}
    }
    
    
    public void animatedTracks(float delta, boolean trackleft, boolean trackright) {
    	player_parts.get(INDEX_TRACK_LEFT).AnimationLoop(delta, trackleft);
    	player_parts.get(INDEX_TRACK_RIGHT).AnimationLoop(delta, trackright);
    }
    
    public void animatedExhaust(float delta, boolean trackleft, boolean trackright) {
    	player_parts.get(INDEX_EXHAUST_LEFT).AnimationLoop(delta, trackleft);
    	player_parts.get(INDEX_EXHAUST_RIGHT).AnimationLoop(delta, trackright);
    }
    
    
    public void rotate() {
    	super.rotate(angle);
    	direction.set((float)Math.cos(Math.toRadians(angle+270)), (float)Math.sin(Math.toRadians(angle+270))).nor();
    	super.setCollisionAngleRef(getX(), getY(), angle*MathUtils.degRad);
    	
    	//DISABLED ROTATION OF CAMERA.
    	
    	if (SecondTestGDX.rotateCameraWithPlayer) {
    		testingCameraRotation();
    	}
    	
    	player_parts.get(INDEX_TRACK_LEFT).rotate(angle,24,getHeight()/2);
    	player_parts.get(INDEX_TRACK_RIGHT).rotate(angle,-8,getHeight()/2); 	
    	
    	
    	if (!SecondTestGDX.isMouseEnabled) {
    		player_parts.get(INDEX_GUN).rotate(angle+angleTurret, ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2, ElementEnum.GUN_PLAYER_1_A.getHeightShow()/2-8 );
    	}else {
    		player_parts.get(INDEX_GUN).rotate(angleTurret, ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2, ElementEnum.GUN_PLAYER_1_A.getHeightShow()/2-8 );
    	}
    	
    	player_parts.get(INDEX_EXHAUST_LEFT).rotate(angle, 16, 56);
    	player_parts.get(INDEX_EXHAUST_RIGHT).rotate(angle, 0, 56);
    	
    }
    
    
    public void rotateTurretMouse() {
    	
    	Vector2 gunPosition = new Vector2();
    	Vector2 mousePosition = new Vector2();
    	
    	gunPosition.x = player_parts.get(INDEX_GUN).getX();
    	gunPosition.y = player_parts.get(INDEX_GUN).getY();
    	
    	mousePosition.x = Gdx.input.getX();
    	mousePosition.y = Gdx.input.getY();
    	
    	
    	float originX = this.gPS.getGamePlay().getCamera().position.x - SecondTestGDX.screenWidth/2;
    	float originY = this.gPS.getGamePlay().getCamera().position.y - SecondTestGDX.screenHeight/2; 
    	
    	gunPosition.x = gunPosition.x;
    	gunPosition.y = gunPosition.y;
    	
    	mousePosition.x += originX;
    	mousePosition.y += originY;
    	
    	
    	angleTurret = (float) Math.atan2((gunPosition.y-mousePosition.y),(gunPosition.x-mousePosition.x));
    	//angleTurret = (angleTurret*MathUtils.radDeg + 270)*(-1);
    	angleTurret = (-1)*(angleTurret*MathUtils.radDeg - 90);
    	
    	player_parts.get(INDEX_GUN).rotate(angleTurret, ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2, ElementEnum.GUN_PLAYER_1_A.getHeightShow()/2-8 );		
    			
    }
    
    
    
    public void rotateTurret() {
    	if (!SecondTestGDX.isMouseEnabled) {
    		player_parts.get(INDEX_GUN).rotate(angle+angleTurret, ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2, ElementEnum.GUN_PLAYER_1_A.getHeightShow()/2-8 );
    	}else {
    		player_parts.get(INDEX_GUN).rotate(angleTurret, ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2, ElementEnum.GUN_PLAYER_1_A.getHeightShow()/2-8 );
    	}
    }
    
    
    public ElementEnum getCannonType() {
    	return this.cannonType;
    }
    
    public void setCannonType(ElementEnum cannonType) {
		this.cannonType = cannonType;
	}
    
    public void setCurrentPlayerVariables(ElementDefinitionObject eDO) {
    	this.eDO = new ElementDefinitionObject(eDO);
    }
    
    
    public void changeTurret(ElementEnum newGun) {
    	
    	this.eDO.setAmmo(GameLogicInformation.MAX_AMMO_PLAYER);
    	
    	Texture gunTXT[] = null;
    	
    	this.cannonType = newGun;
    	
    	if (newGun.equals(ElementEnum.GUN_PLAYER_1_A)) {
    		gunTXT = GameLogicElementInformation.cannonPlayer01AText;
    	}else if (newGun.equals(ElementEnum.GUN_PLAYER_1_B)) {
    		gunTXT =  GameLogicElementInformation.cannonPlayer01BText;
    	}else if (newGun.equals(ElementEnum.GUN_PLAYER_1_C)) {
    		gunTXT = GameLogicElementInformation.cannonPlayer01CText;
    	}else if (newGun.equals(ElementEnum.GUN_PLAYER_1_D)) {
    		gunTXT = GameLogicElementInformation.cannonPlayer01DText;
    	}else if (newGun.equals(ElementEnum.GUN_PLAYER_1_E)) {
    		gunTXT = GameLogicElementInformation.cannonPlayer01EText;
    	}else if (newGun.equals(ElementEnum.GUN_PLAYER_1_F)) {
    		gunTXT = GameLogicElementInformation.cannonPlayer01FText;
    	}else {
    		gunTXT = GameLogicElementInformation.cannonPlayer01AText;
    	}
    	
    	player_parts.get(INDEX_GUN).init(gunTXT, 0);
    	rotateTurret();
    }
    
    public void movement(float delta, float index) {
    	 movement.set(direction).scl(GameLogicInformation.speedUpFactor * GameLogicInformation.bgSpeed * delta * index*4);
         position.add(movement);
         
         
     
         super.setCollisionVel(movement.x, movement.y);
         Vector2 posRelative = super.getPositionFromBodyToPixel();
         super.setPosition(posRelative.x, posRelative.y);
         
         this.spriteShield.setOriginBasedPosition(getX()+getWidth()/2, getY()+getHeight()/2);
         
         player_parts.get(INDEX_TRACK_LEFT).setPosition(getX()+8, getY());
         player_parts.get(INDEX_TRACK_RIGHT).setPosition(getX()+40, getY());
         player_parts.get(INDEX_GUN).setPosition(getX()+(getWidth()/2)-(ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2), getY()+8);
         player_parts.get(INDEX_EXHAUST_LEFT).setPosition(getX()+(getWidth()/2)-16, getY()-ElementEnum.EXHAUST_01.getHeightShow()+8);
         player_parts.get(INDEX_EXHAUST_RIGHT).setPosition(getX()+(getWidth()/2), getY()-ElementEnum.EXHAUST_01.getHeightShow()+8);
    }
    
    public void movementIce() {
    	
    	this.skiddingInIce();
    	Vector2 posRelative = super.getPositionFromBodyToPixel();
        super.setPosition(posRelative.x, posRelative.y);
        
        this.spriteShield.setOriginBasedPosition(getX()+getWidth()/2, getY()+getHeight()/2);
        
        player_parts.get(INDEX_TRACK_LEFT).setPosition(getX()+8, getY());
        player_parts.get(INDEX_TRACK_RIGHT).setPosition(getX()+40, getY());
        player_parts.get(INDEX_GUN).setPosition(getX()+(getWidth()/2)-(ElementEnum.GUN_PLAYER_1_A.getWidthShow()/2), getY()+8);
        player_parts.get(INDEX_EXHAUST_LEFT).setPosition(getX()+(getWidth()/2)-16, getY()-ElementEnum.EXHAUST_01.getHeightShow()+8);
        player_parts.get(INDEX_EXHAUST_RIGHT).setPosition(getX()+(getWidth()/2), getY()-ElementEnum.EXHAUST_01.getHeightShow()+8);

    	
    }
    
    
    
    public void collision() {
    	setCollisionRef(getX(),getY());
    }
    
    public void actionPlayerUP(PlayerMovementsEnum orientation) {this.orientationUP = orientation;}
    public void actionPlayerDOWN(PlayerMovementsEnum orientation) {this.orientationDOWN = orientation;}
    public void actionPlayerLEFT(PlayerMovementsEnum orientation) {this.orientationLEFT = orientation;}
    public void actionPlayerRIGHT(PlayerMovementsEnum orientation) {this.orientationRIGHT = orientation;}
    public void actionPlayerA(PlayerMovementsEnum orientation) {this.orientationA = orientation;}
    public void actionPlayerS(PlayerMovementsEnum orientation) {this.orientationS = orientation;}
    public void actionPlayerSHOOT(PlayerMovementsEnum orientation) {this.orientationSHOOT = orientation;}
    public void actionPlayerMOUSEMOVE(PlayerMovementsEnum orientation) {this.orientationMOUSEMOVE = orientation;}
    
    public void draw(SpriteBatch sb) {
    	
    	player_parts.get(INDEX_TRACK_LEFT).draw(sb);
    	player_parts.get(INDEX_TRACK_RIGHT).draw(sb);
    	player_parts.get(INDEX_EXHAUST_LEFT).draw(sb);
    	player_parts.get(INDEX_EXHAUST_RIGHT).draw(sb);
    	/*HULL*/super.draw(sb);
    	player_parts.get(INDEX_GUN).draw(sb);
    	
    	if (this.eDO.getShield() > 0) {this.spriteShield.draw(sb);}
    	
    }

	@Override
	public void AnimationByMovement(PlayerMovementsEnum movement, float moveStepX, float moveStepY, boolean isAccX,
			boolean isAccY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AnimationByTime(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AnimationLoop(float delta, boolean loop) {
		// TODO Auto-generated method stub
		
	}
	
	public void dispose() {
		player_parts.clear();
		myLight_point.remove();
		myLight_cone.remove();
	}
	
	
	public void setShotSound(String path, float volume) {
	     sfxShot = Gdx.audio.newSound(Gdx.files.internal(path));
	     sfxShotVolume = volume;
	}
	
	public void setFlameSound(String path, float volume) {
	     sfxFlame = Gdx.audio.newSound(Gdx.files.internal(path));
	     sfxFlameVolume = volume;
	}
	
	public void setMissileSound(String path, float volume) {
		sfxMissile = Gdx.audio.newSound(Gdx.files.internal(path));
		sfxMissileVolume = volume;
	}
	
	public void setGrenadeSound(String path, float volume) {
		sfxGrenade = Gdx.audio.newSound(Gdx.files.internal(path));
		sfxGrenadeVolume = volume;
	}

	
	public ElementDefinitionObject getStatsDynElement() {
		return this.eDO;
	}
	
	//ROTATION CAMERA
	public void testingCameraRotation() {
    	
    	OrthographicCamera cameraCobayera = this.gPS.getGamePlay().getCamera();
    	float anglePlayerCobayero = this.angle;
    	float angleCameraCobayera = (float)Math.atan2(cameraCobayera.up.x, cameraCobayera.up.y)*MathUtils.radiansToDegrees;
    	angleCameraCobayera = (-1)*angleCameraCobayera + 180;
    	this.gPS.getGamePlay().getCamera().rotate((angleCameraCobayera-anglePlayerCobayero)+180);
    	
    }

	


	
	
	

}
