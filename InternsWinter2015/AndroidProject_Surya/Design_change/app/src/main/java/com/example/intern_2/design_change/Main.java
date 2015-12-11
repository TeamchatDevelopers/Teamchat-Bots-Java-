package com.example.intern_2.design_change;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.webaroo.teamchatsdk.sdk.Teamchat;

/**
 * Created by Intern-2 on 12/10/2015.
 */
public class Main extends AppCompatActivity {
    static final String HOST_URL = "http://enterprise.teamchat.com/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Teamchat.initializeWithAppID("appID", new Teamchat.TeamchatInitializationWithAppIDCompletionHandler() {
                    @Override
                    public void onTeamchatInitializationWithAppIDCompletion(boolean success, String message) {
                        if (success) {
                            Teamchat.setHostURL(HOST_URL, Main.this);

                            if (!Teamchat.hasActiveSession(Main.this))
                            {

                                Teamchat.login(Main.this, new Teamchat.LoginCompletionHandler() {
                                    @Override
                                    public void onLoginCompletion(boolean success, String message) {
                                        if (success) {
                                            Teamchat.initWithCompletionHandler(new Teamchat.TeamchatStartCompletionHandler() {
                                                                                   @Override
                                                                                   public void onTeamchatStartCompletionHandler(boolean success, String error, String messaeg) {
                                                                                       if (success) {
                                                                                           Teamchat.showRoomList(Main.this);  //Success
                                                                                       } else {
                                                                                           //Failure while initializing Teamchat.
                                                                                       }
                                                                                   }
                                                                               }, Main.this
                                                        ); //Success
                                            }
                                               else {
                                            //Failure while logging into Teamchat.
                                        }
                                    }
                                });



                            }
                            else
                            {
                        //        setContentView(R.layout.content_splash_screen);
                                //Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);

                                //toolbar2.setTitle("CHATS");
                                //setSupportActionBar(toolbar2);
                                Teamchat.showRoomList(Main.this);
                                getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                               // getWindow().setStatusBarColor(Color.TRANSPARENT);
                               //Teamchat.setTableViewSeparatorColor();
                    //            super.onbackpressed();

                            }
                            //success
                        } else {
                            //error
                        }
                    }

                },Main.this
        );



    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
