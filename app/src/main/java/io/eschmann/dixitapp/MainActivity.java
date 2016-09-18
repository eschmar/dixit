package io.eschmann.dixitapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "DixitAppRecording";
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int RC_SIGN_IN = 55;
    protected String basePath;
    protected String fileName = "pew-recording.raw";
    protected String filePath;
    protected FloatingActionButton fab;
    protected String[] reqPermissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    protected ArrayList<Phrase> archive = new ArrayList<Phrase>();
    protected ListView mainListView;
    protected ArrayAdapter mainListAdapter;
    protected DatabaseReference DBreference;
    protected FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    protected Node currentUserNode;
    protected DatabaseReference userRef;

    protected TextView userName;
    protected TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBreference = FirebaseDatabase.getInstance().getReference();
        Util.createAppFolder();

        basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Util.APP_NAME;
        filePath = basePath + File.separator + fileName;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_mic);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView mainListView = (ListView) findViewById(R.id.list);

        mainListAdapter = new ArrayAdapter<Phrase>(
            this,
            android.R.layout.simple_list_item_1,
            archive
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                LayoutInflater mInflater = LayoutInflater.from(this.getContext());

                if (null == convertView) {
                    row = mInflater.inflate(R.layout.two_line_list_item, null);
                } else {
                    row = convertView;
                }

                TextView text1 = (TextView) row.findViewById(android.R.id.text1);
                TextView text2 = (TextView) row.findViewById(android.R.id.text2);
                text1.setText(getItem(position).getText());
                text2.setText(getItem(position).getTranslation());

                return row;
            }
        };

        mainListView.setAdapter(mainListAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void initUserReference() {
        DatabaseReference userRef = mDatabase.child("users").child(mUser.getUid()).getRef();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserNode = dataSnapshot.getValue(Node.class);
                System.out.println("Update: " + currentUserNode.getPhrases().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView userName = (TextView)header.findViewById(R.id.userNameView);
        TextView userEmail = (TextView)header.findViewById(R.id.userEmailView);

        Util.requestPermission(this, reqPermissions);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            mUser = auth.getCurrentUser();
            userName.setText(mUser.getDisplayName());
            userEmail.setText(mUser.getEmail());

            mDatabase.child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUserNode = dataSnapshot.getValue(Node.class);
                    if (currentUserNode == null) {
                        return;
                    }

                    mainListAdapter.clear();
                    mainListAdapter.addAll(currentUserNode.getPhrases());
                    mainListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

        }else {
            AuthUI authUi = AuthUI.getInstance();
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                    .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN);
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    // Invoked when speechRecognizer return a result.
    // Return a List of strings
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (currentUserNode == null) {
                Node temp = new Node();
                temp.addPhrase(new Phrase(results.get(0)));
                mDatabase.child("users").child(mUser.getUid()).setValue(temp);
                initUserReference();
            }else {
                System.out.println("PEW: " + currentUserNode.getPhrases());
                currentUserNode.addPhrase(new Phrase(results.get(0)));
                mDatabase.child("users").child(mUser.getUid()).setValue(currentUserNode);
            }

            if (currentUserNode != null) {
                mainListAdapter.clear();
                mainListAdapter.addAll(currentUserNode.getPhrases());
                mainListAdapter.notifyDataSetChanged();
            }else {
                archive.add(new Phrase(results.get(0)));
                mainListAdapter.notifyDataSetChanged();
            }


        }else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                startActivity(new Intent(this, MainActivity.class));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (userName != null && userEmail != null) {
                    userName.setText(user.getDisplayName());
                    userEmail.setText(user.getEmail());
                }
                this.finish();
                initUserReference();
            } else {
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_logout) {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_practise) {

        } else if (id == R.id.nav_verbs) {

        } else if (id == R.id.nav_verbs_pending) {

        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
