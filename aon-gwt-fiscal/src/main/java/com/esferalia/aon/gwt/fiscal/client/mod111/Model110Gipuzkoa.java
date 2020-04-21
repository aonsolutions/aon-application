package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod111.Model110GipuzkoaScript;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class Model110Gipuzkoa extends Model111Base {
	
	private static final String DOWNLOAD_TEXT = "Descargar fichero para presentaci\u00F3n telem\u00E1tica.";
	
	public Model110Gipuzkoa(IFiscalModelCallback<Mod111> callback, AonData aonData) {
		super(callback, aonData);
	}

	protected void beforeTable() {
		
	}
	
	public FlowPanel getDeclarationPanel(){
		FlowPanel panel = new FlowPanel();
		panel.setWidth("98%");
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod111Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);

		panel.add(formContainer);
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		return panel;
	}
	
	protected FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button(DOWNLOAD_TEXT);
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getModel().isFinished() || getModel().isSent()) {
					submitForm(MODEL111_FILE);
				} else {
			// TODO		getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab);
		return panel;
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria"
				, "http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hZDbjqowFIaf"
				+ "xQcgbakHvCxQBKZ2UECkNwQEO6iAjIro0293YrKzMxld62plff86_ECAe"
				+ "ALhaApHGII1EHXalTI9l02dHv7WYpwgrlKiIwK1pWtAhwauETo6nGD0AO"
				+ "IHAH8JAl_qA_WpfwH8r0fm6NFejoi_WOHPoQoisIbDxN9px_ntvGZ3owt"
				+ "29wXk91CdB6TnZopOPPDy1TLUic4p5NGPnT-Gvvk5AuIlYqMn8MqWd8aI"
				+ "d1dwu6kK4AKh4meCVRZjbjbOlTr-VjTKIcLDclpkuQ71y9lSV18spaxxO"
				+ "5bMiFu3sTDZVtBY1Wq3thuFSK-whi1iG0USLw7FyS5uOPcbNtfUW5Gop7"
				+ "IXGe6r5ppvkiXxh-aXpStyPOnkiLYGl351zfi0EvlB80rJhWQb-771FTy"
				+ "repO6zLT7Gumyaz7dc4WzHpIJvbTXuNdyc0uTUB4TbX_MVj1aGCpHTti2"
				+ "32nJMDnu8o89WxyCdSTHnqbsrZCVUbsYDED8cGryz6mZObegE3pzZDlja"
				+ "HxAEIBYB35xSagPKnGw2E3ZL-0rrjo5-AMzRZ9M/dl4/d5/L0lHSkovd0"
				+ "RNQUxrQUVnQSEhLzRKVUUvZXM!/"));
		return list;
	}
	
	@Override
	protected void paintParticularyRow(IFiscalModelCallback<Mod111> callback, IModelScript<Mod111Key> script) {
		if (script == Model110GipuzkoaScript.X00) {
			int row = getTable().getRowCount();
			getTable().getFlexCellFormatter().setColSpan(row, 0, 8);
			getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
			getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
			getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );
			getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderTop() );
			getTable().setWidget(row, 0, new Label( "Informaci\u00F3n para la presentaci\u00F3n telem\u00E1tica"));
			row++;
			paintLabel(row, callback, script);
			getTable().getFlexCellFormatter().setColSpan(row, 0, 6);
			Mod111Key key = Mod111Key.GP_X00;
			final FiscalModelDetail det1 = callback.getFiscalModel().ensureDetail(key);
			final TextBox input = new TextBox();
			input.setStyleName(AON.AON_CSS.aonInputText());
			input.setVisibleLength(10);
			input.setMaxLength(9);
			input.setEnabled(callback.getFiscalModel().isNotFinished() && script.isEnabled()); 
			input.setValue(det1.getDescription());
			input.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					callback.getFiscalModel().ensureDetail(key).setDescription(input.getValue());	
					if (input.isEnabled()) {
						calculateAndRefresh( callback );
					}
					callback.markAsDirty();
				}
			});
			getTable().setWidget(row, 1, input);
		}
		
	}
}
