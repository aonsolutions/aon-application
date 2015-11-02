package com.esferalia.aon.gwt.template.server.marketplace;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.jooq.DBMarketplace;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class MarketplaceImpl extends RemoteServiceServlet implements IMarketplace{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MarketplaceImpl.class.getName());
	
	private static final long serialVersionUID = 6871016881549113129L;

	void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	

	public List<EcommerceProduct> getProductTemplatesList(Integer domainId){
		String domainName = AonUtil.getDomainName();
		return DBMarketplace.getProductTemplatesList(domainName, domainId);
	}
	
	public Vector<EcommerceProduct> searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates){
		Vector<EcommerceProduct> vector = new Vector<EcommerceProduct>();
		for (EcommerceProduct templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getTemplate().getType(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}
	
	public Vector<EcommerceProduct> searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates){
		Vector<EcommerceProduct> vector = new Vector<EcommerceProduct>();
		for (EcommerceProduct templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getTemplate().getEcommerce(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}
	
	/**
	 * <p>
	 * Checks if CharSequence contains a search CharSequence irrespective of
	 * case, handling {@code null}. Case-insensitivity is defined as by
	 * {@link String#equalsIgnoreCase(String)}.
	 *
	 * <p>
	 * A {@code null} CharSequence will return {@code false}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.contains(null, *) = false
	 * StringUtils.contains(*, null) = false
	 * StringUtils.contains("", "") = true
	 * StringUtils.contains("abc", "") = true
	 * StringUtils.contains("abc", "a") = true
	 * StringUtils.contains("ábc", "a") = true
	 * StringUtils.contains("abc", "z") = false
	 * StringUtils.contains("abc", "A") = true
	 * StringUtils.contains("ábc", "A") = true
	 * StringUtils.contains("abc", "Z") = false
	 * </pre>
	 * @param str
	 * @param searchStr
	 * @return
	 */
	public static boolean containsIgnoreCase2(String str, String searchStr) {
	    Locale locale = new Locale("es_ES");
		Collator c = Collator.getInstance(locale);
		c.setStrength(Collator.PRIMARY);
	    if (str == null || searchStr == null) {
	        return false;
	    }
	    int len = searchStr.length();
	    int max = str.length() - len;
	    for (int i = 0; i <= max; i++) {   	
	    	if (c.compare(str.substring(i, i+len), searchStr) == 0)
	    		return true;  
	    }
	    return false;
	}
}
