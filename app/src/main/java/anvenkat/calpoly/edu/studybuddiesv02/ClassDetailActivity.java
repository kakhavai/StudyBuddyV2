package anvenkat.calpoly.edu.studybuddiesv02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

/**
 * An activity representing a single Class detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClassListActivity}.
 */
public class ClassDetailActivity extends AppCompatActivity implements ClassDetailFragment.CallMain{
    private Class c;
    private int index;
    private ArrayList<Work> toReturn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);


        // Show the Up button in the action bar.
        //ActionBar actionBar = getSupportActionBar();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = getIntent().getExtras();
            c = arguments.getParcelable("class");
            toReturn = c.getWork();
            index = arguments.getInt("index");
            arguments.putParcelable("class", c); //sends the class to the fragment
            arguments.putInt("index", index); // sends the current position to the fragment

            ClassDetailFragment fragment = new ClassDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.class_detail_container, fragment)
                    .commit();
        }
        else{
            toReturn = savedInstanceState.getParcelableArrayList("workList");
            c = savedInstanceState.getParcelable("class");
            index = savedInstanceState.getInt("index");
        }
    }

    @Override
    public void setWork(ArrayList<Work> toSet, int index) {
        toReturn = toSet;
    }

    @Override
    public void finish() {
        super.finish();
    }
    public void onBackPressed(){
        Intent intent = new Intent();
        if(toReturn != null) {
            intent.putParcelableArrayListExtra("workList", toReturn);
        }
        intent.putExtra("index", index);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("class", c);
        outState.putParcelableArrayList("workList", toReturn);
        outState.putInt("index", index);
        super.onSaveInstanceState(outState);
    }
}
