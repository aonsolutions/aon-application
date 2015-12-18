package com.esferalia.aon.gwt.template.server.marketplace;

import java.io.IOException;
import java.text.Collator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.jooq.DBMarketplace;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.Product;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.AttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;


public class MarketplaceImpl extends AonRemoteServiceServlet implements IMarketplace{

	
	private static final long serialVersionUID = 6871016881549113129L;
	
	public User getUser() {
		return new User()
				.setId(getUserID())
				.setLogin(getUserLogin())
				.setDomain(getUserDomainID());
	}

	public List<EcommerceProduct> getProductTemplatesList(Domain domain){
		return DBMarketplace.getProductTemplatesList(domain, getUser());
	}
	
	public List<Order> getAmazonOrdersList(Domain domain, String login){
		return DBMarketplace.getOrderList(domain, login);
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
	
	public void deleteTemplate(Domain domain, String description){
		DBMarketplace.deleteTemplate(domain, getUser(), description);
	}
	
	public LinkedList<Tag> getMarketplaceTagList(Domain domain){
		return DBMarketplace.getMarketplaceTagList(domain, getUser());
	}
	
	public List<Product> getProductList(Domain domain, String login, Integer category){
		return DBMarketplace.getProductList(domain, login, category);
	}
	
	public Vector<Product> searchProductByName(String searchStr, Vector<Product> list){
		Vector<Product> vector = new Vector<Product>();
		for (Product p : list) {
			if(containsIgnoreCase2(p.getName(), searchStr)){
				vector.add(p);
			}
		}
		return vector;
	}
	
	public List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList){
		return DBMarketplace.getAttachTemplateTagList(domain, login, pTagList);
	}
	
	public List<Attach> obtainEcommerceProductTemplates(Domain domain, Product product){
		List<Attach> list = AON.getAttachList(domain.getName()
				, domain.getId()
				, getUserLogin()
				, filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
				, AttachType.REGISTRY);
		return list;
	}
	
	public Attach obtainEcommerceProductAttach(Domain domain, Product product, String templateName){
		return DBMarketplace.getItemTemplateAttach(domain, getUserLogin(), templateName, product);
	}

	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Attach attach, Product product){
		EcommerceProduct ecommerceProduct = null;
		if(attach!=null){
			try {
				ecommerceProduct = XMLUtils.readXml(attach.getData());
				ecommerceProduct.setProduct(new EcommerceProduct.Product());
				ecommerceProduct.getProduct().setId(product.getId().toString());
				ecommerceProduct.getProduct().setCode(product.getCode());
				ecommerceProduct.getProduct().setName(product.getName());
			} catch (JAXBException e) {
				ecommerceProduct = null;
			}
		}
		return ecommerceProduct;
	}
	
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Product product, String templateName){
		// Obtiene la plantilla especifica del producto
		Attach attach = obtainEcommerceProductAttach(domain, product, templateName);
		// Obtiene la plantilla generica 
		if(attach==null){
			attach = AON.getAttach(domain.getName()
					, domain.getId()
					, getUserLogin()
					, filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
					.and(filter.getDescriptionProperty().eq(templateName))
					, AttachType.REGISTRY);
		}
		
		return obtainEcommerceProductValues(domain, attach, product);
	}
	
	public Boolean insertEcommerceProductValues(Domain domain, String login, String templateName, EcommerceProduct ecommerceProduct, Attach attach){
		
		byte[] data = null;
		try {
			data = XMLUtils.writeXml(ecommerceProduct);
		} catch (JAXBException e) {
			data = null;
		} catch (IOException e) {
			data = null;
		}

		if(data!=null){
			if(attach==null || attach.getId()==null){
				attach = new Attach();
			}
			attach.setDomain(domain);
			attach.setMimeType(MimeType.XML);
			attach.setDescription(templateName);
			attach.setAttachType(AttachType.ITEM);
			attach.setAttachModule(Integer.parseInt(ecommerceProduct.getProduct().getId()));
			attach.setType(AttachmentType.ECOMMERCE_PRODUCT.value());
			attach.setConfidential(false);
			attach.setData(data);
			if(attach==null || attach.getId()==null){
				AON.insert(domain.getName(), domain.getId(), login, attach);
			} else {
				AON.update(domain.getName(), domain.getId(), login, attach);
			}
			return true;
		}
		return false;
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
	
	public String getDateStr(Date date){
		return Utils.getDateStr(date);
	}
}
