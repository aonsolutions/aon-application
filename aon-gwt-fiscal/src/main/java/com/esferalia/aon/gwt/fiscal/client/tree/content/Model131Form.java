package com.esferalia.aon.gwt.fiscal.client.tree.content;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.PercentBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.IFiscalTreeContent;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNode;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model131Form extends ResizeComposite implements IFiscalTreeContent<Mod131>{

	interface Model131FormBinder extends
			UiBinder<Widget, Model131Form> {
	}

	private static LinkedHashMap<String, Mod131Key[]> KEYS = new LinkedHashMap<String, Mod131Key[]>();
	static {
		KEYS.put(AON.MSG.mod131Header2(), new Mod131Key[]{Mod131Key.C03,Mod131Key.C04});
		KEYS.put(AON.MSG.mod131Header3(), new Mod131Key[]{Mod131Key.C05,Mod131Key.C06});
		KEYS.put(AON.MSG.mod131Header4(), new Mod131Key[]{Mod131Key.C07,Mod131Key.C08,Mod131Key.C09
			,Mod131Key.C10,Mod131Key.C11,Mod131Key.C12,Mod131Key.C13,Mod131Key.C14,Mod131Key.C15});
	}
	private EnumMap<Mod131Key, DoubleBox> inputs = new EnumMap<Mod131Key, DoubleBox>(Mod131Key.class);	

	private static final Model131FormBinder panelBinder = GWT.create(Model131FormBinder.class);
	
	Mod131 mod131;
	ITreeNodeCallback<Mod131> callback;
	 
	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button finishButton;
	@UiField
	Button reopenButton;
	@UiField
	Button calculateButton;
	@UiField
	Button generateFileButton;
	@UiField
	Button printButton;
	
	
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	Label status;

	@UiField
	ListBox period;
	
	@UiField
	Label year;
	
	@UiField
	SimplePanel headerPanel;
	@UiField
	SimplePanel activitiesTablePanel;

	@UiField
	Panel formContainer;
	FormPanel diskForm;
	Hidden modIdHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	private boolean changeDisplayStyleName;

	public Model131Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		modIdHidden = new Hidden("modId");
		formFlowPanel.add(modIdHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);

		period.addItem(Period.T1.getName(),Integer.toString( Period.T1.ordinal() ));
		period.addItem(Period.T2.getName(),Integer.toString( Period.T2.ordinal() ));
		period.addItem(Period.T3.getName(),Integer.toString( Period.T3.ordinal() ));
		period.addItem(Period.T4.getName(),Integer.toString( Period.T4.ordinal() ));

	}
	@Override
	public void setCallback(ITreeNodeCallback<Mod131> callback) {
		this.callback = callback;
	}

	@Override
	public void select(Mod131 m131) {
		this.mod131 = m131;
		if ( mod131.getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod131(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), mod131.getId()
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
			populate(mod131);
		}
	}

	private void populate(Mod131 m131) {
		this.mod131 = m131;
		changeDisplayStyleName = false;
		
		year.setText( Integer.toString( mod131.getYear() ));
		period.setItemSelected(mod131.getPeriod().ordinal() - Period.T1.ordinal(),true);
		
		document.setValue(mod131.getDocument());
		name.setValue(mod131.getName());
		surname.setValue(mod131.getSurname());

		enableToolbarItems();
		document.setEnabled(false);
		name.setEnabled(false);
		surname.setEnabled(false);

		status.setText(mod131.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod131.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		paintHeaderTable(mod131);	
		paintActivitiesTables(mod131);
	}
	
	private void paintHeaderTable(final FiscalModel fm) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(TreeNode.getAdministrationImage(fm.getAdministration()));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		
		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, TreeNode.getAdministrationBG(fm.getAdministration()));

		headerTable.setWidget(0, 2, new Label(fm.getModel().getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, TreeNode.getAdministrationBG(fm.getAdministration()));
		headerPanel.setWidget(headerTable);
	}

	private void enableToolbarItems() {
		boolean nevv = (mod131.getId() == null);
		saveButton.setEnabled(isEnabled(mod131));
		deleteButton.setEnabled(!nevv && isEnabled(mod131));
		calculateButton.setEnabled(isEnabled(mod131));
		finishButton.setVisible(!mod131.isFinished());
		reopenButton.setVisible(mod131.isFinished());
		generateFileButton.setVisible(!nevv && mod131.isFinished());
		printButton.setVisible(!nevv);
	}
	
	private void paintActivitiesTables(final Mod131 mod131) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( AON.MSG.mod131Header1()));
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
		
		for (int i = 0; i < Mod131Key.ACTIVITIES.length; i++) {
			row++;
			table.setWidget(row, 0, new Label(mod131.getEpigraph(i) ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());

			DoubleBox netYield = new DoubleBox();
			netYield.setValue(mod131.getNetYield(i));
			netYield.setEnabled(false);
			table.setWidget(row, 1, netYield );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			
			PercentBox percent = new PercentBox();
			percent.setValue(mod131.getPercent(i));
			percent.setEnabled(false);
			table.setWidget(row, 2, percent );
			table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());

			FlowPanel r01 = new FlowPanel();
			DoubleBox result = new DoubleBox();
			result.setValue(mod131.getResult(i));
			result.setEnabled(false);
			r01.add(result);
			Button detail = new Button();
			detail.setStyleName(AON.AON_CSS.aonIconView());
			final int index = i;
			detail.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					showActivity(mod131,index);
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
		c01.setValue( mod131.getAmount(Mod131Key.AC01) );
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
		c02.setValue( mod131.getAmount(Mod131Key.AC02) );
		c02.setEnabled(false);
		p02.add(c02);
		inputs.put(Mod131Key.AC02, c02);
		table.setWidget(row, 1, p02 );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());

		for (String label : KEYS.keySet()) {
			row++;
			table.setWidget(row, 0, new Label( label ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			for (Mod131Key subkey : KEYS.get(label)) {
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
				doubleBox.setValue(mod131.getAmount(subkey));
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
		if (mod131.isFinished()) {
			return false;	
		} 
		return subkey != Mod131Key.AC01 && subkey != Mod131Key.AC02 
			&& subkey != Mod131Key.C04 && subkey != Mod131Key.C06 
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
		prepareForSend();
		FiscalTree.FISCAL_SERVICE.calculateMod131(FiscalTree.getCurrentDomainName()
				,mod131
				,new AsyncCallback<Mod131>() {

					@Override
					public void onSuccess(Mod131 result) {
						changeDisplayStyleName = true;
						receiveModel(result);
						changeDisplayStyleName = false;
					}
					@Override
					public void onFailure(Throwable caught) {
						changeDisplayStyleName = false;
						PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
						box.center();
						box.show();
					}
		});
	}

	private void prepareForSend() {
		for (Mod131Key key : inputs.keySet()) {
			mod131.putAmount(key, inputs.get(key).getValue());
		}
	}
	private void receiveModel(Mod131 result) {
		this.mod131 = result;
		enableToolbarItems();
		for (Mod131Key key : inputs.keySet()) {
			Double oldValue = inputs.get(key).getValue();
			Double newValue = result.getAmount(key);
			inputs.get(key).setValue(newValue);
			inputs.get(key).setValue( result.getAmount(key), true, shouldDisplayChange(oldValue, newValue));
			inputs.get(key).setEnabled(isEnabled(key));
		}
		period.setItemSelected(mod131.getPeriod().ordinal() - Period.T1.ordinal(),true);
		status.setText(mod131.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod131.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;

	}

	private void showActivity(Mod131 mod131, int i ) {
		final CustomDialog detailDialog = new CustomDialog();
		detailDialog.setVisible(false);
		detailDialog.setAnimationEnabled(true);
		detailDialog.setGlassEnabled(true);
		detailDialog.setModal(true);
		detailDialog.setCaption( mod131.getEpigraph(i) );
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
	
	@UiHandler("finishButton")
	void onFinishButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.finish() )) {
			final CustomDialog detailDialog = new CustomDialog();
			detailDialog.setVisible(false);
			detailDialog.setAnimationEnabled(true);
			detailDialog.setGlassEnabled(true);
			detailDialog.setModal(true);
			detailDialog.setCaption( AON.MSG.finish() );
			FlexTable table = new FlexTable();
			table.setStyleName(AON.AON_CSS.aonPanelGrid());
			table.addStyleName(AON.AON_CSS.aonWidthAll());
			table.addStyleName(AON.AON_CSS.aonMarginTop());
			detailDialog.add( table );
			
			final IbanTextBox iban = new IbanTextBox(getSuggestOracle());
			iban.setValue( mod131.getIban() );
			iban.addSelectionHandler( new SelectionHandler<Suggestion>() {
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					Suggestion suggestion = event.getSelectedItem();
					iban.setValue(suggestion.getReplacementString());
				}
			});
			table.setWidget(0, 0, new Label(AON.MSG.iban()));
			table.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridOdd());
			table.getFlexCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonWidthAuto());
			table.setWidget(0, 1, iban);
			table.getFlexCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
			
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.AON_CSS.aonWidthAll()); 
			buttons.addStyleName(AON.AON_CSS.aonTextCenter());
			Button accept = new Button();
			accept.setText(AON.MSG.accept());
			accept.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					detailDialog.hide();
					prepareForSend();
					mod131.setStatus(FiscalStatus.FINISHED);
					mod131.setIban( iban.getValue() );
					save();
				}
			});
			buttons.add(accept);
			Button cancel = new Button();
			cancel.addStyleName(AON.AON_CSS.aonMarginLeft());
			cancel.setText(AON.MSG.cancelAction());
			cancel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					detailDialog.hide();
				}
			});
			buttons.add(cancel);
			table.setWidget(1, 0, buttons);
			table.getFlexCellFormatter().setColSpan(1, 0, 2);
			detailDialog.center();
			detailDialog.show();
		}
	}

	@UiHandler("reopenButton")
	void onReopenButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.reopen() )) {
			prepareForSend();
			mod131.setStatus(FiscalStatus.PENDING);
			save();
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			prepareForSend();
			save();
		}
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.deleteAction())) {
			FiscalTree.FISCAL_SERVICE.deleteMod131(FiscalTree.getCurrentDomainName()
					,mod131
					,new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							callback.delete(mod131);
						}
						@Override
						public void onFailure(Throwable caught) {
							PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
							box.center();
							box.show();
						}
			});
			
		}
	}

	private void save() {
		FiscalTree.FISCAL_SERVICE.saveMod131(FiscalTree.getCurrentDomainName()
				,mod131
				,new AsyncCallback<Mod131>() {

					@Override
					public void onSuccess(Mod131 result) {
						receiveModel(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
						box.center();
						box.show();
					}
		});
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en Hacienda.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model131File");
		modIdHidden.setValue( String.valueOf(mod131.getId()) );
		domainIdHidden.setValue(String.valueOf(FiscalTree.getCurrentDomain()));
		domainNameHidden.setValue(FiscalTree.getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model131Print");
		modIdHidden.setValue( String.valueOf(mod131.getId()) );
		domainIdHidden.setValue(String.valueOf(FiscalTree.getCurrentDomain()));
		domainNameHidden.setValue(FiscalTree.getCurrentDomainName());
		diskForm.submit();
	}
	
	@UiHandler("period")
	void onChangePeriod(ChangeEvent event) {
		mod131.setPeriod( Period.values()[ period.getSelectedIndex() + Period.T1.ordinal() ] );
		callback.changeLabel(mod131);
	}
	
	
	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			FiscalTree.COMMON_SERVICE.getCompanyBanks(
					FiscalTree.getCurrentDomainName(),
					mod131.getDomain(),
					new AsyncCallback<ArrayList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(ArrayList<CompanyBank> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new IbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}
	
	private SuggestOracle getSuggestOracle() {
		return new EnterpriseSuggestOracle();
	}
	
	private boolean shouldDisplayChange(Double oldText,Double newText) {
		return changeDisplayStyleName  
				&& oldText != newText
				&& (oldText == null || !oldText.equals(newText));
	}
}
