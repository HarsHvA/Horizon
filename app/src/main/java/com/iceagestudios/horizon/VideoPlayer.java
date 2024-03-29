package com.iceagestudios.horizon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VideoPlayer extends AppCompatActivity implements Player.EventListener,View.OnClickListener,View.OnTouchListener,
GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
    private static final String TAG = "VideoPlayer" ;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private View decorView;
    private Handler mainHandler;
    private Runnable updatePlayer,hideControls;
    private TimeBar timeBar;
    private TextView txt_ct;
    private TextView txt_td;
    private LinearLayout root,unlock_panel,vol_brightness_layout;
    private ImageButton btn_pause,btn_screen_orientation,btn_lock,btn_unlock,btn_more;
    private boolean pause = false,portrait = true,stopAlarmRunnable = false;
    private Dialog dialog_playback,dialog_resize,jump_dialog,list_dialog;
    private PlaybackParameters parameters;
    private GestureDetector gestureDetector;
    private int sWidth,sHeight;
    private Display display;
    private Point size;
    private AudioManager audioManager;
    private WindowManager.LayoutParams layoutParams;
    private String seekDuration;
    private boolean showController = true;
    private ProgressBar progressBar;
    private Intent intent;
    private long seekProgress;
    private int loadControlBufferMs = 90000;
    private String name;
    private FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences savedUrl;
    private boolean saveHistory = true;
   // private ArrayAdapter<String> listAdapter;
   // private ArrayList<String> subArrayList;
