package anvenkat.calpoly.edu.studybuddiesv02;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * A fragment representing a single Class detail screen.
 * This fragment is either contained in a {@link ClassListActivity}
 * in two-pane mode (on tablets) or a {@link ClassDetailActivity}
 * on handsets.
 */
public class ClassDetailFragment extends ContractFragment<ClassDetailFragment.CallMain>  {
    private Class c;
    private int index;
    private ListView wView;
    private EditText newWork;
    private Button confirmWork;
    ArrayList<Work> workList;
    private WorkAdapter arrayAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClassDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = getArguments().getParcelable("class");
        index = getArguments().getInt("index");
        workList = c.getWork();
        arrayAdapter = new WorkAdapter(getContext(), workList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * prints name of class at top and persists on rotate.
         */
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(c.getClassName());
        }
        View rootView = inflater.inflate(R.layout.class_detail, container, false);
        wView = (ListView) rootView.findViewById(R.id.workView);
        newWork = (EditText) rootView.findViewById(R.id.addNewWork);
        confirmWork = (Button) rootView.findViewById(R.id.confirmWork);
        wView.setAdapter(arrayAdapter);

        confirmWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameOfWork = newWork.getText().toString();
                if (nameOfWork.length() > 0) {
                    Work w = new Work();
                    w.setToDo(nameOfWork);
                    w.setCompleted(false);
                    arrayAdapter.add(w);
                    getContract().setWork(workList, index);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        Log.e("?", "Test");
/*        ArrayList<Work> alarms = arrayAdapter.getAlarms();
        //This is kind of a back hack because the day/month/year is being done in the work adapter instead of this class
        //There is no other way to communicate between the adapter and list
        //Thanks Obama..
        for(int i = 0; i < alarms.size(); i++) {
            workList.get(i).setDay(alarms.get(i).day);
            workList.get(i).setMonth(alarms.get(i).month);
            workList.get(i).setYear(alarms.get(i).year);
        }

        //Log.i("yo", "2");
        getContract().setWork(workList, index);
        arrayAdapter.notifyDataSetChanged();
*/
        super.onPause();
    }

    public interface CallMain {
        public void setWork(ArrayList<Work> toSet, int index);

    }

    /**
     * Created by aniru on 11/21/2016.
     *
     */

    public class WorkAdapter extends ArrayAdapter<Work> implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener{

        private ArrayList<Work> workList;
        private int day = 0, year = 0, month = 0;
        private int second = 0, minute = 0, hour = 0;
        private AlarmManager alarm;
        private TextView workName;
        private String workNameTag;
        private int positionTag, cancelTag;
        PendingIntent intentV;
        Intent intent;
        private ImageButton setDT, setAlarm, cancelAlarm, delete;

        public WorkAdapter(Context context, ArrayList<Work> workList){
            super(context, 0, workList);
            this.workList = workList;
        }

        @Override
        public Work getItem(int position){
            return workList.get(position);
        }

        @Override
        public int getCount(){
            return workList.size();
        }

        public View getView(final int position, final View convertView, ViewGroup parent){
            LayoutInflater inf = LayoutInflater.from(getContext());
            View menuL = inf.inflate(R.layout.class_detail_workview_detail, parent, false);

            workName = (TextView)menuL.findViewById(R.id.workName);
            CheckBox workBox = (CheckBox)menuL.findViewById(R.id.workCompleted);

            setDT = (ImageButton)menuL.findViewById(R.id.setDateAndTime);
            setDT.setTag(position);

            cancelAlarm = (ImageButton)menuL.findViewById(R.id.cancel);
            cancelAlarm.setTag(position);

            delete = (ImageButton)menuL.findViewById(R.id.delete);

            final Work w = getItem(position);
            workName.setText(w.getToDo().toString());

            workBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    w.setCompleted(isChecked);
                    getContract().setWork(workList, index);
                    arrayAdapter.notifyDataSetChanged();
                }
            });
            workBox.setChecked(w.getCompleted());

            /**
             * calendar to set the date and time for when the assignment is due.
             */
            setDT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    positionTag = (Integer)view.getTag();
                    final Work w = getItem(positionTag);
                    workNameTag = w.getToDo().toString();
                    Calendar date = Calendar.getInstance();
                    DatePickerDialog dateChooser = new DatePickerDialog(getContext(), WorkAdapter.this,
                            date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                    dateChooser.show();
                }
            });
            /**
             * cancels the specific pending intent
             */
            cancelAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelTag = (Integer)view.getTag();
                    intent = new Intent(getContext(), NotificationBroadcaster.class);
                    final PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), cancelTag, intent, PendingIntent.FLAG_NO_CREATE);
                    if (pendingIntent != null) {
                        Toast.makeText(getContext(), "Cancelled Alarm", Toast.LENGTH_SHORT).show();
                        pendingIntent.cancel();
                    }
                    else {
                        Toast.makeText(getContext(), "Alarm Not Set", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete " + workList.get(position).getToDo()+ "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    workList.remove(position);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });

            return menuL;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            month = i1;
            year = i;
            day = i2;
            Calendar date = Calendar.getInstance();
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), WorkAdapter.this,
                    date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);
            timePicker.show();
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            minute = i1;
            second = 0;
            hour = i;

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DATE, day);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.YEAR, year);

            workList.get(positionTag).setDay(day);
            workList.get(positionTag).setMonth(month);
            workList.get(positionTag).setYear(year);

            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, second);
            Toast.makeText(getContext(), "alarm set to: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();

            intent = new Intent(getContext(), NotificationBroadcaster.class);
            intent.putExtra("nameofwork", workNameTag);
            intent.putExtra("id", positionTag);
            intentV = PendingIntent.getBroadcast(getContext(), positionTag, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                alarm.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), intentV);
            } else {
                alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), intentV);
            }

            getContract().setWork(workList, index);
            arrayAdapter.notifyDataSetChanged();
        }

        public ArrayList<Work> getAlarms(){
            return workList;
        }
    }
}