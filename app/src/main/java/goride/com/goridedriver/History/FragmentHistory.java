package goride.com.goridedriver.History;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import goride.com.goridedriver.R;
import goride.com.goridedriver.listviews.MyAdapterHistory;
import goride.com.goridedriver.listviews.RecyclerViewHistory;

/**
 * Created by User on 9/15/2017.
 */

public class FragmentHistory extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.activity_history, container, false);
        //  final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);



        RecyclerView historyRecyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setFocusable(false);

        MyAdapterHistory myAdapter = new MyAdapterHistory(getActivity(), RecyclerViewHistory.getObjectList());
        historyRecyclerView.setAdapter(myAdapter);


        LinearLayoutManager historyLinearLayoutManager = new LinearLayoutManager(getActivity());
        historyLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyRecyclerView.setLayoutManager(historyLinearLayoutManager);



          /*
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanCount(2);
        showCaseRecyclerView.setLayoutManager(gridLayoutManager);

        */

        historyRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;


    }
}
