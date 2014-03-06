package com.code.aon.ui.groupware.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TaskHolderController extends RegistryController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskHolderController.class);
	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getUsers() {
		List<SelectItem> userItems = new LinkedList<SelectItem>();
		try {
			IManagerBean userBean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			
			String alias = userBean.getFieldName(IEntityAlias.USER_DOMAIN);
			addParentDomainExpression(criteria,alias);
			
			criteria.addEqualExpression(userBean.getFieldName(IEntityAlias.USER_ACTIVE),true);
			List<?> list = userBean.getList(criteria);
			List<User> users = (List<User>) list;
			for (User user : users) {
				criteria = new Criteria();
				criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_HOLDER_USER_ID),user.getId());
				if (!isNew()) {
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.TASK_HOLDER_ID),((TaskHolder) getTo()).getId());	
				}
				List<?> ths = getManagerBean().getList(criteria);
				if (ths == null || ths.size() == 0) {
					SelectItem item = new SelectItem(user,user.getName());
					userItems.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible recuperar los usuarios";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
		}
		return userItems;		 
	}
	private void addParentDomainExpression(Criteria criteria, String alias) {
		criteria.setSkipDomainFilter(true);
		Integer domainId = DomainManager.getCurrentDomain();
		Expression domainExpression = ExpressionUtilities.getEqualExpression(alias, domainId);
    	Integer parentDomainId = AdminUtil.getParentDomain(domainId);
    	if ( parentDomainId != null ) {
    		Expression parentDomainExpression = ExpressionUtilities.getEqualExpression(alias, parentDomainId);
    		domainExpression = ExpressionUtilities.getOrExpression(domainExpression, parentDomainExpression);	
    	}
    	criteria.addExpression(domainExpression);
	}

	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IEntityAlias.WORK_GROUP_DESCRIPTION));
		for (ITransferObject ito : workGroupBean.getList(criteria)) {
			WorkGroup workGroup = (WorkGroup)ito;
			SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}
	
}