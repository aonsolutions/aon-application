package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.LinesController;

public class RegistryDirStaffLinesController extends LinesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryDirStaffLinesController.class);
	
	@SuppressWarnings("unchecked")
	public double getTotalPercentShare() throws ManagerBeanException {
		double total = 0.0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getPercentShare();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public int getTotalShareNumber() throws ManagerBeanException{
		int total = 0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getShareNumber();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public double getTotalNominalValue() throws ManagerBeanException{
		double total = 0.0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getNominalValue();
		}
		return total;
	}

	public void onChangeShareHolder( ValueChangeEvent event) {
		RegistryDirStaff rds = (RegistryDirStaff) getTo();
		rds.setNominalValue(0.0);
		rds.setPercentShare(0.0);
		rds.setShareNumber(0);
	}

	public void onDocumentChange( ValueChangeEvent event) {
		try {
			RegistryDirStaff rds = (RegistryDirStaff) getTo();
			String document = (String) event.getNewValue();
			if (StringUtils.isNotBlank(document)) {
				String docAlias = getManagerBean().getFieldName(IRegistryAlias.REGISTRY_DIR_STAFF_DOCUMENT);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(docAlias, document);
				List<ITransferObject> list = getManagerBean().getList(criteria);
				if (list != null && list.size() > 0 ) {
					RegistryDirStaff r = (RegistryDirStaff) list.get(0);
					rds.setName( r.getName() );
				} else {
					IManagerBean rBean = BeanManager.getManagerBean(Registry.class);
					docAlias = rBean.getFieldName(IRegistryAlias.REGISTRY_DOCUMENT);
					criteria = new Criteria();
					criteria.addEqualExpression(docAlias, document);
					list = rBean.getList(criteria);
					if (list != null && list.size() > 0 ) {
						Registry registry = (Registry) list.get(0);
						rds.setName( registry.getName() );	
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(),e);			
		}
		
		
	}
	
}
