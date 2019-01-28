package com.arkletech.your.memory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class YourMemoryActivity extends Activity implements OnItemClickListener, OnClickListener, OnTickListener {
    /** Called when the activity is first created. */
	static final String TAG="MyAndroidDebugTAG";
	static final int BOARD_SIZE = 4 * 5;
	static final int GAME_END=0;
	static final int GAME_PLAY=1;
	static final int GAME_PAUSE=2;
	GameTile tiles []=null;
	String piece []=null;
	String bkImage []=null;
	Random bkImageRandom;
	int bkImgIndex;
	int boardWidth, boardHeight;
	int tileWidth, tileHeight;	//, tileSize;
	int gameState=GAME_END;
	int shownIndex=-1;
	String lastScore="0", lastTimer="00:00:00";
//	TextView tick;
	Timer timer=null, delayTimer;
    GridView board;
    ImageAdapter imgAdapter;
    int delayIndex[]={-1, -1};
    boolean removeTiles=false;
    int tilesCleared=0;
    SharedPreferences appPrefs;
    String assetPath=null;
    
    public static int gameType=-1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        bkImageRandom = new Random();
        
        Log.d(TAG, "This is the display dimension (in pixel):"+dm.widthPixels+", "+dm.heightPixels);

        board = (GridView)findViewById(R.id.gridViewBoard);

//        ViewTreeObserver vto = board.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//			
//			@Override
//			public void onGlobalLayout() {
//				// TODO Auto-generated method stub
//                boardHeight = board.getMeasuredHeight();
//                boardWidth = board.getMeasuredWidth();
//                int screenOrientation = getResources().getConfiguration().orientation;
//                Log.d(TAG, "ViewTreeObserver.OnGlobalLayoutListener() - Get the board demension: ("+boardWidth+","+boardHeight+"), Orientation:"+screenOrientation);
//				
//                if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
//                	tileWidth = boardWidth >> 2;	// if the board is 4x5
//        			tileHeight = boardHeight / 5;
//                }
//                else {
//                	tileWidth = boardWidth / 5;	// landscape mode - 5x4
//                	tileHeight = boardHeight >> 2;
//                }
//                Log.d(TAG, "ViewTreeObserver.OnGlobalLayoutListener() - tile demension: ("+tileWidth+","+tileHeight+")");
//			}
//		});
        
//        Display display = getWindowManager().getDefaultDisplay(); 
//        int width = display.getWidth();
//        int height = display.getHeight();
        int screenOrientation = getResources().getConfiguration().orientation;
//
//        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
//        	tileSize = width >> 2;	// if the board is 4x5
//        }
//        else
//        	tileSize = (width * 2 / 3) / 5;	// landscape mode - 5x4
//        Log.d(TAG, "YourMemoryActivity - onCreat(): DisplayWidth="+width+", DisplayHeight="+height+", with screenOrientation="+screenOrientation+", tileSize="+tileSize);
        
        // Get App Preference settings
        appPrefs = getSharedPreferences(SettingsActivity.PREF_NAME, MODE_PRIVATE);
        
        tiles = new GameTile [BOARD_SIZE];
        for (int i=0; i<BOARD_SIZE; i++) {
        	tiles[i] = new GameTile();
        }
        
        AssetManager mgr = getAssets();
        try {
			bkImage = mgr.list("pictures");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // setup the adapter for the GridView
        imgAdapter = new ImageAdapter(this);
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
        	board.setNumColumns(5);
        }
//        board.setLayoutParams(new GridView.LayoutParams(width, width));
        board.setAdapter(imgAdapter);
        board.setOnItemClickListener(this);

//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                boardHeight = board.getMeasuredHeight();
//                boardWidth = board.getMeasuredWidth();
//                int screenOrientation = getResources().getConfiguration().orientation;
//                Log.d(TAG, "ViewTreeObserver.OnPreDrawListener() - Get the board demension: ("+boardWidth+","+boardHeight+"), Orientation:"+screenOrientation);
//
//                if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
//                	tileWidth = boardWidth >> 2;	// if the board is 4x5
//        			tileHeight = boardHeight / 5;
//                }
//                else {
//                	tileWidth = boardWidth / 5;	// landscape mode - 5x4
//                	tileHeight = boardHeight >> 2;
//                }
//                Log.d(TAG, "ViewTreeObserver.OnPreDrawListener() - tile demension: ("+tileWidth+","+tileHeight+")");
//                return true;
//            }
//        });
        
		setBackgroundImage(true);
        newGame();

    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "YourMemoryActivity - onStart(): gameType="+gameType+",gameState="+gameState);
		// Restore the settings from Preferences
		if (gameType == -1 && gameState != GAME_END) {
			gameState = GAME_END;
			if (timer != null) {
				timer.stop();
				timer.reset();
			}
			setBackgroundImage(true);
			newGame();
			imgAdapter.notifyDataSetChanged();
		}
		else {
			setBackgroundImage(false);
		}
        
        // Get the Timer
		if (timer == null) {
	        timer = new Timer(this);
	        timer.setOntickListener(this);
		}
