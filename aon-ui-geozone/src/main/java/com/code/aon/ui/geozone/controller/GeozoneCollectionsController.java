package com.code.aon.ui.geozone.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class GeozoneCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public GeoZone getGeoZone() {
		return null;
	}
	public void setGeoZone(GeoZone geoZone) {
	}
	
    public List<SelectItem> getGeoZones() throws ManagerBeanException {
    	List<SelectItem> geoZones = new LinkedList<SelectItem>();
        IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(geozoneBean.getFieldName(IEntityAlias.GEO_ZONE_NAME));
        Iterator<ITransferObject> iter = geozoneBean.getList(criteria).iterator();
        while (iter.hasNext()){
            GeoZone geozone = (GeoZone) iter.next();
            SelectItem item = new SelectItem(geozone, geozone.getName());
            geoZones.add( item );
        }
        return geoZones;
    }

	public List<SelectItem> getGeoTrees() throws ManagerBeanException {
		List<SelectItem> geoTrees = new LinkedList<SelectItem>();
		IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_PARENT));
		criteria.addOrder(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_NAME));
		Iterator<ITransferObject> iter = geoTreeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			GeoTree geoTree = (GeoTree)iter.next();
			String name = geoTree.getChild().getName();
			SelectItem[] selectItems = obtainGeoTreeChilds(geoTree.getChild().getId());
			SelectItemGroup itemGroup = new SelectItemGroup(name, name, false, selectItems);
			geoTrees.add(itemGroup);
		}
		return geoTrees;
	}

	public List<SelectItem> getSystemGeoTrees() throws ManagerBeanException {
		List<SelectItem> systemGeoTrees = new LinkedList<SelectItem>();
		IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_PARENT));
		criteria.addEqualExpression(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_SYSTEM), Boolean.TRUE);
		criteria.addOrder(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_NAME));
		Iterator<ITransferObject> iter = geoTreeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			GeoTree geoTree = (GeoTree)iter.next();
            SelectItem item = new SelectItem(geoTree.getChild().getName(), geoTree.getChild().getName());
			systemGeoTrees.add(item);
		}
		return systemGeoTrees;
	}

	private SelectItem[] obtainGeoTreeChilds(Integer parent) throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_PARENT_ID), parent);
		criteria.addOrder(geoTreeBean.getFieldName(IEntityAlias.GEO_TREE_CHILD_NAME));
		Iterator<ITransferObject> iter = geoTreeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			GeoTree geoTree = (GeoTree)iter.next();
			SelectItem item = new SelectItem(geoTree.getChild(),geoTree.getChild().getName());
			items.add(item);
		}
		return items.toArray(new SelectItem[items.size()]);
	}

}