package com.esferalia.aon.gwt.template.server.marketplace;

import java.text.Collator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.jooq.DBMarketplace;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;


public class MarketplaceImpl extends AonStatelessRemoteServiceServlet implements IMarketplace{

	
	private static final long serialVersionUID = 6871016881549113129L;

	@Override
	public List<EcommerceProduct> getProductTemplatesList(Domain domain, User user){
		return DBMarketplace.getProductTemplatesList(domain, user);
	}
	
	@Override
	public List<Order> getAmazonOrdersList(Domain domain, String login){
		return DBMarketplace.getOrderList(domain, login);
	}
	
	@Override
	public Vector<EcommerceProduct> searchNameTemplate(String searchStr, Vector<EcommerceProduct> templates){
		Vector<EcommerceProduct> vector = new Vector<EcommerceProduct>();
		for (EcommerceProduct templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getTemplate().getType(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}

	@Override
	public Vector<EcommerceProduct> searchTypeTemplate(String searchStr, Vector<EcommerceProduct> templates){
		Vector<EcommerceProduct> vector = new Vector<EcommerceProduct>();
		for (EcommerceProduct templateInfo : templates) {
			if(containsIgnoreCase2(templateInfo.getTemplate().getEcommerce(), searchStr)){
				vector.add(templateInfo);
			}
		}
		return vector;
	}
	
	@Override
	public void deleteTemplate(Domain domain, User user, String description){
		DBMarketplace.deleteTemplate(domain, user, description);
	}
	
	@Override
	public Tag addMarketplaceTag(Domain domain, User user, String name){
		return DBMarketplace.insertMarketplaceTag(domain, user,new Tag()
			.setDomain(domain.getId()).setName(name).setType(TagType.MARKETPLACE));
	}

	@Override
	public void removeMarketplaceTag(Domain domain, User user, Tag tag){
		DBMarketplace.deleteMarketplaceTag(domain, user, tag);
	}
	
	@Override
	public Tag updateMarketplaceTag(Domain domain, User user, Tag tag){
		DBMarketplace.updateMarketplaceTag(domain, user, tag);
		return tag;
	}
	
	@Override
	public LinkedList<Tag> getMarketplaceTagList(Domain domain, User user){
		return DBMarketplace.getMarketplaceTagList(domain, user);
	}

	@Override
	public List<OldProduct> getProductList(Domain domain, String login, Integer category){
		return DBMarketplace.getProductList(domain, login, category);
	}
	
	@Override	
	public List<OldProduct> getProductList(Domain domain, String login, Integer category, Boolean active){
		return DBMarketplace.getProductList(domain, login, category, active, null, null);
	}

	@Override
	public List<OldProduct> getSalesProductList(Domain domain, String login, Integer category, Boolean active, Boolean sales){
		return DBMarketplace.getProductList(domain, login, category, active, sales, null);
	} 
	
	@Override
	public List<OldItem> getMarketItemList(Domain domain, String login, Integer category, Boolean active, Boolean sales){
		return DBMarketplace.getMarketItemList(domain, login, category, active, sales);
	} 
	
	@Override
	public Vector<OldItem> searchItemByProductName(String searchStr, Vector<OldItem> list){
		Vector<OldItem> vector = new Vector<OldItem>();
		for (OldItem i : list) {
			if(containsIgnoreCase2(i.getProduct().getName(), searchStr)){
				vector.add(i);
			}
		}
		return vector;
	}
	
	@Override
	public List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList){
		return DBMarketplace.getAttachTemplateTagList(domain, login, pTagList);
	}
	
	@Override
	public List<Attach> obtainEcommerceProductTemplates(Domain domain, User user, String sellerId){
		Integer id = AonNumberUtils.toInteger(sellerId);
		return AON.getAttachList(
						domain.getName(),
						domain.getId(),
						user.getLogin(),
						filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
								.and(sellerId != null ? filter.getAttachModuleProperty().eq(id): filter.getAttachModuleProperty().isNotNull()), 
						AttachType.REGISTRY);
	}
	
	@Override
	public Attach obtainEcommerceProductAttach(Domain domain, User user, OldItem item, String templateName){
		return DBMarketplace.getItemTemplateAttach(domain, user.getLogin(), templateName, item);
	}

	@Override
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, User user, Attach attach, OldItem item){
		EcommerceProduct ecommerceProduct = null;

		if(attach!=null){
			if(attach.getData() == null && attach.getDriveId() != null){
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), "");
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			try {
				ecommerceProduct = XMLUtils.readXml(attach.getData());
				ecommerceProduct.setProduct(new EcommerceProduct.Product());
				ecommerceProduct.getProduct().setId(item.getProduct().getId().toString());
				ecommerceProduct.getProduct().setCode(item.getProduct().getCode());
				ecommerceProduct.getProduct().setName(item.getProduct().getName());
				ecommerceProduct.getProduct().setItem(item.getId().toString());
			} catch (JAXBException e) {
				ecommerceProduct = null;
			}
		}
		return ecommerceProduct;
	}
	
	@Override
	public EcommerceProduct obtainEcommerceProductValues(Domain domain, User user, OldItem item, String templateName){
		Attach attach = AON.getAttach(domain.getName()
				, domain.getId()
				, user.getLogin()
				, filter -> filter.getTypeProperty().eq(RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())
				.and(filter.getDescriptionProperty().eq(templateName))
				, AttachType.REGISTRY);
		
		return obtainEcommerceProductValues(domain, user, attach, item);
	}
	
	public Boolean acceptEcommerceProductValues(Domain domain, String login, OldItem item, String templateName, EcommerceProduct ecommerceProduct, Attach attach){
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
	
}
