package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;

public class AgreementLevelController extends LinesController {
	
	private String[] categories;
	private boolean showCategoryWindow;
	private List<ITransferObject> categoryList;
	private DataModel categoryModel;
	private AgreementLevel selectedLevel;

	
	public AgreementLevel getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(AgreementLevel selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public List<ITransferObject> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<ITransferObject> categoryList) {
		this.categoryList = categoryList;
	}

	public DataModel getCategoryModel() {
		return categoryModel;
	}

	public void setCategoryModel(DataModel categoryModel) {
		this.categoryModel = categoryModel;
	}

	public boolean isShowCategoryWindow() {
		return showCategoryWindow;
	}

	public void setShowCategoryWindow(boolean showCategoryWindow) {
		this.showCategoryWindow = showCategoryWindow;
	}

	public String getCategories() {
		AgreementLevel level = null;
		try {
			if(this.getModel().isRowAvailable()){
				level = (AgreementLevel) this.getModel().getRowData();
				String categories = "";
				refreshCategoryList(level);
				for(ITransferObject to: getCategoryList()){
					AgreementLevelCategory category = (AgreementLevelCategory) to;
					categories += category.getDescription();
					categories += ", ";
				}
				if(categories.endsWith(", ")){
					categories = categories.substring(0, categories.length()-2);
				}					
				return categories;
			} else if(this.isNew()){
				return null;
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido obtener las categorias del nivel " + level.getDescription());
		}
		return null;
	}

	public void setCategories(String categories) {
		this.categories = StringUtils.split(categories, ",");
	}
	
	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		if(categories!=null && categories.length>0){
			try {
				setSelectedLevel((AgreementLevel) this.getTo());
				saveCategories(categories);
				categories = null;
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido guardar las categorias del nivel");
			}
		}
	}
	
	private void refreshCategoryList(AgreementLevel level) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_ID), level.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_CATEGORY_DESCRIPTION));
		setCategoryList(bean.getList(criteria));
	}
	
	public void onEditCategoryList(ActionEvent event){
		try {
			if(this.getModel().isRowAvailable()){
				setSelectedLevel((AgreementLevel) this.getModel().getRowData());
				refreshCategoryList(getSelectedLevel());
				categoryModel = new ListDataModel(getCategoryList());
			} 
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido obtener las categorias del nivel " + getSelectedLevel().getDescription());
		}
		setShowCategoryWindow(true);
	}
	
	public void onSaveCategories(ActionEvent event) throws ManagerBeanException{
		if (getCategoryModel() != null && getSelectedLevel() != null) {
			List<ITransferObject> list = (List<ITransferObject>) getCategoryModel().getWrappedData();
			saveCategories(list);
		}
	}
	
	public void onAddCategory(ActionEvent event){
		List<ITransferObject> list = (List<ITransferObject>) getCategoryModel().getWrappedData();
		list.add(new AgreementLevelCategory());
	}
	
	public void onRemoveCategory(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
		AgreementLevelCategory category = (AgreementLevelCategory) getCategoryModel().getRowData();
		if(category!= null && category.getId()!=null){
			getCategoryList().remove(category);
			bean.remove(category);
			refreshCategoryList(getSelectedLevel());
			categoryModel = new ListDataModel(getCategoryList());
		}
	}
	
	private void saveCategories(List<ITransferObject> list) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
		for(ITransferObject to: list){
			AgreementLevelCategory category = (AgreementLevelCategory) to;
			if(StringUtils.isNotBlank(category.getDescription())){
				category.setLevel(getSelectedLevel());
				bean.insertOrUpdate(category);
			}
		}
	}
	
	private void saveCategories(String[] categories) throws ManagerBeanException {
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		for(String description: categories){
			AgreementLevelCategory category = new AgreementLevelCategory();;
			category.setLevel(getSelectedLevel());
			category.setDescription(description);
			list.add(category);
		}
		saveCategories(list);
	}
	
	
}
