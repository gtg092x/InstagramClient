package com.yahoo.training.mdrake.instagramclient;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends ActionBarActivity {
    private SwipeRefreshLayout swipeContainer;
    private InstagramClient client;
    private ArrayList<InstagramPhoto> photos;
    ListView instagramListView;
    InstagramPhotosAdapter instagramListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client =  new InstagramClient(this.getApplicationContext());
        photos = new ArrayList<InstagramPhoto>();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        instagramListView = (ListView)findViewById(R.id.lvPhotos);

        instagramListViewAdapter = new InstagramPhotosAdapter(this.getApplicationContext(), photos);

        instagramListView.setAdapter(instagramListViewAdapter);

        fetchTimelineAsync(0);

    }

    private InstagramPhoto parsePhoto(JSONObject obj) throws JSONException{

        InstagramPhoto photo = new InstagramPhoto();

        photo.id = obj.getString("id");
        photo.created_time = Integer.parseInt(obj.getString("created_time"));
        JSONObject std = obj.getJSONObject("images").getJSONObject("standard_resolution");
        photo.standard_resolution_height = std.getInt("height");
        photo.standard_resolution_url = std.getString("url");
        JSONObject user = obj.getJSONObject("user");
        photo.user_profile_picture = user.getString("profile_picture");
        photo.username = user.getString("username");
        if(!obj.isNull("caption")){
            JSONObject caption = obj.getJSONObject("caption");
            if(!caption.isNull("text")) {
                photo.caption = caption.getString("text");
            }
        }


        return photo;
    }



    private void addOrUpdateUniquePhotoInOrder(InstagramPhoto newPhoto){
        InstagramPhoto existingPhoto = null;
        int existingIndex = 0;
        for (int i =0; i < photos.size(); i++) {
            InstagramPhoto photo = photos.get(i);
            if(newPhoto.id.equals(photo.id)){
                existingPhoto = photo;
                existingIndex = i;
                break;
            }
        }

        if(existingPhoto != null){
            photos.remove(existingIndex);
        }
        photos.add(newPhoto);

        Collections.sort(photos, new Comparator<InstagramPhoto>() {
            @Override
            public int compare(InstagramPhoto lhs, InstagramPhoto rhs) {
                return rhs.created_time - lhs.created_time;
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {

                swipeContainer.setRefreshing(false);


                try{
                    JSONArray data = json.getJSONArray("data");
                    photos.clear();
                    for(int i = 0; i < data.length(); i++){
                        JSONObject obj = data.getJSONObject(i);

                        InstagramPhoto photo = parsePhoto(obj);
                        photos.add(photo);
                        // removing this because timestamps don't create an atomic list
                        // addOrUpdateUniquePhotoInOrder(photo);

                    }
                }catch(JSONException ex){
                    toastError(ex.getMessage());
                }catch(Exception ex){
                    toastError(ex.getMessage());
                }

                instagramListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                swipeContainer.setRefreshing(false);
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }

    void toastError(String message){
        Log.e("SHOULD_TOAST",message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
