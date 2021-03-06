package com.mygdx.game.logic;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.SecondTestGDX;
import com.mygdx.game.elements.ElementDefinitionObject;
import com.mygdx.game.enums.BackgroundMusicEnum;
import com.mygdx.game.enums.ElementEnum;
import com.mygdx.game.enums.SpawnType;
import com.mygdx.game.enums.TileMapEnum;
import com.mygdx.game.logic.elements.SpawnObject;
import com.mygdx.game.screens.GamePlayScreen;
import com.mygdx.game.utils.NewItem;

public class GameLogicInformation {

	
	public static final int NUM_LEVELS = 10;
	public static final int FIRST_LEVEL = 0;
	public static final int END_GAME = 99;
	
	public static final int START = 0;
	public static final int INTERMISSION = 1;
	public static final int ENDLEVEL = 2;
	public static final int SETTINGS = 3;
	public static final int SCORE = 4;
	public static final int ENDGAME = 5;
	public static final int RANKING = 6;
	public static final int GAMEPLAY = 7;
	
	public static final float PIXELS_TO_METERS = 100f;
	
	public static final int PLAYERS = 1;
	
	public static final int ENEMIESDRON = 100;
	public static final int ENEMIESTANK = 9;
	public static final int ENEMIESMINE = 20;
	public static final int ENEMIESWATCHTOWER = 20;
	
	public static final double MIN_DISTANCE_BETWEEN_ENEMIES = 200.0f;
	
	public static final double SEEK_DISTANCE = 750;
	public static final double ATTACK_DISTANCE = 128;
	public static final double ATTACK_DISTANCE_WATCHTOWER = 300;
	
	
	public static final double DST_TANK_EXIT = 120;
	public static final double DST_TANK_PLAYER = 500;
	public static final double DST_TANK_COLL_OTHER = 300;
	
	
	public static final int DESERT_LEVEL = 0;
	public static final int JUNGLE_LEVEL = 1;
	public static final int FABRIC_LEVEL = 2;
	public static final int WINTER_LEVEL = 3;
	public static final int BADLAND_LEVEL = 4;
	public static final int VOLCANO_LEVEL = 5;
	public static final int CITY_LEVEL = 6;
	public static final int SPACE_LEVEL = 7;
	public static final int ISLAND_LEVEL = 8;
	public static final int WASTELAND_LEVEL = 9;
	public static final int IDLE_LEVEL = 10;
	
	public static final int FOREST_DESERT = 3;
	public static final int FOREST_JUNGLE = 3;
	public static final int FOREST_FABRIC = 1;
	public static final int FOREST_WINTER = 1;
	public static final int FOREST_BADLAND = 3;
	public static final int FOREST_VOLCANO = 1;
	public static final int FOREST_CITY = 1;
	public static final int FOREST_SPACE = 2;
	public static final int FOREST_ISLAND = 3;
	public static final int FOREST_WASTELAND = 2;
	
	public static final int FOREST_DEFAULT = 1;
	
	public static final int NO_LIGHTS = 0;
	public static final int LIGHTS = 1;
	
	public static final float speedUpFactor = 1.0f;
	public static final float bgSpeed = 50.0f;
	public static final float bgSpeedPos = 20.0f;
		
	public static final int MAX_LIFE_PLAYER = 10;
	public static final int MAX_SHIELD_PLAYER = 10;
	public static final int MAX_AMMO_PLAYER = 1000;
	public static final int SCORE_PLAYER = 0;
	
	public static final int MAX_LIFE_DRON = 1;
	public static final int MAX_SHIELD_DRON = 0;
	public static final int MAX_AMMO_DRON = 0;
	public static final int SCORE_DRON = 100;
	
	public static final int MAX_LIFE_WATCHTOWER = 1;
	public static final int MAX_SHIELD_WATCHTOWER = 0;
	public static final int MAX_AMMO_WATCHTOWER = 0;
	public static final int SCORE_WATCHTOWER = 100;
	
	public static final int MAX_LIFE_TANK_1 = 4;
	public static final int MAX_SHIELD_TANK_1 = 0;
	public static final int MAX_AMMO_TANK_1 = 0;
	public static final int SCORE_TANK_1 = 200;
	
