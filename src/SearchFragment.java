package malaksadek.contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import malaksadek.Contacts.R;
public class SearchFragment extends Fragment {

    EditText number;
    TextView uname, uemail, ucountry;
    ImageView image;
    Button search;
    ImageButton msgbtn, txtbtn, clbtn;
    ProgressBar prgrs;
    String name = "";
    String phone = "";
    String email = "";
    String country = "";
    String hidden = "";
    String msg = "";
    String id ="";
    String imagestring;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_search_fragment, container, false);

        number = (EditText) v.findViewById(R.id.editText5);
        search = (Button) v.findViewById(R.id.button3);
        image = (ImageView) v.findViewById(R.id.imageView);
        uname = (TextView) v.findViewById(R.id.name);
        uemail = (TextView) v.findViewById(R.id.email);
        ucountry = (TextView) v.findViewById(R.id.country);
        msgbtn = (ImageButton) v.findViewById(R.id.imageButton);
        txtbtn = (ImageButton) v.findViewById(R.id.imageButton4);
        clbtn = (ImageButton) v.findViewById(R.id.imageButton2);
        prgrs = (ProgressBar) v.findViewById(R.id.progressBar);
        msgbtn.setVisibility(View.INVISIBLE);
        txtbtn.setVisibility(View.INVISIBLE);
        clbtn.setVisibility(View.INVISIBLE);
        uname.setVisibility(View.INVISIBLE);
        uemail.setVisibility(View.INVISIBLE);
        ucountry.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

        clbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().toString().contains("0"))
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number.getText().toString(), null)));
                else
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
            }
        });

        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(email);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If user is searching using number
                if (number.getText().toString().contains("0"))
                    findUserNumber();
                else //If user is searching using name
                    findUserName();

                prgrs.setVisibility(View.INVISIBLE);
                clbtn.setVisibility(View.VISIBLE);
                uname.setVisibility(View.VISIBLE);
                uemail.setVisibility(View.VISIBLE);
                ucountry.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }

    protected void sendEmail(String mail) {
        Log.i("Send email", "");

        String[] TO = {mail};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void findUserName() {
        String url = "http://188.226.144.157/group1/searchname.php?User_Name=" + number.getText().toString();
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JSONObject jsonObj = new JSONObject();

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    id = (String) response.get("User_ID");
                    phone = (String) response.get("User_Number");
                    email = (String) response.get("User_Email");
                    country = (String) response.get("User_Country");
                    hidden = (String) response.get("Hidden");
                    msg = (String) response.get("Can_Message");

                    if(hidden.contains("1"))
                    {
                        uname.setText(phone);
                        uemail.setText("Hidden");
                        ucountry.setText("Hidden");
                        image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        uname.setText(phone);
                        uemail.setText(email);
                        ucountry.setText(country);
                        msgbtn.setVisibility(View.VISIBLE);
                        LoadImage();
                    }

                    if(msg.contains("1"))
                    {
                        txtbtn.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(jsonObjRequest);
    }

    public void findUserNumber() {
        String url = "http://188.226.144.157/group1/search.php?User_Number=" + number.getText().toString(), j = "";

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JSONObject jsonObj = new JSONObject();

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    id = (String) response.get("User_ID");
                    name = (String) response.get("User_Name");
                    email = (String) response.get("User_Email");
                    country = (String) response.get("User_Country");
                    hidden = (String) response.get("Hidden");
                    msg = (String) response.get("Can_Message");

                    if(hidden.contains("1"))
                    {
                        uname.setText(name);
                        uemail.setText("Hidden");
                        ucountry.setText("Hidden");
                        image.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        uname.setText(name);
                        uemail.setText(email);
                        ucountry.setText(country);
                        msgbtn.setVisibility(View.VISIBLE);
                        LoadImage();
                    }

                    if(msg.contains("1"))
                    {
                        msgbtn.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(jsonObjRequest);

    }

    public void LoadImage() {

        final String url = "http://188.226.144.157/group1/downloadphoto.php?id=" + id;

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imagestring = response;
                imagestring = imagestring.substring(imagestring.indexOf('[')+2, imagestring.length()-2);
                imagestring = imagestring.replaceAll("\\s+","+");
                imagestring = imagestring.replace("\\", "");
                Log.i("imagestring", imagestring);
                byte[] decodedString = Base64.decode(imagestring, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
                Log.d("", "wooo ");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse upload: " + error.toString());
            }
        });
        requestQueue.add(Stringrequest);
    }
}