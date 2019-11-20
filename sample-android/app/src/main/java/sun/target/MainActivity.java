package sun.target;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import sun.target.sample.activity.GlideMultiActivity;
import sun.target.sample.activity.PathRunActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, GlideMultiActivity.class);
        startActivity(intent);
        finish();
    }
}
