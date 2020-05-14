package malaksadek.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

public class ContactDialogFragment extends DialogFragment {

    Context mContext;
    String number, email;

    public ContactDialogFragment() {
        mContext = getActivity();
    }

    public void getNumber (String num){
        number = num;
    }

    public void getEmail (String mail){
        email = mail;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Contact Options");
        alertDialogBuilder.setMessage("How would you like to contact this person?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)));
            }
        });
        alertDialogBuilder.setNegativeButton("Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmail(email);
            }
        });
        alertDialogBuilder.setNeutralButton("Message", null);

        return alertDialogBuilder.create();
    }

    public static ContactDialogFragment newInstance() {
        ContactDialogFragment f = new ContactDialogFragment();
        return f;
    }

    protected void sendEmail(String mail) {
        Log.i("Send email", "");

        String[] TO = {mail};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}