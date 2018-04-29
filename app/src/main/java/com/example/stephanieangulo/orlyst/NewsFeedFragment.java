package com.example.stephanieangulo.orlyst;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "NewsFeedFragment";

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private TextView numItemsFound;

    private Context mContext;
    private NewsFeedAdapter mAdapter;
    private FirebaseAuth mAuth;
    private List<Item> mItems = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();


    public NewsFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mContext = getContext();
        mAuth = AppData.firebaseAuth;
        searchEditText = view.findViewById(R.id.search);
        numItemsFound = view.findViewById(R.id.num_items);
        recyclerView = view.findViewById(R.id.feed_recycler_view);

        mUsers = fetchUsers();

        mAdapter = new NewsFeedAdapter(getActivity(), mItems);

        setUpRecyclerView();
        onNewsFeedItemClick();
        addTextListeners();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
    private void addTextListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void onNewsFeedItemClick() {
        recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                new MyRecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Item selectedItem = mItems.get(position);
                        String sellerID = selectedItem.getSellerID();
                        User selectedUser = null;
                        for(User user: mUsers) {
                            if(user.getUserID().equals(sellerID)) {
                                selectedUser = user;
                                break;
                            }
                        }
                        Intent itemIntent = new Intent(getActivity(), ItemDetailActivity.class);
                        itemIntent.putExtra("Item", Parcels.wrap(selectedItem));
                        itemIntent.putExtra("User", Parcels.wrap(selectedUser));
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, itemIntent, 0);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "newsfeed");
                        builder.setContentIntent(pendingIntent);
                        startActivityForResult(itemIntent, 1);
                    }
                }));
    }

    private List<User> fetchUsers() {
        final List<User> users = new ArrayList<>();
        final DatabaseReference userRef = AppData.firebaseDatabase.getReference("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot data : dataSnapshots) {
                    String key = data.getKey();
                    DataSnapshot a = dataSnapshot.child(key);
                    User user = a.getValue(User.class);
                    users.add(user);
                    Log.d(TAG, "item list size " + user.getItems().values().size());
                    for(Item item: user.getItems().values()) {
                        List<String> keys = getAllItemKeys(mItems);
                        if(!keys.contains(item.getKey())) {
                            Log.d(TAG, "getting item " + item.getItemName());
                            mItems.add(item);
                            // TODO: add booleans on whether or not item is in watchlist
                            fetchImage(item);
                        }
                    }
                    Log.d(TAG, "hello " + user.getFirst());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });
        return users;
    }
    private void fetchImage(Item item) {
        final Item itemWithImage = item;
        StorageReference storageRef = AppData.firebaseStorage.getReference();
        StorageReference pathRef = storageRef.child("images/");
        StorageReference imageRef = pathRef.child(item.getKey());
        final long LIMIT = 512 * 512;
        imageRef.getBytes(LIMIT).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                itemWithImage.setBytes(bytes);
                mAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private List<String> getAllItemKeys(List<Item> items) {
        List<String> keys = new ArrayList<>();
        for(Item item: items) {
            keys.add(item.getKey());
        }
        return keys;
    }


}
