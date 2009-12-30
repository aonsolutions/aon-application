/**
 * 
 */
package es.code.ecm;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import es.code.ecm.nodes.ECMNode;
import es.code.ecm.util.ECMUtil;

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

	String title;
	String keywords;
	String category;
	String language;
	String fileformat;
	Date publishDate;

    private static SelectItem[] LANGUAGES;

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
    		Locale locale = ECMUtil.getCurrentLocale( FacesContext.getCurrentInstance() );
	    	ResourceBundle bundle = ResourceBundle.getBundle( IConstants.ECM_BUNDLE_NAME, locale );
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
     * Gets the option file formats.
     *
     * @return array of file formats
     */
    public SelectItem[] getFileFormats() {
        return FILE_FORMATS;
    }

    @Override
	public ECMNode getSelectedNode() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	public void perform(Widget dependentWidget) {
		bycontent = null;
		allwords = null;
		somewords = null;
		nowords = null;
		phrase = null;
		language = null;
		fileformat = null;
		publishDate = null;
		title = null;
	}

}
