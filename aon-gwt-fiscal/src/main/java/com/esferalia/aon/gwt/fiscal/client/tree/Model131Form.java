package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.EnumMap;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.PercentBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model131Form extends ResizeComposite implements FiscalNodeWidget<Mod131>{

	interface Model131FormBinder extends
			UiBinder<Widget, Model131Form> {
	}

	private static TreeMap<Mod131Key, Mod131Key[]> KEYS = new TreeMap<Mod131Key, Mod131Key[]>();
	static {
		KEYS.put(Mod131Key.H2, new Mod131Key[]{
			Mod131Key.C03,Mod131Key.C04});
		KEYS.put(Mod131Key.H3, new Mod131Key[]{
			Mod131Key.C05,Mod131Key.C06});
		KEYS.put(Mod131Key.H4, new Mod131Key[]{
			Mod131Key.C07,Mod131Key.C08,Mod131Key.C09,Mod131Key.C10,
			Mod131Key.C11,Mod131Key.C12,Mod131Key.C13,Mod131Key.C14,
			Mod131Key.C15});
	}
	private EnumMap<Mod131Key, DoubleBox> inputs = new EnumMap<Mod131Key, DoubleBox>(Mod131Key.class);	

	private static final Model131FormBinder panelBinder = GWT.create(Model131FormBinder.class);
	
	TreeNode<Mod131> node;
	 
	@UiField
	Button saveButton;
	@UiField
	Button calculateButton;
	
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	Button status;

	@UiField
	Label period;
	
	@UiField
	Label year;
	
	@UiField
	SimplePanel activitiesTablePanel;

	public Model131Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	public void select(TreeNode<Mod131> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod131(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<Mod131>() {

						@Override
						public void onSuccess(Mod131 result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(node.getTreeObject());
		}
	}

	private void populate(Mod131 mod131) {
		node.setTreeObject( mod131 );
		saveButton.setEnabled(isEnabled(mod131));
		calculateButton.setEnabled(isEnabled(mod131));
		year.setText( Integer.toString( mod131.getYear() ));
		period.setText(mod131.getPeriod().getName());
		
		document.setValue(mod131.getDocument());
		document.setEnabled(false);
		name.setValue(mod131.getName());
		name.setEnabled(false);
		surname.setValue(mod131.getSurname());
		surname.setEnabled(false);
		status.setText(mod131.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod131.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		paintActivitiesTables(mod131);
	}
	

	private void paintActivitiesTables(final Mod131 mod131) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( Mod131Key.ACH1.getDescription() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		row++;
		table.setWidget(row, 0, new Label(AON.MSG.epigraph() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.setWidget(row, 1, new Label(AON.MSG.netYield()));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth150());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		table.setWidget(row, 2, new Label(AON.MSG.appliedPercent()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth80());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		table.setWidget(row, 3, new Label(AON.MSG.result()));
		table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonWidth150());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		
		for (final Mod131Activity act : mod131.getActivities() ) {
			row++;
			table.setWidget(row, 0, new Label(act.getEpigraph() ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());

			DoubleBox netYield = new DoubleBox();
			netYield.setValue(act.getNetYield());
			netYield.setEnabled(false);
			table.setWidget(row, 1, netYield );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			
			PercentBox percent = new PercentBox();
			percent.setValue(act.getPercent());
			percent.setEnabled(false);
			table.setWidget(row, 2, percent );
			table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());

			FlowPanel r01 = new FlowPanel();
			DoubleBox result = new DoubleBox();
			result.setValue(act.getResult());
			result.setEnabled(false);
			r01.add(result);
			Button detail = new Button();
			detail.setStyleName(AON.AON_CSS.aonIconView());
			detail.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					showActivity(mod131,act);
				}

			});
			r01.add(detail);
			table.setWidget(row, 3, r01);
			table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		row++;
		
		table.setWidget(row, 0, new Label( AON.MSG.netYieldSum()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		
		FlowPanel p01 = new FlowPanel();
		p01.setStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel l01 = new InlineLabel( Mod131Key.AC01.getBox() );
		l01.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
		p01.add(l01);
		DoubleBox c01 = new DoubleBox();
		c01.setValue(mod131.getC01());
		c01.setEnabled(false);
		inputs.put(Mod131Key.AC01, c01);
		p01.add(c01);
		table.setWidget(row, 1, p01 );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		
		
		table.setWidget(row, 2, new Label( "" ));
		table.getFlexCellFormatter().setColSpan(row, 2, 2);
		
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod131ResultSum()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);

		FlowPanel p02 = new FlowPanel();
		p02.setStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel l02 = new InlineLabel( Mod131Key.AC02.getBox() );
		l02.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
		p02.add(l02);
		DoubleBox c02 = new DoubleBox();
		c02.setValue(mod131.getC02());
		c02.setEnabled(false);
		p02.add(c02);
		inputs.put(Mod131Key.AC02, c02);
		table.setWidget(row, 1, p02 );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());

		for (Mod131Key key : KEYS.keySet()) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription() ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			for (Mod131Key subkey : KEYS.get(key)) {
				row++;
				table.setWidget(row, 0, new Label( subkey.getDescription()));
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
				table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
				table.getFlexCellFormatter().setColSpan(row, 0, 3);

				FlowPanel p = new FlowPanel();
				p.setStyleName(AON.AON_CSS.aonNowrap());
				InlineLabel l = new InlineLabel( subkey.getBox() );
				l.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
				p.add(l);
				DoubleBox doubleBox = new DoubleBox();
				doubleBox.setValue(subkey.getValue(mod131));
				doubleBox.setEnabled(isEnabled(subkey));
				p.add(doubleBox);
				inputs.put(subkey, doubleBox);
				doubleBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						calculate();
					}
				});
				
				table.setWidget(row, 1, p );
				table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			}
			
		}
		
		
		activitiesTablePanel.setWidget(table);
	}

	private boolean isEnabled(Mod131Key subkey) {
		if (node.getTreeObject().isFinished()) {
			return false;	
		} 
		return subkey != Mod131Key.C04 && subkey != Mod131Key.C06 
			&& subkey != Mod131Key.C07 && subkey != Mod131Key.C10 
			&& subkey != Mod131Key.C13 && subkey != Mod131Key.C15;
	}

	private boolean isEnabled(Mod131 mod131) {
		return ( mod131.getYear() >= 2015 );
	}


	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.calculateAction())) {
			calculate();
		}
	}
	
	private void calculate() {
		populateTreeObject();
		FiscalTree.FISCAL_SERVICE.calculateMod131(FiscalTree.getCurrentDomainName()
				,node.getTreeObject()
				,new AsyncCallback<Mod131>() {

					@Override
					public void onSuccess(Mod131 result) {
						populateTreeInputs(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(caught.getMessage());								
					}

		});
	}

	private void populateTreeObject() {
		for (Mod131Key key : inputs.keySet()) {
			key.setValue(node.getTreeObject(), inputs.get(key).getValue());
		}
	}
	private void populateTreeInputs(Mod131 result) {
		node.setTreeObject(result);
		for (Mod131Key key : inputs.keySet()) {
			inputs.get(key).setValue(key.getValue(result));
		}
	}
	
	@UiHandler("status")
	void onStatusButtonClick(ClickEvent event) {
		if (Window.confirm( node.getTreeObject().isFinished()?
				AON.MSG.reopen():AON.MSG.finish() )) {
			populateTreeObject();
			node.getTreeObject().setFinished(!node.getTreeObject().isFinished());
			FiscalTree.FISCAL_SERVICE.saveMod131(FiscalTree.getCurrentDomainName()
					,node.getTreeObject()
					,new AsyncCallback<Mod131>() {

						@Override
						public void onSuccess(Mod131 result) {
							populateTreeInputs(result);
							status.setText(node.getTreeObject().isFinished()?
									AON.MSG.finished():AON.MSG.pending() );
							status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
							status.addStyleName(node.getTreeObject().isFinished()?
									AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
							for (Mod131Key key : inputs.keySet()) {
								inputs.get(key).setEnabled(isEnabled(key));
							}
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());								
						}

			});
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			save();
		}
	}
	
	private void save() {
		
	}

	private void showActivity(Mod131 mod131, Mod131Activity act) {
		final CustomDialog detailDialog = new CustomDialog();
		detailDialog.setVisible(false);
		detailDialog.setAnimationEnabled(true);
		detailDialog.setGlassEnabled(true);
		detailDialog.setModal(true);
		detailDialog.setCaption( act.getEpigraph() );
		// *******************
		Button accept = new Button();
		accept.setText(AON.MSG.accept());
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				detailDialog.hide();
			}
		});
		
		detailDialog.center();
		detailDialog.show();
	}
	
}
