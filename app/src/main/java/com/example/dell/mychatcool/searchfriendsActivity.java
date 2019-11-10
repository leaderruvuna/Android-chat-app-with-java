package com.example.dell.mychatcool;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchfriendsActivity extends AppCompatActivity {
    private RecyclerView myUserRecycleList;
    private DatabaseReference get_AllUser_Reference;
    private ImageButton search_friends_button;
    private EditText search_input;


    //private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfriends);

        myUserRecycleList=(RecyclerView)findViewById(R.id.myrecycler);
        myUserRecycleList.setHasFixedSize(true);
        myUserRecycleList.setLayoutManager(new LinearLayoutManager(this));
        get_AllUser_Reference= FirebaseDatabase.getInstance().getReference().child("users");
        //offline work enabling
        get_AllUser_Reference.keepSynced(true);
        search_friends_button=(ImageButton) findViewById(R.id.search_button);
        search_input=(EditText) findViewById(R.id.search_more_friend_input);
        //on text change



        /*search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                search_input_text=search_input.getText().toString();
                if(!search_input_text.isEmpty()) {
                    searchFriends(search_input_text);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/




    }

    private void searchFriends(String search_friends) {
        Query searchFriendsQuery=get_AllUser_Reference.orderByChild("username").startAt(search_friends)
                .endAt(search_friends +"\uf8ff");

        FirebaseRecyclerAdapter<search_all_users,searchfriendsActivity.SearchAllUserViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<search_all_users, searchfriendsActivity.SearchAllUserViewHolder>
                (
                        search_all_users.class,
                        R.layout.search_all_users,
                        SearchAllUserViewHolder.class,
                        searchFriendsQuery
                )



        {
            @Override
            protected void populateViewHolder(searchfriendsActivity.SearchAllUserViewHolder viewHolder, search_all_users model, final int position) {
                //setting values that are got from the getter methods
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUserstatus(model.getUserstatus());
                viewHolder.setUserthumbimage(getApplicationContext(),model.getUserthumbimage());
                //when you click on a user account displayed in the recycler view
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //first get the position and then the key ,this means the user unique id on firebase
                        String selected_user_id=getRef(position).getKey();
                        Intent profileIntent=new Intent(searchfriendsActivity.this,FriendProfileActivity.class);
                        profileIntent.putExtra("selected_user_id",selected_user_id);
                        startActivity(profileIntent);


                    }
                });

            }
        };
        //set the recycleview list adapter,it is called now
        myUserRecycleList.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    public void onStart() {
        super.onStart();
        search_friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()==true) {
                    String search_input_text = search_input.getText().toString();
                    searchFriends(search_input_text);
                }else{
                    Toast.makeText(searchfriendsActivity.this,"error:check your connection",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isNetworkAvailable()==true) {
                    String search_input_text = search_input.getText().toString();
                    if (!search_input_text.isEmpty()) {
                        searchFriends(search_input_text);
                    }
                }
                else{
                    Toast.makeText(searchfriendsActivity.this,"error:check your connection",Toast.LENGTH_LONG)
                            .show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //firebase recycle adapter
        //it has four parameters:the class for setter and getter users,the layout for users,the holder class for,
        //users and then the database reference of the data stored in firebase
        /*Query searchFriendsQuery=get_AllUser_Reference.orderByChild("username").startAt(search_input_text)
                .endAt(search_input_text +"\uf8ff");

        FirebaseRecyclerAdapter<search_all_users,searchfriendsActivity.SearchAllUserViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<search_all_users, searchfriendsActivity.SearchAllUserViewHolder>
                (
                        search_all_users.class,
                        R.layout.search_all_users,
                        SearchAllUserViewHolder.class,
                        searchFriendsQuery
                )



        {
            @Override
            protected void populateViewHolder(searchfriendsActivity.SearchAllUserViewHolder viewHolder, search_all_users model, final int position) {
                //setting values that are got from the getter methods
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUserstatus(model.getUserstatus());
                viewHolder.setUserthumbimage(getApplicationContext(),model.getUserthumbimage());
                //when you click on a user account displayed in the recycler view
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //first get the position and then the key ,this means the user unique id on firebase
                       String selected_user_id=getRef(position).getKey();
                       Intent profileIntent=new Intent(searchfriendsActivity.this,FriendProfileActivity.class);
                       profileIntent.putExtra("selected_user_id",selected_user_id);
                       startActivity(profileIntent);


                    }
                });

            }
        };
        //set the recycleview list adapter,it is called now
        myUserRecycleList.setAdapter(firebaseRecyclerAdapter);

    */
    }

//this is the class holder set the usernames,imges and the status of all users
public static class SearchAllUserViewHolder extends RecyclerView.ViewHolder{
    View mView;
    public SearchAllUserViewHolder(View itemView) {

        super(itemView);
        mView=itemView;

    }
    public void setUsername(String username){

            TextView Uname = (TextView) mView.findViewById(R.id.username_for_search_friend);
            Uname.setText(username);


    }
    public void setUserstatus(String userstatus){
        TextView Ustatus=(TextView) mView.findViewById(R.id.status_for_search_friend);
        Ustatus.setText(userstatus);

    }
    public void setUserthumbimage(final Context ctx, final String userthumbimage){
        final CircleImageView thumbimage=(CircleImageView) mView.findViewById(R.id.pofile_image_for_search_friend);
        if(!userthumbimage.equals("default_profile_picture")) {
            //Picasso.with(ctx).load(userthumbimage).placeholder(R.drawable.avatar).into(thumbimage);
            //network policy is used for offline
            //networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.with(ctx).load(userthumbimage).networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.avatar).
                    into(thumbimage, new Callback() {
                //this is used if it fails to load image offline

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(userthumbimage).placeholder(R.drawable.avatar).into(thumbimage);
                }
            });
        }

    }

}
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}