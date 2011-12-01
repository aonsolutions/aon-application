package es.code.ecm.nodes;

import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18/09/2008
 *
 */
public class Category implements ECMNode {

	private static final long serialVersionUID = 4712265615240285055L;

	/** Category class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( Category.class.getName() );

	/** Wrapped JCR node. */
	private transient Node node;
	/** Document Node wrapped properties. */
	transient String name;

	public static final String getNodeName() {
		return ContentRepository.getNodeName( ECMQName.AON_CATEGORY );
	}

	/**
	 * Constructs a <code>Category</code> object.
	 */
	public Category() {
		super();
	}

	/**
	 * Constructs a <code>Category</code> object.
	 * 
	 * @param node
	 */
	public Category(Node node) throws RepositoryException {
		this.node = node;
	}

	/**
	 * @param node
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void fillName(String name) throws RepositoryException {
		String _name = ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME );
		this.node.setProperty( _name, new StringValue( name ) );
		setName( name );
	}

	/**
	 * @param user UUID
	 */
	public void setUser(String userUUID) throws RepositoryException {
		node.setProperty( User.getNodeType(), new StringValue( userUUID ) );
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public String getStatus() throws RepositoryException {
//	do nothing
		return null;
	}

	@Override
	public String getJcrName() throws RepositoryException {
		return node.getName();
	}

	@Override
	public Date getCreated() throws RepositoryException {
//	do nothing
		return null;
	}

	@Override
	public String getVersion() throws RepositoryException {
//	do nothing
		return null;
	}

	/**
	 * @return the name
	 */
	public String getName() throws RepositoryException {
		if ( name == null && node != null ) {
			String _name = ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME );
			try {
				name = node.getProperty( _name ).getString();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return name;
	}

	@Override
	public String getType() {
		return CATEGORY_TYPE;
	}

	@Override
	public String getUser() throws RepositoryException {
		return node.getProperty( User.getNodeType() ).getString();
	}

}
