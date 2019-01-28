package com.arkletech.your.memory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity implements OnClickListener {
	static final String TAG="MyAndroidDebugTAG";
	static Player player[];
	public static final String playerName[] = {"PlayerName0", "PlayerName1", "PlayerName2", "PlayerName3", "PlayerName4", "PlayerName5", "PlayerName6", "PlayerName7", "PlayerName8", "PlayerName9"};
	public static final String playerScore[] = {"PlayerScore0", "PlayerScore1", "PlayerScore2", "PlayerScore3", "PlayerScore4", "PlayerScore5", "PlayerScore6", "PlayerScore7", "PlayerScore8", "PlayerScore9"};
	static final int nameId[] = {R.id.textViewName0, R.id.textViewName1, R.id.textViewName2, R.id.textViewName3, R.id.textViewName4, R.id.textViewName5, R.id.textViewName6, R.id.textViewName7, R.id.textViewName8, R.id.textViewName9};
	static final int scoreId[] = {R.id.textViewScore0, R.id.textViewScore1, R.id.textViewScore2, R.id.textViewScore3, R.id.textViewScore4, R.id.textViewScore5, R.id.textViewScore6, R.id.textViewScore7, R.id.textViewScore8, R.id.textViewScore9};
	static public final String [] PREF_GAME_TYPE={"AlphaMatch", "ColorMatch"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.high_score);
		
		player = new Player [10];
		for (int i=0; i< 10; i++) {
			player[i] = new Player("Player1", 1000L);
//			Log.d(TAG, "player["+i+"].name="+player[i].name+"; player["+i+"].score="+player[i].score);
		}
		
		findViewById(R.id.buttonScoreOK).setOnClickListener(this);
		
		getSavedTopScore();
		TextView tv = (TextView)findViewById(R.id.textViewGameType);
		tv.setText((YourMemoryActivity.gameType==1?"Alphabet Match":"Color Match"));
		for (int i=0; i<10; i++) {
			Log.d(TAG, "GetView::nameId["+i+"]="+nameId[i]+", scoreId["+i+"]="+scoreId[i]+", player["+i+"].name="+player[i].name+"; player["+i+"].score="+player[i].score);
			tv = (TextView)findViewById(nameId[i]);
			tv.setText(player[i].name);
			tv = (TextView)findViewById(scoreId[i]);
			tv.setText(String.valueOf(player[i].score));
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}
	
	public void getSavedTopScore() {
		SharedPreferences pm0 = getSharedPreferences(PREF_GAME_TYPE[YourMemoryActivity.gameType-1], MODE_PRIVATE);
		for (int i=0; i<10; i++) {
			player[i].name = pm0.getString(playerName[i], "Player1");
			player[i].score = pm0.getLong(playerScore[i], 1000L);
			player[i].name_id = nameId[i];
			player[i].score_id = scoreId[i];
//			Log.d(TAG, "player["+i+"].name="+player[i].name+"; player["+i+"].score="+player[i].score+"; player["+i+"].name_id="+player[i].name_id+"; player["+i+"].score_id="+player[i].score_id);
		}
	}
	
	public class Player {
		int name_id;
		int score_id;
		long score;
		String name;
		
		Player(String n, long s) {
			name = n;
			score = s;
		}
	}

}
