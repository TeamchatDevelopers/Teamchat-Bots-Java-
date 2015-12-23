package com.teamchat.intern.msu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.webaroo.teamchatsdk.sdk.Teamchat;

/**
 * Created by Intern-2 on 12/16/2015.
 */
public class MainActivity extends AppCompatActivity{
    static final String HOST_URL = "http://enterprise.teamchat.com/";
    TextView load;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        load =(TextView) findViewById(R.id.Loading);
        Log.d("MSUAppLog","inside main activity");

        Teamchat.initializeWithAppID("56740f35e4b0b9734bc9c2df", "e96d856928e646c1c689da9e484a1dc8", MainActivity.this, new Teamchat.TeamchatCompletionHandler() {
            @Override
            public void onTeamchatCompletion(boolean b, String s, String s1) {
                if (b) {
                    Log.d("MSUAppLog", "initialization succeded");
                    Teamchat.setHostURL(HOST_URL, MainActivity.this);
                //    finish();
                    if (!Teamchat.hasActiveSession(MainActivity.this)) {
                        Teamchat.login(MainActivity.this, new Teamchat.LoginCompletionHandler() {
                            @Override
                            public void onLoginCompletion(boolean b, String s) {
                                if (b) {
                                    load.setText("Please wait,\r\n   Loading Rooms ...");
                                    Log.d("MSUAppLog", "login");
                                    Teamchat.initWithCompletionHandler(new Teamchat.TeamchatStartCompletionHandler() {
                                                                           @Override
                                                                           public void onTeamchatStartCompletionHandler(boolean b, String s, String s1) {
                                                                              //load.setText("Loading Rooms ..");
                                                                               if (b) {


                                                                                   try {
                                                                                       Teamchat.showRoomList(MainActivity.this);  //Success
                                                                                       finish();
                                                                                   }
                                                                                   catch(Exception e)
                                                                                   {
                                                                                    load.setText("Error Loading Rooms,\r\n Please Try Again");
                                                                                   }

                                                                                   // finish();
                                                                               } else {
                                                                                   load.setText("Loading Rooms failed,\r\n Please Try Again");//Failure while initializing Teamchat.
                                                                               }
                                                                           }
                                                                       }, MainActivity.this
                                    ); //Success
                                } else {
                                    Log.d("MSUAppLog", "login failed");
                                 load.setText("Login failed,\r\n Please Try Again ");   //Failure while logging into Teamchat.
                                }
                            }
                        });


                    } else {
                        Teamchat.showRoomList(MainActivity.this);
                        finish();
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                    }
                    //success
                } else {
                    //error
                    load.setText("Some Problem Occurred, \r\n Please Try Again");
                    Log.d("MSUAppLog", "initialization failed");
                }
            }
        });



    }

}
