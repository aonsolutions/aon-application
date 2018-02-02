package com.esferalia.aon.gwt.stat.client.panel.fee;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.stat.JsStatData;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.stat.client.panel.StatPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class StatFeeProjectionPanel extends StatPanel {

	API API;
	
	public StatFeeProjectionPanel(AonData aonData) {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperButtonElement.SRC,
				PaperInputElement.SRC,
				AonComboBoxElement.SRC
		));
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			setPdfVisible(false);
			setExcelText(AON.MSG.export());
			this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
					aonData.getDomain().getName(), aonData.getDomain().getId(),
					aonData.getUser().getLogin());
			searchContent();
			content();
			return null;
		});
	}
	
	private void searchContent() {
		setSearchContent(new StatFeeProjectionFilterPanel(this));
	}
	
	public void content() {
		API.getFinance().getStatDataFeeProjection(getFilterMap(), new AsyncCallback<JSON<JsStatData>>() {
			
			@Override
			public void onSuccess(JSON<JsStatData> result) {
				setContent(comboChart(result.getData(), AON.MSG.amount(), AON.MSG.monthName()));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}

	@Override
	protected void excel() {
		API.getFinance().downloadExcelFeeProjection(getFilterMap());		
	}
	
	@Override
	protected void pdf() {
		API.getFinance().downloadPdfFeeProjection(getFilterMap());		
	}
}
