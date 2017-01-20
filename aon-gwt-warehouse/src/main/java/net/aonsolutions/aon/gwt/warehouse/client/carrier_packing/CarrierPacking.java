package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Arrays;
import java.util.HashMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class CarrierPacking extends AonTemplate{

	protected API API;
	private HashMap<String, String[]> filterMap;
	private Boolean future = false;
	
	public CarrierPacking(AonData aonData, Boolean future) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
		this.future = future; 
	}
	
	public CarrierPacking(AonData aonData) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				//PaperInputElement.SRC,
				//AonComboBoxElement.SRC,
				PaperIconButtonElement.SRC
		));
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		northContent();
		content();
		southContent();
	}

	private void toolbar() {
		if(future){
			AonToolbar toolbar = new AonToolbar("Carrier Packing") {
			
				@Override protected void onTitleClick() {}
				@Override protected void onStatsButtonClick() {}	
				@Override protected void onRefreshButtonClick() {}
				@Override protected void onMoreOptionButtonClick() {}
				@Override protected void onMenuButtonClick() {}
				@Override protected void onInfoButtonClick() {}
				@Override protected void onFastFilterButtonClick() {}
				@Override protected void onEditButtonClick() {}
				@Override protected void onDownloadButtonClick() {}
				@Override protected void onDeleteButtonClick() {}
				@Override protected void onAddButtonClick() {
					AonDialog dialog = createAddDialog();
					dialog.getElement().getStyle().setWidth(310, Unit.PX);
					dialog.center();
				}			
			}.setVisibleAllButton(false)
			 .setVisibleAddButton(true);
			setToolbar(toolbar);
		} else {
			getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
			Toolbar toolbar = new Toolbar() {
			
				@Override protected void reset() {
					Toolbar toolbar = (Toolbar) getToolbar().getWidget();
					toolbar.back.setVisible(true);
					toolbar.remove.setVisible(true);
					setNorthContent(new CarrierPackingPanel(API));
					setContent(new Label(""));
				}
				@Override protected void remove() {
					CarrierPackingPanel w = (CarrierPackingPanel) getNorthContent().getWidget();
					API.getWarehouse().deleteCarrierPacking(w.getJsCarrierPacking().getId());
					startApplication();
				}
				@Override protected void back() {
					startApplication();
				}
			};
			setToolbar(toolbar);
		}
	}

	private void westContent(){
		
	}
	
	private void northContent(){
		setNorthContent(new FilterPanel(this));
		setContent(new Label(""));
	}
	
	public void carrierPackingContent(JsCarrierPacking js){
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(true);
		setNorthContent(new CarrierPackingPanel(API,js));
		setContent(new Label(""));
	}
	
	public void content(){
		setContent(new Grid(this));
	}
	
	private void southContent(){
		
	}
	
	private AonDialog createAddDialog(){
		VerticalPanel v = new VerticalPanel();
		
		HorizontalPanel hp = new HorizontalPanel();
		
		PaperInput series = new PaperInput();
		series.setLabel(AON.MSG.series());
		hp.add(series);
		
		hp.add(new Label("-"));
		
		PaperInput number = new PaperInput();
		number.setLabel(AON.MSG.number());
		hp.add(number);
		
		v.add(hp);
		
		AonComboBox type = new AonComboBox();
		type.setLabel(AON.MSG.type());
		type.setItemLabelPath("name");
		type.setItemValuePath("name");
		v.add(type);
		
		AonComboBox status = new AonComboBox();
		status.setLabel(AON.MSG.status());
		status.setItemLabelPath("name");
		status.setItemValuePath("name");
		v.add(status);
		
		PaperInput carrier = new PaperInput();
		carrier.setLabel(AON.MSG.carrier());
		v.add(carrier);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		
		PaperInput issueDate = new PaperInput();
		issueDate.setLabel("Fecha de Solicitud");
		hp2.add(issueDate);
		
		hp2.add(new Label("-"));
	
		PaperInput deliveryDate = new PaperInput();
		deliveryDate.setLabel("Fecha de Entrega");
		hp2.add(deliveryDate);
		
		v.add(hp2);
		
		return new AonDialog("Nuevo Carrier Packing", v) {
			@Override protected void onCancel() {hide();}
			
			@Override protected void onAccept() {hide();}
		};
	}
	
	public HashMap<String, String[]> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, String[]> filterMap) {
		this.filterMap = filterMap;
	}
}