//        tick = (TextView)findViewById(R.id.textViewTimer);
		TextView scoreView = (TextView)findViewById(R.id.textViewScore);
		scoreView.setText(lastScore);
		if (!timer.isTimerRunning()) {
			TextView tick = (TextView)findViewById(R.id.textViewTimer);
	        tick.setText(lastTimer);
		}
        
        // Set listener for the buttons 
        ((Button)findViewById(R.id.buttonHighScore)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonPause)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonNewGame)).setOnClickListener(this);
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onResume() - YourMemoryActivity");
		super.onResume();
//
//        Display display = getWindowManager().getDefaultDisplay(); 
//        View s = findViewById(R.id.LinearLayoutTop);
//        s.measure(display.getWidth(), display.getHeight());
//        Log.d(TAG, "boardWidth="+board.getMeasuredWidth()+", boardHeight="+board.getMeasuredHeight());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPause() - YourMemoryActivity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStop() - YourMemoryActivity");
		super.onStop();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRetainNonConfigurationInstance()");
		
		return super.onRetainNonConfigurationInstance();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRestoreInstanceState("+savedInstanceState+")");
		super.onRestoreInstanceState(savedInstanceState);
		if (timer == null) {
			timer = new Timer(this);
	        timer.setOntickListener(this);
			timer.setElapsedTime(savedInstanceState.getLong("timer", 0L));
			if (savedInstanceState.getBoolean("timerRunning", false) == true) {
				timer.start();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSaveInstanceState("+outState+")");
		outState.putBoolean("timerRunning", timer.isTimerRunning());
		outState.putLong("timer", timer.getElapsedTime());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        int screenOrientation = newConfig.orientation;
        setContentView(R.layout.main);
        board = (GridView)findViewById(R.id.gridViewBoard);
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
        	board.setNumColumns(5);
//        	tileSize = (height + 60) / 5;
        }
        else {
        	board.setNumColumns(4);
//        	tileSize = width >> 2;
        }
        Log.d(TAG, "YourMemoryActivity - onConfigurationChanged(): DisplayWidth="+width+", DisplayHeight="+height+", with screenOrientation="+screenOrientation);
        imgAdapter = new ImageAdapter(this);
        board.setAdapter(imgAdapter);
        board.setOnItemClickListener(this);
        
//        tick = (TextView)findViewById(R.id.textViewTimer);
        // Set listener for the buttons 
//        ((Button)findViewById(R.id.buttonHighScore)).setOnClickListener(this);
//        ((Button)findViewById(R.id.buttonCancel)).setOnClickListener(this);
//        ((Button)findViewById(R.id.buttonNewGame)).setOnClickListener(this);

        onStart();
        board.invalidateViews();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.item_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		else if (item.getItemId() == R.id.item_help) {
			startActivity(new Intent(this, HelpActivity.class));
			return true;
		}
		else if (item.getItemId() == R.id.item_about) {
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		}
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick("+parent+", "+v+", "+position+", "+id+")");
		
		if (gameState == GAME_END || gameState == GAME_PAUSE) {
			gameState = GAME_PLAY;
			timer.start();
			if (tiles[position].drawTile == true && tiles[position].showTile == false) {
				tiles[position].showTile = true;
				shownIndex = position;
				tiles[position].flipTimes++;
			}

		}
		else if (gameState == GAME_PLAY) {
			if (delayIndex[0] < 0) {
				if (tiles[position].drawTile == true && tiles[position].showTile == false) {
					tiles[position].showTile = true;
					tiles[position].flipTimes++;
					if (shownIndex >= 0) {	// This is to show the 2nd tile to see if it match
						delayTimer = new Timer(this, 900, true);
						delayTimer.start();
						delayIndex[0] = position;
						delayIndex[1] = shownIndex;
						Log.d(TAG, "delayIndex="+delayIndex[0]+","+delayIndex[1]);
						if (tiles[position].tileIndex == tiles[shownIndex].tileIndex) {
							// It's match, so remove the paired tiles		
							removeTiles = true;
							tilesCleared += 2;
						}
						else {
							// No match, so restore both tiles
							removeTiles = false;
						}
						shownIndex = -1;
					}
					else {	// this is to show the 1st tile
						shownIndex = position;
						
					}
				}
			}
		}
		imgAdapter.notifyDataSetChanged();
//		board.invalidateViews();
		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onClick("+v+"):v.getId()="+v.getId());
		if (v.getId() == R.id.buttonNewGame) {
			if (gameState != GAME_END) {
				final boolean wasTicking = timer.stop();
				AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
				alertBox.setTitle("Start New Game");
				alertBox.setMessage("Are you sure you want to abandon this game?");
				alertBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						gameState = GAME_END;
						timer.stop();
						timer.reset();
						setBackgroundImage(true);
						newGame();
						imgAdapter.notifyDataSetChanged();
					}
					
				});
				alertBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (wasTicking == true)
							timer.start();
					}
					
				});
		        // display box
		        alertBox.show();
			}
			else {
				setBackgroundImage(true);
				newGame();
				imgAdapter.notifyDataSetChanged();
				timer.reset();
				
			}
