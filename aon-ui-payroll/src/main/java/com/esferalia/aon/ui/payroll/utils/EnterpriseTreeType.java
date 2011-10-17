package com.esferalia.aon.ui.payroll.utils;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;

public enum EnterpriseTreeType {
	
	ENTERPRISE( EnterpriseTreeData.ENTERPRISE_ICON, "enterprise_formTree", "onSelectTreeEnterprise" ),
	WORKPLACE( EnterpriseTreeData.WORKPLACE_ICON, "enterpriseWorkPlace_formTree", "onSelectTreeWorkPlace" ),
	CONTRACT( EnterpriseTreeData.CONTRACT_ICON, "contract_formTree", "onSelectTreeContract" ),
	END_CONTRACT( EnterpriseTreeData.END_CONTRACT_ICON, "contract_formTree", "onSelectTreeContract" ),
	ACTIVITY( EnterpriseTreeData.ACTIVITY_ICON, "enterpriseActivity_formTree", null ),
	
	MAIN( EnterpriseTreeData.MAIN_ICON, "contractMainData_formTree", "onSelectTreeMainData" ),
	PAYMENT( EnterpriseTreeData.PAYMENT_ICON, "contractPayment_formTree", "onSelectTreePayments" ),
	DEDUCTION( EnterpriseTreeData.DEDUCTION_ICON, "contractDeduction_formTree", "onSelectTreeDeductions" ),
	BONUS( EnterpriseTreeData.BONUS_ICON, "contractBonus_formTree", "onSelectTreeBonus" ),
	EMBARGO( EnterpriseTreeData.EMBARGO_ICON, "contractEmbargo_formTree", "onSelectTreeEmbargos" ),
	SALARY( EnterpriseTreeData.SALARY_ICON, "salary_formTree", "onSelectTreeSalary" ),
	SALARY_DRAFT( EnterpriseTreeData.SALARY_DRAFT_ICON, "salaryDraft_formTree", "onSelectTreeSalaryDraft" ),
	DOCUMENT( EnterpriseTreeData.DOCUMENT_ICON, "contractDocument_formTree", "onSelectTreeDocuments" ),
	IRPF( EnterpriseTreeData.AEAT_ICON, "irpfResult_formTree", "onSelectTreeIrpf" ),
	;
	
	private String icon;
	
	private String action;
	
	private MethodExpression actionListener;
	
	EnterpriseTreeType( String icon, String action, String method ) {
		this.action = action;
		this.icon = calculateIcon(icon);
		if ( method != null ) {
			String expression = "#{enterpriseTree." + method + "}";
			this.actionListener = calculateActionListener(expression);
		}
	}

	private String calculateIcon( String icon ) {
		ResourceResolver resolver = (ResourceResolver) AonUtil.getRegisteredBean("aonResource");
		return resolver.getResolve().get(icon);		
	}

	private MethodExpression calculateActionListener( String expression ) {
		FacesContext ctx = FacesContext.getCurrentInstance(); 
        ExpressionFactory f = ctx.getApplication().getExpressionFactory();
        MethodExpression me = f.createMethodExpression(ctx.getELContext(), expression, null, FaceletUtil.ACTION_LISTENER_SIG );
        return me;
	}	
	
	public String getIcon() {
		return icon;
	}

	public String action() {
		return action;
	}

	public MethodExpression getActionListener() {
		return actionListener;
	}
	
}
