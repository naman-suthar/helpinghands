package apps.ijp.helpinghandsv2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import apps.ijp.helpinghandsv2.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_contact, container, false);
        TextView emailGeneralInquiries = myView.findViewById(R.id.tv_general_enquiries);
        TextView emailSupport = myView.findViewById(R.id.tv_support);
        TextView emailPartnerships = myView.findViewById(R.id.tv_partnership);

        TextView callUs = myView.findViewById(R.id.tv_call_us);

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+getString(R.string.call_us)));
                startActivity(intent);
            }
        });

        emailGeneralInquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailTo(getString(R.string.email_general_inquiries));
            }
        });

        emailSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailTo(getString(R.string.email_support));
            }
        });

        emailPartnerships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailTo(getString(R.string.email_partnerships));
            }
        });

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendEmailTo(String emailId) {
        // Predefined message

        // Subject of the email
        String subject = "Helping Hands - General Inquiry";
        String[] emailArray = {emailId};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // MIME type for email
        intent.putExtra(Intent.EXTRA_EMAIL, emailArray);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        Intent chooserIntent = Intent.createChooser(intent, "Send Email");
        startActivity(chooserIntent);
    }
}