//			board.invalidateViews();
		}
		else if (v.getId() == R.id.buttonHighScore) {
//			timer.stop();
			startActivity(new Intent(this, ScoreActivity.class));
		}
		else if (v.getId() == R.id.buttonPause) {
			Button ps = (Button) findViewById(R.id.buttonPause);
			if (gameState == GAME_PLAY) {
				gameState = GAME_PAUSE;
				timer.stop();
				ps.setText("Resume");
				if (shownIndex >= 0) {
					tiles[shownIndex].drawTile = true;
					tiles[shownIndex].showTile = false;
					shownIndex = -1;
				}
				if (delayIndex[0] >= 0) {
					delayTimer.stop();
					tiles[delayIndex[0]].drawTile = true;
					tiles[delayIndex[0]].showTile = false;
					tiles[delayIndex[1]].drawTile = true;
					tiles[delayIndex[1]].showTile = false;
					removeTiles = false;
					delayIndex[0] = delayIndex[1] = -1;
				}
				imgAdapter.notifyDataSetChanged();
//				board.invalidateViews();
			}
			else if (gameState == GAME_PAUSE) {
				gameState = GAME_PLAY;
				timer.start();
				ps.setText("Pause");
			}
		}
	}
	
	
	public class ImageAdapter extends BaseAdapter {

		Context context;
		
		public ImageAdapter(Context _context)
		{
			context = _context;
		}

	    @Override
		public int getCount() {
			// TODO: Get the dimension from preferences
	    	
			return BOARD_SIZE;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
			// Get the board dimension 
//			Log.d(TAG, "ImageAdapter - getView("+position+", "+convertView+", "+parent+"["+parent.getMeasuredWidth()+","+parent.getMeasuredHeight()+"]): tileWidth="+tileWidth+",tileHeight="+tileHeight);
			boardWidth = parent.getMeasuredWidth();
			boardHeight = parent.getMeasuredHeight();
			
            int screenOrientation = getResources().getConfiguration().orientation;
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            	tileWidth = boardWidth >> 2;	// if the board is 4x5
    			tileHeight = boardHeight / 5;
            }
            else {
            	tileWidth = boardWidth / 5;	// landscape mode - 5x4
            	tileHeight = boardHeight >> 2;
            }
            tileWidth -= 1;
            tileHeight -= 1;
			
	        if ( convertView == null ) {
	            // if it's not recycled, initialize some attributes
	            imageView = new ImageView(context);
	            imageView.setLayoutParams(new GridView.LayoutParams(tileWidth, tileHeight));
	            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	            imageView.setPadding(0, 0, 0, 0);
	        }
	        else {
	        	imageView = (ImageView)convertView;
	            imageView.setLayoutParams(new GridView.LayoutParams(tileWidth, tileHeight));
	        }
	        
//	        Log.d(TAG, "showTile="+tiles[position].showTile+", drawTile="+tiles[position].drawTile);
	        
	        if (tiles[position].showTile == false) {
	        	if (tiles[position].drawTile == true)
	        		imageView.setImageResource(R.drawable.tile_back);
	        	else
	        		imageView.setImageResource(R.drawable.transparent);
	        }
	        else if (tiles[position].drawTile == true) {
	        	try {
					imageView.setImageBitmap(getBitmapFromAsset(assetPath+"/", tiles[position].imgName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	         
	        return imageView;
		}
		
	}


	@Override
	public void tickListener(Timer t, long l) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "tickListener("+t+", "+l+")");
		TextView tick = (TextView)findViewById(R.id.textViewTimer);
		if (t == timer) {
    		long cnt = l;
    		String sec = String.format("%02d", (cnt % 60));
    		cnt /= 60;
    		String min = String.format("%02d", (cnt % 60));
    		cnt /= 60;
    		String hrs = String.format("%02d", (cnt));
            String asText;
            asText = hrs + ":" + min + ":" + sec; 
            lastTimer = asText;
            tick.setText(lastTimer);
//            Log.d(TAG, "tickListener("+l+") - asText="+asText);
		}
		else if (t == delayTimer) {
			Log.d(TAG, "delayIndex="+delayIndex[0]+","+delayIndex[1]+"removeTiles="+removeTiles);
			if (removeTiles == true) {
				tiles[delayIndex[0]].drawTile = false;
				tiles[delayIndex[0]].showTile = false;
				tiles[delayIndex[1]].drawTile = false;
				tiles[delayIndex[1]].showTile = false;
				if (tilesCleared >= BOARD_SIZE) {	// Game finished
					gameState = GAME_END;
					timer.stop();
					long score = calculateScore();
					TextView scoreView = (TextView)findViewById(R.id.textViewScore);
					scoreView.setText(Long.toString(score));
					
			        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

					SharedPreferences pm = getSharedPreferences(ScoreActivity.PREF_GAME_TYPE[gameType-1], MODE_PRIVATE);
					String currentPlayer = pm.getString(getResources().getString(R.string.pref_player_name), "Player1");
			        // set the message to display with a webview in the dialog
			        alertbox.setTitle("Congratulation");
			        String msg = "<html><body><table align='center' style='color:white'><tr><td align='right'>Player:</td><td>"+currentPlayer+"</td></tr><tr><td align='right'>Game:</td><td>"+(gameType==1?"Alphabet Match":"Color Match")+"</td></tr><tr><td align='right'>Score:</td><td>"+score+"</td></tr><tr><td align='right'>Time:</td><td>"+timer.getElapsedTime()+"s</td></tr></table></body></html>";
			        Log.d(TAG, "Dialog Message:"+msg);
			        WebView wv=new WebView(this);
                    wv.setBackgroundColor(0);	// make it transparent
                    wv.loadData(msg, "text/html", "UTF-8");
                    alertbox.setView(wv);

			        // set a positive/yes button and create a listener
			        alertbox.setPositiveButton("New Game", new DialogInterface.OnClickListener() {

			            // do something when the button is clicked
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
			            	Log.d(TAG, "onItemLongClick("+dialog+", "+which+")");
							setBackgroundImage(true);
							newGame();
							imgAdapter.notifyDataSetChanged();
							timer.reset();
						}
			        });

			        // set a negative/no button and create a listener
			        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			            // do something when the button is clicked
			            public void onClick(DialogInterface dialog, int which) {
				        	Log.d(TAG, "onClick("+dialog+", "+which+")");
			            }
			        });

			        // display box
			        alertbox.show();

				}
			}
			else {
				if (delayIndex[0] >= 0) {
					tiles[delayIndex[0]].drawTile = true;
					tiles[delayIndex[0]].showTile = false;
					tiles[delayIndex[1]].drawTile = true;
					tiles[delayIndex[1]].showTile = false;
				}
			}
			removeTiles = false;
			delayIndex[0] = delayIndex[1] = -1;
			imgAdapter.notifyDataSetChanged();
