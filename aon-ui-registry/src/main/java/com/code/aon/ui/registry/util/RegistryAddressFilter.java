package com.code.aon.ui.registry.util;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.entity.IEntityAlias;

/*
		<ui:include src="/com/code/aon/ui/registry/facelet/raddress/filterModal.xhtml" >
			<ui:param name="controller" value="#{controller}" />
		</ui:include>	
*/
/*
		<aon:commandButton id="showAddressesFilterWindow"
			value="&#160;" title="#{bundle.aon_filter} #{bundle.aon_address}"
			styleClass="aon-icon-commandButton aon-lookupButton aon-margin-left"
			rendered="#{controller.addresses.size() > controller.addressesFilter.quantityForEnable}" >
			<f:setPropertyActionListener
				target="#{controller.addressesFilter.showAddressFilterWindow}" value="#{true}" />
		</aon:commandButton>
 */

public class RegistryAddressFilter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final int QUANTITY_FOR_ENABLE_FILTER = 1;
	
	private boolean showAddressFilterWindow;
	private Registry registry;
	private DataModel model;
	private String value;
	
	public RegistryAddressFilter(Registry registry) {
		this.registry = registry;
	}
	
	public int getQuantityForEnable() {
		return QUANTITY_FOR_ENABLE_FILTER;
	}
	
	public boolean isShowAddressFilterWindow() {
		return showAddressFilterWindow;
	}

	public void setShowAddressFilterWindow(boolean showAddressFilterWindow) {
		this.showAddressFilterWindow = showAddressFilterWindow;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataModel getModel() throws ManagerBeanException {
		if(model==null)
			model = new SerializableListDataModel(getAddresses(registry.getId()));
		return model;
	}
	
	public RegistryAddress getSelectedAddress() {
		if(model.isRowAvailable())
			return (RegistryAddress) model.getRowData();
		return null;
	}
	
	private List<ITransferObject> getAddresses(Integer registryId) throws ManagerBeanException {
		if (registryId != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
			criteria.addOrder(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
			return rAddressBean.getList(criteria);
		}
		return null;
	}
	
	public void filterModel(ActionEvent event) throws ManagerBeanException {
		Integer id = registry.getId();
		if (id != null) {
			List<ITransferObject> list = getAddresses(id);
			if(StringUtils.isNotBlank(value)) {
				list = list.stream().map(to->(RegistryAddress)to)
					.filter(ra->StringUtils.containsIgnoreCase(ra.getAddress(), value)
							|| StringUtils.containsIgnoreCase(ra.getAddress2(), value)
							|| StringUtils.containsIgnoreCase(ra.getAddress3(), value)
							|| StringUtils.containsIgnoreCase(ra.getZip(), value)
							|| StringUtils.containsIgnoreCase(ra.getCity(), value))
					.collect(Collectors.toList());
			}
			model = new SerializableListDataModel(list);
		}
	}
	
	public void onSelectAddress(ActionEvent event) {
		if(model.isRowAvailable())
//			this.getInvoice().setRegistryAddress((RegistryAddress) addressesModel.getRowData());
		// TODO
		System.out.println(((RegistryAddress) model.getRowData()).getFullAddress());
	}

}
