package com.code.aon.ui.form;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * LinesController is used to implement child Controllers.
 */
public class LinesController extends BasicController {

	/** The master controller. */
	private IController masterController;

	/** The master controller name. */
	private String masterControllerName;

	/** The property map. */
	private Map<String, String> propertyMap;

	/** cascade delete. */
	private boolean cascadeDelete;

	/** A list that contains the selected objects of the model. */
	private ArrayList<ITransferObject> checkList;

	/**
	 * Default constructor
	 */
	public LinesController() {
		this.checkList = new ArrayList<ITransferObject>();
	}

	/**
	 * Sets the master controller name.
	 * 
	 * @param masterControllerName
	 *            the master controller name
	 */
	public void setMasterControllerName(String masterControllerName) {
		this.masterControllerName = masterControllerName;
	}

	/**
	 * Sets the property map.
	 * 
	 * @param propertyMap
	 *            the property map
	 */
	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	/**
	 * Sets the cascade delete.
	 * 
	 * @param cascadeDelete
	 *            the cascade delete
	 */
	public void setCascadeDelete(boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}

	/**
	 * Checks if is master new.
	 * 
	 * @return true, if is master new
	 */
	private boolean isMasterNew() {
		return getMasterController().isNew();
	}

	/**
	 * Gets the master controller.
	 * 
	 * @return the master controller
	 */
	public IController getMasterController() {
		if (this.masterController == null) {
			this.masterController = FormUtil.getController(masterControllerName);
		}
		return this.masterController;
	}

	/**
	 * Initializes the model.
	 */
	@SuppressWarnings("unchecked")
	public void initModel() {
		if (isMasterNew()) {
			this.model = new ListDataModel(new ArrayList());
		} else {
			this.model = null;
		}
		resetTo();
	}

	/**
	 * Update join property.
	 * 
	 * @param masterTO
	 *            The Transfer Object of the master bean.
	 * @param masterProperty
	 *            the master property
	 * @param lineTo
	 *            the line to
	 * @param lineProperty
	 *            the line property
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	private void updateJoinProperty(ITransferObject masterTO, String masterProperty,
			ITransferObject lineTo, String lineProperty) throws ManagerBeanException {
		try {
			Object masterPropertyValue = PropertyUtils.getProperty(masterTO, masterProperty);
			PropertyUtils.setProperty(lineTo, lineProperty, masterPropertyValue);
		} catch (IllegalAccessException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/**
	 * Update join properties.
	 * 
	 * @param masterTO
	 *            The Transfer Object of the master bean.
	 * @param to
	 *            the to
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	private void updateJoinProperties(ITransferObject masterTO, ITransferObject to)
			throws ManagerBeanException {
		for (Map.Entry<String, String> entry : this.propertyMap.entrySet()) {
			updateJoinProperty(masterTO, entry.getKey(), to, entry.getValue());
		}
	}

	/**
	 * Save model.
	 * 
	 * @param masterTO
	 *            The Transfer Object of the master bean.
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void saveModel(ITransferObject masterTO) throws ManagerBeanException {
		if (this.masterController == null) {
			throw new AbortProcessingException("Unable to locate Master Controller!");
		}
		List list = (List) this.model.getWrappedData();
		Iterator i = list.iterator();
		while (i.hasNext()) {
			ITransferObject object = (ITransferObject) i.next();
			updateJoinProperties(masterTO, object);
			getManagerBean().insertOrUpdate(object);
		}
		this.model = null;
		super.initializeModel();
		getCheckList().clear();
	}

	/**
	 * Delete deletes the objects loaded in the model is
	 * <code>cascadeDelete</code> is true.
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public void deleteOrphans() throws ManagerBeanException {
		if (this.cascadeDelete) {
			if (this.masterController == null) {
				throw new AbortProcessingException("Unable to locate Master Controller!");
			}
			ITransferObject masterTo = this.masterController.getTo(); 
			if (masterTo == null) {
				throw new AbortProcessingException("No row selected in Master Controller!");
			}
			Serializable id = masterController.getManagerBean().getId(masterTo);
			if (id == null) {
				throw new AbortProcessingException("Found null id on row selected in Master Controller!");
			}
			Criteria criteria = getCriteria();
			if (criteria == null || criteria.isEmpty()) {
				throw new AbortProcessingException("Found null or empty criteria in Detail Controller!");
			}

			List<ITransferObject> list = getManagerBean().getList(criteria);
			for (ITransferObject to : list) {
				getManagerBean().remove(to);
			}
			
		}
	}

	/**
	 * Initializes the model.
	 */
	@Override
	public void initializeModel() {
		if (!isMasterNew()) {
			super.initializeModel();
		}
		getCheckList().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.BasicController#add()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ITransferObject add() throws ManagerBeanException {
		if (isMasterNew()) {
			List<ITransferObject> list = (List) this.model.getWrappedData();
			list.add(getTo());
			return getTo();
		}
		return super.add();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.code.aon.ui.form.BasicController#accept(javax.faces.event.ActionEvent
	 * )
	 */
	@Override
	public void accept(ActionEvent event) {
		if (this.masterController == null) {
			throw new AbortProcessingException("Unable to locate Master Controller!");
		}
		try {
			if (isNew()) {
				updateJoinProperties(getMasterController().getTo(), getTo());
			}
			super.accept(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.BasicController#remove()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void remove() throws ManagerBeanException {
		if (isMasterNew()) {
			List list = (List) this.model.getWrappedData();
			list.remove(getTo());
		} else {
			super.remove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.BasicController#update()
	 */
	@Override
	protected ITransferObject update() throws ManagerBeanException {
		if (isMasterNew()) {
			return getTo();
		}
		return super.update();
	}

	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 */
	public boolean getRowChecked() {
		ITransferObject to = (ITransferObject) model.getRowData();
		return checkList.contains(to);
	}

	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked
	 *            the row checked
	 */
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (!checkList.contains(to)) {
				checkList.add(to);
			}
		} else {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (checkList.contains(to)) {
				checkList.remove(to);
			}
		}
	}

	/**
	 * Gets the check list.
	 * 
	 * @return the check list
	 */
	protected ArrayList<ITransferObject> getCheckList() {
		return checkList;
	}

	/**
	 * Removes all the selected objects.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRemoveSelected(ActionEvent event) {
		try {
			for (ITransferObject to : checkList) {
				getManagerBean().remove(to);
			}
			onSearch(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

}