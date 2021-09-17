package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBank;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.api.checkit.CheckItException;

public class CheckItModule extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(CheckItModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	
	private static CheckItServiceAsync CHECKIT_SERVICE;
	
	private DockLayoutPanel dockLayoutPanel;
	private SplitLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonToolbar toolbar;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private FlowPanel sessionLog;
	boolean minimizedByUser;
	
	private Widget linkedBanks;
	private Widget unlinkedBanks;
	FlowPanel enterpriseData;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		CheckItModuleOptions options = new CheckItModuleOptions()
				.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
		;
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final CheckItModuleOptions opt ) {
		AON.ensureInjected();
		CheckItServiceAsync serviceRaw = GWT.create(CheckItService.class);
		CHECKIT_SERVICE = new CheckItServiceAsyncDecorator(serviceRaw);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
				@Override
				public void onSuccess(CheckItConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void loadModule( final CheckItModuleOptions opt ) {
		toolbar = new AonToolbar(AON.MSG.chekItModule());
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH );
		centerLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(centerLayoutPanel);
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.add(centerPanel);
		centerLayoutPanel.addSouth(getMinimizePanel(), 30);
		dockLayoutPanel.add(centerLayoutPanel);
		enterpriseData = paintEnterpiseData( opt );
		container.add( enterpriseData );
	}

	private FlowPanel paintEnterpiseData(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		CheckItConfiguration conf = opt.getConfiguration();
		if (conf.getEnterpriseId() != null) {
			paintRegistrationData( opt );
			linkedBanks = paintBanks(opt);
			unlinkedBanks = paintUnlinkedBanks(opt);
			panel.add(linkedBanks);
			panel.add(unlinkedBanks);
		} else {
			panel.add( paintRegistration( opt ) );
		}
		return panel;
	}
	
	private void paintRegistrationConfirmation(CheckItModuleOptions opt) {
		FlexTable registrationTable = new FlexTable();
		AonDialog dialog = new AonDialog("Confirmaci\u00F3n de registro", registrationTable);
		Label confirmLabel = new Label("\u00BFDesea registrar esta empresa en CheckIt?");
		confirmLabel.getElement().getStyle().setProperty("margin-bottom", "1.5em");
		confirmLabel.setHorizontalAlignment(Label.ALIGN_CENTER);
		registrationTable.setWidget(0, 0, confirmLabel);
		HorizontalPanel hp = new HorizontalPanel();
		Button hai = new Button("S\u00CD");
		Button iie = new Button(AON.MSG.no());
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		hp.add(hai);
		hp.add(iie);
		registrationTable.setWidget(1, 0, hp);
		
		hai.addClickHandler(handler -> {
			dialog.hide();
//			CHECKIT_SERVICE.saveEnterpriseData(
//			opt.getDomainName()
//			, opt.getDomain()
//			, opt.getUser()
//			, new AsyncCallback<Integer>() {
//				@Override
//				public void onSuccess(Integer result) {
//					opt.getConfiguration().setEnterpriseId(result);
//				}
//				
//				@Override
//				public void onFailure(Throwable caught) {
//					toolbar.showErrorMessage(caught.getMessage());
//				}	
//		});
			dockLayoutPanel.clear();
			onModuleLoad();
			
		});
		
		iie.addClickHandler(handler -> {
			dialog.hide();
			container.remove(dialog);
		});
		dialog.center();
		container.add(dialog);
		dialog.show();
	}

	private Widget paintRegistration(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonBorder());
		panel.addStyleName(AON.CSS.aonPadding());
		InlineLabel label = new InlineLabel("No se ha encontrado informaci\u00F3n sobre el registro en Check It");
		AonTextButton registerButton = new AonTextButton( AON.MSG.register(), AON.CSS.aonIconRegister() );
		registerButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				paintRegistrationConfirmation(opt);
				
			}
		});
		registerButton.addStyleName(AON.CSS.aonMarginLeft());
		panel.add( label );
		panel.add( registerButton );
		return panel;
	}

	private void paintRegistrationData(CheckItModuleOptions opt) {
		AonToolbarButton config = new AonToolbarButton(AON.MSG.settings(), AON.CSS.aonIconSettings());
		toolbar.add(config);
		config.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption(AON.MSG.settings());

				FlowPanel panel = new FlowPanel();
				panel.setStyleName(AON.CSS.aonTextCenter());
				panel.addStyleName(AON.CSS.aonWidthAlmostAll());
				panel.addStyleName(AON.CSS.aonMarginTop());
				panel.addStyleName(AON.CSS.aonMarginBottom());
				panel.addStyleName(AON.CSS.aonBlockCenter());
				panel.addStyleName(AON.CSS.aonBorder());
				panel.addStyleName(AON.CSS.aonPadding());
				InlineLabel label = new InlineLabel("Identificador de empresa en Check It");
				label.setStyleName(AON.CSS.aonTableLabel());
				panel.add( label );
				AonIntegerBox enterpriseIdBox = new AonIntegerBox();
				enterpriseIdBox.setVisibleLength(8);
				enterpriseIdBox.addStyleName(AON.CSS.aonMarginLeft());
				enterpriseIdBox.setValue(opt.getConfiguration().getEnterpriseId());
				enterpriseIdBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Integer> event) {
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm("Confirma la modificación del identificador", new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
								enterpriseIdBox.setValue(opt.getConfiguration().getEnterpriseId());				
							}
							
							@Override
							public void onAccept() {
								// TODO Modificar el parámetro en la BD
							}
						});
					}
				});
				panel.add( enterpriseIdBox );
				
				FlowPanel buttons = new FlowPanel();
		    	buttons.setStyleName(AON.CSS.aonTextCenter());
		    	buttons.addStyleName(AON.CSS.aonMarginTop());
		    	buttons.addStyleName(AON.CSS.aonMarginBottom());

		    	final Button cancelButton = new Button();
		    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
		    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		    	cancelButton.setText( AON.MSG.close());
		    	cancelButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						cancelButton.setEnabled(false);
						dialog.hide();
					}
				});
		    	buttons.add(cancelButton);
		    	panel.add(buttons);
				
				dialog.add(panel);
				dialog.center();
				dialog.show();
			}
		});
	}

	private Widget paintBanks(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonBorder());
		panel.addStyleName(AON.CSS.aonPadding());
		Label linkedTitle = new Label("Cuentas vinculadas");
		linkedTitle.addStyleName(AON.CSS.aonTextLeft());
		linkedTitle.addStyleName(AON.CSS.aonFontMedium());
		linkedTitle.addStyleName(AON.CSS.aonBold());
		panel.add(linkedTitle);
		if (opt.getConfiguration().getCheItBanks() != null && opt.getConfiguration().getCheItBanks().size() > 0) {
			AonCards cards = new AonCards();
			double balanceTotal = 0;
			double remainderTotal = 0;
			for (CheckItBankAccount bankAccount : opt.getConfiguration().getCheItBanks()) {
				balanceTotal = balanceTotal + bankAccount.getBalance();
				remainderTotal = remainderTotal + bankAccount.getRemainder();
				cards.addCard( new AonCheckItBankCard(opt, bankAccount) );
			}
			panel.add(cards);
			
			balanceTotal = AonMathUtils.round( balanceTotal );
			remainderTotal = AonMathUtils.round( remainderTotal );
			
			FlowPanel totals = new FlowPanel();
			panel.setStyleName(AON.CSS.aonTextCenter());
			panel.addStyleName(AON.CSS.aonWidthAlmostAll());
			panel.addStyleName(AON.CSS.aonMarginTop());
			panel.addStyleName(AON.CSS.aonBlockCenter());
			panel.addStyleName(AON.CSS.aonPadding());
			
			InlineLabel accumLabel = new InlineLabel("Acumulados ");
			accumLabel.setStyleName(AON.CSS.aonTableLabel());
			accumLabel.addStyleName(AON.CSS.aonFontMedium());

			InlineLabel balanceLabel = new InlineLabel("Saldo: ");
			balanceLabel.setStyleName(AON.CSS.aonMarginLeft());
			balanceLabel.addStyleName(AON.CSS.aonTableLabel());
			
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.setText( AON.FMT.format( balanceTotal ));
			balanceBox.setStyleName(AON.CSS.aonMarginLeft());
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			if (AonMathUtils.isLessThanZero( balanceTotal )) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			
			InlineLabel remainderLabel = new InlineLabel("Disponible: ");
			remainderLabel.setStyleName(AON.CSS.aonTableLabel());
			remainderLabel.addStyleName(AON.CSS.aonMarginLeft());
			
			InlineLabel remainderBox = new InlineLabel();
			remainderBox.setText( AON.FMT.format( remainderTotal ));
			remainderBox.setStyleName(AON.CSS.aonMarginLeft());
			remainderBox.addStyleName(AON.CSS.aonFontMedium());
			if (AonMathUtils.isLessThanZero( remainderTotal )) {
				remainderBox.addStyleName(AON.CSS.aonColorRed());	
			}
			totals.add(accumLabel);
			totals.add(balanceLabel);
			totals.add(balanceBox);
			totals.add(remainderLabel);
			totals.add(remainderBox);
			panel.add(totals);
		}
		return panel;
	}
	
	private Widget paintUnlinkedBanks(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
//		panel.addStyleName(AON.CSS.aonBorder());
		panel.addStyleName(AON.CSS.aonPadding());
		Label unlinkedTitle = new Label("Cuentas no vinculadas");
		unlinkedTitle.addStyleName(AON.CSS.aonTextLeft());
		unlinkedTitle.addStyleName(AON.CSS.aonFontMedium());
		unlinkedTitle.addStyleName(AON.CSS.aonBold());
		panel.add(unlinkedTitle);
		if (opt.getConfiguration().getCheItBanks() != null && opt.getConfiguration().getCheckItUnlinkedBanks().size() > 0) {
			AonCards cards = new AonCards();
			for (CheckitUnlinkedBankAccount unlinkedBankAccount : opt.getConfiguration().getCheckItUnlinkedBanks()) {
				cards.addCard( new AonCheckItUnlinkedBankCard(opt, unlinkedBankAccount) );
			}
			panel.add(cards);
		}
		return panel;
	}
	
	private class AonCheckItBankCard extends AonCard {

		private AonCheckItBankCard(final CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(checkItBankAccount.getBank());
			this.setTitle(title);
			
			FlowPanel body = new FlowPanel();
			FlowPanel movText = new FlowPanel();
			
			FlowPanel ibanPanel = new FlowPanel();
			InlineLabel ibanBox = new InlineLabel();
			ibanBox.setText(checkItBankAccount.getCcc());
			ibanPanel.add(ibanBox);
			
			FlowPanel atDateLabelPanel = new FlowPanel();
			InlineLabel atDateLabel = new InlineLabel("Fecha del saldo ");
			atDateLabel.setStyleName(AON.CSS.aonTableLabel());
			atDateLabelPanel.add(atDateLabel);
			
			FlowPanel atDatePanel = new FlowPanel();
			InlineLabel atDateBox = new InlineLabel();
			atDateBox.addStyleName(AON.CSS.aonFontMedium());
			atDateBox.setText( checkItBankAccount.getAtDate() == null ? "----" : AON.TIME_FORMAT.format( checkItBankAccount.getAtDate()));
			atDatePanel.add(atDateBox);
			
			FlowPanel balanceLabelPanel = new FlowPanel();
			InlineLabel balanceLabel = new InlineLabel("Saldo ");
			balanceLabel.setStyleName(AON.CSS.aonTableLabel());
			balanceLabelPanel.add(balanceLabel);
			
			FlowPanel balancePanel = new FlowPanel();
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			if (AonMathUtils.isLessThanZero( checkItBankAccount.getBalance())) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			balanceBox.setText( AON.FMT.format( checkItBankAccount.getBalance()));
			balancePanel.add(balanceBox);

			FlowPanel remainderLabelPanel = new FlowPanel();
			InlineLabel remainderLabel = new InlineLabel("Disponible ");
			remainderLabel.setStyleName(AON.CSS.aonTableLabel());
			remainderLabelPanel.add(remainderLabel);

			FlowPanel remainderPanel = new FlowPanel();
			InlineLabel remainderBox = new InlineLabel();
			if (AonMathUtils.isLessThanZero( checkItBankAccount.getRemainder())) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			remainderBox.setText( AON.FMT.format( checkItBankAccount.getRemainder()));
			remainderPanel.add(remainderBox);

			body.add(ibanPanel);
			
			body.add(atDateLabelPanel);
			body.add(atDatePanel);
			
			body.add(balanceLabelPanel);
			body.add(balancePanel);
			
			body.add(remainderLabelPanel);
			body.add(remainderPanel);
			
			int pendingMovements = checkItBankAccount.getPendingMovements();
			
			if (pendingMovements > 0) {
				String singPlur = pendingMovements != 1 ? " nuevos movimientos" : " nuevo movimiento";
//				Label pendingLabel = new Label("Hay " + pendingMovements + singPlur);
				
				InlineLabel pending1Label = new InlineLabel("Hay ");
				InlineLabel pending2Label = new InlineLabel(AonNumberUtils.toString(pendingMovements));
				pending2Label.setStyleName(AON.CSS.aonBold());
//				pending2Label.addStyleName(AON.CSS.aonMarginLeft());
				InlineLabel pending3Label = new InlineLabel(singPlur);
				movText.add(pending1Label);
				movText.add(pending2Label);
				movText.add(pending3Label);
				
				
				movText.addStyleName(AON.CSS.aonTextCenter());
				movText.addStyleName(AON.CSS.aonColorGreen());
				body.add(movText);
			}
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO IMPORT!
					CHECKIT_SERVICE.insertTransactions(
							opt.getDomainName()
							, opt.getDomain()
							, opt.getUser()
							, opt.getConfiguration().getEnterpriseId()
							, checkItBankAccount
							, new AsyncCallback<Integer>() {
								@Override
								public void onSuccess(Integer result) {
									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
									
									Label label = new Label(result + singPlur + checkItBankAccount.getBank() + " - " + checkItBankAccount.getCcc());
									label.addStyleName(AON.CSS.aonColorGreen());
									sessionLog.add(label);
									
									if (result != null && result != 0) {
										body.remove(movText);
										checkItBankAccount.setPendingMovements(0);
									}
									
								}
								
								@Override
								public void onFailure(Throwable caught) {
									toolbar.showErrorMessage(caught.getMessage());
								}	
						} );
					
				}
			});
			
			AonTableButton modifyButton = new AonTableButton("Editar credenciales", AON.CSS.aonIconEdit());
			modifyButton.setTabIndex(-2);
			modifyButton.addClickHandler(event -> {
				Integer loginType = checkItBankAccount.getBankLoginType();
				Integer enterpriseId = opt.getConfiguration().getEnterpriseId();
				
				CHECKIT_SERVICE.getCredentials(enterpriseId, loginType, new AsyncCallback<CheckItLoginFields>() {

					@Override
					public void onFailure(Throwable caught) {
						paintCredentialAdd(checkItBankAccount, opt);
						
					}

					@Override
					public void onSuccess(CheckItLoginFields result) {
						paintCredentialEdit(result, opt);
						
					}
				});
				
				
			});
			
