package goride.com.goridedriver.listviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import goride.com.goridedriver.R;


/**
 * Created by User on 5/19/2017.
 */

public class MyAdapterHistory extends RecyclerView.Adapter<MyAdapterHistory.MyViewHolder>{

    private List<RecyclerViewHistory> objectList;
    private LayoutInflater inflater;

    public MyAdapterHistory(Context context, List<RecyclerViewHistory> objectList){

        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_history, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RecyclerViewHistory current = objectList.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private int position;
       // private ImageView imageItem;
        private TextView trip_price;
        private TextView trip_date;
        private TextView trip_from;
        private TextView trip_to;

        private RecyclerViewHistory currentObject;

        public MyViewHolder(View itemView) {
            super(itemView);

            trip_price = (TextView) itemView.findViewById(R.id.trip_fare);
            trip_date = (TextView) itemView.findViewById(R.id.trip_date);
            trip_from = (TextView) itemView.findViewById(R.id.trip_from);
           trip_to = (TextView) itemView.findViewById(R.id.trip_to);
        }

        public void setData(RecyclerViewHistory currentObject, int position) {

           // this.imageItem.setImageResource(currentObject.getItemImage());
            this.trip_price.setText(currentObject.gettripPrice());
            this.trip_date.setText(currentObject.gettripDate());
            this.trip_from.setText(currentObject.getTripFrom());
            this.trip_to.setText(currentObject.getTripTo());
            this.position = position;
            this.currentObject = currentObject;


        }
    }
}
