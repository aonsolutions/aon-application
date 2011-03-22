package com.esferalia.aon.ui.payroll.controller.agreement;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public enum AgreementTreeType {
	
	AGREEMENT( AgreementTreeData.AGREEMENT_ICON, IPayrollConstants.AGREEMENT_FORM, "onSelectAgreement" ),
	AGREEMENT_LEVEL( AgreementTreeData.AGREEMENT_LEVEL_ICON, IPayrollConstants.AGREEMENT_LEVEL_FORM, "onSelectAgreementLevel" ),
	AGREEMENT_LEVEL_CATEGORY( AgreementTreeData.AGREEMENT_LEVEL_CATEGORY_ICON, null , null ),
	AGREEMENT_LEVEL_PAYMENT( AgreementTreeData.AGREEMENT_LEVEL_PAYMENT_ICON, null , null  );
	
	private String icon;
	
	private String action;
	
	private MethodExpression actionListener;
	
	private AgreementTreeType( String icon, String action, String method ) {
		this.action = action;
		this.icon = calculateIcon(icon);
		if ( method != null ) {
			String expression = "#{agreementTree." + method + "}";
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
