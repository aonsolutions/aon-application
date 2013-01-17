package com.esferalia.aon.ui.payroll.controller.enterprise;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseCCC;

public class EnterpriseCCCController extends LinesController {
	
	public void onChangeCcc(ActionEvent event){
		EnterpriseCCC ccc = (EnterpriseCCC) this.getTo();
		if(ccc.isValidSSNumber()){
			ccc.setGeozone(getGeoZoneByValue(ccc.getCcc()));
		}
	}

	private GeoZone getGeoZoneByValue(String code) {
		try {
			Domain domain = getCurrentDomain();
			IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_CODE), code.substring(0,2));
			if(domain.isEnableHeredity()){
				Integer[] ids = {domain.getId(), domain.getParent().getId()};
				criteria.addInExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_DOMAIN), ids);
				criteria.setSkipDomainFilter(true);
			}
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (GeoZone) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, no se autocompleta la provincia
		}
		return null;
	}
	
	public Domain getCurrentDomain(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_ID), DomainManager.getCurrentDomain());
			if( bean.getCount(criteria)<1 ) {
				String msg = "No hay datos de empresa definidos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				return (Domain) bean.getList(criteria).get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA. se devuelve nulo
		}
		return null;
	}

}
