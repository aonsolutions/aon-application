package com.code.aon.company.util;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyUtil {

	private GeoZone companyGeoZone;

	public GeoZone getCompanyGeoZone() throws ManagerBeanException {
		if (companyGeoZone == null) {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			for (ITransferObject ito : companyBean.getList(null)) {
				Company company = (Company)ito;
	    		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
	    		Criteria criteria = new Criteria();
	    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
	    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
	    		for (ITransferObject itr : registryAddressBean.getList(criteria)) {
	    			companyGeoZone = ((RegistryAddress)itr).getGeozone();
	    			break;
	    		}
			}
		}
		return companyGeoZone;
	}

}
