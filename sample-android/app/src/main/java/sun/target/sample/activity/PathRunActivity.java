package sun.target.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import sun.target.R;
import sun.target.sample.anim.PathAnimDrawable;
import sun.target.sample.widget.DisplayUtils;


public class PathRunActivity extends AppCompatActivity {
    private ProgressBar pathDrawable;
    private ProgressBar pathRun1;
    private ProgressBar pathSpread1;

    private ProgressBar pathRun2;
    private ProgressBar pathSpread2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_run);
        int stoke = DisplayUtils.dip2px(3, getResources().getDisplayMetrics());
        int color1 = Color.parseColor("#32FFFFFF");
        int endColor = Color.parseColor("#00FFFFFF");
        pathDrawable = findViewById(R.id.pathDrawable1);

        pathRun1 = findViewById(R.id.pathRun1);
        pathSpread1 = findViewById(R.id.pathSpread1);

        pathRun2 = findViewById(R.id.pathRun2);
        pathSpread2 = findViewById(R.id.pathSpread2);

        pathRun1.setIndeterminateDrawable(new PathAnimDrawable.Builder()
                .setStokeColor(color1)
                .setStroke(stoke)
                .setEndColor(endColor)
                .setShape(PathAnimDrawable.SHAPE_TRIANGLE)
                .setSpread(false)
                .build());
        pathSpread1.setIndeterminateDrawable(new PathAnimDrawable.Builder()
                .setStokeColor(color1)
                .setStroke(stoke)
                .setEndColor(endColor)
                .setShape(PathAnimDrawable.SHAPE_TRIANGLE)
                .setRunning(false)
                .build());
        pathRun2.setIndeterminateDrawable(new PathAnimDrawable.Builder()
                .setStokeColor(color1)
                .setStroke(stoke)
                .setEndColor(endColor)
                .setSpread(false)
                .build());
        pathSpread2.setIndeterminateDrawable(new PathAnimDrawable.Builder()
                .setStokeColor(color1)
                .setStroke(stoke)
                .setEndColor(endColor)
                .setRunning(false)
                .build());

        pathDrawable.setIndeterminateDrawable(
                new PathAnimDrawable.Builder()
                        .setStokeColor(color1)
                        .setStroke(stoke)
                        .setEndColor(endColor)
                        .build());
    }
}
