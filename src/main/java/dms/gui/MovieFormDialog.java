package dms.gui;

import dms.model.Movie;
import dms.service.MovieService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Modal dialog window used for creating or editing a {@link Movie} record.
 * <p>
 * This dialog is used by the Movie Manager DMS GUI to allow users
 * to input or modify movie information such as ID, title, director,
 * release year, duration, genre, and rating.
 * </p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Supports both "Create" and "Edit" modes, determined by constructor input.</li>
 *   <li>Performs input validation before saving.</li>
 *   <li>Uses {@link MovieService} to persist data into the database.</li>
 *   <li>Displays user-friendly success and error messages.</li>
 * </ul>
 *
 * <p><b>Author:</b> Luis Augusto Monserratt Alvarado</p>
 * <p><b>Version:</b> 1.0</p>
 */
public class MovieFormDialog extends JDialog {

    private JTextField txtId, txtTitle, txtDirector, txtGenre;
    private JSpinner spYear, spDuration, spRating;
    private final MovieService service;
    private final boolean editMode;
    private final Runnable onSuccess;

    // ---------- FACTORY METHODS ----------

    /**
     * Opens a modal dialog in "Create" mode.
     *
     * @param owner     the parent window that owns this dialog
     * @param service   the {@link MovieService} used to save the new movie
     * @param onSuccess callback executed after a successful creation
     */
    public static void openCreate(Window owner, MovieService service, Runnable onSuccess) {
        MovieFormDialog dlg = new MovieFormDialog(owner, service, null, onSuccess);
        dlg.setVisible(true);
    }

    /**
     * Opens a modal dialog in "Edit" mode.
     *
     * @param owner     the parent window that owns this dialog
     * @param service   the {@link MovieService} used to update the movie
     * @param existing  the {@link Movie} object to pre-fill the form
     * @param onSuccess callback executed after a successful update
     */
    public static void openEdit(Window owner, MovieService service, Movie existing, Runnable onSuccess) {
        MovieFormDialog dlg = new MovieFormDialog(owner, service, existing, onSuccess);
        dlg.setVisible(true);
    }

    // ---------- CONSTRUCTOR ----------

    /**
     * Constructs a {@code MovieFormDialog} in either create or edit mode.
     *
     * @param owner     the parent window that owns this dialog
     * @param service   the {@link MovieService} to handle database operations
     * @param existing  if non-null, dialog enters edit mode and preloads this movie
     * @param onSuccess callback executed after successful save/update (non-null)
     */
    public MovieFormDialog(Window owner, MovieService service, Movie existing, Runnable onSuccess) {
        super(owner, (existing == null ? "Add Movie" : "Edit Movie"), ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.editMode = (existing != null);
        this.onSuccess = (onSuccess != null ? onSuccess : () -> {});
        initUi();
        setLocationRelativeTo(owner);
        if (editMode) loadExisting(existing);
    }

    // ---------- UI INITIALIZATION ----------

    /**
     * Initializes and configures the dialog's user interface components.
     * Includes form fields, labels, buttons, and layout configuration.
     */
    private void initUi() {
        txtId = new JTextField(12);
        txtTitle = new JTextField(20);
        txtDirector = new JTextField(20);
        txtGenre = new JTextField(16);
        spYear = new JSpinner(new SpinnerNumberModel(2010, 1888, 2100, 1));
        spDuration = new JSpinner(new SpinnerNumberModel(120, 1, 999, 1));
        spRating = new JSpinner(new SpinnerNumberModel(7.5, 0.0, 10.0, 0.1));

        JButton btnSave = new JButton(editMode ? "Update" : "Create");
        JButton btnCancel = new JButton("Cancel");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        addRow(form, c, row++, new JLabel("Movie ID:"), txtId);
        addRow(form, c, row++, new JLabel("Title:"), txtTitle);
        addRow(form, c, row++, new JLabel("Director:"), txtDirector);
        addRow(form, c, row++, new JLabel("Release Year:"), spYear);
        addRow(form, c, row++, new JLabel("Duration (min):"), spDuration);
        addRow(form, c, row++, new JLabel("Genre:"), txtGenre);
        addRow(form, c, row++, new JLabel("Rating (0.0–10.0):"), spRating);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel);
        buttons.add(btnSave);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setResizable(false);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());
        getRootPane().setDefaultButton(btnSave);
    }

    /**
     * Helper method to add a labeled form field to a {@link JPanel}.
     *
     * @param p      the parent panel
     * @param c      the {@link GridBagConstraints} layout configuration
     * @param row    current row index
     * @param label  label component
     * @param field  input component
     */
    private static void addRow(JPanel p, GridBagConstraints c, int row, JComponent label, JComponent field) {
        c.gridy = row;
        c.gridx = 0;
        c.weightx = 0.0;
        p.add(label, c);
        c.gridx = 1;
        c.weightx = 1.0;
        p.add(field, c);
    }

    // ---------- DATA POPULATION ----------

    /**
     * Preloads an existing movie's data into the form fields for editing.
     *
     * @param m the existing {@link Movie} to edit
     */
    private void loadExisting(Movie m) {
        txtId.setText(m.getMovieId());
        txtId.setEnabled(false);
        txtTitle.setText(m.getTitle());
        txtDirector.setText(m.getDirector());
        spYear.setValue(m.getReleaseYear());
        spDuration.setValue(m.getDurationMinutes());
        txtGenre.setText(m.getGenre());
        spRating.setValue(m.getRating());
    }

    // ---------- SAVE OPERATION ----------

    /**
     * Handles the save/update button click event.
     * <p>
     * Validates input fields, constructs a {@link Movie} object,
     * and either creates or updates the record via {@link MovieService}.
     * </p>
     * <p>Displays error dialogs if validation or SQL exceptions occur.</p>
     */
    private void onSave() {
        try {
            String id = txtId.getText();
            String title = txtTitle.getText();
            String director = txtDirector.getText();
            int year = ((Number) spYear.getValue()).intValue();
            int duration = ((Number) spDuration.getValue()).intValue();
            String genre = txtGenre.getText();
            double rating = ((Number) spRating.getValue()).doubleValue();

            Movie movie = new Movie(id, title, director, year, duration, genre, rating);

            if (editMode) {
                service.update(movie);
                JOptionPane.showMessageDialog(this, "Movie updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                service.create(movie);
                JOptionPane.showMessageDialog(this, "Movie created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            onSuccess.run();
            dispose();

        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        } catch (SQLException sqle) {
            showError("Database error:\n" + friendlySqlMessage(sqle));
        } catch (Exception ex) {
            showError("Unexpected error:\n" + ex.getMessage());
        }
    }

    /**
     * Displays an error message in a dialog box.
     *
     * @param msg error message text
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns a shortened, user-friendly message extracted from a {@link SQLException}.
     *
     * @param ex the SQL exception
     * @return simplified message text
     */
    private static String friendlySqlMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) return "Unknown SQL error.";
        if (msg.length() > 300) msg = msg.substring(0, 300) + "...";
        return msg;
    }
}
