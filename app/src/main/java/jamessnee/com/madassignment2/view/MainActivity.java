package jamessnee.com.madassignment2.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jamessnee.com.madassignment2.R;
import jamessnee.com.madassignment2.model.AppData;
import jamessnee.com.madassignment2.model.Movie;


public class MainActivity extends ActionBarActivity {

    //movie vars
    private TextView rating;
    private ArrayAdapter<Movie> adapter;
    private ListView list;
    private SearchView search;
    private String searchedMovie;
    private ImageView posterImage;
    private String imageUrl;
    private Bitmap retrievedPoster;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void populateListView(){

        //if connected to internet, populate with data from get request
        if (isConnected()){

        }
        //if not connected, populate with data from database store
        else {

        }

        //setup initial adapter with Movie list
        adapter = new MyListAdapter();
        list = (ListView) findViewById(R.id.listViewMain);
        list.setAdapter(adapter);


    }

    //check if connected to internet
    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            Toast.makeText(this, "Connected to internet, will get movie data from OMDB API",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        else {

            Toast.makeText(this, "Not connected to internet, will get movie data from database",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }


    //onResume refresh the listView
    public void onResume() {
        super.onResume();

        search = (SearchView) findViewById(R.id.searchView);
        posterImage = (ImageView) findViewById(R.id.poster);

        //populate movie data in the list view
        populateListView();
        adapter.notifyDataSetChanged();


    }

    //search button pressed
    public void searchButtonPressed(View view) {

        //get text from textbox
        searchedMovie = search.getQuery().toString();

        //Call AsyncTask to perform network operation, once for short plot and once for long plot
        new HttpAsyncTask().execute("http://www.omdbapi.com/?t="+ searchedMovie +"&y=&plot=short&r=json");

    }

    //inner class for MyListAdapter
    private class MyListAdapter extends ArrayAdapter<Movie> {

        //get movies from singleton
        //get movies from GET request
        public MyListAdapter() {
            super(MainActivity.this, R.layout.list_item, AppData.getInstance().getMovies());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //make sure we have a view, if not create one from XML
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            //create onclick listener for list item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Movie clickedMovie = AppData.getInstance().getMovie(position);

                    //put intent here to go to next activity
                    Intent detailIntent = new Intent(MainActivity.this, DetailViewActivity.class);
                    //extra to pass in movie variables
                    detailIntent.putExtra("movieTitle", clickedMovie.getTitle());
                    detailIntent.putExtra("movieDesc", clickedMovie.getFull_plot());
                    detailIntent.putExtra("movieShortPlot", clickedMovie.getShort_plot());
                    detailIntent.putExtra("movieYear", clickedMovie.getYear());
                    detailIntent.putExtra("moviePoster", clickedMovie.getPoster());
                    detailIntent.putExtra("movieRating", clickedMovie.getRating());
                    detailIntent.putExtra("movieID", clickedMovie.getId());
                    detailIntent.putExtra("position", position);
                    startActivity(detailIntent);

                }
            });

            //find movie to work with
            final Movie currentMovie = AppData.getInstance().getMovie(position);

            //fill the view
            //poster
            ImageView poster = (ImageView)itemView.findViewById(R.id.poster);
            poster.setImageResource(currentMovie.getPoster());

            //title
            TextView title = (TextView)itemView.findViewById(R.id.item_title);
            title.setText(currentMovie.getTitle());
            //short_plot
            TextView short_plot = (TextView)itemView.findViewById(R.id.item_short_plot);
            short_plot.setText(currentMovie.getShort_plot());
            //year
            TextView year = (TextView)itemView.findViewById(R.id.item_year);
            year.setText(String.valueOf(currentMovie.getYear()));

            //RatingBar
            RatingBar ratingBar = (RatingBar)itemView.findViewById(R.id.listRatingBar);
            ratingBar.setRating((int) currentMovie.getRating());
            ratingBar.setStepSize(1);

            //fill rating
            rating = (TextView)itemView.findViewById(R.id.ratingText);
            rating.setText("Rating: " + currentMovie.getRating() + "/5 Stars");



            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    ratingBar.setRating((int) rating);
                    //add to model
                    AppData.getInstance().getMovie(position).setRating((int) rating);
                    currentMovie.setRating((int) rating);

                    //refresh list to reflect new rating




                    Toast.makeText(getApplicationContext(), "The rating was changed to " +
                            AppData.getInstance().getMovie(position).getRating(), Toast.LENGTH_SHORT).show();


                }
            });



            return itemView;

        }


    }

    //get method
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";

        try {
            //Create Http Client
            HttpClient httpClient = new DefaultHttpClient();

            //Make get request with url
            HttpResponse response = httpClient.execute(new HttpGet(url));

            //receive response
            inputStream = response.getEntity().getContent();

            //Convert inputstream to string
            if (inputStream != null){
                result = convertInputstreamToString(inputStream);
            }
            else {
                //Throw error
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //Convert input stream to string
    private static String convertInputstreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){

            result += line;
        }

        inputStream.close();
        return result;
    }

    //get a Drawable from a URL
    private static Bitmap drawableFromURL(String url) throws IOException {

        Bitmap bmp;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        bmp = BitmapFactory.decodeStream(input);
        return bmp;

    }


    //Http Async task to run Get operation in separate thread
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            //get movie poster from server
            try {

                retrievedPoster = drawableFromURL(imageUrl);

            }
            catch (IOException e){

                //throw error message

            }

            return GET(urls[0]);
        }

        protected void onPostExecute(String result){
            Toast.makeText(getBaseContext(), "Search result received!", Toast.LENGTH_LONG).show();
            //get movie object from JSON data
            Movie retrievedMovie = parseJSON(result);

            if (retrievedPoster != null){
                posterImage.setImageBitmap(retrievedPoster);
            }

            //set movie to current listadapter
            adapter.add(retrievedMovie);
            adapter.notifyDataSetChanged();
            //set movie to database


        }
    }

    //Parse JSON into a movie object
    public Movie parseJSON(String input){

        Movie retrieved;

        try {
            //Create JSON object from input string
            JSONObject json = new JSONObject(input);
            String title = "";
            int year = 0;
            String plot = "";
            String id = "";


            title = json.optString("Title").toString();
            year = json.optInt("Year");
            plot = json.optString("Plot").toString();
            id = json.optString("imdbID").toString();
            imageUrl = json.optString("Poster").toString();

            retrieved = new Movie(title, year, plot, null, 0, id, 0, null);
            return retrieved;



        }
        catch(JSONException e){
            e.printStackTrace();
            return null;
        }



    }


}
