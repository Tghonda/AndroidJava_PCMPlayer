package jp.co.orifice.pcm_player;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private PcmAudioPlayer player = new PcmAudioPlayer(48000);
    private WavReader wav;
    private PcmReader pcm;

    /********************************************************************************************
     Activity Life Cycle
     ********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_play_wav).setOnClickListener(onClickPlayWAV);
        findViewById(R.id.bt_play_pcm).setOnClickListener(onClickPlayPCM);

        findViewById(R.id.bt_stop).setOnClickListener(onClickStop);

    }
    @Override
    protected void onDestroy () {
//        localEb.unregister(this);
        Log.d(TAG, "<<<  onDestroy  >>>");

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "<<<  onStart  >>>");
//        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {               // Pauseの後で呼ばれる、いろいろ停止する
        Log.d(TAG, "<<<  onStop  >>>");
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "<<<  onResume  >>>");

    }
    @Override
    protected void onPause() {              // 現在の画面が非アクティブになるとき
        Log.d(TAG, "<<<  onPause  >>>");
        super.onPause();
    }
    /********************************************************************************************
     Button Actions
     ********************************************************************************************/
    // Button Click.
    View.OnClickListener onClickPlayWAV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                InputStream is = getAssets().open("audio_test.wav");
                wav = new WavReader(is);
                player.play(wav);
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                return;
            }

        }
    };
    View.OnClickListener onClickPlayPCM = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                InputStream is = getAssets().open("audio_test.pcm");
                pcm = new PcmReader(is);
                player.play(pcm);
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                return;
            }

        }
    };
    View.OnClickListener onClickStop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            player.stop();
        }
    };

}
