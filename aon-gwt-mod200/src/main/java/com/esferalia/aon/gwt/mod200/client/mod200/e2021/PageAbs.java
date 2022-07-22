package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Behaviour.BEHAVIOUR_KEYS_MAP;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PageAbs extends ResizeComposite {
	
	protected static final String ACCOUNTING_STATEMENTS_FOOTER = "(N) Modelo normal de dep\u00F3sito de cuentas en el Registro Mercantil; (A) Modelo abreviado de dep\u00F3sito de cuentas en el Registro Mercantil; (P) Modelo PYMES de dep\u00F3sito de cuentas en el Registro Mercantil.";
	protected static final int BOX_LENGTH = 5;	

	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			PageAbs.this.callback.getMod200Object().mathExpression(expression,callback);
		}
	};
	
	protected HashMap<IMod200Key, AonDoubleBox> inputs = new HashMap<IMod200Key, AonDoubleBox>();
	protected ArrayList<Widget> otherInputs = new ArrayList<Widget>();
	protected FlowPanel basePanel;
	protected Model200PageCallback callback;
	
	public PageAbs(Model200PageCallback callback) {
		this.callback = callback;
		
		callback.getMod200Object().register(new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002021 mod200) {
				for (IMod200Key key : inputs.keySet()) {
					DoubleVariableEx var = mod200.getDraftMap().get(key);
					if (var != null && !var.isChangedByUser()) {
						// Se repintan los valores calculados automaticamente
						AonDoubleBox input = inputs.get(key);
						input.setValue(var.getValue());						
					}
				}
			}
		});
		
		addBasePanel();
		initializeTable();

	}
	
