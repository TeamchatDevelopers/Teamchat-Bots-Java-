package com.teamchat.intern.msu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.webaroo.teamchatsdk.sdk.Teamchat;

/**
 * Created by Intern-2 on 12/16/2015.
 */
public class MainActivity extends AppCompatActivity{
    static final String HOST_URL = "http://enterprise.teamchat.com/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Teamchat.initializeWithAppID("appID", new Teamchat.TeamchatInitializationWithAppIDCompletionHandler() {
                    @Override
                    public void onTeamchatInitializationWithAppIDCompletion(boolean success, String message) {
                        if (success) {
                            Teamchat.setHostURL(HOST_URL, MainActivity.this);

                            if (!Teamchat.hasActiveSession(MainActivity.this)) {

                                Teamchat.login(MainActivity.this, new Teamchat.LoginCompletionHandler() {
                                    @Override
                                    public void onLoginCompletion(boolean success, String message) {
                                        if (success) {
                                            Teamchat.initWithCompletionHandler(new Teamchat.TeamchatStartCompletionHandler() {
                                                                                   @Override
                                                                                   public void onTeamchatStartCompletionHandler(boolean success, String error, String messaeg) {
                                                                                       if (success) {
                                                                                           Teamchat.showRoomList(MainActivity.this);  //Success

                                                                                           finish();
                                                                                       } else {
                                                                                           //Failure while initializing Teamchat.
                                                                                       }
                                                                                   }
                                                                               }, MainActivity.this
                                            ); //Success
                                        } else {
                                            //Failure while logging into Teamchat.
                                        }
                                    }
                                });


                            } else {
                                //        setContentView(R.layout.content_splash_screen);
                                //Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);

                                //toolbar2.setTitle("CHATS");
                                //setSupportActionBar(toolbar2);
                                Teamchat.showRoomList(MainActivity.this);
                                getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                                // getWindow().setStatusBarColor(Color.TRANSPARENT);
                                //Teamchat.setTableViewSeparatorColor();
                                //            super.onbackpressed();
                                finish();
                            }
                            //success
                        } else {
                            //error
                        }
                    }

                }, MainActivity.this
        );



    }
    @Override
    public void onBackPressed(){
        this.finish();
        Log.d("plz", "kill");
        super.onBackPressed();
    }
}
