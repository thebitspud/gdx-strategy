package io.thebitspud.libgdxstrategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import io.thebitspud.libgdxstrategy.map.Tile;

public class World {
	private final StrategyGame app;
	public TiledMap map;
	private TiledMapRenderer mapRenderer;
	public OrthographicCamera mapCamera;

	public int width, height, tileSize;

	public World(StrategyGame app) {
		this.app = app;

		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void loadMap(String fileName) {
		map = app.assets.get(fileName, TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		tileSize = map.getProperties().get("tilewidth", Integer.class);
		width = map.getProperties().get("width", Integer.class);
		height = map.getProperties().get("height", Integer.class);
	}

	public void tick() {
		Vector3 pos = mapCamera.position;

		float maxZoom = Math.min((float) width * tileSize / Gdx.graphics.getWidth(),
				(float) height * tileSize / Gdx.graphics.getHeight());
		mapCamera.zoom = (float) MathUtils.clamp(mapCamera.zoom, 0.5, maxZoom);

		float xHalf = Gdx.graphics.getWidth() * mapCamera.zoom / 2f;
		float yHalf = Gdx.graphics.getHeight() * mapCamera.zoom / 2f;
		float xLim = width * tileSize - xHalf;
		float yLim = height * tileSize - yHalf;

		pos.x = MathUtils.clamp(pos.x, xHalf, xLim);
		pos.y = MathUtils.clamp(pos.y, yHalf, yLim);

		mapCamera.update();
	}

	public int getTileID(int x, int y) {
		int adjY = height - y - 1;
		if (adjY < 0) adjY = 0;
		TiledMapTile tile = ((TiledMapTileLayer) map.getLayers().get(0)).getCell(x, adjY).getTile();
		if(tile != null) return tile.getId();

		return 0;
	}

	public Tile getTile(int x, int y) {
		for(Tile tile : Tile.values())
			if(tile.getID() == getTileID(x, y))
				return tile;

		return Tile.VOID;
	}

	public void render() {
		mapRenderer.setView(mapCamera);
		mapRenderer.render();
	}

	public void dispose() {
		map.dispose();
	}
}