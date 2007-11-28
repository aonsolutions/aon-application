package es.code.cdr.beans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.mime.Magic;
import com.code.aon.common.mime.MagicException;
import com.code.aon.common.mime.MagicMatch;
import com.code.aon.common.mime.MagicMatchNotFoundException;
import com.code.aon.common.mime.MagicParseException;

import es.code.cdr.CDRQName;
import es.code.cdr.IConstants;
import es.code.cdr.core.ContentRepository;
import es.code.cdr.ui.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/07/2007
 *
 */
public class Document implements CDRNode {

	/** Document class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( Document.class.getName() );

	/** Wrapped JCR node. */
	Node node;

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
		node.setProperty( name, new DateValue( calendar ) );
	}

	/**
	 * @param keywords
	 */
	public void setKeywords(String keywords) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_KEYWORDS );
		try {
			node.setProperty( name, JCRUtils.string2array( keywords ) );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
	}

	/**
	 * @param nowords
	 */
	public void setNowords(String nowords) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_NOWORDS );
		try {
			node.setProperty( name, JCRUtils.string2array( nowords ) );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
	}

	/**
	 * @param category
	 */
	public void setCategory(String category) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_CATEGORY );
		try {
			node.setProperty( name, new StringValue( category ) );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
	}

	/**
	 * @param language
	 */
	public void setLanguage(String language) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_LANGUAGE );
		try {
			node.setProperty( name, new StringValue( language ) );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
	}
	
	/**
	 * @param roles
	 */
	public void setRoles(String[] roles) throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_AUTHREAD );
		node.setProperty( name, roles );
	}

	/**
	 * Adds the document resouce.
	 * 
	 * @param resource
	 * @throws IOException
	 * @throws RepositoryException
	 */
	public void addContent(URL resource) throws IOException, RepositoryException {
		Node resNode = node.addNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ), ContentRepository.getNodeName( CDRQName.AON_RESOURCE ) );
		setContent( resNode, resource );
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
		String status = "D";
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		if ( node.isLocked() ) 
			status = "B";
		else if ( contentNode.isCheckedOut() )
			status = "C";
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
		return node.getProperty( name ).getString();
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.beans.CDRNode#getEntryDate()
	 */
	public Date getEntryDate() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_ENTRYDATE );
		return node.getProperty( name ).getDate().getTime();
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_KEYWORDS );
		try {
			return JCRUtils.value2String ( node.getProperty( name ).getValues() );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
		return null;
	}

	/**
	 * @return the nowords
	 */
	public String getNowords() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_NOWORDS );
		try {
			return JCRUtils.value2String ( node.getProperty( name ).getValues() );
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
		return null;
}

	/**
	 * @return the category
	 */
	public String getCategory() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_CATEGORY );
		try {
			return node.getProperty( name ).getString();
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
		return null;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() throws RepositoryException {
		String name = ContentRepository.getNodeName( CDRQName.AON_LANGUAGE );
		try {
			return node.getProperty( name ).getString();
		} catch (PathNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
		return null;
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
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		return contentNode.getProperty( IConstants.JCR_DATA ).getStream();
	}

	/**
	 * @return the mimetype
	 */
	public String getMimeType() throws RepositoryException {
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		return contentNode.getProperty( IConstants.JCR_MIMETYPE ).getString();
	}

	public Collection getVersionHistory() throws RepositoryException {
		ArrayList<Version> l = new ArrayList<Version>();
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		VersionHistory vh = contentNode.getVersionHistory();
		for(VersionIterator vi = vh.getAllVersions(); vi.hasNext();) {
			l.add( vi.nextVersion() );
		}
		return l;
	}

	public Version checkin() throws RepositoryException {
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		return contentNode.checkin();
	}

	public Version checkin(String author, URL resource) throws IOException, RepositoryException {
		setAuthor( author );
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		setContent( contentNode, resource );
		contentNode.save();
		Version version = checkin();
//		unlock();
		return version;
	}

	public void checkout() throws RepositoryException {
//		lock();
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		contentNode.checkout();
	}

	public boolean isCheckedOut() throws RepositoryException {
		Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
		return contentNode.isCheckedOut();
	}

	public void restore() throws RepositoryException {
		try {
//            String lockToken = node.getLock().getLockToken();
//			unlock();
//			node.getSession().removeLockToken( lockToken );
			Node contentNode = node.getNode( ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
			contentNode.restore( contentNode.getBaseVersion(), true );
		} catch(RepositoryException e) {e.printStackTrace();}
	}

	public void lock() throws RepositoryException {
		node.lock( true, false );
	}

	public void unlock() throws RepositoryException {
		node.unlock();
	}

	private void setContent(Node resNode, URL resource) throws IOException, RepositoryException {
		File file = new File( resource.getFile() );
		String mimeType = "application/octet-stream";
		try {
			MagicMatch match = Magic.getMagicMatch( file, true, false );
			mimeType = match.getMimeType();
		} catch (MagicParseException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		} catch (MagicMatchNotFoundException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		} catch (MagicException e) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug( e.getMessage(), e );
		}
		resNode.setProperty( ContentRepository.getNodeName( CDRQName.AON_SIZE ), new LongValue( file.length() ) );
		resNode.setProperty( IConstants.JCR_MIMETYPE, mimeType );
		resNode.setProperty( IConstants.JCR_ENCODING, JCRUtils.EMPTY_STRING );
		resNode.setProperty( IConstants.JCR_DATA, resource.openStream() );
		Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis(file.lastModified());
		resNode.setProperty( IConstants.JCR_LASTMODIFIED, lastModified );
		
	}
}