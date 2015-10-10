package jamessnee.com.madassignment2.model;

/**
 * Created by jamessnee on 4/08/15. Stores info about Movies
 */
public class Movie {

    private String title;
    private int year;
    private String short_plot;
    private String full_plot;
    private int poster;
    private String id;
    private int rating;
    private Party party;


    public Movie(String title, int year, String short_plot, String full_plot, int poster, String id, int rating, Party party) {

        super();

        this.title = title;
        this.year = year;
        this.short_plot = short_plot;
        this.full_plot = full_plot;
        this.poster = poster;
        this.id = id;
        this.rating = rating;
        this.party = party;

    }

    public String getTitle(){
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getShort_plot() {
        return short_plot;
    }

    public String getFull_plot() {
        return full_plot;
    }

    public int getPoster(){
        return poster;
    }

    public String getId() {
        return id;
    }

    public int getRating() { return rating; }

    public Party getParty() { return party; }

    //setter for rating
    public void setRating(int rating) {
        this.rating = rating;
    }

    //setter for party
    public void setParty(Party party) { this.party = party; }
}
