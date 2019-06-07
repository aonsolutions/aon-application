package com.code.aon.ui.commercial.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.company.util.ReportScriptlet;
import com.esferalia.aon.entity.IEntityAlias;

import net.sf.jasperreports.engine.JRScriptletException;

public class OfferReportScriptlet extends ReportScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferReportScriptlet.class.getName());
	
	/**
	 * REPORT TEMPLATE FIELDS
	 */
	public static final String FIELD_ID = "id";
	public static final String FIELD_TARGET = "target";
	
	@Override
	public InputStream getOfferBackgroundFile() {
		try {
			Target target = (Target) super.getFieldValue(FIELD_TARGET);
			RegistryAttachment rAttach = getTargetOfferBackground( target );
			if (rAttach != null) {
				byte[] data = rAttach.getData();
				if(data != null && data.length>0){
					return new ByteArrayInputStream(data);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Cannot load target custom offer background image.", e);
		} catch (JRScriptletException e) {
			LOGGER.error("ERROR: no se ha podido obtener el cliente potencial",e);
		}
		return super.getOfferBackgroundFile();
	}
	
	public RegistryAttachment getTargetOfferBackground( Target target ) throws ManagerBeanException {
		IManagerBean targetAttach = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(targetAttach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), target.getId());
		criteria.addEqualExpression(targetAttach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), "OFFER_BG_TEMPLATE");
		List<ITransferObject> list = targetAttach.getList(criteria);
		return (! list.isEmpty() ) ? (RegistryAttachment) list.get(0) : null;
	}
	
	
}