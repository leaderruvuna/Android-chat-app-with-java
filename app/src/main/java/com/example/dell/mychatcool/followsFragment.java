package com.example.dell.mychatcool;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
public class followsFragment extends Fragment {
    private RecyclerView myRequestList;
    private View myView;
    private FirebaseAuth mAuth;
    private DatabaseReference FriendRequestRef;
    String online_user_id;
    private DatabaseReference UserReference;
    private DatabaseReference friendsDataRef;
    private DatabaseReference friendReq;

    public followsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView=inflater.inflate(R.layout.fragment_follows, container, false);

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        FriendRequestRef=FirebaseDatabase.getInstance().getReference().child("FollowFriends").child(online_user_id);
        friendsDataRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        friendReq=FirebaseDatabase.getInstance().getReference().child("FollowFriends");
        UserReference=FirebaseDatabase.getInstance().getReference().child("users");
        myRequestList=(RecyclerView) myView.findViewById(R.id.followRequestList);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return myView;
    }
    @Override
    public void onStart() {
        super.onStart();

     FirebaseRecyclerAdapter<request,requestViewHolder> firebaseRecyclerAdapter=new
                FirebaseRecyclerAdapter<request, requestViewHolder>(
                      request.class,
                      R.layout.follow_request_layout,
                      followsFragment.requestViewHolder.class,
                        FriendRequestRef



                   ){
                    @Override
                    protected void populateViewHolder(final requestViewHolder viewHolder, request model, int position) {
                       final String list_users_id=getRef(position).getKey();

                       DatabaseReference get_type_reference=getRef(position).child("request_type").getRef();

                       get_type_reference.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    //checks wether the request type is received to display,otherwise it does not display
                                    String requesType=dataSnapshot.getValue().toString();
                                    if(requesType.equals("received")){

                                        UserReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //Toast.makeText(getContext(),"friend request",Toast.LENGTH_SHORT).show();
                                                final String username=dataSnapshot.child("username").getValue().toString();
                                                final String userthumb=dataSnapshot.child("userthumbimage").getValue().toString();
                                                final String UserStatus=dataSnapshot.child("userstatus").getValue().toString();
                                                viewHolder.setUsername(username);
                                                viewHolder.setStatus(UserStatus);
                                                viewHolder.setUserThumbImage(getContext(),userthumb);

                                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[]=new CharSequence[]{
                                                                "Accept request"," Cancell request"


                                                        };
                                                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
                                                        alertDialog.setTitle("Request Choice");
                                                        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int position) {
                                                                if(position==0){


                                                                    Calendar calForDate=null;
                                                                    SimpleDateFormat currentDate=null;
                                                                    final String saveCurrentDate;
                                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                        calForDate= Calendar.getInstance();
                                                                    }
                                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                        currentDate=new SimpleDateFormat("dd-MMMM-YYYY");
                                                                    }
                                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                        saveCurrentDate=currentDate.format(calForDate.getTime());
                                                                        friendsDataRef.child(online_user_id).child(list_users_id).child("date").setValue(saveCurrentDate)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            friendsDataRef.child(list_users_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            //cancell as well user request,the same as cancel,becaue when you're accepted the request is canceled

                                                                                                            friendReq.child(online_user_id).child(list_users_id).removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            friendReq.child(list_users_id).child(online_user_id).removeValue()
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if(task.isSuccessful()){

                                                                                                                                                Toast.makeText(getContext()," request accepted",Toast.LENGTH_LONG).show();



                                                                                                                                            }

                                                                                                                                        }
                                                                                                                                    });

                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }





                                                                }
                                                                if(position==1){


                                                                    friendReq.child(online_user_id).child(list_users_id).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    friendReq.child(list_users_id).child(online_user_id).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if(task.isSuccessful()){

                                                                                                        Toast.makeText(getContext(),"request cancelled !",Toast.LENGTH_SHORT).show();

                                                                                                    }

                                                                                                }
                                                                                            });

                                                                                }
                                                                            });


                                                                }
                                                            }
                                                        });
                                                        alertDialog.show();



                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });




                                    }
                                    else if(requesType.equals("sent")){
                                        Button request_sent_button=(Button) myView.findViewById(R.id.accepteRequest);
                                        request_sent_button.setText("Request sent");
                                        Button request_cancell=(Button) myView.findViewById(R.id. reqDecline);
                                        request_cancell.setVisibility(View.INVISIBLE);


                                        UserReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //Toast.makeText(getContext(),"friend request",Toast.LENGTH_SHORT).show();
                                                final String username=dataSnapshot.child("username").getValue().toString();
                                                final String userthumb=dataSnapshot.child("userthumbimage").getValue().toString();
                                                final String UserStatus=dataSnapshot.child("userstatus").getValue().toString();
                                                viewHolder.setUsername(username);
                                                viewHolder.setStatus(UserStatus);
                                                viewHolder.setUserThumbImage(getContext(),userthumb);

                                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[]=new CharSequence[]{
                                                                "Cancel Request"


                                                        };
                                                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
                                                        alertDialog.setTitle("Request sent");
                                                        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int position) {

                                                                if(position==0){


                                                                    friendReq.child(online_user_id).child(list_users_id).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    friendReq.child(list_users_id).child(online_user_id).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if(task.isSuccessful()){

                                                                                                        Toast.makeText(getContext(),"request cancelled !",Toast.LENGTH_SHORT).show();

                                                                                                    }

                                                                                                }
                                                                                            });

                                                                                }
                                                                            });


                                                                }
                                                            }
                                                        });
                                                        alertDialog.show();




                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                }
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });





                    }
                };
        myRequestList.setAdapter(firebaseRecyclerAdapter);



    }
    public static class requestViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        public requestViewHolder(View itemView) {

            super(itemView);
            mView=itemView;
        }

        public void setUsername(String username) {
            TextView Uname=(TextView) mView.findViewById(R.id.requestUsername);
            Uname.setText(username);
        }

        public void setStatus(String userStatus) {
            TextView Ustatus=(TextView) mView.findViewById(R.id.requestStatus);
            Ustatus.setText(userStatus);
        }

        public void setUserThumbImage(final Context ctx, final String userthumb) {
            final CircleImageView thumImg=(CircleImageView) mView.findViewById(R.id.requestUimage);
            Picasso.with(ctx).load(userthumb).networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.avatar).
                    into(thumImg, new Callback() {

                        //this is used if it fails to load image offline

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(userthumb).placeholder(R.drawable.avatar).into(thumImg);
                        }
                    });


        }

    }


}
