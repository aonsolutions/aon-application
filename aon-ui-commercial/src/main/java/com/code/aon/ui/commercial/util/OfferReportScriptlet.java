package com.code.aon.ui.commercial.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.util.ReportScriptlet;
import com.esferalia.aon.entity.IEntityAlias;

import net.sf.jasperreports.engine.JRScriptletException;

public class OfferReportScriptlet extends ReportScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferReportScriptlet.class.getName());
	
	private final String BACKGROUND_VARIABLE_NAME = "PPTO_FONDO";
	
	/**
	 * REPORT TEMPLATE FIELDS
	 */
	private final String FIELD_SUPPLIER = "supplier";
	private final String FIELD_TYPE = "type";
	
	@Override
	public InputStream getOfferBackgroundFile() {
		try {
			OfferType type = (OfferType) super.getFieldValue(FIELD_TYPE);
			if( OfferType.DEALERSHIP == type ) {
				Supplier supplier = (Supplier) super.getFieldValue(FIELD_SUPPLIER);
				RegistryAttachment rAttach = getBackgroundFromSupplier( supplier );
				if (rAttach != null) {
					byte[] data = rAttach.getData();
					if(data != null && data.length>0){
						return new ByteArrayInputStream(data);
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Cannot load target custom offer background image.", e);
		} catch (JRScriptletException e) {
			LOGGER.error("ERROR: no se ha podido obtener el proveedor",e);
		}
		return super.getOfferBackgroundFile();
	}
	
	public RegistryAttachment getBackgroundFromSupplier( Supplier supplier ) throws ManagerBeanException {
		IManagerBean attach = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(attach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), supplier.getId());
		criteria.addEqualExpression(attach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), BACKGROUND_VARIABLE_NAME);
		List<ITransferObject> list = attach.getList(criteria);
		return (! list.isEmpty() ) ? (RegistryAttachment) list.get(0) : null;
	}
	
	
}