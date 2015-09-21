package jamessnee.com.madassignment2.model;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import jamessnee.com.madassignment2.R;

/**
 * Created by jamessnee on 21/08/15.
 * Singleton class to populate data
 *
 */
public class AppData {

    private static AppData instance;
    private List<Movie> movies = new ArrayList<Movie>();


    private AppData(){

        this.instance = instance;
    }

    public static AppData getInstance(){

        if (instance == null){
            instance = new AppData();
        }

        return instance;
    }

    //getter and setter for movie list
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    //get specific movie
    public Movie getMovie(int position){
        return movies.get(position);
    }


    //set specific movie
    public void setMovie(String movieId, Movie mov) {

        for (Movie movie : movies){
            if (movie.getId() == movieId){
                int position = movies.indexOf(movie);
                movies.set(position, mov);
            }
        }

    }


}
