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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import goride.com.goridedriver.R;
import goride.com.goridedriver.entities.Ride;
import goride.com.goridedriver.listviews.MyAdapterHistory;
import goride.com.goridedriver.listviews.RecyclerViewHistory;
import goride.com.goridedriver.util.L;
import goride.com.goridedriver.util.MemoryManager;

/**
 * Created by User on 9/15/2017.
 */

public class FragmentHistory extends android.support.v4.app.Fragment {

    @BindView(R.id.history_recycler_view)
    RecyclerView mRecyclerView;

    private MyAdapterHistory adapterHistory;
    private List<Ride> rideHistories = new ArrayList<>();
    private Query mQuery;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mRecyclerView.setFocusable(false);

        mQuery = FirebaseDatabase.getInstance().getReference("ride_requests")
                .orderByChild("driverID").equalTo("08130101049");
        mQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null) {

                    L.fine("data ==> " + dataSnapshot);
                    Ride ride = dataSnapshot.getValue(Ride.class);
                    rideHistories.add(ride);
                    if (adapterHistory != null)
                        adapterHistory.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null) {
                    L.fine("data Changed ==> " + dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    L.fine("data Removed ==> " + dataSnapshot);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null) {
                    L.fine("data Moved ==> " + dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.fine("Error ==> " + databaseError.getDetails());
            }
        });

        adapterHistory = new MyAdapterHistory(getActivity(), rideHistories);
        mRecyclerView.setAdapter(adapterHistory);

        LinearLayoutManager historyLinearLayoutManager = new LinearLayoutManager(getActivity());
        historyLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(historyLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
