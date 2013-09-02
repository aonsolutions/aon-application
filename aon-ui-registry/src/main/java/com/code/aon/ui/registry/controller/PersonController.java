package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.registry.controller.event.PersonFormListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PersonController extends RegistryController {

	public void onChangeSSNumber(ActionEvent event){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME); 
		Person person = (Person) this.getTo();
		if(person.isValidSSNumber()){
			personForm.getMainAddress().setGeozone(getGeoZoneByValue(person.getSocialSecurityNumber()));
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
	
	public List<SelectItem> getMunicipalities(){
		PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME);
		return personForm.getMunicipalities();
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
