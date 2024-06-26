package apps.ijp.helpinghandsv2.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import apps.ijp.helpinghandsv2.FindVictimActivity;
import apps.ijp.helpinghandsv2.R;
import apps.ijp.helpinghandsv2.UploadActivity;
import apps.ijp.helpinghandsv2.VictimAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FindVictimFragment extends Fragment implements VictimAdapter.VictimAdapterOnClickHandler {

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private VictimAdapter mVictimAdapter;
    private CardView mResultCard;
    private EditText mSearchField;
    private String TAG = FindVictimActivity.class.getSimpleName();
//    private GetVictim mTask;
    // URL to get contacts JSON
    private  String url = "/HHAPI/select.php";
    private String headurl = "http://";
    String ipadd;
    ArrayList<HashMap<String, String>> shelterList;


    private OnFragmentInteractionListener mListener;

    public FindVictimFragment() {
        // Required empty public constructor
    }


    public static FindVictimFragment newInstance(String param1, String param2) {
        FindVictimFragment fragment = new FindVictimFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragview = inflater.inflate(R.layout.fragment_find_victim, container, false);

        shelterList = new ArrayList<>();
        mRecyclerView = (RecyclerView) fragview.findViewById(R.id.recycler_shelter);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) fragview.findViewById(R.id.error_mess);
        mLoadingIndicator = (ProgressBar) fragview.findViewById(R.id.loadbar);
        mSearchField =(EditText)fragview.findViewById(R.id.search_field);
        mResultCard=(CardView)fragview.findViewById(R.id.card_viewresult);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(fragview.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);



        mVictimAdapter = new VictimAdapter(this);

        mRecyclerView.setAdapter(mVictimAdapter);

        mSearchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                showShelterDataView();
                Log.e("TestCode","Working");
               /* if(mSearchField.getText().toString().length()>0)
                    new GetVictim().execute(mSearchField.getText().toString());*/
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereGreaterThanOrEqualTo("name", s.toString().trim())
                        .whereLessThan("name", s.toString().trim() + "\uf8ff")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Handle query success
                            List<String> users = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String user = documentSnapshot.getString("name");
                                Object latitude = documentSnapshot.get("lati");
                                Object longitude = documentSnapshot.get("longi");

                                users.add(user+ " " + latitude.toString() + " " + longitude);
                            }
                            // Update UI with search results
                            // For example, display users in a RecyclerView
                            mVictimAdapter.setShelterData(users.toArray(new String[0]));
                        })
                        .addOnFailureListener(e -> {
                            // Handle query failure
                            Log.e(TAG, "Error searching users: ", e);
                            // Display an error message to the user
                        });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return fragview;
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


    private void showShelterDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        mResultCard.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String ShelterData) {
        Intent intentnext = new Intent(getActivity(), FindVictimActivity.class);
        intentnext.putExtra("shelterdata",ShelterData);
        startActivity(intentnext);
    }

/*    private class GetVictim extends AsyncTask<String, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            ipadd = getActivity().getResources().getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();

            url = "/HHAPI/select.php?search="+strings[0];
            headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr); //The Output of First Page

            if (jsonStr != null) {
                try {

                    // Getting JSON Array node
                    JSONArray names = new JSONArray(jsonStr);
                    shelterList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < names.length(); i++) {
                        JSONObject c = names.getJSONObject(i);

                        String name = c.getString("name");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value\
                        contact.put("name", name);
                        //Log.e("Names",contact.get("name").toString());
                        // adding contact to contact list

                        shelterList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }



            } else {
                Log.e(TAG, "Couldn't get json from server.");
                *//*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });*//*
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            *//**
             * Updating parsed JSON data into ListView
             * *//*

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showShelterDataView();
            *//*ListAdapter adapter = new SimpleAdapter(
                    SheltersActivity.this, shelterList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);*//*
            if(shelterList.size()>0) {
                HashMap<String, String> m = shelterList.get(0);
                String strArr[] = new String[shelterList.size() * m.size()];
                int i = 0;
                for (HashMap<String, String> hash : shelterList) {
                    for (String current : hash.values()) {
                        strArr[i] = current;
                        i++;
                    }
                }
                mVictimAdapter.setShelterData(strArr);
            }else {
                showErrorMessage();
            }
        }

    }*/
}
