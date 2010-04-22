package es.code.ecm.nodes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.IConstants;
import es.code.ecm.util.DocumentUpload;
import es.code.ecm.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/07/2007
 *
 */
public class Document implements ECMNode {

	/** Document class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( Document.class.getName() );

	/** Wrapped JCR node. */
	private transient Node node;
	/** Document Node wrapped properties. */
	transient String name;
	transient String keywords;
	transient String nowords;
	transient String language;
//    private boolean subscribed;
//    private Collection subscriptors;
	transient String userName;
	transient String category;
	transient Long level;

	/**
	 * Constructs a <code>Document</code> object.
	 * 
	 * @param node
	 */
	public Document(Node node) throws RepositoryException {
		this.node = node;
	}

	/**
	 * @param name
	 */
	public void setName(String name) throws RepositoryException {
		this.name = name;
	}

	/**
	 * @param keywords
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @param nowords
	 */
	public void setNowords(String nowords) {
		this.nowords = nowords;
	}

	/**
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param level
	 */
	public void setLevel(Long level) {
		this.level = level;
	}

	/**
	 * @param user UUID
	 */
	public void setUser(String userUUID) throws RepositoryException {
		node.setProperty( User.getNodeType(), new StringValue( userUUID ) );
	}

	/**
	 * @param qname, CRUD options: ContentRepository.getNodeName( ECMQName.AON_READ ).
	 * @param array of userUUID.
	 */
	public void setAccessControl(String qname, String[] userUUID) throws RepositoryException {
		node.setProperty( qname, userUUID );
	}

