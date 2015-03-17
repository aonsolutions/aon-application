package com.code.aon.ui.product.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ui.util.AonUtil;

public class ItemTagPrintController extends ItemController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemTagPrintController.class);
	
	private Integer startPosition; 
	
	private Integer tagRepeatCountAll;
	
	private String freeTextAll;
	
	private Map<Integer, Integer> tagRepeatCount;
	
	
	public Integer getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(Integer startPosition) {
		this.startPosition = startPosition;
	}
	
	public Map<Integer, Integer> getTagRepeatCount() {
		return tagRepeatCount;
	}

	public void setTagRepeatCount(Map<Integer, Integer> tagRepeatCount) {
		this.tagRepeatCount = tagRepeatCount;
	}

	public Integer getTagRepeatCountAll() {
		return tagRepeatCountAll;
	}

	public void setTagRepeatCountAll(Integer tagRepeatCountAll) {
		this.tagRepeatCountAll = tagRepeatCountAll;
	}

	public String getFreeTextAll() {
		return freeTextAll;
	}

	public void setFreeTextAll(String freeTextAll) {
		this.freeTextAll = freeTextAll;
	}
	
	public Integer getCheckedItems(){
		return super.getCheckList().size();
	}

	private <T> void loadMapValue(Map<Integer, T> map, T value){
		Item item;
		try {
			item = (Item) getModel().getRowData();
			map.put(item.getId(), value);
		} catch (ManagerBeanException e) {
			LOGGER.error("No se ha podido seleccionar el tipo de pago");
		}
	}
	
	private void removeMapValue(Map<Integer, ?> map){
		Item item;
		try {
			item = (Item) getModel().getRowData();
			if(map.containsKey(item.getId())){
				map.remove(item.getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("No se ha podido seleccionar el tipo de pago");
		}
	}
	public Integer getTagRepeatCountChanged() {
		if(model.isRowAvailable()){
			Item to = (Item) model.getRowData();
			return tagRepeatCount.get(to.getId());
		}
		return null;
	}
	
	public void setTagRepeatCountChanged(Integer tagRepeatValue) {
		Item to = (Item) model.getRowData();
		if (tagRepeatValue!=null && tagRepeatValue>0) {
			tagRepeatCount.put(to.getId(), tagRepeatValue);
		} else {
			tagRepeatCount.remove(to.getId());
		}
	}
	
	@Override
	public boolean getRowChecked() throws ManagerBeanException {
		if(model.isRowAvailable()){
			return super.getRowChecked();
		}
		return false;
	}
	
	@Override
	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		if (rowChecked) {
			loadMapValue(tagRepeatCount, 1);
		} else {
			removeMapValue(tagRepeatCount);
		}
		super.setRowChecked(rowChecked);
	}
	
	@Override
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		super.checkAll(event);
		for(Serializable o: super.getCheckList()){
			Integer key = (Integer) o;
			tagRepeatCount.put(key, 1);
		}
	}
	
	@Override
	public void checkNone(ActionEvent event) {
		tagRepeatCount = new HashMap<Integer, Integer>();
		super.checkNone(event);
	}
	
	public void onInit(ActionEvent event){
		checkNone(event);
		setStartPosition(1);
		setFreeTextAll("");
		setTagRepeatCountAll(1);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		onInit(event);
		super.onSearch(event);
	}
	
	public void completeAllValues(ActionEvent event) {
		for (Serializable o : this.getCheckList()) {
			Integer itemId = (Integer)o;
			tagRepeatCount.put(itemId, tagRepeatCountAll);
		}
	}
	
	///////////////////////////////////////
	// JASPER PRINT
	///////////////////////////////////////

	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Item.class);
			List<ITransferObject> list = new LinkedList<ITransferObject>();
			for(int i=1; i<startPosition;i++){
				list.add(bean.createNewTo());
			}
			for(Serializable o: this.getCheckList()){
				Integer itemId = (Integer) o;
				for(int i=0; i<tagRepeatCount.get(itemId);i++){
					list.add(bean.get(itemId));
				}
			}
			return list;
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getCollection ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
}