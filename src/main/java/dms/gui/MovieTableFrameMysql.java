package dms.gui;

import dms.dao.MovieDao;
import dms.dao.MysqlMovieDao;
import dms.model.Movie;
import dms.service.MovieService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Main Swing window for the MySQL-backed Movie Manager DMS.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Auto-connects to MySQL on startup using default/system properties</li>
 *   <li>Provides full CRUD actions (Create, Read, Update, Delete)</li>
 *   <li>Search by title and custom action to compute average duration</li>
 *   <li>Manual connection dialog fallback if auto-connection fails</li>
 *   <li>Optional one-row seed if the table is empty (first run)</li>
 * </ul>
 *
 * <p><b>Connection behavior (final version):</b></p>
 * <ul>
 *   <li>The database location is fixed to {@code jdbc:mysql://localhost:3306/dms_movies}.</li>
 *   <li>The user never types the database URL, host, port, or schema name.</li>
 *   <li>The user only supplies username and password when needed.</li>
 * </ul>
 *
 * <p><b>System properties / environment variables</b> (optional overrides):</p>
 * <ul>
 *   <li><b>JDBC_URL</b> – e.g., {@code jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf8}</li>
 *   <li><b>DB_USER</b> – DB username</li>
 *   <li><b>DB_PASS</b> – DB password</li>
 * </ul>
 *
 * @author
 *     Luis Augusto Monserratt Alvarado
 * @version 1.1
 */
public class MovieTableFrameMysql extends JFrame {

    // ---- Auto-connect defaults (can be overridden by -D props or env vars) ----
    /** Default JDBC URL (overridden by system property or env var JDBC_URL). */
    private static final String DEFAULT_JDBC_URL =
            getPropOrEnv("JDBC_URL",
                    "jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8");

    /** Default DB user (overridden by -DDB_USER or env DB_USER). */
    private static final String DEFAULT_DB_USER = getPropOrEnv("DB_USER", "root");

    /** Default DB password (overridden by -DDB_PASS or env DB_PASS). */
    private static final String DEFAULT_DB_PASS = getPropOrEnv("DB_PASS", "");

    /** Table model backing the JTable. */
    private final DefaultTableModel tableModel;

    /** Main grid displaying movie rows. */
    private final JTable table;

    /** Search text field for title filtering. */
    private final JTextField txtSearch;

    /** DAO implementation used by the service layer. */
    private final MovieDao dao;

    /** Service layer handling validation, orchestration, and DB calls. */
    private final MovieService service;

    /**
     * Builds the main window, initializes UI widgets, and attempts an automatic MySQL connection.
     * <p>If auto-connection fails, a simple connection dialog is offered where the user only
     * provides username and password (the DB location is fixed).</p>
     */
    public MovieTableFrameMysql() {
        super("DMS – Movies (MySQL)");
        this.dao = new MysqlMovieDao();
        this.service = new MovieService(dao);

        // ---- Table model and UI wiring ----
        this.tableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Director", "Year", "Duration", "Genre", "Rating"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        this.txtSearch = new JTextField(20);

        setContentPane(buildContent());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);

        // ---- Auto-connect on startup, then load table data ----
        SwingUtilities.invokeLater(() -> {
            if (autoConnectAndLoad()) {
                // Connected and data loaded successfully.
            } else {
                // Fallback: show manual connection dialog if auto-connect fails.
                if (showConnectionDialogAndConnect()) {
                    // Ensure seed and then refresh after manual connection
                    ensureSeedIfEmpty();
                    safeRefreshAll();
                }
            }
        });
    }

    /**
     * Builds toolbar, search panel, and central table area; wires UI actions.
     *
     * @return the root panel for this frame
     */
    private JPanel buildContent() {
        // Toolbar
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        JButton btnConnect = new JButton("Connect");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnAverage = new JButton("Average Duration");
        JButton btnExit = new JButton("Exit");

        tb.add(btnConnect);
        tb.add(btnRefresh);
        tb.addSeparator();
        tb.add(btnAdd);
        tb.add(btnEdit);
        tb.add(btnDelete);
        tb.addSeparator();
        tb.add(btnAverage);
        tb.add(Box.createHorizontalGlue());
        tb.add(btnExit);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        searchPanel.add(new JLabel("Search title:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Center area with table
        JScrollPane scroll = new JScrollPane(table);
        JPanel center = new JPanel(new BorderLayout());
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        // Root container
        JPanel root = new JPanel(new BorderLayout());
        root.add(tb, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        // Actions
        btnConnect.addActionListener(e -> {
            if (showConnectionDialogAndConnect()) {
                ensureSeedIfEmpty();
                safeRefreshAll();
            }
        });
        btnRefresh.addActionListener(e -> safeRefreshAll());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnAverage.addActionListener(e -> onAverage());
        btnExit.addActionListener(e -> onExit());
        btnSearch.addActionListener(e -> onSearch());
        btnClear.addActionListener(e -> { txtSearch.setText(""); safeRefreshAll(); });
        txtSearch.addActionListener(e -> onSearch());
        return root;
    }

    /**
     * Attempts an automatic connection using default URL/credentials.
     * If successful, seeds the DB if empty and loads all movies into the table.
     *
     * @return {@code true} if auto-connect succeeded; {@code false} otherwise
     */
    private boolean autoConnectAndLoad() {
        try {
            service.connect(DEFAULT_JDBC_URL, DEFAULT_DB_USER, DEFAULT_DB_PASS);
            if (service.isConnected()) {
                ensureSeedIfEmpty();             // Seed only when table is empty
                applyTableData(service.readAll());
                System.out.println("✅ Auto-connected to MySQL, seeded if empty, and loaded data.");
                return true;
            }
        } catch (Exception ex) {
            // Swallow here and let caller show manual dialog.
            System.err.println("Auto-connect failed: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Shows a simple modal dialog to capture only MySQL username and password
     * and connects using the default JDBC URL:
     * {@code jdbc:mysql://localhost:3306/dms_movies?...}
     *
     * <p>This avoids forcing the user to type the database location manually,
     * while still allowing them to supply credentials as required.</p>
     *
     * @return {@code true} if connected successfully; {@code false} otherwise
     */
    private boolean showConnectionDialogAndConnect() {
        JTextField userField = new JTextField(DEFAULT_DB_USER);
        JPasswordField passField = new JPasswordField(DEFAULT_DB_PASS);

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Username:")); p.add(userField);
        p.add(new JLabel("Password:")); p.add(passField);

        int res = JOptionPane.showConfirmDialog(
                this,
                p,
                "Connect to MySQL (dms_movies)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (res != JOptionPane.OK_OPTION) return false;

        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        try {
            // Always use default URL: jdbc:mysql://localhost:3306/dms_movies...
            service.connect(DEFAULT_JDBC_URL, user, pass);
            JOptionPane.showMessageDialog(
                    this,
                    "Connected to MySQL successfully.\nDatabase: dms_movies",
                    "Connection",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        } catch (SQLException sqle) {
            showError("Database connection failed:\n" + friendlySql(sqle));
        } catch (Exception ex) {
            showError("Unexpected error:\n" + ex.getMessage());
        }
        return false;
    }

    /**
     * Seeds the database with at least one row if table {@code movies} is empty.
     * <p>This ensures a fresh DB shows content on first run. Non-blocking on failure.</p>
     */
    private void ensureSeedIfEmpty() {
        try (java.sql.Connection c =
                     java.sql.DriverManager.getConnection(
                             System.getProperty("JDBC_URL", DEFAULT_JDBC_URL),
                             System.getProperty("DB_USER", DEFAULT_DB_USER),
                             System.getProperty("DB_PASS", DEFAULT_DB_PASS))) {

            try (java.sql.Statement st = c.createStatement();
                 java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM movies")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    // Minimal seed (replace with your full 20 inserts if desired)
                    st.executeUpdate(
                            "INSERT INTO movies (movie_id,title,director,release_year,duration_minutes,genre,rating) VALUES " +
                                    "('INT2010','Inception','Christopher Nolan',2010,148,'Science Fiction',9.0)"
                    );
                    System.out.println("ℹ️ Seeded table 'movies' with a minimal row.");
                }
            }
        } catch (Exception ignore) {
            // Do not block the app if seeding fails; it's just a convenience.
            System.err.println("Seeding skipped/failed: " + ignore.getMessage());
        }
    }

    /** Reloads all data from the DB into the table; shows an error if not connected. */
    private void safeRefreshAll() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        try {
            applyTableData(service.readAll());
        } catch (SQLException sqle) {
            showError("Failed to load data:\n" + friendlySql(sqle));
        }
    }

    /** Search action: filters by title (case-insensitive LIKE on DAO side). */
    private void onSearch() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        String q = txtSearch.getText();
        if (q == null || q.isBlank()) { safeRefreshAll(); return; }
        try {
            applyTableData(service.searchByTitle(q.trim()));
        } catch (SQLException sqle) {
            showError("Search failed:\n" + friendlySql(sqle));
        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        }
    }

    /** Create handler: opens the modal creation dialog and refreshes the table on success. */
    private void onAdd() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        MovieFormDialog.openCreate(this, service, this::safeRefreshAll);
    }

    /** Update handler: requires a selected row; opens the modal edit dialog and refreshes on success. */
    private void onEdit() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        Movie selected = getSelectedMovieFromTable();
        if (selected == null) { showError("Select a row to edit."); return; }
        MovieFormDialog.openEdit(this, service, selected, this::safeRefreshAll);
    }

    /** Delete handler: asks for confirmation and deletes by {@code movie_id}. */
    private void onDelete() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        Movie selected = getSelectedMovieFromTable();
        if (selected == null) { showError("Select a row to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete movie with ID: " + selected.getMovieId() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = service.deleteById(selected.getMovieId());
            if (ok) safeRefreshAll();
            else showError("Movie ID not found: " + selected.getMovieId());
        } catch (SQLException sqle) {
            showError("Delete failed:\n" + friendlySql(sqle));
        }
    }

    /** Custom action handler: computes and shows the average duration of all movies. */
    private void onAverage() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        try {
            double avg = service.averageDuration();
            JOptionPane.showMessageDialog(this, String.format("Average duration: %.2f minutes", avg),
                    "Custom Feature", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException sqle) {
            showError("Unable to compute average:\n" + friendlySql(sqle));
        }
    }

    /** Graceful exit handler: closes DB connection (if open), disposes the frame, and exits JVM. */
    private void onExit() {
        try { service.close(); } catch (SQLException ignore) {}
        dispose();
        System.exit(0);
    }

    /**
     * Applies a list of movies to the table model and clears selection.
     *
     * @param list list of {@link Movie} objects to render
     */
    private void applyTableData(List<Movie> list) {
        tableModel.setRowCount(0);
        for (Movie m : list) {
            tableModel.addRow(new Object[]{
                    m.getMovieId(), m.getTitle(), m.getDirector(),
                    m.getReleaseYear(), m.getDurationMinutes(), m.getGenre(), m.getRating()
            });
        }
        table.clearSelection();
    }

    /**
     * Extracts the currently selected {@link Movie} from the table, or returns {@code null} if none selected.
     *
     * @return selected movie or {@code null}
     */
    private Movie getSelectedMovieFromTable() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int row = table.convertRowIndexToModel(viewRow);
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        String title = String.valueOf(tableModel.getValueAt(row, 1));
        String director = String.valueOf(tableModel.getValueAt(row, 2));
        int year = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 3)));
        int duration = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 4)));
        String genre = String.valueOf(tableModel.getValueAt(row, 5));
        double rating = Double.parseDouble(String.valueOf(tableModel.getValueAt(row, 6)));
        return new Movie(id, title, director, year, duration, genre, rating);
    }

    /**
     * Shows an error dialog with the given message.
     *
     * @param msg message to display
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Truncates very long SQL messages to keep dialogs readable.
     *
     * @param ex SQL exception
     * @return shortened, user-friendly message
     */
    private static String friendlySql(SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) return "Unknown SQL error.";
        if (msg.length() > 300) msg = msg.substring(0, 300) + "...";
        return msg;
    }

    /**
     * Utility: reads a value from System properties first, then environment variables, or returns a default.
     * <p>Example: run with {@code -DDB_USER=root} or set env var {@code DB_USER}.</p>
     *
     * @param key    property/env key
     * @param defVal default value if not found
     * @return resolved value
     */
    private static String getPropOrEnv(String key, String defVal) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v;
        v = System.getenv(key);
        if (v != null && !v.isBlank()) return v;
        return defVal;
    }
}
