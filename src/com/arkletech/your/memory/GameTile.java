package com.arkletech.your.memory;

import android.util.Log;

public class GameTile {
	static final String TAG="MyAndroidDebugTAG";

	public static final int TILE_OPEN=1;
	public static final int TILE_CLOSE=2;
	public static final int TILE_EMPTY=3;
	
	public boolean showTile=false;
	public boolean drawTile=true;
	public int tileIndex;
	public int flipTimes;
	public String imgName;
	
	GameTile() {
		Log.d(TAG, "Tile constructor()");
		showTile=false;
		drawTile=true;
		tileIndex=0;
		flipTimes=0;
		imgName=null;
	}
	
}
