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
import io.thebitspud.libgdxstrategy.units.*;

import java.util.ArrayList;

public class World {
	private final StrategyGame app;
	public TiledMap map;
	private TiledMapRenderer mapRenderer;
	public OrthographicCamera mapCamera;
	public MapInput mapInput;
	private final ArrayList<Player> players;

	public int width, height, tileSize, gameTurn, maxTurns, lastPlayer;

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

		gameTurn = 0;
		maxTurns = -1;
		lastPlayer = -1;

		players.clear();
		players.add(new User(100, app));
		players.add(new EnemyAI(100, app));
		nextPlayer();
	}

	public void tick(float delta) {
		for (Player player: players) player.updateUnits();

		Vector3 pos = mapCamera.position;

		float xHalf = Gdx.graphics.getWidth() * mapCamera.zoom / 2f;
		float yHalf = Gdx.graphics.getHeight() * mapCamera.zoom / 2f;
		float xLim = width * tileSize - xHalf;
		float yLim = height * tileSize - yHalf;

		pos.x = MathUtils.clamp(pos.x, xHalf, xLim);
		pos.y = MathUtils.clamp(pos.y, yHalf, yLim);

		mapCamera.update();
		mapInput.tick(delta);
	}

	public void render() {
		mapRenderer.setView(mapCamera);
		mapRenderer.render();

		app.batch.begin();
		for (Player player: players) player.render();
		mapInput.render();
		app.batch.end();
	}

	public void endGame(boolean victory) {
		app.setScreen(victory ? app.winScreen : app.lossScreen);
	}

	public void clampMap() {
		float maxZoom = Math.min((float) width * tileSize / Gdx.graphics.getWidth(),
				(float) height * tileSize / Gdx.graphics.getHeight());
		mapCamera.zoom = (float) MathUtils.clamp(mapCamera.zoom, 0.5, maxZoom);
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
		String turnText = "Turn " + gameTurn + ((maxTurns > 0) ? "/" + maxTurns : "");
		app.gameScreen.turnInfo.setText(turnText);
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
				if(unit.getTileX() == x && unit.getTileY() == y)
					return unit;

		return null;
	}

	public Vector2 getTileOffset(int x, int y) {
		int adjustedY = (height - y - 1);

		float cameraOffsetX = x * tileSize - mapCamera.position.x;
		float cameraOffsetY = adjustedY * tileSize - mapCamera.position.y;

		float xOffset = cameraOffsetX / mapCamera.zoom + Gdx.graphics.getWidth() / 2f;
		float yOffset = cameraOffsetY / mapCamera.zoom + Gdx.graphics.getHeight() / 2f;

		return new Vector2(xOffset, yOffset);
	}

	public void dispose() {
		map.dispose();
	}
}