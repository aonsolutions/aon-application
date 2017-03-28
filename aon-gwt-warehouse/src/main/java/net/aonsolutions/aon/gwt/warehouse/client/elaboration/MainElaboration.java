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
			protected void accept() {
				this.download.setVisible(true);
				JsElaboration js = getJsElaboration();
				if(js.getId()!=null){
					updateElaboration(js);
				} else {
					insertElaboration(js);
				}
//				onSelectElaboration(js);
			}

			@Override
			protected void reset() {
				this.accept.setVisible(true);
				this.back.setVisible(true);
				this.remove.setVisible(false);
				this.reset.setVisible(false);
				this.download.setVisible(false);
				this.subtitle.setText("Nuevo");
				resetElaboration();
			}

			@Override
			protected void remove() {
				deleteElaboration(getJsElaboration().getId());
				load();
			}

			@Override
			protected void back() {
				this.accept.setVisible(false);
				load();
			}

			@Override
			protected void download() {
				downloadElaboration(getJsElaboration().getId());
			}
		};
		
		toolbar.back.setVisible(false);
		toolbar.accept.setVisible(false);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(false);
		toolbar.title.setText("Elaboraciones");
		toolbar.subtitle.setText("Lista");
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
		toolbar.accept.setVisible(true);
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(true);
		toolbar.download.setVisible(true);
		toolbar.subtitle.setText("Edicion");
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
		
		setNorthContent(new ElaborationPanel(me, js));
		setContent(new ElaborationSelect(me, js));
		setSouthContent(new FootPanel(me, js));
	}
	
	private void resetElaboration() {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.accept.setVisible(true);
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(false);
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
				
		API.getWarehouse().createElaboration(new AsyncCallback<JSON<JsElaboration>>() {
			
			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				ElaborationPanel panel = new ElaborationPanel(me, result.getOneData());
				setNorthContent(panel);
				setContent(new Label(""));
				loadSouthContent();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error resetear. \n"+caught.getMessage());
			}
		});
	}
	
	public void changeSouthContentSize(Double value) {
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), value);	
	}
	
	private void updateElaboration(JsElaboration jsElaboration) {
		API.getWarehouse().updateElaboration(jsElaboration.getId(), getData(), new AsyncCallback<JsElaboration>() {

			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}
	
	private void deleteElaboration(Integer elaboratinId) {
		API.getWarehouse().deleteElaboration(elaboratinId, getData(), new AsyncCallback<JsElaboration>() {

			@Override
			public void onSuccess(JsElaboration result) {
				Toolbar toolbar = (Toolbar) getToolbar().getWidget();
				toolbar.back();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al eliminar. \n"+caught.getMessage());
			}
		});
	}
	
	private void insertElaboration(JsElaboration jsElaboration) {
		API.getWarehouse().insertElaboration(getData(), new AsyncCallback<JsElaboration>() {

			@Override
			public void onSuccess(JsElaboration result) {
				ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
				panel.setJsElaboration(result);
				onSelectElaboration(panel.getJsElaboration());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
			}
		});
	}
	
	protected void downloadElaboration(Integer elaboratinId) {
		API.getWarehouse().downloadElaboration(getJsElaboration().getId());
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
		
//		String data = JsonUtils.stringify(getJsElaboration());
		ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
		String data = "{\"series\":\"" + panel.series.getValue() + "\"," 
				+ "\"number\":\"" + panel.number.getValue() + "\","
				+ "\"status\":\"" + 0 + "\","
				+ "\"date\":\"" + (panel.date.getValue() != null ? panel.date.getValue().getTime() : "") + "\","
				+ "\"quantity\":\"" + panel.quantity.getValue() + "\","
				+ "\"item\":\"" + panel.pBox.getId() + "\","
				+ "\"warehouse\":\"" + panel.warehouse.getSelectedValue() + "\","
				+ "\"comments\":\"" + getJsElaboration().getComments() + "\"" + "}";
		return data;
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

//	public void refreshSouth(JsElaboration elaboration) {
//		ElaborationCommentsSouth ecs = (ElaborationCommentsSouth)
//		getSouthContent().getWidget();
//		ecs.refresh(elaboration);
//	}
	
	public JsElaboration getJsElaboration(){ 
		ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
		return w.getJsElaboration();
	}
	
	public void setJsElaboration(JsElaboration js){ 
		ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
		w.setJsElaboration(js);
	}
	
}
