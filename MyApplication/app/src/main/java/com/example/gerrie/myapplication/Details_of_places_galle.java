package com.example.gerrie.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gerrie on 10/16/2018.
 */

public class Details_of_places_galle extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private TextView textView_description;
    private Button Calendar_event;
    private android.support.v7.widget.Toolbar toolbar;
    private Button Directions;
    private Button Favourite;
    public static String ToDo_event_task_name_galle="";

    RequestQueue rq;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;

    String request_url = details_seeker_galle.destination_url_around_galle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galle_details_slider);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.description_toolbar_galle);
        toolbar.setTitle(details_seeker_galle.destination_name_around_galle);

        rq = CustomVolleyRequest.getInstance(this).getRequestQueue();

        sliderImg = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.viewPager_galle);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots_galle);

        Calendar_event=(Button) findViewById(R.id.add_calendar_event_galle);

        Directions = (Button) findViewById(R.id.directions_galle);

        Favourite = (Button) findViewById(R.id.favourite_galle);

        textView_description=(TextView) findViewById(R.id.textView_description_des_galle);
        textView_description.setText(details_seeker_galle.destination_description_around_galle);

        sendRequest();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //String my_data= String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=6.8994217,80.4985535(My Destination Place)");
              String my_data= String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr="+details_seeker_galle.destination_latitude_around_galle+","+details_seeker_galle.destination_longitude_around_galle)+"("+details_seeker_galle.destination_name_around_galle+")";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_data));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDBHelper_favourite mydb_favourite;
                mydb_favourite = new TaskDBHelper_favourite(getApplicationContext());
                mydb_favourite.insertContact(details_seeker_galle.destination_name_around_galle, "31/12/2050");
                toast(details_seeker_galle.destination_name_around_galle+" is added to Favourite Destinations");
            }
        });

        Calendar_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Details_of_places_galle.this);
                alertDialog.setTitle("Add this destination as an event");
                alertDialog.setMessage("Select the event type")
                        .setPositiveButton("ToDo event",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        ToDo_event_task_name_galle="Travel to "+details_seeker_galle.destination_name_around_galle;
                                        startActivity(new Intent(Details_of_places_galle.this, AddTask.class));

                                        toast("Fix a date for the event manually.");
                                    }
                                })

                        .setNegativeButton("Calendar event",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_EDIT);
                                        intent.setType("vnd.android.cursor.item/event");
                                        intent.putExtra("title", "Travel to "+details_seeker_galle.destination_name_around_galle);
                                        intent.putExtra("description", "Reserved this day to visit "+details_seeker_galle.destination_name_around_galle);
                                        startActivity(intent);

                                        toast("Fix a date for the event manually.");
                                    }
                                });

                alertDialog.show();
            }
        });
    }


    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, request_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){

                    SliderUtils sliderUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        sliderUtils.setSliderImageUrl(jsonObject.getString("image_url"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);

                }

                viewPagerAdapter = new ViewPagerAdapter(sliderImg, Details_of_places_galle.this);

                viewPager.setAdapter(viewPagerAdapter);

                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int i = 0; i < dotscount; i++){

                    dots[i] = new ImageView(Details_of_places_galle.this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        CustomVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public void toast(String toast1) {
        Context context = getApplicationContext();
        CharSequence text = toast1;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
