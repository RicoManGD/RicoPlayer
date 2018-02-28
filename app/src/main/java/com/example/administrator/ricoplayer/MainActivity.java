package com.example.administrator.ricoplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.administrator.ricoplayer.bean.Songs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button play,pause,stop,open;
    private SeekBar sb;
    private ListView lv;
    private ArrayList<Songs> arrayList;
    private ArrayAdapter<Songs> arrayAdapter=null;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TimerTask timerTask;
    private TextView time_current,time_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play=(Button) findViewById(R.id.play_btn);
        pause=(Button) findViewById(R.id.pause_btn);
        stop=(Button) findViewById(R.id.stop_btn);
        open=(Button) findViewById(R.id.open_btn);
        time_current = (TextView) findViewById(R.id.time_current);
        time_total = (TextView) findViewById(R.id.time_total);
        sb=(SeekBar)findViewById(R.id.sb);



        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        open.setOnClickListener(this);



        //进度条改变事件
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
                    time_current.setText(String.format("%02d:%02d",0,0));
                }
                else {
                    int currentTime=Math.round(mediaPlayer.getCurrentPosition()/1000);
                    String vt1=String.format("%02d:%02d",currentTime/60,currentTime%60);
                    time_current.setText(vt1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sb.getProgress());

            }
        });

        arrayList=new ArrayList<Songs>();//
        arrayAdapter=new ArrayAdapter<Songs>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList);//

        lv=(ListView)findViewById(R.id.history_lv);
        lv.setAdapter(arrayAdapter);

        //ListView各项点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(arrayList.get(position)!=null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer=null;
                    try{
                        mediaPlayer=new MediaPlayer();
                        mediaPlayer.setDataSource(MainActivity.this,Uri.parse(arrayList.get(position).getPath()));//
                        mediaPlayer.prepare();
                        StartTrackingProgress(mediaPlayer);
                        mediaPlayer.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        initMediaPlayer();



    }
    //初始化
    void initMediaPlayer(){
        try{

            arrayList.add(new Songs("soler","android.resource://"+getPackageName()+"/"+R.raw.soler));//
            arrayAdapter.notifyDataSetChanged();
            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.soler);
            mediaPlayer.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //播放器进度
    void StartTrackingProgress( MediaPlayer player){
        if(timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        sb.setMax(player.getDuration());
        int totalTime=Math.round(player.getDuration()/1000);
        String tv2=String.format("/%02d:%02d",totalTime/60,totalTime%60);
        time_total.setText(tv2);

        timer=new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                sb.setProgress(mediaPlayer.getCurrentPosition());

            }
        };
        timer.schedule(timerTask,0,1000);

    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_btn:
                if(mediaPlayer!=null){
                    if (!mediaPlayer.isPlaying()){
                        StartTrackingProgress(mediaPlayer);
                        mediaPlayer.start();
                    }
                }
                break;
            case R.id.pause_btn:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.start();
                }
                break;
            case R.id.stop_btn:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();

                }
                time_total.setText(String.format("/%02d:%02d",0,0));


                break;
            case R.id.open_btn:
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
                break;


        }
    }


    //intent返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(data!=null){

                Uri uri=data.getData();
                String path=uri.toString();
                Songs Songz=new Songs(path.substring(path.lastIndexOf("/") + 1, path.length()),path);
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer=null;
                try {
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setDataSource(MainActivity.this,Uri.parse(path));
                    mediaPlayer.prepare();

                    if (!arrayList.contains(Songz)){
                        arrayList.add(Songz);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    mediaPlayer.start();
                    StartTrackingProgress(mediaPlayer);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
}
