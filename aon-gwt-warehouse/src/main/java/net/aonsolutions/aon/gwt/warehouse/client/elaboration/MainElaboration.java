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
import com.google.gwt.dom.client.Style.Unit;
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
		super.onModuleLoad();
		Polymer.importHref(Arrays.asList(IronIconsElement.SRC, AonComboBoxElement.SRC, PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC, PaperItemElement.SRC)
			);
		Polymer.whenReady(o -> {
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
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
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
		FooterPanel fp = new FooterPanel(this);
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
		setSouthContent(new FooterPanel(me, js));
	}
	
	private void resetElaboration() {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.accept.setVisible(true);
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(false);
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
		
		FooterPanel footer = (FooterPanel) getSouthContent().getWidget();
		footer.sourcePanel.setVisible(false);
				
		API.getWarehouse().createElaboration(new AsyncCallback<JSON<JsElaboration>>() {
			
			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				JsElaboration js = result.getOneData();
				ElaborationPanel panel = new ElaborationPanel(me, result.getOneData());
				setNorthContent(panel);
				setContent(new Label(""));
				setSouthContent(new FooterPanel(me, js));
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
	
	protected void closeElaboration(JsElaboration jsElaboration) {
		API.getWarehouse().updateElaboration(jsElaboration.getId(), getCloseData(), new AsyncCallback<JsElaboration>() {
			
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
		AonDialog dialog = new AonDialog("Solicitud de confirmaci\u00f3n", new Label("\u00bfBorrar\u003f")) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				API.getWarehouse().deleteElaboration(elaboratinId, getData(), new AsyncCallback<JsElaboration>() {
					
					@Override
					public void onSuccess(JsElaboration result) {
						hide();
						Toolbar toolbar = (Toolbar) getToolbar().getWidget();
						toolbar.back();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Ha ocurrido algun error al eliminar. \n"+caught.getMessage());
					}
				});
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();		
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
//		String data = JsonUtils.stringify(getJsElaboration());
		ElaborationPanel main = getElaborationPanel();
		FooterPanel footer = getFooterPanel();
		String comments = footer!=null?footer.comments.getValue():""; 
		String data = "{\"series\":\"" + main.series.getValue()+ "\""
				+ ",\"number\":\"" + main.number.getValue()+ "\""
				+ ",\"date\":\"" + (main.date.getValue() != null ? main.date.getValue().getTime() : "")+ "\""
				+ ",\"quantity\":\"" + main.quantity.getValue()+ "\""
				+ ",\"item\":\"" + main.itemBox.getId()+ "\""
				+ ",\"description\":\"" + main.descriptionLabel.getText()+ "\""
				+ ",\"warehouse\":\"" + main.warehouse.getSelectedValue()+ "\""
				+ ",\"comments\":\"" + (comments!=null && !"".equals(comments.trim())?comments:"") + "\""
				+ "}";
		return data;
	}
	
	private String getCloseData() {
		String data = "{\"status\":\"CLOSED\"}";
		return data;
	}
	
	private ElaborationPanel getElaborationPanel() {
		ElaborationPanel panel = (ElaborationPanel) getNorthContent().getWidget();
		return panel;
	}
	
	private FooterPanel getFooterPanel() {
		FooterPanel panel = (FooterPanel) getSouthContent().getWidget();
		return panel;
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
