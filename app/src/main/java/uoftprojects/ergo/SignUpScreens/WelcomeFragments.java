package uoftprojects.ergo.SignUpScreens;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;


import uoftprojects.ergo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragments extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int aSectionNumber = 0;

    private OnFragmentInteractionListener mListener;


    public static WelcomeFragments newInstance(int pSectionNumber) {
        WelcomeFragments fragment = new WelcomeFragments();
        fragment.aSectionNumber = pSectionNumber;
        return fragment;
    }

    public WelcomeFragments() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        switch(aSectionNumber){
            case 1:
                rootView = inflater.inflate(R.layout.fragment_welcome_1, container, false);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_welcome_3, container, false);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_welcome_2, container, false);
                break;
            default:
                System.err.println("The should not have defaulted, please ensure a page is set");
                rootView = null;
                break;
        }
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
