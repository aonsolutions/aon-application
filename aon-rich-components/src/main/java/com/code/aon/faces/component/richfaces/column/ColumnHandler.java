package com.code.aon.faces.component.richfaces.column;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlColumn;
import org.richfaces.component.html.HtmlDataTable;
import org.richfaces.model.Ordering;
import org.richfaces.taglib.ColumnTagHandler;

import com.code.aon.faces.component.ComponentManager;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.dataTable.DataTableHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class ColumnHandler extends ColumnTagHandler implements IRichFacesTags {

	private static final String ALIAS = "alias";
	
	public ColumnHandler(ComponentConfig config) {
		super(config);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set.ignore(ALIAS);
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		ComponentManager.getInstance().onComponentCreated( ctx, c, parent );		
		TagAttribute aliasTag = getAttribute(ALIAS);
		if ( aliasTag != null ) {
			HtmlColumn column = (HtmlColumn) c;
			if ( column.isSortable() ) {
				String alias = aliasTag.getValue(ctx); 
				String sortBy = "#{'" + alias + "'}";
				UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), column, SORT_BY, sortBy);
				ExtendedPageDataModel model = DataTableHandler.getModel(parent);
				if ( model != null ) {
					HtmlDataTable table = (HtmlDataTable) parent;
					String modelExpression = table.getValueExpression("value").getExpressionString();
					String sortOrder = FaceletUtil.appendExpression( modelExpression, "sortOrder['" + alias + "']");
					ValueExpression ve = FaceletUtil.getValueExpression(ctx, sortOrder, Ordering.class);
					column.setValueExpression(SORT_ORDER, ve);
					model.setSortable(true);
				}
			}
		}
	}
	
}
