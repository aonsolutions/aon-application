package com.code.aon.geozone.dao.hibernate;

import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.hibernate.HibernateDAO;
import com.code.aon.geozone.Country;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.GeoZoneDAOFactory;

/**
 * <code>IGeoZoneDAO</code> factory.
 * 
 * @author Consulting & Development. Aimar Tellitu - 20-jun-2005
 * @since 1.0
 *  
 */
public class GeoZoneHibernateDAOFactory extends GeoZoneDAOFactory {

	/**
     * Returns the Data Access Object needed to get the GeoZone.
	 * 
	 * @return IBasicDAO Required DAO.
	 */
	public IDAO getGeoZoneDAO() {
		return new HibernateDAO( GeoZone.class );
	}

	/**
     * Returns the Data Access Object needed to get the Country.
	 * 
	 * @return IBasicDAO Required DAO.
	 */
	public IDAO getCountryDAO() {
		return new HibernateDAO( Country.class );
	}

}