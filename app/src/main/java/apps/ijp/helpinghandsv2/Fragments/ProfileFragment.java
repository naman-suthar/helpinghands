package apps.ijp.helpinghandsv2.Fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;

import apps.ijp.helpinghandsv2.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Firebase variables and async task variable
//    private ProfileFragment.UserLoginTask mAuthTask = null;
    private FirebaseAuth auth;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnEditProfile;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    String email = "loading";
    String name = "loading";
    String phone = "N/A";
    String age = "N/A";
    String gender = "N/A";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        View photoHeader = view.findViewById(R.id.photoHeader);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        //Firebase authentication getting instance
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            DocumentReference docRef = db.collection("users").document(auth.getCurrentUser().getEmail());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()) {
                        Log.d("TAG", "Profile: DocumentSnapshot data: " + value.getData());
                        email = value.getString("email");
                        name = value.getString("name");
                        phone = value.getString("phone");
                        age = value.getString("age");
                        gender = value.getString("gender");
                        TextView tvName = view.findViewById(R.id.tvName);
                        tvName.setText(name);
                        TextView tvEmail = view.findViewById(R.id.tvemail);
                        tvEmail.setText(email);
                        TextView tvGender = view.findViewById(R.id.tvgender);
                        tvGender.setText("Gender: " + gender);
                        TextView tvAge = getActivity().findViewById(R.id.tvage);
                        tvAge.setText("Age: " + age);

                    }
                }

            });

            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog dialog;
                    builder.setTitle("Edit Profile");
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_profile_edit, null);
                    builder.setView(dialogView);
                    EditText mPhone = (EditText) dialogView.findViewById(R.id.signup_input_phone);
                    EditText mName = (EditText) dialogView.findViewById(R.id.signup_input_name);
                    EditText mAge = (EditText) dialogView.findViewById(R.id.signup_input_age);
                    RadioGroup mGender = (RadioGroup) dialogView.findViewById(R.id.gender_radio_group);

                    mPhone.setText(phone);
                    mName.setText(name);
                    mAge.setText(age);
                    if (Objects.equals(gender, "Male")) {
                        mGender.check(R.id.male_radio_btn);
                    } else {
                        mGender.check(R.id.female_radio_btn);
                    }

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Declaring Storing variables

//                EditText mAadhaar = (EditText) findViewById(R.id.signup_input_aadhaar);

                            //Storing Inputs into the variables
                            String phone = mPhone.getText().toString();
//                String profession = mProfession.getText().toString();
                            String age = mAge.getText().toString();
//                String aadhaar = mAadhaar.getText().toString();
                            int selectedId = mGender.getCheckedRadioButtonId();
                            RadioButton tempRadioButton = dialogView.findViewById(selectedId);
                            String gender = tempRadioButton.getText().toString();
                            String name = mName.getText().toString();
                            String email = auth.getCurrentUser().getEmail();
                            HashMap<String, Object> postparam = new HashMap<>();

                            // adding each child node to HashMap key => value\
                            postparam.put("name", name);
                            postparam.put("phone", phone);
                            postparam.put("age", age);
                            postparam.put("gender", gender);

                /*for (Map.Entry<String,Object> entry : postparam.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    Log.e(key,value.toString());
                }*/

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("users").document(email)
                                    .update(postparam)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "Profile updated Successfully :)", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Error updating profile, Check your connection please or try after some time ", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    });

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();

                }
            });

        }
            return view;
        }


}


