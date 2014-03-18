package com.code.aon.ui.registry.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.registry.Relationship;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryRelationshipLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryRelationshipLinesController.class);
	
	public void onRegistryChanged(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(registryBean.getFieldName(IEntityAlias.REGISTRY_ID), event.getNewValue());
				Iterator<?> iter = registryBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Registry registry = (Registry)iter.next();
					((RegistryRelationship)this.getTo()).setRelatedRegistry(registry);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error loading registry with id=" + event.getNewValue(), e);
			}
		}
	}
	
	public void onRelationshipChanged(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean relationshipBean = BeanManager.getManagerBean(Relationship.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(relationshipBean.getFieldName(IEntityAlias.RELATIONSHIP_ID), event.getNewValue());
				Iterator<?> iter = relationshipBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Relationship relationship = (Relationship)iter.next();
					((RegistryRelationship)this.getTo()).setRelationship(relationship);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error loading relationship with id=" + event.getNewValue(), e);
			}
		}
	}
}
