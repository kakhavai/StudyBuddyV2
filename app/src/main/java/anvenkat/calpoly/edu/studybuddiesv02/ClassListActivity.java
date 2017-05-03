package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class "Class" which allows us to instantiate an object of type class. It can hold the name of
 * the class, and added tests/assignments as indicated by the user. Class is parcelable so we can
 * write data to file
 */
class Class implements Parcelable {
    ArrayList<Work> work = new ArrayList<Work>(); //holds the stuff that is due
    String classname; //holds the name of the class
    int progress = 0;

    public Class(String name){
        this.classname = name;
    }

    public void setClassName(String classname){
        this.classname = classname;
    }

    public String getClassName(){
        return classname;
    }

    public int getProgress(){
        return progress;
    }

    public void setProgress(int progressVal){
        this.progress = progressVal;
    }

    public void setWork(ArrayList<Work> work){
        this.work = work;
    }

    public ArrayList<Work> getWork(){
        return work;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeTypedList(work);
        out.writeString(classname);
        out.writeInt(progress);
    }

    public Class(Parcel in) {
        in.readTypedList(work, Work.CREATOR);
        classname = in.readString();
        progress = in.readInt();
    }

    public static final Parcelable.Creator<Class> CREATOR = new Parcelable.Creator<Class>(){
        public Class createFromParcel(Parcel in){
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };
}

/**
 * instantiates an object of type Work which is either a test or an assignment, has a boolean value
 * for if completed, and also stores the date and time for when it is due.
 */
class Work implements Parcelable{
    private String toDo; //name of the assignment or test
    private boolean completed; // has it been completed
    int day;
    int month;
    int year;

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getToDo() {
        return toDo;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }
    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear(){
        return year;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeString(toDo);
        boolean[] arr = new boolean[1];
        arr[0] = completed;
        out.writeBooleanArray(arr);

        //Write the paramaters of the alarm, eg the date the assignment is due
        out.writeInt(day);
        out.writeInt(month);
        out.writeInt(year);
    }

