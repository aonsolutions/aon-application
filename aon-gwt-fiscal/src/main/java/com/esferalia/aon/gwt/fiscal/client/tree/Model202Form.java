package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.EnumMap;
import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model202Form extends ResizeComposite implements FiscalNodeWidget<Mod202>{

	interface Model202FormBinder extends
			UiBinder<Widget, Model202Form> {
	}
	private static Mod202Key[] additionalDataKeys = new Mod202Key[]{
			 Mod202Key.X01,Mod202Key.X02,Mod202Key.X03,Mod202Key.X04,Mod202Key.X05
			,Mod202Key.X06,Mod202Key.X07,Mod202Key.X08,Mod202Key.X09,Mod202Key.X10};  
	private static LinkedHashMap<String, Mod202Key[]> KEYS = new LinkedHashMap<String, Mod202Key[]>();
	static {
//		KEYS.put(AON.MSG.accrual()
//			, new Mod202Key[]{Mod202Key.P01,Mod202Key.P02,Mod202Key.P03});
		KEYS.put(AON.MSG.liquidacion(), new Mod202Key[]{});
		KEYS.put(AON.MSG.mod202Compute1(), new Mod202Key[]{Mod202Key.C01
			,Mod202Key.C02,Mod202Key.C03});
		KEYS.put(AON.MSG.mod202Compute1(), new Mod202Key[]{
			 Mod202Key.C04,Mod202Key.C05,Mod202Key.C06,Mod202Key.C36
			,Mod202Key.C37,Mod202Key.C07,Mod202Key.C08,Mod202Key.C38
			,Mod202Key.C39,Mod202Key.C09,Mod202Key.C43,Mod202Key.C13
			,Mod202Key.C44,Mod202Key.C14,Mod202Key.C45,Mod202Key.C46
		});
		KEYS.put(AON.MSG.mod202Compute2(), new Mod202Key[]{
			 Mod202Key.C16,Mod202Key.C17,Mod202Key.C47,Mod202Key.C40
			,Mod202Key.C48,Mod202Key.C49,Mod202Key.C18
		});
		KEYS.put(AON.MSG.mod202Compute3(), new Mod202Key[]{
			 Mod202Key.C19,Mod202Key.C20,Mod202Key.C21,Mod202Key.C22 
			,Mod202Key.C23,Mod202Key.C24,Mod202Key.C25,Mod202Key.C50 
			,Mod202Key.C42,Mod202Key.C51,Mod202Key.C52,Mod202Key.C26
			,Mod202Key.C27,Mod202Key.C28,Mod202Key.C29,Mod202Key.C30 
			,Mod202Key.C31,Mod202Key.C32,Mod202Key.C33,Mod202Key.C34
		});
	}
	private EnumMap<Mod202Key, CheckBox> checks = new EnumMap<Mod202Key, CheckBox>(Mod202Key.class);
	private EnumMap<Mod202Key, DoubleBox> inputs = new EnumMap<Mod202Key, DoubleBox>(Mod202Key.class);	

	private static final Model202FormBinder panelBinder = GWT.create(Model202FormBinder.class);
	
	TreeNode<Mod202> node;
	 
	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
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
	SimplePanel tablePanel;

	public Model202Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	public void select(TreeNode<Mod202> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod202(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<Mod202>() {

						@Override
						public void onSuccess(Mod202 result) {
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

	private void populate(Mod202 mod202) {
		node.setTreeObject( mod202 );
		saveButton.setEnabled(isEnabled(mod202));
		deleteButton.setEnabled(isEnabled(mod202));
		calculateButton.setEnabled(isEnabled(mod202));
		year.setText( Integer.toString( mod202.getYear() ));
		period.setText(mod202.getPeriod().getName());
		
		document.setValue(mod202.getDocument());
		document.setEnabled(false);
		name.setValue(mod202.getName());
		name.setEnabled(false);
		surname.setValue(mod202.getSurname());
		surname.setEnabled(false);
		status.setText(mod202.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod202.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		paintTable(mod202);
	}
	

	private void paintTable(final Mod202 mod202) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		
		table.setWidget(row, 0, new Label( AON.MSG.additionalData() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		for (Mod202Key key : additionalDataKeys) {
			row++;
			CheckBox checkBox = new CheckBox(key.getDescription());
			checkBox.setValue(mod202.getAmount(key)==1);
			checkBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					calculate();
				}
			});
			checks.put(key, checkBox);
			table.setWidget(row, 0, checkBox);
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
		}		
		for (String label: KEYS.keySet()) {
			row++;
			table.setWidget(row, 0, new Label( label ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			for (Mod202Key key : KEYS.get(label)) {
				row++;
				table.setWidget(row, 0, new Label( key.getDescription()));
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
				table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
				table.getFlexCellFormatter().setColSpan(row, 0, 3);

				FlowPanel p = new FlowPanel();
				p.setStyleName(AON.AON_CSS.aonNowrap());
				InlineLabel l = new InlineLabel( key.getBox() );
				l.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
				p.add(l);
				DoubleBox doubleBox = new DoubleBox();
				doubleBox.setValue(mod202.getAmount(key));
				doubleBox.setEnabled(isEnabled(key));
				p.add(doubleBox);
				inputs.put(key, doubleBox);
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
		tablePanel.setWidget(table);
	}

	private boolean isEnabled(Mod202Key key) {
		if (node.getTreeObject().isFinished()) {
			return false;	
		} 
		return key.isEnabled();
	}

	private boolean isEnabled(Mod202 mod202) {
		return ( mod202.getYear() >= 2015 );
	}


	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.calculateAction())) {
			calculate();
		}
	}
	
	private void calculate() {
		populateTreeObject();
		FiscalTree.FISCAL_SERVICE.calculateMod202(FiscalTree.getCurrentDomainName()
				,node.getTreeObject()
				,new AsyncCallback<Mod202>() {

					@Override
					public void onSuccess(Mod202 result) {
						populateTreeInputs(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(caught.getMessage());								
					}

		});
	}

	private void populateTreeObject() {
		for (Mod202Key key : checks.keySet()) {
			node.getTreeObject().putAmount(key, checks.get(key).getValue()?1:0);
		}
		for (Mod202Key key : inputs.keySet()) {
			node.getTreeObject().putAmount(key, inputs.get(key).getValue());
		}
	}
	private void populateTreeInputs(Mod202 result) {
		node.setTreeObject(result);
		for (Mod202Key key : checks.keySet()) {
			checks.get(key).setValue(result.getAmount(key)==1);
		}
		for (Mod202Key key : inputs.keySet()) {
			inputs.get(key).setValue(result.getAmount(key));
		}
	}
	
	@UiHandler("status")
	void onStatusButtonClick(ClickEvent event) {
		if (Window.confirm( node.getTreeObject().isFinished()?
				AON.MSG.reopen():AON.MSG.finish() )) {
			populateTreeObject();
			node.getTreeObject().setFinished(!node.getTreeObject().isFinished());
			save();
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			save();
		}
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.deleteAction())) {
			FiscalTree.FISCAL_SERVICE.deleteMod202(FiscalTree.getCurrentDomainName()
					,node.getTreeObject()
					,new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							node.getParentItem().removeItem(node);							
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());								
						}
			});
			
		}
	}

	private void save() {
		FiscalTree.FISCAL_SERVICE.saveMod202(FiscalTree.getCurrentDomainName()
				,node.getTreeObject()
				,new AsyncCallback<Mod202>() {

					@Override
					public void onSuccess(Mod202 result) {
						populateTreeInputs(result);
						status.setText(node.getTreeObject().isFinished()?
								AON.MSG.finished():AON.MSG.pending() );
						status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
						status.addStyleName(node.getTreeObject().isFinished()?
								AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
						for (Mod202Key key : inputs.keySet()) {
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