//	protected abstract void populate();
	protected abstract void initializeTable();
	
	protected void dump() {		
		
		// Se repintan los valores calculados automáticamente. DraftMap lleva los valores que se han modificado o 
		// calculado automáticamente, desde que hemos entrado en el modelo
		for (IMod200Key key : callback.getMod200Object().getMod200().getDraftMap().keySet()) {
			if (inputs.containsKey(key)) {
				AonDoubleBox input = inputs.get(key);
				DoubleVariableEx var = callback.getMod200Object().getMod200().getDraftMap().get(key);
				input.setValue(var.getValue());
			}
		}
		
		// Habilitar/Deshabilitar controles de la pagina
		setEnabled();
		
	}
	
    // Indica si la página está disponible en el modelo, para su cumplimentacion
	protected boolean isAvailable() {
		return true;
	}

	protected boolean isTitle(IMod200Key k) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k);
		return (behaviour != null && behaviour[0]); 
	}
	
	protected boolean isDisabled(IMod200Key k) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k);
		return behaviour != null && behaviour[1];
	}
	
	protected boolean isEditable() {
		return isEditable(null);
	}
	
	protected boolean isEditable(IMod200Key k) {
		return (!isDisabled(k)) && (callback.getMod200Object().getMod200().isEditable());
	}
	
	// Habilitar/Deshabilitar los controles de edicion de la página 
	protected void setEnabled() {
		
		// Se habilitan si el modelo es editable y la casilla no está deshabilitada
		for (IMod200Key key : inputs.keySet()) {
			inputs.get(key).setEnabled(isEditable(key));
		}
		
		// Se habilitan si el modelo es editable
		boolean enabled = isEditable();
		for (Widget input : otherInputs) {
			if (input instanceof AonDateBox) {
				((AonDateBox) input).setEnabled(enabled);
			} else if (input instanceof FocusWidget) {
				((FocusWidget) input).setEnabled(enabled);
			} else if (input instanceof AonIbanTextBox) {
				((AonIbanTextBox) input).setEnabled(enabled);
			}
		}
		
	}
	
	private void addBasePanel() {
		ScrollPanel scroll = new ScrollPanel();
		basePanel = new FlowPanel();		
		basePanel.addStyleName(AON.CSS.aonPaddingBottom());
		scroll.add(basePanel);
		initWidget(scroll);		
	}
	
	protected void paintEmptyCell(FlexTable tab, int row, int col) {
		tab.setWidget(row, col, new FlowPanel());		
	}
	
	protected int paintKey(FlexTable tab, final Mod2002021Key key, int row) {
		paintKeyDescription(tab, key, row, 0);
		paintKeyField(tab, key, row, 1);	
		return ++row;
	}
	
	protected int paintKey(FlexTable tab, final Mod2002021Key key, int row, boolean bold) {
		paintDescription(tab, key.getDescription(), row, 0, bold);
		paintKeyField(tab, key, row, 1);	
		return ++row;
	}
	
	protected int paintKey(FlexTable tab, final Mod2002021Key key, int row, boolean bold, boolean padding) {
		paintDescription(tab, key.getDescription(), row, 0, bold);
		paintKeyField(tab, key, row, 1, padding);	
		return ++row;
	}
	
	protected void paintKeyDescription(FlexTable tab, Mod2002021Key key, int row, int col) {
		paintDescription(tab, key.getDescription(), row, col, isTitle(key));	
	}
	
	protected void paintDescription(FlexTable tab, String description, int row, int col, boolean bold) {
		paintDescription(tab, description, row, col, bold, 0);
	}
	
	protected void paintDescription(FlexTable tab, String description, int row, int col, boolean bold, int size) {
		Label desc = new Label( size > 0 ? AonStringUtils.abbreviate(description, size) : description );
		if (size > 0 && AonStringUtils.length(description) > (size-3)) {
			desc.setTitle(description);
		}
		if (bold) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}
	
	protected void paintKeyField(FlexTable tab, final Mod2002021Key key, int row, int col) {
		paintKeyField(tab, key, row, col, !isTitle(key));
	}
	
	protected void paintKeyField(FlexTable tab, final IMod200Key key, int row, int col, boolean padding) {
		String code = key.getCode(callback.getMod200Object().getAdministration());
		paintKeyField(tab, key, row, col, padding, code, isEditable(key));
	}
	
	protected void paintKeyField(FlexTable tab, final IMod200Key key, int row, int col, boolean padding, String code, boolean enabled) {
		paintKeyField(tab, key, row, col, AonDoubleBox.VISIBLE_LENGTH, padding, code, enabled);
	}
	
	protected void paintKeyField(FlexTable tab, final IMod200Key k, int row, int col, int visibleLength, boolean padding) {
		String code = k.getCode(callback.getMod200Object().getAdministration());
		paintKeyField(tab, k, row, col, visibleLength, padding, code);
	}
	
	protected void paintKeyField(FlexTable tab, final IMod200Key k, int row, int col, int visibleLength, boolean padding, String code) {
		paintKeyField(tab, k, row, col, visibleLength, padding, code, isEditable(k));
	}
	
	protected void paintKeyField(FlexTable tab, final IMod200Key k, int row, int col, int visibleLength, boolean padding, String code, boolean enabled) {
		
		FlowPanel panel = new FlowPanel();
		if (padding) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}		

		if (AonStringUtils.isNotBlank(code)) {
			AonBoxLabel codeBoxLabel = new AonBoxLabel(code, BOX_LENGTH);
			panel.add(codeBoxLabel);
		}

		final AonDoubleBox text = new AonDoubleBox(visibleLength);
		text.setResolver(resolver);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setValue(callback.getMod200Object().getDoubleValue(k));
		text.setEnabled(enabled);
		text.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					callback.getMod200Object().doubleValueChanged(k, d);
					callback.markAsDirty();
					
					// Caso especial, la casilla 103 de Deducciones de Doble Imposición, es la 
					// misma en 4 apartados, pero aqui se graba con 4 claves distintas, así que 
					// si se modifica cualquiera de ellas, se hace que todas tengan el mismo valor
					if (k==Mod2002021Key.BN103A) {						
						AonDoubleBox db = inputs.get(Mod2002021Key.BN103B);
						db.setValue(d,true);
					}
					else if (k==Mod2002021Key.BN103B) {						
						AonDoubleBox db = inputs.get(Mod2002021Key.BN103C);
						db.setValue(d,true);						
					}
					else if (k==Mod2002021Key.BN103C) {						
						AonDoubleBox db = inputs.get(Mod2002021Key.BN103D);
						db.setValue(d,true);
					}
					else if (k==Mod2002021Key.BN103D) {						
						AonDoubleBox db = inputs.get(Mod2002021Key.BN103A);
						db.setValue(d,true);
					}
					
				} catch (ParseException e) {
					// nothing
				}
			}
		});
			
		inputs.put(k, text);
		
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	
	protected int paintKeyBreakdownLink(final FlexTable tab, int row, Mod2002021Key breakdownKey, IMod200KeysProvider[] keysProvider, String[] headers, String... footernotes) {
		
		final int boxRow = row-1;
		final int boxCell = 1;
		
		FlowPanel panel = (FlowPanel) tab.getWidget( boxRow , boxCell );
		panel.addStyleName(AON.AON_CSS.aonNowrap());
		
		Button breakdown = new Button();		
		breakdown.setStyleName(AON.AON_CSS.aonIconModel());
		breakdown.addStyleName(AON.AON_CSS.aonBorderNone());
		breakdown.addStyleName(AON.AON_CSS.aonCursorPointer());
		breakdown.addStyleName(AON.AON_CSS.aonMarginRight());
		breakdown.setTitle(AON.MSG.breakdown());		 
		panel.insert(breakdown,0);
		
		final FlowPanel container = new FlowPanel();		
		container.addStyleName(AON.CSS.aonPaddingBottom());
		container.setVisible(false);
		final String backgroundColor = "#E0FFFF";
		FlexTable tableDetail = getFlexTable(container, headers);
		int r = 1;
		int col = 0;		
		for (IMod200KeysProvider key : keysProvider) {
			boolean paintDesc = true;
			col = 1;
			for (final IMod200Key k : key.getKeys() ) {
				// La casilla del desglose no la pintamos porque sino se duplicarian
				// las casillas en la pantalla y tal y como está montado ahora el 
				// repintado de todas las casillas, al recalcular solo se pinta una de las 
				// casillas, si ambas casillas son la misma
				if (k != null && k != breakdownKey) {
					if (paintDesc) {
						// Casilla [2287] comienza bloque de información adicional, dentro del desglose de la [590]
						if (k == Mod2002021Key.BN2287) {							
							addHeaderCell(tableDetail, r++, 0, "Informaci\u00F3n adicional para el c\u00E1lculo de l\u00EDmites de deducciones");
						}
						Label desc = new Label(key.getDescription());			
						tableDetail.setWidget(r, 0, desc);
						desc.setStyleName(AON.AON_CSS.aonMarginLeft());
						if ("Total".equals(key.getDescription()))
							desc.addStyleName(AON.AON_CSS.aonBold());
						tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
						paintDesc = false;
					}
					paintKeyField(tableDetail, k, r, col, 9, false);					
				}
				++col;
			}
			if (!paintDesc)
				++r;
		}
		
		// Notas al pie
		paintFooterNote(container, footernotes);
		
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
	
	protected void paintKeysProvider(IMod200KeysProvider[] keysProvider, final FlexTable tab, String... headers) {
		paintKeysProvider(keysProvider, tab, 0, true, headers);
	}
	
	protected void paintKeysProvider(IMod200KeysProvider[] keysProvider, final FlexTable tab, int row, boolean padding, String... headers) {
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				addHeaderCell(tab, row, i, headers[i]);				
			}
			row++;
		}
				
		for (IMod200KeysProvider kp : keysProvider) {
			paintDescription(tab, kp.getDescription(), row, 0, "Total".equals(kp.getDescription()));
			int col = 1;
			for (IMod200Key key : kp.getKeys()) {				 
				if (key != null && callback.getMod200Object().isVisible((Mod2002021Key) key)) {
					paintKeyField(tab, key, row, col, 10, padding);
				}
				col++;
			}
			row++;
		}
	}
	
	protected FlexTable getFlexTable(Panel container, String[] headers) {
		FlexTable tableDetail = new FlexTable();
		container.add(tableDetail);
		return getFlexTable(tableDetail, 0, headers);
	}
	
	protected FlexTable getFlexTable(FlexTable tableDetail, int row, String[] headers) {
		tableDetail.addStyleName(AON.AON_CSS.aonWidthAll());
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(row, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
				tableDetail.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
				if (col>0) {
					tableDetail.getColumnFormatter().setWidth(col, "140px");
				}
				++col;
			}
		}
		return tableDetail;
	}
	
	protected Label getTitle(String text) {
		Label title = new Label(text);
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontMedium());
		title.addStyleName(AON.CSS.aonWidthAlmostAll());
		title.addStyleName(AON.CSS.aonBlockCenter());
		title.addStyleName(AON.CSS.aonBorderBottom());
		return title;
	}
	
	protected Label getSubtitle(String text) {
		Label subtitle = new Label(text);
		subtitle.setStyleName(AON.CSS.aonMarginTop());
		subtitle.addStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonTextUppercase());
		subtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subtitle.addStyleName(AON.CSS.aonBlockCenter());
		subtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subtitle;
	}

	// Añadir FlexTable a basePanel
	protected FlexTable addTable() {
		return addTable("");
	}
	protected FlexTable addTable(String title) {
		return addTable(title, 1);
	}
	protected FlexTable addTable(int numAmountCols) {
		return addTable("", numAmountCols);
	}
	protected FlexTable addTable(String title, int numAmountCols) {
		return addTable(title, numAmountCols, "200px", false);			
	}
	protected FlexTable addTable(String title, int numAmountCols, String columnWidth) {
		return addTable(title, numAmountCols, columnWidth, false);			
	}
	protected FlexTable addTable(String title, int numAmountCols, String columnAmountWidth, boolean horizontalScroll) {

		if (AonStringUtils.isNotBlank(title)) {
			basePanel.add(getTitle(title));			
		}
		
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonMargin());
		
		// Ancho de las columnas de importes
		for (int i = 0; i < numAmountCols; i++) {
			tab.getColumnFormatter().setWidth((i+1), columnAmountWidth);
		}
		
		if (horizontalScroll) {
			FlowPanel tableContainer = new FlowPanel();
			tableContainer.addStyleName(AON.AON_CSS.aonBorderBottom());
			tableContainer.addStyleName(AON.AON_CSS.aonFiscalScrollTableWrapper());
			tableContainer.add(tab);			
			basePanel.add(tableContainer);
		} else {
			basePanel.add(tab);			
		}
		
		return tab;
		
	}
	
	// Añadir FlexTable a basePanel, con unas filas de datos (descripcion, 1 casilla de importe)
	protected void addTable(String title, Mod2002021Key[] keys) {

		FlexTable table = addTable(title);

		int row = 0;
		for (Mod2002021Key key : keys) {
			if (callback.getMod200Object().isVisible(key) ) {
				row = paintKey(table, key, row);
			}
		}
		
	}
	
	protected void addHeaderCell(FlexTable table, int row, int col, String msg) {
		addHeaderCell(table, row, col, msg, true);
	}
	protected void addHeaderCell(FlexTable table, int row, int col, String msg, boolean smallFont) {

		table.setWidget(row, col, new Label( msg ));
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		if (smallFont) {
			table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
		}
		
	}
	
	protected void paintFooterNote(Panel container, String... notes) {
 
		for (String text : notes) {
			Label footernote = new Label(text);
			footernote.setWidth("95%");
			footernote.addStyleName(AON.CSS.aonFontSmaller());
			footernote.addStyleName(AON.CSS.aonBlockCenter());
			container.add(footernote);
		}
	
	}
	
	protected void addLabel(String text) {
		addLabel(text, false);		
	}
	protected void addLabel(String text, boolean isBold) {
		paintLabel(basePanel, text, isBold);		
	}
	
	protected void paintLabel(Panel container, String text, boolean isBold) {
		
		Label label = new Label(text);
		label.setStyleName(AON.CSS.aonMargin());
		label.addStyleName(AON.CSS.aonWidthAlmostAll());
		label.addStyleName(AON.CSS.aonBlockCenter());
		if (isBold) 
			label.addStyleName(AON.CSS.aonBold());
		
		container.add(label);
		
	}
	
	public void removeAonChanged() {
		for (AonDoubleBox input : inputs.values()) {
			input.removeStyleName(AON.AON_CSS.aonChanged());
		}
	}
	
}
