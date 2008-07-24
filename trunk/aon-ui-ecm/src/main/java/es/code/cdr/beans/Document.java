package es.code.cdr.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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

import es.code.cdr.CDRQName;
import es.code.cdr.IConstants;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.ui.util.DocumentUpload;
import es.code.cdr.ui.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/07/2007
 *
 */
public class Document implements CDRNode {

	private static final long serialVersionUID = 6783041184514068135L;
	/** Document class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( Document.class.getName() );

	/** Wrapped JCR node. */
	private transient Node node;
	/** Document Node wrapped properties. */
	transient String authorName;
	transient String keywords;
	transient String nowords;
	transient String category;
	transient String language;

	/**
	 * Constructs a <code>Document</code> object.
	 * 
	 * @param node
	 */
	public Document(Node node) {
		this.node = node;
	}

	/**
	 * @param name
	 */
	public void setName(String name) throws RepositoryException {
//		do nothing
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
		node.setProperty( name, calendar );
	}

	/**
	 * @param roles
	 */
	public void setRoles(String[] roles) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHREAD );
		node.setProperty( name, roles );
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
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Adds the document.
	 * 
	 * @param dup
	 * @throws IOException
	 * @throws RepositoryException
	 */
	public void add(DocumentUpload dup) throws IOException, RepositoryException {
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_KEYWORDS ), JCRUtils.string2array( dup.getKeywords() ) );
		setKeywords( dup.getKeywords() );

		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_NOWORDS ), JCRUtils.string2array( dup.getNowords() ) );
		setNowords( dup.getNowords() );

		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_CATEGORY ), new StringValue( dup.getCategory() ) );
		setCategory( dup.getCategory() );

		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_LANGUAGE ), new StringValue( dup.getLanguage() ) );
		setLanguage( dup.getLanguage() );

		setRoles( new String[] {"Manager"} );

		Node resNode = 
			node.addNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ), 
					ContentRepository.getNodeName( CDRQName.AON_RESOURCE ) );
		setContent( resNode, dup );
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public String getType() {
		return DOCUMENT_TYPE;
	}

	@Override
	public String getStatus() throws RepositoryException {
		String status = "D";
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		if ( node.isLocked() ) 
			status = "B";
		else if ( contentNode.isCheckedOut() )
			status = "C";
		return status;
	}

	@Override
	public String getName() throws RepositoryException {
		return node.getName();
	}

	@Override
	public String getAuthor() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHOR );
		return node.getProperty( name ).getString();
	}

	@Override
	public Date getEntryDate() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_ENTRYDATE );
		return node.getProperty( name ).getDate().getTime();
	}

	@Override
	public String getVersion() throws RepositoryException {
		return getContentNode().getBaseVersion().getName();
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		if ( authorName == null ) {
			try {
				authorName = getAuthor();
				authorName = authorName.substring( 0, authorName.indexOf('@') );
			} catch (RepositoryException e) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( e.getMessage(), e );
			}
		}
		return authorName;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() throws RepositoryException {
		if ( keywords == null ) {
			String name = ContentRepository.getNodeName( CDRQName.AON_KEYWORDS );
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
			String name = ContentRepository.getNodeName( CDRQName.AON_NOWORDS );
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
	 * @return the category
	 */
	public String getCategory() throws RepositoryException {
		if ( category == null ) {
			String name = ContentRepository.getNodeName( CDRQName.AON_CATEGORY );
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
	 * @return the language
	 */
	public String getLanguage() throws RepositoryException {
		if ( language == null ) {
			String name = ContentRepository.getNodeName( CDRQName.AON_LANGUAGE );
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
	 * @return the read roles
	 */
	public Value[] getReadRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHREAD );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the write roles
	 */
	public Value[] getWriteRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHWRITE );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the delete roles
	 */
	public Value[] getDeleteRoles() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHDELETE );
		return node.getProperty( name ).getValues();
	}

	/**
	 * @return the node content <code>InputStream</code>
	 */
	public InputStream getContent() throws RepositoryException {
		Node contentNode = getContentNode();
		return contentNode.getProperty( IConstants.JCR_DATA ).getStream();
	}

	/**
	 * @return the node content
	 */
	public Node getContentNode() throws RepositoryException {
		return node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
	}

	/**
	 * @return the mimetype
	 */
	public String getMimeType() throws RepositoryException {
		Node contentNode = getContentNode();
		return contentNode.getProperty( IConstants.JCR_MIMETYPE ).getString();
	}

	/**
	 * Creates a <code>Document</code> for selected version.
	 * 
	 * @param version
	 * @return
	 * @throws UnsupportedRepositoryOperationException
	 * @throws RepositoryException
	 */
	public Document getDocument4Version(Version version) 
			throws UnsupportedRepositoryOperationException, RepositoryException {
		Document documentByVersion = new Document ( this.node );
//        Node frozenNode = version.getNode( IConstants.JCR_FROZENNODE );
//        Node resNode = documentByVersion.getContentNode();
//		resNode.setProperty( IConstants.JCR_DATA, frozenNode.getProperty( IConstants.JCR_DATA ).getStream() );
//		resNode.setProperty( IConstants.JCR_LASTMODIFIED, version.getCreated().getTimeInMillis() );
		return documentByVersion;
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
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_KEYWORDS ), JCRUtils.string2array( getKeywords() ) );
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_NOWORDS ), JCRUtils.string2array( getNowords() ) );
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_CATEGORY ), new StringValue( getCategory() ) );
		node.setProperty( ContentRepository.getNodeName( CDRQName.AON_LANGUAGE ), new StringValue( getLanguage() ) );
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
		resNode.setProperty( ContentRepository.getNodeName( CDRQName.AON_SIZE ), new LongValue( dup.getLength() ) );
		resNode.setProperty( IConstants.JCR_MIMETYPE, dup.getMimeType() );
		resNode.setProperty( IConstants.JCR_ENCODING, JCRUtils.EMPTY_STRING );
		resNode.setProperty( IConstants.JCR_DATA, new ByteArrayInputStream( dup.getData() ) );
		resNode.setProperty( IConstants.JCR_LASTMODIFIED, dup.getLastModified() );
	}

}