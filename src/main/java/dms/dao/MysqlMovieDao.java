package dms.dao;

import dms.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of {@link MovieDao} using plain JDBC.
 * <p>
 * This DAO provides CRUD (Create, Read, Update, Delete) and search operations
 * for {@link Movie} objects stored in a MySQL database.
 * It expects a table named {@code movies} in a schema such as {@code dms_movies},
 * with the following columns:
 * </p>
 * <ul>
 *   <li><b>movie_id</b> VARCHAR(10) PRIMARY KEY</li>
 *   <li><b>title</b> VARCHAR(...)</li>
 *   <li><b>director</b> VARCHAR(...)</li>
 *   <li><b>release_year</b> INT</li>
 *   <li><b>duration_minutes</b> INT</li>
 *   <li><b>genre</b> VARCHAR(...)</li>
 *   <li><b>rating</b> DOUBLE</li>
 * </ul>
 *
 * <p>All connections must be opened using {@link #connect(String, String, String)}
 * and closed with {@link #close()} to avoid memory leaks.</p>
 *
 * @author Luis
 * @version 1.0
 */
public class MysqlMovieDao implements MovieDao {

    /** The active JDBC connection to the MySQL database. */
    private Connection conn;

    // ---------- CONNECTION ----------

    /**
     * Opens a JDBC connection to a MySQL database.
     * <p>Example JDBC URL:
     * {@code jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8}</p>
     *
     * @param jdbcUrl JDBC connection URL (cannot be blank)
     * @param user    database username
     * @param pass    database password
     * @throws SQLException              if the MySQL driver is missing or connection fails
     * @throws IllegalArgumentException  if {@code jdbcUrl} is null or blank
     */
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        if (jdbcUrl == null || jdbcUrl.isBlank())
            throw new IllegalArgumentException("JDBC URL cannot be empty");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        }

        this.conn = DriverManager.getConnection(jdbcUrl, user, pass);
    }

    /**
     * Checks whether there is an open JDBC connection.
     *
     * @return {@code true} if a connection is established; {@code false} otherwise
     */
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes the JDBC connection if it is currently open.
     *
     * @throws SQLException if closing the connection fails
     */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    // ---------- HELPERS ----------

    /**
     * Maps a single {@link ResultSet} row into a {@link Movie} object.
     *
     * @param rs result set positioned on a valid row
     * @return a new {@link Movie} object populated from the current row
     * @throws SQLException if column reading fails
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

    // ---------- CRUD ----------

    /**
     * Retrieves all movies in the database, ordered alphabetically by title.
     *
     * @return a non-null list of all movies (empty if none found)
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Movie> findAll() throws SQLException {
        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies ORDER BY title ASC";
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
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
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Movie ID cannot be empty");

        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
     * @return the inserted movie
     * @throws SQLException             if insertion fails (duplicate key, constraint error, etc.)
     * @throws IllegalArgumentException if {@code movie} is null
     */
    @Override
    public Movie insert(Movie movie) throws SQLException {
        if (movie == null)
            throw new IllegalArgumentException("Movie cannot be null");

        String sql = "INSERT INTO movies (movie_id,title,director,release_year,duration_minutes,genre,rating) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
     * @param movie the updated movie information
     * @return {@code true} if at least one record was updated; {@code false} otherwise
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code movie} is null
     */
    @Override
    public boolean update(Movie movie) throws SQLException {
        if (movie == null)
            throw new IllegalArgumentException("Movie cannot be null");

        String sql = "UPDATE movies " +
                "SET title=?, director=?, release_year=?, duration_minutes=?, genre=?, rating=? " +
                "WHERE movie_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getDirector());
            ps.setInt(3, movie.getReleaseYear());
            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getGenre());
            ps.setDouble(6, movie.getRating());
            ps.setString(7, movie.getMovieId());
            int rows = ps.executeUpdate();
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
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Movie ID cannot be empty");

        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // ---------- CUSTOM QUERY ----------

    /**
     * Searches for movies whose title contains a specific text fragment.
     * The search is case-insensitive.
     *
     * @param titleFragment partial title text to search (must not be blank)
     * @return a non-null list of movies that match the search term
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if {@code titleFragment} is null or blank
     */
    public List<Movie> searchByTitle(String titleFragment) throws SQLException {
        if (titleFragment == null || titleFragment.isBlank())
            throw new IllegalArgumentException("Title fragment cannot be empty");

        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE LOWER(title) LIKE LOWER(?) ORDER BY title ASC";
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titleFragment.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