	/**
	 * Adds the document.
	 * 
	 * @param dup
	 * @throws IOException
	 * @throws RepositoryException
	 */
	public void add(DocumentUpload dup) throws IOException, RepositoryException {
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_NAME ), new StringValue( dup.getTitle() ) );
		setName( dup.getTitle() );

		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_KEYWORDS ), JCRUtils.string2array( dup.getKeywords() ) );
		setKeywords( dup.getKeywords() );

		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_NOWORDS ), JCRUtils.string2array( dup.getNowords() ) );
		setNowords( dup.getNowords() );

		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_LANGUAGE ), new StringValue( dup.getLanguage() ) );
		setLanguage( dup.getLanguage() );

		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME ), new StringValue( dup.getCategory() ) );
		setCategory( dup.getCategory() );

		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ), new LongValue( dup.getLevel() ) );
		setLevel( dup.getLevel() );

		Node resNode = 
			node.addNode( IConstants.JCR_CONTENT, ContentRepository.getNodeName( ECMQName.AON_RESOURCE ) );
		setContent( resNode, dup );
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public Date getCreated() throws RepositoryException {
		return node.getProperty( IConstants.JCR_CREATED ).getDate().getTime();
	}

	@Override
	public String getJcrName() throws RepositoryException {
		return node.getName();
	}

	@Override
	public String getStatus() throws RepositoryException {
		String status = "D";
		Node contentNode = node.getNode( IConstants.JCR_CONTENT );
		if ( node.isLocked() ) 
			status = "B";
		else if ( contentNode.isCheckedOut() )
			status = "C";
		return status;
	}

	@Override
	public String getVersion() throws RepositoryException {
		return getContentNode().getBaseVersion().getName();
	}

	@Override
	public String getName() throws RepositoryException {
		if ( this.name == null ) {
			String _name = ContentRepository.getNodeName( ECMQName.AON_NAME );
			try {
				this.name = node.getProperty( _name ).getString();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return this.name;
	}

	@Override
	public String getType() {
		return DOCUMENT_TYPE;
	}

	@Override
	public String getUser() throws RepositoryException {
		return node.getProperty( User.getNodeType() ).getString();
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() throws RepositoryException {
		if ( keywords == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_KEYWORDS );
			try {
				keywords = JCRUtils.value2String ( node.getProperty( name ).getValues() );
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return keywords;
	}

	/**
	 * @return the nowords
	 */
	public String getNowords() throws RepositoryException {
		if ( nowords == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_NOWORDS );
			try {
				nowords = JCRUtils.value2String ( node.getProperty( name ).getValues() );
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return nowords;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() throws RepositoryException {
		if ( language == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_LANGUAGE );
			try {
				language = node.getProperty( name ).getString();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return language;
	}


	/**
	 * @return the category
	 */
	public String getCategory() throws RepositoryException {
		if ( category == null ) {
			String name = ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME );
			try {
				category = node.getProperty( name ).getString();
			} catch (PathNotFoundException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return category;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		if ( userName == null ) {
			try {
				String uuid = getUser();
				Node userNode = node.getSession().getNodeByUUID( uuid );
				String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_NAME );
				userName = userNode.getProperty( primaryNodeTypeName ).getString();
			} catch (RepositoryException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return userName;
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

	/**
	 * @return the read, group and user UUID
	 */
	public Value[] getReadRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_READ );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the write, group and user UUID
	 */
	public Value[] getWriteRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_WRITE );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the delete, group and user UUID
	 */
	public Value[] getDeleteRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( ECMQName.AON_DELETE );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the node content
	 */
	public Node getContentNode() throws RepositoryException {
		return node.getNode( IConstants.JCR_CONTENT );
	}

	/**
	 * @return the node content <code>InputStream</code>
	 */
	public InputStream getContent() throws RepositoryException {
		Node contentNode = getContentNode();
		return contentNode.getProperty( IConstants.JCR_DATA ).getStream();
	}

	/**
	 * @return the mimetype
	 */
	public String getMimeType() throws RepositoryException {
		Node contentNode = getContentNode();
		return contentNode.getProperty( IConstants.JCR_MIMETYPE ).getString();
	}

	/**
	 * Return the <code>Document</code> content for selected version.
	 * 
	 * @param version
	 * @return
	 * @throws UnsupportedRepositoryOperationException
	 * @throws RepositoryException
	 */
	public InputStream getDocument4Version(Version version) 
			throws UnsupportedRepositoryOperationException, RepositoryException {
        Node frozenNode = version.getNode( IConstants.JCR_FROZENNODE );
        return frozenNode.getProperty( IConstants.JCR_DATA ).getStream();
	}

	/**
	 * Returns the document version history.
	 *  
	 * @return
	 * @throws RepositoryException
	 */
	public List<Version> getVersionHistory() throws RepositoryException {
		ArrayList<Version> l = new ArrayList<Version>();
		Node contentNode = getContentNode();
		VersionHistory vh = contentNode.getVersionHistory();
		for(VersionIterator vi = vh.getAllVersions(); vi.hasNext();) {
			Version version = vi.nextVersion();
            if( !version.getName().equals( IConstants.JCR_ROOTVERSION ) )
            	l.add( version );
		}
		return l;
	}

	public void save() throws RepositoryException {
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_NAME ), new StringValue( getName() ) );
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_KEYWORDS ), JCRUtils.string2array( getKeywords() ) );
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_NOWORDS ), JCRUtils.string2array( getNowords() ) );
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_LANGUAGE ), new StringValue( getLanguage() ) );
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME ), new StringValue( getCategory() ) );
		node.setProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ), new LongValue( getLevel() ) );

		node.save();
	}

	public void refresh(boolean bol) throws RepositoryException {
		node.refresh( bol );
	}

	public Version checkin(DocumentUpload dup) throws IOException, RepositoryException {
		setContent( getContentNode(), dup );
		node.save();
		Version version = checkin();
		unlock();
		return version;
	}

	public Version checkin() throws RepositoryException {
		return getContentNode().checkin();
	}

	public void checkout() throws RepositoryException {
		lock();
		getContentNode().checkout();
	}

	public boolean isCheckedOut() throws RepositoryException {
		return getContentNode().isCheckedOut();
	}

	public void restore() throws RepositoryException {
        String lockToken = node.getLock().getLockToken();
		unlock();
		node.getSession().removeLockToken( lockToken );
		Node contentNode = getContentNode();
		contentNode.restore( contentNode.getBaseVersion(), true );
	}

	public void restoreVersion(String versionId) throws RepositoryException {
		Node contentNode = getContentNode();
        contentNode.restore( versionId, true );
        contentNode.save();
	}

    public void lock() throws RepositoryException {
		node.lock( true, false );
	}

	public void unlock() throws RepositoryException {
		node.unlock();
	}

	public boolean isLocked() throws RepositoryException {
        return node.isLocked();
	}

	private void setContent(Node resNode, DocumentUpload dup) throws IOException, RepositoryException {
		resNode.setProperty( ContentRepository.getNodeName( ECMQName.AON_NAME ), new StringValue( dup.getTitle() ) );
/*TODO Falta las siguientes propiedades que se obtienen al parsear el documento.
	<propertyDefinition name="aon:title" autoCreated="false" mandatory="false" multiple="false" onParentVersion="COPY" protected="false" requiredType="String"/>
	<propertyDefinition name="aon:author" autoCreated="false" mandatory="false" multiple="false" onParentVersion="COPY" protected="false" requiredType="String"/>
	<propertyDefinition name="aon:created" autoCreated="false" mandatory="false" multiple="false" onParentVersion="IGNORE" protected="false" requiredType="Date"/>
	<propertyDefinition name="aon:modified" autoCreated="false" mandatory="false" multiple="false" onParentVersion="IGNORE" protected="false" requiredType="Date"/>
	<propertyDefinition name="aon:producer" autoCreated="false" mandatory="false" multiple="false" onParentVersion="IGNORE" protected="false" requiredType="String"/>
	<propertyDefinition name="aon:pages" autoCreated="false" mandatory="false" multiple="false" onParentVersion="IGNORE" protected="false" requiredType="Long"/>
 */ 
		resNode.setProperty( ContentRepository.getNodeName( ECMQName.AON_SIZE ), new LongValue( dup.getLength() ) );
		resNode.setProperty( IConstants.JCR_MIMETYPE, dup.getMimeType() );
		resNode.setProperty( IConstants.JCR_ENCODING, JCRUtils.EMPTY_STRING );
		resNode.setProperty( IConstants.JCR_DATA, new ByteArrayInputStream( dup.getData() ) );
		resNode.setProperty( IConstants.JCR_LASTMODIFIED, dup.getLastModified() );
	}

}