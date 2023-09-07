package com.code.aon.company;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.IScopable;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.EnterpriseDB;

@Entity
@Table(name="enterprise")
@PrimaryKeyJoinColumn(name="registry")
public class Enterprise extends EnterpriseDB implements IRegistry, IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

	@Transient
	public RecordData getEnterpriseRecordData() throws ManagerBeanException{
		IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(recordDataBean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), this.getRegistry().getId());
		Iterator<ITransferObject> iter = recordDataBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (RecordData)iter.next();
		}
		return null;
	}
	
	/**
	 * Obtains the address.
	 * 
	 * @return the registry address
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public RegistryAddress obtainAddress() throws ManagerBeanException{
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), this.getRegistry().getId());
		criteria.addOrder(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
		Iterator<ITransferObject> iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}
	
	
}
