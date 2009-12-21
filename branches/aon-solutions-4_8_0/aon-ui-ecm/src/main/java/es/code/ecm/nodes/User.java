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
import es.code.ecm.IConstants;
import es.code.ecm.util.JCRUtils;

/**
 * This class wraps JCR user node.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19/11/2008
 *
 */
public class User implements ECMNode {

	private static final long serialVersionUID = 2610439652540188549L;

	/** User class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( User.class.getName() );

	/** Wrapped JCR node. */
	private transient Node node;
	private transient String name;
	private transient String roles;
	private transient Long level;

	public static final String getNodeType() {
		return ContentRepository.getNodeName( ECMQName.AON_USER );
	}

	/**
	 * Constructs a <code>User</code> object.
	 * 
	 * @param node
	 */
	public User(Node node) throws RepositoryException {
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
	public void setName(String name) throws RepositoryException {
		String nodeName = ContentRepository.getNodeName( ECMQName.AON_NAME );
		node.setProperty( nodeName, new StringValue( name ) );
	}

	/**
	 * @param domain
	 */
	public void setDomain(String domain) throws RepositoryException {
		String nodeName = ContentRepository.getNodeName( ECMQName.AON_DOMAIN );
		node.setProperty( nodeName, new StringValue( domain ) );
	}

	/**
	 * @param login
	 */
	public void setLogin(String login) throws RepositoryException {
		String nodeName = ContentRepository.getNodeName( ECMQName.AON_LOGIN );
		node.setProperty( nodeName, new StringValue( login ) );
	}

	/**
	 * @param roles
	 */
	public void setRoles(String roles) throws RepositoryException {
		String nodeName = ContentRepository.getNodeName( ECMQName.AON_ROLES );
		node.setProperty( nodeName, JCRUtils.string2array( roles ) );
	}

	/**
	 * @param level
	 */
	public void setLevel(Long level) throws RepositoryException {
		this.level = level;
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
		if ( this.name == null ) {
			String nodeName = ContentRepository.getNodeName( ECMQName.AON_NAME );
			try {
				this.name = node.getProperty( nodeName ).getString();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return this.name;
	}

	@Override
	public String getType() {
		return USER_TYPE;
	}

	@Override
	public String getUser() throws RepositoryException {
		throw new UnsupportedOperationException( NOT_SUPPORTED );
	}

	/**
	 * @return the domain
	 */
	public String getDomain() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_DOMAIN );
		return node.getProperty( name ).getString();
	}

	/**
	 * @return the login
	 */
	public String getLogin() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_LOGIN );
		return node.getProperty( name ).getString();
	}

	/**
	 * @return the roles
	 */
	public String getRoles() throws RepositoryException {
		if ( roles == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_ROLES );
			try {
				roles = JCRUtils.value2String ( node.getProperty( name ).getValues() );
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return roles;
	}

	/**
	 * @return the level
	 */
	public Long getLevel() throws RepositoryException {
		if ( level == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_LEVEL );
			try {
				level = node.getProperty( name ).getLong();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return level;
	}

}