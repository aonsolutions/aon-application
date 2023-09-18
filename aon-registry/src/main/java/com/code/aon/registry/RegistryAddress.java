package com.code.aon.registry;


import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.RegistryAddressDB;

@Entity
@Table(name="raddress")
@Heritable
public class RegistryAddress extends RegistryAddressDB implements IAddress {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
    public String getProvince() {
  		return (getGeozone() != null && getGeozone().getId() != null) ? getGeozone().getName() : null;
    }
    public void setProvince(String province) {
    }

	@Transient
    public String getFullAddress() {
    	StringBuffer buf = new StringBuffer();
    	if(getStreetType()!=null && getStreetType()!=StreetType.XX) {
    		buf.append(getStreetType());
    		buf.append(". ");
    	}
    	buf.append(StringUtils.isEmpty(getAddress())? "":getAddress());
    	buf.append(StringUtils.isEmpty(getNumber())?"":" ");
    	buf.append(StringUtils.isEmpty(getNumber())?"":getNumber());
    	buf.append(StringUtils.isEmpty(getAddress2())?"":", ");
    	buf.append(StringUtils.isEmpty(getAddress2())?"":getAddress2());
    	buf.append(StringUtils.isEmpty(getAddress3())?"":" (");
    	buf.append(StringUtils.isEmpty(getAddress3())?"":getAddress3());
    	buf.append(StringUtils.isEmpty(getAddress3())?"":")");
    	return buf.toString();
    }

	@Transient
    public String getShortAddress() {
  		return StringUtils.isEmpty(getAlias())?StringUtils.abbreviate(getFullAddress(), 25): getAlias();
    }

	@Transient
    public String getLocation() {
    	StringBuffer buf = new StringBuffer();
    	buf.append((getCity()!=null) ? getCity()+" " : "");
    	buf.append((getGeozone()!=null && getGeozone().getId()!=null) ? "("+getGeozone().getName()+")" : "");
    	buf.append((getGeozone()==null || getGeozone().getId()==null) ? "("+getProvince()+")" : "");
    	return buf.toString();
    }

    @Transient
    public boolean isMainAddress() {
  		return getAddressType() == AddressType.MAIN;
    }
    
    @Transient
    public void loadGeoZoneByZip() {
		if(StringUtils.isNotBlank(this.getZip())){
			try {
				IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_CODE), StringUtils.substring(this.getZip(), 0, 2));
				List<ITransferObject> list = bean.getList(criteria);
				if( !list.isEmpty() ){
					this.setGeozone((GeoZone) list.get(0));
				}
			} catch (ManagerBeanException e) {
				// NADA, no se autocompleta la provincia
			}
		} else{
			this.setGeozone(null);
		}
	}
    
}
