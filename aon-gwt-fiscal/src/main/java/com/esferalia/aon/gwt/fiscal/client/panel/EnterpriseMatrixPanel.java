package com.esferalia.aon.gwt.fiscal.client.panel;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix.FiscalModelMatrixRow;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseMatrixPanel extends ResizeComposite {

	interface EnterpriseFormBinder extends
			UiBinder<Widget, EnterpriseMatrixPanel> {
	}

	private static final EnterpriseFormBinder panelBinder = GWT
			.create(EnterpriseFormBinder.class);
	
	Enterprise enterprise;
	
	@UiField
	ListBox year;
	@UiField
	SimplePanel tablePanel;

	public EnterpriseMatrixPanel() {
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		year.addItem("2010");
		year.addItem("2011");
		year.addItem("2012");
		year.addItem("2013");
		year.addItem("2014");
		year.addItem("2015");
		year.setSelectedIndex(5);
	}
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
		search();
	}

	private void search() {
		int yr = Integer.parseInt( year.getSelectedValue() );
		FiscalPanel.FISCAL_SERVICE.getFiscalPanel(FiscalPanel.getCurrentDomainName(),
				this.enterprise.getDomain(), yr
				,new AsyncCallback<FiscalModelMatrix>() {

					@Override
					public void onSuccess(FiscalModelMatrix result) {
						if (result.getRows() == null ) {
							tablePanel.setWidget(new HTMLPanel(FiscalPanel.MSG.noData()));
						} else {
							paintTable(result);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						tablePanel.setWidget(new HTMLPanel(FiscalPanel.MSG.noData()));
					}
		});
	}
	
	@UiHandler("year")
	void onChangeYear(ChangeEvent event) {
		search(); 
	}
	

	private void paintTable(FiscalModelMatrix matrix) {
		FlexTable table = new FlexTable();
		table.addStyleName(FiscalPanel.AON_RESOURCES.css().aonFiscalMatrix());
		int row = 0;
		
		table.setWidget(row, 0, new Label(""));
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
		String[] months = new String[]{"E","F","M","A","M","J","X","A","S","O","N","D"};
		for (int i = 0; i < months.length; i++  ) {
			table.setWidget(row, i+1, new Label(months[i]));	
			table.getFlexCellFormatter().addStyleName(row, i+1, FiscalPanel.AON_RESOURCES.css().aonFiscalMatrixMonthCell());
		}
		row++;
		
		for (FiscalModelMatrixRow item : matrix.getRows()) {

			int col = 0;
			
			InlineLabel admon = new InlineLabel("");
			if (item.getAdministration() == Administration.COMMON_TERRITORY) {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconAeat());
			} else if (item.getAdministration() == Administration.ALAVA) {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconAraba());
			} else if (item.getAdministration() == Administration.BIZKAIA) {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconBizkaia());
			} else if (item.getAdministration() == Administration.GIPUZKOA) {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconGipuzkoa());
			} else if (item.getAdministration() == Administration.NAVARRA) {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconNavarra());
			} else {
				admon.setStyleName(FiscalPanel.AON_RESOURCES.css().aonIconQuestion());
			}
			admon.addStyleName(FiscalPanel.AON_RESOURCES.css().aonIconPaddingLeft());
			table.getFlexCellFormatter().addStyleName(row, col, FiscalPanel.AON_RESOURCES.css().aonFiscalMatrixAdmonCell());
			table.setWidget(row, col++, admon);
			
			
			Label model = new Label(item.getModel().getName() );
			table.getFlexCellFormatter().addStyleName(row, col, FiscalPanel.AON_RESOURCES.css().aonFiscalMatrixModelCell());
			table.setWidget(row, col++, model);
			
			int colSpan = 12 / item.getPeriod().getNumberOfPeriod();
			for (int x = 0 ; x < item.getStatuses().length ; x++) {
				FiscalStatus status = item.getStatuses()[x];
				String styleName = FiscalPanel.AON_RESOURCES.css().aonFiscalStatusMissing();
				String text = "\u2022";
				if (status ==FiscalStatus.PENDING) {
					styleName = FiscalPanel.AON_RESOURCES.css().aonFiscalStatusPending();
					text = "\u2014";
				} else if (status ==FiscalStatus.FINISHED) {
					styleName = FiscalPanel.AON_RESOURCES.css().aonFiscalStatusFinished();
					text = "\u2714";
				}
				Label label = new Label(text);
				table.getFlexCellFormatter().setColSpan(row, (x+col), colSpan);
				table.setWidget(row, (x+col), label);
				table.getFlexCellFormatter().addStyleName(row, col, FiscalPanel.AON_RESOURCES.css().aonFiscalMatrixDataCell());
				table.getFlexCellFormatter().addStyleName(row, (x+col), styleName);
				
			}
			row++;
		}
		tablePanel.setWidget(table);
	}
}
