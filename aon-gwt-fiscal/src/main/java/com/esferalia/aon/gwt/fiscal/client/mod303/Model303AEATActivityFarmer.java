package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.FarmerIVA;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Model303AEATActivityFarmer extends DockLayoutPanel implements HasValueChangeHandlers<Mod303ActivityFarmer> {
	
	private final Label epigraph = new Label();
	private final Label epigraphLabel = new Label();
	
	private DoubleBox vol = new DoubleBox();
	private DoubleBox ind = new DoubleBox(DoubleBox.VISIBLE_LENGTH,5);
	private DoubleBox cuo = new DoubleBox();
	private DoubleBox por = new DoubleBox();
	private DoubleBox ing = new DoubleBox();
	
	private DoubleBox sop = new DoubleBox();
	private DoubleBox cad = new DoubleBox();
	
	public static interface IMod303ActivityFarmerCallback {
		Mod303ActivityFarmer getActivity();
		void onAccept(Mod303ActivityFarmer act);
		void onCancel();
		void onRemove();
	}
	
	public Model303AEATActivityFarmer(final IMod303ActivityFarmerCallback cbk) {
		super(Unit.PX);
		setStyleName(AON.AON_CSS.aonSelector());
		setWidth("700px");
		setHeight("280px");

		FlowPanel headerPanel = new FlowPanel();
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(AON.MSG.activity()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.setWidget(0, 1, new Label(AON.MSG.additionalData()));
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());

		final Button accept = new Button();
		accept.setText(AON.MSG.saveAction());
		accept.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		accept.addStyleName(AON.AON_CSS.aonIconSave());
		accept.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.onAccept(
					new Mod303ActivityFarmer()
						.setCode(epigraph.getText())
						.setDescription(epigraphLabel.getText())
						.setVol(vol.getValue())
						.setInd(ind.getValue())
						.setCuo(cuo.getValue())
						.setPor(por.getValue())
						.setIng(ing.getValue())
						.setSop(sop.getValue())
						.setCad(cad.getValue())
						);
			}
		});
		buttonContainer.add(accept);
		
		final Button cancel = new Button();
		cancel.setText(AON.MSG.cancelAction());
		cancel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancel.addStyleName(AON.AON_CSS.aonIconCancel());
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.onCancel();
			}
		});
		buttonContainer.add(cancel);
		
		final Button remove = new Button();
		remove.setText(AON.MSG.deleteAction());
		remove.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		remove.addStyleName(AON.AON_CSS.aonIconDelete());
		remove.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog dialog = new ConfirmDialog();
				dialog.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					@Override
					public void onCancel() {
					}
					
					@Override
					public void onAccept() {
						cbk.onRemove();
					}
				});
			}
		});
		buttonContainer.add(remove);
		
		toolbarPanel.add(toolbar);
		headerPanel.add(toolbarPanel);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonDataTable());
		tab.addStyleName(AON.AON_CSS.aonBorderBottom());
		epigraph.setStyleName(AON.AON_CSS.aonBold());
		epigraph.setStyleName(AON.AON_CSS.aonFontBig());
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonWidth80());
		tab.setWidget(0, 0, epigraph);
		Button activitiesButton = new Button();
		activitiesButton.setStyleName(AON.AON_CSS.aonIconLoupe());
		activitiesButton.addStyleName(AON.AON_CSS.aonBorderNone());
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonWidth20());
		tab.setWidget(0, 1, activitiesButton);
		
		epigraphLabel.setStyleName(AON.AON_CSS.aonFontBig());
		epigraphLabel.setStyleName(AON.AON_CSS.aonNowrap());
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonWidthAuto());
		tab.setWidget(0, 2, epigraphLabel);
		
		final Model303AEATActivityFarmer2016 activityFarmer2016 = new Model303AEATActivityFarmer2016(new Model303AEATActivityFarmer2016.SelectionCallBack() {
			
			@Override
			public void onSelect(FarmerIVA selected) {
				if (AonStringUtils.isNotBlank( cbk.getActivity().getCode())) {
					ConfirmDialog dialog = new ConfirmDialog();
					dialog.confirm(AON.MSG.epigrapChanged(), new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {}
						
						@Override
						public void onAccept() {
							accept(selected);
						}
					});
				} else {
					accept(selected);
				}
			}
			
			@Override
			public void onClose() {}
			
			private void accept(final FarmerIVA selected) {
				cbk.getActivity().initialize();
				cbk.getActivity().setCode(selected.getCode());
				cbk.getActivity().setDescription(selected.getDescription());
				cbk.getActivity().setInd(selected.getIndiceRendimientoNeto());
				cbk.getActivity().setPor(selected.getPorcentaje());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmer.this, cbk.getActivity());
			}
		});
		
		activitiesButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				activityFarmer2016.onShow();
			}
		});
		flowPanel.add(tab);
		headerPanel.add(flowPanel);
		
		populateActivity(cbk.getActivity());
		addNorth(headerPanel, 80);
		
		ScrollPanel container = new ScrollPanel();

		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		table.getColumnFormatter().setWidth(1, "140px");
		int row = 0;
		
		vol.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				cbk.getActivity().setVol(vol.getValue());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmer.this, cbk.getActivity());
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.operationsVolume()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(0, 1, vol);
		++row;
		
		ind.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.page6E()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, ind);
		++row;
		
		cuo.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.f04Msg()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, cuo);
		++row;
		
		por.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.incomePercent()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, por);
		++row;
		
		ing.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.income()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, ing);
		++row;
		container.add(table);
		
		add(container);
		onResize();
	}
	
	public void populateActivity(Mod303ActivityFarmer act) {
		epigraph.setText(act.getCode());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),100));
		epigraphLabel.setTitle(act.getDescription());
		vol.setValue(act.getVol());
		ind.setValue(act.getInd());
		cuo.setValue(act.getCuo());
		por.setValue(act.getPor());
		ing.setValue(act.getIng());
		sop.setValue(act.getSop());
		cad.setValue(act.getCad());
		vol.setEnabled(AonStringUtils.isNotBlank( act.getCode() ) );
		if (vol.isEnabled()) {
			vol.selectAll();
			vol.setFocus(true);
		}
			
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303ActivityFarmer> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
}
