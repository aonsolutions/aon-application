package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

	private List<com.esferalia.aon.gwt.payroll.shared.Cost> costs;

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
		syncCostDateListBox();
	}

	private void onSalaryDateChanged() {
		com.esferalia.aon.gwt.payroll.shared.Cost currentCost = 
				costs.get(dateListBox.getSelectedIndex());
		costDocuments.setCurrent(currentCost);
		getAsHTML();
	}
	

	private void syncCostDateListBox() {
		dateListBox.clear();

		com.esferalia.aon.gwt.payroll.shared.Cost currentCost = getCurrentCost();

		costs = getCosts();
		
		CostComparator costComparator = new CostComparator();
		
		Collections.sort(costs, costComparator);

		for (com.esferalia.aon.gwt.payroll.shared.Cost cost : costs) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dateListBox.addItem(DATE_FORMAT.format(date));
		}
		
		int index = Collections.binarySearch(costs, currentCost, costComparator);
		
		dateListBox.setSelectedIndex(index);
	}
	
	
	private com.esferalia.aon.gwt.payroll.shared.Cost getCurrentCost() {
		return costDocuments.getCosts().get(costDocuments.getCurrentIndex());
	}
	
	private List<com.esferalia.aon.gwt.payroll.shared.Cost> getCosts(){
		return costDocuments.getCosts();
	}
	
	
	private static class CostComparator implements Comparator<com.esferalia.aon.gwt.payroll.shared.Cost> {
		@Override
		public int compare(com.esferalia.aon.gwt.payroll.shared.Cost c1,
				com.esferalia.aon.gwt.payroll.shared.Cost c2) {
			int compare = c1.getYear() - c2.getYear();
			return compare == 0 ? c1.getMonth() - c1.getMonth() : compare ;
		}
	}
	

}
