package com.github.lukaszmalyszko.wifi_timer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddDialogFragment extends DialogFragment {

    public interface AddDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    AddDialogListener mListener;
    public EditText name;
    public EditText networkId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);
        networkId = view.findViewById(R.id.networkId);
        networkId.setText(getIdFromConnection());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        name = getActivity().findViewById(R.id.wifiName);
                        networkId = getActivity().findViewById(R.id.networkId);
                        mListener.onDialogPositiveClick(AddDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(AddDialogFragment.this);
                    }
                });
        return builder.create();
    }

    private String getIdFromConnection(){
        // wifi manager do obslugiwania wszystkich usług/serwisów wifi
        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // wifiinfo wywolanie getconnection z managera w ktorym przechowywane sa różne rzeczy odnosnie aktualnego połączenia
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            return "" + wifiInfo.getNetworkId();
        }else{
            return "";
        }
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(" must implement AddDialogListener");
        }
    }
}
