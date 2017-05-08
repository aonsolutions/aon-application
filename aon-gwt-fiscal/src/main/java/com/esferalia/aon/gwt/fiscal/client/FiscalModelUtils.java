package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class FiscalModelUtils {
	
	public static void paintHeaderTable(SimplePanel headerPanel, FiscalModel fm) {
		Administration admon = (fm == null?Administration.COMMON_TERRITORY:fm.getAdministration());
		Period period = fm.getPeriod();
		
		headerPanel.clear();
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(getAdministrationImage(admon));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(fm.getModel().getName(admon,period))); 
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		headerTable.setWidget(0, 2, new Label(AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		headerTable.setWidget(0, 3, new Label(""+fm.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 3, getAdministrationBG(admon));
		
		headerTable.setWidget(1, 0, new Label(fm.getPeriod().getDescription()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG(admon));

		headerPanel.setWidget(headerTable);
	}

	public static String getAdministrationBG(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonFiscalArabaBg();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonFiscalBizkaiaBg();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonFiscalGipuzkoaBg();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonFiscalNavarraBg();
		} 
		return AON.AON_CSS.aonFiscalAeatBg();
	}

	public static String getAdministrationImage(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonArabaHeaderImage();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonBizkaiaHeaderImage();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonGipuzkoaHeaderImage();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonNavarraHeaderImage();
		} 
		return AON.AON_CSS.aonAeatHeaderImage();
	}
	
	public static String getAdministrationIcon(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonIconAraba();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaia();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoa();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarra();
		} 
		return AON.AON_CSS.aonIconAeat();
	}

	public static String getAdministrationIconBW(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonIconArabaBW();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaiaBW();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoaBW();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarraBW();
		} 
		return AON.AON_CSS.aonIconAeatBW();
	}

	public static ImageResource getAdministrationIconResource(Administration adm) {
		if (adm ==Administration.ALAVA) {
			return AON.AON_RESOURCES.aonIconAraba();	
		} else if (adm ==Administration.BIZKAIA) {
			return AON.AON_RESOURCES.aonIconBizkaia();
		} else if (adm ==Administration.GIPUZKOA) {
			return AON.AON_RESOURCES.aonIconGipuzkoa();
		} else if (adm ==Administration.NAVARRA) {
			return AON.AON_RESOURCES.aonIconNavarra();
		} 
		return AON.AON_RESOURCES.aonAeat();
	}
