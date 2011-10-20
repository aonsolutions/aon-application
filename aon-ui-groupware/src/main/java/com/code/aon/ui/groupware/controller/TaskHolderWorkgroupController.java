package com.code.aon.ui.groupware.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class TaskHolderWorkgroupController extends LinesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskHolderWorkgroupController.class);
	
	public List<SelectItem> getWorkgroups()  {
		List<SelectItem> workgroups = new LinkedList<SelectItem>();
		try {
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
			criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
			for (ITransferObject to : workGroupBean.getList(criteria)) {
				WorkGroup workGroup = (WorkGroup) to;
				List<?> groups = (List<?>) getModel().getWrappedData();
				boolean skip = false;
				for (int i = 0; i < groups.size(); i++) {
					TaskHolderWorkgroup current = (TaskHolderWorkgroup) getTo();
					TaskHolderWorkgroup thw = (TaskHolderWorkgroup) groups.get(i);
					if (workGroup.equals(thw.getWorkGroup()) && current != thw ) {
						skip = true;
						break;
					}
				}
				if (!skip) {
					SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
					workgroups.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar la lista de grupos";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
		} 
		return workgroups;
	}
	
}