package dms.model;

import java.util.Objects;

/**
 * Represents a movie entity in the Movie Manager DMS.
 * <p>
 * This class acts as a plain data model (POJO) and mirrors the structure of
 * the {@code movies} table in the MySQL database. Each field maps directly
 * to a column in the database.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Encapsulate movie details such as ID, title, director, release year, duration, genre, and rating.</li>
 *   <li>Validate data through constructor and setter methods to ensure integrity.</li>
 *   <li>Provide basic overrides for {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.</li>
 * </ul>
 *
 * <p><b>Author:</b> Luis Augusto Monserratt Alvarado</p>
 * <p><b>Version:</b> 1.0</p>
 */
public class Movie {

    /** Unique identifier for the movie (e.g., "INT2010"). */
    private String movieId;

    /** Movie title. */
    private String title;

    /** Name of the director. */
    private String director;

    /** Year the movie was released (between 1888 and 2100). */
    private int releaseYear;

    /** Duration of the movie in minutes (1–999). */
    private int durationMinutes;

    /** Movie genre (e.g., Action, Drama, Sci-Fi). */
    private String genre;

    /** Viewer rating between 0.0 and 10.0. */
    private double rating;

    // ---------- CONSTRUCTOR ----------

    /**
     * Constructs a new {@code Movie} instance with the provided field values.
     *
     * @param movieId         unique identifier (cannot be null or blank)
     * @param title           movie title (optional; trimmed if non-null)
     * @param director        director's name (optional; trimmed if non-null)
     * @param releaseYear     year the movie was released (1888–2100)
     * @param durationMinutes duration of the movie in minutes (1–999)
     * @param genre           genre of the movie (trimmed if non-null)
     * @param rating          rating score (0.0–10.0)
     * @throws IllegalArgumentException if {@code movieId} is null or blank
     */
    public Movie(String movieId, String title, String director, int releaseYear,
                 int durationMinutes, String genre, double rating) {
        if (movieId == null || movieId.isBlank())
            throw new IllegalArgumentException("movieId cannot be null or blank");
        this.movieId = movieId.trim();
        this.title = safeTrim(title);
        this.director = safeTrim(director);
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.genre = safeTrim(genre);
        this.rating = rating;
    }

    // ---------- HELPERS ----------

    /**
     * Trims a string safely, returning an empty string if {@code null}.
     *
     * @param s input string
     * @return trimmed string or empty string if {@code null}
     */
    private static String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }

    // ---------- GETTERS AND SETTERS ----------

    /**
     * Returns the unique identifier of this movie.
     *
     * @return movie ID
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * Sets the movie ID.
     *
     * @param movieId new movie ID (cannot be null or blank)
     * @throws IllegalArgumentException if {@code movieId} is null or blank
     */
    public void setMovieId(String movieId) {
        if (movieId == null || movieId.isBlank())
            throw new IllegalArgumentException("movieId cannot be null or blank");
        this.movieId = movieId.trim();
    }

    /**
     * Returns the movie title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the movie title.
     *
     * @param title title text (trimmed if non-null)
     */
    public void setTitle(String title) {
        this.title = safeTrim(title);
    }

    /**
     * Returns the movie director's name.
     *
     * @return director name
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets the movie director's name.
     *
     * @param director name of the director (trimmed if non-null)
     */
    public void setDirector(String director) {
        this.director = safeTrim(director);
    }

    /**
     * Returns the release year of the movie.
     *
     * @return release year (1888–2100)
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year of the movie.
     *
     * @param releaseYear year between 1888 and 2100
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Returns the duration of the movie in minutes.
     *
     * @return duration in minutes
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Sets the duration of the movie in minutes.
     *
     * @param durationMinutes duration (1–999 minutes)
     */
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /**
     * Returns the genre of the movie.
     *
     * @return genre text
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the movie.
     *
     * @param genre genre text (trimmed if non-null)
     */
    public void setGenre(String genre) {
        this.genre = safeTrim(genre);
    }

    /**
     * Returns the rating of the movie (0.0–10.0).
     *
     * @return movie rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the rating of the movie (0.0–10.0).
     *
     * @param rating movie rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    // ---------- OVERRIDDEN METHODS ----------

    /**
     * Checks equality between two {@code Movie} objects based on their {@code movieId}.
     *
     * @param o the object to compare
     * @return {@code true} if IDs match, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie other = (Movie) o;
        return Objects.equals(movieId, other.movieId);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code for this movie
     */
    @Override
    public int hashCode() {
        return Objects.hash(movieId);
    }

    /**
     * Returns a readable string representation of the movie.
     *
     * @return formatted movie details
     */
    @Override
    public String toString() {
        return "Movie{" +
                "movieId='" + movieId + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", releaseYear=" + releaseYear +
                ", durationMinutes=" + durationMinutes +
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                '}';
    }
}
