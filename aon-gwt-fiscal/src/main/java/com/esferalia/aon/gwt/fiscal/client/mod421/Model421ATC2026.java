package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultT1Script;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultT4Script;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model421ATC2026 extends Model421ATC {

	private TabLayoutPanel tabPanel;
	private Model421ATC2026SimplifiedRegimeActivities simplifiedRegimeActivities;
	
	private CheckBox withoutActivityCheck;
	private CheckBox x03; // Concurso de acreedores
	private AonTextBox previousReceiptBox;
	private AonTextBox administrationCode;

	private static final int SIMPLIFIED_REGIME_TAB = 2;
	private static final int RESULT_TAB = 3;

	protected Model421ATC2026(Mod421 mod421, Model421Callback callback) {
		super(mod421,callback);
		tabPanel = new TabLayoutPanel(26, Unit.PX);
		
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintSimplifiedRegimeTab(tabPanel);
		paintResultTab(tabPanel);
		showPaymentInfo(getModel());
		paintAdministrationTab(tabPanel);
		Scheduler.get().scheduleDeferred(this::selectDefaultTab);
	}
	
	private void selectDefaultTab() {
		if (getModel().isFinished() || getModel().isSent()) {
			tabPanel.selectTab(RESULT_TAB);
		} else {
			tabPanel.selectTab(SIMPLIFIED_REGIME_TAB);	
		}
	}
	
	private void paintSimplifiedRegimeTab(TabLayoutPanel tabPanel) {
		DockLayoutPanel simplifiedTableContainer = new DockLayoutPanel(Unit.PX);
		
		simplifiedRegimeActivities = new Model421ATC2026SimplifiedRegimeActivities(() -> getModel());
		simplifiedRegimeActivities.addValueChangeHandler(e -> {
			calculateAndRefresh();
			markAsDirty();
		});
		simplifiedTableContainer.addNorth( simplifiedRegimeActivities, 300);
		tabPanel.add(simplifiedTableContainer, AON.MSG.simplifiedRegime());
	}

	private void paintResultTab(TabLayoutPanel tabPanel) {
		ScrollPanel resultScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "25px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "130px");
		table.getColumnFormatter().setWidth(3, "25px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "130px");
		table.getColumnFormatter().setWidth(5, "25px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "130px");
		table.getColumnFormatter().setWidth(7, "30px");
		table.getColumnFormatter().setStyleName(7, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, "130px");
		table.getColumnFormatter().setWidth(9, "50px");
		
		resultScrollPanel.setWidget(table);
		tabPanel.add(resultScrollPanel, AON.MSG.result());
		
		if (getModel().isLastPeriod()) {
			paintDeclaration(table, Model4212026ATCResultT4Script.values(),9);
		} else {
			paintDeclaration(table, Model4212026ATCResultT1Script.values(),9);
		}
		
		paintEmptyRow(table);
		paintEmptyRow(table);
		paintEmptyRow(table);
		paintEmptyRow(table);
		paintScript(table, Model4212026ATCResultScript.values(),9);
		
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		administrationCode = paintAdministrationCode(table);		// Código administración tributaria
		withoutActivityCheck = paintWithoutActivityCheck(table);	// Sin actividad
		x03 = paintCheck(Mod421Key.X03,table);	// Concurso de acreedores 
				
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.receipt());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setEnabled(getModel().isEditable());
		receiptBox.setValue( getModel().getNumber() );
		receiptBox.addValueChangeHandler( event -> {
			getModel().setNumber(receiptBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, receiptBox);

		// Complementaria: Numero justificante de la declaración anterior
		if (getModel().isComplementary()) {
			paintEmptyRow(table);
			paintEmptyRow(table);
			row = table.getRowCount();
			paintLabel(table, row, "Autoliquidaci\u00F3n Complementaria:", true);
			row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt());
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setEnabled(getModel().isEditable());
			previousReceiptBox.setValue( getModel().getReplacedNumber() );
			previousReceiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, previousReceiptBox);
		}	
		
		container.add(addGroupPanel("", table));
		
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	private FlexTable createTable() {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		return table;
	}
	
	@Override
	protected void populate(Mod421 mod421) {
		super.populate(mod421);
		simplifiedRegimeActivities.populate(getModel());
	}
	
	@Override
	protected void save() {
		save(new AsyncCallback<Mod421>() {
			@Override 
			public void onFailure(Throwable caught) { 
				// Nothing 
			}
			@Override
			public void onSuccess(Mod421 result) {
				simplifiedRegimeActivities.populate( result );
			}
		});
	}
	
	@Override
	protected void decorateDeclarationTab() {
		super.decorateDeclarationTab();
		enable(withoutActivityCheck);
		enable(x03);
		enable(previousReceiptBox);
		enable(receiptBox);
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
		enable(administrationCode);
	}
	
}
