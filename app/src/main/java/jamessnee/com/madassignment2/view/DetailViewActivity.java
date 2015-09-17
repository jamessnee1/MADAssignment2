package jamessnee.com.madassignment2.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

import jamessnee.com.madassignment2.R;
import jamessnee.com.madassignment2.model.AppData;
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


        //set values to display
        TextView displayText = (TextView)findViewById(R.id.titleDisplay);
        TextView yearView = (TextView)findViewById(R.id.yearView);
        TextView descView = (TextView)findViewById(R.id.movieDescText);
        ImageView displayPoster = (ImageView)findViewById(R.id.posterView);
        RatingBar rating = (RatingBar)findViewById(R.id.detailRatingBar);
        rating.setStepSize(1);
        displayText.setText(titleValue);
        yearView.setText(String.valueOf(yearValue));
        descView.setText(descValue);
        displayPoster.setImageResource(posterValue);
        rating.setRating((int) ratingValue);

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
        partyLocationTitle.setText("Enter party location:");
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
                String time = partyTime.getText().toString();
                GregorianCalendar partyDate = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
                String venue = partyVenue.getText().toString();
                String location = partyLocation.getText().toString();
                Party party = new Party(partyDate, time, venue, location, selectedEmails);

                //set party to corresponding movie
                AppData.getInstance().getMovie(position).setParty(party);

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

}
