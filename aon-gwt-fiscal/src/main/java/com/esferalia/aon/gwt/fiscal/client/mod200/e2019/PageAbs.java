package com.esferalia.aon.gwt.fiscal.client.mod200.e2019;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Behaviour.BEHAVIOUR_KEYS_MAP;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2019.Mod2002019Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2019.Model2002019.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.DoubleVariable2019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Key;
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
import com.google.gwt.user.client.ui.CheckBox;
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
	interface SelectButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-row-selector\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			PageAbs.this.callback.getMod200Object().mathExpression(expression,callback);
		}
	};
	
	static class SelectButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static SelectButtonTemplate template;

		protected SelectButtonSafeHtmlTemplates() {
			template = GWT.create(SelectButtonTemplate.class);
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

	private HashMap<IMod200Key, DoubleBox> inputs = new HashMap<IMod200Key, DoubleBox>();
	private HashMap<IMod200Key, BoxLabel> labels = new HashMap<IMod200Key, BoxLabel>();

	@UiField
	Panel basePanel;

	protected Model200PageCallback callback;
	
	public PageAbs( Model200PageCallback callback) {
		this.callback = callback;
		callback.getMod200Object().register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002019 mod200) {
				for ( IMod200Key key : inputs.keySet() ) {
					DoubleVariable2019 var = mod200.getDraftMap().get(key);
					if (var != null && !var.isChangedByUser()) {
						DoubleBox input = inputs.get(key);
						input.setValue(var.getValue(),false,true); ;
						input.addStyleName(AON.AON_CSS.aonChanged());
					}
				}
				for ( BoxLabel label  : labels.values() ) {
					label.removeErrorState();
				}
			}
		});
	}
	
	public Map<IMod200Key, DoubleBox> getInputs() {
		return inputs;
	}
	public Map<IMod200Key, BoxLabel> getLabels() {
		return labels;
	}
	
	protected int paintKey(FlexTable tab,final Mod2002019Key key,int row) {
		paintKeyDescription(tab,key,row,0);
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	
	protected void paintEmptyCell(FlexTable tab, int row,int col) {
		tab.setWidget(row, col, new FlowPanel());
	}
	
	protected void paintKeyDescription(FlexTable tab, Mod2002019Key key, int row,int col) {
		String description = key.getDescription();
		paintDescription(tab, description, row,col,isTitle(key));	
	}
	protected void paintDescription(FlexTable tab, String description, int row,int col, boolean title) {
		paintDescription(tab, description, row,col, title, 120);	
	}
	protected void paintDescription(FlexTable tab, String description, int row,int col, boolean title, int size) {
		Label desc = new Label( AonStringUtils.abbreviate(description, size) );
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
	protected void paintKeyField(FlexTable tab,final Mod2002019Key key,int row, int col) {
		paintKeyField(tab, key, row, col, DoubleBox.VISIBLE_LENGTH);
	}
	protected void paintKeyField(FlexTable tab,final IMod200Key k,int row, int col, int fieldLength) {
		boolean disabled = isDisabled(k);
		
		FlowPanel panel = new FlowPanel();
		String codeId = k.getCode( callback.getMod200Object().getAdministration());
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId, Model2002019.BOX_LENGTH);
			getLabels().put(k, code);
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
					callback.getMod200Object().doubleValueChanged(k, d );
					
					// Caso especial, la casilla 103 de Deducciones de Doble Imposición, es la 
					// misma en 4 apartados, pero aqui se graba con 4 claves distintas, así que 
					// si se modifica cualquiera de ellas, se hace que todas tengan el mismo valor
					if (k==Mod2002019Key.BN103A) {						
						DoubleBox db = getInputs().get(Mod2002019Key.BN103B);
						db.setValue(d,true);
					}
					else if (k==Mod2002019Key.BN103B) {						
						DoubleBox db = getInputs().get(Mod2002019Key.BN103C);
						db.setValue(d,true);						
					}
					else if (k==Mod2002019Key.BN103C) {						
						DoubleBox db = getInputs().get(Mod2002019Key.BN103D);
						db.setValue(d,true);
					}
					else if (k==Mod2002019Key.BN103D) {						
						DoubleBox db = getInputs().get(Mod2002019Key.BN103A);
						db.setValue(d,true);
					}
					
				} catch (ParseException e) {
					// nothing
				}
			}
		});
		text.setValue(callback.getMod200Object().getDoubleValue(k));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		getInputs().put(k, text);
		if (!isTitle(k)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	protected boolean isDisabled(IMod200Key k) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k);
		return behaviour != null && behaviour[1];
	}

	protected boolean isTitle(IMod200Key k) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k);
		return (behaviour != null && behaviour[0]); 
	}
	
	protected int paintKeyBreakdownLink(final FlexTable tab, int row, Mod2002019Key breakdownKey, IMod200KeysProvider[] keysProvider, String[] headers) {
		
		final int boxRow = row-1;
		final int boxCell = 1;
		
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
		FlexTable tableDetail = getFlexTable(container, row, headers);
		int r = 1;
		int col = 0;		
		for (IMod200KeysProvider key : keysProvider) {
			Label desc = new Label(key.getDescription() );			
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;			
			for (final IMod200Key k : key.getKeys() ) {
				// La casilla del desglose no la pintamos porque sino se duplicarian
				// las casillas en la pantalla y tal y como está montado ahora el 
				// repintado de todas las casillas, al recalcular solo se pinta una de las 
				// casillas, si ambas casillas son la misma
				if (k != null && k != breakdownKey) {			
					paintKeyField(tableDetail, k, r, col, 9);					
				}
				++col;
			}
			++r;
		}
		
		// Desglose casilla 565, lleva al final 2 checks más
		if (breakdownKey == Mod2002019Key.BN565) {
			addCheckBox(Mod2002019Key.BN565A, tableDetail, r);
			++r;
			addCheckBox(Mod2002019Key.BN565B, tableDetail, r);
		}
		
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, tab.getCellCount(boxRow)); 
		
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
	
	protected void addCheckBox(Mod2002019Key key, FlexTable tab, int row) {
		
		final CheckBox cb = new CheckBox();
		cb.setText(key.getDescription());
		
		DoubleVariable2019 dv = callback.getMod200Object().getMod200().getKeysMap().get(key);			
		if (dv != null) {				 
		   cb.setValue(dv.getValue()==1.0);
		}

		cb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DoubleVariable2019 bv = new DoubleVariable2019(key);
				bv.setValue( cb.getValue()?1.0:0.0 );
				callback.getMod200Object().getMod200().addVariable(bv);					
			}
			
		});
		
		tab.setWidget(row, 0, cb);
		tab.getFlexCellFormatter().setColSpan(row, 0, 3);
		
		
	}
	

	protected FlexTable getFlexTable(Panel container, int row, String[] headers) {
		FlexTable tableDetail = new FlexTable();
		container.add(tableDetail);
		return getFlexTable(tableDetail, row, headers);
	}
	
	protected FlexTable getFlexTable(FlexTable tableDetail, int row, String[] headers) {
		tableDetail.addStyleName(AON.AON_CSS.aonWidthAll());
		int r = 0;
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(r, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonTextCenter());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonFontSmall());
				if (col>0) {
					tableDetail.getColumnFormatter().setWidth(col, "140px");
				}
				++col;
			}
		}
		return tableDetail;
	}
	
	protected abstract void populate();
	protected abstract void initializeTable();
	
	protected void dump() {
		for (IMod200Key key : callback.getMod200Object().getMod200().getDraftMap().keySet()) {
			if (inputs.containsKey(key)) {
				DoubleBox input = inputs.get(key);
				DoubleVariable2019 var = callback.getMod200Object().getMod200().getDraftMap().get(key);
				input.setValue(var.getValue()); ;
				input.addStyleName(AON.AON_CSS.aonChanged());
			}
		}
	}
	
	protected boolean isAvailable() {
		return true;
	}
}
