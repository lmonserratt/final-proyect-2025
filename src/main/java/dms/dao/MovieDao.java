package dms.dao;

import dms.model.Movie;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) interface for the {@link Movie} entity.
 * <p>
 * This interface defines the CRUD contract used by all DAO implementations
 * (e.g., {@code MysqlMovieDao}, {@code SqliteMovieDao}) for persistence operations.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Provide abstract methods for Create, Read, Update, and Delete operations.</li>
 *   <li>Enforce a consistent API for working with {@link Movie} objects, independent of the database type.</li>
 *   <li>Ensure compatibility with the schema where {@code movie_id} is a {@code VARCHAR(10)} primary key.</li>
 * </ul>
 *
 * <p><b>Author:</b> Luis Augusto Monserratt Alvarado</p>
 * <p><b>Version:</b> 1.0</p>
 */
public interface MovieDao {

    // ---------- READ OPERATIONS ----------

    /**
     * Retrieves all movies from the database.
     *
     * @return a list of all {@link Movie} records in the table
     * @throws SQLException if a database access error occurs
     */
    List<Movie> findAll() throws SQLException;

    /**
     * Finds a single movie by its string primary key.
     *
     * @param id the unique movie identifier (e.g., {@code "INT2010"}); cannot be null or blank
     * @return an {@link Optional} containing the matching movie, or empty if not found
     * @throws SQLException if a database access error occurs
     */
    Optional<Movie> findById(String id) throws SQLException;

    // ---------- CREATE ----------

    /**
     * Inserts a new {@link Movie} record into the database.
     *
     * @param movie the movie to insert (all required fields must be provided)
     * @return the inserted movie instance
     * @throws SQLException if a database access error occurs or constraints are violated
     */
    Movie insert(Movie movie) throws SQLException;

    // ---------- UPDATE ----------

    /**
     * Updates an existing {@link Movie} record based on its primary key.
     *
     * @param movie the movie to update; must contain a valid primary key
     * @return {@code true} if at least one row was updated; {@code false} if no match found
     * @throws SQLException if a database access error occurs
     */
    boolean update(Movie movie) throws SQLException;

    // ---------- DELETE ----------

    /**
     * Deletes a movie record by its string primary key.
     *
     * @param id the movie ID to delete (e.g., {@code "INT2010"})
     * @return {@code true} if at least one row was deleted; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean delete(String id) throws SQLException;
}
