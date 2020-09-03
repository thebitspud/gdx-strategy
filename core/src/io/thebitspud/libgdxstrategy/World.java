package io.thebitspud.libgdxstrategy;

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
import io.thebitspud.libgdxstrategy.map.Tile;
import io.thebitspud.libgdxstrategy.units.*;

import java.util.ArrayList;

public class World {
	private final StrategyGame app;
	public TiledMap map;
	private TiledMapRenderer mapRenderer;
	public OrthographicCamera mapCamera;
	private ArrayList<Unit> units;

	public int width, height, tileSize, gameTurn, maxTurns;

	public World(StrategyGame app) {
		this.app = app;

		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		units = new ArrayList<>();
	}

	public void loadMap(String fileName) {
		map = app.assets.get(fileName, TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		tileSize = map.getProperties().get("tilewidth", Integer.class);
		width = map.getProperties().get("width", Integer.class);
		height = map.getProperties().get("height", Integer.class);

		gameTurn = 0;
		maxTurns = -1;
		initUnits();
	}

	public void initUnits() {
		units.clear();

		spawnUnit(7, 2, Unit.ID.HEAVY, true);
		spawnUnit(4, 3, Unit.ID.RANGED, true);
		spawnUnit(6, 5, Unit.ID.BASIC, true);
		spawnUnit(5, 7, Unit.ID.RANGED, true);
		spawnUnit(7, 8, Unit.ID.HEAVY, true);
		spawnUnit(8, 10, Unit.ID.BASIC, true);
		spawnUnit(5, 11, Unit.ID.MAGIC, true);
		spawnUnit(7, 13, Unit.ID.RANGED, true);
		spawnUnit(10, 14, Unit.ID.BASIC, true);

		spawnUnit(13, 2, Unit.ID.BASIC, false);
		spawnUnit(17, 3, Unit.ID.RANGED, false);
		spawnUnit(14, 5, Unit.ID.BASIC, false);
		spawnUnit(18, 5, Unit.ID.MAGIC, false);
		spawnUnit(17, 8, Unit.ID.RANGED, false);
		spawnUnit(15, 7, Unit.ID.HEAVY, false);
		spawnUnit(16, 11, Unit.ID.BASIC, false);
		spawnUnit(19, 12, Unit.ID.RANGED, false);
		spawnUnit(16, 14, Unit.ID.HEAVY, false);

		nextTurn();
	}

	public void tick() {
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			if (unit.isDead()) units.remove(unit);
		}

		Vector3 pos = mapCamera.position;

		float xHalf = Gdx.graphics.getWidth() * mapCamera.zoom / 2f;
		float yHalf = Gdx.graphics.getHeight() * mapCamera.zoom / 2f;
		float xLim = width * tileSize - xHalf;
		float yLim = height * tileSize - yHalf;

		pos.x = MathUtils.clamp(pos.x, xHalf, xLim);
		pos.y = MathUtils.clamp(pos.y, yHalf, yLim);

		mapCamera.update();
	}

	public void render() {
		mapRenderer.setView(mapCamera);
		mapRenderer.render();

		app.batch.begin();

		for(Unit unit: units) unit.updateScreenPosition();
	}

	public void clampMap() {
		float maxZoom = Math.min((float) width * tileSize / Gdx.graphics.getWidth(),
				(float) height * tileSize / Gdx.graphics.getHeight());
		mapCamera.zoom = (float) MathUtils.clamp(mapCamera.zoom, 0.5, maxZoom);
	}

	public void spawnUnit(int x, int y, Unit.ID id, boolean ally) {
		if(getUnit(x, y) != null) return;

		switch (id) {
			case BASIC:
				units.add(new BasicUnit(x, y, ally, app));
				break;
			case RANGED:
				units.add(new RangedUnit(x, y, ally, app));
				break;
			case MAGIC:
				units.add(new MagicUnit(x, y, ally, app));
				break;
			case HEAVY:
				units.add(new HeavyUnit(x, y, ally, app));
				break;
		}
	}

	public void nextTurn() {
		for(Unit unit: units) unit.nextTurn();
		gameTurn += 1;
		app.gameScreen.mapInput.selectedUnit = null;
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
		for(Unit unit: units)
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