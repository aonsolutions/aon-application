package com.code.aon.facturae;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;

public class FACeUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FACeUtil.class.getName());
	
	private static final String FACE_PREFFIX = "FACe_";
	
	private static final String CENTRE_CODE_SUFFIX = "_CENTRE_CODE";
	
	private static final String ADDRESS_SUFFIX = "_ADDRESS";

	private static final String DELIVERY_NUMBER_SUFFIX = "_DELIVERY_NUMBER";
	
	private static final String SEQUENCE_NUMBER_SUFFIX = "_SEQUENCE_NUMBER";

	private static final String ISSUER_CONTRACT_REFERENCE_SUFFIX = "_ISSUER_CONTRACT_REFERENCE";
	
	private static final String FISCAL = "FISCAL";
	
	private static final String RECEPTOR = "RECEPTOR";
	
	private static final String PAGADOR = "PAGADOR";
	
	private static final String COMPRADOR = "COMPRADOR";
	
	private static final String INVOICE = "INVOICE";
	
	public static final String FACE_ENABLED = FACE_PREFFIX + "ENABLED";
	
	public static final String FACE_FISCAL_CENTRE_CODE = FACE_PREFFIX + FISCAL + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_FISCAL_ADDRESS = FACE_PREFFIX + FISCAL + ADDRESS_SUFFIX;
	
	public static final String FACE_FISCAL_ROLE_TYPE_CODE = "01";
	
	public static final String FACE_FISCAL_DESCRIPTION = "Oficina Contable";

	public static final String FACE_RECEPTOR_CENTRE_CODE = FACE_PREFFIX + RECEPTOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_RECEPTOR_ADDRESS = FACE_PREFFIX + RECEPTOR + ADDRESS_SUFFIX;
	
	public static final String FACE_RECEPTOR_ROLE_TYPE_CODE = "02";
	
	public static final String FACE_RECEPTOR_DESCRIPTION = "Órgano Gestor";

	public static final String FACE_PAGADOR_CENTRE_CODE = FACE_PREFFIX + PAGADOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_PAGADOR_ADDRESS = FACE_PREFFIX + PAGADOR + ADDRESS_SUFFIX;
	
	public static final String FACE_PAGADOR_ROLE_TYPE_CODE = "03";
	
	public static final String FACE_PAGADOR_DESCRIPTION = "Unidad Tramitadora";

	public static final String FACE_COMPRADOR_CENTRE_CODE = FACE_PREFFIX + COMPRADOR + CENTRE_CODE_SUFFIX;
	
	public static final String FACE_COMPRADOR_ADDRESS = FACE_PREFFIX + COMPRADOR + ADDRESS_SUFFIX;
	
	public static final String FACE_COMPRADOR_ROLE_TYPE_CODE = "04";
	
	public static final String FACE_COMPRADOR_DESCRIPTION = "Subdirección de compras";
	
	public static final String FACE_VENDEDOR_ROLE_TYPE_CODE = "06";
	
	public static final String FACE_VENDEDOR_DESCRIPTION = "Vendedor";

	public static final String FACE_INVOICE_DELIVERY_NUMBER = FACE_PREFFIX + INVOICE + DELIVERY_NUMBER_SUFFIX;
	
	public static final String FACE_INVOICE_SEQUENCE_NUMBER = FACE_PREFFIX + INVOICE + SEQUENCE_NUMBER_SUFFIX;

	public static final String FACE_INVOICE_ISSUER_CONTRACT_REFERENCE = FACE_PREFFIX + INVOICE + ISSUER_CONTRACT_REFERENCE_SUFFIX;
	
	public static final String[] FACE_CONSTANTS = {
		FACeUtil.FACE_ENABLED,
		FACeUtil.FACE_FISCAL_CENTRE_CODE, FACeUtil.FACE_FISCAL_ADDRESS,
		FACeUtil.FACE_RECEPTOR_CENTRE_CODE, FACeUtil.FACE_RECEPTOR_ADDRESS,
		FACeUtil.FACE_PAGADOR_CENTRE_CODE, FACeUtil.FACE_PAGADOR_ADDRESS,
		FACeUtil.FACE_COMPRADOR_CENTRE_CODE, FACeUtil.FACE_COMPRADOR_ADDRESS
	};	
	public static final String[] FACE_REQUIRED_CONSTANTS = {
		FACeUtil.FACE_INVOICE_DELIVERY_NUMBER, FACeUtil.FACE_INVOICE_SEQUENCE_NUMBER, 
		FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE
	};	

	public static RegistryNote getRegistryNote( String key, Integer registryId ) {
    	RegistryNote note = null;
    	try {
       		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
       		Criteria criteria = new Criteria();
       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), registryId);
       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.FACTURAE);
       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION), key);
       		List<ITransferObject> list = bean.getList(criteria);
       		if (! list.isEmpty() ) {
       			note = (RegistryNote) list.get(0);
       		}    		
    	} catch ( ManagerBeanException e ) {
    		LOGGER.error(e.getMessage(), e);
    	}
   		return note;
    }
	
	public static boolean isDefined( Invoice invoice ) {
		if ( invoice.getType() == InvoiceType.SALES ) {
			try {
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				Customer customer = (Customer) customerBean.get(invoice.getRegistry().getId());
				if ( (customer != null) && (customer.isEInvoice()) ) {
		       		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
		       		Criteria criteria = new Criteria();
		       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), customer.getId());
		       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.FACTURAE);
		       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION), FACE_ENABLED);
		       		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS), Boolean.TRUE.toString().toLowerCase());
		       		return (bean.getCount(criteria) > 0);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return false;
	}
	
	public static String getValue( String key, Invoice invoice ) {
		RegistryNote note = getRegistryNote(key, invoice.getRegistry().getId());
		if ( note != null ) {
			return StringUtils.trimToNull(note.getComments());
		}
		return null;
	}

	public static RegistryAddress getAddress( String key, Invoice invoice ) throws ManagerBeanException {
		RegistryAddress address = null;
		RegistryNote note = getRegistryNote(key, invoice.getRegistry().getId());
		if (! StringUtils.isEmpty(note.getComments()) ) {
			int id = NumberUtils.toInt(note.getComments());
			IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
			return (RegistryAddress) bean.get(id);
		}
		return address;
	}		
	
}
