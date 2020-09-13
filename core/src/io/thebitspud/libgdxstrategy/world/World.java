package io.thebitspud.libgdxstrategy.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.*;

import java.util.ArrayList;

public class World {
	private final StrategyGame app;
	public TiledMap map;
	private TiledMapRenderer mapRenderer;
	public OrthographicCamera mapCamera;
	public MapInput mapInput;
	private final ArrayList<Player> players;
	public User user;

	public int width, height, tileSize, gameTurn, maxTurns, lastPlayer;
	public boolean touchingMapEdgeX, touchingMapEdgeY;

	public World(StrategyGame app) {
		this.app = app;

		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		players = new ArrayList<>();
		mapInput = new MapInput(app, this);
	}

	public void init(String fileName) {
		map = app.assets.get(fileName, TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		tileSize = map.getProperties().get("tilewidth", Integer.class);
		width = map.getProperties().get("width", Integer.class);
		height = map.getProperties().get("height", Integer.class);

		mapCamera.zoom = 1;
		mapCamera.position.x = 0;
		mapCamera.position.y = width * tileSize;

		gameTurn = 0;
		maxTurns = -1;
		lastPlayer = -1;
		app.gameScreen.chosenUnit = null;

		players.clear();
		players.add(user = new User(-20, app));
		players.add(new EnemyAI(40, app));

		nextPlayer();
		nextTurn();
	}

	public void tick(float delta) {
		for (Player player: players) player.updateUnits();

		mapInput.tick(delta);
		clampMap();
		mapCamera.update();
	}

	public void render() {
		mapRenderer.setView(mapCamera);
		mapRenderer.render();

		app.batch.begin();

		for (Player player: players) player.render();
		mapInput.render();
	}

	public void endGame(boolean victory) {
		app.setScreen(victory ? app.winScreen : app.lossScreen);
	}

	public void clampMap() {
		Vector3 pos = mapCamera.position;

		float widthMax = (float) width * tileSize / (Gdx.graphics.getWidth() - 143);
		float heightMax = (float) height * tileSize / Gdx.graphics.getHeight();
		float maxZoom = Math.min(widthMax, heightMax);
		mapCamera.zoom = (float) MathUtils.clamp(mapCamera.zoom, 0.5, maxZoom);

		float xHalf = Gdx.graphics.getWidth() * mapCamera.zoom / 2f;
		float yHalf = Gdx.graphics.getHeight() * mapCamera.zoom / 2f;
		float xLim = width * tileSize - xHalf + (143 * mapCamera.zoom);
		float yLim = height * tileSize - yHalf;

		touchingMapEdgeX = pos.x <= xHalf || pos.x > xLim;
		touchingMapEdgeY = pos.y <= yHalf || pos.y > yLim;

		pos.x = MathUtils.clamp(pos.x, xHalf, xLim);
		pos.y = MathUtils.clamp(pos.y, yHalf, yLim);
	}

	public void nextPlayer() {
		lastPlayer++;
		if(lastPlayer >= players.size()) {
			lastPlayer = 0;
			nextTurn();
		}

		players.get(lastPlayer).playTurn();
	}

	public void nextTurn() {
		gameTurn += 1;
		mapInput.selectedUnit = null;

		for (Player p: players) p.adjustGold(20);
	}

	public void updateTurnInfo() {
		String goldText = user.getCurrentGold() + " Gold";
		String turnText = "\nTurn " + gameTurn + ((maxTurns > 0) ? "/" + maxTurns : "");
		app.gameScreen.turnInfo.setText(goldText + turnText);
		app.gameScreen.updateButtonStates();
	}

	public int getTileID(int x, int y) {
		int adjustedY = height - y - 1;
		x = MathUtils.clamp(x, 0, width - 1);
		adjustedY = MathUtils.clamp(adjustedY, 0, height - 1);

		TiledMapTile tile = ((TiledMapTileLayer) map.getLayers().get(0)).getCell(x, adjustedY).getTile();
		if(tile != null) return tile.getId();

		return 0;
	}

	public Tile getTile(int x, int y) {
		for(Tile tile : Tile.values())
			if(tile.getID() == getTileID(x, y))
				return tile;

		return Tile.VOID;
	}

	public Unit getUnit(int x, int y) {
		for (Player player: players)
			for(Unit unit: player.units)
				if(unit.getCell().x == x && unit.getCell().y == y)
					return unit;

		return null;
	}

	public Vector2 getTileOffset(int x, int y) {
		int adjustedY = height - y - 1;
		Vector3 screenPos = new Vector3(x * tileSize, adjustedY * tileSize, 0);
		screenPos = mapCamera.project(screenPos);
		return new Vector2(screenPos.x, screenPos.y);
	}

	public void dispose() {
		map.dispose();
	}
}