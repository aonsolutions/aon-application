package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

class ParentAccountLinker extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	
	protected ParentAccountLinker(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		
		Button check = new Button();
		FlowPanel runContainer = new FlowPanel();		
		runContainer.setVisible(false);
		Button run = new Button();

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGrid());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		Label title = new Label("Introduzca los datos de la nueva cuenta");
		title.setStyleName(AON.AON_CSS.aonMarginBottom());
		tab.setWidget(0, 0, title);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBold());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonPanelGridOdd());
		TextBox accountBox = new TextBox();
		accountBox.setStyleName(AON.AON_CSS.aonInputText());
		accountBox.setMaxLength(9);
		accountBox.setVisibleLength(9);
		accountBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				runContainer.setVisible(false);
			}
		});
		tab.setWidget(1, 1, accountBox);
		tab.getCellFormatter().setStyleName(1, 1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(2, 0, new Label(AON.MSG.description()));
		tab.getCellFormatter().setStyleName(2, 0, AON.AON_CSS.aonPanelGridOdd());
		TextBox nameBox = new TextBox();
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		nameBox.setMaxLength(128);
		nameBox.setVisibleLength(45);
		nameBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				runContainer.setVisible(false);
			}
		});
		tab.setWidget(2, 1, nameBox);
		tab.getCellFormatter().setStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(3, 0, new Label(AON.MSG.alias()));
		tab.getCellFormatter().setStyleName(3, 0, AON.AON_CSS.aonPanelGridOdd());
		TextBox aliasBox = new TextBox();
		aliasBox.setStyleName(AON.AON_CSS.aonInputText());
		aliasBox.setMaxLength(32);
		aliasBox.setVisibleLength(32);
		aliasBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				runContainer.setVisible(false);
			}
		});
		tab.setWidget(3, 1, aliasBox);
		tab.getCellFormatter().setStyleName(3, 1, AON.AON_CSS.aonPanelGridEven());

		check.setText("Comprobar");
		check.setStyleName(AON.AON_CSS.aonMarginTop());
		check.addStyleName(AON.AON_CSS.aonIconCommandButton());
		check.addStyleName(AON.AON_CSS.aonSimpleBorder());
		check.addStyleName(AON.AON_CSS.aonClickable());
		check.addStyleName(AON.AON_CSS.aonIconValidate());
		
		run.setText("Ejecutar");
		run.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		run.addStyleName(AON.AON_CSS.aonSimpleBorder());
		run.addStyleName(AON.AON_CSS.aonIconValidate());
		run.addStyleName(AON.AON_CSS.aonMarginLeft());
		
		tab.getCellFormatter().setStyleName(4, 0, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(4, 0, 2);
		tab.setWidget(4, 0, check);
		
		
		Label runMsg = new Label("Por favor, revise detenidamente la informaci\u00F3n suministrada, si est\u00E1 conforme con lo propuesto pulse \"Ejecutar\".");
		runMsg.setStyleName(AON.AON_CSS.aonErrorPanel());
		runMsg.addStyleName(AON.AON_CSS.aonMarginTop());
		runContainer.add(runMsg);
		runContainer.add(run);
		tab.getCellFormatter().setStyleName(5, 0, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(5, 0, 2);
		tab.setWidget(5, 0, runContainer);
		

		check.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Account account = new Account();
				account.setDomain(domain.getId());
				account.setCode(accountBox.getValue());
				account.setDescription(nameBox.getValue());
				account.setAlias(aliasBox.getValue());
				openFootPanelIfNeeded();
				SERVICE.checkParentLinker(domainName, user, domain, account, new AsyncCallback<AccUtilitiesResult>(){

					@Override
					public void onFailure(Throwable caught) {
						showErrorPanel(caught.getMessage());
						runContainer.setVisible(false);
					}

					@Override
					public void onSuccess(AccUtilitiesResult result) {
						showResults(result);
						boolean canRun =  !result.hasErrorMessages();
						if (canRun) {
							runContainer.setVisible(true);
						}
					}
				});
			}
		});
		
		run.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm("Continuar?", "Ejecutar", new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
					}
					
					@Override
					public void onAccept() {
						runContainer.setVisible(false);
						Account account = new Account();
						account.setDomain(domain.getId());
						account.setCode(accountBox.getValue());
						account.setDescription(nameBox.getValue());
						account.setAlias(aliasBox.getValue());
						openFootPanelIfNeeded();
						SERVICE.runParentLinker(domainName, user, domain, account, new AsyncCallback<AccUtilitiesResult>(){

							@Override
							public void onFailure(Throwable caught) {
								showErrorPanel(caught.getMessage());
							}

							@Override
							public void onSuccess(AccUtilitiesResult result) {
								showResults(result);
							}
						});
					}
				});
			}
		});
		
		container.setWidget(tab);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Crear una cuenta contable y enlazarla en todos los dominios con herencia habilitada, "
			+"sustituyendo la que pueda existir con el mismo n\u00FAmero de cuenta por la que se est\u00E1 creando";
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlowPanel log = new FlowPanel("ul"); 
		log.setStyleName(AON.AON_CSS.aonWidth98Percent());
		log.addStyleName(AON.AON_CSS.aonBlockCenter());
		log.addStyleName(AON.AON_CSS.aonMarginTop());
		log.addStyleName(AON.AON_CSS.aonMarginBottom());
		log.addStyleName(AON.AON_CSS.aonFixedFont());
		log.addStyleName(AON.AON_CSS.aonFontMedium());
		log.addStyleName(AON.AON_CSS.aonNowrap());
		log.addStyleName(AON.AON_CSS.aonPaddingLeft());
		if (result != null && !result.isEmpty()) {
			for ( IAccUtilitiesItem item : result.getItems()) {
				item.getType().visit(
						new IAccUtilitiesItemTypeVisitor() {
							
							@Override public void visitOther(AccUtilitiesItemType type) {}
							@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
							@Override public void visitCustomerAccount(AccUtilitiesItemType type) {}
							@Override public void visitSupplierAccount(AccUtilitiesItemType type) {}
							@Override public void visitCreditorAccount(AccUtilitiesItemType type) {}
							@Override public void visitEmptyEntry(AccUtilitiesItemType type) {}
							@Override public void visitDeleteEntries(AccUtilitiesItemType type) {}
							@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
							@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {}
							@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
							@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
							
							@Override 
							public void visitParentAccountLinker(AccUtilitiesItemType type) {
								FlowPanel itemPanel = new FlowPanel();
								InlineLabel domainLabel = new InlineLabel(item.getDomainName());
								domainLabel.setStyleName(AON.AON_CSS.aonBold());
								domainLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
								domainLabel.addStyleName(AON.AON_CSS.aonIconChecked());
								itemPanel.add(domainLabel);
								InlineLabel msgLabel = new InlineLabel(item.getMessage());
								msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
								itemPanel.add(msgLabel);
								log.add(itemPanel);
							}
							@Override 
							public void visitInfoMessage(AccUtilitiesItemType type) {
								FlowPanel itemPanel = new FlowPanel();
								InlineLabel msgLabel = new InlineLabel(item.getMessage());
								if (!AonStringUtils.isBlank( item.getDomainName())) {
									InlineLabel domainLabel = new InlineLabel(item.getDomainName());
									domainLabel.setStyleName(AON.AON_CSS.aonBold());
									domainLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
									domainLabel.addStyleName(AON.AON_CSS.aonIconInfo());
									itemPanel.add(domainLabel);
								} else {
									if (!AonStringUtils.isBlank( item.getMessage())) {
										msgLabel.addStyleName(AON.AON_CSS.aonIconInfo());
									} else {
										msgLabel.addStyleName(AON.AON_CSS.aonMarginTop());
									}
								}
								msgLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
								itemPanel.add(msgLabel);
								log.add(itemPanel);
							}
							@Override 
							public void visitErrorMessage(AccUtilitiesItemType type) {
								FlowPanel itemPanel = new FlowPanel();
								InlineLabel msgLabel = new InlineLabel(item.getMessage());
								if (!AonStringUtils.isBlank( item.getDomainName())) {
									InlineLabel domainLabel = new InlineLabel(item.getDomainName());
									domainLabel.setStyleName(AON.AON_CSS.aonBold());
									domainLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
									domainLabel.addStyleName(AON.AON_CSS.aonIconError());
									itemPanel.add(domainLabel);
								} else {
									msgLabel.addStyleName(AON.AON_CSS.aonIconInfo());
								}
								msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
								itemPanel.add(msgLabel);
								log.add(itemPanel);
							}
						}						
				);
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}
}