//			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
//			deleteButton.setTabIndex(-2);
//			deleteButton.addClickHandler( new ClickHandler() {
//				
//				@Override
//				public void onClick(ClickEvent event) {
//					deleteButton.setEnabled(false);
//					AonConfirmDialog cd = new AonConfirmDialog();
//					cd.confirm("Eliminar registro en Check IT?", new AonConfirmDialogCallback() {
//						
//						@Override
//						public void onCancel() {
//							deleteButton.setEnabled(true);
//						}
//						
//						@Override
//						public void onAccept() {
//							// TODO REMOVE FROM CHEC IT
//						}
//					});
//				}
//			});
			getMenuPanel().add(saveButton);
			getMenuPanel().add(modifyButton);
//			getMenuPanel().add(deleteButton);
		}
		
	}
	
	private void paintCredentialEdit (CheckItLoginFields checkitLoginFields, CheckItModuleOptions opt) {
		AonTextBox userID = new AonTextBox();
		AonTextBox userPassword = new AonTextBox();
		AonTextBox userPIN = new AonTextBox();
		
		
		FlowPanel flow = new FlowPanel();
		AonDialog dialog = new AonDialog("Editar credenciales", flow);
		
		int nextRow = 0;
		
		FlexTable fieldsTable = new FlexTable();
		
		if (checkitLoginFields.getUserID() != null && !checkitLoginFields.getUserID().isEmpty()) {
			if (checkitLoginFields.getUserIDInput() != null)
				userID.setValue(checkitLoginFields.getUserIDInput());
			fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserID() + ":"));
			fieldsTable.setWidget(nextRow++, 1, userID);
		}
		if (checkitLoginFields.getUserPassword() != null && !checkitLoginFields.getUserPassword().isEmpty()) {
			fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserPassword() + ":"));
			fieldsTable.setWidget(nextRow++, 1, userPassword);
		}
		if (checkitLoginFields.getUserPIN() != null && !checkitLoginFields.getUserPIN().isEmpty()) {
			fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserPIN() + ":"));
			fieldsTable.setWidget(nextRow, 1, userPIN);
		}	
		
		flow.add(fieldsTable);
		
		HorizontalPanel hp = new HorizontalPanel();
		Button hai = new Button(AON.MSG.accept());
		Button iie = new Button(AON.MSG.cancelAction());
		hp.setWidth("50%");
		hp.addStyleName(AON.CSS.aonBlockCenter());
		hp.addStyleName(AON.CSS.aonMarginTop());
		hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		hp.add(hai);
		hp.add(iie);
		flow.add(hp);
		dialog.center();
		
		Label errorLbl = new Label();
		errorLbl.addStyleName(AON.CSS.aonColorRed());
		errorLbl.addStyleName(AON.CSS.aonBlockCenter());
		errorLbl.addStyleName(AON.CSS.aonTextCenter());
		errorLbl.addStyleName(AON.CSS.aonMarginTop());		
		flow.add(errorLbl);
		
		iie.addClickHandler(e -> {
			dialog.hide();
			container.remove(dialog);
		});
		hai.addClickHandler(e -> {
			
			CHECKIT_SERVICE.editCredentials(opt.getConfiguration().getEnterpriseId(), checkitLoginFields, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					String errMsg = caught.getMessage();
					errorLbl.setText(errMsg);
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						Label successLabel = new Label("Se han actualizado correctamente las credenciales");
						successLabel.addStyleName(AON.CSS.aonColorGreen());
						sessionLog.add(successLabel);
					} else {
						Label successLabel = new Label("Error: no se pudieron actualizar las credenciales");
						successLabel.addStyleName(AON.CSS.aonColorGreen());
						sessionLog.add(successLabel);
					}
					dialog.hide();
					container.remove(dialog);
				}
			});
		});
		
		container.add(dialog);
		dialog.show();
	}
	
	private void paintCredentialAdd (CheckItBankAccount checkItBankAccount, CheckItModuleOptions opt) {
		AonTextBox userID = new AonTextBox();
		AonTextBox userPassword = new AonTextBox();
		AonTextBox userPIN = new AonTextBox();
		
		
		FlowPanel flow = new FlowPanel();
		AonDialog dialog = new AonDialog("A\u00F1adir credenciales", flow);
		
		CHECKIT_SERVICE.getFields(checkItBankAccount.getBankLoginType(), new AsyncCallback<CheckItLoginFields>() {

			@Override
			public void onFailure(Throwable caught) {
				Label errLabel = new Label("Error: se produjo un error inexperado al obtener las credenciales");
				errLabel.addStyleName(AON.CSS.aonColorRed());
				sessionLog.add(errLabel);
			}

			@Override
			public void onSuccess(CheckItLoginFields result) {
				int nextRow = 0;
				
				FlexTable fieldsTable = new FlexTable();
				
				
				
				if (result.getUserID() != null && !result.getUserID().isEmpty()) {
					if (result.getUserIDInput() != null)
						userID.setValue(result.getUserIDInput());
					fieldsTable.setWidget(nextRow, 0, new Label(result.getUserID() + ":"));
					fieldsTable.setWidget(nextRow++, 1, userID);
				}
				if (result.getUserPassword() != null && !result.getUserPassword().isEmpty()) {
					fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPassword() + ":"));
					fieldsTable.setWidget(nextRow++, 1, userPassword);
				}
				if (result.getUserPIN() != null && !result.getUserPIN().isEmpty()) {
					fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPIN() + ":"));
					fieldsTable.setWidget(nextRow, 1, userPIN);
				}	
				
				flow.add(fieldsTable);
				
				HorizontalPanel hp = new HorizontalPanel();
				Button hai = new Button(AON.MSG.accept());
				Button iie = new Button(AON.MSG.cancelAction());
				hp.setWidth("50%");
				hp.addStyleName(AON.CSS.aonBlockCenter());
				hp.addStyleName(AON.CSS.aonMarginTop());
				hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
				hp.add(hai);
				hp.add(iie);
				flow.add(hp);
				dialog.center();
				
				Label errorLbl = new Label();
				errorLbl.addStyleName(AON.CSS.aonColorRed());
				
				iie.addClickHandler(e -> {
					dialog.hide();
					container.remove(dialog);
				});
				hai.addClickHandler(e -> {
					
					CHECKIT_SERVICE.editCredentials(opt.getConfiguration().getEnterpriseId(), result, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							String errMsg = caught.getMessage();
							errorLbl.setText(errMsg);
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								Label successLabel = new Label("Se han actualizado correctamente las credenciales");
								successLabel.addStyleName(AON.CSS.aonColorGreen());
								sessionLog.add(successLabel);
							} else {
								Label successLabel = new Label("Error: no se pudieron actualizar las credenciales");
								successLabel.addStyleName(AON.CSS.aonColorGreen());
								sessionLog.add(successLabel);
							}
							dialog.hide();
							container.remove(dialog);
						}
					});
				});
				
				container.add(dialog);
				dialog.show();
				
			}
		});
		
		
	}
	
	private void paintBankRegistration(CheckItModuleOptions opt, CheckitUnlinkedBankAccount checkItUnlinkedBankAccount) {
		
		AonTextBox userID = new AonTextBox();
		AonTextBox userPassword = new AonTextBox();
		AonTextBox userPIN = new AonTextBox();
		CheckItLoginFields loginType = new CheckItLoginFields();
		
		FlexTable registrationTable = new FlexTable();
		AonDialog dialog = new AonDialog("REGISTRAR CUENTA", registrationTable);
		Label bankLabel = new Label(checkItUnlinkedBankAccount.getBank());
		bankLabel.addStyleName(AON.AON_BOLD);
		bankLabel.getElement().getStyle().setProperty("margin-bottom", "1.5em");
		bankLabel.setHorizontalAlignment(Label.ALIGN_CENTER);
		bankLabel.getElement().getStyle().setFontSize(2, Unit.EM);
		bankLabel.addStyleName(AON.AON_NO_MARGIN);
		registrationTable.setWidget(0, 0, bankLabel);
		
		Label ibanLabel = new Label(checkItUnlinkedBankAccount.getIban());
		ibanLabel.setHorizontalAlignment(Label.ALIGN_CENTER);
		ibanLabel.getElement().getStyle().setFontSize(1.75, Unit.EM);
		registrationTable.setWidget(1, 0, ibanLabel);
		
		FlexTable loginTable = new FlexTable();
		loginTable.setWidth("100%");
		
		
		
		
		
		AonCheckItBankBox bankBox = new AonCheckItBankBox(opt.getDomainName(), opt.getDomain(), opt.getUser(), opt.getConfiguration().getBankIds());
		
		bankBox.setWidth("100%");
		
		
		
//		registrationTable.setWidget(2, 0, loginTable);
		
		FlexTable fieldsTable = new FlexTable();
		fieldsTable.addStyleName(AON.AON_CSS.aonJustifyContentSpaceBetween());
		fieldsTable.addStyleName(AON.CSS.aonBlockCenter());
		fieldsTable.setWidget(0, 0, new Label("Entidad:"));
		fieldsTable.setWidget(0, 1, bankBox);
		registrationTable.setWidget(2, 0, fieldsTable);
		
		
		bankBox.addSelectionHandler(new SelectionHandler<CheckItBank>() {

			@Override
			public void onSelection(SelectionEvent<CheckItBank> event) {
				if (bankBox.getId() != null) {
					CHECKIT_SERVICE.getLogins(bankBox.getId(), new AsyncCallback<List<CheckItLoginFields>>() {

						@Override
						public void onFailure(Throwable caught) {
							toolbar.showErrorMessage(caught.getMessage());
							
						}

						@Override
						public void onSuccess(List<CheckItLoginFields> result) {
							if (result != null && !result.isEmpty()) {
								fieldsTable.removeAllRows();
								fieldsTable.clear();
								fieldsTable.setWidget(0, 0, new Label("Entidad:"));
								fieldsTable.setWidget(0, 1, bankBox);
								if (result.size() > 1) {
									ListBox loginDropdown = new ListBox();
									loginDropdown.addItem("Seleccione un login");										
									
									for (CheckItLoginFields item : result) {
										loginDropdown.addItem(item.getType(), AonNumberUtils.toString(item.getId()));										
									}
									
									
									fieldsTable.setWidget(1, 0, new Label("Tipo de login:"));
									fieldsTable.setWidget(1, 1, loginDropdown);
									Label marginLabel = new Label();
									marginLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
									fieldsTable.setWidget(2, 0, marginLabel);
									
									loginDropdown.addChangeHandler(new ChangeHandler() {
										
										@Override
										public void onChange(ChangeEvent event) {
											
											while (fieldsTable.getRowCount() > 3) {
												fieldsTable.removeRow(fieldsTable.getRowCount() - 1);
											}
											
											if (loginDropdown.getSelectedIndex() != 0) {
												Integer typeId = AonNumberUtils.toint(loginDropdown.getSelectedValue());
												Optional<CheckItLoginFields> selected = result.stream().filter(f -> f.getId().equals(typeId)).findAny();
												
												if (selected.isPresent()) {
													loginType.clone(selected.get());
													checkItUnlinkedBankAccount.setLogin(loginType);
													
													int nextRow = fieldsTable.getRowCount();
													
													if (loginType.getUserID() != null && !loginType.getUserID().isEmpty()) {
														fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserID() + ":"));
														fieldsTable.setWidget(nextRow++, 1, userID);
													}
													if (loginType.getUserPassword() != null && !loginType.getUserPassword().isEmpty()) {
														fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPassword() + ":"));
														fieldsTable.setWidget(nextRow++, 1, userPassword);
													}
													if (loginType.getUserPIN() != null && !loginType.getUserPIN().isEmpty()) {
														fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPIN() + ":"));
														fieldsTable.setWidget(nextRow, 1, userPIN);
													}	

												}
											}
											
											
										}
									});
									
								} else {
									CheckItLoginFields loginType = result.get(0);
									checkItUnlinkedBankAccount.setLogin(loginType);
									
									int nextRow = fieldsTable.getRowCount();
									
									Label marginLabel = new Label();
									marginLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
									fieldsTable.setWidget(nextRow++, 0, marginLabel);
									
									if (loginType.getUserID() != null && !loginType.getUserID().isEmpty()) {
										fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserID() + ":"));
										fieldsTable.setWidget(nextRow++, 1, userID);
									}
									if (loginType.getUserPassword() != null && !loginType.getUserPassword().isEmpty()) {
										fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPassword() + ":"));
										fieldsTable.setWidget(nextRow++, 1, userPassword);
									}
									if (loginType.getUserPIN() != null && !loginType.getUserPIN().isEmpty()) {
										fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPIN() + ":"));
										fieldsTable.setWidget(nextRow, 1, userPIN);
									}
									
									
								}
							}
						}
					});
					checkItUnlinkedBankAccount.setBankId(bankBox.getId());
				}
			}
		});
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.CSS.aonBlockCenter());
		Button hai = new Button(AON.MSG.accept());
		Button iie = new Button(AON.MSG.cancelAction());
		hp.setWidth("50%");
		hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		hp.add(iie);
		hp.add(hai);
		registrationTable.setWidget(4, 0, hp);
		
		hai.addClickHandler(handler -> {
			dialog.hide();
			//TODO: AÑADIR LA CUNETA
			Integer enterpriseId = opt.getConfiguration().getEnterpriseId();
			String user = userID.getValue();
			String pass = userPassword.getValue();
			String pin = userPIN.getValue();
			CHECKIT_SERVICE.addAccount(enterpriseId, checkItUnlinkedBankAccount, user, pass, pin, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					String error = caught.getMessage();
					showErrorDialog(error);
					Label errorLabel = new Label("Se ha producido un error al a\u00F1adir la cuenta: \"" + error + "\"");
					errorLabel.setStyleName(AON.CSS.aonColorRed());
					sessionLog.add(errorLabel);
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						Label sccsLabel = new Label("A\u00F1adida la cuenta " + checkItUnlinkedBankAccount.getIban());
						sccsLabel.setStyleName(AON.CSS.aonColorGreen());
						sessionLog.add(sccsLabel);
						
						CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
							@Override
							public void onSuccess(CheckItConfiguration result) {
								opt.setConfiguration(result);
//								linkedBanks = paintBanks(opt);
								enterpriseData.remove(unlinkedBanks);
								enterpriseData.remove(linkedBanks);
								linkedBanks = paintBanks(opt);
								unlinkedBanks = paintUnlinkedBanks(opt);
								enterpriseData.add(linkedBanks);
								enterpriseData.add(unlinkedBanks);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
							}
						});
						
						
					} else {
						String error = "Se ha producido un error desconocido";
						showErrorDialog(error);
						Label errorLabel = new Label("Se ha producido un error desconocido al a\u00F1adir la cuenta");
						errorLabel.setStyleName(AON.CSS.aonColorRed());
						sessionLog.add(errorLabel);
					}
					
				}
			});
			
			
			
			
			
		});
		
		iie.addClickHandler(handler -> {
			dialog.hide();
			checkItUnlinkedBankAccount.setBankId(null);
			checkItUnlinkedBankAccount.setLogin(null);
			
		});
		dialog.center();
		container.add(dialog);
		dialog.show();
	}
	
	private void addCredentialsToAccount(CheckItBankAccount checkItBankAccount, CheckItModuleOptions opt) {
		
	}
	
	private AonDialog showErrorDialog(String error) {
		FlowPanel flowPanel = new FlowPanel();
		AonDialog dialog = new AonDialog("Se ha producido un error", flowPanel);
		flowPanel.add(new Label(error));
		Button ok = new Button(AON.MSG.accept());
		ok.addStyleName(AON.CSS.aonMarginTop());
		ok.addStyleName(AON.CSS.aonBlockCenter());
		ok.getElement().getStyle().setDisplay(Display.BLOCK);
		ok.addClickHandler(handler -> {
			dialog.hide();
			container.remove(dialog);
		});
		flowPanel.add(ok);
		container.add(dialog);
		dialog.show();
		dialog.center();
		return dialog;
	}
	
	private class AonCheckItUnlinkedBankCard extends AonCard {
		
		
		private AonCheckItUnlinkedBankCard(final CheckItModuleOptions opt, CheckitUnlinkedBankAccount checkItUnlinkedBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(checkItUnlinkedBankAccount.getBank());
			this.setTitle(title);
			
			FlowPanel body = new FlowPanel();
			FlowPanel movText = new FlowPanel();
			
			FlowPanel ibanPanel = new FlowPanel();
			InlineLabel ibanBox = new InlineLabel();
			ibanBox.setText(checkItUnlinkedBankAccount.getIban());
			ibanPanel.add(ibanBox);
			
			
			body.add(ibanPanel);
			
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton("Vincular", AON.CSS.aonIconUpload());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO REGISTER!
					paintBankRegistration(opt, checkItUnlinkedBankAccount);
					
					
					
				}
			});
			
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.setTabIndex(-2);
			deleteButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					deleteButton.setEnabled(false);
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm("Eliminar registro en Check IT?", new AonConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							deleteButton.setEnabled(true);
						}
						
						@Override
						public void onAccept() {
							// TODO REMOVE FROM CHEC IT
						}
					});
				}
			});
			getMenuPanel().add(saveButton);
//			getMenuPanel().add(deleteButton);
		}
		
	}
	
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(new MinimizeHandler() {
			
			@Override
			public void onMinimize(MinimizeEvent event) {
				minimizedByUser = true;
				closeFootPanel();
			}
		});
		footPanel.addMaximizeHandler(new MaximizeHandler() {
			
			@Override
			public void onMaximize(MaximizeEvent event) {
				openFootPanel();
			}
		});
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		sessionLog = new FlowPanel();
		footPanel.addStyleName(AON.AON_CSS.aonBackgroundWhite());
		
		// tabLayout.add(sessionLog, "Informaci\u00F3n");
		tabLayout.add(sessionLog, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.information(), AON.CSS.aonIconHistory()));
		footPanel.add(tabLayout);

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				minimizedByUser = false;
				openFootPanelIfNeeded();
			}
		});
		return footPanel; 
	}
	
	private void closeFootPanel() {
		centerLayoutPanel.setWidgetSize(footPanel, 30);
		centerLayoutPanel.animate(500);
	}
	
	private void openFootPanelIfNeeded() {
		if (!minimizedByUser && centerLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void openFootPanel() {
		
		int effectiveHeigth = 3;
		centerLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		centerLayoutPanel.animate(500);
	}
	

}		
