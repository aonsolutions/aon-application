package com.code.aon.ui.geozone.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.geozone</code>.
 * 
 */
public class GeozoneCollectionsController {

	/** The geoZones list. */
	private List<SelectItem> geoZones;
	
	/** The geoTrees list. */
	private List<SelectItem> geoTrees;

    /**
     * Gets the geoZones.
     * 
     * @return the geoZones
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public List<SelectItem> getGeoZones() throws ManagerBeanException {
        if (geoZones == null) {
            geoZones = new LinkedList<SelectItem>();
            IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
            Criteria criteria = new Criteria();
            criteria.addOrder(geozoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_NAME));
            Iterator<ITransferObject> iter = geozoneBean.getList(criteria).iterator();
            while (iter.hasNext()){
                GeoZone geozone = (GeoZone) iter.next();
                SelectItem item = new SelectItem(geozone.getId(), geozone.getName());
                geoZones.add( item );
            }
        }
        return geoZones;
    }

	@SuppressWarnings("unchecked")
	public List<SelectItem> getGeoTrees() throws ManagerBeanException {
		if (geoTrees == null) {
			geoTrees = new LinkedList<SelectItem>();
			IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
			Criteria criteria = new Criteria();
			criteria.addNullExpression(geoTreeBean.getFieldName(IGeoZoneAlias.GEO_TREE_PARENT));
			criteria.addOrder(geoTreeBean.getFieldName(IGeoZoneAlias.GEO_TREE_CHILD_NAME));
			Iterator<ITransferObject> iter = geoTreeBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				GeoTree geoTree = (GeoTree)iter.next();
				SelectItemGroup itemGroup = new SelectItemGroup(geoTree.getChild().getName(),geoTree.getChild().getName(),true,obtainGeoTreeChilds(geoTree.getChild().getId()));
				geoTrees.add(itemGroup);
			}
		}
		return geoTrees;
	}

	@SuppressWarnings("unchecked")
	private SelectItem[] obtainGeoTreeChilds(Integer parent) throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean geoTreeBean = BeanManager.getManagerBean(GeoTree.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(geoTreeBean.getFieldName(IGeoZoneAlias.GEO_TREE_PARENT_ID), parent);
		criteria.addOrder(geoTreeBean.getFieldName(IGeoZoneAlias.GEO_TREE_CHILD_NAME));
		Iterator iter = geoTreeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			GeoTree geoTree = (GeoTree)iter.next();
			SelectItem item = new SelectItem(geoTree.getChild().getId(),geoTree.getChild().getName());
			items.add(item);
		}
		return items.toArray(new SelectItem[items.size()]);
	}

}