// Subtitles
    MediaSource subtitleSource;
    public boolean caption;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
    // gesture
    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown: called");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: called");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //     Log.d(TAG, "onSingleTapUp: called");
        if(vol_brightness_layout!=null)
        {
            vol_brightness_layout.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(e1.getX()<e2.getX() && (e2.getX()-e1.getX())>100)
        {
            int speed = (int) ((e2.getX() - e1.getX())*10);
            if(exoPlayer.getCurrentPosition() > exoPlayer.getDuration() - 10000 )
            {
                exoPlayer.seekTo(exoPlayer.getDuration());
            }
            else
            {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() + speed);
            }
            ShowVolBrightLayout(seekDuration,null);
        }
        else if(e2.getX()<e1.getX() && (e1.getX()-e2.getX())>100)
        {
            int speed = (int) ((e1.getX() - e2.getX())*10);
            if(exoPlayer.getCurrentPosition() < 10000)
            {
                exoPlayer.seekTo(0);
            }else
            {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() - speed);
            }
            ShowVolBrightLayout("["+seekDuration+"]",null);
        }
        int width = sWidth/2;
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG+"volume", String.valueOf(mediaVolume));
        /*
         * Volume
         */
        if(e1.getX()>width)
        {
            if(e1.getY() > e2.getY() && (e1.getY() - e2.getY())>30 )
            {
                double cal = Math.abs(distanceY)/15;
                int newMediaVolume = mediaVolume + (int) cal;
                if (newMediaVolume > maxVolume) {
                    newMediaVolume = maxVolume;
                } else if (newMediaVolume < 1) {
                    newMediaVolume = 0;
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newMediaVolume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                double volPerc = Math.ceil((((double) newMediaVolume / (double) maxVolume) * (double) 100));
                Log.d(TAG, String.valueOf(volPerc));
                ShowVolBrightLayout(volPerc + "%","Volume");
            }
            else if(e1.getY() < e2.getY() && (e2.getY() - e1.getY())>30)
            {
                double cal = Math.abs(distanceY)/15;
                int newMediaVolume = mediaVolume - (int) cal;
                if (newMediaVolume > maxVolume) {
                    newMediaVolume = maxVolume;
                } else if (newMediaVolume < 1) {
                    newMediaVolume = 0;
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newMediaVolume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                double volPerc = Math.ceil((((double) newMediaVolume / (double) maxVolume) * (double) 100));
                Log.d(TAG, String.valueOf(volPerc));
                ShowVolBrightLayout(volPerc + "%","Volume");
            }
        }

        /*
        Brightness
         */
        else
        {
            float brightness = layoutParams.screenBrightness;
            //    Log.i(TAG+"brightness", String.valueOf((brightness)));
            if(e1.getY() > e2.getY() && (e1.getY() - e2.getY())>80 )
            {
                brightness += 0.01;
                float new_brightness = brightness ;
                if (new_brightness > 1) {
                    new_brightness = 1;
                } else if (new_brightness < 0) {
                    new_brightness = 0;
                }
                //      Log.i(TAG+"up", String.valueOf((new_brightness)));
                layoutParams.screenBrightness = new_brightness;
                getWindow().setAttributes(layoutParams);
                ShowVolBrightLayout(Math.ceil(new_brightness*100) + "%","Brightness");
            }
            else if(e1.getY() < e2.getY() && (e2.getY() - e1.getY())>80)
            {
                brightness -= 0.01;
                float new_brightness = brightness;
                if (new_brightness > 1) {
                    new_brightness = 1;
                } else if (new_brightness < 0) {
                    new_brightness = 0;
                }
                //      Log.i(TAG+"down", String.valueOf((new_brightness)));
                layoutParams.screenBrightness = new_brightness;
                getWindow().setAttributes(layoutParams);
                ShowVolBrightLayout(Math.ceil(new_brightness*100) + "%","Brightness");
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: called");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling: called");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        if(showController)
        {
            showControls();
            showController = false;
        }
        else {
            hideAllControls();
            showController = true;
        }

        if(vol_brightness_layout!=null) {
            if (vol_brightness_layout.getVisibility() == View.VISIBLE) {
                vol_brightness_layout.setVisibility(View.GONE);
            }
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        int width = sWidth/2;
        if(e.getX()>width)
        {

            if(exoPlayer.getCurrentPosition() < exoPlayer.getDuration()) {
                ShowVolBrightLayout("[+00:10]",null);
                if(exoPlayer.getCurrentPosition() > exoPlayer.getDuration() - 10000)
                {
                    exoPlayer.seekTo(exoPlayer.getDuration());
                }
                else
                {
                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
                }
            }
            return true;
        }else
        {
            if(exoPlayer.getCurrentPosition() > 0) {
                ShowVolBrightLayout("[-00:10]",null);
                if(exoPlayer.getCurrentPosition() < 10000)
                {
                    exoPlayer.seekTo(0);
                }
                else
                {
                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
                }
            }
            return true;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent: called");
        return true;
    }

    public enum ControlsMode {
        LOCK, FULLCONTORLS
    }
    private ControlsMode controlsState;
    private boolean playerState = true;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_pause:
                if(!pause)
                {
                    exoPlayer.setPlayWhenReady(false);
                    btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                    pause = true;
                }else
                {
                    exoPlayer.setPlayWhenReady(true);
                    btn_pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    pause = false;
                }
                break;

            case R.id.btn_screen_orientation:
                Toast.makeText(this, "Orientation locked! (Long press on button to cancel", Toast.LENGTH_SHORT).show();
                if (portrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    portrait = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    portrait = true;
                }
                break;

            case R.id.btn_lock:
                controlsState = ControlsMode.LOCK;
                root.setVisibility(View.GONE);
                unlock_panel.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_unlock:
                controlsState = ControlsMode.FULLCONTORLS;
                root.setVisibility(View.VISIBLE);
                unlock_panel.setVisibility(View.GONE);
                break;

            case R.id.verySlow:
                parameters = new PlaybackParameters(0.25f);
                exoPlayer.setPlaybackParameters(parameters);
                dialog_playback.dismiss();
                break;

            case R.id.slow:
                parameters = new PlaybackParameters(0.5f);
                exoPlayer.setPlaybackParameters(parameters);
                dialog_playback.dismiss();
                break;

            case R.id.normal:
                parameters = new PlaybackParameters(1f);
                exoPlayer.setPlaybackParameters(parameters);
                dialog_playback.dismiss();
                break;

            case R.id.fast:
                parameters = new PlaybackParameters(1.25f);
                exoPlayer.setPlaybackParameters(parameters);
                dialog_playback.dismiss();
                break;

            case R.id.very_fast:
                parameters = new PlaybackParameters(2f);
                exoPlayer.setPlaybackParameters(parameters);
                dialog_playback.dismiss();
                break;

            case R.id.four_resize:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                dialog_resize.dismiss();
                break;

            case R.id.six_resize:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                dialog_resize.dismiss();
                break;

            case R.id.fit_resize:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                dialog_resize.dismiss();
                break;

            case R.id.fill_resize:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                dialog_resize.dismiss();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        setContentView(R.layout.activity_video_player);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        intent = getIntent();

        savedUrl = PreferenceManager.getDefaultSharedPreferences(this);
        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(audioManager).requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        name = intent.getStringExtra("VideoName");
        layoutParams = getWindow().getAttributes();
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;
        portrait = true;
        caption = false;
        playerView = findViewById(R.id.playerView);
        FindView();
        decorView = getWindow().getDecorView();

        gestureDetector = new GestureDetector(this,this);
        gestureDetector.setOnDoubleTapListener(this);

        PreparePlayer();

        playerView.setPlayer(exoPlayer);
        exoPlayer.prepare(CreateMediaSource());
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

        long savedUri = savedUrl.getLong(String.valueOf(VideoUrl()),-1);
        if(savedUri!= -1)
        {
            exoPlayer.seekTo(savedUri);
        }

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText(name);
        mainHandler = new Handler();

        updatePlayer = () -> {


            @SuppressLint("DefaultLocale") String curDur = String.format("%02d.%02d.%02d",
                    TimeUnit.MILLISECONDS.toHours(exoPlayer.getCurrentPosition()),
                    TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getCurrentPosition()) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(exoPlayer.getCurrentPosition())), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(exoPlayer.getCurrentPosition()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getCurrentPosition())));
            txt_ct.setText(curDur);
            seekDuration = curDur;
            timeBar.setDuration(exoPlayer.getDuration());
            timeBar.setPosition(exoPlayer.getCurrentPosition());
            mainHandler.postDelayed(updatePlayer, 100);
        };
        mainHandler.postDelayed(updatePlayer,200);
        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {

                exoPlayer.seekTo(position);
            }
        });

        btn_pause.setOnClickListener(this);
        btn_screen_orientation.setOnClickListener(this);
        btn_screen_orientation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                Toast.makeText(VideoPlayer.this, "Orientation reset!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        btn_lock.setOnClickListener(this);
        btn_unlock.setOnClickListener(this);
        playerView.setOnTouchListener(this);
        showControls();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        exoPlayer.setPlayWhenReady(false);
//        btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
//        pause = true;
        PIPMode();
    }


    private void FindView()
    {
        timeBar = findViewById(R.id.timeBar);
        txt_ct = findViewById(R.id.txt_currentTime);
        txt_td = findViewById(R.id.txt_totalDuration);
        root = findViewById(R.id.root);
        unlock_panel = findViewById(R.id.unlock_panel);
        btn_pause = findViewById(R.id.btn_pause);
        btn_screen_orientation = findViewById(R.id.btn_screen_orientation);
        btn_lock = findViewById(R.id.btn_lock);
        btn_unlock = findViewById(R.id.btn_unlock);
        btn_more = findViewById(R.id.btn_more);
        progressBar = findViewById(R.id.progress_bar);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus)
        {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    {
        hideControls = this::hideAllControls;
    }
    private void hideAllControls(){
        playerView.hideController();
        showController = true;
    }
    private void showControls()
    {
        playerView.showController();
        showController = false;
        mainHandler.removeCallbacks(hideControls);
        mainHandler.postDelayed(hideControls, 3000);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
    {

        if(playbackState == ExoPlayer.STATE_READY && playWhenReady)
        {
            progressBar.setVisibility(View.GONE);
            @SuppressLint("DefaultLocale") String totDur = String.format("%02d.%02d.%02d",
                    TimeUnit.MILLISECONDS.toHours(exoPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getDuration()) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(exoPlayer.getDuration())), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(exoPlayer.getDuration()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(exoPlayer.getDuration())));
            txt_td.setText(totDur);
        }else if(playbackState == ExoPlayer.STATE_BUFFERING)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void ShowMoreBtnPopUp(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.play_back_speed:
                    ShowPlaybackPopup();
                    return true;

                case R.id.resize:
                    ShowResizePopup();
                    return true;

                case R.id.sleep_timer:
                    stopAlarmRunnable = false;
                    SleepTimer();
                    return true;

                case R.id.jump_to_time:
                    ShowJumpToTimePopup();
                    return true;

                case R.id.share_btn:
                    ShareFile();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    public void ShowSubtitlePopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.subtitle_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.offline_sub:
                    seekProgress = exoPlayer.getCurrentPosition();
                    exoPlayer.setPlayWhenReady(false);
                    btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                    pause = true;
                    ShowOfflineSubtitleDialog();
                    return true;

                case R.id.remove_sub:
                    if(caption)
                    {
                        long progress = exoPlayer.getCurrentPosition();
                        exoPlayer.setPlayWhenReady(false);
                        exoPlayer.prepare(CreateMediaSource());
                        caption = false;
                        exoPlayer.setPlayWhenReady(true);
                        exoPlayer.seekTo(progress);
                    }
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void SleepTimer()
    {
        exoPlayer.setPlayWhenReady(false);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(VideoPlayer.this, (view, hourOfDay, minute) -> SleepAlarm(hourOfDay,minute),0,0,true);
        timePickerDialog.show();
        timePickerDialog.setOnDismissListener(dialog -> exoPlayer.setPlayWhenReady(true));
        timePickerDialog.setOnCancelListener(dialog -> exoPlayer.setPlayWhenReady(true));
    }

    private void SleepAlarm(final int hourOfTheDay,final int minutes)
    {
        final Handler mhandler = new Handler();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                if(!stopAlarmRunnable) {
                    if (hour == hourOfTheDay && minute == minutes) {
                        exoPlayer.setPlayWhenReady(false);
                        btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                        pause = true;
                        Toast.makeText(VideoPlayer.this, "Video Paused!", Toast.LENGTH_SHORT).show();
                        stopAlarmRunnable = true;
                    }
                    mhandler.postDelayed(this, 0);
                }else
                {
                    mhandler.removeCallbacks(this);
                }
            }
        });
    }

    private void ShowPlaybackPopup()
    {
        dialog_playback = new Dialog(this);
        dialog_playback.setContentView(R.layout.playback_popup);
        Objects.requireNonNull(dialog_playback.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_playback.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView verySlow = dialog_playback.findViewById(R.id.verySlow);
        TextView slow = dialog_playback.findViewById(R.id.slow);
        TextView normal = dialog_playback.findViewById(R.id.normal);
        TextView fast = dialog_playback.findViewById(R.id.fast);
        TextView veryFast = dialog_playback.findViewById(R.id.very_fast);
        verySlow.setOnClickListener(this);
        slow.setOnClickListener(this);
        normal.setOnClickListener(this);
        fast.setOnClickListener(this);
        veryFast.setOnClickListener(this);
        dialog_playback.show();
    }

    private void ShowResizePopup()
    {
        dialog_resize = new Dialog(this);
        dialog_resize.setContentView(R.layout.resize_popup);
        Objects.requireNonNull(dialog_resize.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_resize.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView four_resize = dialog_resize.findViewById(R.id.four_resize);
        TextView six_resize = dialog_resize.findViewById(R.id.six_resize);
        TextView fit_resize = dialog_resize.findViewById(R.id.fit_resize);
        TextView fill_resize = dialog_resize.findViewById(R.id.fill_resize);
        four_resize.setOnClickListener(this);
        six_resize.setOnClickListener(this);
        fit_resize.setOnClickListener(this);
        fill_resize.setOnClickListener(this);
        dialog_resize.show();
    }

    private void ShowJumpToTimePopup() {
        exoPlayer.setPlayWhenReady(false);
        btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        pause= true;
        jump_dialog = new Dialog(this);
        jump_dialog.setContentView(R.layout.timer_dialog);
        Objects.requireNonNull(jump_dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText hour = jump_dialog.findViewById(R.id.timerHourEditText);
        final EditText minute = jump_dialog.findViewById(R.id.timerMinuteEditText);
        final EditText seconds = jump_dialog.findViewById(R.id.timerSecondsEditText);
        ImageButton jumpTime = jump_dialog.findViewById(R.id.timerBtn);
        jump_dialog.show();
        jumpTime.setOnClickListener(v -> jump_time(hour,minute,seconds));
    }

    private void ShowVolBrightLayout(String percentage, String name)
    {
        vol_brightness_layout = findViewById(R.id.vol_brightness_layout);
        vol_brightness_layout.setVisibility(View.VISIBLE);
        TextView nameText = findViewById(R.id.vol_text);
        TextView percentageText = findViewById(R.id.vol_percentage);
        nameText.setText(name);
        percentageText.setText(percentage);
        Runnable r = () -> {

            if(vol_brightness_layout!= null)
            {
                vol_brightness_layout.setVisibility(View.GONE);
            }
        };
        mainHandler.removeCallbacks(r);
        mainHandler.postDelayed(r,3000);
    }

    private void jump_time(EditText hour,EditText minute,EditText seconds)
    {
        int hourInt ;
        int minuteInt;
        int secondsInt;
        if (hour.getText().toString().isEmpty()) {
            hourInt = 0;
        } else {
            hourInt = (Integer.parseInt(hour.getText().toString())) * 60 * 60;
        }

        if (minute.getText().toString().isEmpty()) {
            minuteInt = 0;
        } else {
            minuteInt = Integer.parseInt(minute.getText().toString()) * 60;
        }
        if (seconds.getText().toString().isEmpty()) {
            secondsInt = 0;
        } else {
            secondsInt = Integer.parseInt(seconds.getText().toString());
        }
        int timeToJump = hourInt + minuteInt + secondsInt;
        long timeToJumpLong = timeToJump * 1000;
        if (exoPlayer.getDuration() > timeToJumpLong) {
            exoPlayer.seekTo(timeToJumpLong);
            jump_dialog.dismiss();
        } else {
            Toast.makeText(VideoPlayer.this, "Its out of duration!", Toast.LENGTH_SHORT).show();
            jump_dialog.dismiss();
        }

        exoPlayer.setPlayWhenReady(true);
        btn_pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        pause = false;
    }


    // Subtitles

    private void ShowOfflineSubtitleDialog()
    {
        FilePickerDialog dialog = new FilePickerDialog(this,GetDialogProperties());
        dialog.setTitle("Select a File");

        dialog.show();
        dialog.setDialogSelectionListener(files -> FetchSubtitles(files[0]));
    }
    private DialogProperties GetDialogProperties()
    {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{".srt"};

        return  properties;
    }

    public void FetchSubtitles(String path)
    {
        Uri uri = Uri.parse(path);
        subtitleSource = new SingleSampleMediaSource.Factory(CreateDataSourceFactory()).createMediaSource(uri,
                Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                        Format.NO_VALUE, "en", null),
                C.TIME_UNSET);
            MergingMediaSource mergingMediaSource = new MergingMediaSource(CreateMediaSource(), subtitleSource);
            exoPlayer.prepare(mergingMediaSource);
        exoPlayer.setPlayWhenReady(true);
        btn_pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        pause = false;
        exoPlayer.seekTo(seekProgress);
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        caption = true;
    }
    private MediaSource CreateMediaSource()
    {
        int type = Util.inferContentType(Objects.requireNonNull(VideoUrl()));

        switch (type)
        {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(CreateDataSourceFactory())
                        .createMediaSource(Objects.requireNonNull(VideoUrl()));

            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(CreateDataSourceFactory()).setAllowChunklessPreparation(true)
                        .createMediaSource(VideoUrl());

            case C.TYPE_SS:
                return new SsMediaSource.Factory(CreateDataSourceFactory()).createMediaSource(Objects.requireNonNull(VideoUrl()));

            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(CreateDataSourceFactory()).createMediaSource(VideoUrl());

                default:
                    throw  new IllegalStateException("Unsupported Type: " + type);
        }

    }


    private Uri VideoUrl()
    {
        Uri url;
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if(Intent.ACTION_VIEW.equals(action) && type!=null) {
            if (type.equals("video/*")) {
                Uri videoUri = intent.getData();
                if (videoUri != null) {
                    url = videoUri;
                    loadControlBufferMs = 50000;
                    return url;
                }
            }
        }else if(action == null)
        {
            url = Uri.parse(intent.getStringExtra("VideoPath"));
            Log.d(TAG, "VideoUrl: "+ url);
            loadControlBufferMs = 90000;
            return url;
        }else if(Intent.ACTION_VIEW.equals(action))
        {
            url = intent.getData();
            loadControlBufferMs = 90000;
            return url;
        }
        return intent.getData();
    }

    private boolean PreparePlayer()
    {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(action == null && type == null)
        {
            saveHistory = true;
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        }else if(Intent.ACTION_VIEW.equals(action) && type!=null) {

            saveHistory = false;
            if(type.equals("video/*")) {
                exoPlayer = ExoPlayerFactory.newSimpleInstance(this,TrackSelector(),
                        defaultLoadControl());
            }
        }else if(action == null)
        {
            saveHistory = false;
            if(name!=null && name.equals("Stream"))
            {
                exoPlayer = ExoPlayerFactory.newSimpleInstance(this,TrackSelector(),
                        defaultLoadControl());
            }else
            {
                exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
            }
        }else if(Intent.ACTION_VIEW.equals(action))
        {
            saveHistory = false;
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this,TrackSelector(),
                    defaultLoadControl());
        }
        return true;
    }

    private DefaultDataSourceFactory CreateDataSourceFactory()
    {
        return new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,"com.iceagestudios.hvplayer"));
    }

    private DefaultLoadControl defaultLoadControl()
    {
        loadControlBufferMs = 90000;
        DefaultLoadControl.Builder builder = new DefaultLoadControl.Builder();
        builder.setBufferDurationsMs(loadControlBufferMs,loadControlBufferMs
                ,DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                .setAllocator(new DefaultAllocator(true,16))
                .setTargetBufferBytes(-1);
        return builder.createDefaultLoadControl();
    }

    private TrackSelector TrackSelector()
    {
        return new DefaultTrackSelector();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(saveHistory) {
            History history = new History();
            history.SaveHistory(this, String.valueOf(VideoUrl()), true);
        }
        savedUrl.edit().putLong(String.valueOf(VideoUrl()),exoPlayer.getCurrentPosition()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
        btn_pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        pause = false;
    }

    public void FinishPlayerActivity(View view)
    {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    public void ShareFile() {
        if (VideoUrl() != null) {
            String path = String.valueOf(VideoUrl());
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(path);

            if (fileWithinMyDir.exists()) {
                intentShareFile.setType("video/*");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));

                String shareMessage = "\nLet me recommend you this awesome video player\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                // TODO: Change Sharing File with your app name and link
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "HV Player");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, shareMessage);

                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }
        }
    }

    public void PIPMode(){
        Display d = getWindowManager()
                .getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        int width = p.x;
        int height = p.y;

        Rational ratio
                = new Rational(width, height);
       if(Build.VERSION.SDK_INT >= 26) {
           PictureInPictureParams.Builder
                   pip_Builder
                   = new PictureInPictureParams
                   .Builder();
           pip_Builder.setAspectRatio(ratio).build();
           enterPictureInPictureMode(pip_Builder.build());
       }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if(!isInPictureInPictureMode){
        exoPlayer.setPlayWhenReady(false);
        btn_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        pause = true;
        }
    }
}
