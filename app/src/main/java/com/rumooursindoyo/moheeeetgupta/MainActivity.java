package com.rumooursindoyo.moheeeetgupta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    // instance of firebase auth for authentication purpose
    private FirebaseAuth mAuth;

    // instance of FirebaseFirestore used to store and sync data for client and server-side development.
    private FirebaseFirestore firebaseFirestore;

    /**
    This variable is used in splashScreen activity to check whether
     the user is login or not if user is login then after showing splashScreen HomeFragmet will be shown and if user is logout
     then after showing splashScreen loginactivity will be shown
     */
    public static int check_login = 0;


    private String current_user_id;

    // FloatingActionButton button for adding new posts
    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;

    // fragment used for prediction of truth probability of any youtube video
    private NotificationFragment notificationFragment;

    // fragment used for managing personal account section of user after registration
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // no need , check it
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // obtaining an instance of this class by calling getInstance() method, like initialising mAuth variable.
        mAuth = FirebaseAuth.getInstance();


        // obtaining an instance of this class by calling getInstance() method, like initialising firebaseFirestore variable.
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.MainToolbar);
        setSupportActionBar(mainToolbar);

        // setting title to actionbar of mainactivity
        getSupportActionBar().setTitle("RuDoYo");

        // if current user exists
        if(mAuth.getCurrentUser() != null) {


            mainbottomNav = findViewById(R.id.mainBottomNav);


            // FRAGMENTS
            homeFragment = new HomeFragment();   // fragment which shows list of posts
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            addPostBtn.show();
                            replaceFragment(homeFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_account:

                            addPostBtn.hide();
                            replaceFragment(accountFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_notif:

                            addPostBtn.hide();
                            replaceFragment(notificationFragment, currentFragment);

                            return true;

                        default:
                            return false;


                    }

                }
            });


            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainActivity.this,NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });

        }



    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(MainActivity.this, SetupAcivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
      return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;


            default:
                return false;


        }

    }



    private void logOut() {


        mAuth.signOut();
        sendToLogin();
        check_login = 0;
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, loginactivity.class);
        startActivity(loginIntent);
        finish();
        check_login = 1;

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment == homeFragment) {

            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if (fragment == accountFragment) {

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if (fragment == notificationFragment) {

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

}