	public static final int MAX_LIFE_TANK_2 = 6;
	public static final int MAX_SHIELD_TANK_2 = 0;
	public static final int MAX_AMMO_TANK_2 = 0;
	public static final int SCORE_TANK_2 = 400;
	
	
	public static final int MAX_LIFE_TANK_3 = 8;
	public static final int MAX_SHIELD_TANK_3 = 0;
	public static final int MAX_AMMO_TANK_3 = 0;
	public static final int SCORE_TANK_3 = 600;
	
	public static final int MINE_DAMAGE = 5;
	
	public static final int INI_TIME_LEVEL = 300;
	
	public static final int MIN_ENEMIES_TO_EXIT = 5;
	
	private static int level;
	private static int levelGameplay;
	
	private static long time_level;
	
	private static long enemiesLeft;
	
	
	private static ElementDefinitionObject currentStatePlayer;
	private static ElementEnum currentCannon;
	
	public static final String backGround_Start = SecondTestGDX.resources.imgSplash;
    //public static final String backGround_Start = SecondTestGDX.resources.imgSplash_1;
	public static final String backGround_Intermission = SecondTestGDX.resources.imgIntermission;
	
	public static final String backGround_Start_MP3 = SecondTestGDX.resources.musicSplash;
	public static final String backGround_Intermission_MP3 = SecondTestGDX.resources.musicIntermission;
	
	public static final String backGround_AfterLevel_DIED_MP3 = SecondTestGDX.resources.musicPlayerDied;
	public static final String backGround_AfterLevel_NEXT_MP3 = SecondTestGDX.resources.musicEndLevel;
	public static final String backGround_AfterLevel_END_MP3 = SecondTestGDX.resources.musicEndGame;
	private static BackgroundMusicEnum mEnum;
	
	public static void setLevelGamePlay(int level) {
		if ((level >= FIRST_LEVEL) && (level < END_GAME)) {
			GameLogicInformation.levelGameplay = level;
		}else {
			GameLogicInformation.levelGameplay = GameLogicInformation.FIRST_LEVEL;
		}
	}
	
	public static int getLevelGamePlay() {
		return GameLogicInformation.levelGameplay;
	}
	
	public static void setCurrentPlayerVariables(ElementDefinitionObject currentStatePlayer, ElementEnum currentCannon) {
		GameLogicInformation.currentStatePlayer = new ElementDefinitionObject(currentStatePlayer);
		GameLogicInformation.currentCannon = ElementEnum.getByIndex(currentCannon.getIndex());
	}
	
	public static ElementDefinitionObject getCurrentPlayerVariables() {
		return GameLogicInformation.currentStatePlayer;
	}
	
	public static ElementEnum getCurrentCannon() {
		return GameLogicInformation.currentCannon;
	}
	
	private static long getEnemiesAlive(GamePlayScreen gPS) {
		
		long enemies = 0L;
		
		enemies += gPS.getGamePlay().getGameLogic().getPool(SpawnType.Enemy_01).size();
		enemies += gPS.getGamePlay().getGameLogic().getPool(SpawnType.Enemy_02).size();
		enemies += gPS.getGamePlay().getGameLogic().getPool(SpawnType.Enemy_03).size();
		
		ArrayList<SpawnObject> items = gPS.getGamePlay().getGameLogic().getPool(SpawnType.Item);
		for(SpawnObject item:items) {if (item.getSubType().equals(SpawnType.Item_Mine)) {enemies+=1;}}
		
		return enemies;
		
	}
	
	
	public static long getEnemiesLeft(GamePlayScreen gPS) {
		return getEnemiesAlive(gPS);
	}
	
	
	public static void setTimeLevel(long time_level) {
		GameLogicInformation.time_level = time_level;
	}
	
	public static long getTimeLevel() {
		return GameLogicInformation.time_level;
	}
	
	
	public static void setLevel(int level) {
		
		if (level >= START) {
			GameLogicInformation.level = level;
		}else {
			GameLogicInformation.level = GameLogicInformation.START;
		}
	}
	
	public static int getLevel() {
		return GameLogicInformation.level;
	}
	
