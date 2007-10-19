package com.code.aon.geozone.dao;

import com.code.aon.common.dao.DAOFactory;
import com.code.aon.common.dao.IDAO;
import com.code.aon.geozone.IGeoZoneJNDIConstants;

/**
 * <code>IGeoZoneDAO</code> factory.
 * 
 * @author Consulting & Development. Eugenio Castellano - 04-mar-2005
 * @since 1.0
 *  
 */
public abstract class GeoZoneDAOFactory {

    /**
     * The DAO factory for geozone package.
     */
	private static GeoZoneDAOFactory DAO_FACTORY;

    /**
     * Singleton constructor.
     */
	public static GeoZoneDAOFactory getInstance() {
		if (DAO_FACTORY == null) {
			DAO_FACTORY = (GeoZoneDAOFactory) DAOFactory
					.getDAOFactory(IGeoZoneJNDIConstants.AON_GEOZONE_DAO_FACTORY_CLASS);
		}
		return DAO_FACTORY;
	}

	/**
     * Returns the Data Access Object needed to get the GeoZone.
	 * 
	 * @return IBasicDAO Required DAO.
	 */
	public abstract IDAO getGeoZoneDAO();

	/**
     * Returns the Data Access Object needed to get the Country.
	 * 
	 * @return IBasicDAO Required DAO.
	 */
	public abstract IDAO getCountryDAO();

}