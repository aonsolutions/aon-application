package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.cms.Activity;
import com.code.aon.cms.Company;
import com.code.aon.cms.CompanyActivity;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class CompanyController extends BasicController implements ICMSConstants {
	
	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	private DataModel currentActivitiesModel;

	private DataModel activityModel;

	public DataModel getCurrentActivitiesModel() {
		return currentActivitiesModel;
	}

	public void setCurrentActivitiesModel(DataModel currentActivitiesModel) {
		this.currentActivitiesModel = currentActivitiesModel;
	}

	public DataModel getActivityModel() {
		return activityModel;
	}

	public void setActivityModel(DataModel activityModel) {
		this.activityModel = activityModel;
	}

	private List avoidable;
	
	private void loadAllActivities(){
		List<Activity> activityList = new LinkedList<Activity>();
		try {
			IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(activityBean.getFieldName(ICMSAlias.ACTIVITY_ALIAS));
			for(ITransferObject to : activityBean.getList(null)){
				if (!avoidable.contains(((Activity)to).getId()))
					activityList.add((Activity)to);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		activityModel = new ListDataModel(activityList);
	}

	private void loadCurrentActivities(){
		avoidable = new ArrayList<Integer>();
		List<CompanyActivity> activityList = new LinkedList<CompanyActivity>();
		try {
			Company company = (Company)getTo();
			IManagerBean companyActivityBean = BeanManager.getManagerBean(CompanyActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(companyActivityBean.getFieldName(ICMSAlias.COMPANY_ACTIVITY_COMPANY_ID), company.getId());
			for(ITransferObject to : companyActivityBean.getList(criteria)){
				activityList.add((CompanyActivity)to);
				avoidable.add(((CompanyActivity)to).getActivity().getId());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		currentActivitiesModel = new ListDataModel(activityList);
	}
	
	public void onLoadActivities(ActionEvent event) {
		loadCurrentActivities();
		loadAllActivities();
	}

	public void addCurrentActivity(ActionEvent event) {
		Activity activity = (Activity)activityModel.getRowData();
		try{
			IManagerBean companyActivityBean = BeanManager.getManagerBean(CompanyActivity.class);
			CompanyActivity companyActivity = new CompanyActivity();
			companyActivity.setCompany((Company)getTo());
			companyActivity.setActivity(activity);
			companyActivityBean.insert(companyActivity);
			onLoadActivities(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removeCurrentActivity(ActionEvent event) {
		try{
			IManagerBean companyActivityBean = BeanManager.getManagerBean(CompanyActivity.class);
			companyActivityBean.remove((CompanyActivity)currentActivitiesModel.getRowData());
			onLoadActivities(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onDelImage(ActionEvent event) {
		Company current = (Company)getTo();
		current.setLogo(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Company current = (Company)getTo();
		current.setLogo(image);
	}

}