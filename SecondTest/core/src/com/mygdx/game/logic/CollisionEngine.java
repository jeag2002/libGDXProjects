package com.mygdx.game.logic;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.SecondTestGDX;
import com.mygdx.game.elements.enemies.centroid.WatchTowerEnemy;
import com.mygdx.game.elements.enemies.drons.SimpleEnemy;
import com.mygdx.game.elements.enemies.special.tanks.TankEnemy;
import com.mygdx.game.elements.enemies.special.tanks.TankEnemyStateEnum;
import com.mygdx.game.elements.items.Item;
import com.mygdx.game.elements.missiles.Missile;
import com.mygdx.game.elements.players.simpleplayer.Player;
import com.mygdx.game.enums.SpawnType;
import com.mygdx.game.logic.elements.SpawnObject;
import com.mygdx.game.logic.elements.SpawnPool;
import com.mygdx.game.logic.map.SimpleMapGeneration;
import com.mygdx.game.logic.map.elements.StaticTiledMapColl;
import com.mygdx.game.screens.GamePlayScreen;
import com.mygdx.game.utils.NewItem;

public class CollisionEngine implements ContactListener{
	
	private static final int LIMIT_BONUS = 40;
	
	private GamePlayScreen gPS;
	private TiledMap map;
	
	private TiledMapTileLayer walls;
	private TiledMapTileLayer forests;
	
	private SpawnPool pool;
	private Player player;
	
	private Random rand;
	
	private int bonusGenerator;
	
	
	private ArrayList<StaticTiledMapColl> wallElements;
	private ArrayList<StaticTiledMapColl> forestElements;
	
	
	public CollisionEngine(GamePlayScreen gPS, TiledMap map, ArrayList<StaticTiledMapColl> walls, ArrayList<StaticTiledMapColl> forest) {
		super();
		this.gPS = gPS;
		this.map = map;
		this.wallElements = walls;
		this.forestElements = forest;
		
		this.walls = (TiledMapTileLayer)map.getLayers().get(SimpleMapGeneration.INDEX_WALLS);
		this.forests = (TiledMapTileLayer)map.getLayers().get(SimpleMapGeneration.INDEX_FOREST);
		this.pool = gPS.getGamePlay().getGameLogic().getSpawnPool();
		this.player = gPS.getGamePlay().getGameLogic().getPlayer();
		
		this.bonusGenerator = 0;
		
		rand = new Random();
	}
	
	
	
	
	@Override
	public void beginContact(Contact contact) {
		
		NewItem objectStrA = (NewItem)contact.getFixtureA().getBody().getUserData();
		NewItem objectStrB = (NewItem)contact.getFixtureB().getBody().getUserData();
		
		System.out.println("Collision detected A (" + objectStrA.getIdCode() + ")" +  objectStrA.getType().toString() + " B (" + objectStrB.getIdCode() + ")" + objectStrB.getType().toString());
		
		//-->MISSILE ENEMY
	    if (objectStrA.getType().equals(SpawnType.MissileEnemy)) {
	    	processEnemyMissile(objectStrA, objectStrB);
	    }else if (objectStrB.getType().equals(SpawnType.MissileEnemy)) {
	    	processEnemyMissile(objectStrB, objectStrA);
	    }
	    
	    //-->MISSILE PLAYER
	    if (objectStrA.getType().equals(SpawnType.MissilePlayer)) {
	    	processPlayerMissile(objectStrA, objectStrB);
	    }else if (objectStrB.getType().equals(SpawnType.MissilePlayer)) {
	    	processPlayerMissile(objectStrB, objectStrA);
	    }
	    
	    //-->ENEMY2
	    if (objectStrA.getType().equals(SpawnType.Enemy_02)) {
	    	processEnemy2(objectStrA, objectStrB);
	    }else if (objectStrB.getType().equals(SpawnType.Enemy_02)) {
	    	processEnemy2(objectStrB, objectStrA);
	    }
	    
	    //-->PLAYER
	    if (objectStrA.getType().equals(SpawnType.Player_01)) {
	    	processPlayer(objectStrB);
	    }else if (objectStrB.getType().equals(SpawnType.Player_01)) {
	    	processPlayer(objectStrA);
	    }
	    
	}
	
