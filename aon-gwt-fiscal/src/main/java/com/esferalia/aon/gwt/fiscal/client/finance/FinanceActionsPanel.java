package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinancePayPanel.FinancePayPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceReturnPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceReturnPanel.FinanceReturnPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoiceFinanceTrackingPanel;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FinanceActionsPanel extends FlowPanel {

	public FinanceActionsPanel(Finance finance, FinanceModuleCallback cbk) {
		
		AON.ensureInjected();
		
		setStyleName(AON.CSS.aonFlexBlock());

		// *************************************************************************
		// *******															 *******
		// *******				TRACKING INFO BUTTON		 				 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton trackingButton = new AonTableButton(AON.MSG.tracking(), AON.CSS.aonIconHistory() );
		trackingButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cbk.getFinanceService().getFinanceTracking(cbk.getOptions().getDomainName()
						,cbk.getOptions().getDomain()
						,cbk.getOptions().getUser(), finance.getId()
						,new AsyncCallback<LinkedList<FinanceTracking>>() {
							@Override
							public void onFailure(Throwable caught) {
								Label label = new Label("Se ha producido un error al recuperar el historial del vencimiento. ["+caught.getMessage()+"]"); 
								cbk.addExtraInfo( label );
							}

							@Override
							public void onSuccess(LinkedList<FinanceTracking> list) {
								if (list == null || list.size() == 0) {
									Label label = new Label("No existen movimientos registrados del vencimiento.");
									label.setStyleName(AON.CSS.aonBlockMessage());
									label.addStyleName(AON.CSS.aonBlockInfoMessage());
									cbk.addExtraInfo( label );
								} else {
									InvoiceFinanceTrackingPanel trackingPanel = new InvoiceFinanceTrackingPanel(list);
									cbk.addExtraInfo( trackingPanel );
								}
							}
					
				});						
			}
		});
		
		// *************************************************************************
		// *******															 *******
		// *******				PAY BUTTON		 				 			 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton payButton = null;
		if (finance.isFullPending() && finance.getId() != null) {
			payButton = new AonTableButton(AON.MSG.toPay(), AON.CSS.aonIconFinancePay() );
			final AonCustomDialog dialog = new AonCustomDialog();
			String suffix = ( finance.isPayment()?" PAGO":" COBRO");
			dialog.setCaption(AON.MSG.payFinance() + suffix );
			payButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					FinancePayPanel payPanel = new FinancePayPanel();
					payPanel.show(cbk.getOptions().getDomainName()
							,cbk.getOptions().getDomain()
							,cbk.getOptions().getUser()
							,cbk.getOptions().getConfiguration(), finance,
							new FinancePayPanelCallback() {

								@Override
								public void onCancel() {
									dialog.hide();
								}

								@Override
								public void onAccept(FinanceTracking tracking) {
									dialog.hide();
									cbk.getFinanceService().payFinance(cbk.getOptions().getDomainName()
											,cbk.getOptions().getDomain()
											,cbk.getOptions().getUser(),tracking
											,new AsyncCallback<FinanceTracking>() {

												@Override
												public void onFailure(Throwable caught) {
													AonMessageDialog.error("Se ha producido un error al pagar el vencimiento. ["+caught.getMessage()+"]");
												}

												@Override
												public void onSuccess(FinanceTracking tracking) {
													cbk.updateAndRefresh( tracking.getFinance() );
												}
											});
								}
							});
					dialog.setWidget(payPanel);
					dialog.center();
					dialog.show();
				}
			});
		}
		
		// *************************************************************************
		// *******															 *******
		// *******				SETTLE BUTTON		 				 			 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton settleButton = null;
		if (finance.isFullPending() && finance.getId() != null) {
			settleButton = new AonTableButton(AON.MSG.toSettle(), AON.CSS.aonIconFinanceSettle() );
			settleButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.settleFinanceAction(), new AonConfirmDialogCallback(){

						@Override
						public void onAccept() {
							cbk.getFinanceService().settleFinance(cbk.getOptions().getDomainName()
									,cbk.getOptions().getDomain()
									,cbk.getOptions().getUser(), finance.getId()
									,new AsyncCallback<Finance>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessageDialog.error("Se ha producido un error al saldar el vencimiento. ["+caught.getMessage()+"]");
								}

								@Override
								public void onSuccess(Finance fin) {
									cbk.updateAndRefresh( fin );
								}

							});						
						}

						@Override
						public void onCancel() {
						}
					});
				}
			});
		}
		
		// *************************************************************************
		// *******															 *******
		// *******				UNSETTLE BUTTON		 				 		 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton undoButton = null;
		boolean canUndo = (finance.isSettled() && finance.getFinanceGroup() == null) || finance.isPaid() || finance.isReturned(); 
		if (canUndo && finance.getId() != null) {
			undoButton = new AonTableButton(AON.MSG.undoFinanceLastTracking(), AON.CSS.aonIconFinanceUndo() );
			undoButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.undoFinanceAction(), new AonConfirmDialogCallback(){

						@Override
						public void onAccept() {
							cbk.getFinanceService().undoFinance(cbk.getOptions().getDomainName()
									,cbk.getOptions().getDomain()
									,cbk.getOptions().getUser(), finance.getId()
									,new AsyncCallback<Finance>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessageDialog.error("Se ha producido un error al marcar el vencimiento como pendiente. ["+caught.getMessage()+"]"); 
								}

								@Override
								public void onSuccess(Finance fin) {
									cbk.updateAndRefresh( fin );
								}
							});						
						}

						@Override
						public void onCancel() {
						}
					});
				}
			});
		}

		// *************************************************************************
		// *******															 *******
		// *******				RETURN BUTTON		 				 			 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton returnButton = null;
		if (finance.isPaid() && finance.getId() != null) {
			returnButton = new AonTableButton(AON.MSG.toReturn(), AON.CSS.aonIconFinanceReturn() );
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption(AON.MSG.returnFinance());
			returnButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					FinanceReturnPanel returnPanel = new FinanceReturnPanel();
					returnPanel.show(cbk.getOptions().getDomainName()
							,cbk.getOptions().getDomain()
							,cbk.getOptions().getUser(),cbk.getOptions().getConfiguration(), finance,
							new FinanceReturnPanelCallback() {

								@Override
								public void onCancel() {
									dialog.hide();
								}

								@Override
								public void onAccept(FinanceTracking tracking) {
									dialog.hide();
									cbk.getFinanceService().returnFinance(cbk.getOptions().getDomainName()
											,cbk.getOptions().getDomain()
											,cbk.getOptions().getUser(), tracking
											,new AsyncCallback<FinanceTracking>() {

												@Override
												public void onFailure(Throwable caught) {
													AonMessageDialog.error("Se ha producido un devolver el pagar del vencimiento. ["+caught.getMessage()+"]");
												}

												@Override
												public void onSuccess(FinanceTracking tracking) {
													cbk.updateAndRefresh( tracking.getFinance() );
												}
											});
								}
							});
					dialog.setWidget(returnPanel);
					dialog.center();
					dialog.show();
				}
			});
		}

		// *************************************************************************
		// *******															 *******
		// *******				GROUP BUTTON		 				 		 *******
		// *******															 *******
		// *************************************************************************
		AonTableButton  groupedButton = null; 
		if (finance.isSettled() && finance.getFinanceGroup() != null) {
			groupedButton = new AonTableButton(AON.MSG.financeGrouped(), AON.CSS.aonIconFinanceGroup());
			groupedButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AonMessageDialog.show("No se puede deshacer.","El vencimiento pertence a una agrupaci\u00F3n de vencimientos");
				}
			});
		}

		ensureAdd(trackingButton );
		ensureAdd(payButton);
		ensureAdd(settleButton);
		ensureAdd(returnButton);
		ensureAdd(undoButton);
		ensureAdd(groupedButton);
	}
	
	private void ensureAdd(Widget widget) {
		if (widget == null) {
			widget = new Label();
			widget.setStyleName(AON.CSS.aonIconLabel());
		}
		widget.getElement().getStyle().setMarginRight(5, Unit.PX);
		add(widget);
	}

}		
