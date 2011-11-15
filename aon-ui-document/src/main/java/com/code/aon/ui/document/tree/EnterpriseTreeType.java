package com.code.aon.ui.document.tree;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;

public enum EnterpriseTreeType {
	
	ENTERPRISE( EnterpriseTreeData.ENTERPRISE_ICON, "enterprise_formTree", null ),
	CATEGORY( EnterpriseTreeData.CATEGORY_ICON, "alfrescoCategory_formTree", "onSelectTreeCategory" ),
	DOCUMENT( EnterpriseTreeData.DOCUMENT_ICON, "enterpriseDocument_formTree", "onSelectTreeDocument" ),
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