//			board.invalidateViews();
		}
	}
	
	
    private long calculateScore() {
		// TODO Auto-generated method stub
		long t = timer.getElapsedTime();
		int flipCount=0;
		for (int i=0; i<BOARD_SIZE; i++) {
			flipCount += tiles[i].flipTimes;
		}
		long score = (t == 0)? 0L : (1000000 / t) - (flipCount << 2);
		if (score < 0)
			score = 0;
		Log.d(TAG, "calculateScore(): timer="+t+", flipCount="+flipCount+"; score="+score);
		lastScore = Long.toString(score);
		saveScore(score);
		return score;
	}

	private void saveScore(long s) {
		// TODO Auto-generated method stub
		SharedPreferences pm = getSharedPreferences(ScoreActivity.PREF_GAME_TYPE[gameType-1], MODE_PRIVATE);
		SharedPreferences.Editor ed = pm.edit();
		String name, currentPlayer;
		long score;
		
		currentPlayer = pm.getString(getResources().getString(R.string.pref_player_name), "Player1");
		Log.d(TAG, "saveScore("+s+"): for player:"+currentPlayer);
		for (int i=9; i>=0; i--) {
			score = pm.getLong(ScoreActivity.playerScore[i], 1000L);
			if (s >= score) {
				name = pm.getString(ScoreActivity.playerName[i], "Player1");
				if (i < 9) {
					ed.putString(ScoreActivity.playerName[i+1], name);
					ed.putLong(ScoreActivity.playerScore[i+1], score);				
				}
				ed.putString(ScoreActivity.playerName[i], currentPlayer);
				ed.putLong(ScoreActivity.playerScore[i], s);
			}
			else
				break;
		}
		ed.commit();
	}

	private Bitmap getBitmapFromAsset(String path, String strName) throws IOException
    {
        AssetManager assetManager = getAssets();
        Log.d(TAG, "getBitmapFromAsset("+path+", "+strName+")");

        InputStream istr = assetManager.open(path+strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Log.d(TAG, "getBitmapFromAsset("+strName+"):bitmap="+bitmap);

        return bitmap;
    }

	void setBackgroundImage(boolean newImage) {
		String imgPath=null;
		Drawable bk=null;
		if (appPrefs.getBoolean(getResources().getString(R.string.pref_use_custom_picture), true)) {
		imgPath = appPrefs.getString(getResources().getString(R.string.pref_background_image), null);
//      	Uri uri = Uri.parse(imgPath);
	      	if (imgPath != null) {
	      		bk = Drawable.createFromPath(imgPath);
	      	}
	      	Log.d(TAG, "imgPath="+imgPath+", bk="+bk);
		}
		// If we can't get the image from user's picture gallery, then we use the build-in picture
		if (bk == null) {
			InputStream is = null;
			if (newImage) {
				bkImgIndex = bkImageRandom.nextInt(6);
			}
			imgPath = "pictures/"+bkImage[bkImgIndex];
	        try {
				is = getAssets().open(imgPath);
				bk = Drawable.createFromStream(is, bkImage[bkImgIndex]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d(TAG, "setBackgroundImage() - imgPath="+imgPath+", Drawable="+bk+"boardSize:["+boardWidth+","+boardHeight+"]");
      
//      Drawable bk = new BitmapDrawable(img);
//		int width = tileSize << 2;
//		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			bk.setBounds(0, 0, boardWidth, boardHeight);
//		}
//		else {
//			bk.setBounds(0, 0, width, width+tileSize);
//		}
//      Drawable bk = Drawable.createFromPath("pictures/"+bkImage[0]);
//      Log.d(TAG, "bkImage[0]="+bkImage[0]+", Drawable="+bk);
		board.setBackgroundDrawable(bk);
	}

    // Setup board for a new game
    void newGame() {
    	int imgIndex[] = new int[BOARD_SIZE];

    	SharedPreferences sp = getSharedPreferences(SettingsActivity.PREF_NAME, MODE_PRIVATE);
    	gameType = Integer.parseInt(sp.getString(getResources().getString(R.string.pref_game_type), "1"));
    	assetPath=(gameType==1 ? "tiles" : "ctiles");
    	
        AssetManager mgr = getAssets();
        try {
			piece = mgr.list(assetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for (int i=0; i<BOARD_SIZE; i++) {
    		imgIndex[i] = i;
    	}
    	
    	// Randomize the image order
    	Random r = new Random();
    	for (int i=BOARD_SIZE-1; i>=0; i--) {
    		int rdm = r.nextInt(i+1);
    		int temp = imgIndex[rdm];
    		imgIndex[rdm] = imgIndex[i];
    		imgIndex[i] = temp;
    	}
    	Log.d(TAG, "Random imgIndex="+imgIndex[0]+","+imgIndex[1]+","+imgIndex[2]+","+imgIndex[3]+","+imgIndex[4]+","+imgIndex[5]+","+imgIndex[6]+","+imgIndex[7]+","+imgIndex[8]+","+imgIndex[9]+","+imgIndex[10]+","+imgIndex[11]+","+imgIndex[12]+","+imgIndex[13]+","+imgIndex[14]+","+imgIndex[15]+","+imgIndex[16]+","+imgIndex[17]+","+imgIndex[18]+","+imgIndex[19]);
    	
    	if (gameType == 1) {
	    	for (int i=0; i<BOARD_SIZE; i++) {
	    		tiles[i].tileIndex = imgIndex[i] % 10;
	    		tiles[i].drawTile = true;
	    		tiles[i].showTile = false;
	    		tiles[i].flipTimes = 0;
	    		tiles[i].imgName = piece[imgIndex[i]%10];
	    	}
    	}
    	else {
	    	for (int i=0; i<BOARD_SIZE; i++) {
	    		tiles[i].tileIndex = imgIndex[i] % 10;
	    		tiles[i].drawTile = true;
	    		tiles[i].showTile = false;
	    		tiles[i].flipTimes = 0;
	    		tiles[i].imgName = "color_"+Integer.toString(imgIndex[i]%10)+(imgIndex[i] < 10 ? "0" : "1")+".png";
	    	}
    	}
    		
    	tilesCleared = 0;
    }
}