	public static String getBackgroundImage() {
		if (level == START) {
			return backGround_Start;
		}else if ((level == INTERMISSION) || (level == ENDLEVEL) || (level == SETTINGS) || (level == RANKING)){
			return backGround_Intermission;
		}else {
			return "";
		}
	}
	
	public static String getBackgroundMusic(GamePlayScreen gPS) {
		if (level == START) {
			return backGround_Start_MP3;
		}else if (level == INTERMISSION) {
			return backGround_Intermission_MP3;
		}else if ((level == ENDLEVEL) && (gPS.getGamePlay().isNextLevel())){
			return backGround_AfterLevel_NEXT_MP3;
		}else if ((level == ENDLEVEL) && (gPS.getGamePlay().isEndGame())){
			return backGround_AfterLevel_END_MP3;
		}else if ((level == ENDLEVEL) && (gPS.getGamePlay().isPlayerDied())){
			return backGround_AfterLevel_DIED_MP3;
		}else {
			mEnum = BackgroundMusicEnum.MUSIC_1;
			Random ran = new Random();
			int musicIndex = ran.nextInt(6)+1;
			return mEnum.getByIndex(musicIndex).getMusicStr();
		}
	}
	
	
	public static int getRandomForestTileMap(int index) {
		if (index == DESERT_LEVEL) {return FOREST_DESERT;}
		else if (index == JUNGLE_LEVEL) {return FOREST_JUNGLE;}
		else if (index == FABRIC_LEVEL) {return FOREST_FABRIC;}
		else if (index == WINTER_LEVEL) {return FOREST_WINTER;}
		else if (index == BADLAND_LEVEL) {return FOREST_BADLAND;}
		else if (index == VOLCANO_LEVEL) {return FOREST_VOLCANO;}
		else if (index == CITY_LEVEL) {return FOREST_CITY;}
		else if (index == SPACE_LEVEL) {return FOREST_SPACE;}
		else if (index == ISLAND_LEVEL) {return FOREST_ISLAND;}
		else if (index == WASTELAND_LEVEL) {return FOREST_WASTELAND;}
		else {return FOREST_DEFAULT;}
	}
	
	
	
