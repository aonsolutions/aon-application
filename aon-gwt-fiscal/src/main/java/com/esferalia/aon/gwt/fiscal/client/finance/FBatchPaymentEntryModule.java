package com.esferalia.aon.gwt.fiscal.client.finance;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentModule.FBATCH_TYPE;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
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
	private AonToolbarButton downloadButton;
	private AonToolbarButton excelButton;
	private AonToolbarButton deleteFileButton;
	private HTMLPanel messagePanel;

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
	
	private FBATCH_TYPE fbatchType;

	// -------------------------------------------------------------------
	// --------------------- ON MODULE LOAD ----------------------------
	// -------------------------------------------------------------------

	public FBatchPaymentEntryModule(FBATCH_TYPE fbatchType) {
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
		financesPanel.addWest(fBatchPaymentAviableList, fBatch.isGenerated() || fBatch.isAccounted() ? 0 : Window.getClientWidth() / 2);
		
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
				if(fBatch.isAccounted() || fBatch.isGenerated())
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
		sepaButton.setVisible(this.fBatch.getRattach() == null && !this.fBatch.getBatchDetails().isEmpty() && this.fBatch.getType() != (byte)0 && this.fBatch.getRbank() != null);
		downloadButton.setVisible(this.fBatch.getRattach() != null);
		deleteFileButton.setVisible(this.fBatch.getRattach() != null);
		excelButton.setVisible(this.fBatch.getRattach() != null);
	}
	
	private String getToolbarTitle() {
		if(FBATCH_TYPE.PAYROLL_PAYMENT == this.fbatchType)
			return "Remesa Transferencias N\u00f3minas";
		else if(FBATCH_TYPE.PAYMENT == this.fbatchType)
			return "Remesa Pagos";
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
				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>' ha sido generado correctamente."));
				
				fBatch.setRattach(rattachId);
				fBatch.setStatus(FBatchStatus.GENERATED);
				
				showHideToolbarButtons();

				FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch savedFbatch) {
						fBatch = savedFbatch;
						hasSaved = true;
						fBatchPaymentAviableList.setFBatch(fBatch);
						fBatchPaymentBatchedList.setFBatch(fBatch);
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
				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>' ha sido eliminado correctamente."));
				
				fBatch.setRattach(null);
				fBatch.setStatus(FBatchStatus.PENDING);
				
				showHideToolbarButtons();

				FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch savedFbatch) {
						hasSaved = true;
						fBatch = savedFbatch;
						fBatchPaymentAviableList.setFBatch(fBatch);
						fBatchPaymentBatchedList.setFBatch(fBatch);
						
						sepaButton.setVisible(!savedFbatch.getBatchDetails().isEmpty() && fBatch.getRbank() != null);
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

	public abstract void back(boolean refresh);

}
