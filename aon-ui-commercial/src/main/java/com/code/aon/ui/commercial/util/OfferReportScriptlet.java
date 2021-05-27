package com.code.aon.ui.commercial.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
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
	private final String FIELD_TARGET = "target";
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
	
	public RegistryMedia getAdministrativeCellular() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.CELLULAR, true, false, false);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}
	
	public RegistryMedia getAdministrativeEmail() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.EMAIL, true, false, false);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}

	public RegistryMedia getTechnicalCellular() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.CELLULAR, false, true, false);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}
	
	public RegistryMedia getTechnicalEmail() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.EMAIL, false, true, false);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}

	public RegistryMedia getCommercialCellular() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.CELLULAR, false, false, true);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}
	
	public RegistryMedia getCommercialEmail() throws ManagerBeanException, JRScriptletException {
		List<ITransferObject> list = getRMediaList(MediaType.EMAIL, false, false, true);
		return list!=null && list.size()>0 ? ((RegistryMedia)list.get(0)): new RegistryMedia();
	}

	private List<ITransferObject> getRMediaList(MediaType type, boolean administrative, boolean technical,
			boolean commercial) throws ManagerBeanException, JRScriptletException {
		Target target = (Target) super.getFieldValue(FIELD_TARGET);

		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), target.getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		if (administrative)
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_ADMINISTRATIVE),
				administrative);
		if (commercial)
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL), commercial);
		
		if (technical)
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_TECHNICAL), technical);
		criteria.setSkipDomainFilter(true);
		return rMediaBean.getList(criteria);
	}
	
	public RegistryDirStaff getRDirStaff() throws ManagerBeanException, JRScriptletException {
		Target target = (Target) super.getFieldValue(FIELD_TARGET);

		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), target.getId());
		criteria.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(criteria);
		if(list!=null && list.size()>0)
			return (RegistryDirStaff) list.get(0);
		return null;
	}
	
	
}