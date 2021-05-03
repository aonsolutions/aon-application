package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunicaInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class WorkplaceComunica extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static WorkplaceComunicaUiBinder uiBinder = GWT.create(WorkplaceComunicaUiBinder.class);

	interface WorkplaceComunicaUiBinder extends UiBinder<Widget, WorkplaceComunica> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexEvenly();
		String headerStyle();
	}
	
	@UiField
	HTMLPanel centerContainer;
	
	@UiField
	Grid workplaceDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid workplaceDataTable;
	
	@UiField
	HTMLPanel footerOptionsToolbar;
	
	private Integer newId = -1;
	
	private Map<Integer, String> addresses;
	
	// --------------------------------------------------	 CONSTRUCTOR	--------------------------------------------------------

	public WorkplaceComunica() {
		initWidget(uiBinder.createAndBindUi(this));
		initFooterOptionsToolbar();
		initPreview();
		this.addresses = new HashMap<Integer, String>();
	}
	
	// --------------------------------------------------	   PREVIEW		--------------------------------------------------------
	
	public void setAddresses(Map<Integer, String> addresses) {
		this.addresses = addresses;
	}
	
	public void resetPreview() {
		initPreview();
	}
	
	private void initPreview() {
		workplaceDataTableHeader.clear();
		workplaceDataTableHeader.resize(0, 0);
		workplaceDataTable.clear();
		workplaceDataTable.resize(0, 0);
		workplaceDataTableHeader.resizeColumns(3);
		workplaceDataTable.resizeColumns(3);
		
		paintHeader();
		calculateScrollPanelHeight();
		setColumnWidth();
	}
	
	private void paintHeader() {
		int row = workplaceDataTableHeader.insertRow(workplaceDataTableHeader.getRowCount());
		Label description = new Label("DESCRIPCI\u00D3N");
		Label address = new Label("DIRECCI\u00D3N");
		Label blank = new Label("");
		
		description.addStyleName(style.headerStyle());
		address.addStyleName(style.headerStyle());
		
		workplaceDataTableHeader.setWidget(row, 0, description);
		workplaceDataTableHeader.setWidget(row, 1, address);
		workplaceDataTableHeader.setWidget(row, 2, blank);
	}
	
	private void calculateScrollPanelHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight/3) + "px");
	}
	
	private void setColumnWidth() {
		workplaceDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(45, Unit.PCT);
		workplaceDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PCT);
		workplaceDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(5, Unit.PCT);
		
		workplaceDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(45, Unit.PCT);
		workplaceDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PCT);
		workplaceDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(5, Unit.PCT);
	}
	
	// --------------------------------------------------	   INSERT ROWS		--------------------------------------------------------
	
	public void insertRow(WorkplaceComunicaInfo workplaceComunicaInfo) {
		int row = workplaceDataTable.insertRow(workplaceDataTable.getRowCount());
		
		TextBox descriptionTB = new TextBox();
		descriptionTB.getElement().getStyle().setWidth(95, Unit.PCT);
		descriptionTB.addValueChangeHandler(e -> {
			String newDescription = e.getValue();
			if(AonStringUtils.isNotBlank(newDescription)) {
				onInsertWorkplace(workplaceComunicaInfo.getId(), newDescription.trim(), workplaceComunicaInfo.getAddressId());
			}
			
		});
		descriptionTB.setValue(workplaceComunicaInfo.getDescription());
		
		ListBox addressLB = createAddressListBox();
		addressLB.addChangeHandler(e -> {
			Integer addressId = Integer.valueOf(addressLB.getSelectedValue());
			onInsertWorkplace(workplaceComunicaInfo.getId(), workplaceComunicaInfo.getDescription(), addressId);
		});
		setSelectedValueLB(addressLB, workplaceComunicaInfo.getAddressId().toString());
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flexEvenly());
		
		AonTableButton delete = new AonTableButton("Eliminar Centro de trabajo", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteWorkplace(workplaceComunicaInfo.getId());
				initPreview();
				onInsertRows();
			}
		});
		buttonsPanel.add(delete);
			
		workplaceDataTable.setWidget(row, 0, descriptionTB);
		workplaceDataTable.setWidget(row, 1, addressLB);
		workplaceDataTable.setWidget(row, 2, buttonsPanel);
		
	}
	
	private ListBox createAddressListBox() {
		ListBox addressListBox = new ListBox();
		addressListBox.setStyleName("aon-selectOneMenu");
		addressListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
		
		addressListBox.addItem("-", "-1");
		
		for(Entry<Integer, String> entry : this.addresses.entrySet())
			addressListBox.addItem(entry.getValue(), entry.getKey().toString());
		
		return addressListBox;
	}

	public Integer insertNewRow(Integer newId) {
		int row = workplaceDataTable.insertRow(workplaceDataTable.getRowCount());
		this.newId = newId;
		this.newId--;
		
		TextBox descriptionTB = new TextBox();
		descriptionTB.getElement().getStyle().setWidth(95, Unit.PCT);
		ListBox addressLB = createAddressListBox();
		
		descriptionTB.addValueChangeHandler(e -> {
			String newDescription = e.getValue();
			if(AonStringUtils.isNotBlank(newDescription)) {
				Integer addressId = Integer.valueOf(addressLB.getSelectedValue());
				onInsertWorkplace(newId, newDescription.trim(),addressId);
			}
			
		});
		
		addressLB.addChangeHandler(e -> {
			Integer addressId = Integer.valueOf(addressLB.getSelectedValue());
			onInsertWorkplace(newId, descriptionTB.getValue(), addressId);
		});
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flexEvenly());
		
		AonTableButton delete = new AonTableButton("Eliminar Centro de trabajo", AON.CSS.aonIconDelete());
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteWorkplace(newId);
				initPreview();
				onInsertRows();
			}
		});
		buttonsPanel.add(delete);
			
		workplaceDataTable.setWidget(row, 0, descriptionTB);
		workplaceDataTable.setWidget(row, 1, addressLB);
		workplaceDataTable.setWidget(row, 2, buttonsPanel);
		
		onInsertRow();
		
		return newId;
	}
	
	// --------------------------------------------------	   AUX METHODS		--------------------------------------------------------
	
	public int getRowCount() {
		return workplaceDataTable.getRowCount();
	}

	public Widget getWidget(int row, int column) {
		return workplaceDataTable.getWidget(row, column);
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	// --------------------------------------------------	   ABSTRACT METHODS		--------------------------------------------------------
	
	protected abstract void onInsertRow();
	
	protected abstract void onInsertRows();

	protected abstract void onDeleteWorkplace(Integer workplaceId);

	protected abstract void onInsertWorkplace(Integer workplaceId, String description, Integer addressId);

	// --------------------------------------------------	   FOOTER PANEL		--------------------------------------------------------
	
	private void initFooterOptionsToolbar() {
		footerOptionsToolbar.clear();
		
		AonTableButton newCCCBtn = new AonTableButton("Nuevo Centro de trabajo",  AON.CSS.aonIconAdd());
		newCCCBtn.addClickHandler(e -> {
			onAddNewWorkplace(e);
		});
		
		footerOptionsToolbar.add(newCCCBtn);
	}

	private void onAddNewWorkplace(ClickEvent e) {
		if(0 != workplaceDataTable.getRowCount()) {
			TextBox description = (TextBox) workplaceDataTable.getWidget(0, 0);
			ListBox addressLB = (ListBox) workplaceDataTable.getWidget(0, 1);
			Integer addressId = Integer.parseInt(addressLB.getSelectedValue());
			if(AonStringUtils.isNotBlank(description.getValue()) && addressId > 0) {
				this.newId = insertNewRow(this.newId);
			}
		}else
			this.newId = insertNewRow(this.newId);
	}

}
