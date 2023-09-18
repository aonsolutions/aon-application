package com.code.aon.faces.component.richfaces.lookup.button.popup;

import static com.code.aon.faces.controller.IRichConstants.LOOKUP_MODAL_PANEL_SET;

import java.util.HashSet;
import java.util.Set;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonHandler;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonType;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.controller.RichLookupBean;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonPopupHandler extends TagHandler implements ILookupConstants, IRichFacesTags {

	private static final String LOOKUP_ID = "lookupId";
	
	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/lookup/";

	private static final String TEMPLATE = TEMPLATE_PATH + "panelPopup.xhtml";
	
	private static final String ON_LOOKUP_SHOW = "onLookupShow";
	
	private static final String SELECT_FIRST_ROW = "selectFirstRow";
	
	private static final String FOCUS_FIRST_INPUT = "focusFirstInput";

	private TagAttribute lookup;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public LookupButtonPopupHandler(TagConfig config) {
		super(config);
		lookup = getRequiredAttribute(LOOKUP);
	}

	@SuppressWarnings("unchecked")
	private boolean isInsertTemplate(FaceletContext ctx, UIComponent parent) {
		boolean insert = true;
		RichLookupBean bean = (RichLookupBean) lookup.getObject(ctx, RichLookupBean.class);
		String lookupName = bean.getBeanName();
		Set<String> set = (Set<String>) FaceletUtil.getRequestValue(ctx, LOOKUP_MODAL_PANEL_SET); 
		if (set == null) {
			set = new HashSet<String>();
			FaceletUtil.putRequestValue(ctx, LOOKUP_MODAL_PANEL_SET, set);
		} else {
			insert = ! set.contains(lookupName);
		}
		if (insert) {
			set.add(lookupName);
		}
		return insert;
	}
	
	public LookupButtonType getType(FaceletContext ctx) {
		String expr = FaceletUtil.appendExpression(lookup.getValue(), "selectedPanel" );
		ValueExpression panel = ctx.getExpressionFactory().createValueExpression(
				ctx, expr, String.class);		
		String value = (String) panel.getValue(ctx);
		return StringUtils.isEmpty(value) ? null : LookupButtonType.get(value);
	}	
	
	private ValueExpression getSelectFirstRow( FaceletContext ctx ) {
		String lookupName = LookupButtonHandler.getLookupBeanName(ctx, lookup);
		String expression = "aonSelectFirstRow('" + lookupName + "Data')";
		return FaceletUtil.getValueExpression(ctx, expression, Object.class);
	}

	private ValueExpression getFocusFirstInput( FaceletContext ctx ) {
		String lookupName = LookupButtonHandler.getLookupBeanName(ctx, lookup);
		String expression = "aonFocusFirstInput('" + lookupName + "Form')";
		return FaceletUtil.getValueExpression(ctx, expression, Object.class);		
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(LOOKUP, lookup.getValueExpression(ctx, Object.class));
		String panelId = LookupButtonHandler.getModalPanelId(ctx, lookup);
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, panelId, String.class);
		newMapper.setVariable(LOOKUP_ID, id);
		ValueExpression selectFirstRow = getSelectFirstRow(ctx);
		newMapper.setVariable(SELECT_FIRST_ROW, selectFirstRow);
		ValueExpression focusFirstInput = getFocusFirstInput(ctx);
		newMapper.setVariable(FOCUS_FIRST_INPUT, focusFirstInput);
		LookupButtonType type = getType(ctx);
		if ( type != null ) {
			if ( getType(ctx) == LookupButtonType.LIST ) {
				newMapper.setVariable(ON_LOOKUP_SHOW, selectFirstRow);
			} else {
				newMapper.setVariable(ON_LOOKUP_SHOW, focusFirstInput);
			}			
		}		
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isInsertTemplate(ctx, parent) ) {
			insertTemplate( ctx, parent );
		}
	}

}