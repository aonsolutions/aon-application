package com.esferalia.aon.pms.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class ReservationUtils {

	public Hotel obtainHotel(String hotelCode) throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_CODE), hotelCode);
		return (Hotel)hotelBean.getList(criteria).get(0);
	}

	public Date obtainCreationDate(String date, String time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd+hh:mm:ss");
			return dateFormat.parse(date + "+" + time);
		} catch (ParseException ex) {
			return new Date();
		}
	}

	public Customer obtainAgency(ProfileInfo agencyInfo) throws ManagerBeanException {
		String alias = agencyInfo.getUniqueID().getID();
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ALIAS), alias);
		Customer agency = (Customer)customerBean.getList(criteria).get(0);
		if (agency == null) {
			Registry registry = new Registry();
			registry.setAlias(alias);
			registry.setName(agencyInfo.getProfile().getCompanyInfo().getCompanyNameArray(0).getCompanyShortName());
			registry.setType(RegistryType.LEGAL);
			registry.setSecurityLevel(SecurityLevel.OFFICIAL);

			agency.setRegistry(registry);
		}
		return agency;
	}

}
