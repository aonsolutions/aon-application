/**
 * 
 */
package es.code.cdr.beans;

import java.io.Serializable;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public interface CDRNode extends Serializable {

	public static final String FOLDER_TYPE = "folder";
	public static final String DOCUMENT_TYPE = "document";

	/**
	 * @return the node
	 */
	Node getNode();

	/**
	 * @return the type
	 */
	String getType();

	/**
	 * @return the status
	 */
	String getStatus() throws RepositoryException;

	/**
	 * @return the name
	 */
	String getName() throws RepositoryException;

	/**
	 * @return the author
	 */
	String getAuthor() throws RepositoryException;

	/**
	 * @return the entrydate
	 */
	Date getEntryDate() throws RepositoryException;
	
	/**
	 * @return the version
	 */
	String getVersion() throws RepositoryException;

}
