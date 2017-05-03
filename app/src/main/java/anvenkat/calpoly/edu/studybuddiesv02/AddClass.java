package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ani on 11/21/2016.
 */

public class AddClass extends AppCompatActivity implements View.OnClickListener{

    private EditText className;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_class);

        className = (EditText)findViewById(R.id.inputClass);
        confirm = (Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * On click method for confirm button, creates an intent and sends it back to the Main Activity
     * where the class information can be added to the list.
     */
    public void onClick(View v) { //when the add button is clicked
        if (v == confirm) {
            final Class c = new Class("");
            String classString = className.getText().toString();
            c.setClassName(classString);
            if (classString.length() > 0) { //if valid class name, it sends intent back
                Intent sendClass = new Intent();
                sendClass.putExtra("class", c);
                setResult(RESULT_OK, sendClass);
                super.finish();
            }
            else { //if invalid class name, pops this message
                Toast.makeText(this, "Please Enter a Class Name", Toast.LENGTH_SHORT).show();
            }
        }
    }
}