/*
	public static <T extends FiscalModel> CustomDialog getFinalizeDialog(final IFiscalModelCallback<T> callback) {
		final CustomDialog finalizeDialog = new CustomDialog();
		finalizeDialog.setCaption(AON.MSG.finish());
		finalizeDialog.setGlassEnabled(true);
		finalizeDialog.setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "450px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		FlexCellFormatter fmt = tab.getFlexCellFormatter();
		
		int row = 0;
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.fiscalDebt()));
		fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonFontBig());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPaddingRight());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.setWidget(row, 1, new Label( AON.FMT.format(callback.getFiscalModel().getResult())));
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.declarationType()));
		fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		if (callback.getFiscalModel().getDeclarationType() == FiscalModelDeclarationType.NEGATIVE) {
			fmt.addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			fmt.addStyleName(row, 1, AON.AON_CSS.aonBold());
			tab.setWidget(row, 1, new Label( FiscalModelDeclarationType.NEGATIVE.getDescription() ));
		} else {
			final CreditorBox creditorBox = new CreditorBox(callback.getDomainName(),callback.getDomain() );
			final IbanTextBox iban = new IbanTextBox( new EnterpriseSuggestOracle<T>(callback) );
			
			final ListBox listBox = new ListBox();
			listBox.setSelectedIndex(0);
			listBox.addItem(FiscalModelDeclarationType.DEPOSIT.getDescription(), FiscalModelDeclarationType.DEPOSIT.getValue());
			listBox.addItem(FiscalModelDeclarationType.BANK.getDescription(), FiscalModelDeclarationType.BANK.getValue());
			if (callback.getFiscalModel().isAEAT()) {
				listBox.addItem(FiscalModelDeclarationType.CCT.getDescription(), FiscalModelDeclarationType.CCT.getValue());
			}
				listBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						FiscalModelDeclarationType type = FiscalModelDeclarationType.safeValueOf(listBox.getSelectedValue());
						callback.getFiscalModel().setDeclarationType( type );
						iban.setEnabled( type.isBankRequired() );
						creditorBox.setEnabled(type.mustCreateFinance());
					}
				});
				tab.setWidget(row, 1, listBox );
				row++;
			
			fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			Finance finance = callback.getFiscalModel().getFinance();
			creditorBox.setValue(
				new Creditor()
					.setRegistry(finance.getRegistry())
					.setId(finance.getRegistry()==null?null:finance.getRegistry().getId())
				);
			creditorBox.addSelectionHandler(new SelectionHandler<Creditor>() {
				
				@Override
				public void onSelection(SelectionEvent<Creditor> event) {
					Registry registry = event.getSelectedItem().getRegistry();
					callback.getFiscalModel().getFinance().setRegistry(registry);
					callback.getFiscalModel().getFinance().setRegistryDocument(registry.getDocument());
					callback.getFiscalModel().getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
					callback.getFiscalModel().getFinance().setRegistryDocumentType(registry.getDocumentType());
					callback.getFiscalModel().getFinance().setRegistryName(registry.getName());
				}
			});
			tab.setWidget(row, 1, creditorBox);
			row++;
	
			fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.bankAccount()));
			fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			
			tab.setWidget(row, 1, iban);
			iban.setEnabled( false );
			iban.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
					IIbanContainer cont = suggestion.getIbanContainer();
					iban.setValue(cont.getIBan());
					BankAccount bankAccount = new BankAccount(cont.getIBan());
					callback.getFiscalModel().getFinance().setBankAccount(bankAccount);
					callback.getFiscalModel().getFinance().setBankAlias(cont.getAlias());
					callback.getFiscalModel().getFinance().setBic(cont.getBic());
				}
			});
		}
		
		row++;
	
		fmt.setColSpan(row, 0, 2);
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		flowPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		flowPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				finalizeDialog.hide();
				callback.doFinish();
			}
		});
		
		flowPanel.add(acceptButton);
		Button cancelButton = new Button();
		cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
		cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {
	
			@Override
			public void onClick(ClickEvent event) {
				finalizeDialog.hide();
			}
			
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);
		finalizeDialog.add(tab);
		return finalizeDialog;
	}
*/	
	public static <T extends FiscalModel> void paintPaymentInfo(FlowPanel paymentInfo,T mod) {
		paymentInfo.clear();
		paymentInfo.setVisible(mod.isFinished());
		InlineLabel l1 = new InlineLabel(AON.MSG.result());
		l1.setStyleName(AON.AON_CSS.aonInnerLabel());
		paymentInfo.add(l1);
		InlineLabel l2 = new InlineLabel(AON.FMT.format(mod.getResult()));
		l2.setStyleName(AON.AON_CSS.aonInnerLabel());
		l2.addStyleName(AON.AON_CSS.aonBold());
		paymentInfo.add(l2);
		if (mod.getDeclarationType() != null) {
			InlineLabel l3 = new InlineLabel(mod.getDeclarationType().getDescription());
			l3.setStyleName(AON.AON_CSS.aonInnerLabel());
			l3.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l3);
		}
		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null) {
			InlineLabel l4 = new InlineLabel(mod.getFinance().getBankAccount().getIban());
			l4.setStyleName(AON.AON_CSS.aonInnerLabel());
			l4.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l4);
			
			InlineLabel l5 = new InlineLabel(mod.getFinance().getBankAlias());
			l5.setStyleName(AON.AON_CSS.aonInnerLabel());
			paymentInfo.add(l5);
		}
	}
	
	public static FlowPanel getAnchorPanel(IFiscalModel model, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(model.getAdministration()));
		p.add(a);
		return p;
	}
}


