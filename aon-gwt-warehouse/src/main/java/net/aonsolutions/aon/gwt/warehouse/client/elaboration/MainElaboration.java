package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;

public class MainElaboration extends AonTemplate {

	protected API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private MainElaboration me = this;

	public MainElaboration(AonData aonData, Boolean future) {
		this(aonData);
	}

	public MainElaboration(AonData aonData) {
		filterMap = new HashMap<>();
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(), aonData.getDomain().getName(),
				aonData.getUser().getLogin());
	}

	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(IronIconsElement.SRC, AonComboBoxElement.SRC, PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC, PaperItemElement.SRC));

		Polymer.whenReady(o -> {
			super.onModuleLoad();
			load();
			return null;
		});
	}

	private void load() {
		loadToolbar();
		loadWestContent();
		loadNorthContent();
		loadContent();
		loadSouthContent();
	}

	private void loadToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {

			@Override
			protected void reset() {
				Toolbar toolbar = (Toolbar) getToolbar().getWidget();
				toolbar.back.setVisible(true);
				toolbar.remove.setVisible(true);
				getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
				setNorthContent(new ElaborationPanel(me));
				setContent(new Label(""));
			}

			@Override
			protected void remove() {
				ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
				deleteElaboration(panel.getJsElaboration().getId());
				load();
			}

			@Override
			protected void back() {
				load();
			}

			@Override
			protected void download() {
				ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
				downloadElaboration(panel.getJsElaboration().getId());
			}
		};
		setToolbar(toolbar);
	}

	private void loadWestContent() {

	}

	private void loadNorthContent() {
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 85);
		setNorthContent(new FilterPanel(this));
	}

	public void loadContent() {
		setContent(null);
		API.getWarehouse().getElaborationList(filterMap, new AsyncCallback<JSON<JsElaboration>>() {

			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				getToolbar().setTitle(getToolbar().getTitle()+" - Lista");
				setContent(new GridPanel(me, result.getData().toLinkedList()));
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void loadSouthContent() {
		FootPanel fp = new FootPanel(this);
		setSouthContent(fp);
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 0);		
	}

	public void onSelectElaboration(JsElaboration js) {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.setTitle(toolbar.getTitle()+" - Edicion");
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(true);
		toolbar.download.setVisible(true);
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
		
		// NORTH
		setNorthContent(new ElaborationPanel(me, js));
		
		// CONTENT
		setContent(new ElaborationSelect(me, js));
		
		// FOOTER
		setSouthContent(new FootPanel(me, js));
	}
	
	public void changeSouthContentSize(Double value) {
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), value);	
	}
	
	// TODO update elaboration
	protected void updateElaboration() {
		Window.alert("update no imlementado");
//		if (jsElaboration != null) {
//			API.getWarehouse().updateElaboration(jsElaboration.getId(), getData(),
//					new AsyncCallback<JsElaboration>() {
//
//						@Override
//						public void onSuccess(JsElaboration result) {
//							jsElaboration = result;
//							number.setValue(result.getNumber() + "");
//							parent.setSelectContent(result);
//						}
//
//						@Override
//						public void onFailure(Throwable caught) {
//						}
//					});
//		} else {
//			this.API.getWarehouse().insertElaboration(getData(), new AsyncCallback<JsElaboration>() {
//
//				@Override
//				public void onSuccess(JsElaboration result) {
//					jsElaboration = result;
//					number.setValue(result.getNumber() + "");
//					parent.setSelectContent(result);
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//				}
//			});
//		}
	}
	
	// TODO delete elaboration
	protected void deleteElaboration(Integer elaboratinId) {
//		API.getWarehouse().deleteCarrierPacking(panel.getJsCarrierPacking().getId());
		Window.alert("delete no implementado");
	}
	
	// TODO create elaboration
	protected void createElaboration() {
		
	}
	
	// TODO download elaboration
	protected void downloadElaboration(Integer elaboratinId) {
//		API.getWarehouse().downloadPackingList(panel.getJsElaboration().getId());
		Window.alert("download no implementado");
	}
	
	private String getData() {
//		return "{\"series\":\"" + series.getSelectedValue() + "\"," + "\"number\":\"" + number.getValue() + "\","
//				+ "\"type\":\"" + type.getSelectedValue() + "\"," + "\"status\":\"" + status.getSelectedValue() + "\","
//				+ "\"issue_date\":\"" + (issueDate.getValue() != null ? issueDate.getValue().getTime() : "") + "\","
//				+ "\"delivery_date\":\"" + (deliveryDate.getValue() != null ? deliveryDate.getValue().getTime() : "")
//				+ "\"," + "\"carrier\":\"" + carrier.getSelectedValue() + "\"," + "\"carrier_reference\":\""
//				+ reference.getValue() + "\"," + "\"number_plate\":\"" + numberPlate.getValue() + "\","
//				+ "\"driver_document\":\"" + driverDocument.getValue() + "\"," + "\"driver_name\":\""
//				+ driverName.getValue() + "\"" + "}";
		return null;
	}

	private AonDialog createAddDialog() {
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

		return new AonDialog("Nueva elaboracion", v) {
			@Override
			protected void onCancel() {
				hide();
			}

			@Override
			protected void onAccept() {
				hide();
			}
		};
	}

	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}

	public void setEnableType(Boolean enable) {
//		ElaborationPanel cpp = (ElaborationPanel) getNorthContent().getWidget();
//		cpp.type.setEnabled(enable);
	}

	public void refreshSelect() {
		 ElaborationSelect cps = (ElaborationSelect)
		 getContent().getWidget();
		 cps.refresh();
	}

	public void refreshSouth(JsElaboration elaboration) {
		ElaborationCommentsSouth ecs = (ElaborationCommentsSouth)
		getSouthContent().getWidget();
		ecs.refresh(elaboration);
	}
	
	public JsElaboration getJsElaboration(){ 
		ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
		return w.getJsElaboration();
	}
	
	public void setJsElaboration(JsElaboration js){ 
		ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
		w.setJsElaboration(js);
	}
	
}
