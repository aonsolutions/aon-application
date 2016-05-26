package com.esferalia.aon.gwt.template.server.marketplace;

import java.text.Collator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.math.NumberUtils;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.jooq.DBMarketplace;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;

import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.TagType;


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
	
	public Tag addMarketplaceTag(Domain domain, String name){
		return DBMarketplace.insertMarketplaceTag(domain, getUser(),new Tag()
			.setDomain(domain.getId()).setName(name).setType(TagType.MARKETPLACE.value()));
	}
	
	public void removeMarketplaceTag(Domain domain, Tag tag){
		DBMarketplace.deleteMarketplaceTag(domain, getUser(), tag);
	}
	
	public Tag updateMarketplaceTag(Domain domain, Tag tag){
		DBMarketplace.updateMarketplaceTag(domain, getUser(), tag);
		return tag;
	}
	
	public LinkedList<Tag> getMarketplaceTagList(Domain domain){
		return DBMarketplace.getMarketplaceTagList(domain, getUser());
	}
	
	public List<Product> getProductList(Domain domain, String login, Integer category){
		return DBMarketplace.getProductList(domain, login, category);
	}
	
	public List<Product> getProductList(Domain domain, String login, Integer category, Boolean active){
		return DBMarketplace.getProductList(domain, login, category, active, null, null);
	}

	public List<Product> getSalesProductList(Domain domain, String login, Integer category, Boolean active, Boolean sales){
		return DBMarketplace.getProductList(domain, login, category, active, sales, null);
	} 
	
	public List<Item> getMarketItemList(Domain domain, String login, Integer category, Boolean active, Boolean sales){
		return DBMarketplace.getMarketItemList(domain, login, category, active, sales);
	} 
	
	public Vector<Item> searchItemByProductName(String searchStr, Vector<Item> list){
		Vector<Item> vector = new Vector<Item>();
		for (Item i : list) {
			if(containsIgnoreCase2(i.getProduct().getName(), searchStr)){
				vector.add(i);
			}
		}
		return vector;
	}
	
	public List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList){
		return DBMarketplace.getAttachTemplateTagList(domain, login, pTagList);
	}
	
	public List<Attach> obtainEcommerceProductTemplates(Domain domain, String sellerId){
		Integer id = NumberUtils.isNumber(sellerId)?Integer.parseInt(sellerId):null;
		return AON.getAttachList(
						domain.getName(),
						domain.getId(),
						getUserLogin(),
						filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
								.and(sellerId != null ? filter.getAttachModuleProperty().eq(id): filter.getAttachModuleProperty().isNotNull()), 
						AttachType.REGISTRY);
	}
	
	public Attach obtainEcommerceProductAttach(Domain domain, Item item, String templateName){
		return DBMarketplace.getItemTemplateAttach(domain, getUserLogin(), templateName, item);
	}

	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Attach attach, Item item){
		EcommerceProduct ecommerceProduct = null;
		if(attach!=null){
			try {
				ecommerceProduct = XMLUtils.readXml(attach.getData());
				ecommerceProduct.setProduct(new EcommerceProduct.Product());
				ecommerceProduct.getProduct().setId(item.getProduct().getId().toString());
				ecommerceProduct.getProduct().setCode(item.getProduct().getCode());
				ecommerceProduct.getProduct().setName(item.getProduct().getName());
			} catch (JAXBException e) {
				ecommerceProduct = null;
			}
		}
		return ecommerceProduct;
	}
	
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, Item item, String templateName){
		Attach attach = AON.getAttach(domain.getName()
				, domain.getId()
				, getUserLogin()
				, filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
				.and(filter.getDescriptionProperty().eq(templateName))
				, AttachType.REGISTRY);
		
		return obtainEcommerceProductValues(domain, attach, item);
	}
	
	public Boolean acceptEcommerceProductValues(Domain domain, String login, Item item, String templateName, EcommerceProduct ecommerceProduct, Attach attach){
		return DBMarketplace.acceptProductValues(domain, login, item, templateName, ecommerceProduct, attach);
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
