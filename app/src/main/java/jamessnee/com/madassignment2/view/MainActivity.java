package jamessnee.com.madassignment2.view;

import android.content.Intent;
import android.database.Cursor;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jamessnee.com.madassignment2.R;
import jamessnee.com.madassignment2.model.AppData;
import jamessnee.com.madassignment2.model.DatabaseHandler;
import jamessnee.com.madassignment2.model.Movie;


public class MainActivity extends ActionBarActivity {

    //movie vars
    private TextView rating;
    private ArrayAdapter<Movie> adapter;
    private ArrayList<Movie> movies;
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

        populateMovieData();


    }

    private void populateMovieData(){

        Movie movie1 = new Movie("Star Wars Episode V: The Empire Strikes Back", 1980,"The second installment in the" +
                " Star Wars series.",
                "After the Rebel base on the icy planet Hoth is taken over by the empire, Han, Leia, " +
                        "Chewbacca, and C-3PO flee across the galaxy from the Empire. Luke travels to the forgotten " +
                        "planet of Dagobah to receive training from the Jedi master Yoda, while Vader endlessly " +
                        "pursues him.",R.drawable.esb,"tt0080684", 0, null);



        Movie movie2 = new Movie("The Terminator", 1984,"The first installment in the" +
                " Terminator series.",
                "A cyborg is sent from the future on a deadly mission. He has to kill Sarah Connor, a young woman " +
                        "whose life will have a great significance in years to come. Sarah has only one protector - " +
                        "Kyle Reese - also sent from the future. The Terminator uses his exceptional intelligence and " +
                        "strength to find Sarah, but is there any way to stop the seemingly indestructible " +
                        "cyborg?", R.drawable.terminator,"tt0088247", 0, null);

        Movie movie3 = new Movie("Frozen", 2013,"Disney/Pixar animated film.",
                "Anna, a fearless optimist, sets off on an epic journey - teaming up with rugged mountain " +
                        "man Kristoff and his loyal reindeer Sven - to find her sister Elsa, whose icy powers have " +
                        "trapped the kingdom of Arendelle in eternal winter. Encountering Everest-like conditions, " +
                        "mystical trolls and a hilarious snowman named Olaf, Anna and Kristoff battle the " +
                        "elements in a race to save the kingdom. From the outside Anna's sister, " +
                        "Elsa looks poised, regal and reserved, but in reality, she lives in fear as she " +
                        "wrestles with a mighty secret-she was born with the power to create ice and snow. " +
                        "It's a beautiful ability, but also extremely dangerous. Haunted by the moment her " +
                        "magic nearly killed her younger sister Anna, Elsa has isolated herself, spending every " +
                        "waking minute trying to suppress her growing powers. Her mounting emotions trigger " +
                        "the magic, accidentally setting off an eternal winter that she can't stop. She fears " +
                        "she's becoming a monster and that no one, not even her sister, " +
                        "can help her.",R.drawable.frozen,"tt2294629", 0, null);

        Movie movie4 = new Movie("The Lion King", 1994,"Disney 2D Animated film.",
                "A young lion Prince is cast out of his pride by his cruel uncle, who claims he killed his " +
                        "father. While the uncle rules with an iron fist, the prince grows up beyond the savannah, " +
                        "living by a philosophy: No worries for the rest of your days. But when his past " +
                        "comes to haunt him, the young Prince must decide his fate: will he remain an outcast, " +
                        "or face his demons and become what he needs to be?",R.drawable.lionking,"tt0110357", 0, null);

        Movie movie5 = new Movie("The Shawshank Redemption", 1994,"Frank Darabont's prison film.",
                "Andy Dufresne is a young and successful banker whose life changes drastically when he is " +
                        "convicted and sentenced to life imprisonment for the murder of his wife and her " +
                        "lover. Set in the 1940s, the film shows how Andy, with the help of his friend Red, " +
                        "the prison entrepreneur, turns out to be a most unconventional " +
                        "prisoner.",R.drawable.shawshank,"tt0111161", 0, null);

        Movie movie6 = new Movie("The Dark Knight", 2008,"Heath Ledger's last film appearance.",
                "Batman raises the stakes in his war on crime. With the help of Lieutenant Jim Gordon and " +
                        "District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal " +
                        "organizations that plague the city streets. The partnership proves to be effective, " +
                        "but they soon find themselves prey to a reign of chaos unleashed by a rising " +
                        "criminal mastermind known to the terrified citizens of Gotham " +
                        "as The Joker.",R.drawable.darkknight,"tt0468569", 0, null);

        Movie movie7 = new Movie("Pulp Fiction", 1994,"Quentin Tarantino's breakout hit.",
                "Jules Winnfield and Vincent Vega are two hitmen who are out to retrieve a suitcase " +
                        "stolen from their employer, mob boss Marsellus Wallace. Wallace has also asked " +
                        "Vincent to take his wife Mia out a few days later when Wallace himself will be " +
                        "out of town. Butch Coolidge is an aging boxer who is paid by Wallace to lose " +
                        "his next fight. The lives of these seemingly unrelated people are woven " +
                        "together comprising of a series of funny, bizarre and " +
                        "uncalled-for incidents.",R.drawable.pulpfiction,"tt0110912", 0, null);

        Movie movie8 = new Movie("Bad Behaviour", 2010,"The programmer worked on this movie.",
                "Emma and Peterson encounter their fierce predator Voyte Parker, a cop " +
                        "confronts his son's murderer, and a man finds his wife is cheating on him. " +
                        "Intersecting story lines; murderers, coppers, teachers " +
                        "and teenagers.",R.drawable.badbehaviour,"tt1621418", 0, null);


        DatabaseHandler.getInstance(this).insertMovieData(movie1);
        DatabaseHandler.getInstance(this).insertMovieData(movie2);
        DatabaseHandler.getInstance(this).insertMovieData(movie3);
        DatabaseHandler.getInstance(this).insertMovieData(movie4);
        DatabaseHandler.getInstance(this).insertMovieData(movie5);
        DatabaseHandler.getInstance(this).insertMovieData(movie6);
        DatabaseHandler.getInstance(this).insertMovieData(movie7);
        DatabaseHandler.getInstance(this).insertMovieData(movie8);

    }

    private void populateListView(){

        //if connected to internet, populate with data from get request
        if (isConnected()){

        }
        //if not connected, populate with data from database store
        else {

        }

        Cursor data = DatabaseHandler.getInstance(this).retrieveAllData();

        if (data.getCount() == 0){

        }

        //Go through retrieved movie details and populate the list
        movies = new ArrayList<Movie>();

        while(data.moveToNext()){

            Movie temp = new Movie(data.getString(1), data.getInt(2), data.getString(3), data.getString(4),
                    data.getInt(5), data.getString(0), data.getInt(6), null);

            movies.add(temp);

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


        public MyListAdapter() {
            super(MainActivity.this, R.layout.list_item, movies);
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

                    Movie clickedMovie = movies.get(position);

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
            final Movie currentMovie = movies.get(position);

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
                    movies.get(position).setRating((int) rating);
                    currentMovie.setRating((int) rating);

                    //refresh list to reflect new rating




                    Toast.makeText(getApplicationContext(), "The rating was changed to " +
                            movies.get(position).getRating(), Toast.LENGTH_SHORT).show();


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
                return null;
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
            return GET(urls[0]);
        }

        protected void onPostExecute(String result){
            //get movie object from JSON data
            Movie retrievedMovie = parseJSON(result);

            if (retrievedPoster != null){
                posterImage.setImageBitmap(retrievedPoster);
            }

            if (retrievedMovie != null){
                //set movie to current listadapter
                adapter.add(retrievedMovie);
                adapter.notifyDataSetChanged();
                //set movie to database
                DatabaseHandler.getInstance(getApplicationContext()).insertMovieData(retrievedMovie);
                copyDatabase();
            }
            else {
                Toast.makeText(getBaseContext(), "Movie not found!", Toast.LENGTH_LONG).show();
            }

        }
    }


    //Parse JSON into a movie object
    public Movie parseJSON(String input){

        Movie retrieved;

        try {
            //Create JSON object from input string
            JSONObject json = new JSONObject(input);

            String title = json.optString("Title").toString();
            int year = json.optInt("Year");
            String plot = json.optString("Plot").toString();
            String id = json.optString("imdbID").toString();
            imageUrl = json.optString("Poster").toString();

            retrieved = new Movie(title, year, plot, null, 0, id, 0, null);
            return retrieved;



        }
        catch(JSONException e){
            e.printStackTrace();
            return null;
        }



    }

    //This method is to get around having to enable root access to device, copy database file to
    //documents folder on the device to ensure expected data appears in database. For testing only.
    public void copyDatabase() {

        //get source path and dest path
        String databasePath = getApplicationContext().getDatabasePath(DatabaseHandler.DATABASE_NAME).getPath();
        File db = new File(databasePath);

        //input and output streams
        InputStream in = null;
        OutputStream out = null;

        //check if our file exists
        if (db.exists()){

            try {

                File dir = new File("/mnt/sdcard/DB_DEBUG");

                if(!dir.exists()){
                    dir.mkdir();
                }

                in = new FileInputStream(databasePath);
                out = new FileOutputStream(dir.getAbsolutePath() + "/" + DatabaseHandler.DATABASE_NAME);


                byte[] buffer = new byte[1024];
                int length;

                while((length = in.read(buffer)) > 0){
                    out.write(buffer, 0, length);
                }

                out.flush();
            }
            catch(Exception e){

            }finally {

                try{
                    if (in != null){
                        in.close();
                        in = null;
                    }
                    if(out != null){
                        out.close();
                        out = null;
                    }
                }
                catch(Exception e){

                }

            }

        }

        Toast.makeText(getApplicationContext(), "Copied file " + DatabaseHandler.DATABASE_NAME + " from " +
                databasePath + " to mnt/sdcard/DB_DEBUG", Toast.LENGTH_LONG).show();

    } //end copyDatabase


}
