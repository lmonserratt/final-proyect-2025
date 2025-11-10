package dms.dao;

import dms.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of {@link MovieDao} using plain JDBC.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Open/close the DB connection</li>
 *   <li>CRUD operations (findAll, findById, insert, update, delete)</li>
 *   <li>Case-insensitive search by title using SQL LIKE</li>
 * </ul>
 *
 * <p><b>Expected schema</b> (table {@code movies}):</p>
 * <pre>
 *  movie_id         VARCHAR(10) PRIMARY KEY,
 *  title            VARCHAR(100) NOT NULL,
 *  director         VARCHAR(100),
 *  release_year     INT,
 *  duration_minutes INT,
 *  genre            VARCHAR(50),
 *  rating           DOUBLE
 * </pre>
 *
 * <p><b>Example JDBC URL</b>:</p>
 * <pre>
 * jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8
 * </pre>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * MysqlMovieDao dao = new MysqlMovieDao();
 * dao.connect(url, user, pass);
 * List<Movie> movies = dao.findAll();
 * dao.close();
 * }</pre>
 *
 * @author Luis
 * @since 1.0.0
 */
public class MysqlMovieDao implements MovieDao {

    /** Active JDBC connection (null when closed). */
    private Connection conn;

    /** Default constructor (no special initialization). */
    public MysqlMovieDao() { }

    // ---------- CONNECTION ----------

    /**
     * Opens a JDBC connection to MySQL.
     *
     * @param jdbcUrl JDBC URL (e.g., {@code jdbc:mysql://localhost:3306/dms_movies?...})
     * @param user    database username
     * @param pass    database password
     * @throws IllegalArgumentException if {@code jdbcUrl} is blank
     * @throws SQLException             if the driver is missing or the connection cannot be established
     */
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("JDBC URL cannot be empty");
        }
        try {
            // Ensure MySQL driver is available on the classpath
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        }
        this.conn = DriverManager.getConnection(jdbcUrl, user, pass);
    }

    /**
     * Checks whether the current JDBC connection is open.
     *
     * @return {@code true} if the connection is valid and open; {@code false} otherwise
     */
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes the JDBC connection if it is open.
     *
     * @throws SQLException if closing the connection fails
     */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    // ---------- HELPERS ----------

    /**
     * Maps the current {@link ResultSet} row into a {@link Movie} object.
     *
     * @param rs active {@link ResultSet} positioned at a valid row
     * @return populated {@link Movie} object
     * @throws SQLException if a column cannot be accessed
     */
    private static Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("director"),
                rs.getInt("release_year"),
                rs.getInt("duration_minutes"),
                rs.getString("genre"),
                rs.getDouble("rating")
        );
    }

    /**
     * Ensures that the database connection is valid before executing queries.
     *
     * @return the active {@link Connection}
     * @throws SQLException if the connection is null or closed
     */
    private Connection requireConn() throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("No open JDBC connection. Call connect(...) first.");
        }
        return conn;
    }

    // ---------- CRUD ----------

    /**
     * Retrieves all movies in the database, ordered alphabetically by title.
     *
     * @return a non-null list of all movies (empty if none found)
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Movie> findAll() throws SQLException {
        final String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies ORDER BY title ASC";
        final List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = requireConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Finds a movie by its unique ID.
     *
     * @param id movie ID (e.g., "INT2010")
     * @return an {@link Optional} containing the movie if found, or empty if not found
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code id} is null or blank
     */
    @Override
    public Optional<Movie> findById(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Movie ID cannot be empty");
        }
        final String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = requireConn().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    /**
     * Inserts a new movie record into the database.
     *
     * @param movie the movie to insert (must not be null)
     * @return the inserted {@link Movie}
     * @throws SQLException             if insertion fails (duplicate key, constraint error, etc.)
     * @throws IllegalArgumentException if {@code movie} is null
     */
    @Override
    public Movie insert(Movie movie) throws SQLException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }
        final String sql = "INSERT INTO movies (movie_id,title,director,release_year,duration_minutes,genre,rating) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = requireConn().prepareStatement(sql)) {
            ps.setString(1, movie.getMovieId());
            ps.setString(2, movie.getTitle());
            ps.setString(3, movie.getDirector());
            ps.setInt(4, movie.getReleaseYear());
            ps.setInt(5, movie.getDurationMinutes());
            ps.setString(6, movie.getGenre());
            ps.setDouble(7, movie.getRating());
            ps.executeUpdate();
            return movie;
        }
    }

    /**
     * Updates an existing movie record by its {@code movie_id}.
     *
     * @param movie the updated movie information (must not be null)
     * @return {@code true} if at least one record was updated; {@code false} otherwise
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code movie} is null
     */
    @Override
    public boolean update(Movie movie) throws SQLException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }
        final String sql = "UPDATE movies " +
                "SET title=?, director=?, release_year=?, duration_minutes=?, genre=?, rating=? " +
                "WHERE movie_id=?";
        try (PreparedStatement ps = requireConn().prepareStatement(sql)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getDirector());
            ps.setInt(3, movie.getReleaseYear());
            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getGenre());
            ps.setDouble(6, movie.getRating());
            ps.setString(7, movie.getMovieId());
            final int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Deletes a movie by its {@code movie_id}.
     *
     * @param id movie ID to delete
     * @return {@code true} if a record was deleted; {@code false} otherwise
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code id} is null or blank
     */
    @Override
    public boolean delete(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Movie ID cannot be empty");
        }
        final String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = requireConn().prepareStatement(sql)) {
            ps.setString(1, id);
            final int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // ---------- CUSTOM QUERY ----------

    /**
     * Searches for movies whose title contains a specific text fragment (case-insensitive).
     *
     * @param titleFragment partial title text to search (must not be blank)
     * @return a non-null list of movies that match the search term
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code titleFragment} is null or blank
     */
    public List<Movie> searchByTitle(String titleFragment) throws SQLException {
        if (titleFragment == null || titleFragment.isBlank()) {
            throw new IllegalArgumentException("Title fragment cannot be empty");
        }
        final String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE LOWER(title) LIKE LOWER(?) ORDER BY title ASC";
        final List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = requireConn().prepareStatement(sql)) {
            ps.setString(1, "%" + titleFragment.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
