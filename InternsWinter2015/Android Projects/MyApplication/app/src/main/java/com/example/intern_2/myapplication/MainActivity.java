package com.example.intern_2.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
//
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.webaroo.teamchatsdk.sdk.Teamchat;
import com.webaroo.teamchatsdk.sdk.TeamchatError;
import com.webaroo.teamchatsdk.sdk.TeamchatGroupCreator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String authCode = "AUTH_CODE";
    static final String HOST_URL = "http://enterprise.teamchat.com/";
    private View headerView;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
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
                                                                                           setContentView(R.layout.activity_main);            //Success
                                                                                           final Button button1 = (Button) findViewById(R.id.button1);
                                                                                           final Button button2 = (Button) findViewById(R.id.button2);
                                                                                           final Button button3 = (Button) findViewById(R.id.button3);
                                                                                           final Button button4 = (Button) findViewById(R.id.button4);
                                                                                           mDrawerList = (ListView) findViewById(R.id.navList);
                                                                                           addDrawerItems();
                                                                                           button1.setOnClickListener(new View.OnClickListener() {
                                                                                               public void onClick(View v) {

                                                                                                   TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                                                                                   creator.setGroupName("SearchMyFood");

                                                                                                   //Group Members
                                                                                                   ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                                                                                   Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                                                                                   contact1.email = "sswaroop@iitb.ac.in";
                                                                                                   contact1.profileName = "MESS";

//
                                                                                                   groupMembers.add(contact1);
//
                                                                                                   creator.setGroupMembers(groupMembers);


                                                                                                   creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                                                                                       @Override
                                                                                                       public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                                                                           if (success) {
                                                                                                               Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                                                                               //Teamchat.showRoomList(MainActivity.this);   //success
                                                                                                           } else {
                                                                                                               //Failure
                                                                                                           }
                                                                                                       }
                                                                                                   });


                                                                                               }
                                                                                           });


                                                                                           button2.setOnClickListener(new View.OnClickListener() {
                                                                                               public void onClick(View v) {
                                                                                                   setContentView(R.layout.content);
                                                                                                   final ImageButton ibutton = (ImageButton) findViewById(R.id.imageButton);
                                                                                                   final ImageButton ibutton1 = (ImageButton) findViewById(R.id.imageButton1);
                                                                                                   final TextView dept = (TextView) findViewById(R.id.dept);
                                                                                                   final TextView sems = (TextView) findViewById(R.id.sems);
                                                                                                   Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
                                                                                                   //  toolbar.setNavigationIcon(R.drawable.ic_good);
                                                                                                   toolbar2.setTitle("Save My Sem");
                                                                                                   // toolbar.setSubtitle("Sub");
                                                                                                   // toolbar.setLogo(R.drawable.ic_launcher);
                                                                                                   setSupportActionBar(toolbar2);

                                                                                                   ibutton.setOnClickListener(new View.OnClickListener() {
                                                                                                       public void onClick(View v) {
                                                                                                           final CharSequence dep[] = new CharSequence[]{"Computer Science", "Electrical Engineering", "Aerospace Engineering", "Chemical Engineering", "Civil Engineering", "Mechanical Engineering"};

                                                                                                           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                                                           builder.setTitle("Pick a color");
                                                                                                           builder.setItems(dep, new DialogInterface.OnClickListener() {
                                                                                                               @Override
                                                                                                               public void onClick(DialogInterface dialog, int which) {
                                                                                                                   dept.setText(dep[which]);
                                                                                                                   // the user clicked on colors[which]
                                                                                                               }
                                                                                                           });
                                                                                                           builder.show();
                                                                                                       }
                                                                                                   });

                                                                                                   ibutton1.setOnClickListener(new View.OnClickListener() {
                                                                                                       public void onClick(View v) {
                                                                                                           final CharSequence sem[] = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                                                                                                           AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                                                                                           builder1.setTitle("Pick your semester");
                                                                                                           builder1.setItems(sem, new DialogInterface.OnClickListener() {
                                                                                                               @Override
                                                                                                               public void onClick(DialogInterface dialog, int which) {
                                                                                                                   sems.setText(sem[which]);
                                                                                                                   // the user clicked on colors[which]
                                                                                                               }
                                                                                                           });
                                                                                                           builder1.show();


                                                                                                       }


                                                                                                   });
                                                                                               }
                                                                                           });

                                                                                           button3.setOnClickListener(new View.OnClickListener() {
                                                                                               public void onClick(View v) {

                                                                                                   TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                                                                                   creator.setGroupName("SearchMySem");

                                                                                                   //Group Members
                                                                                                   ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                                                                                   Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                                                                                   contact1.email = "bot01hp0211@gmail.com";
                                                                                                   contact1.profileName = "SEM";

//
                                                                                                   groupMembers.add(contact1);
//
                                                                                                   creator.setGroupMembers(groupMembers);


                                                                                                   creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                                                                                       @Override
                                                                                                       public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                                                                           if (success) {
                                                                                                               Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                                                                               //Teamchat.showRoomList(MainActivity.this);   //success
                                                                                                           } else {
                                                                                                               //Failure
                                                                                                           }
                                                                                                       }
                                                                                                   });


                                                                                               }
                                                                                           });


                                                                                           button4.setOnClickListener(new View.OnClickListener() {
                                                                                               public void onClick(View v) {

                                                                                                   TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                                                                                   creator.setGroupName("Splitcash");

                                                                                                   //Group Members
                                                                                                   ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                                                                                   Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                                                                                   contact1.email = "bot.calc@gmail.com";
                                                                                                   contact1.profileName = "SPLIT";

//
                                                                                                   groupMembers.add(contact1);
//
                                                                                                   creator.setGroupMembers(groupMembers);


                                                                                                   creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                                                                                       @Override
                                                                                                       public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                                                                           if (success) {
                                                                                                               Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                                                                               //Teamchat.showRoomList(MainActivity.this);   //success
                                                                                                           } else {
                                                                                                               //Failure
                                                                                                           }
                                                                                                       }
                                                                                                   });


                                                                                               }
                                                                                           });


                                                                                       } else {
                                                                                           //Failure while initializing Teamchat.
                                                                                       }
                                                                                   }
                                                                               }, MainActivity.this
                                            );


                                            //Success


                                        } else {
                                            //Failure while logging into Teamchat.
                                        }
                                    }


                                });
                            } else {
                                setContentView(R.layout.activity_main);            //Success
                                final Button button1 = (Button) findViewById(R.id.button1);
                                final Button button2 = (Button) findViewById(R.id.button2);
                                final Button button3 = (Button) findViewById(R.id.button3);
                                final Button button4 = (Button) findViewById(R.id.button4);
                                mDrawerList = (ListView) findViewById(R.id.navList);
                                addDrawerItems();
                                button1.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                        creator.setGroupName("SearchMyFood");

                                        //Group Members
                                        ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                        Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                        contact1.email = "sswaroop@iitb.ac.in";
                                        contact1.profileName = "MESS";

//
                                        groupMembers.add(contact1);
//
                                        creator.setGroupMembers(groupMembers);


                                        creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                            @Override
                                            public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                if (success) {
                                                    Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                    //Teamchat.showRoomList(MainActivity.this);   //success
                                                } else {
                                                    //Failure
                                                }
                                            }
                                        });


                                    }
                                });


                                button3.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                        creator.setGroupName("SearchMySem");

                                        //Group Members
                                        ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                        Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                        contact1.email = "bot01hp0211@gmail.com";
                                        contact1.profileName = "SEM";

