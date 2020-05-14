package malaksadek.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import malaksadek.Contacts.R;
public class ConnectFragment extends Fragment {

//Uses same logic as ContactsFragment, except that after finding user's contacts which are users,
// it then repeats the process for those users and shows their contacts which are users to the
// current user (contacts of contacts)

    String data;
    String ID;
    String names[], icons[], numbers[], emails[];
    ArrayList<Item> contacts, randoms;
    ListView list;
    String tocall, tomsg, toemail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_contacts_fragment, container, false);

        list = (ListView) v.findViewById(R.id.list);

        contacts = new ArrayList<>();
        randoms = new ArrayList<>();
        names = new String[100];
        icons = new String[100];
        numbers = new String[100];
        emails = new String[100];
        readFile();
        int count = 0;
        int j = data.indexOf("ID:") + 4;
        while (data.charAt(j) != 'P') {
            count = count + 1;
            j = j + 1;
        }

        ID = data.substring(data.indexOf("ID:") + 4, (data.indexOf("ID:") + 4) + (count - 1));


        fetch_contacts();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                numbers[position] = numbers[position].replaceAll("\\s+", "");
                tocall = numbers[position];
                toemail = emails[position];
                ContactDialogFragment dialog = ContactDialogFragment.newInstance();
                dialog.getNumber(tocall);
                dialog.getEmail(toemail);
                dialog.show(getFragmentManager(), "fragmentDialog");
            }
        });


        return v;
    }

    public void fetch_contacts() {

        String url1 = "http://188.226.144.157/group1/fetch_contacts.php?id=" + ID;
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JSONArray jsonArr = new JSONArray();

        JsonArrayRequest jsonArrRequest = new JsonArrayRequest(Request.Method.POST, url1, jsonArr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {
                findcontactnumbers(response1);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("", "onERRORResponse register: " + error.toString());
            }
        });
        requestQueue.add(jsonArrRequest);
    }

    public void readFile() {
        try {
            InputStream inputStream = getContext().openFileInput("imgggg.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                data = stringBuilder.toString();


            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public void findcontactnumbers (JSONArray response1) {
        String t[] = new String[response1.length()];

        for (int i = 0; i < response1.length(); i = i + 1) {

            try {
                t[i] = response1.get(i).toString();
                Log.i("response1: ", t[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url1 = "http://188.226.144.157/group1/search_Contact_ID.php?id=" + t[i];
            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JSONArray jsonArr = new JSONArray();

            final int finalI = i;
            JsonArrayRequest jsonArrRequest = new JsonArrayRequest(Request.Method.POST, url1, jsonArr, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response2) {
                    findcontactsasusers(response2, finalI);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("", "onERRORResponse register: " + error.toString());
                }
            });
            requestQueue.add(jsonArrRequest);
        }
    }

    public void findcontactsasusers (final JSONArray response2, final int finalI) {
        Log.i("response2: ", response2.toString());

        String phone = "";
        try {
            phone = response2.get(2).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url1 = "http://188.226.144.157/group1/search.php?User_Number=" + phone;
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        final String finalPhone = phone;
        StringRequest StringRequest = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response3) {
                try {
                    updatelist(response3, finalI, response2, finalPhone);
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
        requestQueue.add(StringRequest);

    }

    public void updatelist(String response3, int finalI, JSONArray response2, String phone) throws JSONException {
        Log.i("response3: ", response3.toString());
        if (!response3.toString().contains("User Not Found")) {
            int count = 0;
            int i = response3.toString().indexOf("User_Name") + 12;
            while (response3.toString().charAt(i) != '"') {
                count = count + 1;
                i = i + 1;
            }
            numbers[finalI] = phone;
            emails[finalI] = response2.get(3).toString();

            Item temp = new Item();
            temp.name = response3.substring(response3.indexOf("User_Name") + 12, response3.indexOf("User_Name") + 12 + (count));

            count = 0;
            i = response3.toString().indexOf("User_Photo") + 12;
            while (response3.toString().charAt(i) != '"') {
                count = count + 1;
                i = i + 1;
            }
            temp.icon = response3.substring(response3.indexOf("User_Photo") + 12, response3.indexOf("User_Photo") + 12 + (count));
            contacts.add(0, temp);

            for (int k = 0; k < contacts.size(); k = k + 1) {

                count = 0;
                i = response3.toString().indexOf("User_ID") + 10;
                while (response3.toString().charAt(i) != '"') {
                    count = count + 1;
                    i = i + 1;
                }

                String url1 =  "http://188.226.144.157/group1/fetch_contacts.php?id=" + response3.substring(response3.indexOf("User_ID") + 10, response3.indexOf("User_ID") + 10 + (count));

                com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                JSONArray jsonArr = new JSONArray();

                JsonArrayRequest jsonArrRequest = new JsonArrayRequest(Request.Method.POST, url1, jsonArr, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response1) {

                        String t[] = new String[response1.length()];

                        for (int i = 0; i < response1.length(); i = i + 1) {

                            try {
                                t[i] = response1.get(i).toString();
                                Log.i("response1: ", t[i]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String url1 = "http://188.226.144.157/group1/search_Contact_ID.php?id=" + t[i];
                            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                            JSONArray jsonArr = new JSONArray();

                            final int finalI = i;
                            JsonArrayRequest jsonArrRequest = new JsonArrayRequest(Request.Method.POST, url1, jsonArr, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response2) {
                                    Log.i("response2: ", response2.toString());

                                    String phone = "";
                                    try {
                                        phone = response2.get(2).toString();
                                        numbers[finalI] = phone;
                                        emails[finalI] = response2.get(3).toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String url1 = "http://188.226.144.157/group1/search.php?User_Number=" + phone;
                                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                                    StringRequest StringRequest = new StringRequest(url1, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response3) {

                                            Log.i("response3: ", response3.toString());
                                            if (!response3.toString().contains("User Not Found")) {
                                                int count = 0;
                                                int i = response3.toString().indexOf("User_Name") + 12;
                                                while (response3.toString().charAt(i) != '"') {
                                                    count = count + 1;
                                                    i = i + 1;
                                                }

                                                Item temp = new Item();
                                                temp.name = response3.substring(response3.indexOf("User_Name") + 12, response3.indexOf("User_Name") + 12 + (count));


                                                count = 0;
                                                i = response3.toString().indexOf("User_Photo") + 12;
                                                while (response3.toString().charAt(i) != '"') {
                                                    count = count + 1;
                                                    i = i + 1;
                                                }
                                                temp.icon = response3.substring(response3.indexOf("User_Photo") + 12, response3.indexOf("User_Photo") + 12 + (count));

                                                randoms.add(temp);
                                            }

                                            ListViewAdapter CA = new ListViewAdapter(getContext(), randoms);

                                            list.setAdapter(CA);
                                        }

                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("", "onERRORResponse register: " + error.toString());
                                        }
                                    });
                                    requestQueue.add(StringRequest);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("", "onERRORResponse register: " + error.toString());
                                }
                            });
                            requestQueue.add(jsonArrRequest);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("", "onERRORResponse register: " + error.toString());
                    }
                });
                requestQueue.add(jsonArrRequest);
            }
        }
    }

}