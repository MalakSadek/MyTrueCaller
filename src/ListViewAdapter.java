package malaksadek.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import malaksadek.Contacts.R;
/**
 * Created by malaksadek on 6/14/17.
 */
import malaksadek.Contacts.R;
import malaksadek.Contacts.R;
public class ListViewAdapter extends ArrayAdapter<Item> {

    ImageView imageView;
    Item c;

    public ListViewAdapter(Context context, ArrayList<Item> c) {
        super(context, 0, c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        c = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView category = (TextView) convertView.findViewById(R.id.Name);
        imageView = (ImageView) convertView.findViewById(R.id.Icon);

        category.setText(c.name);

        LoadImage();

        return convertView;
    }

    public void LoadImage() {
        //TODO: script for downloading image
        final String url1 = "http://192.168.1.103/downloadphoto.php?id=" + c.icon;

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest Stringrequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String imagestring = response;
                imagestring = imagestring.substring(imagestring.indexOf('[')+2, imagestring.length()-2);
                imagestring = imagestring.replaceAll("\\s+","+");
                imagestring = imagestring.replace("\\", "");
                Log.i("imagestring", imagestring);
                byte[] decodedString = Base64.decode(imagestring, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
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
