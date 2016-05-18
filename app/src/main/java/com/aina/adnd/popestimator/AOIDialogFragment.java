package com.aina.adnd.popestimator;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AOIDialogFragment
        extends DialogFragment
        implements View.OnClickListener {

    public AOIDialogFragment() {
        // Required empty public constructor
    }

    final int MAX_MILES = 30;
    final int MIN_MILES = 1;
    final int MAX_MINUTES = 30;
    final int MIN_MINUTES = 1;
    final int DEFAULT_AOITYPE = 0;
    final int DEFAULT_MINUTES = 10;
    final int DEFAULT_MILES = 3;
    int mMiles;
    int mMinutes;
    int mType;
    NumberPicker milesPicker;
    NumberPicker minutesPicker;
    RadioButton radialButton;
    RadioButton drivetimeButton;
    AOIDialogListener mListener;


    public interface AOIDialogListener {
        void onDialogPositiveClick(Bundle bundle);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (AOIDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AoiDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (UserPreferences.getUserAoi(getActivity()) >= 0)
            mType = UserPreferences.getUserAoi(getActivity());

        if (UserPreferences.getUserMiles(getActivity()) > 0)
            mMiles = UserPreferences.getUserMiles(getActivity());

        if (UserPreferences.getUserMinutes(getActivity()) > 0)
            mMinutes = UserPreferences.getUserMinutes(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.fragment_aoidialog, null);

        radialButton = (RadioButton) rootView.findViewById(R.id.aoiRadial);
        drivetimeButton = (RadioButton) rootView.findViewById(R.id.aoiDriveTime);
        radialButton.setOnClickListener(this);
        drivetimeButton.setOnClickListener(this);

        milesPicker = (NumberPicker) rootView.findViewById(R.id.miles);
        milesPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        milesPicker.setMinValue(MIN_MILES);
        milesPicker.setMaxValue(MAX_MILES);
        milesPicker.setValue(mMiles);

        milesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMiles = newVal;
            }
        });

        minutesPicker = (NumberPicker) rootView.findViewById(R.id.minutes);
        minutesPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutesPicker.setMinValue(MIN_MINUTES);
        minutesPicker.setMaxValue(MAX_MINUTES);
        minutesPicker.setValue(mMinutes);

        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMinutes = newVal;
            }
        });

        if(mType==0){
            drivetimeButton.setChecked(false);
            radialButton.setChecked(true);
            milesPicker.setEnabled(true);
            minutesPicker.setEnabled(false);
        }
        else{
            drivetimeButton.setChecked(true);
            radialButton.setChecked(false);
            milesPicker.setEnabled(false);
            minutesPicker.setEnabled(true);
        }

        builder.setView(rootView)
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Bundle b = new Bundle();

                        if (mType == 0) {
                            b.putInt("t", 0);
                            b.putInt("v", milesPicker.getValue());

                        }else {
                            b.putInt("t", 1);
                            b.putInt("v", minutesPicker.getValue());
                        }
                        mListener.onDialogPositiveClick(b);
                        UserPreferences.setUserMiles(getActivity(),milesPicker.getValue());
                        UserPreferences.setUserMinutes(getActivity(),minutesPicker.getValue());
                        UserPreferences.setUserAoiType(getActivity(),mType);
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AOIDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.label_aoidialog);

        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public void onClick(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.aoiRadial:
                if (checked) {
                    milesPicker.setEnabled(true);
                    minutesPicker.setEnabled(false);
                    drivetimeButton.setChecked(false);
                    mType = 0;
                }

                break;
            case R.id.aoiDriveTime:
                if (checked) {
                    minutesPicker.setEnabled(true);
                    milesPicker.setEnabled(false);
                    radialButton.setChecked(false);
                    mType = 1;
                }
                break;
        }
    }
}
