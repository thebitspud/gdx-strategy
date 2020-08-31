package io.thebitspud.libgdxstrategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class World {
	private final StrategyGame app;
	private TiledMap map;
	private TiledMapRenderer mapRenderer;
	public OrthographicCamera mapCamera;

	private int worldWidth, worldHeight, tileSize;

	public World(StrategyGame app) {
		this.app = app;

		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		loadMap("testlevel.tmx");
	}

	public void loadMap(String fileName) {
		map = app.assets.get(fileName, TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		tileSize = map.getProperties().get("tilewidth", Integer.class);
		worldWidth = map.getProperties().get("width", Integer.class);
		worldHeight = map.getProperties().get("height", Integer.class);

		mapCamera.position.x = 0.5f * tileSize * worldWidth;
		mapCamera.position.y = 0.5f * tileSize * worldHeight;
	}

	public void tick(float delta) {
		Vector3 pos = mapCamera.position;

		final float maxZoom = Math.min((float) worldWidth * tileSize / Gdx.graphics.getWidth(),
				(float) worldHeight * tileSize / Gdx.graphics.getHeight());
		mapCamera.zoom = (float) MathUtils.clamp(mapCamera.zoom, 0.5, maxZoom);

		final int xHalf = (int) (Gdx.graphics.getWidth() * mapCamera.zoom * 0.5f),
				yHalf = (int) (Gdx.graphics.getHeight() * mapCamera.zoom * 0.5f),
				xLim = worldWidth * tileSize - xHalf,
				yLim = worldHeight * tileSize - yHalf;

		pos.x = MathUtils.clamp(pos.x, xHalf, xLim);
		pos.y = MathUtils.clamp(pos.y, yHalf, yLim);

		mapCamera.update();
	}

	public void render() {
		mapRenderer.setView(mapCamera);
		mapRenderer.render();
	}

	public void dispose() {
		map.dispose();
	}
}