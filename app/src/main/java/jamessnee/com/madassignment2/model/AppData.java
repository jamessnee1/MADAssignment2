package jamessnee.com.madassignment2.model;

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

        populateMovieList();
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

    //AppData methods
    private void populateMovieList() {

        //method to populate movies list. Rating initially will be 0. As party is optional, it will be null
        //to start with.

        movies.add(new Movie("Star Wars Episode V: The Empire Strikes Back", 1980,"The second installment in the" +
                " Star Wars series.",
                "After the Rebel base on the icy planet Hoth is taken over by the empire, Han, Leia, " +
                        "Chewbacca, and C-3PO flee across the galaxy from the Empire. Luke travels to the forgotten " +
                        "planet of Dagobah to receive training from the Jedi master Yoda, while Vader endlessly " +
                        "pursues him.",R.drawable.esb,"tt0080684", 0, null));
        movies.add(new Movie("The Terminator", 1984,"The first installment in the" +
                " Terminator series.",
                "A cyborg is sent from the future on a deadly mission. He has to kill Sarah Connor, a young woman " +
                        "whose life will have a great significance in years to come. Sarah has only one protector - " +
                        "Kyle Reese - also sent from the future. The Terminator uses his exceptional intelligence and " +
                        "strength to find Sarah, but is there any way to stop the seemingly indestructible " +
                        "cyborg?", R.drawable.terminator,"tt0088247", 0, null));
        movies.add(new Movie("Frozen", 2013,"Disney/Pixar animated film.",
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
                        "can help her.",R.drawable.frozen,"tt2294629", 0, null));
        movies.add(new Movie("The Lion King", 1994,"Disney 2D Animated film.",
                "A young lion Prince is cast out of his pride by his cruel uncle, who claims he killed his " +
                        "father. While the uncle rules with an iron fist, the prince grows up beyond the savannah, " +
                        "living by a philosophy: No worries for the rest of your days. But when his past " +
                        "comes to haunt him, the young Prince must decide his fate: will he remain an outcast, " +
                        "or face his demons and become what he needs to be?",R.drawable.lionking,"tt0110357", 0, null));
        movies.add(new Movie("The Shawshank Redemption", 1994,"Frank Darabont's prison film.",
                "Andy Dufresne is a young and successful banker whose life changes drastically when he is " +
                        "convicted and sentenced to life imprisonment for the murder of his wife and her " +
                        "lover. Set in the 1940s, the film shows how Andy, with the help of his friend Red, " +
                        "the prison entrepreneur, turns out to be a most unconventional " +
                        "prisoner.",R.drawable.shawshank,"tt0111161", 0, null));
        movies.add(new Movie("The Dark Knight", 2008,"Heath Ledger's last film appearance.",
                "Batman raises the stakes in his war on crime. With the help of Lieutenant Jim Gordon and " +
                        "District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal " +
                        "organizations that plague the city streets. The partnership proves to be effective, " +
                        "but they soon find themselves prey to a reign of chaos unleashed by a rising " +
                        "criminal mastermind known to the terrified citizens of Gotham " +
                        "as The Joker.",R.drawable.darkknight,"tt0468569", 0, null));
        movies.add(new Movie("Pulp Fiction", 1994,"Quentin Tarantino's breakout hit.",
                "Jules Winnfield and Vincent Vega are two hitmen who are out to retrieve a suitcase " +
                        "stolen from their employer, mob boss Marsellus Wallace. Wallace has also asked " +
                        "Vincent to take his wife Mia out a few days later when Wallace himself will be " +
                        "out of town. Butch Coolidge is an aging boxer who is paid by Wallace to lose " +
                        "his next fight. The lives of these seemingly unrelated people are woven " +
                        "together comprising of a series of funny, bizarre and " +
                        "uncalled-for incidents.",R.drawable.pulpfiction,"tt0110912", 0, null));
        movies.add(new Movie("Bad Behaviour", 2010,"The programmer worked on this movie.",
                "Emma and Peterson encounter their fierce predator Voyte Parker, a cop " +
                        "confronts his son's murderer, and a man finds his wife is cheating on him. " +
                        "Intersecting story lines; murderers, coppers, teachers " +
                        "and teenagers.",R.drawable.badbehaviour,"tt1621418", 0, null));


    }

}
