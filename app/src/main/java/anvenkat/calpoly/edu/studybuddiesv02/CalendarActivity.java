package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Calendar Class. Allows for specific day to be selected on calendar.
 * @Author: Kian
 */

public class CalendarActivity extends AppCompatActivity {
    //Tried to use an ArrayMap but it didnt hit our API requirement
    HashMap<String, ArrayList<Work>> workPerDay;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);

        Bundle extras = getIntent().getExtras();
        workPerDay = new HashMap<String, ArrayList<Work>>();
        ArrayList<Class> classes = extras.getParcelableArrayList("classes");

        //Populate a hashmap with day/month/year as the key
        //And in this hashmap I will build what will go in the calendar
        //The reason we use a hashmap is so that if we want to access a certain day
        //we can get the list of assignments due on that day easily.
        for(int i = 0; i < classes.size(); i++) {
            for(int j = 0; j < classes.get(i).getWork().size(); j++) {
                Work temp = classes.get(i).getWork().get(j);
                String key = temp.day + "-" + temp.month + "-" + temp.year;
                if(workPerDay.containsKey(key)){
                    workPerDay.get(key).add(temp);
                }else{
                    workPerDay.put(key, new ArrayList<Work>());
                    workPerDay.get(key).add(temp);
                }
            }
        }

        //classes.get(0).getWork().get(0).g

        ExtendedCalendarView calendar = (ExtendedCalendarView)findViewById(R.id.calendar);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        calendar.setOnDayClickListener(new ExtendedCalendarView.OnDayClickListener() {
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
                //Toast.makeText(this, "Day: " + day.g)
                String key = day.getDay() + "-" + day.getMonth() + "-" + day.getYear();
                Log.e("?", key);
                Intent sendIntent = new Intent(getApplicationContext(), CalendarSelectedDayActivity.class);
                if(workPerDay.containsKey(key)) {
                    sendIntent.putExtra("toAdd", workPerDay.get(key));
                }else{
                    sendIntent.putExtra("toAdd", new ArrayList<Work>());
                }
                startActivity(sendIntent);
            }
        });
        getContentResolver().delete(CalendarProvider.CONTENT_URI, null, null);
        for(String tempKey : workPerDay.keySet()) {

            ContentValues values = new ContentValues();


            //These wont ever be seen, lets set them to some default values
            values.put(CalendarProvider.DESCRIPTION, "Some Description");
            values.put(CalendarProvider.LOCATION, "Some location");
            values.put(CalendarProvider.EVENT, "Event name");
            Calendar cal = Calendar.getInstance();

            String list[] = tempKey.split("-");

            Log.e("?",list[0] + " " + list[1] + " " + list[2]);
            //Set date based on key
            cal.set(Integer.parseInt(list[2]),Integer.parseInt(list[1]),Integer.parseInt(list[0]));
            values.put(CalendarProvider.START, cal.getTimeInMillis());
            TimeZone tz = TimeZone.getDefault();
            int startDay = Time.getJulianDay(cal.getTimeInMillis(),TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
            values.put(CalendarProvider.START_DAY, startDay);
            int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
            values.put(CalendarProvider.END, cal.getTimeInMillis());
            values.put(CalendarProvider.END_DAY, endDayJulian);

            //Set color based on amount of assignments on certain day
            if(workPerDay.get(tempKey).size() > 3)
                values.put(CalendarProvider.COLOR, Event.COLOR_RED);
            else if( workPerDay.get(tempKey).size() > 2)
                values.put(CalendarProvider.COLOR, Event.COLOR_YELLOW);
            else
                values.put(CalendarProvider.COLOR, Event.COLOR_GREEN);
            //Basically call "set"
            Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI, values);

        }


    }
}