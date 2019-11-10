package com.example.dell.mychatcool;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.AudioTimestamp;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 4/10/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    public MessageAdapter(List<Messages> userMessageList){

        this.userMessageList=userMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View V= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_message_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();

        return new MessageViewHolder(V);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        String messageSenderId=mAuth.getCurrentUser().getUid();
        //messages for getting the user position on recyclerview
        Messages messages= userMessageList.get(position);
        String FromUserId=messages.getFrom();
        String FromMessageType=messages.getType();
        long MessageTime=messages.getTime();




        usersReference= FirebaseDatabase.getInstance().getReference().child("users").child(FromUserId);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child("username").getValue().toString();
                String userimage=dataSnapshot.child("userthumbimage").getValue().toString();
                Picasso.with(holder.messageUserprofile.getContext()).load(userimage).placeholder(R.drawable.avatar)
                        .into(holder.messageUserprofile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(FromMessageType.equals("text")){
             holder.imageMessage.setVisibility(View.INVISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
            if(messageSenderId.equals(FromUserId)){
                holder.messageText.setBackgroundResource(R.drawable.messagelayoutstyle2);
                holder.messageText.setTextColor(Color.parseColor("#FF7AAEF5"));
                holder.messageText.setMaxWidth(400);
                holder.messageTime.setTextColor(Color.parseColor("#797979"));
                holder.messageTime.setTranslationX(160);
                holder.messageText.setPadding(0,20,30,20);
                holder.messageText.setTranslationX(220);
                holder.messageUserprofile.setVisibility(View.INVISIBLE);

            }else{
                holder.messageText.setBackgroundResource(R.drawable.messagelayoutstyle);
                holder.messageText.setPadding(30,20,0,20);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageTime.setTextColor(Color.BLACK);
                holder.messageTime.setTranslationX(5);
                holder.messageText.setMaxWidth(400);
                holder.messageUserprofile.setX(5);
                holder.messageText.setTranslationX(30);
                holder.messageUserprofile.setVisibility(View.VISIBLE);
            }
            //set the messages and the time of submittion of the message
            holder.messageTime.setText(getDate(MessageTime));
            holder.messageText.setText(messages.getMessage());
        }else{
            holder.imageMessage.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.INVISIBLE);

            if(messageSenderId.equals(FromUserId)) {
                holder.messageUserprofile.setVisibility(View.INVISIBLE);
                holder.messageText.setBackgroundResource(R.drawable.imagemessagestroke);
                holder.messageTime.setTranslationX(370);
                holder.messageUserprofile.setVisibility(View.INVISIBLE);
                holder.imageMessage.setTranslationX(220);
                holder.messageText.setPadding(0, 0, 0, 0);
                holder.imageMessage.setMaxWidth(300);
                holder.imageMessage.setMaxHeight(400);
                holder.messageTime.setTextColor(Color.BLACK);
                holder.messageTime.setText(getDate(MessageTime));
                holder.messageUserprofile.setVisibility(View.INVISIBLE);
                Picasso.with(holder.messageUserprofile.getContext()).load(messages.getMessage()).
                        placeholder(R.drawable.avatar).into(holder.imageMessage);


            }else{
                holder.messageUserprofile.setVisibility(View.VISIBLE);
                holder.messageText.setBackgroundResource(R.drawable.imagemessagestroke);
                holder.messageTime.setTranslationX(-30);
                holder.messageUserprofile.setVisibility(View.VISIBLE);
                holder.imageMessage.setTranslationX(30);
                holder.messageText.setPadding(0, 0, 0, 0);
                holder.imageMessage.setMaxWidth(300);
                holder.imageMessage.setMaxHeight(400);
                holder.messageTime.setTextColor(Color.parseColor("#797979"));

                holder.messageTime.setText(getDate(MessageTime));
                holder.messageUserprofile.setVisibility(View.INVISIBLE);
                Picasso.with(holder.messageUserprofile.getContext()).load(messages.getMessage()).
                        placeholder(R.drawable.avatar).into(holder.imageMessage);

            }


        }


    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView messageTime;
        public TextView messageText2;
        public CircleImageView messageUserprofile;
        public ImageView imageMessage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.userMessage);
            messageUserprofile=(CircleImageView)view.findViewById(R.id.chat_prof_image);
            messageTime=(TextView)view.findViewById(R.id.messageTime);
            imageMessage=(ImageView) view.findViewById(R.id.imageMessage);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        //"yyyy-MM-dd HH:mm:ss"
        String date = DateFormat.format("HH:mm",cal.getTime()).toString();
        return date;
    }
}
