package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Cost extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM =  25;
	private static final int MAX_ZOOM =  500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	interface Binder extends UiBinder<Widget, Cost> {
	}


	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	HTML container;

	@UiField
	Button excelButton;
	@UiField
	Button printButton;

	@UiField
	MenuItem printMenuItem;
	@UiField
	MenuItem downloadMenuItem;

	@UiField
	MenuItem reduceMenuItem;
	@UiField
	MenuItem enlargeMenuItem;

	@UiField
	ListBox dateListBox;

	private int zoom = DEFAULT_ZOOM;

	private CostDocuments costDocuments;

	public Cost() {
		initWidget(binder.createAndBindUi(this));

		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSalaryDateChanged();
			}
		});
	
		excelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = costDocuments.current();
				document.download("xls");
			}
		});

		printButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = costDocuments.current();
				document.print();
			}
		});

		printMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = costDocuments.current();
				document.print();
			}
		});
		
		downloadMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = costDocuments.current();
				document.download();
			}
		});

		
		reduceMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
				getAsHTML();
			}
		});

		enlargeMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
				getAsHTML();
			}
		});
	}
	
	/**
	 * 
	 */
	public void setCostDocuments(CostDocuments costDocuments) {
		
		
		this.costDocuments = costDocuments;
		onCostDocumentsChanged();
	}

	private void getAsHTML() {
		costDocuments.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				container.setHTML(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				container.setHTML(caught.getLocalizedMessage());
			}
		});
	}

	private void onCostDocumentsChanged() {
		getAsHTML();
		syncFormatsButtons();
		syncCostDateListBox();
	}

	private void onSalaryDateChanged() {
		int selected = dateListBox.getSelectedIndex();
		costDocuments.setCurrentIndex(selected);
		getAsHTML();
	}
	

	private void syncCostDateListBox() {
		dateListBox.clear();
		for (com.esferalia.aon.gwt.payroll.shared.Cost cost : costDocuments.getCosts()) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dateListBox.addItem(DATE_FORMAT.format(date));
		}
		dateListBox.setSelectedIndex(costDocuments.getCurrentIndex());
	}
	
	private void syncFormatsButtons() {
		String formats [] = costDocuments.getSupportedFormats();
		Arrays.sort(formats);
		
		excelButton.setVisible(Arrays.binarySearch(formats, "xls")>=0);
	}
	

}
