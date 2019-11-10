package com.example.dell.mychatcool;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class messagesFragment extends Fragment {
    private View myView;
    private RecyclerView mChatList;
    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;
    String onlineUserId;

    public messagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.fragment_messages, container, false);
        mChatList=(RecyclerView) myView.findViewById(R.id.chat_list);
        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        FriendsReference= FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUserId);
        UsersReference=FirebaseDatabase.getInstance().getReference().child("users");
        mChatList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mChatList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats,messagesFragment.ChatsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Chats,ChatsViewHolder>(
                        Chats.class,
                        R.layout.allfriends,
                        messagesFragment.ChatsViewHolder.class,
                        FriendsReference



                ) {
                    @Override
                    protected void populateViewHolder(final messagesFragment.ChatsViewHolder viewHolder, Chats model, int position) {

                        final String list_user_id=getRef(position).getKey();

                        UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final String username=dataSnapshot.child("username").getValue().toString();
                                final String userthumb=dataSnapshot.child("userthumbimage").getValue().toString();
                                String UserStatus=dataSnapshot.child("userstatus").getValue().toString();
                                //get if the user is online

                                if(dataSnapshot.hasChild("online")){
                                    String onlineStatus=(String) dataSnapshot.child("online").getValue().toString();
                                    viewHolder.setUserOnline(onlineStatus);
                                }

                                viewHolder.setUserName(username);
                                viewHolder.setThumbImage(getContext(),userthumb);
                                viewHolder.setUserStatus(UserStatus);
                                //
                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if(dataSnapshot.child("online").exists()) {
                                            Intent messageIntent = new Intent(getContext(), MessageActivity.class);
                                            messageIntent.putExtra("selected_user_id", list_user_id);
                                            messageIntent.putExtra("username", username);
                                            //messageIntent.putExtra("userimage",userthumb);
                                            startActivity(messageIntent);
                                        }else{
                                            UsersReference.child(list_user_id).child("online")
                                                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Intent messageIntent = new Intent(getContext(), MessageActivity.class);
                                                    messageIntent.putExtra("selected_user_id", list_user_id);
                                                    messageIntent.putExtra("username", username);
                                                    startActivity(messageIntent);
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
        mChatList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }


        public  void setUserName(String username){
            TextView Uname=(TextView) mView.findViewById(R.id.friendusername);
            Uname.setText(username);


        }
        public  void setThumbImage(final Context ctx, final String thumbImage){
            final CircleImageView thumImg=(CircleImageView) mView.findViewById(R.id.friendprofile);
            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.avatar).
                    into(thumImg, new Callback() {
                        //this is used if it fails to load image offline

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.avatar).into(thumImg);
                        }
                    });
        }

        public void setUserOnline(String onlineStatus) {
            ImageView onlineStatusView=(ImageView) mView.findViewById(R.id.onlineStatus);
            if(onlineStatus.equals("true")){
                onlineStatusView.setVisibility(View.VISIBLE);
            }
            else{
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }
        public void setUserStatus(String userStatus) {

            TextView userstatus=(TextView)mView.findViewById(R.id.friendshipdate);
            userstatus.setText(userStatus);
        }


    }
}