	private void processPlayer(NewItem other) {
		
		
		if (other.getType().equals(SpawnType.Item)) {
			if (other.getSubType().equals(SpawnType.Item_Mine)) {
				
				SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
				if (object != null) {
					if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
						gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
						
						Item item = (Item)object;
						
						other.setX(item.getX());
						other.setY(item.getY());
						other.setWidth(item.getWidth());
						other.setHeight(item.getHColl());
						
						long numEnemies = GameLogicInformation.getEnemiesLeft();
						if (numEnemies > 0) {GameLogicInformation.setEnemiesLeft(numEnemies-1);}
						
						gPS.getGamePlay().processPlayerVariables(GameLogicInformation.MINE_DAMAGE);
						gPS.getGamePlay().getGameLogic().bigExplosionSoundPlay();
						createExplosionDynamic(other);
						
					}
				}
				
				
				
				
			}else if (other.getSubType().equals(SpawnType.Item_Bonus_Shield) || 
					other.getSubType().equals(SpawnType.Item_Bonus_Bullet) || 
					other.getSubType().equals(SpawnType.Item_Bonus_Gun) ||
					other.getSubType().equals(SpawnType.Item_Bonus_Life)) {
				
				boolean bonusAccepted = false;
				
				if (other.getSubType().equals(SpawnType.Item_Bonus_Life)) {
					
					
					if (gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().getLife() < GameLogicInformation.MAX_LIFE_PLAYER) {
						gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().setLife(gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().getLife() + 1);
						bonusAccepted = true;
					}
					
				}else if (other.getSubType().equals(SpawnType.Item_Bonus_Shield)) {
					
					if (gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().getShield() < GameLogicInformation.MAX_SHIELD_PLAYER) {
						gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().setShield(gPS.getGamePlay().getGameLogic().getPlayer().getStatsDynElement().getShield() + 1);
						bonusAccepted = true;
					}
				
				}else if (other.getSubType().equals(SpawnType.Item_Bonus_Bullet)) {
					gPS.getGamePlay().changeTurretPlayer();
					bonusAccepted = true;
				}
				
				if (bonusAccepted) {
					gPS.getGamePlay().getGameLogic().bonusSoundPlay();
					
					SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
					if (object != null) {
						if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
							gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
						}
					}
				}
			}
		}
		
		else if (other.getType().equals(SpawnType.Enemy_03)) {
			
			SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
			if (object != null) {
				if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
					
					gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
					gPS.getGamePlay().processPlayerVariables();
					
					
					long numEnemies = GameLogicInformation.getEnemiesLeft();
					if (numEnemies > 0) {GameLogicInformation.setEnemiesLeft(numEnemies-1);}
					
					createExplosionDynamic(other);
					gPS.getGamePlay().getGameLogic().crashSoundPlay();
					gPS.getGamePlay().getGameLogic().explosionSoundPlay();
				
				}
			}
			
		}
		
		else if (other.getType().equals(SpawnType.MissileEnemy)){
			//System.out.println("Collision Detected Player vs Missile Enemy");
		}
		
		else if (other.getType().equals(SpawnType.Wall_Space)) {
			//System.out.println("Collision Detected Player vs Wall Space");
			gPS.getGamePlay().dieInmediately();
			gPS.getGamePlay().getGameLogic().explosionSoundPlay();
		}
		
		else if (other.getType().equals(SpawnType.Wall_Volcano)) {
			//System.out.println("Collision Detected Player vs Wall Volcano");
			gPS.getGamePlay().processPlayerVariables();
			gPS.getGamePlay().getGameLogic().fireSoundPlay();
		}
		
		
		
	}
	
	
	private void createExplosionDynamic(NewItem objectStr) {
		
		NewItem explosion = new NewItem(SpawnType.Explosion,SpawnType.Simple_Explosion, objectStr.getX(), objectStr.getY(), objectStr.getWidth(), objectStr.getHeight());
		gPS.getGamePlay().getGameLogic().getSpawnPool().getCreatedBodiesWithCollision().add(explosion);
		
	}
	
	private void createFlames(NewItem objectStr) {
		NewItem explosion = new NewItem(SpawnType.Explosion,SpawnType.Fire, objectStr.getX(), objectStr.getY(), objectStr.getWidth(), objectStr.getHeight());
		gPS.getGamePlay().getGameLogic().getSpawnPool().getCreatedBodiesWithCollision().add(explosion);
	}
	
	
	private void createExplosionStatic(NewItem objectStr) {		
		NewItem explosion = new NewItem(SpawnType.Explosion,SpawnType.Simple_Explosion, objectStr.getIndex_X()*SecondTestGDX.tileWidth_TL, objectStr.getIndex_Y()*SecondTestGDX.tileHeight_TL, SecondTestGDX.tileWidth_TL, SecondTestGDX.tileHeight_TL);
		gPS.getGamePlay().getGameLogic().getSpawnPool().getCreatedBodiesWithCollision().add(explosion);
		
	}
	
	private void createBonus(NewItem objectStr) {
			
			this.bonusGenerator++;
		
			if (bonusGenerator >= LIMIT_BONUS) {
				bonusGenerator = 0;
				NewItem Bonus = new NewItem(SpawnType.Item,SpawnType.Item_Bonus, objectStr.getIndex_X()*SecondTestGDX.tileWidth_TL, objectStr.getIndex_Y()*SecondTestGDX.tileHeight_TL, SecondTestGDX.tileWidth_TL, SecondTestGDX.tileHeight_TL);
				gPS.getGamePlay().getGameLogic().getSpawnPool().getCreatedBodiesWithCollision().add(Bonus);
			}
	
		
	}
	
	
	public void processEnemy2(NewItem objectStr, NewItem other) {
		
		
			boolean isIniEnemy = false;
		
			SpawnObject otherObject = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
			
			if (otherObject != null) {
				if (other.getType().equals(SpawnType.Item)) {
					Item item = (Item)otherObject;
					if (item.getSubType().equals(SpawnType.Item_PlatformEnemy)) {
						isIniEnemy = true;
					}
				}
			}
			
			
			
			if 			
			(other.getType().equals(SpawnType.Enemy_01) ||
			other.getType().equals(SpawnType.Enemy_03) ||
			other.getType().equals(SpawnType.Enemy_02) ||
			(other.getType().equals(SpawnType.Item) && !isIniEnemy) ||
			other.getType().equals(SpawnType.Wall_City) ||	
			other.getType().equals(SpawnType.Wall_Badlands) || 
			other.getType().equals(SpawnType.Wall_Desert) ||
			other.getType().equals(SpawnType.Wall_Fabric) ||
			other.getType().equals(SpawnType.Wall_Jungle) ||
			other.getType().equals(SpawnType.Wall_Volcano) ||
			other.getType().equals(SpawnType.Wall_Winter) ||
			other.getType().equals(SpawnType.Wall_Space) ||
			other.getType().equals(SpawnType.Wall_Island) ||
			other.getType().equals(SpawnType.Forest_Volcano) ||
			other.getType().equals(SpawnType.Forest_Winter) ||
			other.getType().equals(SpawnType.Forest_Space) ||
			other.getType().equals(SpawnType.Border)) {
				
				SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(objectStr.getIdCode());
				
				
				
				if 
				(other.getType().equals(SpawnType.Enemy_01) ||
				 other.getType().equals(SpawnType.Enemy_03) ||
				 other.getType().equals(SpawnType.Enemy_02) ||
				 (other.getType().equals(SpawnType.Item) && !isIniEnemy)) {
					
					if (object != null) {
						
						object.getBox2DBody().setLinearVelocity(0, 0);
						object.getBox2DBody().setAngularVelocity(0.0f);	
						
						((TankEnemy)object).getStateMachine().changeState(TankEnemyStateEnum.STOP);
						((TankEnemy)object).stopTANKState(0);
						
						if (other.getType().equals(SpawnType.Enemy_02)) {
							
							SpawnObject otherO = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
							((TankEnemy)otherO).getStateMachine().changeState(TankEnemyStateEnum.STOP);
							((TankEnemy)otherO).stopTANKState(0);
							
						}
						
						//Telegram msg = new Telegram();
						//msg.extraInfo = new NewItem(other);
						//((TankEnemy)object).handleMessage(msg);
						
						//Gdx.app.log("[COLL]"," ENEMY COLL (" + objectStr.getIdCode() + ") WITH ENEMY_1/ENEMY_3/ENEMY_2/ITEM (" + other.getIdCode() + ") MOVE STOP");
						
					}
				
				}else if (other.getType().equals(SpawnType.Border) || 
						other.getType().equals(SpawnType.Wall_Volcano) || 
						other.getType().equals(SpawnType.Wall_Island) || 
						other.getType().equals(SpawnType.Wall_Space)) {	
					
					if (object != null) {
						object.getBox2DBody().setLinearVelocity(0, 0);
						object.getBox2DBody().setAngularVelocity(0.0f);		
						((TankEnemy)object).getStateMachine().changeState(TankEnemyStateEnum.STOP);
						
						//Gdx.app.log("[COLL]"," ENEMY COLL (" + objectStr.getIdCode() + ") WITH BORDER (" + other.getIdCode() + ") STOP MSG");
					}
					
				}else if (other.getType().equals(SpawnType.Wall_City) ||
						other.getType().equals(SpawnType.Wall_Badlands) || 
						other.getType().equals(SpawnType.Wall_Desert) ||
						other.getType().equals(SpawnType.Wall_Fabric) ||
						other.getType().equals(SpawnType.Wall_Jungle) ||
						other.getType().equals(SpawnType.Wall_Winter) 
						){
					
						Cell cell = walls.getCell(other.getIndex_X(), other.getIndex_Y());
						if (cell != null) {
							StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
							if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().add(tile);
							}
							
							createExplosionStatic(other);
							createBonus(other); 
							
						}
						
						
						
						((TankEnemy)object).getStateMachine().changeState(TankEnemyStateEnum.MOVE);		
						Telegram msg = new Telegram();
						msg.extraInfo = new NewItem(other);
						((TankEnemy)object).handleMessage(msg);
							
						//Gdx.app.log("[COLL]"," ENEMY COLL (" + objectStr.getIdCode() + ") WITH WALL_DESTROYABLE (" + other.getIdCode() + ") MOVE MSG");
						
						
				}else if (other.getType().equals(SpawnType.Forest_Volcano) ||
						other.getType().equals(SpawnType.Forest_Winter) ||
						other.getType().equals(SpawnType.Forest_Space)) {
						
						Cell cell = forests.getCell(other.getIndex_X(), other.getIndex_Y());
					
						if (cell != null) {
						
							if (other.getType().equals(SpawnType.Forest_Space)) {
								
								if (cell.getTile() instanceof AnimatedTiledMapTile) {
									AnimatedTiledMapTile tile = (AnimatedTiledMapTile)cell.getTile();						
									if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().contains(tile)) {
										gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().add(tile);
									}
								}else {
									StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
									if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
										gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
									}
								}
							
							}else {
								StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
								}
							}
							
							createExplosionStatic(other);
							createBonus(other);
							
						}
						
						
						
						//Gdx.app.log("[COLL]"," ENEMY COLL (" + objectStr.getIdCode() + ") WITH FOREST_DESTROYABLE (" + other.getIdCode() + ") MOVE MSG");
						
						((TankEnemy)object).getStateMachine().changeState(TankEnemyStateEnum.MOVE);	
						Telegram msg = new Telegram();
						msg.extraInfo = new NewItem(other);
						((TankEnemy)object).handleMessage(msg);
				}
				
			}
		
	}
	
	
	
	
	public void processPlayerMissile(NewItem objectStr, NewItem other) {
		
		
			if (other.getType().equals(SpawnType.Enemy_01) ||
				other.getType().equals(SpawnType.Enemy_02) ||	
				other.getType().equals(SpawnType.Enemy_03) ||
				
				other.getType().equals(SpawnType.Item) ||
				
				other.getType().equals(SpawnType.MissileEnemy) ||
				
				other.getType().equals(SpawnType.Wall_City) ||	
				other.getType().equals(SpawnType.Wall_Badlands) || 
				other.getType().equals(SpawnType.Wall_Desert) ||
				other.getType().equals(SpawnType.Wall_Fabric) ||
				other.getType().equals(SpawnType.Wall_Jungle) ||
				other.getType().equals(SpawnType.Wall_Volcano) ||
				other.getType().equals(SpawnType.Wall_Winter) ||
				
				other.getType().equals(SpawnType.Forest_Volcano) ||
				other.getType().equals(SpawnType.Forest_Winter) ||
				other.getType().equals(SpawnType.Forest_Space) ||
				
				other.getType().equals(SpawnType.Forest_Desert) ||
				other.getType().equals(SpawnType.Forest_Fabric) ||
				other.getType().equals(SpawnType.Forest_Jungle) ||
				other.getType().equals(SpawnType.Forest_Badlands) ||
				other.getType().equals(SpawnType.Forest_City) ||
				
				
				other.getType().equals(SpawnType.Border)) {
				
					SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(objectStr.getIdCode());
					if (object != null) {
						if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
							gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
						}
					}
					
					Missile miss = (Missile)object;
					
				
					if (other.getType().equals(SpawnType.Enemy_01) ||
						other.getType().equals(SpawnType.Enemy_02) ||
						other.getType().equals(SpawnType.Enemy_03) ||
						other.getType().equals(SpawnType.MissileEnemy)) {
						
						object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
						if (object != null) {
							if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
								gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
							}
							
							if (!other.getType().equals(SpawnType.MissileEnemy)) {
								
								
								if (other.getType().equals(SpawnType.Enemy_01)) {
									
									SimpleEnemy dron = (SimpleEnemy)object;
									other.setX(dron.getX());
									other.setY(dron.getY());
									other.setWidth(dron.getWidth());
									other.setHeight(dron.getHColl());
									
									if (miss.getSubType().equals(SpawnType.Missile_Flame)) {createFlames(other);}
									
									createExplosionDynamic(other);
									
								}else if (other.getType().equals(SpawnType.Enemy_02)) {
									
									TankEnemy tank = (TankEnemy)object;
									
									other.setX(tank.getX());
									other.setY(tank.getY());
									other.setWidth(tank.getWidth());
									other.setHeight(tank.getHColl());
									
									if (miss.getSubType().equals(SpawnType.Missile_Flame)) {createFlames(other);}
									
									createExplosionDynamic(other);
									
								}else if (other.getType().equals(SpawnType.Enemy_03)) {
									
									WatchTowerEnemy watch = (WatchTowerEnemy)object;
									
									other.setX(watch.getX());
									other.setY(watch.getY());
									other.setWidth(watch.getWidth());
									other.setHeight(watch.getHColl());
									
									if (miss.getSubType().equals(SpawnType.Missile_Flame)) {createFlames(other);}
									
									createExplosionDynamic(other);
									
								}
								
			
							}
							
						}
					}
					
					if (other.getType().equals(SpawnType.Item)) {
						
						object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
						if (object != null) {
							Item item = (Item)object;
							if (item.getSubType().equals(SpawnType.Item_Mine)) {
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
								}
								
								other.setX(item.getX());
								other.setY(item.getY());
								other.setWidth(item.getWidth());
								other.setHeight(item.getHColl());
							
								if (miss.getSubType().equals(SpawnType.Missile_Flame)) {createFlames(other);}
								
								long numEnemies = GameLogicInformation.getEnemiesLeft();
								if (numEnemies > 0) {GameLogicInformation.setEnemiesLeft(numEnemies-1);}
								
								createExplosionDynamic(other);
							}
						}
						
					} 
					
					if (other.getType().equals(SpawnType.Border)) {
						
						objectStr.setX(miss.getX());
						objectStr.setY(miss.getY());
						
						createExplosionDynamic(objectStr);
					}
					
					
					if (other.getType().equals(SpawnType.Wall_City) ||
					other.getType().equals(SpawnType.Wall_Badlands) || 
					other.getType().equals(SpawnType.Wall_Desert) ||
					other.getType().equals(SpawnType.Wall_Fabric) ||
					other.getType().equals(SpawnType.Wall_Jungle) ||
					other.getType().equals(SpawnType.Wall_Winter)
					) {
						Cell cell = walls.getCell(other.getIndex_X(), other.getIndex_Y());
						if (cell != null) {
							StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
							if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().add(tile);
							}
							
							createExplosionStatic(other);
							createBonus(other);
						}
					}
				
					if (other.getType().equals(SpawnType.Forest_Volcano) ||
					other.getType().equals(SpawnType.Forest_Winter) ||
					other.getType().equals(SpawnType.Forest_Space)) {
						
					Cell cell = forests.getCell(other.getIndex_X(), other.getIndex_Y());
					
					if (cell != null) {
						
						if (other.getType().equals(SpawnType.Forest_Space)) {
							
							if (cell.getTile() instanceof AnimatedTiledMapTile) {
								AnimatedTiledMapTile tile = (AnimatedTiledMapTile)cell.getTile();						
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().add(tile);
								}
							}else {
								StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
								}
							}
						
						}else {
							StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
							if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
								gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
							}
						}
						
						createExplosionStatic(other);
						createBonus(other);
						
					}
				}
				
				if 	(other.getType().equals(SpawnType.Forest_Desert) ||
				other.getType().equals(SpawnType.Forest_Fabric) ||
				other.getType().equals(SpawnType.Forest_Jungle) ||
				other.getType().equals(SpawnType.Forest_Badlands) ||
				other.getType().equals(SpawnType.Forest_City)) {
					
					Cell cell = forests.getCell(other.getIndex_X(), other.getIndex_Y());
					
					if (cell != null) {
						
						StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
						if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
							gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
						}
						
						createExplosionStatic(other);
					}
					
				}
					
					
			}
	}
	
	
	
	public void processEnemyMissile(NewItem objectStr, NewItem other) {
	
			
			if (other.getType().equals(SpawnType.Player_01) ||
				other.getType().equals(SpawnType.MissilePlayer) ||
				other.getType().equals(SpawnType.Wall_City) ||	
				other.getType().equals(SpawnType.Wall_Badlands) || 
				other.getType().equals(SpawnType.Wall_Desert) ||
				other.getType().equals(SpawnType.Wall_Fabric) ||
				other.getType().equals(SpawnType.Wall_Jungle) ||
				other.getType().equals(SpawnType.Wall_Volcano) ||
				other.getType().equals(SpawnType.Wall_Winter) ||
				other.getType().equals(SpawnType.Forest_Volcano) ||
				other.getType().equals(SpawnType.Forest_Winter) ||
				other.getType().equals(SpawnType.Forest_Space) ||
				other.getType().equals(SpawnType.Border)) {
			
				SpawnObject object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(objectStr.getIdCode());
				if (object != null) {
					if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
						gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
					}
				}
				
				if (other.getType().equals(SpawnType.MissilePlayer)) {
					object = gPS.getGamePlay().getGameLogic().getSpawnPool().getDynamicElementtWithCollisionById(other.getIdCode());
					if (object != null) {
						if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().contains(object)) {
							gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedBodiesWithCollision().add(object);
						}
					}
				}
				
				if (other.getType().equals(SpawnType.Player_01)) {
					gPS.getGamePlay().processPlayerVariables();
					gPS.getGamePlay().getGameLogic().crashSoundPlay();
				}
				
				
				if (other.getType().equals(SpawnType.Wall_City) ||
					other.getType().equals(SpawnType.Wall_Badlands) || 
					other.getType().equals(SpawnType.Wall_Desert) ||
					other.getType().equals(SpawnType.Wall_Fabric) ||
					other.getType().equals(SpawnType.Wall_Jungle) ||
					other.getType().equals(SpawnType.Wall_Winter)
					) {
					
					Cell cell = walls.getCell(other.getIndex_X(), other.getIndex_Y());
					if (cell != null) {
						StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
						if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().contains(tile)) {
							gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedWallsWithCollision().add(tile);
						}
						
						createExplosionStatic(other);
						createBonus(other);
					}
				}
				
				if (other.getType().equals(SpawnType.Border)) {
					
					Missile miss = (Missile)object;
					objectStr.setX(miss.getX());
					objectStr.setY(miss.getY());
					
					createExplosionDynamic(objectStr);
				}
				
				if (other.getType().equals(SpawnType.Forest_Volcano) ||
				    other.getType().equals(SpawnType.Forest_Winter) ||
				    other.getType().equals(SpawnType.Forest_Space)) {	
					
					Cell cell = forests.getCell(other.getIndex_X(), other.getIndex_Y());
					
					if (cell != null) {
						
						if (other.getType().equals(SpawnType.Forest_Space)) {
							
							if (cell.getTile() instanceof AnimatedTiledMapTile) {
								AnimatedTiledMapTile tile = (AnimatedTiledMapTile)cell.getTile();						
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedAnimForestWithCollision().add(tile);
								}
							}else {
								StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
								if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
									gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
								}
							}
							
						}else {
							StaticTiledMapColl tile = (StaticTiledMapColl)cell.getTile();
							if (!gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().contains(tile)) {
								gPS.getGamePlay().getGameLogic().getSpawnPool().getDeletedForestsWithCollision().add(tile);
							}
						}
						
						createExplosionStatic(other);
						createBonus(other);
						
					}
				}
			}
	}
	
	
	

	@Override
	public void endContact(Contact contact) {
			
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	

}
