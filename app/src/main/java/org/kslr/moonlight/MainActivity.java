package org.kslr.moonlight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;


public class MainActivity extends AppCompatActivity {

    private TextView mFeedback;
    private TextView mProgress;
    private SeekBar mSeekBar;
    private TextView mLayout;
    private Float mAlpha = 0.9f;
    private Button mRunController;
    private Boolean mRunMark = true;
    private WindowManager mWindowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化悬浮遮罩层
        mLayout = new TextView(getApplicationContext());
        // 初始化窗口服务
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 初始化Google统计
        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        // 滑动条
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekListener);
        // 控制器按钮
        mRunController = (Button) findViewById(R.id.button);
        mRunController.setOnClickListener(buttonListener);
        // 第一次打开的时候自动打开开关
        buttonListener.onClick(null);
        // 监听屏幕全屏动作
        mLayout.setOnTouchListener(touchListener);
        // 反馈
        mFeedback = (TextView) findViewById(R.id.feedback);
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mail  = new Intent(Intent.ACTION_SENDTO);
                mail.setData(Uri.parse("mailto:kslrwang@gmail.com"));
                mail.putExtra(Intent.EXTRA_SUBJECT, "Moonlight Feedback");
                startActivity(mail);
            }
        });
    }

    /**
     * 控制器监听器
     */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mRunMark) {
                // 关闭状态,准备启动
                startNight();
                mSeekBar.setProgress(90);
                mRunController.setText(R.string.app_button_off);
                mRunMark = false;
            } else {
                // 启动状态,准备关闭
                stopNight();
                mSeekBar.setProgress(0);
                mRunController.setText(R.string.app_buttion_on);
                mRunMark = true;
            }
        }
    };

    /**
     * 启动遮罩层
     */
    public void startNight() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_TOAST
                , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
        );
        lp.x = 0;
        lp.y = 0;
        lp.alpha = mAlpha;
        mLayout.setBackgroundColor(Color.BLACK);
        mWindowManager.addView(mLayout, lp);
    }

    /**
     * 删除遮罩层
     */
    public void stopNight() {
        mWindowManager.removeView(mLayout);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Window window = getWindow();
            int location[] = new int[2];
            v.getLocationOnScreen(location);
            System.out.println(location);
            return true;
        }
    };

//    public boolean onTouchListener() {
//        Window window  = getWindow();

//        if(isFullScreen){
//            //设置非全屏
//            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        }else{
//            //设置全屏
//            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//        isFullScreen = !isFullScreen;
//        return true;
//    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(mLayout != null) {
                // 最高只能到90
                if(progress <= 90) {
                    mLayout.setAlpha((float) (progress / 100.0));
                }
                mProgress = (TextView) findViewById(R.id.progress);
                mProgress.setText(progress + "%");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
