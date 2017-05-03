package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kian on 11/26/2016.
 */

public class CalendarSelectedDayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_click_view);
        RecyclerView rv = (RecyclerView) findViewById(R.id.calendarlist);
        ArrayList<Work>toAdd = getIntent().getExtras().getParcelableArrayList("toAdd");

        ArrayList<String>temp = new ArrayList<String>();

//        for(int i = 0; i < toAdd.size(); i++) {
//            temp.add(toAdd.get(i).getToDo());
//        }
//        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.work_on_day_item, temp);
        CalendarSelectedAdapter aa = new CalendarSelectedAdapter(toAdd,this);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv.setAdapter(aa);
        aa.notifyDataSetChanged();




    }

    /**
     * adapter which handles the addition of courses to the recycler view.
     */
    protected class CalendarSelectedAdapter extends RecyclerView.Adapter<CalendarSelectedDayActivity.ClassHolder2> {
        private ArrayList<Work> work;
        View RecycleV;
        Context context;

        public CalendarSelectedAdapter(ArrayList<Work> work, Context context) {
            this.work = work;
            this.context = context;
        }

        @Override
        public CalendarSelectedDayActivity.ClassHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            RecycleV = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_click_view_item, parent, false);
            return new ClassHolder2(RecycleV);
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.class_list_content;
        }

        @Override
        public int getItemCount() {
            return work.size();
        }

        @Override
        public void onBindViewHolder(CalendarSelectedDayActivity.ClassHolder2 holder, final int position) {
            holder.bind(work.get(position));
        }
    }

    /**
     * each individual holder in the recycler view and what it contains. Will contain class name,
     * progress bar for individual classes.
     */
    public class ClassHolder2 extends RecyclerView.ViewHolder {
        private TextView textd;
        private CheckBox cbox;

        private Work work;

        public ClassHolder2(View view) {
            super(view);
            textd = (TextView) itemView.findViewById(R.id.calendarwork);
            cbox = (CheckBox)itemView.findViewById(R.id.calendarcomplete);
        }

        public void bind(Work work) {
            this.work = work;
            textd.setText(work.getToDo());
            cbox.setChecked(work.getCompleted());


        }

        public Work getWork(){
            return work;
        }
    }
}
