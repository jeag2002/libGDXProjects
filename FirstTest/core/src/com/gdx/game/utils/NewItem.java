package com.gdx.game.utils;

import com.gdx.game.stages.enums.SpawnType;

public class NewItem {
	
	private SpawnType type;
	private float X;
	private float Y;
	
	private float width;
	private float height;
	
	private float angle;
	private float speed;
	
	
	public NewItem() {
		this.type = SpawnType.Item;
		this.X = 0.0f;
		this.Y = 0.0f;
		this.width = 0.0f;
		this.height = 0.0f;
		this.angle = 0.0f;
		this.speed = 0.0f;
	}

	public NewItem(SpawnType type, float x, float y) {
		super();
		this.type = type;
		X = x;
		Y = y;
		width = 0.0f;
		height = 0.0f;
		angle = 0.0f;
		speed = 0.0f;
	}
	
	
	public NewItem(SpawnType type, float x, float y, float width, float height, float angle, float speed) {
		
		super();
		this.type = type;
		this.X = x;
		this.Y = y;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.speed = speed;
	}
	
	
	public SpawnType getType() {
		return type;
	}

	public void setType(SpawnType type) {
		this.type = type;
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}
	
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	

}
