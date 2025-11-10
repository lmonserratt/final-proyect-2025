package dms.service;

import dms.dao.MovieDao;
import dms.dao.MysqlMovieDao;
import dms.model.Movie;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer that coordinates validation and orchestration between the GUI and the DAO layer.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Keep business rules and input validation outside the GUI.</li>
 *   <li>Delegate persistence to a {@link MovieDao} implementation (MySQL in this project).</li>
 *   <li>Provide convenience methods used by controllers and dialogs.</li>
 * </ul>
 *
 * <p><b>Author:</b> Luis Augusto Monserratt Alvarado</p>
 * <p><b>Version:</b> 1.0</p>
 */
public class MovieService {

    private final MovieDao dao;

    /**
     * Constructs the service with a specific DAO implementation.
     *
     * @param dao non-null DAO implementation (e.g., {@link MysqlMovieDao})
     * @throws IllegalArgumentException if {@code dao} is {@code null}
     */
    public MovieService(MovieDao dao) {
        if (dao == null) throw new IllegalArgumentException("dao cannot be null");
        this.dao = dao;
    }

    // ---------- CONNECTION ----------

    /**
     * Opens a database connection when the underlying DAO supports it (i.e., {@link MysqlMovieDao}).
     *
     * @param jdbcUrl JDBC URL (e.g., {@code jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC})
     * @param user    database username
     * @param pass    database password
     * @throws SQLException if the connection attempt fails
     * @throws IllegalStateException if the configured DAO does not support connections
     */
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        if (dao instanceof MysqlMovieDao) {
            ((MysqlMovieDao) dao).connect(jdbcUrl, user, pass);
        } else {
            throw new IllegalStateException("Unsupported DAO type for connection");
        }
    }

    /**
     * Indicates whether the underlying DAO is currently connected (for {@link MysqlMovieDao} only).
     *
     * @return {@code true} if connected; {@code false} otherwise
     */
    public boolean isConnected() {
        if (dao instanceof MysqlMovieDao) {
            return ((MysqlMovieDao) dao).isConnected();
        }
        return false;
    }

    /**
     * Closes the database connection if supported by the underlying DAO.
     *
     * @throws SQLException if closing the connection fails
     */
    public void close() throws SQLException {
        if (dao instanceof MysqlMovieDao) {
            ((MysqlMovieDao) dao).close();
        }
    }

    // ---------- CRUD ----------

    /**
     * Creates a new movie after validating its fields.
     *
     * @param movie movie to create (must be non-null and valid)
     * @throws SQLException             if the insert fails at the DAO layer
     * @throws IllegalArgumentException if validation fails
     */
    public void create(Movie movie) throws SQLException {
        validateMovie(movie);
        dao.insert(movie);
    }

    /**
     * Retrieves all movies from the database.
     *
     * @return a non-null list of movies (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Movie> readAll() throws SQLException {
        return dao.findAll();
    }

    /**
     * Retrieves a movie by its string ID (e.g., {@code "INT2010"}).
     *
     * @param id movie primary key (VARCHAR(10)); cannot be null/blank
     * @return an {@link Optional} containing the movie if found, otherwise empty
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code id} is null or blank
     */
    public Optional<Movie> readById(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        return dao.findById(id.trim());
    }

    /**
     * Updates an existing movie (matched by the entity's primary key).
     *
     * @param movie updated movie data (must be valid and contain a primary key)
     * @throws SQLException             if the update fails or no row is affected
     * @throws IllegalArgumentException if validation fails
     */
    public void update(Movie movie) throws SQLException {
        validateMovie(movie);
        boolean ok = dao.update(movie);
        if (!ok) {
            throw new SQLException("Movie ID not found: " + movie.getMovieId());
        }
    }

    /**
     * Deletes a movie by its string ID (matches VARCHAR(10) PK).
     *
     * @param id movie ID to delete (cannot be null/blank)
     * @return {@code true} if a row was deleted; {@code false} otherwise
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code id} is null or blank
     */
    public boolean deleteById(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        return dao.delete(id.trim());
    }

    /**
     * Performs a case-insensitive title search via a custom DAO method.
     *
     * @param titleFragment partial title text to search
     * @return a non-null list of matches (possibly empty)
     * @throws SQLException         if a database access error occurs
     * @throws IllegalStateException if the configured DAO does not support this operation
     */
    public List<Movie> searchByTitle(String titleFragment) throws SQLException {
        if (!(dao instanceof MysqlMovieDao)) {
            throw new IllegalStateException("searchByTitle only implemented in MysqlMovieDao");
        }
        return ((MysqlMovieDao) dao).searchByTitle(titleFragment);
    }

    // ---------- CUSTOM ACTION ----------

    /**
     * Computes the average duration (in minutes) of all movies.
     *
     * @return average duration as a {@code double}; returns {@code 0.0} if there are no movies
     * @throws SQLException if retrieving movies fails
     */
    public double averageDuration() throws SQLException {
        List<Movie> movies = dao.findAll();
        if (movies.isEmpty()) return 0.0;
        double total = 0;
        for (Movie m : movies) total += m.getDurationMinutes();
        return total / movies.size();
    }

    // ---------- VALIDATION ----------

    /**
     * Validates required fields and value ranges according to database constraints.
     * <p>
     * Checks:
     * <ul>
     *   <li>{@code movieId}: non-empty</li>
     *   <li>{@code title}: non-empty</li>
     *   <li>{@code director}: non-empty</li>
     *   <li>{@code genre}: non-empty</li>
     *   <li>{@code releaseYear}: 1888..2100</li>
     *   <li>{@code durationMinutes}: 1..999</li>
     *   <li>{@code rating}: 0.0..10.0</li>
     * </ul>
     *
     * @param m movie to validate
     * @throws IllegalArgumentException if any constraint is violated
     */
    private void validateMovie(Movie m) {
        if (m == null) throw new IllegalArgumentException("Movie cannot be null");
        if (m.getMovieId() == null || m.getMovieId().trim().isEmpty())
            throw new IllegalArgumentException("Movie ID cannot be empty");
        if (m.getTitle() == null || m.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty");
        if (m.getDirector() == null || m.getDirector().trim().isEmpty())
            throw new IllegalArgumentException("Director cannot be empty");
        if (m.getGenre() == null || m.getGenre().trim().isEmpty())
            throw new IllegalArgumentException("Genre cannot be empty");
        if (m.getReleaseYear() < 1888 || m.getReleaseYear() > 2100)
            throw new IllegalArgumentException("Release year must be between 1888 and 2100");
        if (m.getDurationMinutes() <= 0 || m.getDurationMinutes() > 999)
            throw new IllegalArgumentException("Duration must be 1..999");
        if (m.getRating() < 0.0 || m.getRating() > 10.0)
            throw new IllegalArgumentException("Rating must be 0.0..10.0");
    }
}
