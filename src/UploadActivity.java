package malaksadek.contacts;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import malaksadek.Contacts.R;
public class UploadActivity extends AppCompatActivity {

    EditText ET;
    TextView tv, tvp;
    Button done;
    ProgressBar prgrs;

    int code, currentcontactID, index = 0, j;
    ArrayList<User> user = new ArrayList<User>();
    String temp_Email, temp_Phone;
    User user_temp;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("SuperContactsList - Upload");
        getSupportActionBar().setLogo(R.drawable.appicon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Get the verification code sent from the previous activity
        Intent i = getIntent();
        code = i.getIntExtra("code", 0);


        ET = (EditText) findViewById(R.id.editText3);
        done = (Button) findViewById(R.id.done);
        done.setClickable(false);
        prgrs = (ProgressBar) findViewById(R.id.progressBar);
        tvp = (TextView) findViewById(R.id.textView5);
        done.setText("Please Wait");
        done.setClickable(false);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TabbedActivitiesMain.class);
                startActivity(intent);
            }
        });

        Button verify = (Button) findViewById(R.id.button2);
        tv = (TextView) findViewById(R.id.textView2);
        tv.setText("Enter Verification Code");
        progressStatus = 10;
        prgrs.setProgress(progressStatus);
        tvp.setText(Integer.toString(progressStatus)+"%");
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressStatus = 20;
                prgrs.setProgress(progressStatus);
                tvp.setText(Integer.toString(progressStatus)+"%");
                if (Integer.parseInt(ET.getText().toString()) == code) {
                    Toast t = new Toast(getApplicationContext());
                    t.makeText(getApplicationContext(), "Verified!", Toast.LENGTH_SHORT).show();
                    try {
                        Contacts(); //Fetch contacts from phone
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Search for contacts in database to avoid adding duplicates
                    final String url = "http://188.226.144.157/group1/search_Contact.php?User_Number="+user.get(0).getPhone_Number();

                        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                response = response.replaceAll("\\s+", "");

                                //First contact
                                if (response.contains("UserNotFound!")) {
                                    get_ID(0);
                                    j = j + 1;
                                } else {
                                    Toast t = new Toast(getApplicationContext());
                                    t.makeText(getApplicationContext(), "Duplicate Number Already in Database", Toast.LENGTH_LONG).show();
                                    try {
                                        int count = 0, i = response.indexOf("[")+2;
                                        while(response.charAt(i)!='"')
                                        {
                                            count=count+1;
                                            i=i+1;
                                        }
                                        //Updates the relations table (that contact x belongs to user y)
                                        update_Relations(Integer.parseInt(response.substring(response.indexOf("[")+2,response.indexOf("[")+2+(count))));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                //Rest of the contacts
                                }
                                for (int i = 1; i < user.size(); i = i + 1) {

                                    final String url = "http://188.226.144.157/group1/search_Contact.php?User_Number=" + user.get(i).getPhone_Number();

                                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                                    final int finalI = i;
                                    StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            response = response.replaceAll("\\s+", "");
                                            Log.i("r", response);
                                            if (response.contains("UserNotFound!")) {
                                                get_ID(finalI);
                                                j = j + 1;
                                            } else {
                                                Toast t = new Toast(getApplicationContext());
                                                t.makeText(getApplicationContext(), "Duplicate Number Already in Database", Toast.LENGTH_LONG).show();
                                                try {

                                                    int count = 0, i = response.indexOf("[")+2;
                                                    while(response.charAt(i)!='"')
                                                    {
                                                        count=count+1;
                                                        i=i+1;
                                                    }
                                                    update_Relations(Integer.parseInt(response.substring(response.indexOf("[")+2,response.indexOf("[")+2+(count))));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("", "onERRORResponse register: " + error.toString());
                                        }
                                    });
                                    requestQueue.add(Stringrequest);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("", "onERRORResponse register: " + error.toString());
                            }
                        });
                        requestQueue.add(Stringrequest);

                } else {
                    Toast t = new Toast(getApplicationContext());
                    t.makeText(getApplicationContext(), "Failed to verify, please check the code and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    void Contacts() throws InterruptedException {

        progressStatus = 20;
        prgrs.setProgress(progressStatus);
        tvp.setText(Integer.toString(progressStatus)+"%");
        tv.setText("Uploading Contacts.");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            user_temp = new User();
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id},
                    null);

            user_temp.setName(name);

            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                temp_Phone = phoneNumber;
            }

            user_temp.setPhone_Number(temp_Phone);

            Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id},
                    null);

            int size_email = phoneCursor.getCount();

            int j = 0;
            while (emailCursor.moveToNext() && size_email != 0) {
                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                temp_Email = email;
                j++;
            }

            user_temp.setEmail(temp_Email);
            user.add(index, user_temp);

            index++;
            progressStatus = progressStatus+5;
            prgrs.setProgress(progressStatus);
            tvp.setText(Integer.toString(progressStatus)+"%");
        }
    }

    //Same function as previous activity
    public void get_ID(final int i)
    {
        progressStatus = progressStatus+2;
        prgrs.setProgress(progressStatus);
        tvp.setText(Integer.toString(progressStatus)+"%");
        String url = "http://188.226.144.157/group1/find_ID_Contact.php";

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONArray jsonArray = new JSONArray();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("", "onResponse save_id: " +Integer.toString(i) + response.toString());
                try {
                    currentcontactID = Integer.parseInt(response.getString(0));
                    add_contact(i);
                    Log.i("CURRENT ID: ", Integer.toString(currentcontactID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("", "onERRORResponse save_id: " + error.toString());
            }
        }) {

        };
        requestQueue.add(jsonArrayRequest);
    }

    //Adds contact to contact table if it is not a duplicate
    public void add_contact(final int i)
    {
        progressStatus = progressStatus+2;
        prgrs.setProgress(progressStatus);
        tvp.setText(Integer.toString(progressStatus)+"%");
        final String url = "http://188.226.144.157/group1/upload_contact.php?name="+user.get(i).getName()+"&id="+Integer.toString(currentcontactID+j)+"&number="+user.get(i).getPhone_Number()+"&email="+user.get(i).getEmail();

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONArray jsonArray = new JSONArray();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String temp = response.toString();
                    int count = 0, i = temp.indexOf("[")+2;
                    while(temp.charAt(i)!='"')
                    {
                        count=count+1;
                        i=i+1;
                    }
                    update_Relations(Integer.parseInt(temp.substring(temp.indexOf("[")+2,temp.indexOf("[")+2+(count))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    //Updates the relations table (contact x belongs to user y)
    public void update_Relations(int i) throws IOException {
        progressStatus = progressStatus+2;
        prgrs.setProgress(progressStatus);
        tvp.setText(Integer.toString(progressStatus)+"%");
        String userID = get_User_ID();
        final String url = "http://188.226.144.157/group1/update_Relation.php?userid="+userID+"&contactid="+Integer.toString(i);

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressStatus = progressStatus+(100-progressStatus);
                prgrs.setProgress(progressStatus);
                tvp.setText(Integer.toString(progressStatus)+"%");
                tv.setText("Done!");
                done.setText("Continue");
                done.setClickable(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(Stringrequest);
    }

    //Extracts the current user's ID from the file on the phone written in the previous activity
    public String get_User_ID() throws IOException {
        String temp = "";
        FileInputStream inputStream = openFileInput("imgggg.txt");
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            StringBuilder stringBuilder = new StringBuilder();

            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }

            inputStream.close();
            temp = stringBuilder.toString();
        }

        String sub = temp.substring(temp.indexOf("ID: ")+4);
        return sub;
    }
}

