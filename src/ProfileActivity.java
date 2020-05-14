package malaksadek.contacts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import malaksadek.Contacts.R;
public class ProfileActivity extends AppCompatActivity {

    TextView tv;
    ImageView iv;
    ToggleButton tb1;   //For hiding user
    ToggleButton tb2;   //For allowing messaging
    String imagestring, data;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv = (TextView) findViewById(R.id.textView4);
        iv = (ImageView) findViewById(R.id.imageView3);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton4);
        tb2 = (ToggleButton) findViewById(R.id.toggleButton6);
        tb2.setChecked(true);

        //Gets user info from their file and displays it
        readFile();
        int count = 0, i = data.indexOf("Name:")+6;
        while(data.charAt(i)!='P')
        {
            count=count+1;
            i=i+1;
        }
        tv.setText(data.substring(data.indexOf("Name:")+6, (data.indexOf("Name:")+6)+(count-1)));
        LoadImage();

        count = 0;
        i = data.indexOf("ID:")+4;
        while(data.charAt(i)!='P')
        {
            count=count+1;
            i=i+1;
        }

        final String ID = data.substring(data.indexOf("ID:")+4, (data.indexOf("ID:")+4)+(count-1));

        //Hides user's info, if another user searches for them, their email and country would not show and they cannot email them
        tb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tb1.isChecked())
                {
                    url ="http://188.226.144.157/group1/changepreferences.php?id="+ID+"&code=1";
                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast t = new Toast(getApplicationContext());
                            t.makeText(getApplicationContext(), "Preferences Updated.", Toast.LENGTH_SHORT).show();
                            Log.i("r", "Hidden");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("", "onERRORResponse upload: " + error.toString());
                        }
                    });
                    requestQueue.add(Stringrequest);
                }
                else
                {
                    url ="http://188.226.144.157/group1/changepreferences.php?id="+ID+"&code=0";
                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast t = new Toast(getApplicationContext());
                            t.makeText(getApplicationContext(), "Preferences Updated.", Toast.LENGTH_SHORT).show();
                            Log.i("r", "Not Hidden");
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
        });

        //Sets whether or not user can message, if disabled, they will have no messages tab and other users cannot message them after searching for them
        tb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tb2.isChecked())
                {
                    url ="http://188.226.144.157/group1/changepreferences.php?id="+ID+"&code=3";
                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast t = new Toast(getApplicationContext());
                            t.makeText(getApplicationContext(), "Preferences Updated.", Toast.LENGTH_SHORT).show();
                            Log.i("r", "Can Message");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("", "onERRORResponse upload: " + error.toString());
                        }
                    });
                    requestQueue.add(Stringrequest);
                }
                else
                {
                    url ="http://188.226.144.157/group1/changepreferences.php?id="+ID+"&code=2";
                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast t = new Toast(getApplicationContext());
                            t.makeText(getApplicationContext(), "Preferences Updated.", Toast.LENGTH_SHORT).show();
                            Log.i("r", "Cannot Message");
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
        });
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


    }

    public void readFile() {
        try {
            InputStream inputStream = openFileInput("imgggg.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                data = stringBuilder.toString();


            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
    public void LoadImage() {
        //TODO: script for downloading image
        int count = 0, i = data.indexOf("ID:")+4;
        while(data.charAt(i)!='P')
        {
            count=count+1;
            i=i+1;
        }

        String ID = data.substring(data.indexOf("ID:")+4, (data.indexOf("ID:")+4)+(count-1));

        final String url1 = "http://188.226.144.157/group1/downloadphoto.php?id=" + ID;

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imagestring = response;
                imagestring = imagestring.substring(imagestring.indexOf('[')+2, imagestring.length()-2);
                imagestring = imagestring.replaceAll("\\s+","+");
                imagestring = imagestring.replace("\\", "");
                Log.i("imagestring", imagestring);
                byte[] decodedString = Base64.decode(imagestring, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv.setImageBitmap(decodedByte);
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
