package io.thebitspud.libgdxstrategy.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AssetLibrary extends AssetManager {
	public TextureRegion pixel;
	public TextureRegion[] highlights;
	public TextureRegion[][] units;
	public TextureRegionDrawable[][] buttons;

	public Label.LabelStyle titleStyle, subTitleStyle, largeTextStyle, smallTextStyle;

	public AssetLibrary() {
		buttons = new TextureRegionDrawable[16][3];
		highlights = new TextureRegion[8];
		units = new TextureRegion[4][2];
	}

	public void loadAll() {
		loadFiles();
		generateFonts();

		finishLoading();
		assignTextures();
		assignAudio();
	}

	private void loadFiles() {
		this.load("buttons.png", Texture.class);
		this.load("highlights.png", Texture.class);
		this.load("pixel.png", Texture.class);
		this.load("tiles.png", Texture.class);
		this.load("units.png", Texture.class);

		setLoader(TiledMap.class, new TmxMapLoader());
		load("testlevel.tmx", TiledMap.class);
	}

	private void assignTextures() {
		final Texture unitSheet = this.get("units.png", Texture.class);
		final Texture buttonSheet = this.get("buttons.png", Texture.class);
		final Texture highlightSheet = this.get("highlights.png", Texture.class);
		pixel = new TextureRegion(this.get("pixel.png", Texture.class));

		for (int i = 0; i < 4; i++) {
			units[i][0] = new TextureRegion(unitSheet, i * 64, 0, 64, 64);
			units[i][1] = new TextureRegion(unitSheet, i * 64, 64, 64, 64);
		}

		for (int i = 0; i < 15; i++) {
			if (i < 6) buttons[i] = getButton(buttonSheet, 0, i * 90, 400);
			else buttons[i] = getButton(buttonSheet, 1200, (i - 6) * 90, 90);
		}

		buttons[15] = getButton(buttonSheet, 0, 710, 90);

		for ( int i = 0; i < 8; i++)
			highlights[i] = new TextureRegion(highlightSheet, i * 66, 0, 64, 64);
	}

	private TextureRegionDrawable[] getButton(Texture sheet, int x, int y, int width) {
		final TextureRegion iconUp = new TextureRegion(sheet, x, y, width, 90);
		final TextureRegion iconHover = new TextureRegion(sheet, x + width, y, width, 90);
		final TextureRegion iconDown = new TextureRegion(sheet, x + width * 2, y, width, 90);

		TextureRegionDrawable[] button = new TextureRegionDrawable[3];

		button[0] = new TextureRegionDrawable(iconUp);
		button[1] = new TextureRegionDrawable(iconHover);
		button[2] = new TextureRegionDrawable(iconDown);

		return button;
	}

	public ImageButton.ImageButtonStyle getButtonStyle(TextureRegionDrawable[] button) {
		ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();

		style.imageUp = button[0];
		style.imageOver = button[1];
		style.imageDown = button[2];

		return style;
	}

	private void assignAudio() {}

	private void generateFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.incremental = true;

		parameter.size = 96;
		titleStyle = new Label.LabelStyle(generator.generateFont(parameter), Color.WHITE);

		parameter.size = 48;
		subTitleStyle = new Label.LabelStyle(generator.generateFont(parameter), Color.WHITE);

		parameter.size = 24;
		largeTextStyle = new Label.LabelStyle(generator.generateFont(parameter), Color.WHITE);

		parameter.size = 14;
		smallTextStyle = new Label.LabelStyle(generator.generateFont(parameter), Color.WHITE);

		generator.dispose();
	}
}