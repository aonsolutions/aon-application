package com.code.aon.ui.groupware.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.CampaignType;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignSearchControllerListener extends ControllerSearchListener {

	private boolean statusPending;
	private boolean statusInProgress;
	private boolean statusFinished;
	private boolean statusDeleted;
	
	public boolean isStatusPending() {
		return statusPending;
	}
	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusInProgress() {
		return statusInProgress;
	}
	public void setStatusInProgress(boolean statusInProgress) {
		this.statusInProgress = statusInProgress;
	}

	public boolean isStatusFinished() {
		return statusFinished;
	}
	public void setStatusFinished(boolean statusFinished) {
		this.statusFinished = statusFinished;
	}

	public boolean isStatusDeleted() {
		return statusDeleted;
	}
	public void setStatusDeleted(boolean statusDeleted) {
		this.statusDeleted = statusDeleted;
	}
	
	public Process getProcess() {
		return null;
	}
	public void setProcess( Process process ) {
	}
	
	public CampaignType getCampaignType() {
		return null;
	}
	public void setCampaignType( CampaignType campaignType) {
	}

	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setStatusPending(true);
		setStatusInProgress(true);
		setStatusFinished(false);
		setStatusDeleted(false);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		if (isStatusDeleted() || isStatusFinished() || isStatusInProgress() || isStatusPending()) {
			String statusAlias = getFieldName(IEntityAlias.CAMPAIGN_STATUS);
			// Hay que realizar una expression OR con los valores
			// seleccionados. Como hay
			// cuatro valores de status creamos un array con esas
			// dimensiones y asignamos
			// las expresiones correspondientes al array.
			Expression[] exps = { null, null, null, null };
			int count = 0;
			int inCaseCount1 = -1;
			if (isStatusDeleted()) {
				exps[0] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.DELETED);
				count++;
				inCaseCount1 = 0;
			}
			if (isStatusFinished()) {
				exps[1] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.FINISHED);
				count++;
				inCaseCount1 = 1;
			}
			if (isStatusInProgress()) {
				exps[2] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.IN_PROGRESS);
				count++;
				inCaseCount1 = 2;
			}
			if (isStatusPending()) {
				exps[3] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.PENDING);
				count++;
				inCaseCount1 = 3;
			}
			addOrExpression(criteria,exps,count,inCaseCount1);
		}
	}
	
	private void addOrExpression(Criteria criteria, Expression[] exps,int count, int inCaseCount1) {
		Expression expToAdd = null;
		if (count == 1) {
			expToAdd = exps[inCaseCount1];
		} else {
			// Si count > 1 hay que hacer una OR Expression
			boolean ready = false;
			for (int i = 0; i < exps.length; i++) {
				if (exps[i] != null) {
					if (!ready) {
						expToAdd = exps[i];
						ready = true;
					} else {
						expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exps[i]);
					}
				}
			}
		}
		criteria.addExpression(expToAdd);
	}	
	
}