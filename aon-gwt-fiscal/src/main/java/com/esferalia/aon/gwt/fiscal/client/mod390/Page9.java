package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.DialogMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page9 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page9> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	private static final FiscalMessages MSG = GWT.create(FiscalMessages.class);
	
	private FiscalServiceAsync fiscalService;

	@UiField
	DoubleTextBox box95;
	@UiField
	DoubleTextBox box96;
	@UiField
	DoubleTextBox box524;
	@UiField
	DoubleTextBox box97;
	@UiField
	DoubleTextBox box98;
	@UiField
	DoubleTextBox box525;
	@UiField
	DoubleTextBox box526;

	int domain;
	int year;

	public Page9() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		FiscalServiceAsync mod190ServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(mod190ServiceRaw);

		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void initialize(int domain, int year) {
		this.domain = domain;
		this.year = year;
		
		fiscalService.getMod303Results(domain, year,
				new AsyncCallback<Mod303Results>() {
					@Override
					public void onSuccess(Mod303Results result) {
						box95.setValue(result.getDepositSum());
						box96.setValue(result.getPaybackSum());
						box97.setValue(result.getLastPeriodCompensateResult());
						box98.setValue(result.getLastPeriodPaybackResult());
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG
								.unableToFindMod190Detail(caught
										.getMessage()));
					}
				});
	}

	public void setValue(Mod390 m390) {
		box95.setValue(m390.getBox95());
		box96.setValue(m390.getBox96());
		box524.setValue(m390.getBox524());
		box97.setValue(m390.getBox97());
		box98.setValue(m390.getBox98());
		box525.setValue(m390.getBox525());
		box526.setValue(m390.getBox526());
	}

	public void populate(Mod390 mod390) {
		mod390.setBox95(box95.getDoubleValue());
		mod390.setBox96(box96.getDoubleValue());
		mod390.setBox524(box524.getDoubleValue());
		mod390.setBox97(box97.getDoubleValue());
		mod390.setBox98(box98.getDoubleValue());
		mod390.setBox525(box525.getDoubleValue());
		mod390.setBox526(box526.getDoubleValue());
	}
	
}
