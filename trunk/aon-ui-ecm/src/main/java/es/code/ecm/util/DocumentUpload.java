package es.code.ecm.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/07/2007
 *
 */
public class DocumentUpload implements Serializable {

	private static final long serialVersionUID = 7396574084982407310L;

	/** DocumentUpload class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( DocumentUpload.class.getName() );

	/** Document name. */
	String name;
	/** Document title. */
	String title;
	/** Document keywords. */
	String keywords;
	/** Document nowords. */
	String nowords;
	/** Document language. */
	String language;
	/** Document category. */
	String category;
	/** Document access level. */
	Long level;

	/** Document length. */
	long length;
	/** Document lastModified. */
	Calendar lastModified;
	/** Document mime type*/
	String mimeType;
	/** Document content. */
	byte[] data;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the nowords
	 */
	public String getNowords() {
		return nowords;
	}

	/**
	 * @param nowords the nowords to set
	 */
	public void setNowords(String nowords) {
		this.nowords = nowords;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the level
	 */
	public Long getLevel() {
		return level;
	}

	/**
	 * @param level
	 */
	public void setLevel(Long level) {
		this.level = level;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		mimeType = "application/octet-stream";
		try {
			MagicMatch match = Magic.getMagicMatch( data );
			mimeType = match.getMimeType();
		} catch (MagicParseException e) {
			LOGGER.debug( e.getMessage() );
		} catch (MagicMatchNotFoundException e) {
			LOGGER.debug( e.getMessage() );
		} catch (MagicException e) {
			LOGGER.debug( e.getMessage() );
		}
	}

	public void paint(OutputStream stream, Object object) throws IOException {
		stream.write( getData() );
	}


}
