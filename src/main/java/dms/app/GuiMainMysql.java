package dms.app;

import dms.gui.MovieTableFrameMysql;

import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the MySQL-based Movie Manager DMS application (Phase 4).
 *
 * <p>This class initializes the Swing user interface and collects MySQL database
 * connection credentials from the user. It then exposes the JDBC information
 * as system properties to be consumed by the DAO/GUI layers and launches the
 * main application window.</p>
 *
 * <p><b>Responsibilities</b></p>
 * <ul>
 *   <li>Prompt the user for MySQL host, username, and password.</li>
 *   <li>Construct a JDBC URL targeting the {@code dms_movies} schema.</li>
 *   <li>Store {@code JDBC_URL}, {@code DB_USER}, and {@code DB_PASS} as system properties.</li>
 *   <li>Launch {@link dms.gui.MovieTableFrameMysql}.</li>
 * </ul>
 *
 * <p><b>Usage</b></p>
 * <pre>{@code
 *   java -jar movie-manager-dms-1.0.0.jar
 * }</pre>
 *
 * <p>On launch, the program prompts for connection details. If valid, the GUI is displayed.</p>
 *
 * @author  Luis
 * @version 1.0
 * @since   1.0
 * @see     dms.gui.MovieTableFrameMysql
 */
public final class GuiMainMysql {

  /** Not instantiable. Use {@link #main(String[])} to start the application. */
  private GuiMainMysql() { }

  /**
   * Entry point of the Movie Manager DMS application.
   *
   * <p>Runs the Swing event dispatcher thread, collects MySQL credentials via dialogs,
   * stores them as system properties, and launches the main GUI frame.</p>
   *
   * @param args command-line arguments (unused)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // --- Step 1: Request MySQL Host ---
        final String host = JOptionPane.showInputDialog(
                null,
                "Enter MySQL host (e.g., localhost):",
                "Database Connection",
                JOptionPane.QUESTION_MESSAGE
        );
        if (host == null || host.isBlank()) {
          JOptionPane.showMessageDialog(null, "No host provided. Exiting.");
          System.exit(0);
        }

        // --- Step 2: Request MySQL Username ---
        final String user = JOptionPane.showInputDialog(
                null,
                "Enter MySQL username:",
                "Database Connection",
                JOptionPane.QUESTION_MESSAGE
        );
        if (user == null || user.isBlank()) {
          JOptionPane.showMessageDialog(null, "No username provided. Exiting.");
          System.exit(0);
        }

        // --- Step 3: Request MySQL Password (hidden field) ---
        final JPasswordField pwdField = new JPasswordField();
        final int opt = JOptionPane.showConfirmDialog(
                null,
                pwdField,
                "Enter MySQL password:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        String pass = (opt == JOptionPane.OK_OPTION) ? new String(pwdField.getPassword()) : "";
        if (pass == null) pass = ""; // never null

        // --- Step 4: Construct the JDBC URL (default port 3306 and schema dms_movies) ---
        final String url = "jdbc:mysql://" + host.trim() + ":3306/dms_movies"
                + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";

        // --- Step 5: Publish connection data as system properties ---
        System.setProperty("JDBC_URL", url);
        System.setProperty("DB_USER", user.trim());
        System.setProperty("DB_PASS", pass);

        // --- Step 6: Launch the Main GUI Window ---
        new MovieTableFrameMysql().setVisible(true);

      } catch (Exception e) {
        // Show a concise error dialog and exit gracefully
        JOptionPane.showMessageDialog(
                null,
                "Error initializing application:\n" + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
      }
    });
  }
}
