package net.aonsolutions.core.pool;

/**
 * Checked exception thrown when an attempt is made to access a schema that does
 * not exist.
 *
 */

public class NoSuchShemaException extends AonConnectionException {
	
	private String schema;
	
    /**
     * Constructs an instance of this class.
     *
     * @param   schema
     *          a string identifying the schema or {@code null} if not known.
     */
    public NoSuchShemaException(String schema) {
        this.schema = schema;
    }
}
