package jamessnee.com.madassignment2.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import jamessnee.com.madassignment2.R;
import jamessnee.com.madassignment2.model.AppData;
import jamessnee.com.madassignment2.model.DatabaseHandler;
import jamessnee.com.madassignment2.model.Movie;
import jamessnee.com.madassignment2.model.Party;

public class DetailViewActivity extends ActionBarActivity {

    private String movieIDValue;
    private String titleValue;
    private String descValue;
    private String shortPlotValue;
    private int yearValue;
    private int posterValue;
    private int ratingValue;
    private int position;
    private String imageUrl;
    private Movie retrievedMovie;
    private Bitmap posterFromURL;
    private ImageView displayPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        //extract extra values from intent for display on the DetailView
        //For assignment 2, we could get these directly from the AppData
        Intent intent = getIntent();
        titleValue = intent.getStringExtra("movieTitle");
        descValue = intent.getStringExtra("movieDesc");
        shortPlotValue = intent.getStringExtra("movieShortPlot");
        yearValue = intent.getIntExtra("movieYear", 0);
        posterValue = intent.getIntExtra("moviePoster", 0);
        ratingValue = intent.getIntExtra("movieRating", 0);
        movieIDValue = intent.getStringExtra("movieID");
        position = intent.getIntExtra("position", position);

        RatingBar rating = (RatingBar)findViewById(R.id.detailRatingBar);
        rating.setStepSize(1);
        //set rating regardless of whether we are connected or not
        rating.setRating(ratingValue);

        if(isConnected()){
            //do another OMDB search with the movieID instead
            HttpAsyncTask movieSearch = new HttpAsyncTask();
            movieSearch.execute("http://www.omdbapi.com/?i=" + movieIDValue + "&plot=full&r=json");

        }
        else {
            //get passed in values instead
            TextView displayText = (TextView)findViewById(R.id.titleDisplay);
            TextView yearView = (TextView)findViewById(R.id.yearView);
            TextView descView = (TextView)findViewById(R.id.movieDescText);
            ImageView displayPoster = (ImageView)findViewById(R.id.posterView);
            displayText.setText(titleValue);
            yearView.setText(String.valueOf(yearValue));
            descView.setText(descValue);
            displayPoster.setImageResource(R.drawable.notavailablejpg);

        }




