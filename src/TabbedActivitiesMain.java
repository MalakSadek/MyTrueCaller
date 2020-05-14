package malaksadek.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import malaksadek.Contacts.R;
public class TabbedActivitiesMain extends AppCompatActivity {
    private FragmentTabHost mTabHost;
    String data;
    String permission;
    String j = "";
    Intent profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabbed_layout);

        readFile();
        permissions(data);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(getApplicationContext(), getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("SEARCH").setIndicator("Search", null),
                SearchFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("CONTACT").setIndicator("Contact", null),
                ContactsFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("CONNECT").setIndicator("Connect", null),
                ConnectFragment.class, null);

        mTabHost.setCurrentTab(0);

        switch (mTabHost.getId()) {
            case 0:
            {
                getSupportActionBar().setTitle("SuperContactsList - Search");
                getSupportActionBar().setLogo(R.drawable.searchicon);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
            case 1:
            {
                getSupportActionBar().setTitle("SuperContactsList - Contacts");
                getSupportActionBar().setLogo(R.drawable.contactsicon);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
            case 2:
            {
                getSupportActionBar().setTitle("SuperContactsist - Connect");
                getSupportActionBar().setLogo(R.drawable.connecticon);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
            case 3:
            {
                getSupportActionBar().setTitle("SuperContactsList - Chats");
                getSupportActionBar().setLogo(R.drawable.chaticon);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
        }

    }

    //Reads user's file from phone
    public void readFile() {
        try {
            InputStream inputStream = openFileInput("imgg.txt");
            Log.d("", "onResponse save_id: " + data);
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
                int count = 0;
                int i = data.indexOf("Phone:")+7;
                while(data.charAt(i)!='E')
                {
                    count=count+1;
                    i=i+1;
                }
                data=data.substring(data.indexOf("Phone:")+7, (data.indexOf("Phone:")+7)+(count-1));
                Log.d("", "onResponse save_id: " + data);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }

    //Checks whether user has allowed messaging or not (from profile activity) to show chat tab
    public void permissions(String data)
    {
        String url1 = "http://188.226.144.157/group1/search.php?User_Number="+data;

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonObj = new JSONObject();

        try
        {
            jsonObj = new JSONObject();
        }
        catch (Exception e)
        {
        }

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, url1, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                permission = response.toString();
                permission = permission.substring(permission.indexOf("Can_Message")+14,permission.indexOf("Can_Message")+15);
                Log.d("", "onResponse permission: " + permission);

                //If messaging is allowed, a chat tab is created
                if(permission.contains("1")) {
                    mTabHost.addTab(
                            mTabHost.newTabSpec("CHATS").setIndicator("Chats", null),
                            ChatFragment.class, null);
                    Log.d("", String.valueOf(mTabHost.isClickable()));
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
        requestQueue.add(jsonObjRequest);
    }

    //Creates action button in the toolbar to go to profile activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        profile = new Intent(this, ProfileActivity.class);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(profile);
        return super.onOptionsItemSelected(item);
    }
}