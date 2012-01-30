package com.code.aon.company.util;

import java.util.Iterator;
import java.util.List;

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

	private static GeoZone companyGeoZone;

	public static GeoZone getCompanyGeoZone() throws ManagerBeanException {
		if (companyGeoZone == null) {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator<?> iterator = companyBean.getList(null).iterator();
			if (iterator.hasNext()) {
				Company company = (Company)iterator.next();
	    		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
	    		Criteria criteria = new Criteria();
	    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
	    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
	    		Iterator<?> iter = registryAddressBean.getList(criteria).iterator();
	    		if (iter.hasNext()) {
	    			RegistryAddress registryAddress = (RegistryAddress)iter.next();
	    			companyGeoZone = registryAddress.getGeozone();
	    		}
			}
		}
		return companyGeoZone;
	}
	
	public Enterprise getActiveEnterprise() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		List<ITransferObject> list = companyBean.getList(null);
		if (list == null || list.size() < 1) {
			throw new IllegalStateException("No existe company!");
		}
		Company company = (Company) list.get(0);
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(enterpriseBean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_ID), company.getId());
		list = enterpriseBean.getList(criteria);
		if (list == null || list.size() < 1) {
			throw new IllegalStateException("No existe un enterprise vinculado a company!");
		}
		return (Enterprise) list.get(0);
	}
}
