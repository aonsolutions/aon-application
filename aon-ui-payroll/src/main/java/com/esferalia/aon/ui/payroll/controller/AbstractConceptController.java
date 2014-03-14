package com.esferalia.aon.ui.payroll.controller;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public abstract class AbstractConceptController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<String> systemDataVariables;
	
	public List<String> getSystemDataVariables() {
		if (systemDataVariables == null) {
			systemDataVariables = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				Date date = new Date();
				String alias = bean.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					SystemData sd = (SystemData) to;
					systemDataVariables.add(sd.getName());					
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return systemDataVariables;
	}

	public void setSystemDataVariables(List<String> systemDataVariables) {
		this.systemDataVariables = systemDataVariables;
	}

	public List<?> expressionContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		for (String systemDataVariable :getSystemDataVariables()){
			if (systemDataVariable.startsWith(filter)) {
				list.add(systemDataVariable);
			}
		}
		for (ContextVariable cv :ContextVariable.values() ){
			if (cv.getName().startsWith(filter)) {
				list.add(cv.getName());		
			}
		}
		Collections.sort(list);
		return list;
	}
	
	
}
