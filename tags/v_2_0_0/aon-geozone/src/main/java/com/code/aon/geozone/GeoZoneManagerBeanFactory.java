package com.code.aon.geozone;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.dao.IDAO;
import com.code.aon.geozone.dao.GeoZoneDAOFactory;

/**
 * ImanagerBean factory for each IDAO in this package
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-jun-2005 
 */
public class GeoZoneManagerBeanFactory {

    /**
     * Returns the IManagerBean for GeoZone DAO.
     * 
     * @return IManagerBean.
     */
	public static IManagerBean getGeoZoneManagerBean() {
		IDAO dao = GeoZoneDAOFactory.getInstance().getGeoZoneDAO();
		return new BasicManagerBean( dao );
	}
	
    /**
     * Returns the IManagerBean for Country DAO.
     * 
     * @return IManagerBean.
     */
	public static IManagerBean getCountryManagerBean() {
		IDAO dao = GeoZoneDAOFactory.getInstance().getCountryDAO();
		return new BasicManagerBean( dao );
	}
}