        //Get button from UI
        Button schedulePartyButton = (Button)findViewById(R.id.partyButton);
        //Set onClickListener
        schedulePartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPartyDialog();
            }
        });

        //share button
        Button shareButton = (Button)findViewById(R.id.shareButton);
        //set onClickListener
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("plain/text");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "My favourite movie is "
                        + titleValue + ". " + descValue + ". The rating I gave it was " + ratingValue);
                startActivity(Intent.createChooser(shareIntent, "Share using: "));

            }
        });

        //ratingbar changed listener
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                ratingBar.setRating((int) rating);
                AppData.getInstance().getMovie(position).setRating((int) rating);
                Toast.makeText(getApplicationContext(), "The rating was changed to " +
                        AppData.getInstance().getMovie(position).getRating(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
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

    //method to create party dialog
    public void createPartyDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Schedule a party");

        //party date picker
        final TextView partyDateTitle = new TextView(this);
        partyDateTitle.setText("Enter party date:");

        final DatePicker date = new DatePicker(this);
        date.setCalendarViewShown(false);
        date.setSpinnersShown(true);

        //party date
        final TextView partyTimeTitle = new TextView(this);
        partyTimeTitle.setText("Enter party time");
        final EditText partyTime = new EditText(this);

        //party venue
        final TextView partyVenueTitle = new TextView(this);
        partyVenueTitle.setText("Enter party venue:");
        final EditText partyVenue = new EditText(this);

        //party location
        final TextView partyLocationTitle = new TextView(this);
        partyLocationTitle.setText("Enter party location (Latitude and longitude separated by a comma):");
        final EditText partyLocation = new EditText(this);

        //add contacts
        final TextView partyInviteesTitle = new TextView(this);
        partyInviteesTitle.setText("Party Invitees - Select invitees and they will be added to list below.");
        final TextView partyInviteesTitle2 = new TextView(this);
        partyInviteesTitle2.setText("To remove an invitee, tap their email address on the list below.");

        //get all email addresses on device
        ArrayList<String>emails = getEmailAddresses();
        //sort by alphabetical
        Collections.sort(emails);

        //new array for saving selected email addresses
        final ArrayList<String>selectedEmails = new ArrayList<String>();

        //create listview to display chosen email addresses under spinner
        final ListView emailList = new ListView(this);
        final ArrayAdapter<String> emailsArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, selectedEmails);


        //spinner for choosing email addresses
        final Spinner emailSpinner = new Spinner(this);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, emails);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailSpinner.setAdapter(spinnerArrayAdapter);

        //onItemSelectedListener
        emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //save string of selected item and add it to the array of emails for display
                String selectedEmail = emailSpinner.getSelectedItem().toString();
                selectedEmails.add(selectedEmail);
                //update list
                emailList.setAdapter(emailsArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //selected emails list onItemClicked Listener, will delete emails from the list
        emailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEmails.remove(position);
                //update list
                emailList.setAdapter(emailsArrayAdapter);
            }
        });


        partyTime.setInputType(InputType.TYPE_CLASS_DATETIME);
        partyVenue.setInputType(InputType.TYPE_CLASS_TEXT);
        partyLocation.setInputType(InputType.TYPE_CLASS_TEXT);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(partyDateTitle);
        layout.addView(date);
        layout.addView(partyTimeTitle);
        layout.addView(partyTime);
        layout.addView(partyVenueTitle);
        layout.addView(partyVenue);
        layout.addView(partyLocationTitle);
        layout.addView(partyLocation);
        layout.addView(partyInviteesTitle);
        layout.addView(partyInviteesTitle2);
        layout.addView(emailSpinner);
        layout.addView(emailList);
        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //save data here
                //create new party and add data to it
                //send party to Firebase from here
                String time = partyTime.getText().toString();
                GregorianCalendar partyDate = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                String venue = partyVenue.getText().toString();
                String location = partyLocation.getText().toString();
                Party party = new Party(partyDate, time, venue, location, selectedEmails);

                //input validation
                if(TextUtils.isEmpty(time)){
                    createErrorDialog("Error", "You must enter a time!");
                }
                else if (venue.isEmpty()){
                    createErrorDialog("Error", "You must enter a venue!");
                }
                else if (location.isEmpty()){
                    createErrorDialog("Error", "You must enter a location! Latitude and longitude separated by a comma.");
                }
                else {


                    //set party to corresponding movie
                    AppData.getInstance().getMovie(position).setParty(party);


                    //send latlngs of where party is to google map intent
                    String[] convertedLatLng = location.split(",");
                    double convertedLat = Double.parseDouble(convertedLatLng[0]);
                    double convertedLong = Double.parseDouble(convertedLatLng[1]);

                    //convert date to string
                    StringBuilder sb = new StringBuilder();
                    sb.append(date.getDayOfMonth());
                    sb.append("/");
                    sb.append(date.getMonth());
                    sb.append("/");
                    sb.append(date.getYear());
                    String dateOutput = sb.toString();

                    //publish to Firebase
                    Firebase reference = new Firebase("https://boiling-fire-450.firebaseIO.com/");
                    reference.child("movieTitle").setValue(titleValue);
                    reference.child("movieRating").setValue(ratingValue);
                    reference.child("partyDate").setValue(dateOutput);
                    reference.child("partyTime").setValue(time);
                    reference.child("partyVenue").setValue(venue);
                    reference.child("partyLocation").setValue(location);
                    reference.child("partyInvitees").setValue(selectedEmails);


                    //intent stuff
                    //put intent here to go to next activity
                    Intent mapIntent = new Intent(DetailViewActivity.this, PartyMap.class);
                    //extra to pass in movie variables
                    Bundle extras = new Bundle();
                    extras.putSerializable("invitees", selectedEmails);
                    mapIntent.putExtra("extras", extras);
                    mapIntent.putExtra("address", venue);
                    mapIntent.putExtra("latitude", convertedLat);
                    mapIntent.putExtra("longitude", convertedLong);
                    mapIntent.putExtra("date", dateOutput);
                    mapIntent.putExtra("time", time);
                    startActivity(mapIntent);

                }


            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //method to pull email addresses from device
    public ArrayList<String> getEmailAddresses() {

        //new storage for emails
        ArrayList<String> emails = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        //cursor traversal of list
        if (cur.getCount() > 0){

            while(cur.moveToNext()){
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while(cur1.moveToNext()){
                    //get contact emails
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                    //if email isnt null, add to emails list
                    if (email != null){
                        emails.add(email);
                    }
                }

                cur1.close();
            }

        }

        cur.close();
        return emails;
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

    //Http Async task to run Get operation in separate thread
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        protected void onPostExecute(String result){

            //get movie object from JSON data
            retrievedMovie = parseJSON(result);

            if (retrievedMovie != null){

                //set all movie info to Detail view
                //set values to display
                TextView displayText = (TextView)findViewById(R.id.titleDisplay);
                TextView yearView = (TextView)findViewById(R.id.yearView);
                TextView descView = (TextView)findViewById(R.id.movieDescText);
                displayPoster = (ImageView)findViewById(R.id.posterView);
                displayText.setText(retrievedMovie.getTitle());
                yearView.setText(String.valueOf(retrievedMovie.getYear()));
                descView.setText(retrievedMovie.getFull_plot());

                //get poster from URL
                new LoadImage().execute(imageUrl);

            }
            else {
                createErrorDialog("Error", "Movie not found!");
            }


        }
    }

    //Load image class
    private class LoadImage extends AsyncTask<String, String, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... args) {

            Bitmap bitmap;

            try{
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }

            return bitmap;

        }

        protected void onPostExecute(Bitmap image){

            if (image != null){
                posterFromURL = image;
                displayPoster.setImageBitmap(image);
            }
            else {
                displayPoster.setImageResource(R.drawable.notavailablejpg);
            }
        }
    }


    //Parse JSON into a movie object
    public Movie parseJSON(String input){

        try {
            //Create JSON object from input string
            JSONObject json = new JSONObject(input);


            if(json != null){

                    String title = json.optString("Title").toString();
                    int year = json.optInt("Year");
                    String plot = json.optString("Plot").toString();
                    String id = json.optString("imdbID").toString();
                    imageUrl = json.optString("Poster").toString();

                    //add to new movie object
                    Movie retrievedMovie = new Movie(title, year, null, plot, 0, id, 0, null);

                return retrievedMovie;

            }

        }
        catch(JSONException e){

            createErrorDialog("Error", e.getMessage().toString());
            return null;
        }


        return null;
    }

    //create error dialog box
    public void createErrorDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.show();


    }

    //check if connected to internet
    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else {
            return false;
        }
    }

}
