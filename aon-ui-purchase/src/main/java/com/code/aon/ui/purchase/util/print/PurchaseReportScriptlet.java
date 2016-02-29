package com.code.aon.ui.purchase.util.print;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAddInfo;
import com.code.aon.product.Product;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.AttachmentType;

public class PurchaseReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseReportScriptlet.class.getName());
	
	private static final String FIELD_ID = "id";

	private static final String MANUFACTURING_DETAIL_TEXT = "ELABORACION_DETALLE";
	
	private static final String MANUFACTURING_COMMENTS_TEXT = "ELABORACION_OBSERVACIONES";
	
	public String getItemManufacturingDetail() throws JRScriptletException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
			PurchaseDetail detail = (PurchaseDetail) bean.get((Integer)super.getFieldValue(FIELD_ID));
			for(ItemAddInfo addInfo: detail.getItem().getProduct().getBaseItem().getAddInfos().toArray(new ItemAddInfo[0])){
				if(addInfo.getAttribute().equals(MANUFACTURING_DETAIL_TEXT)){
					return addInfo.getValue();
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}

	public String getItemManufacturingComments() throws JRScriptletException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
			PurchaseDetail detail = (PurchaseDetail) bean.get((Integer)super.getFieldValue(FIELD_ID));
			for(ItemAddInfo addInfo: detail.getItem().getProduct().getBaseItem().getAddInfos().toArray(new ItemAddInfo[0])){
				if(addInfo.getAttribute().equals(MANUFACTURING_COMMENTS_TEXT)){
					return addInfo.getValue();
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
	public List<EcommerceProduct> getValuesTemplate(Product product) throws JRScriptletException{
		List<EcommerceProduct> list = new ArrayList<EcommerceProduct>();
		try {
			for(Attach attach: getTemplates(product.getBaseItem())){
				if(attach!=null){
					EcommerceProduct ecommerceProduct = null;
					ecommerceProduct = readXml(attach.getData());
					// line below is for jasperreport print only
					ecommerceProduct.getTemplate().setEcommerce(attach.getDescription());
					ecommerceProduct.setProduct(new EcommerceProduct.Product());
					ecommerceProduct.getProduct().setId(product.getId().toString());
					ecommerceProduct.getProduct().setCode(product.getCode());
					ecommerceProduct.getProduct().setName(product.getName());
					list.add(ecommerceProduct);
				}
			}
			list.sort((o1, o2) -> o1.getTemplate().getEcommerce().compareTo(o2.getTemplate().getEcommerce()));
		} catch (JAXBException e) {
			String msg = "No se han podido obtener los valores de la plantilla del producto";
			LOGGER.error(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener los valores de la plantilla del producto";
			LOGGER.error(msg,e);
		}
		return list;
	}
	
	private List<Attach> getTemplates(Item item){
		List<Attach> list = null;
		list = AON.getAttachList(
				AonUtil.getDomainName(),
				DomainManager.getCurrentDomain(),
				UserUtils.getInstance().getLoggedUser().getLogin(),
				filter -> filter.getTypeProperty().eq(AttachmentType.ECOMMERCE_PRODUCT.value())
//					.and(sellerId != null ? filter.getAttachModuleProperty().eq(id): filter.getAttachModuleProperty().isNotNull()), 
					.and(filter.getAttachModuleProperty().eq(item.getId())), 
				AttachType.ITEM);
		return list;
	}
	
	private EcommerceProduct readXml(byte[] xmlFile) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(EcommerceProduct.class);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		InputStream input = new ByteArrayInputStream(xmlFile);
		EcommerceProduct ecommerceProduct = (EcommerceProduct) unmarshaller.unmarshal(input);
		return ecommerceProduct;
	}
	
}