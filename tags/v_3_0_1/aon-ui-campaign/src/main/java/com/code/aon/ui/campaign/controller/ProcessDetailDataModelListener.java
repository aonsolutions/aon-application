/**
 * 
 */
package com.code.aon.ui.campaign.controller;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class ProcessDetailDataModelListener extends LinesControllerListener implements
		DataModelListener {

	@Override
	public void rowSelected(DataModelEvent event) {
		if (event.getRowData() != null) {
			IController c = FormUtil.getController("processDetail");
			ProcessDetail selected = (ProcessDetail) c.getTo();
			if (selected == null) {
				ProcessDetail pd = (ProcessDetail) event.getRowData();
				IController controller = FormUtil.getController("processDetailTransition");	
				Criteria criteria = new Criteria();
				try {
					String a = controller
							.getFieldName(ICampaignAlias.PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID);
					criteria.addEqualExpression(a, pd.getId());
					controller.setCriteria(criteria);
					controller.initializeModel();
				} catch (ManagerBeanException e) {
					e.printStackTrace();
				}
			}
		}
	}
}