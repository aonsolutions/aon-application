package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfResult;
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

public class Irpf extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM =  25;
	private static final int MAX_ZOOM =  500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	interface Binder extends UiBinder<Widget, Irpf> {
	}


	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	HTML container;

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

	private IrpfDocuments irpfDocuments;

	public Irpf() {
		initWidget(binder.createAndBindUi(this));

		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSalaryDateChanged();
			}
		});
	

		printButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = irpfDocuments.current();
				document.print();
			}
		});

		printMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = irpfDocuments.current();
				document.print();
			}
		});
		
		downloadMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = irpfDocuments.current();
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
	public void setIrpfDocuments(IrpfDocuments irpfDocuments) {
		this.irpfDocuments = irpfDocuments;
		onIrpfDocumentsChanged();
	}

	private void getAsHTML() {
		irpfDocuments.getAsHTML(zoom, new AsyncCallback<String>() {
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

	private void onIrpfDocumentsChanged() {
		getAsHTML();
		syncIrpfDateListBox();
	}

	private void onSalaryDateChanged() {
		int selected = dateListBox.getSelectedIndex();
		irpfDocuments.setCurrentIndex(selected);
		getAsHTML();
	}
	

	private void syncIrpfDateListBox() {
		dateListBox.clear();
		for (com.esferalia.aon.gwt.payroll.shared.Irpf irpf : irpfDocuments.getIrpfs()) {
			IrpfResult irpfResult = irpf.getIrpfResult();
			dateListBox.addItem(DATE_FORMAT.format(irpfResult.getEffectiveDate()));
		}
		dateListBox.setSelectedIndex(irpfDocuments.getCurrentIndex());
	}
	
	

}
