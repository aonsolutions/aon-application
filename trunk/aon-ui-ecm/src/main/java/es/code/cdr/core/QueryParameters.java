/**
 * 
 */
package es.code.cdr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import es.code.cdr.IConstants;
import es.code.cdr.beans.CDRNode;
import es.code.repository.util.Path;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 17/07/2007
 *
 */
public class QueryParameters extends AbstractWidget {

	private static final long serialVersionUID = 5013580659737102750L;

	String bycontent;
	String allwords;
	String somewords;
	String nowords;
	String phrase;

	String language;
	String category;
	String fileformat;
	Date publishDate;

    private static SelectItem[] LANGUAGES;
    private static SelectItem[] CATEGORIES;

    private static final SelectItem[] FILE_FORMATS = new SelectItem[]{
    	new SelectItem( IConstants.UNKNOWN, IConstants.EMPTY_STRING ),
    	new SelectItem( "text/html", "HTML" ),
    	new SelectItem( "application/vnd.ms-excel", "MS Excel" ),
    	new SelectItem( "application/vnd.ms-powerpoint", "MS PowerPoint" ),
    	new SelectItem( "application/msword", "MS Word" ),
    	new SelectItem( "application/pdf", "PDF" ),
    	new SelectItem( "application/rtf", "RTF" ),
    	new SelectItem( "text/plain", "TXT" ),
    	new SelectItem( "text/xml", "XML" ),
    };

	/**
	 * @return the allwords
	 */
	public String getAllwords() {
		return allwords;
	}

	/**
	 * @param allwords the allwords to set
	 */
	public void setAllwords(String allwords) {
		this.allwords = allwords;
	}

	/**
	 * @return the bycontent
	 */
	public String getBycontent() {
		return bycontent;
	}

	/**
	 * @param bycontent the bycontent to set
	 */
	public void setBycontent(String bycontent) {
		this.bycontent = bycontent;
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
	 * @return the phrase
	 */
	public String getPhrase() {
		return phrase;
	}

	/**
	 * @param phrase the phrase to set
	 */
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	/**
	 * @return the somewords
	 */
	public String getSomewords() {
		return somewords;
	}

	/**
	 * @param somewords the somewords to set
	 */
	public void setSomewords(String somewords) {
		this.somewords = somewords;
	}

	/**
	 * 
	 * @return
	 */
	public String getAdvancedSearchContent() {
		return allwords 
				+ ( (phrase.length() > 0)? " '" + phrase + "'": "" ) 
				+ ( (somewords.length() > 0)? " " + somewords.trim().replaceAll( " " , " OR " ): "" )  
				+ ( (nowords.length() > 0)? " -" + nowords.trim().replaceAll( " " , " -" ): "" ) ;
	}

	/**
	 * @return the fileformat
	 */
	public String getFileformat() {
		return fileformat;
	}

	/**
	 * @param fileformat the fileformat to set
	 */
	public void setFileformat(String fileformat) {
		this.fileformat = fileformat;
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
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

    /**
     * Gets the option languages.
     *
     * @return array of languages
     */
    public SelectItem[] getLanguages() {
    	if ( LANGUAGES == null ) {
    		FacesContext ctx = FacesContext.getCurrentInstance();
	    	Locale locale = ctx.getExternalContext().getRequestLocale();
	    	ResourceBundle bundle = ResourceBundle.getBundle( IConstants.CDR_BUNDLE_NAME, locale );
    		LANGUAGES = 
    			new SelectItem[]{
    		    	new SelectItem( IConstants.UNKNOWN, IConstants.EMPTY_STRING ),
    		    	new SelectItem( IConstants.ES, bundle.getString( "aon_language_es" ) ),
    		    	new SelectItem( IConstants.EN, bundle.getString( "aon_language_en" ) ),
    		    };
    	}
        return LANGUAGES;
    }

    /**
     * Gets the option categories.
     *
     * @return array of categories
     */
    public SelectItem[] getCategories() {
    	if ( CATEGORIES == null ) {
    		FacesContext ctx = FacesContext.getCurrentInstance();
	    	Locale locale = ctx.getExternalContext().getRequestLocale();
	    	ResourceBundle bundle = ResourceBundle.getBundle( IConstants.CDR_BUNDLE_NAME, locale );
	    	Properties categories = new Properties();
			try {
				InputStream is = Path.getResource( System.getProperty( IConstants.CATEGORIES_PATH_FILE_KEY ), IConstants.CATEGORIES_PATH, "" ).openStream();
				categories.load( is );
	    		CATEGORIES = new SelectItem[ categories.size() + 1 ];
				Iterator<Object> iter = categories.values().iterator();
				int i = 0;
				CATEGORIES[ i ] = new SelectItem( IConstants.UNKNOWN, IConstants.EMPTY_STRING );
				while (iter.hasNext()) {
					i++;
					Object elem = iter.next();
					CATEGORIES[ i ] = new SelectItem( elem, bundle.getString( "aon_category_" + elem ) );
				}
			} catch (IOException e) {
	    		CATEGORIES = new SelectItem[]{ new SelectItem( IConstants.UNKNOWN, IConstants.EMPTY_STRING ) };
			}
    	}
        return CATEGORIES;
    }

    /**
     * Gets the option file formats.
     *
     * @return array of file formats
     */
    public SelectItem[] getFileFormats() {
        return FILE_FORMATS;
    }

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#getSelectedNode()
	 */
	public CDRNode getSelectedNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#perform(es.code.cdr.ui.controller.Widget)
	 */
	public void perform(Widget dependentWidget) {
		bycontent = null;
		allwords = null;
		somewords = null;
		nowords = null;
		phrase = null;
		language = null;
		fileformat = null;
		publishDate = null;
	}

}
