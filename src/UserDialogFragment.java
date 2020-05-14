package malaksadek.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserDialogFragment extends DialogFragment {

    Context mContext;
    String data, name;

    public UserDialogFragment() {
        mContext = getActivity();
    }

    public void getName (String n){
        name = n;
    }

    public void getData (String d){
        data = d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Login Options");
        alertDialogBuilder.setMessage("The number you have entered is already registered, what would you like to do?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("I am "+name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createFile(data);
            }
        });
        alertDialogBuilder.setNegativeButton("Re-enter number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    private void createFile(String data) {
        File testFile = new File(getContext().getExternalFilesDir(null), "imgggg.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true));
            writer.write(data);
            writer.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    
    public static UserDialogFragment newInstance() {
        UserDialogFragment f = new UserDialogFragment();
        return f;
    }
    
    }

