package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
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
import com.vaadin.polymer.paper.widget.PaperInput;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class Elaboration extends AonTemplate {

	protected API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private Boolean future = false;
	private Elaboration me = this;

	public Elaboration(AonData aonData, Boolean future) {
		this(aonData);
		this.future = future;
	}

	public Elaboration(AonData aonData) {
		filterMap = new HashMap<>();
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(), aonData.getDomain().getName(),
				aonData.getUser().getLogin());
	}

	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(IronIconsElement.SRC));
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
		if (future) {
			AonToolbar toolbar = new AonToolbar("Elaboration") {

				@Override
				protected void onTitleClick() {
				}

				@Override
				protected void onStatsButtonClick() {
				}

				@Override
				protected void onRefreshButtonClick() {
				}

				@Override
				protected void onMoreOptionButtonClick() {
				}

				@Override
				protected void onMenuButtonClick() {
				}

				@Override
				protected void onInfoButtonClick() {
				}

				@Override
				protected void onFastFilterButtonClick() {
				}

				@Override
				protected void onEditButtonClick() {
				}

				@Override
				protected void onDownloadButtonClick() {
				}

				@Override
				protected void onDeleteButtonClick() {
				}

				@Override
				protected void onAddButtonClick() {
					AonDialog dialog = createAddDialog();
					dialog.getElement().getStyle().setWidth(310, Unit.PX);
					dialog.center();
				}
			}.setVisibleAllButton(false).setVisibleAddButton(true);
			setToolbar(toolbar);
		} else {
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
					ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
					// TODO Toolbar::remove
					// API.getWarehouse().deleteCarrierPacking(w.getJsCarrierPacking().getId());
					startApplication();
				}

				@Override
				protected void back() {
					startApplication();
				}

				@Override
				protected void download() {
					ElaborationPanel w = (ElaborationPanel) getNorthContent().getWidget();
					// TODO Toolbar::download
//					API.getWarehouse().downloadPackingList(w.getJsCarrierPacking().getId());
					Window.alert("desarrollo en curso");
				}
			};
			setToolbar(toolbar);
		}
	}

	private void westContent() {

	}

	private void northContent() {
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 85);
		setNorthContent(new FilterPanel(this));
	}

	public void elaborationContent(JsElaboration js) {
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(true);
		toolbar.download.setVisible(true);
//		toolbar.packingList.setVisible(true);
//		toolbar.sendPackingList.setVisible(true);
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
		setNorthContent(new ElaborationPanel(me,js));
		setContent(new ElaborationSelect(me, js));
//		setParameterPanel(js);
//		isMainScreem = false;
	}

	public void setSelectContent(JsElaboration js) {
		setContent(new ElaborationSelect(me, js));
	}

	public void content() {
		API.getWarehouse().getElaborationList(filterMap, new AsyncCallback<JSON<JsElaboration>>() {

			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				setContent(new Grid(me, result.getData().toLinkedList()));
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void southContent() {
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 0);
	}

	public void southContent(JsElaboration js, AonJsArray<JsElaborationDetail> details) {
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 300);
//		if (js.getType().getName().equalsIgnoreCase(CarrierPackingType.SHIPMENT_REQUEST.getName())) {
			// TODO setSouthContent(new CarrierPackingSouth2(me, js, p, details));
//		} else {
			// TODO setSouthContent(new CarrierPackingSouth(p, details));
//		}
//		setSouthContent(new ElaborationSouth(p, details));
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

	public void refreshSouth2(JsElaboration order) {
//		ElaborationSouth2 cps = (ElaborationSouth2)
//		getSouthContent().getWidget();
//		cps.refresh(order);
	}
}
