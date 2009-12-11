package com.code.aon.ui.project.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Activity;
import com.code.aon.project.Dossier;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ImportActivitiesController {

	private static final Logger LOGGER = Logger.getLogger(ImportActivitiesController.class
			.getName());

	private boolean importPanel;
	private Integer dossierId;
	private DataModel model;

	public boolean isImportPanel() {
		return importPanel;
	}

	public void setImportPanel(boolean importPanel) {
		this.importPanel = importPanel;
	}

	public Integer getDossierId() {
		return dossierId;
	}

	public void setDossierId(Integer dossierId) {
		this.dossierId = dossierId;
	}

	public DataModel getModel() {
		if (model == null) {
			setModel(new ListDataModel());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public void onShowImportPanel(ActionEvent event) {
		setDossierId(null);
		setModel(null);
		setImportPanel(true);
	}

	public void onCloseImportPanel(ActionEvent event) {
		setImportPanel(false);
	}

	@SuppressWarnings("unchecked")
	public void onImport(ActionEvent event) {
		int i = 0;
		try {
			List<CustomActivity> list = (List<CustomActivity>) getModel().getWrappedData();
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			for (CustomActivity ca : list) {
				if (ca.isSelected()) {
					Activity a = new Activity();
					a.setDossier(getDossier());
					a.setActivityType(ca.getActivity().getActivityType());
					a.setWorkgroup(ca.getActivity().getWorkgroup());
					i++;
					bean.insert(a);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error importing activities", e);
		} finally {
			FormUtil.getController("activity").onSearch(event);
			AonUtil.addInfoMessage("" + i + " activities imported");			
			setImportPanel(false);
		}
	}

	public List<SelectItem> getDossiers() {
		List<SelectItem> dossiers = new LinkedList<SelectItem>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			String identifier = bean.getFieldName(IProjectAlias.DOSSIER_ID);
			Expression exp = ExpressionUtilities.getNotEqualExpression(identifier, getDossier()
					.getId());
			criteria.addExpression(exp);
			criteria.addOrder(bean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
			Iterator<ITransferObject> iterator = bean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				StringBuilder sb = new StringBuilder(dossier.getNumber());
				sb.append(" (");
				sb.append(dossier.getCustomer().getRegistry().getAlias());
				sb.append(")");
				if (dossier.getStatus() == DossierStatus.INACTIVE) {
					sb.append(" (");
					sb.append(dossier.getStatus());
					sb.append(")");
				}
				SelectItem item = new SelectItem(dossier.getId(), sb.toString());
				dossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers", e);
		}
		return dossiers;
	}

	public void onChangeDossier(ActionEvent event) {
		List<CustomActivity> list = new LinkedList<CustomActivity>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID);
			criteria.addEqualExpression(alias, getDossierId());
			Iterator<ITransferObject> iterator = bean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Activity act = (Activity) iterator.next();
				CustomActivity ca = new CustomActivity();
				ca.setActivity(act);
				ca.setSelected( ca.isClickable() );
				list.add(ca);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers", e);
		}
		setModel(new ListDataModel(list));
	}

	private DossierController getDossierController() {
		return (DossierController) FormUtil.getController("dossier");
	}

	private Dossier getDossier() {
		return (Dossier) getDossierController().getTo();
	}

	public class CustomActivity {
		private boolean selected;
		private Activity activity;

		public boolean isClickable() {
			Integer a = null;
			if (this.activity.getActivityType().getDossierType() != null) {
				a = this.activity.getActivityType().getDossierType().getId();
			}
			Integer b = getDossier().getDossierType().getId();
			return (a== null || a.equals(b));
		}
		
		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

	}
}
