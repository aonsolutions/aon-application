package es.code.cdr.beans;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.StringValue;

import es.code.cdr.CDRQName;
import es.code.cdr.core.ContentRepository;

/**
 * This class wraps JCR folder node.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class Folder implements CDRNode {

	/** Wrapped JCR node. */
	Node node;

	/**
	 * Constructs a <code>Folder</code> object.
	 * 
	 * @param node
	 */
	public Folder(Node node) {
		this.node = node;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status) throws RepositoryException {
//		do nothing
	}

	/**
	 * @param author
	 */
	public void setAuthor(String author) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHOR );
		node.setProperty( name, new StringValue( author ) );
	}

	/**
	 * @param calendar
	 */
	public void setEntryDate(Calendar calendar) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_ENTRYDATE );
		node.setProperty( name, new DateValue( calendar ) );
	}

	/**
	 * @param roles
	 */
	public void setRoles(String[] roles) throws RepositoryException {
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_AUTHREAD ), roles );
	}

	/**
     * Returns the node at <code>relPath</code> relative to this node.
	 * 
     * @param relPath The relative path of the node to retrieve.
	 * @return
	 * @throws RepositoryException 
	 * @throws PathNotFoundException 
	 */
	public Node getNode(String relPath) throws RepositoryException {
		return node.getNode( relPath );
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getNode()
	 */
	public Node getNode() {
		return node;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getStatus()
	 */
	public String getStatus() throws RepositoryException {
		String status = "N";
		if ( node.isLocked() ) 
			status = "B";
		else if ( node.isCheckedOut() || node.isModified() )
			status = "M";
		return status;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getName()
	 */
	public String getName() throws RepositoryException {
		return node.getName();
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getAuthor()
	 */
	public String getAuthor() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHOR );
		return node.getProperty( name ).getName();
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getEntryDate()
	 */
	public Date getEntryDate() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_ENTRYDATE );
		return node.getProperty( name ).getDate().getTime();
	}

}