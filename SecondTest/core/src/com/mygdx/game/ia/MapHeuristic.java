package com.mygdx.game.ia;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.NewItem;

public class MapHeuristic implements Heuristic<NewItem> {

	@Override
	public float estimate(NewItem node, NewItem endNode) {
		return Vector2.dst(node.getX(), node.getY(), endNode.getX(), endNode.getY());
	}

	
}
