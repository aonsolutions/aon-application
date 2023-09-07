package com.code.aon.supplier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Where;

import com.code.aon.AonVersion;
import com.code.aon.account.IAccount;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.company.Company;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryItem;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.esferalia.aon.entity.master.SupplierDB;

@Entity
@Table(name="supplier")
public class Supplier extends SupplierDB implements IRegistry, ITaxInfo, IScopable, IAccount, ITariffable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Boolean surcharge;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryItem> items = new HashSet<RegistryItem>();
	private Set<RegistryAddInfo> addInfos = new HashSet<RegistryAddInfo>();

	public Supplier() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(SupplierStatus.ACTIVE);
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})	
	@Where(clause = "type=2")
	public Set<RegistryItem> getItems() {
		return items;
	}
	public void setItems(Set<RegistryItem> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAddInfo> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Set<RegistryAddInfo> addInfos) {
		this.addInfos = addInfos;
	}	

	@Transient
	public boolean isSurcharge() {
		if (surcharge == null) {
			surcharge = false;
			try {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				List<ITransferObject> companyList = companyBean.getList(null, 0, 1);
				if (companyList.size() > 0) {
					Company company = (Company)companyList.get(0);
					surcharge = company.isSurcharge();
				}
			} catch (ManagerBeanException ex) {}
		}
		return surcharge;
	}

	@Transient
	public boolean isVatFree() {
		return getTransaction() != InvoiceTransactionType.NATIONAL;
	}
	
	@Transient
	public boolean isRetentionFree() {
		return (getTransaction() == InvoiceTransactionType.INTRACOMMUNITY || getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY);
	}

}