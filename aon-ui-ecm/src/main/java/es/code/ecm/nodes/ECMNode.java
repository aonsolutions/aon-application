/**
 * 
 */
package es.code.ecm.nodes;

import java.io.Serializable;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public interface ECMNode extends Serializable {

	public static final String FOLDER_TYPE = "folder";
	public static final String DOCUMENT_TYPE = "document";
	public static final String CATEGORY_TYPE = "category";
	public static final String USER_TYPE = "user";
	public static final String NOT_SUPPORTED = "Not supported!";

	/**
	 * @return the JCR node
	 */
	Node getNode();

	/**
	 * @return the JCR status
	 */
	String getStatus() throws RepositoryException;

	/**
	 * @return the JCR name
	 */
	String getJcrName() throws RepositoryException;

	/**
	 * @return the JCR entry date.
	 */
	Date getCreated() throws RepositoryException;

	/**
	 * @return the JCR version
	 */
	String getVersion() throws RepositoryException;

	/**
	 * @return the name
	 */
	String getName() throws RepositoryException;

	/**
	 * @return the type
	 */
	String getType();

	/**
	 * @return the user UUID
	 */
	String getUser() throws RepositoryException;

}
