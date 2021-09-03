package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CheckItModule extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(CheckItModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static CheckItServiceAsync CHECKIT_SERVICE;
	
	private DockLayoutPanel dockLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonToolbar toolbar; 
	
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
		centerLayoutPanel = new SimpleLayoutPanel();
		dockLayoutPanel.add(centerLayoutPanel);
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		dockLayoutPanel.add(centerLayoutPanel);
		container.add(paintEnterpiseData( opt ) );
	}

	private FlowPanel paintEnterpiseData(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		CheckItConfiguration conf = opt.getConfiguration();
		if (conf.getEnterpriseId() != null) {
			paintRegistrationData( opt );
			panel.add( paintBanks(opt) );
		} else {
			panel.add( paintRegistration( opt ) );
		}
		return panel;
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
				// REGISTRAR LA EMPRESA EN CHECK IT
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
		if (opt.getConfiguration().getCheItBanks() != null && opt.getConfiguration().getCheItBanks().size() > 0) {
			AonCards cards = new AonCards();;
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
	
	private class AonCheckItBankCard extends AonCard {

		private AonCheckItBankCard(final CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(checkItBankAccount.getBank());
			this.setTitle(title);
			
			FlowPanel body = new FlowPanel();
			
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
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					// TODO IMPORT!
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
			getMenuPanel().add(deleteButton);
		}
		
	}
	

}		
