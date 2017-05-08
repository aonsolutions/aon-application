package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Behaviour.BEHAVIOUR_KEYS_MAP;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Mod2002016Object.IMod200ChangeListener;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class PageAbs extends ResizeComposite {

	interface DeleteButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-delete\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			mod200Object.mathExpression(expression,callback);
		}
	};
	
	static class DeleteButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static DeleteButtonTemplate template;

		protected DeleteButtonSafeHtmlTemplates() {
			template = GWT.create(DeleteButtonTemplate.class);
		}
		
		@Override
		public SafeHtml render(String object) {
			return template.render(object);
		}

		@Override
		public void render(String object, SafeHtmlBuilder builder) {
			builder.append( template.render(object) );
		}
		
	}

	protected Mod2002016Object mod200Object;

	private HashMap<Mod2002016Key, DoubleBox> inputs = new HashMap<Mod2002016Key, DoubleBox>();
	private HashMap<Mod2002016Key, BoxLabel> labels = new HashMap<Mod2002016Key, BoxLabel>();

	@UiField
	Panel basePanel;

	@UiField(provided = true)
	FlexTable table;
	
	public PageAbs() {
		table = new FlexTable();
	}
	
	public void dump(Mod2002016Object mod200Object) {
		this.mod200Object = mod200Object;
		this.mod200Object.register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002016 mod200) {
				for (Mod2002016Key key : mod200.getDraftMap().keySet()) {
					if (inputs.containsKey(key)) {
						DoubleBox input = inputs.get(key);
						DoubleVariable2016 var = mod200.getDraftMap().get(key);
						if (!var.isChangedByUser()) {
							input.setValue(var.getValue(),false,true); ;
						}
						input.addStyleName(AON.AON_CSS.aonChanged());
					}
					if (labels.containsKey(key)) {
						BoxLabel label = labels.get(key);
						label.removeErrorState();
					}
				}
			}
			
		});
		initializeTable();
		refreshDraftMap(this.mod200Object.getMod200());
	}
	
	private void refreshDraftMap(Mod2002016 mod200) {
		for (Mod2002016Key key : mod200.getDraftMap().keySet()) {
			if (inputs.containsKey(key)) {
				DoubleBox input = inputs.get(key);
				DoubleVariable2016 var = mod200.getDraftMap().get(key);
				input.setValue(var.getValue()); ;
				input.addStyleName(AON.AON_CSS.aonChanged());
			}
		}
	}
	
	public Map<Mod2002016Key, DoubleBox> getInputs() {
		return inputs;
	}
	public Map<Mod2002016Key, BoxLabel> getLabels() {
		return labels;
	}
	
	protected int paintKey(final Mod2002016Key key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(FlexTable tab,final Mod2002016Key key,int row) {
		paintKeyDescription(tab,key,row,0);
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	
	protected void paintEmptyCell(FlexTable tab, int row,int col) {
		tab.setWidget(row, col, new Label());
	}
	
	protected void paintKeyDescription(FlexTable tab, Mod2002016Key key, int row,int col) {
		String description = key.getDescription();
		paintDescription(tab, description, row,col,isTitle(key));	
	}
	protected void paintDescription(FlexTable tab, String description, int row,int col, boolean title) {
		Label desc = new Label( AonStringUtils.abbreviate(description, 120) );
		if (AonStringUtils.length(description) > 117) {
			desc.setTitle(description);
		}
		if (title) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}

	protected void paintTitle(FlexTable tab, String description, int row,int col, boolean title) {
		Label desc = new Label( description);
		desc.setStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextUnderline());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
	}
	protected void paintKeyField(FlexTable tab,final Mod2002016Key key,int row, int col) {
		paintKeyField(tab, key, row, col, DoubleBox.VISIBLE_LENGTH);
	}
	protected void paintKeyField(FlexTable tab,final Mod2002016Key key,int row, int col, int fieldLength) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode( mod200Object.getAdministration());
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId);
			getLabels().put(key, code);
			panel.add(code);
		}

		final DoubleBox text = new DoubleBox(fieldLength);
		text.setResolver(resolver);
		text.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					mod200Object.doubleValueChanged(key, d );
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		getInputs().put(key, text);
		if (!isTitle(key)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	protected boolean isDisabled(Mod2002016Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}

	protected boolean isTitle(Mod2002016Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return (behaviour != null && behaviour[0]); 
	}

	protected int paintKeyBreakdownLink(final FlexTable tab,int row,final String label, IMod200KeysProvider[] keysProvider,String[] headers) {
		final int boxRow = row-1;
		final int boxCell = tab.getCellCount(boxRow) - 1;
		FlowPanel panel  = (FlowPanel) tab.getWidget( boxRow , boxCell );
		panel.addStyleName(AON.AON_CSS.aonNowrap());
		Button breakdown = new Button();
		breakdown.setStyleName(AON.AON_CSS.aonIconModel());
		breakdown.addStyleName(AON.AON_CSS.aonBorderNone());
		breakdown.addStyleName(AON.AON_CSS.aonCursorPointer());
		breakdown.addStyleName(AON.AON_CSS.aonMarginRight());
		breakdown.setTitle(AON.MSG.breakdown());
		panel.insert(breakdown,0);
		final FlowPanel container = new FlowPanel();
		container.setVisible(false);
		final String backgroundColor = "#E0FFFF";
		FlexTable tableDetail = getFlexTable(container,row,label,headers);
		int r = 1;
		int col = 0;
		for (IMod200KeysProvider key : keysProvider) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final Mod2002016Key k : key.getKeys() ) {
				if (k != null){
					paintKeyField(tableDetail, k, r, col, 9);
				}
				++col;
			}
			++r;
		}
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);

		breakdown.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				container.setVisible( !container.isVisible() );
				for ( int i = 0 ; i < tab.getCellCount(boxRow); i++) {
					tab.getCellFormatter().getElement(boxRow , i).getStyle().setBackgroundColor(
							container.isVisible()?backgroundColor:"#FFFFFF");	
				}
				container.getElement().getStyle().setBackgroundColor(
						container.isVisible()?backgroundColor:"#FFFFFF");
			}
			
		});
		
		return ++row;
	}
	
	protected FlexTable getFlexTable(Panel container,int row,final String label, String[] headers) {
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(AON.AON_CSS.aonWidthAll());
		container.add(tableDetail);
		int r = 0;
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(r, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonTextCenter());
				if (col>0) {
					tableDetail.getColumnFormatter().setWidth(col, "140px");
				}
				++col;
			}
		}
		return tableDetail;
	}
	
	
	
	protected abstract void initializeTable();
	
}