	public static TileMapEnum[] getRandomTileMap(int index) {
		
		TileMapEnum[] levelMap= new TileMapEnum[8];
		
		if (index == DESERT_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING DESERT_LEVEL");
			levelMap[0] = TileMapEnum.GROUND_TILE_02_C;
			levelMap[1] = TileMapEnum.BLOCK_B_02;
			levelMap[2] = TileMapEnum.BLOCK_C_02;
			
			levelMap[3] = TileMapEnum.CACTUS_01;
			levelMap[4] = TileMapEnum.CACTUS_02;
			levelMap[5] = TileMapEnum.CACTUS_03;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
			
		}else if (index == JUNGLE_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING JUNGLE_LEVEL");
			levelMap[0] = TileMapEnum.GROUND_TILE_02_D;
			levelMap[1] = TileMapEnum.BLOCK_A_02;
			levelMap[2] = TileMapEnum.BLOCK_D_02;
			
			levelMap[3] = TileMapEnum.PALM_01;
			levelMap[4] = TileMapEnum.PALM_02;
			levelMap[5] = TileMapEnum.PALM_03;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
		}else if (index == FABRIC_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING INDUSTRIAL_LEVEL");
			levelMap[0] = TileMapEnum.GROUND_TILE_01_A;
			levelMap[1] = TileMapEnum.BLOCK_C_02;
			levelMap[2] = TileMapEnum.BLOCK_A_02;
			
			levelMap[3] = TileMapEnum.SOLAR_02;
			levelMap[4] = TileMapEnum.CZECH_01;
			levelMap[5] = TileMapEnum.CZECH_02;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
		
		}else if (index == WINTER_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING WINTER_LEVEL");
			levelMap[0] = TileMapEnum.GROUND_TILE_02_E;
			levelMap[1] = TileMapEnum.BLOCK_E_02;
			levelMap[2] = TileMapEnum.BLOCK_E_01;
			
			levelMap[3] = TileMapEnum.ICEBERG_01;
			
			levelMap[4] = null;
			levelMap[5] = null;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
		}else if (index == BADLAND_LEVEL) {	
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING BADLAND_LEVEL");
			
			levelMap[0] = TileMapEnum.GROUND_TILE_02_F;
			levelMap[1] = TileMapEnum.BUILDING_01;
			levelMap[2] = TileMapEnum.BLOCK_F_02;
			
			levelMap[3] = TileMapEnum.TREE_07;
			levelMap[4] = TileMapEnum.TREE_08;
			levelMap[5] = TileMapEnum.TREE_09;
			
			levelMap[6] = null;
			levelMap[7] = null;
		
		}else if (index == VOLCANO_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING VOLCANO_LEVEL");
			
			levelMap[0] = TileMapEnum.GROUND_TILE_01_B;
			levelMap[1] = TileMapEnum.BLOCK_F_02;
			levelMap[2] = TileMapEnum.VOLCANO_FOREST_1;
			levelMap[3] = TileMapEnum.VOLCANO_WALL;
			
			levelMap[4] = null;
			levelMap[5] = null;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
		}else if (index == CITY_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING CITY_LEVEL");
			levelMap[0] = TileMapEnum.BACKGROUND_CITY;
			levelMap[1] = TileMapEnum.BLOCK_A_02;
			levelMap[2] = TileMapEnum.WALL_CITY;
			levelMap[3] = TileMapEnum.PARK_CITY;
			levelMap[4] = null;
			levelMap[5] = null;
			levelMap[6] = null;
			levelMap[7] = null;
		
		}else if (index == SPACE_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING SPACE_LEVEL");
		
			levelMap[0] = TileMapEnum.GROUND_TILE_02_A;
			
			levelMap[1] = TileMapEnum.SPACE_WALL;
			levelMap[2] = TileMapEnum.SPACE_WALL;
			
			levelMap[3] = TileMapEnum.SPACE_FOREST_1;
			
			levelMap[4] = TileMapEnum.SPACE_FAN_1;
			levelMap[5] = TileMapEnum.SPACE_FAN_2;
			levelMap[6] = TileMapEnum.SPACE_FAN_3;
			levelMap[7] = TileMapEnum.SPACE_FAN_4;
			
		}
		
		else if (index == ISLAND_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING ISLAND_LEVEL");
			
			levelMap[0] = TileMapEnum.BACKGROUND_ISLAND;
			
			//levelMap[1] = TileMapEnum.WATER_2;
			//levelMap[2] = TileMapEnum.WATER_2;
			
			levelMap[1] = TileMapEnum.WATER_1;
			levelMap[2] = TileMapEnum.WATER_1;
			
			levelMap[3] = TileMapEnum.PALM_01;
			levelMap[4] = TileMapEnum.PALM_02;
			levelMap[5] = TileMapEnum.PALM_03;
			
			levelMap[6] = null;
			levelMap[7] = null;
		
			
		}else if (index == WASTELAND_LEVEL) {
			
			Gdx.app.log("[GAMELOGICINFORMATION]", "GENERATING WASTELAND_LEVEL");
			
			levelMap[0] = TileMapEnum.GROUND_TILE_02_A;
			
			levelMap[1] = TileMapEnum.BLOCK_A_02;
			levelMap[2] = TileMapEnum.SLIME_1;
			
			levelMap[3] = TileMapEnum.BARREL_1;
			levelMap[4] = TileMapEnum.BARREL_2;
			//levelMap[5] = TileMapEnum.BARREL_3;
			levelMap[5] = null;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
			
			
		}else {
			
			levelMap[0] = TileMapEnum.GROUND_TILE_02_C;
			levelMap[1] = TileMapEnum.BLOCK_B_02;
			levelMap[2] = TileMapEnum.BLOCK_C_02;
			
			levelMap[3] = TileMapEnum.CACTUS_01;
			levelMap[4] = TileMapEnum.CACTUS_02;
			levelMap[5] = TileMapEnum.CACTUS_03;
			
			levelMap[6] = null;
			levelMap[7] = null;
			
		}
		
		return levelMap;
	}
	
	
}

/*
public static final short GROUP_SCENERY = 1;
public static final short GROUP_PLAYER = -1;
public static final short GROUP_MONSTER = -2;
public static final short MISSILE_MONSTER = -3;
public static final short MISSILE_PLAYER = -4;
*/
