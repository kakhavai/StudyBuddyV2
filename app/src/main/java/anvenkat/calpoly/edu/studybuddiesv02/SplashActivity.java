package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the main activity (classListActivity)
        Intent startMain = new Intent(this, ClassListActivity.class);
        startActivity(startMain);
        finish();
    }
}
