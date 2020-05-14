package malaksadek.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import malaksadek.Contacts.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    boolean firstTime = true;                               //Checks whether this is the first time for the app to be accessed from this device or not
    RequestParams params = new RequestParams();             //Sends parameters to POST method
    int sms;                                                //Sends correct sms # to next activity to verify
    String encodedString, data;                             //encodedString is for storing the image in the database, data is for storing the image from the gallery and displaying it in the app
    Bitmap bitmap;                                          //To display chosen image from gallery
    List<String> countries;                                 //Holds the country options for the spinner
    EditText name, phone, email;
    Button register;
    OutputStreamWriter outputStreamWriter;
    InputStream inputStream;
    String j = "", SMS, imgPath, fileName;
    int currentID;                                          //The last ID stored in the database, the incoming user should be stored as currentID+1
    User u;
    Intent intent;
    ArrayList<User> users;
    Gson gson;
    File testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To display activity name and application icon in the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.appicon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("SuperContactList - Register");
        intent = new Intent(MainActivity.this, UploadActivity.class);

        //Checks if first time the app is opened
        try {
            firstTime = checkFirstTime();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Asks for permission to read from external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }

                int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 74;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        if (firstTime) {
            u = new User();
            gson = new Gson();
            users = new ArrayList<User>();
            register = (Button) findViewById(R.id.button);

            //Logic for the spinner
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(this);
            countries = new ArrayList<String>();
            countries.add("Egypt");
            countries.add("England");
            countries.add("New York");
            countries.add("California");
            countries.add("Spain");
            countries.add("France");
            countries.add("China");
            countries.add("Brazil");
            countries.add("India");
            countries.add("Russia");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            register.setOnClickListener(this);
        }
        else
        {
            //If not the first time app opened, then user already registered, go straight to tabbed activity
            Intent intenttabs = new Intent(MainActivity.this, TabbedActivitiesMain.class);
            startActivity(intenttabs);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        u.setCity(countries.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Checks if the file is empty or it contains something
    public boolean checkFirstTime() throws FileNotFoundException {
        testFile = new File(this.getExternalFilesDir(null), "imgggg.txt");
        if (testFile.exists())
            return false;
        else
            return true;
    }

    //If file is empty or does not exist, it creates a file with the user's info
    private void createFile(String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true));
            writer.write(data);
            writer.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    public void onClick(View v) {

        //Get user information entered
        name = (EditText) findViewById(R.id.editText);
        phone = (EditText) findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText4);

        u.setName(name.getText().toString());
        u.setPhone_Number(phone.getText().toString());
        u.setEmail(email.getText().toString());


        //Checks that the user's phone number is not already registered in the database
        final String url = "http://188.226.144.157/group1/search.php?User_Number="+u.getPhone_Number();

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replaceAll("\\s+","");
              if(response.contains("UserNotFound!"))
              {
                  get_id();
              }
              else
              {
                  Toast t = new Toast(getApplicationContext());
                  t.makeText(getApplicationContext(), "Number Already Registered!", Toast.LENGTH_LONG).show();

                  UserDialogFragment d = UserDialogFragment.newInstance();
                  d.getData(data);
                  d.getName(u.getName());
                  d.show(getSupportFragmentManager(), "Dialog");
              }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(Stringrequest);

    }

    //Adds user to database
    public void register_user()
    {
        final String url = "http://188.226.144.157/group1/register_user.php?name="+u.getName()+"&id="+Integer.toString(currentID+1)+"&number="+u.getPhone_Number()+"&email="+u.getEmail()+"&city="+u.getCity()+"&photo="+encodedString;

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                triggerImageUpload();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(Stringrequest);
    }

    //Sends verification SMS to user
    public void sendSMS() throws JSONException {
        String url = "http://188.226.144.157/group1/sms.php?num=" + u.getPhone_Number()+"&country="+u.getCity();
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                SMS = response.toString();
                SMS = SMS.substring(SMS.indexOf(":") + 1, SMS.indexOf("}"));
                sms = Integer.parseInt(SMS);
                intent.putExtra("code", sms);
                startActivity(intent);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse sms: " + error.toString());
            }
        });
        requestQueue.add(Stringrequest);
    }

    //Finds current ID in database so current user can be currentID+1
    public void get_id()
    {
        String url = "http://188.226.144.157/group1/find_ID.php";

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONArray jsonArray = new JSONArray();

        try
        {
            jsonArray = new JSONArray(j);
        }
        catch (Exception e)
        {

        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("", "onResponse save_id: " + response.toString());
                try {
                    currentID = Integer.parseInt(response.getString(0));
                    u.setId(currentID);
                    users.add(0,u);
                    j = gson.toJson(users);
                    users.clear();
                    data = "Name: " + name.getText().toString() + " Phone: " + phone.getText().toString() + " Email: " + email.getText().toString() + " City: " + u.getCity() + " ID: " + Integer.toString(currentID+1)+" Photo:  " + encodedString;
                    createFile(data);
                    register_user();
                    Log.i("CURRENT ID: ", Integer.toString(currentID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }, new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            //Log.d("", "onERRORResponse save_id: " + error.toString());
        }
    }) {

    };
        requestQueue.add(jsonArrayRequest);
}

    public void upload_pic(View view) {

        // This opens image applications (the gallery)
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //If an image has been selected
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {

                //Gets the image as data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();

                //Displays the image chosen
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgPath));

                //Gets the file name and its path to upload to database
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                params.put("filename", fileName);
                uploadImage();

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void uploadImage() {
        if (imgPath != null && !imgPath.isEmpty()) {
            encodeImagetoString();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Encodes images as a 64-bit string to upload to database
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                params.put("image", encodedString);
                params.put("id", Integer.toString(currentID+1));
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    //Actually uploads the image
    public void makeHTTPCall() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://188.226.144.157/group1/upload_pic.php",
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(), "Image Uploaded",
                                Toast.LENGTH_LONG).show();

                        try {
                            sendSMS();
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

