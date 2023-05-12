package com.code.aon.geozone;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.GeoZoneDB;

@Entity
@Table(name="geozone")
@Heritable
public class GeoZone extends GeoZoneDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeoZone.class.getName());
	
	@Transient
	public GeoTree getGeoTree() {
		try {
			IManagerBean rMediaBean = BeanManager.getManagerBean(GeoTree.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_ID), getId());
			List<ITransferObject> list = rMediaBean.getList(criteria);
			if (! list.isEmpty() ) {
				return (GeoTree) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining GeoTree", e);
		}			
		return null;
	}
	
	@Transient
	public GeoZone getGeoZoneCountry() {
		GeoZone geozone = this;
		while ( geozone != null ) {
			GeoTree geoTree = geozone.getGeoTree();
			if ( geoTree != null ) {
				if ( geoTree.getParent() == null ) {
					return geozone;
				} else {
					geozone = geoTree.getParent();
				}
			} else {
				break;		
			}
		}
		return null;		
	}	
	
}