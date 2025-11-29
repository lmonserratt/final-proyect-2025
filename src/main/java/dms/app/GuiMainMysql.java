package dms.app;

import dms.gui.MovieTableFrameMysql;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the MySQL-based Movie Manager DMS application (Final version).
 *
 * <p>This class simply launches the main Swing GUI window. All database connection
 * details (host, port, database name) are handled inside {@link MovieTableFrameMysql},
 * so the user never has to type the database location manually.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 *   java -jar movie-manager-dms-1.0.0.jar
 * }</pre>
 *
 * <p>On launch, the application will try to auto-connect to:
 * {@code jdbc:mysql://localhost:3306/dms_movies}
 * and if needed, the user can enter only username/password from the GUI.</p>
 *
 * @author  Luis
 * @version 1.1
 * @since   1.0
 */
public final class GuiMainMysql {

  /** Not instantiable. Use {@link #main(String[])} to start the application. */
  private GuiMainMysql() { }

  /**
   * Entry point of the Movie Manager DMS application.
   *
   * @param args command-line arguments (unused)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        new MovieTableFrameMysql().setVisible(true);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(
                null,
                "Error starting application:\n" + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
      }
    });
  }
}
