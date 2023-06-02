package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProgressBarDialog;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.shared.AccountEntryImportClass;
import com.esferalia.aon.gwt.template.shared.AccountImportClass;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass;
import com.esferalia.aon.gwt.template.shared.RegistryImportClass;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.ImportError;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportContent extends Composite {
	final ITemplateAsync item = GWT.create(ITemplate.class);
	private static RegistryServiceAsync SERVICE;

	interface PageBinder extends UiBinder<Widget, ImportContent> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField HTMLPanel htmlPanel;
	@UiField ListBox typeList;
	@UiField Button importButton;
	
	AonData aonData;	
	AonProgressBarDialog pbd;
	LinkedList<String> verror = new LinkedList<>();
	LinkedList<String> werror = new LinkedList<>();

	private AonData getAonData() {
		return aonData;
	}
	
	private Domain getDomain() {
		return getAonData().getDomain();
	}
	
	private User getUser() {
		return getAonData().getUser();
	}
	
	
	public ImportContent(AonData aonData) {
		this.aonData = aonData;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		importButton = new Button();
		initWidget(pageBinder.createAndBindUi(this));
		init();
	}
	
	private void init() {
		typeList.addItem("Facturas", ImportType.INVOICE.name());
		typeList.addItem("Clientes, Proveedores y Acreedores", ImportType.REGISTRY.name());
		typeList.addItem("Plan General Contable",ImportType.PGC.name());
		typeList.addItem("Libro Diario", ImportType.DIARY.name());
		typeList.addItem("Cuotas", ImportType.FEE.name());
		typeList.setSelectedIndex(0);
	}
	
	@UiHandler("importButton")
	void importAction(ClickEvent event) {
		ImportType type = ImportType.valueOf(typeList.getSelectedValue());
		importation(type);
	}	
	
	private void importation(ImportType type){	
		verror = new LinkedList<>();
		werror = new LinkedList<>();
		Upload upload = new Upload() {
			
			@Override
			protected void onUpload(String data) {
				pbd = new AonProgressBarDialog("Procesando Excel...") {};
				pbd.addStyleName("gwt-PopupPanel-template");
				pbd.setGlassEnabled(true);
				pbd.show();
				
				if(ImportType.INVOICE.equals(type)) {
					item.executeInvoice(getDomain(), getUser(), data, new AsyncCallback<List<InvoiceImportClass>>() {
						@Override
						public void onSuccess(List<InvoiceImportClass> result) {
							loadingAonProgressBar(ImportType.INVOICE);
							insertInvoices(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else if(ImportType.REGISTRY.equals(type)) {
					item.executeRegistry(getDomain(), getUser(), data, new AsyncCallback<List<RegistryImportClass>>() {
						@Override
						public void onSuccess(List<RegistryImportClass> result) {
							loadingAonProgressBar(ImportType.REGISTRY);
							insertRegistries(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else if(ImportType.PGC.equals(type)) { 
					item.executePGC(getDomain(), getUser(), data, new AsyncCallback<List<AccountImportClass>>() {
						@Override
						public void onSuccess(List<AccountImportClass> result) {
							loadingAonProgressBar(ImportType.PGC);
							insertPGC(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else if(ImportType.DIARY.equals(type)) {
					item.executeDiary(getDomain(), getUser(), data, new AsyncCallback<List<AccountEntryImportClass>>() {
						@Override
						public void onSuccess(List<AccountEntryImportClass> result) {
							loadingAonProgressBar(ImportType.DIARY);
							insertDiary(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else if(ImportType.FEE.equals(type)) {
					SERVICE.parseFeeFile(getDomain(), getUser(), data, new AsyncCallback<List<Fee>>() {
						@Override
						public void onSuccess(List<Fee> result) {
							loadingAonProgressBar(ImportType.FEE);
							insertFee(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				
			}
		};
		upload.upload();
	}
	
	private void loadingAonProgressBar(ImportType type) {
		pbd.completed();
		pbd.hide();
		pbd = new AonProgressBarDialog("Importando " + type.getName() + "...") {};
		pbd.addStyleName("gwt-PopupPanel-template");
		pbd.setGlassEnabled(true);
		pbd.center();
		pbd.show();
	}
	
	private void insertInvoices(List<InvoiceImportClass> invoices, Integer index) {
		Integer lines = invoices.size();
		AsyncCallback<Error> callback = new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertInvoices(invoices, result.getLine() + 1);
				} else error(ImportType.INVOICE);
			}
				
			@Override public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				pbd.completed();
				pbd.hide();
			}
		};
		item.insertInvoice(getDomain(), getUser(), invoices.get(index), index, callback);
	}
	
	private void insertRegistries(List<RegistryImportClass> registries, Integer index) {
		Integer lines = registries.size();
		AsyncCallback<Error> callback = new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertRegistries(registries, result.getLine() + 1);
				} else error(ImportType.REGISTRY);
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};
		
		item.insertRegistry(getDomain(), getUser(), registries.get(index), index, callback);
	}
	
	private void insertPGC(List<AccountImportClass> pgc, Integer index) {
		Integer lines = pgc.size();
		AsyncCallback<Error> callback = new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertPGC(pgc, result.getLine() + 1);
				} else error(ImportType.PGC);
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};
		
		item.insertPGC(getDomain(), getUser(), pgc.get(index), index, callback);
	}
	
	private void insertDiary(List<AccountEntryImportClass> diary, Integer index) {
		Integer lines = diary.size();
		AsyncCallback<Error> callback = new AsyncCallback<Error>() {
			@Override
			public void onSuccess(Error result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertDiary(diary, result.getLine() + 1);
				} else error(ImportType.DIARY);
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};
		
		item.insertDiary(getDomain(), getUser(), diary.get(index), index, callback);
	}
	
	private void insertFee(List<Fee> fees, Integer index) {
		Integer lines = fees.size();
		AsyncCallback<ImportError> callback = new AsyncCallback<ImportError>() {
			@Override
			public void onSuccess(ImportError result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertFee(fees, result.getLine() + 1);
				} else error(ImportType.FEE);
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};
		SERVICE.importFee(getDomain(), getUser(), fees.get(index), index, callback);
	}
	
	private void error(ImportType type) {
		pbd.completed();
		pbd.hide();
		
		ImportError error = new ImportError();
		error.setError(verror.isEmpty());
		error.setTextError(verror);
		error.setTextWarning(werror);
		
		VerticalPanel vPanel = new VerticalPanel();
		error.getTextError().forEach(errorIt -> {
			Label label = new Label(errorIt);
			label.getElement().getStyle().setColor("red");
			vPanel.add(label);
		});
		error.getTextWarning().forEach(warnIt -> {
			Label label = new Label(warnIt);
			label.getElement().getStyle().setColor("orange");
			vPanel.add(label);
		});
		if(verror.isEmpty() && werror.isEmpty()) {
			vPanel.add(new Label("La importaci\u00f3n se ha realizado correctamente."));
		}
		AonDialog dialog = new AonDialog("Importar " + type.getName(), vPanel);
		dialog.info();
	}
}
