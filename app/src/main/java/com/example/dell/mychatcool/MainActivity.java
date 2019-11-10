package com.example.dell.mychatcool;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
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

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mToolbar;
    //used for tabs
    private TabLayout tablayout;
    private ClipData.Item AddFriends;
    //this is an none primitive class that is used to extend the main tabs adapter for fragment
    private TabsPagerAdapter TheMaintabsPagerAdapter;
    //bottom navigation bar
    private BottomNavigationView myBottomNavbar;
    //drawer
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myDrawerToggle;
    private NavigationView myNavView;
    private DatabaseReference getUserData;
    //drawer username and profile image

    public CircleImageView ProfileImageForDrawer;
    public TextView DrawerUsername;
    String onlineUser_Id;

    FirebaseUser currentUser;

    private DatabaseReference user_reference;
    private FloatingActionButton myFloatingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                handleUncaughtException(thread,throwable);
            }
        });
        */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        //listener to check if the mauth is null

        /*mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent startIntentPage=new Intent(MainActivity.this,LoginAndSignPageUpActivity.class);
                    //this line prevent the user to go back to the main activity
                    startIntentPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startIntentPage);
                }
            }
        };*/


        currentUser=mAuth.getCurrentUser();
        if(currentUser !=null){
            String OnlineUserId=mAuth.getCurrentUser().getUid();
            user_reference=FirebaseDatabase.getInstance().getReference().child("users").child(OnlineUserId);
        }


        //declaring the view pager and then using it for to handdle the fragments

        final ViewPager  myViewpager=(ViewPager)findViewById(R.id.viewpager);
        TheMaintabsPagerAdapter=new TabsPagerAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(TheMaintabsPagerAdapter);
        tablayout=(TabLayout)findViewById(R.id.main_activity_tabs);
        tablayout.setupWithViewPager(myViewpager);

        //this is our main toolbar named
        mToolbar=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChattCool");

        //drawer layout

        myDrawerLayout=(DrawerLayout)findViewById(R.id.myDrawerLayout);
        myDrawerToggle=new ActionBarDrawerToggle(this,myDrawerLayout, R.string.open, R.string.close);
        myDrawerLayout.addDrawerListener(myDrawerToggle);
        myDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myNavView=(NavigationView) findViewById(R.id.mynav);
        //when the user click one item from the drawer menu
        myNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.drawerlogout){
                    if(isNetworkAvailable()==true) {
                        if (currentUser != null) {
                            user_reference.child("online").setValue(ServerValue.TIMESTAMP);

                        }
                        mAuth.signOut();//problem to solve
                        userLogoutAction();

                    }else{
                        Toast.makeText(MainActivity.this,"error:check your connection",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                if(item.getItemId()==R.id.setting_button1){
                    Intent settingIntent=new Intent(MainActivity.this,mySettingsAcitivity.class);
                    startActivity(settingIntent);
                }
                /*if(item.getItemId()==R.id.drawersearch){
                    Intent searchIntent=new Intent(MainActivity.this,searchfriendsActivity.class);
                    startActivity(searchIntent);
                }*/
                return true;
            }
        });
        //user reference


        //get user name and the profile
        //mAuth=FirebaseAuth.getInstance();
        //catcth exception of null pointer exception
        try {
            onlineUser_Id = mAuth.getCurrentUser().getUid();
            getUserData= FirebaseDatabase.getInstance().getReference().child("users").child(onlineUser_Id);




                        //instatiating the profile image and username from the drawer header
                        ProfileImageForDrawer= (CircleImageView) myNavView.getHeaderView(0).findViewById(R.id.drawerProfileImage);
                        DrawerUsername= (TextView) myNavView.getHeaderView(0).findViewById(R.id.drawerUsername);
                        getUserData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String username=dataSnapshot.child("username").getValue().toString();
                                String userstatus=dataSnapshot.child("userstatus").getValue().toString();
                                final String image=dataSnapshot.child("userimage").getValue().toString();
                                String thumbimage=dataSnapshot.child("userthumbimage").getValue().toString();

                                DrawerUsername.setText(username);

                                if(!image.equals("default_profile_picture")) {
                                    //Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.avatar).into(ProfileImageForDrawer);
                                    Picasso.with(MainActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).
                                            into(ProfileImageForDrawer, new Callback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError() {
                                                    Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.avatar).into(ProfileImageForDrawer);
                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //when you click on the image in the drawer header it automatically bring you the profile settings

                        ProfileImageForDrawer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent settingsIntent=new Intent(MainActivity.this,mySettingsAcitivity.class);
                                startActivity(settingsIntent);
                            }
                        });



                        //floating button

                        myFloatingButton=(FloatingActionButton) findViewById(R.id.myFloatingButton);
                        myFloatingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent searchIntent=new Intent(MainActivity.this,searchfriendsActivity.class);
                                startActivity(searchIntent);

                            }
                        });

            

        }catch (NullPointerException e){
            Intent startIntentPage=new Intent(MainActivity.this,LoginAndSignPageUpActivity.class);
            //this line prevent the user to go back to the main activity
            startIntentPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntentPage);

        }


    }


     private void userLogoutAction() {
        Intent startIntentPage=new Intent(MainActivity.this,LoginAndSignPageUpActivity.class);
        //this line prevent the user to go back to the main activity
        startIntentPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startIntentPage);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser=mAuth.getCurrentUser();

        if(currentUser==null){

            userLogoutAction();

        }
        else if(currentUser !=null){
            user_reference.child("online").setValue("true");

        }
    }
    //if the user minimize his app
    /*@Override
    protected void onStop() {
        super.onStop();
        if(currentUser !=null){
            user_reference.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }*/






    //method used for the main menu when you login

    //@Override
    //when the menu is created
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.our_main_menu,menu);
        return true;
    }

    //when an option is selected

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.Logout_button){
             mAuth.signOut();
             userLogoutAction();
         }
         if(item.getItemId()==R.id.setting_button){
             Intent settingIntent=new Intent(MainActivity.this,mySettingsAcitivity.class);
             startActivity(settingIntent);
         }
        if(item.getItemId()==R.id.allusers){
            Intent searchIntent=new Intent(MainActivity.this,searchfriendsActivity.class);
            startActivity(searchIntent);
        }
        if(myDrawerToggle.onOptionsItemSelected(item)){

            return true;
        }


        return true;
    }

    /*private void handleUncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        Intent intent=new Intent();
        intent.setAction("com.example.SEND_LOG");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }
    */

    //network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

//these blocks of codes are used to thow exceptions

/*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                handleUncaughtException(thread,throwable);
            }
        });*/



    /* */