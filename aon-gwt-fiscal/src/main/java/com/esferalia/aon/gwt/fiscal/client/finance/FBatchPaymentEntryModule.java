package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentEntryModule extends SimpleLayoutPanel {

	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	private DockLayoutPanel dockLayoutPanel;

	// Toolbar

	private AonToolbar toolbar;
	private AonToolbarButton backButton;
	private AonToolbarButton sepaButton;
	private AonToolbarButton recordButton;
	private AonToolbarButton unrecordButton;
	private AonToolbarButton downloadButton;
	private AonToolbarButton excelButton;
	private AonToolbarButton deleteFileButton;
	
	private HTMLPanel messagePanel;
	private String  pendingMessage;
	private boolean pendingMessageIsError;
	
	private HTMLPanel container;

	// FBatchDetail

	private SplitLayoutPanel financesPanel;

	// FBatchDetail (Aviable)

	private FBatchPaymentAviableList fBatchPaymentAviableList;

	// FBatchDetail (Selected)
	
	private FBatchPaymentBatchedList fBatchPaymentBatchedList;

	// Variables

	private FBatch fBatch;
	private FinanceModuleOptions opt;

	private boolean hasSaved;
	
	private FBatchType fbatchType;

	// -------------------------------------------------------------------
	// --------------------- ON MODULE LOAD ----------------------------
	// -------------------------------------------------------------------

	public FBatchPaymentEntryModule(FBatchType fbatchType) {
		this.fbatchType = fbatchType;
	}

	public void onModuleLoad(final FinanceModuleOptions opt, FBatch fBatchIn) {
		AON.ensureInjected();

		this.clear();

		this.opt = opt;
		this.hasSaved = false;

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.add(dockLayoutPanel);
		
		FINANCE_SERVICE.getFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatchIn.getId(), new AsyncCallback<FBatch>() {
			@Override
			public void onSuccess(FBatch fBatchDB) {
				fBatch = fBatchDB;

				if (opt.getConfiguration() == null) {
					COMMON_SERVICE.getAonConfiguration(opt.getDomainName(), opt.getDomain(), opt.getUser(),
							new AsyncCallback<AonConfiguration>() {
								@Override
								public void onSuccess(AonConfiguration result) {
									opt.setConfiguration(result);
									loadModule(opt);
								}

								@Override
								public void onFailure(Throwable caught) {
									dockLayoutPanel.add(new Label(
											AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage() + "]"));
								}
							});
				} else {
					loadModule(opt);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage() + "]"));
			}
		});
	}

	private void loadModule(final FinanceModuleOptions opt) {
		dockLayoutPanel.addNorth(getToolbarPanel(opt), AonToolbar.HEIGTH);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		final boolean hadPendingMessage = pendingMessage != null;
		if (hadPendingMessage) {
			if (pendingMessageIsError)
				AonMessagePanel.showError(messagePanel, new HTMLPanel(pendingMessage));
			else
				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel(pendingMessage));
			pendingMessage = null;
		}

		financesPanel = new SplitLayoutPanel();
		financesPanel.setHeight("100%");
		
		fBatchPaymentAviableList = new FBatchPaymentAviableList(opt, fbatchType, fBatch) {
			
			@Override
			protected void saveFBatch(FBatch fBatchModufy) {
				fBatch = fBatchModufy;
				save();
			}

			@Override
			protected void showError(String message) {
				AonMessagePanel.showError(messagePanel, message);
			}

			@Override
			protected void hideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}
			
		};
		financesPanel.addWest(fBatchPaymentAviableList, fBatch.isGenerated() || fBatch.isRecorded() ? 0 : Window.getClientWidth() / 2);
		
		fBatchPaymentBatchedList = new FBatchPaymentBatchedList(opt, fbatchType, fBatch) {
			
			@Override
			protected void saveFBatch(FBatch fBatchModufy) {
				fBatch = fBatchModufy;
				save();
			}
		};
		
		financesPanel.add(fBatchPaymentBatchedList);
		
		container.add(financesPanel);

		dockLayoutPanel.add(container);
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				if(!hadPendingMessage && (fBatch.isRecorded() || fBatch.isGenerated()))
 					AonMessagePanel.showInfo(messagePanel, "Para poder modificar un vencimiento con estado " + fBatch.getStatus().getDescription() + " se debe eliminar primero el fichero generado");
			}
		};
		
		timer.schedule(1500);
	}

	// -------------------------------------------------------------------
	// --------------------- TOOLBAR --------------------------
	// -------------------------------------------------------------------

	private Widget getToolbarPanel(final FinanceModuleOptions opt) {
		toolbar = new AonToolbar(getToolbarTitle());

		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);

		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		Hidden rattachHidden = new Hidden("rattach");
		Hidden fbatchhHidden = new Hidden("fbatch");
		Hidden attachTypeHidden = new Hidden("attachType");

		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(rattachHidden);
		formFlowPanel.add(fbatchhHidden);
		formFlowPanel.add(attachTypeHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		backButton = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> back(hasSaved));
		toolbar.add(backButton);
		
		sepaButton = new AonToolbarButton("Crear fichero SEPA", AON.CSS.aonIconXml());
		sepaButton.addClickHandler(e -> createSepaFile());
		toolbar.add(sepaButton);

		recordButton = new AonToolbarButton(AON.MSG.record(), AON.CSS.aonIconAccountingRecord());
		recordButton.addClickHandler(e -> recordFBatch());
		toolbar.add(recordButton);

		unrecordButton = new AonToolbarButton(AON.MSG.unrecord(), AON.CSS.aonIconAccountingUnrecord());
		unrecordButton.addClickHandler(e -> unrecord());
		// TODO ------ test!!
		unrecordButton.setVisible(false);
		// ------------------
		toolbar.add(unrecordButton);

		downloadButton = new AonToolbarButton(AON.MSG.download() + " fichero SEPA", AON.CSS.aonIconDownload());
		downloadButton.addClickHandler(e -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + "/ms/download_attachment/");

			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(opt.getDomain() + "");
			domainNameHidden.setValue(opt.getDomainName());
			userHidden.setValue(opt.getUser());

			diskForm.submit();
		});
		toolbar.add(downloadButton);

		deleteFileButton = new AonToolbarButton(AON.MSG.deleteAction() + " fichero SEPA", AON.CSS.aonIconDeleteFile());
		deleteFileButton.addClickHandler(e -> deleteSepaFile());
		toolbar.add(deleteFileButton);
		
		excelButton = new AonToolbarButton("Relaci\u00f3n Remesa Bacaria", AON.CSS.aonIconExcel());
		excelButton.addClickHandler(e -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + "ms/api/fbatchPaymentReport");

			fbatchhHidden.setValue(fBatch.getId().toString());
			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(opt.getDomain() + "");
			domainNameHidden.setValue(opt.getDomainName());
			userHidden.setValue(opt.getUser());

			diskForm.submit();
		});
		toolbar.add(excelButton);
		
		showHideToolbarButtons();

		return toolbar;
	}
	
	private void showHideToolbarButtons(){
		sepaButton.setVisible(this.fBatch.getRattach() == null && !this.fBatch.getBatchDetails().isEmpty() && FBatchType.isSepaFile(this.fBatch.getType()) && this.fBatch.getRbank() != null);
		downloadButton.setVisible(this.fBatch.getRattach() != null);
		deleteFileButton.setVisible(this.fBatch.getRattach() != null);
		excelButton.setVisible(this.fBatch.getRattach() != null);
		recordButton.setVisible(this.fBatch.getId() != null && this.fBatch.isNotRecorded());
		unrecordButton.setVisible(this.fBatch.getId() != null && this.fBatch.isRecorded());
	}
	
	private String getToolbarTitle() {
		if(FBatchType.PAYROLL_PAYMENT == this.fbatchType)
			return "Remesa Transferencias N\u00f3minas";
		else if(FBatchType.PAYMENT == this.fbatchType)
			return "Remesa Pagos";
		else if(FBatchType.CHARGE == this.fbatchType)
			return "Remesa Cobros";
		else
			return "Tipo Remesa Desconocido";
	}

	private void save() {
		AonMessagePanel.showLoading(messagePanel, "Guardando la remesa '" + fBatch.getDescription() + "'...");
		
		FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {

			@Override
			public void onSuccess(FBatch savedFbatch) {
				fBatch = savedFbatch;
				hasSaved = true;
				fBatchPaymentAviableList.setFBatch(fBatch);
				fBatchPaymentBatchedList.setFBatch(fBatch);
				
				showHideToolbarButtons();

				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("La remesa '<b>" + fBatch.getDescription() + "</b>' ha sido guarda correctamente."));
			}

			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
			}
			
		});
	}

	private void createSepaFile() {
		AonMessagePanel.showLoading(messagePanel, "Generando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		
		FINANCE_SERVICE.createSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getId(), new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer rattachId) {
				fBatch.setRattach(rattachId);
				fBatch.setStatus(FBatchStatus.GENERATED);

				FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch savedFbatch) {
						reload(savedFbatch, "El fichero SEPA de la remesa '<b>" + savedFbatch.getDescription() + "</b>' ha sido generado correctamente.", false);
								 					
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
					
				});
			}

			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al generar el fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
			}
			
		});
	}

	private void deleteSepaFile() {
		AonMessagePanel.showLoading(messagePanel, "Eliminando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		
		FINANCE_SERVICE.deleteSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getRattach(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void seccess) {
				fBatch.setRattach(null);
				fBatch.setStatus(FBatchStatus.PENDING);

				FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch savedFbatch) {
						reload(savedFbatch, "El fichero SEPA de la remesa '<b>" + savedFbatch.getDescription() + "</b>' ha sido eliminado correctamente.", false);		 					
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
			}

			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al eliminar el fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
			}
			
		});
	}

	private void recordFBatch() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.record());
		RecordPanel recordPanel = new RecordPanel();
		recordPanel.show(fBatch.getIssueDate(), new RecordPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(Date date) {
				dialog.hide();
				FINANCE_SERVICE.recordFBatch(opt.getOccam(), fBatch.getId(), date, new AsyncCallback<FBatch>() {
					
					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al contabilizar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
					
					@Override
					public void onSuccess(FBatch fBatch) {
						reload(fBatch, "Remesa contabilizada correctamente.", false);
					}
					
				});
			}
		});
		dialog.add( recordPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> recordPanel.setFocus(true));		
		
		
	}
	private void unrecord() {
		AonConfirmDialog.showConfirm(AON.MSG.unrecord()
			, "\u00BFContinuar con la descontabilizaci\u00f3n de la remesa?"
			, new AonConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				FINANCE_SERVICE.unrecordFBatch(opt.getOccam(), fBatch.getId(), new AsyncCallback<FBatch>() {
					
					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al descontabilizar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
					
					@Override
					public void onSuccess(FBatch fBatch) {
						reload(fBatch,"Remesa descontabilizada correctamente.", false);
					}
					
				});
			}
			
			@Override
			public void onCancel() {
				
			}
		});
	}

	private static interface RecordPanelCallback {
		void onAccept(Date date);
		void onCancel();
	}
	
	private static class RecordPanel extends SimplePanel implements Focusable {

		private AonDateBox issueDate; 
		
		void show(final Date date, final RecordPanelCallback callback) {
			setWidth("400px");
			setHeight("150px");
			
			FlowPanel rootPanel = new FlowPanel();
			rootPanel.setStyleName(AON.CSS.aonMarginTop());
			
			FlowPanel tablePanel = new FlowPanel();
			tablePanel.setStyleName(AON.CSS.aonScrollArea());
			
			KeyUpHandler keyUpHandler = event -> {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			};
			
			FlexTable table = new FlexTable();
			table.setStyleName(AON.CSS.aonTable());
			int row = 0;
			
			table.setWidget(row,0,new InlineLabel(AON.MSG.accountingDate()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			issueDate = new AonDateBox();
			issueDate.setValue(date);
			issueDate.getTextBox().addKeyUpHandler( keyUpHandler);
			table.setWidget(row,1,issueDate);
			
			tablePanel.add( table );
			rootPanel.add( tablePanel );
			
			FlowPanel buttons = new FlowPanel();
	    	buttons.setStyleName(AON.CSS.aonTextCenter());
	    	
	    	final Button okButton = new Button();
	    	okButton.setStyleName(AON.CSS.aonOkButton());
	    	okButton.setText( AON.MSG.accept());
	    	okButton.addKeyUpHandler( keyUpHandler);
	    	okButton.addClickHandler(event -> {
				okButton.setEnabled(false);
				callback.onAccept(issueDate.getValue());
			});
	    	
	    	buttons.add(okButton);
	    	
	    	final Button cancelButton = new Button();
	    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
	    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
	    	cancelButton.setText( AON.MSG.cancelAction());
	    	cancelButton.addKeyUpHandler( keyUpHandler);
	    	cancelButton.addClickHandler(event -> {
				cancelButton.setEnabled(false);
				callback.onCancel();
			});
	    	buttons.add(cancelButton);
	    	rootPanel.add(buttons);
			setWidget(rootPanel);
			issueDate.setFocus(true);
		}

		@Override
		public int getTabIndex() {
			return issueDate.getTabIndex();
		}

		@Override
		public void setAccessKey(char key) {
			issueDate.setAccessKey(key);
		}

		@Override
		public void setFocus(boolean focused) {
			issueDate.getTextBox().selectAll();
			issueDate.setFocus(focused);
			issueDate.hideDatePicker();
		}

		@Override
		public void setTabIndex(int index) {
			issueDate.setTabIndex(index);
		}

	}

	public void setHasSaved(boolean isNewFBatch) {
		hasSaved = isNewFBatch;
	}
	
	/** Recarga el modulo para que el split se repinte segun el nuevo estado. */
	private void reload(FBatch fBatchIn, String message, boolean isError) {
	    pendingMessage = message;
	    pendingMessageIsError = isError;

	    onModuleLoad(opt, fBatchIn);
	    hasSaved = true;   // onModuleLoad lo pone a false de forma sincrona
	}
		
	public abstract void back(boolean refresh);

}
