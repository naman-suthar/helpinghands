package apps.ijp.helpinghandsv2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import apps.ijp.helpinghandsv2.Fragments.AboutFragment;
import apps.ijp.helpinghandsv2.Fragments.ContactFragment;
import apps.ijp.helpinghandsv2.Fragments.FindVictimFragment;
import apps.ijp.helpinghandsv2.Fragments.HelplineTipsFragment;
import apps.ijp.helpinghandsv2.Fragments.ProfileFragment;
import apps.ijp.helpinghandsv2.Fragments.RequestHelpFragment;
import apps.ijp.helpinghandsv2.Fragments.VolunteerFragment;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    private FirebaseAuth auth;
    TextView tvProName, tvEmail;

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RequestHelpFragment fragment = new RequestHelpFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        toolbar.setTitle("Request Help");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                AlertDialog dialog;
                builder.setTitle("Are you in danger?");
                View view1 = getLayoutInflater().inflate(R.layout.dialog_sos, null);
                Spinner spinner = view1.findViewById(R.id.spinner_disaster);
                String[] items = new String[]{
                        "Earthquake",
                        "Flood",
                        "Fire",
                        "Tsunami",
                        "Cyclone",
                        "Tornado",
                        "Hurricane",
                        "Medical Emergency",
                        "Drought",
                        "Landslide",
                        "Volcano",
                        "Pandemics",
                        "Accident"
                };
                String[] disaster = new String[1];
                ArrayAdapter array_adapter = new ArrayAdapter(
                        NavigationActivity.this,
                        android.R.layout.simple_spinner_item,
                        items
                );
                array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(array_adapter);
                Button btnSOS = view1.findViewById(R.id.btn_submit_send);
                TextView tvLocation = view1.findViewById(R.id.tv_location_cords);
                TextView tvName = view1.findViewById(R.id.tv_title);
                tvName.setText(auth.getCurrentUser().getEmail());
                tvLocation.setText(fragment.getLocations().getLatitude() + ", " + fragment.getLocations().getLongitude());
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        disaster[0] = items[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        disaster[0] = items[0];
                    }
                });
                //Todo : Set Location cords
                builder.setView(view1);
                dialog = builder.create();
                dialog.show();
                btnSOS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Todo: Logic to send the Message to respective Authorities
                        String number;
                        switch (disaster[0]) {
                            case "Earthquake":
                                //SDRF
                                number = "9325711055";
                                break;
                            case "Flood":
                                //SDRF
                                number = "9325711055";
                                break;
                            case "Fire":
                                //Fire Brigade
                                number = "9021480996";
                                break;
                            case "Tsunami":
                                //NDRF
                                number = "9529059987";
                                break;
                            case "Cyclone":
                                //NDRF
                                number = "9529059987";
                                break;
                            case "Tornado":
                                //SDRF
                                number = "9325711055";
                                break;
                            case "Hurricane":
                                //NDMA
                                number = "9637167419";
                                break;
                            case "Drought":
                                //SDRF
                                number = "9325711055";
                                break;
                            case "Landslide":
                                //SDRF
                                number = "9325711055";
                                break;
                            case "Volcano":
                                //NDMA
                                number = "9637167419";
                                break;
                            case "Pandemics":
                                //NDRF
                                number = "9529059987";
                                break;
                            case "Car Accident":
                                //Police
                                number = "7823851265";
                                break;
                            case "Medical Emergency":
                                //Hospitals
                                number = "7057835845";
                                break;
                            default:
                                number = "9529059987";
                                break;
                        }
                        ;


                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        HashMap<String,Object> updates = new HashMap<>();
                        updates.put("victim",true);
                        db.collection("users").document(auth.getCurrentUser().getEmail()).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                String message = "I'm *" + auth.getCurrentUser().getDisplayName() + "* in danger, Please help me with *" + disaster[0] + "*.  My Location is : " + fragment.getLocations().getLatitude() + ", " + fragment.getLocations().getLongitude()
                                                        + "\n https://www.google.com/maps/?q=" + fragment.getLocations().getLatitude() + "," + fragment.getLocations().getLongitude();
                                                try {
                                                    String encodedMessage = URLEncoder.encode(message, "UTF-8");
                                                    openUrl("https://api.whatsapp.com/send?phone=" + number + "&text=" + encodedMessage);

                                                } catch (UnsupportedEncodingException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                dialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NavigationActivity.this, "Network Error, please try after some time :)",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });


                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        auth = FirebaseAuth.getInstance();
        tvProName = navigationView.getHeaderView(0).findViewById(R.id.proName);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.email);
        checkIfUserDataIsInDatabase();

        if (auth.getCurrentUser() != null) {
            tvProName.setText(auth.getCurrentUser().getDisplayName());
            tvEmail.setText(auth.getCurrentUser().getEmail());
            Log.e("LoginStatus", "Success");
        }


    }

    private void signOut() {
        Intent signOutIntent = new Intent(NavigationActivity.this, LoginActivity.class);
        startActivity(signOutIntent);
        finish();
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(NavigationActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                signOut();
                            } else {
                                displayMessage("Signout Error");
                            }
                        }
                    });
        }

        if (id == R.id.action_i_am_safe){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String,Object> updates = new HashMap<>();
            updates.put("victim",false);
            db.collection("users").document(auth.getCurrentUser().getEmail()).update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(NavigationActivity.this, "Glad to here that, Take care :)",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NavigationActivity.this, "Network Error, please try after some time :)",Toast.LENGTH_SHORT).show();
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

        if (id == R.id.request_help) {
            RequestHelpFragment fragment = new RequestHelpFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Request Help");
        } else if (id == R.id.volunteer) {
            VolunteerFragment fragment = new VolunteerFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Volunteer");

        }
        /*else if (id == R.id.shelters) {
            SheltersFragment fragment = new SheltersFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Shelters");

        } */
        else if (id == R.id.find_victim) {
            FindVictimFragment fragment = new FindVictimFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Find Victim");

        }
       /* else if (id == R.id.live_updates) {
            LiveUpdatesFragment fragment = new LiveUpdatesFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Live Updates");

        } */
        else if (id == R.id.helpline_tips) {
            HelplineTipsFragment fragment = new HelplineTipsFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Helpline and Tips");

        } else if (id == R.id.profile) {
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Profile");

        }
       /* else if (id == R.id.ec) {
            FAQFragment fragment = new FAQFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("FAQ");

        } */
        else if (id == R.id.contact) {
            ContactFragment fragment = new ContactFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("Contact Us");

        } else if (id == R.id.about) {
            AboutFragment fragment = new AboutFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            toolbar.setTitle("About Us");

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkIfUserDataIsInDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() == null) {
            return;
        }
        db.collection("users").document(auth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                    } else {
                        Log.d("Document", "No such document");
                        Intent intent = new Intent(NavigationActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(NavigationActivity.this, "Error getting document" + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
