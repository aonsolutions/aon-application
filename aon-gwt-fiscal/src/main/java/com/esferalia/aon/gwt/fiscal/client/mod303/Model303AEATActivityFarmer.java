package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.FarmerIVA;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Model303AEATActivityFarmer extends DockLayoutPanel implements HasValueChangeHandlers<Mod303ActivityFarmer> {
	
	private final Label epigraph = new Label();
	private final Label epigraphLabel = new Label();
	
	private boolean lastPeriod;
	private AonDoubleBox vol = new AonDoubleBox();
	private AonDoubleBox ind = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH,5);
	private AonDoubleBox cuo = new AonDoubleBox();
	private AonDoubleBox por = new AonDoubleBox();
	private AonDoubleBox ing = new AonDoubleBox();
	
	private AonDoubleBox sop = new AonDoubleBox();
	private AonDoubleBox cad = new AonDoubleBox();
	
	public static interface IMod303ActivityFarmerCallback {
		Mod303ActivityFarmer getActivity();
		void onAccept(Mod303ActivityFarmer act);
		void onCancel();
		void onRemove();
	}
	
	protected Model303AEATActivityFarmer(final IMod303ActivityFarmerCallback cbk, boolean lastPeriod) {
		super(Unit.PX);
		this.lastPeriod = lastPeriod;
		setStyleName(AON.CSS.aonSelector());
		setWidth("700px");
		setHeight("280px");

		AonToolbar toolbarPanel = new AonToolbar("");
		addNorth(toolbarPanel, AonToolbar.HEIGTH);
		
		final AonToolbarButton accept = new AonToolbarButton(AON.MSG.saveAction(),AON.CSS.aonIconAccept());
		accept.addClickHandler(event -> cbk.onAccept(
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
					)
		);
		toolbarPanel.add(accept);
		
		final AonToolbarButton cancel = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconCancel());
		cancel.addClickHandler(event -> cbk.onCancel());
		toolbarPanel.add(cancel);
		
		final AonToolbarButton remove = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		remove.addClickHandler(event -> {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {
				@Override
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					cbk.onRemove();
				}
			});
		});
		toolbarPanel.add(remove);
		
		FlowPanel epigraphContainerPanel = new FlowPanel();
		epigraphContainerPanel.setStyleName(AON.CSS.aonPadding());
		epigraphContainerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBlockCenter());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBorder());
		epigraphContainerPanel.addStyleName(AON.CSS.aonFlexBlock());
		epigraphContainerPanel.addStyleName(AON.CSS.aonMarginBottom());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBackgroundLigthGray());

		epigraph.setStyleName(AON.CSS.aonBold());
		epigraph.setStyleName(AON.CSS.aonFontLarger());
		epigraph.getElement().getStyle().setWidth(60, Unit.PX);
		epigraphContainerPanel.add(epigraph);

		AonTableButton showEpigraphs = new AonTableButton(AON.MSG.epigraph() ,AON.CSS.aonIconSearch());
		epigraphContainerPanel.add(showEpigraphs);

		epigraphLabel.setStyleName(AON.CSS.aonFontLarger());
		epigraphLabel.addStyleName(AON.CSS.aonNowrap());
		epigraphLabel.addStyleName(AON.CSS.aonFlexGrow1());
		epigraphLabel.addStyleName(AON.CSS.aonMarginLeft());
		epigraphContainerPanel.add(epigraphLabel);

		final Model303AEATActivityFarmer2018 activityFarmer2016 = new Model303AEATActivityFarmer2018();
		activityFarmer2016.addSelectionHandler( new SelectionHandler<FarmerIVA>() {
			@Override
			public void onSelection(SelectionEvent<FarmerIVA> event) {
				FarmerIVA selected = event.getSelectedItem();
				if (AonStringUtils.isNotBlank( cbk.getActivity().getCode())) {
					AonConfirmDialog dialog = new AonConfirmDialog();
					dialog.confirm(AON.MSG.epigrapChanged(), new AonConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							// Nothing
						}
						
						@Override
						public void onAccept() {
							accept(selected);
						}
					});
				} else {
					accept(selected);
				}
			}
			
			private void accept(final FarmerIVA selected) {
				cbk.getActivity().initialize();
				cbk.getActivity().setCode(selected.getCode());
				cbk.getActivity().setDescription(selected.getDescription());
				cbk.getActivity().setInd(selected.getIndiceRendimientoNeto());
				cbk.getActivity().setPor(selected.getPorcentaje());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmer.this, cbk.getActivity());
			}
		});
		showEpigraphs.addClickHandler(event -> activityFarmer2016.onShow());
		
		addNorth(epigraphContainerPanel, 40);
		
		populateActivity(cbk.getActivity());
		
		ScrollPanel container = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		table.getColumnFormatter().setWidth(1, "140px");
		int row = 0;
		
		vol.addValueChangeHandler(event -> {
			if (vol.getValue() == null) vol.setValue(0.0,false);
			cbk.getActivity().setVol(vol.getValue());
			ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmer.this, cbk.getActivity());
		});
		table.setWidget(row, 0, new Label(AON.MSG.operationsVolume()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, vol);
		++row;
		
		ind.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.page6E()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, ind);
		++row;
		
		cuo.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.f04Msg()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, cuo);
		++row;
		
		if (!this.lastPeriod) {
			por.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.incomePercent()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, por);
			++row;
			
			ing.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.income()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, ing);
			++row;
		} else {
			sop.addValueChangeHandler(event -> {
				if (sop.getValue() == null) sop.setValue(0.0,false);
				cbk.getActivity().setSop(sop.getValue());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmer.this, cbk.getActivity());
			});
			table.setWidget(row, 0, new Label(AON.MSG.page6D()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, sop);
			++row;
			
			cad.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6J()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, cad);
			++row;
		}
		
		container.add(table);
		
		add(container);
		onResize();
	}
	
	public void populateActivity(Mod303ActivityFarmer act) {
		epigraph.setText(act.getCode());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),80));
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
