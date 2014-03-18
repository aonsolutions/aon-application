package com.code.aon.ui.config.controller;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.RelationalType;
import com.code.aon.ql.ast.impl.ConstantExpressionImpl;
import com.code.aon.ql.ast.impl.RelationalExpressionImpl;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;

public class FiltrableBasicController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String suggestAlias;
	private String filter;
	
	public String getSuggestAlias() {
		return suggestAlias;
	}

	public void setSuggestAlias(String suggestAlias) {
		this.suggestAlias = suggestAlias;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	@Override
	public DataModel getModel() throws ManagerBeanException {
		this.clearCriteria();
		if (model == null || StringUtils.isBlank(getFilter())) {
			initializeModel();
		} else {
			Expression exp;
			try {
				exp = ExpressionUtilities.getExpression(getFilter(), getFieldName(suggestAlias));
				updateTextExpression(exp);
				getCriteria().addExpression(exp);
			} catch (ExpressionException e) {
				throw new ManagerBeanException(e.getMessage(), e);
			}
		}
		this.onSearch(null);
		return super.getModel();
	}
	
	private void updateTextExpression( Expression expression ) {
		RelationalExpressionImpl re = (RelationalExpressionImpl) expression;
		re.setType(RelationalType.LIKE);
		ConstantExpressionImpl ce = (ConstantExpressionImpl) re.getRightExpression();
		ce.setData( "%" + ce.getData().toString() + "%" );
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		setFilter(null);
		super.onEditSearch(event);
	}
	
}
