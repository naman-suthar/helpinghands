package apps.ijp.helpinghandsv2.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import apps.ijp.helpinghandsv2.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestHelpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestHelpFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int FINE_LOCATION_PERMISSION = 100;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Boolean zoomedToMe = false;

    private OnFragmentInteractionListener mListener;

    public RequestHelpFragment() {
        // Required empty public constructor
    }

    public Location getLocations() {
        return currentLocation;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestHelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestHelpFragment newInstance(String param1, String param2) {
        RequestHelpFragment fragment = new RequestHelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap map;
    private Button btnSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();
        View myView = inflater.inflate(R.layout.fragment_request_help, container, false);
        btnSync = myView.findViewById(R.id.sync);
        btnSync.setText("Fetching...");
        btnSync.setOnClickListener(v -> {
            getLastLocation();
            updateLocation();
        });
        Button btnInfo = myView.findViewById(R.id.map_info);
        btnInfo.setOnClickListener(v -> {
            openLegendsDialog();
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_home);
        mapFragment.getMapAsync(RequestHelpFragment.this);

        return myView;
    }

    private void openLegendsDialog() {
        AlertDialog.Builder legendsDialog = new AlertDialog.Builder(getActivity());
        legendsDialog.setTitle("Map Info");
        View mapDialogView = getLayoutInflater().inflate(R.layout.map_legends_dialog, null);
        legendsDialog.setView(mapDialogView);
        legendsDialog.show();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("TAG", "onMapReady: " + googleMap.toString());
        map = googleMap;
        fetchUsersLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getActivity(), "Permission denied, app may misbehave", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                if (map != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10f));
                }
                btnSync.setVisibility(View.VISIBLE);
                btnSync.setText("Sync");
            }
        }).addOnFailureListener(e -> {
            btnSync.setVisibility(View.GONE);
            Log.d("TAG", "onFailure: " + e.getLocalizedMessage());
        });
    }

    private void updateLocation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && currentLocation != null) {
            DocumentReference documentRef = db.collection("users").document(auth.getCurrentUser().getEmail());
            Map<String, Object> updates = new HashMap<>();
            updates.put("lati", currentLocation.getLatitude());
            updates.put("longi", currentLocation.getLongitude());
            documentRef.update(updates).addOnSuccessListener(aVoid -> {
                btnSync.setText("Synced");
            }).addOnFailureListener(e -> {
                btnSync.setText("Try again!");
            });
        }

    }

    private void fetchUsersLocation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            db.collection("users").addSnapshotListener((value, error) -> {
                if (value != null) {
                    map.clear();
                    for (int i = 0; i < value.size(); i++) {
                        Log.d("TAG", "fetchUsersLocation: " + value.getDocuments().get(i));
                        Object lati = value.getDocuments().get(i).get("lati");
                        Object longi = value.getDocuments().get(i).get("longi");
                        String name = value.getDocuments().get(i).getString("name");
                        String email = value.getDocuments().get(i).getString("email");
                        Boolean isVolunteer = value.getDocuments().get(i).getBoolean("volunteer");
                        Boolean providingShelter = value.getDocuments().get(i).getBoolean("shelter");
                        Boolean providingFood = value.getDocuments().get(i).getBoolean("food");
                        Boolean providingClothes = value.getDocuments().get(i).getBoolean("clothes");
                        Boolean isVictim = value.getDocuments().get(i).getBoolean("victim");
//                        Object isVictim =

                        LatLng userLocation = new LatLng(Double.valueOf(lati.toString()), Double.valueOf(longi.toString()));
                        MarkerOptions marker = new MarkerOptions().position(userLocation);
                        Log.d("TAG", "fetchUsersLocation: " + email + " && " + auth.getCurrentUser().getEmail());

                        if (email.equals(auth.getCurrentUser().getEmail())) {
                            marker.title("Me");
                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            if (!zoomedToMe) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f));
                                zoomedToMe = true;
                            }
                        } else {
                            if (isVictim) {
                                marker.title(name);
                                marker.snippet(" is in need of help!");
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            } else if (isVolunteer) {

                                String facilities = "";
                                if (providingFood && providingClothes && providingShelter) {
                                    facilities += "Food, ";
                                    if (getContext() != null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.marker_all_helps);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }

                                } else if (providingFood && providingClothes){
                                    facilities += "Food and Clothes ";
                                    if (getContext() !=null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.food_and_clothes);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }


                                } else if (providingFood && providingShelter){
                                    facilities += "Food and Home";
                                    if (getContext() != null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shelter_and_food);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }
                                } else if (providingClothes && providingShelter){
                                    facilities += "Clothes and home ";
                                    if (getContext() != null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shelter_and_clothes);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }

                                }else if (providingFood) {
                                    facilities += "Food, ";
                                    if (getContext() != null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.food_marker);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }
                                } else if (providingClothes) {
                                    facilities += "Clothes, ";
                                    if (getContext() != null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.clothes_marker);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }

                                } else if (providingShelter) {
                                    facilities += "Shelter";
                                    if (getContext() !=null){
                                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shelter);
                                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
                                        marker.icon(bitmapDescriptor);
                                    }
                                } else {
                                    marker.title(name);
                                    marker.snippet("is ready to help!");
                                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                                }
                                marker.contentDescription(
                                        facilities
                                );
                                marker.title(name);

                            } else {
                                marker.title(name);
                                marker.snippet("Safe");
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }
                        }
                        map.addMarker(marker);
                    }
                }
            });

        }
    }
}

