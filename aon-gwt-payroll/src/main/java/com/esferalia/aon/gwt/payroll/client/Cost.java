package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Cost extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	interface Binder extends UiBinder<Widget, Cost> {
	}
	
	static interface Listener {
		void onPublish(CostDocuments documents);
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private class TypeCommand implements ScheduledCommand {

		private Salary.Type type;
		private MenuItem menuItem;

		private TypeCommand(MenuItem menuItem, Salary.Type type) {
			this.type = type;
			this.menuItem = menuItem;
			this.menuItem.setScheduledCommand(this);
		}

		@Override
		public void execute() {
			try {
				if (!costDocuments.containsType(type)) {
					costDocuments.addType(type);
					setCheckedStyle(menuItem, true);
				} else {
					costDocuments.removeType(type);
					setCheckedStyle(menuItem, false);
				}
				getAsHTML();
			} catch (Exception e) {
				Window.alert(e.getMessage());
			}
		}

	}

	private class TypeValueChangeHandler implements ValueChangeHandler<Boolean> {

		private Salary.Type type;
		private CheckBox checkBox;

		private TypeValueChangeHandler(CheckBox checkBox, Salary.Type type) {
			this.type = type;
			this.checkBox = checkBox;
			this.checkBox.addValueChangeHandler(this);
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if (!costDocuments.containsType(type)) {
				costDocuments.addType(type);
			} else {
				costDocuments.removeType(type);
			}
			getAsHTML();
		}

	}

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
	MenuItem salaryMenuItem;
	@UiField
	MenuItem extraMenuItem;
	@UiField
	MenuItem settleMenuItem;
	@UiField
	MenuItem delayMenuItem;
	@UiField
	MenuItem publishMenuItem;

	@UiField
	CheckBox salaryCheckBox;
	@UiField
	CheckBox extraCheckBox;
	@UiField
	CheckBox settleCheckBox;
	@UiField
	CheckBox delayCheckBox;

	@UiField
	ListBox dateListBox;

	@UiField
	Label titleLabel;


	private int zoom = DEFAULT_ZOOM;

	private CostDocuments costDocuments;
	
	private List<Listener> listeners;

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
//				IDocument document = costDocuments.current();
//				document.download("xls");
//				Window.alert(costDocuments.geCurrentCost().toString());
				String printURL = GWT.getModuleBaseURL()+ "/cost_excel/"
						+ "?month=" + costDocuments.geCurrentCost().getMonth()
			            + "&year=" + costDocuments.geCurrentCost().getYear()
			            + "&enterpriseId=" + costDocuments.geCurrentCost().getEnterpriseId()
			            + "&workplaceId=" + costDocuments.geCurrentCost().getWorkplaceId();
				Window.open(printURL, "_blank", null);
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
		
		publishMenuItem.setScheduledCommand( new ScheduledCommand() {
			@Override
			public void execute() {
				onPublish(costDocuments);
			}
		});

		new TypeCommand(salaryMenuItem, Salary.Type.SALARY);
		new TypeCommand(extraMenuItem, Salary.Type.EXTRA);
		new TypeCommand(settleMenuItem, Salary.Type.SETTLE);
		new TypeCommand(delayMenuItem, Salary.Type.DELAY);
		
		new TypeValueChangeHandler(salaryCheckBox, Salary.Type.SALARY);
		new TypeValueChangeHandler(extraCheckBox, Salary.Type.EXTRA);
		new TypeValueChangeHandler(settleCheckBox, Salary.Type.SETTLE);
		new TypeValueChangeHandler(delayCheckBox, Salary.Type.DELAY);
		
		listeners = new LinkedList<Listener>(); 

	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		titleLabel.setText(title);
	}

	/**
	 * 
	 */
	public void setCostDocuments(CostDocuments costDocuments) {

		this.costDocuments = costDocuments;
		onCostDocumentsChanged();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	@UiHandler("publishButton")
	void onPublisButtonClick(ClickEvent event) {
		onPublish(costDocuments);
	}
	
	
	void onPublish(CostDocuments documents) {
		for (Listener listener : listeners)
			listener.onPublish(documents);
	}
	

	private void getAsHTML() {
		costDocuments.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				container.setHTML(html);
				syncTypeCheckBoxes();
				syncTypeMenuItems();
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
		for (com.esferalia.aon.gwt.payroll.shared.Cost cost : costDocuments
				.getCosts()) {
			Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
			dateListBox.addItem(DATE_FORMAT.format(date));
		}
		dateListBox.setSelectedIndex(costDocuments.getCurrentIndex());
	}

	private void syncTypeMenuItems() {
		setCheckedStyle(salaryMenuItem,
				costDocuments.containsType(Salary.Type.SALARY));
		setCheckedStyle(extraMenuItem,
				costDocuments.containsType(Salary.Type.EXTRA));
		setCheckedStyle(settleMenuItem,
				costDocuments.containsType(Salary.Type.SETTLE));
		setCheckedStyle(delayMenuItem,
				costDocuments.containsType(Salary.Type.DELAY));
	}

	private void syncTypeCheckBoxes() {
		salaryCheckBox.setValue(costDocuments.containsType(Salary.Type.SALARY), false);;
		extraCheckBox.setValue(costDocuments.containsType(Salary.Type.EXTRA), false);;
		settleCheckBox.setValue(costDocuments.containsType(Salary.Type.SETTLE), false);;
		delayCheckBox.setValue(costDocuments.containsType(Salary.Type.DELAY), false);;
	}

	private void syncFormatsButtons() {
		String formats[] = costDocuments.getSupportedFormats();
		Arrays.sort(formats);

		excelButton.setVisible(Arrays.binarySearch(formats, "xls") >= 0);
	}

	private void setCheckedStyle(MenuItem menuItem, boolean checked) {
		if (checked) {
			menuItem.addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			menuItem.removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}

}
