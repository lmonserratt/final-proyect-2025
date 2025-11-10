/**
 * Service layer coordinating validation, business rules and DAO orchestration.
 * Shields the GUI from SQL exceptions and centralizes input checks.
 *
 * Typical flow:
 * <ol>
 *   <li>Validate inputs.</li>
 *   <li>Call DAO methods.</li>
 *   <li>Translate low-level errors to user-friendly messages.</li>
 * </ol>
 *
 * @author Luis Augusto Monserratt
 * @since 1.0
 */
package dms.service;
