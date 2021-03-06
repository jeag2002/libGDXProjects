package com.gdx.game;

//https://www.gamefromscratch.com/post/2014/09/25/LibGDX-LibGDX-Tutorial-13-Physics-with-Box2D-Part-3-Collisions.aspx
//https://www.gamedevelopment.blog/full-libgdx-game-tutorial-input-controller/
//https://docs.google.com/viewer?a=v&pid=forums&srcid=MTQxNTM5ODIzOTU2OTk5ODI1NDkBMDU4NjUzOTc3NDYwMTU2ODk0MzABUEdMdDlUdTlXM0FKATAuMQEBdjI&authuser=0


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.gdx.game.resources.Resources;
import com.gdx.game.screens.GamePlayScreen;
import com.gdx.game.screens.LoadingScreen;

public class FirstTestGDX extends Game{
	
	//Screen settings 
	public static int screenWidth = 640;
	public static int screenHeight = 1000;
	
	public static float ratioX = 1.0f;
	public static float ratioY = 1.0f;
	
	public static float ASPECT_RATIO = (float)screenWidth/(float)screenHeight;
	
	//Resources
	public static Resources resources;
	
	//Screens
	public LoadingScreen loadingScreen;
	public GamePlayScreen gamePlayScreen;
	

	@Override
	public void create() {
		resources = new Resources();
		
		loadingScreen = new LoadingScreen(this);
		gamePlayScreen = new GamePlayScreen(this);
		
		resources.loadAssets();		
		
		setScreen(loadingScreen);
	}
	
	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		resources.dispose();
	}
	
	
	public static void initGraphicRatio() {
		ratioX = (float)FirstTestGDX.screenWidth /  (float)Gdx.graphics.getWidth();
		ratioY = (float)FirstTestGDX.screenHeight / (float)Gdx.graphics.getHeight();
	}
		
}