//
                                        groupMembers.add(contact1);
//
                                        creator.setGroupMembers(groupMembers);


                                        creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                            @Override
                                            public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                if (success) {
                                                    Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                    //Teamchat.showRoomList(MainActivity.this);   //success
                                                } else {
                                                    //Failure
                                                }
                                            }
                                        });


                                    }
                                });

                                button2.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        setContentView(R.layout.content);
                                        final ImageButton ibutton = (ImageButton) findViewById(R.id.imageButton);
                                        final ImageButton ibutton1 = (ImageButton) findViewById(R.id.imageButton1);
                                        final TextView dept = (TextView) findViewById(R.id.dept);
                                        final TextView sems = (TextView) findViewById(R.id.sems);
                                        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
                                        //  toolbar.setNavigationIcon(R.drawable.ic_good);
                                        toolbar2.setTitle("Save My Sem");
                                        // toolbar.setSubtitle("Sub");
                                        // toolbar.setLogo(R.drawable.ic_launcher);
                                        setSupportActionBar(toolbar2);

                                        ibutton.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                final CharSequence dep[] = new CharSequence[]{"Computer Science", "Electrical Engineering", "Aerospace Engineering", "Chemical Engineering", "Civil Engineering", "Mechanical Engineering"};

                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Pick a color");
                                                builder.setItems(dep, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dept.setText(dep[which]);
                                                        // the user clicked on colors[which]
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                        ibutton1.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                final CharSequence sem[] = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                                builder1.setTitle("Pick your semester");
                                                builder1.setItems(sem, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        sems.setText(sem[which]);
                                                        // the user clicked on colors[which]
                                                    }
                                                });
                                                builder1.show();


                                            }


                                        });
                                    }
                                });

                                button4.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        TeamchatGroupCreator creator = new TeamchatGroupCreator();
//
////Mandatory methods for group creation
                                        creator.setGroupName("SplitCash");

                                        //Group Members
                                        ArrayList<Teamchat.TCTeamchatContact> groupMembers = new ArrayList<Teamchat.TCTeamchatContact>();

                                        Teamchat.TCTeamchatContact contact1 = new Teamchat.TCTeamchatContact();
                                        contact1.email = "bot.calc@gmail.com";
                                        contact1.profileName = "SPLIT";

//
                                        groupMembers.add(contact1);
//
                                        creator.setGroupMembers(groupMembers);


                                        creator.createGroupWithCompletionHandler(MainActivity.this, new TeamchatGroupCreator.TeamchatGroupCreationCompletionHandler() {
                                            @Override
                                            public void onTeamchatGroupCreationComplete(boolean success, TeamchatError error, String message, Teamchat.TeamchatGroup createdGroup) {
                                                if (success) {
                                                    Teamchat.openRoom(createdGroup.getGroupID(), MainActivity.this);
                                                    //Teamchat.showRoomList(MainActivity.this);   //success
                                                } else {
                                                    //Failure
                                                }
                                            }
                                        });


                                    }
                                });

                            }
                            //success
                        } else {
                            //error
                        }
                    }
                }
                , MainActivity.this
        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void addDrawerItems() {
        String[] osArray = {"Android", "iOS", "Windows", "OS X", "Linux"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

         //   mDrawerToggle.setDrawerIndicatorEnabled(true);
           // mDrawerLayout.setDrawerListener(mDrawerToggle);
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
