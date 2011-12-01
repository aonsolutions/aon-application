package es.code.ecm.nodes;

import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;

import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.IConstants;

/**
 * This class wraps JCR folder node.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class Folder implements ECMNode {

	private static final long serialVersionUID = 618347765904857468L;

	/** Wrapped JCR node. */
	transient Node node;

	/**
	 * Constructs a <code>Folder</code> object.
	 * 
	 * @param node
	 */
	public Folder(Node node) throws RepositoryException {
		this.node = node;
	}

	/**
	 * @param name
	 */
	public void setName(String name) throws RepositoryException {
		String nodeName = ContentRepository.getNodeName( ECMQName.AON_NAME );
		node.setProperty( nodeName, new StringValue( name ) );
	}

	/**
	 * @param user UUID
	 */
	public void setUser(String userUUID) throws RepositoryException {
		node.setProperty( User.getNodeType(), new StringValue( userUUID ) );
	}

	/**
	 * @param level
	 */
	public void setAccessControl(long level) throws RepositoryException {
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ), new LongValue( level ) );
	}

	/**
	 * @param qname, CRUD options: ContentRepository.getNodeName( ECMQName.AON_READ ).
	 * @param array of userUUID.
	 */
	public void setAccessControl(String qname, String[] userUUID) throws RepositoryException {
		node.setProperty( qname, userUUID );
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

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public String getStatus() throws RepositoryException {
		String status = "N";
		if ( node.isLocked() ) 
			status = "B";
		else if ( node.isCheckedOut() || node.isModified() )
			status = "M";
		return status;
	}

	@Override
	public String getJcrName() throws RepositoryException {
		return node.getName();
	}

	@Override
	public Date getCreated() throws RepositoryException {
		return node.getProperty( IConstants.JCR_CREATED ).getDate().getTime();
	}

	@Override
	public String getVersion() throws RepositoryException {
		return node.getBaseVersion().getName();
	}

	@Override
	public String getName() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_NAME );
		return node.getProperty( name ).getString();
	}

	@Override
	public String getType() {
		return FOLDER_TYPE;
	}

	@Override
	public String getUser() throws RepositoryException {
		return node.getProperty( User.getNodeType() ).getString();
	}

}