    public static final Parcelable.Creator<Work> CREATOR = new Parcelable.Creator<Work>() {
        public Work createFromParcel(Parcel in) {
            Work w = new Work();
            boolean[] arr = new boolean[1];
            String toDoName = in.readString();
            in.readBooleanArray(arr);

            //Set the paramaters of the work
            w.setToDo(toDoName);
            w.setCompleted(arr[0]);
            w.setDay(in.readInt());
            w.setMonth(in.readInt());
            w.setYear(in.readInt());
            return w;
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };
}

/**
 * An activity representing a list of Classes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ClassDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ClassListActivity extends AppCompatActivity implements ClassDetailFragment.CallMain{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private int currPos = -1; //used for maintaining highlight position of current item selected
    private final int REQUEST_CODE_ADD_CLASS = 1; //request code used to get information back from intent\
    private final int REQUEST_CODE_ADD_WORK = 2;
    private ArrayList<Class> classes;
    private ClassAdapter set = null;
    private RecyclerView r;
    private boolean activityReturned = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        /*
         * add class button done as a floating action button at the bottom, when clicked, pops new intent
         * which allows addition of class.
         */
        FloatingActionButton addClass = (FloatingActionButton) findViewById(R.id.fab);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent classAdder = new Intent(getApplicationContext(), AddClass.class);
                startActivityForResult(classAdder, REQUEST_CODE_ADD_CLASS);
            }
        });

        r = (RecyclerView)findViewById(R.id.class_list);
        r.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState == null) {
            classes = new ArrayList<Class>();
            set = new ClassAdapter(classes, this);
        } else {
            //return saved items
            classes = savedInstanceState.getParcelableArrayList("classList");
            currPos = savedInstanceState.getInt("currSelected");
            set = new ClassAdapter(classes, this);
        }

        r.setAdapter(set); //sets the adapter so that it can be updated when new classes are added.

        if (findViewById(R.id.class_detail_container) != null) {
            mTwoPane = true;
        }

        /**
         * swipe left/right to delete
         */
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    int index = viewHolder.getAdapterPosition();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ClassDetailFragment fragment = (ClassDetailFragment)fragmentManager.findFragmentById(R.id.class_detail_container);
                    if(fragment != null){
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    classes.remove(index);
                    currPos = -1;
                    set.notifyDataSetChanged();
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(r);
    }

    /**
     * saves both the current position and the array list of classes on orientation change.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("classList", classes);
        outState.putInt("currSelected", currPos);
        super.onSaveInstanceState(outState);
    }

    /**
     * onActivityResult is when the Add Class button is clicked. It gets the string via intent
     * and adds the string to the class list adapter.
     * @param requestCode
     * @param resultCode
     * @param returnedIntent
     * @Author: Ani
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent){
        activityReturned = true;
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_CODE_ADD_CLASS:
                    Class c = returnedIntent.getExtras().getParcelable("class");
                    c.setWork(new ArrayList<Work>());
                    classes.add(c);
                    set.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_ADD_WORK:
                    //Log.i("yo", "yo");
                    //ArrayList<Work>workList = returnedIntent.getExtras().getParcelableArrayList("workList");
                    //worklist ends up being null when rotated
                    //Log.i("yo", workList.get(0).getToDo());
                    //int idx = returnedIntent.getExtras().getInt("index");
                    Bundle b = returnedIntent.getExtras();
                    if(b!= null) {
                        ArrayList<Work> workList = b.getParcelableArrayList("workList");
                        int idx = b.getInt("index");
                        //classes.get(idx).setWork(workList);
                        setWork(workList, idx);
                        //set.notifyItemChanged(idx);
                        //set.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            /*case R.id.courses:
                Intent i = new Intent(this, ClassListActivity.class);
                startActivity(i);
                break;*/
            case R.id.calendar:
                Intent calendarStarter = new Intent(getApplicationContext(), CalendarActivity.class);
                calendarStarter.putParcelableArrayListExtra("classes", classes);
                startActivity(calendarStarter);
                break;
            default:
                break;
        }
        return true;
    }
    //Simple code to read from file and convert to string, credit to stackOverflow for this one
    private String readFromFile(Context context)throws Exception {

        String ret = "";
        Log.e("?", "?");
        InputStream inputStream = context.openFileInput("config.txt");

        if ( inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        }


        return ret;
    }
    //Simple method to write to a file named config

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //On Pause, assume the device is closing. When a pause occurs write the current class list
    //to a file
    protected void onStop(){
        Gson gson = new Gson();
        String toStore = gson.toJson(classes);
        writeToFile(toStore, this);
        super.onStop();
    }

    //On resume restore the app from the file  UNLESS an activity just returned!!
    protected void onStart(){
        Gson gson = new Gson();
        //Dont read from file if an activity returned
        //This will cause overwrites from the file instead of adding to the array what the activity actually returns
        if(!activityReturned) {
            //Enter catch if the config file is lost or distorted
            try {
                String json = readFromFile(this);
                //Make sure there is json, if not just make a new classlist
                if (json.length() > 0)
                    classes = gson.fromJson(json, new TypeToken<ArrayList<Class>>() {
                    }.getType());
                else
                    classes = new ArrayList<Class>();
            } catch (Exception E) {

                classes = new ArrayList<Class>();

            }

            set = new ClassAdapter(classes, this);
            r.setAdapter(set); //sets the adapter so that it can be updated when new classes are added.
        } else {
            //if the activity just returned, no need to read from the file, just unset the flag
            activityReturned = false;
        }
        super.onStart();

    }

    @Override
    public void setWork(ArrayList<Work> toSet, int index) {
        classes.get(index).setWork(new ArrayList<Work>(toSet));
        //Log.i("wat", "wat");
        int finishedCount = 0;
        for (int i = 0; i < toSet.size(); i++){
            finishedCount += toSet.get(i).getCompleted() ? 1 : 0;
        }
        if(finishedCount > 0) {
            classes.get(index).setProgress(100 * finishedCount / toSet.size());
        }
        else{
            classes.get(index).setProgress(0);
        }
        set.notifyItemChanged(index);
    }

    /**
     * adapter which handles the addition of courses to the recycler view.
     */
    public class ClassAdapter extends RecyclerView.Adapter<ClassHolder> {
        private ArrayList<Class> classes;
        View RecycleV;
        Context context;

        public ClassAdapter(ArrayList<Class> classes, Context context) {
            this.classes = classes;
            this.context = context;
        }

        @Override
        public ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecycleV = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_list_content, parent, false);
            return new ClassHolder(RecycleV);
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.class_list_content;
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        @Override
        public void onBindViewHolder(ClassHolder holder, final int position) {
            final Class c = classes.get(position);
            if (currPos == position) {
                holder.itemView.setBackgroundColor(Color.parseColor("#CAEBF2"));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.bind(classes.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                /**
                 * when a specific course is clicked (not just the text, works anywhere)
                 * change to 'textd' instead of 'itemView' if encountering problems.
                 */
                public void onClick(View v) {
                    if (mTwoPane) { // if fragmented (on tablets)
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("class", c);
                        arguments.putInt("index", position);
                        ClassDetailFragment fragment = new ClassDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.class_detail_container, fragment)
                                .commit();

                    } else { // if not fragmented, on phones, starts ClassDetailActivity
                        Intent sendIntent = new Intent(getApplicationContext(), ClassDetailActivity.class);
                        sendIntent.putExtra("class", c);
                        sendIntent.putExtra("index", position);
                        startActivityForResult(sendIntent, REQUEST_CODE_ADD_WORK);
                    }
                    notifyItemChanged(currPos);
                    currPos = position;
                    notifyItemChanged(currPos);
                }
            });
        }
    }

    /**
     * each individual holder in the recycler view and what it contains. Will contain class name,
     * progress bar for individual classes.
     */
    public class ClassHolder extends RecyclerView.ViewHolder {
        private TextView textd;
        private ProgressBar progress;
        private ImageView alert;

        public Class classData;

        public ClassHolder(View view) {
            super(view);
            textd = (TextView) itemView.findViewById(R.id.className);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            alert = (ImageView)itemView.findViewById(R.id.alertStatus);
        }

        public void bind(Class option) {
            this.classData = option;
            textd.setText(classData.getClassName());
            progress.setProgress(classData.getProgress()); //currently displays as 0%
            checkClass();
        }

        public void checkClass(){
            ArrayList<Work> w = classData.getWork();
            Calendar today = Calendar.getInstance();
            boolean fail = false;
            for(Work temp: w) {
                Log.e("hit", temp.getDay() + " " + today.getTime().getDate() + " " +  today.getTime().getMonth() + " " + temp.getMonth() + " " + today.get(Calendar.YEAR) + " "+ temp.getYear());
                int year = today.get(Calendar.YEAR);
                if(!temp.getCompleted() && temp.getDay() - today.getTime().getDate() <= 1 && temp.getDay() - today.getTime().getDate() >= 0 && today.getTime().getMonth() == temp.getMonth() && year ==  (temp.getYear()))
                    fail = true;
            }
            if(fail) {
                Log.e("hit", "checkclass did fail");

                alert.setVisibility(View.VISIBLE);
            }else {
                alert.setVisibility(View.INVISIBLE);
            }
        }
    }
}