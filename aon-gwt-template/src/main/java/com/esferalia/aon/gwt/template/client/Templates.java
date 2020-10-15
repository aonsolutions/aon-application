package com.esferalia.aon.gwt.template.client;



import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplaceAsync;
import com.esferalia.aon.gwt.template.client.marketplace.Marketplace;
import com.esferalia.aon.gwt.template.client.payroll.ContractMediaPage;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.ExportInfo;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Templates extends Composite implements EntryPoint {

	private static final String SILENT = "silent";
	private static final String IMPORT = "import";
	private static final String MARKETPLACE = "marketplace";
	private static final String CONSUMPTION = "consumption";
	private static final String TEMPLATES = "templates";
	private static final String DOWNLOAD_AMAZON_DELIVERY = "download_amazon_delivery";
	private static final String CONTRACT_MEDIA = "contract_media";
	private static final String IMPORT_ONLY= "importOnly";
	
	final ITemplateAsync item = GWT.create(ITemplate.class);
	final IMarketplaceAsync mpimpl = GWT.create(IMarketplace.class);

	interface Binder extends UiBinder<Widget, Templates> {

	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField FlowPanel pagesPanel;
	
	ListBox list_box = new ListBox();
	LinkedList<TemplateInfo> templateList;
	TemplatesDialog popup;
	ProgressBarDialog pbd;
	ExportInfo eiAux;
	Boolean closeInventoryAux;	
	TemplateInfo tiAux;

	AonData aonData;
	API API;
	Templates me = this;
	
	public Templates(AonData aonData){
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}

	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain() {
		return getAonData().getDomain();
	}
	
	public User getUser() {
		return getAonData().getUser();
	}
	
	LinkedList<Warehouse> ws;
	Integer inventory, workplaceId, proposalId, num, inventoryId;
	String closedAux, inventoryIdAux, warehouseAux, warehouse2Aux, initialDateAux, finalDateAux, initialIdAux,
			finalIdAux, onlyNegativeAux, detailAux, w, wAux, incomeId, seriesAux, commentsAux;
	
	public void onModuleLoad(String entryPoint){
		if(entryPoint.equals(CONTRACT_MEDIA)){
			new ContractMediaPage(aonData).onModuleLoad();
		} else if(entryPoint.equals(IMPORT)){
			GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
			GWT.<AonResources> create(AonResources.class).css().ensureInjected();
			GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
			
			new ImportPage(aonData).onModuleLoad();
		} else {
			GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
			GWT.<AonResources> create(AonResources.class).css().ensureInjected();
			GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
			pagesPanel = new FlowPanel();
			if (entryPoint.equals(SILENT)){
				item.getTemplates(getDomain(), getUser(), new AsyncCallback<LinkedList<TemplateInfo>>() {		
					@Override
					public void onSuccess(LinkedList<TemplateInfo> result) {
						templateList = result;
						exportEcommerce(me);
						exportEcommercex(me);
						exportProduct(me);
						exportStock(me);
						exportTransferStock(me);
						exportFee(me);
						exportProductx(me);
						exportStockx(me);
						exportStockx2(me);
						exportTransferStockx(me);
						exportFeex(me);
						exportCataloguex(me);
						exportProposal(me);
						exportProposalx(me);	
						exportInventoryx(me);
						exportIncomex(me);
						exportDeliveryx(me);
						exportDelivery(me);
						exportProjectCommercial(me);
						exportProjectCommercialx(me);
						exportPGCx(me);
						exportOfferx(me);
						exportOfferPdfx(me);
						exportFullExpedient(me);
						exportResumeExpedient(me);
						exportCustomerIban(me);
					}
					@Override
					public void onFailure(Throwable caught) {print(caught);}
				});
			} else if(entryPoint.equals(DOWNLOAD_AMAZON_DELIVERY)){
				deliveryx();
			} else {
				item.getTemplates(getDomain(), getUser(), new AsyncCallback<LinkedList<TemplateInfo>>() {		
					@Override
					public void onSuccess(LinkedList<TemplateInfo> result) {
						templateList = result;						
						if(entryPoint.equals(TEMPLATES)){
							Widget w = new TemplatesPage(getAonData(), templateList);
							pagesPanel.add(w);
						} else if(entryPoint.equals(MARKETPLACE)){
							Marketplace marketplace = new Marketplace(getAonData());		
							pagesPanel.add(marketplace);
						} else if(entryPoint.equals(CONSUMPTION)){
							ConsumptionPage cp = new ConsumptionPage(getAonData(), templateList);		
				 			pagesPanel.add(cp);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {print(caught);}
				});
			}
		
			Widget ui = binder.createAndBindUi(this);
			RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
			root.add(ui);	
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "templates";
		onModuleLoad(entryPoint);
	}	
	
//------------------------------ Utils	
	Boolean ignoreInactiveClientAux;
	private void importFee(){
		Dialog d = new Dialog("Importar Cuotas","Importar",true,"Cancelar",true,"importFee");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Cuota")){
						ti = t;
					}
				}
				
				CheckBox cb = (CheckBox) flex_table.getWidget(2, 0);
				Boolean ignoreInactiveClient = cb.getValue();
				
				hide();
				tiAux = ti;
				ignoreInactiveClientAux = ignoreInactiveClient;
				item.excelRowNumber(new AsyncCallback<Integer>() {
					TemplateInfo ti = tiAux;
					Boolean ignoreInactiveClient = ignoreInactiveClientAux;
					@Override
					public void onSuccess(Integer result) {
						hide();
						pbd = new ProgressBarDialog(result.doubleValue(), 0.86) {
					
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						item.executeExcel(getDomain(), getUser(), ti, ImportType.FEE, ignoreInactiveClient,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							@Override
							public void onSuccess(Integer result) {
								item.insertFee(getDomain(), getUser(), new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Cuotas","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}
										
											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {}
								});
							}	
					
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
			
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportFee(){
		//TODO
	}

	private void importDelivery(){
		Dialog d = new Dialog("Importar Albaranes de Venta","Importar",true,"Cancelar",true, IMPORT_ONLY);
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.DELIVERY, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Albaranes de Venta","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertDelivery(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void importProjectCommercial(){
		
		Dialog d = new Dialog("Importar Operación Commercial","Importar",true,"Cancelar",true, IMPORT_ONLY);
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.PROJECT_COMMERCIAL, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Operaciones Comerciales","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertProjectCommercial(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void importCustomerIban(){
		
		Dialog d = new Dialog("Importar Cuenta Bancaria","Importar",true,"Cancelar",true, IMPORT_ONLY);
		d.setUrl(GWT.getModuleBaseURL());
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				item.excelRowNumber(new AsyncCallback<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), null, ImportType.CUSTOMER_IBAN, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Cuenta Bancaria","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};
								item.insertCustomerIban(getDomain(), getUser(), callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void importProduct(){
		Dialog d = new Dialog("Importar Productos","Importar",true,"Cancelar",true,"importProduct");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Producto")){
						ti = t;
					}
				}
				
				ListBox lb2 = (ListBox) flex_table.getWidget(2, 1);
				final String value = lb2.getSelectedValue();
				
				tiAux = ti;
				item.excelRowNumber(new AsyncCallback<Integer>() {
					TemplateInfo ti = tiAux;
					@Override
					public void onSuccess(Integer result) {
						hide();
						Double doubleValue = result.doubleValue();
						pbd = new ProgressBarDialog(doubleValue , 0.46) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						item.executeExcel(getDomain(), getUser(), ti, ImportType.PRODUCT, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								AsyncCallback<Error> callback = new AsyncCallback<Error>() {
									
									
									@Override
									public void onSuccess(Error result) {
							
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Productos","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();			
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
										
									@Override
									public void onFailure(Throwable caught) {
										//TODO 
										pbd.completed();
										pbd.hide();
									}
								};

								item.insertProduct(getDomain(), getUser(), value, callback);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}

	private void exportProducts(ExportInfo ei){
		Dialog d = new Dialog("Exportar Productos","Descargar",true,"Cancelar",true,"exportProduct");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Producto")){
						ti = t;
					}
				}
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_product/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName()
		            	+ "&domain_name=" + getDomain().getName())
		            	+ "&domain_id=" + getDomain().getId()
						+ "&description="+ei.getName()
						+ "&code="+ei.getCode()
						+ "&category="+ei.getCategory()
						+ "&tags="+ei.getTags()
						+ "&vat="+ei.getVat()
						+ "&retention="+ei.getRetention()
						+ "&purchaseAccount="+ei.getPurchaseAccount()
						+ "&salesAccount="+ei.getSalesAccount()
						+ "&serializable="+ei.getSerializable()
						+ "&inventoriable="+ei.getInventoriable()
						+ "&manufactured="+ei.getManufactured()
						+ "&composition="+ei.getComposition()
						+ "&statuses="+ei.getStatuses()
						+ "&types="+ei.getTypes()
						+ "&brand="+ei.getBrand()
						
						+ "&barcode="+ei.getBarcode()
						+ "&serialNumber="+ei.getSerialNumber()
						+ "&itemSerialDate1="+ei.getItemSerialDate1()
						+ "&itemSerialDate2="+ei.getItemSerialDate2()
						+ "&detail="+ei.getDetail()
						+ "&detail2="+ei.getDetail2()
						+ "&detail3="+ei.getDetail3()
						+ "&itemDescription="+ei.getItemDescription()
						+ "&purchasePrice="+ei.getPurchasePrice()
						+ "&profitPercent="+ei.getProfitPercent()
						+ "&price="+ei.getPrice()
						+ "&itemStatuses="+ei.getItemStatuses()
						+ "&supplierCode="+ei.getSupplierCode()
						
						+ "&creationUser="+ei.getCreationUser()
						+ "&creationDate1="+ei.getCreationDate1()
						+ "&creationDate2="+ei.getCreationDate2()
						+ "&modificationUser="+ei.getModificationUser()
						+ "&modificationDate1="+ei.getModificationDate1()
						+ "&modificationDate2="+ei.getModificationDate1()
						+ "&username="+ getUser().getLogin();

				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}

	private void importStock(Integer inventory, String warehouse){
		inventoryId =  inventory;
		Dialog d = new Dialog("Importar Stock","Importar",true,"Cancelar",true,"importStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		d.setWarehouseName(warehouse);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				String  warehouse = "";
				ListBox  lb2 = (ListBox) flex_table.getWidget(1, 1);
				warehouse = lb2.getItemText(lb2.getSelectedIndex());				

				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				tiAux = ti;warehouseAux = warehouse;
				item.excelRowNumber(new AsyncCallback<Integer>() {
					TemplateInfo ti = tiAux;
					String warehouse = warehouseAux;
					@Override
					public void onSuccess(Integer result) {
						hide();
						pbd = new ProgressBarDialog(result.doubleValue(),0.4) {
							
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();						
						item.executeExcel(getDomain(), getUser(), ti, ImportType.STOCK, null,
								inventoryId, warehouse,null, "" , "",false,-1,new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								item.insertStock(getDomain(), getUser(), new AsyncCallback<Error>() {
									@Override
									public void onSuccess(Error result) {
										pbd.completed();
										pbd.hide();
										Dialog d2 = new Dialog("Importar Stock","Aceptar",true,"Cancelar",false,"importResponse");
										d2.setError(result);
										TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

											@Override
											protected void onAccept() {
												hide();
												refreshInventoryDetail();
											}

											@Override
											protected void onCancel() {
												hide();
											}
										};
										popup2.addStyleName("gwt-PopupPanel-template");
										popup2.setGlassEnabled(true);
										popup2.show();
									}
									
									@Override
									public void onFailure(Throwable caught) {print(caught);}
								});
							}

							@Override
							public void onFailure(Throwable caught) {print(caught);}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {print(caught);}
				});	
			}
		};

		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();

	}
	
	private void importTransferStock( LinkedList<Warehouse> w, LinkedList<Series> series, Integer number){
		num = number;
		Dialog d = new Dialog("Traspaso entre almacenes","Importar",true,"Cancelar",true,"importTransferStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		d.setWarehouses(w);
		d.setSeries2(series);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			Integer number = num;
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				String  warehouse2 = "";
				ListBox  lb2 = (ListBox) flex_table.getWidget(1, 1);
				warehouse2 = lb2.getItemText(lb2.getSelectedIndex());
				
				String  warehouse = "";
				ListBox  lb4 = (ListBox) flex_table.getWidget(2, 1);
				warehouse = lb4.getItemText(lb4.getSelectedIndex());				
				
				String series = "";
				ListBox lb3 = (ListBox) flex_table.getWidget(3, 1);
				series = lb3.getItemText(lb3.getSelectedIndex());
				String comments = "";
				TextBox tb = (TextBox) flex_table.getWidget(4, 1);
				comments = tb.getText();
		
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				tiAux = ti;warehouseAux = warehouse;warehouse2Aux = warehouse2;seriesAux= series;
				commentsAux = comments;
				item.excelRowNumber(new AsyncCallback<Integer>() {
					TemplateInfo ti= tiAux;String warehouse = warehouseAux;String warehouse2 = warehouse2Aux;
					String series = seriesAux;String comments = commentsAux;
					@Override
					public void onSuccess(Integer result) {
						hide();
						pbd = new ProgressBarDialog(result.doubleValue(),0.101) {

						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();	
						item.executeExcel(getDomain(), getUser(), ti, ImportType.STOCK, null,
								0, warehouse, warehouse2, series, comments,true,number, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								item.insertTransferStock(getDomain(), getUser(), new AsyncCallback<Error>() {
										@Override
										public void onSuccess(Error result) {
											pbd.completed();
											pbd.hide();
											Dialog d2 = new Dialog("Traspaso entre almacenes","Aceptar",true,"Cancelar",false,"importResponse");
											d2.setError(result);
											TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){
												
												@Override
												protected void onAccept() {
													hide();
													refreshTransferStock();
												}

												@Override
												protected void onCancel() {
													hide();
												}	
											};
											popup2.addStyleName("gwt-PopupPanel-template");
											popup2.setGlassEnabled(true);
											popup2.show();
										}
									
										@Override
										public void onFailure(Throwable caught) {

										}	
								});
							}
								
							
							
							@Override
							public void onFailure(Throwable caught) {

							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};

		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();

	}

	private void exportStocks(LinkedList<Warehouse> w,ExportInfo ei, Boolean closeInventory){
		eiAux = ei;
		closeInventoryAux = closeInventory;
		Dialog d = new Dialog("Exportar Stock","Descargar",true,"Cancelar",true,"exportStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		d.setWarehouses(w);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			ExportInfo ei = eiAux;
			Boolean closeInventory = closeInventoryAux;
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {

				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
				String warehouse = lb2.getItemText(lb2.getSelectedIndex());

				CheckBox cb = (CheckBox) flex_table.getWidget(2, 0);
				int onlyNonCero = cb.getValue() ? 1 : 0;
				
				CheckBox cb2 = (CheckBox) flex_table.getWidget(3, 0);
				int addPackagedInfo = cb2.getValue() ? 1 : 0;
				
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_stock/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName())
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&domain_id=" + getDomain().getId()
		            	+ "&warehouse=" + warehouse
		            	+ "&category="+ei.getCategory()
		            	+ "&brand="+ei.getBrand()
		            	+ "&code="+ei.getCode()
		            	+ "&description="+ei.getDescription()
		            	+ "&stock="+ei.getStock()
						+ "&barcode="+ei.getBarcode()
						+ "&provider="+ei.getProvider()
						+ "&tags="+ei.getTags()
						+ "&statuses="+ei.getStatuses()
						+ "&types="+ei.getTypes()
						+ "&quantity="+ei.getQuantity()
						+ "&close="+closeInventory
						+ "&inventory="+ ei.getInventory()
						+ "&only_non_cero="+ onlyNonCero
						+ "&username="+ getUser().getLogin()
						+ "&packaged_info=" + addPackagedInfo;
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportTransferStocks(LinkedList<Warehouse> w){
		Dialog d = new Dialog("Exportar Stock","Descargar",true,"Cancelar",true,"exportStock");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		d.setWarehouses(w);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {

				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
				String warehouse = lb2.getItemText(lb2.getSelectedIndex());
				
				String driveId="";
				if(ti.getDriveId()!=null)driveId= ti.getDriveId();
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_stock/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&drive_id=" +URL.encode(driveId)
		            	+ "&name=" +URL.encode(ti.getName())
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&domain_id=" + getDomain().getId()
		            	+ "&warehouse=" + warehouse
		            	+ "&username="+ getUser().getLogin();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportCatalogue(){
		Dialog d = new Dialog("Exportar Catalogo","Descargar",true,"Cancelar",true,"exportCatalogue");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				ListBox lb0 = (ListBox) flex_table.getWidget(0,1);
				String template = lb0.getSelectedItemText();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				ListBox lb1 = (ListBox) flex_table.getWidget(1, 1);
				String workplace = lb1.getSelectedItemText();
				if(workplace.contains("&")){
					Integer i = workplace.indexOf("&");
					workplace = workplace.substring(0,i) + "*"+ workplace.substring(i+1);
				}
				ListBox lb2;
				String department = "-";
				if(workplace != "-"){
					lb2 = (ListBox) flex_table.getWidget(2, 1);
					department = lb2.getSelectedItemText();
				}
				
				
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_catalogue"
		            	+ "?domain_id=" + getDomain().getId()
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&workplace=" + workplace
		            	+ "&department="+ department
		            	+ "&template_id="+ti.getId()
		            	+ "&username="+ getUser().getLogin();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void importProposal(Integer proposal, Integer workplace) {
		proposalId = proposal;
		workplaceId = workplace;
		Dialog d = new Dialog("Importar Solicitudes de Compra","Importar",true,"Cancelar",true,"importProposal");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
			
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				tiAux = ti;
				item.excelRowNumber(new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
						hide();
						pbd = new ProgressBarDialog(result.doubleValue(), 0.86) {
							
						};
							
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();	
						item.executeExcel(getDomain(), getUser(), tiAux, ImportType.PROPOSAL, null,
								null, null, null, null, null, null, null, new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								item.insertProposal(getDomain(), getUser(), proposalId,workplaceId,new AsyncCallback<Error>() {
											@Override
											public void onSuccess(Error result) {
												pbd.completed();
												pbd.hide();
												Dialog d2 = new Dialog("Importar Solicitudes de Compra","Aceptar",true,"Cancelar",false,"importResponse");
												d2.setError(result);
												TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

													@Override
													protected void onAccept() {
														hide();	
														refreshProposalDetail();
													}

													@Override
													protected void onCancel() {
														hide();
													}
												};
												popup2.addStyleName("gwt-PopupPanel-template");
												popup2.setGlassEnabled(true);
												popup2.show();
											}
											
											@Override
											public void onFailure(Throwable caught) {

											}
										});
									}		

							@Override
							public void onFailure(Throwable caught) {

							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportProposal(Integer proposal){
		proposalId = proposal;
		Dialog d = new Dialog("Exportar Compra","Descargar",true,"Cancelar",true,"exportProposal");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_proposal/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&domain_id=" + getDomain().getId()
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&proposal=" + proposalId
		            	+ "&username="+ getUser().getLogin();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void exportIncome(String income){
		incomeId = income;
		Dialog d = new Dialog("Exportar Albarán","Descargar",true,"Cancelar",true,"exportIncome");
		d.setUrl(GWT.getModuleBaseURL());
		d.setTemplateList(templateList);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				
				ListBox lb = (ListBox) flex_table.getWidget(0, 1);
				String template = lb.getItemText(lb.getSelectedIndex());
				TemplateInfo ti = new TemplateInfo();
				
				for(TemplateInfo t : tlist) {
					if(t.getName().equals(template) && t.getType().equals("Stock")){
						ti = t;
					}
				}
				
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_income/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&domain_id=" + getDomain().getId()
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&income=" + incomeId
		            	+ "&username="+ getUser().getLogin();
				
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};	
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}

	private void exportInventory(String closed, String inventoryId){
		closedAux = closed;
		inventoryIdAux = inventoryId;
		Integer size = 0;
		TemplateInfo templateInfo = null;
		for (TemplateInfo ti : templateList) {
			if(closed.equals("true") && ti.getType().equals("Inventario Cerrado")){
				templateInfo = ti;
				size++;
			}
			if(closed.equals("false") && ti.getType().equals("Inventario Valorado")){
				templateInfo = ti;
				size++;
			}
		}
		if(size != 1){
			String title;
			if(closed.equals("true")) title = "Listado de Recuento";
			else title = "Listado Valorado";
			Dialog d = new Dialog(title,"Descargar",true,"Cancelar",true,"exportInventory");
			d.setUrl(GWT.getModuleBaseURL());
			d.setTemplateList(templateList);
			d.setClosed(closed.equals("true"));
			TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
				String closed = closedAux;
				String inventoryId = inventoryIdAux;
				@Override
				protected void onCancel() {
					hide();
				}
			
				@Override
				protected void onAccept() {
					ListBox lb = (ListBox) flex_table.getWidget(0, 1);
					String template = lb.getItemText(lb.getSelectedIndex());
					TemplateInfo ti = new TemplateInfo();
					for (TemplateInfo t : tlist) {
						if(closed.equals("true") && t.getName().equals(template) && t.getType().equals("Inventario Cerrado")){
							ti = t;
						}
						if(closed.equals("false") && t.getName().equals(template) && t.getType().equals("Inventario Valorado")){
							ti = t;
						}
					}
					String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_inventory/"
		            	+ "?id=" + Integer.toString(ti.getId())
		            	+ "&domain_id=" + getDomain().getId()
		            	+ "&domain_name=" + getDomain().getName()
		            	+ "&closed="+closed
		            	+ "&inventory="+inventoryId
		            	+ "&username="+ getUser().getLogin();
				
				
					Window.open( fileDownloadURL, "_blank",null);
					hide();
				}
			};	
			popup.addStyleName("gwt-PopupPanel-template");
			popup.setGlassEnabled(true);
			popup.show();
		}
		else{
			String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_inventory/"
	            	+ "?id=" + Integer.toString(templateInfo.getId())
	            	+ "&domain_id=" + getDomain().getId()
	            	+ "&domain_name=" + getDomain().getName()
	            	+ "&closed="+closed
	            	+ "&inventory="+inventoryId
	            	+ "&username="+ getUser().getLogin();
			
			Window.open( fileDownloadURL, "_blank",null);
		}
	}
	
	private void exportEcommerce(){
		item.getTypeList(getDomain(), getUser(), new AsyncCallback<LinkedList<String>>() {
			
			@Override
			public void onSuccess(LinkedList<String> result) {
				Dialog d = new Dialog("Exportar Productos Ecommerce","Exportar",true,"Cancelar",true,"exportEcommerce");
				d.setUrl(GWT.getModuleBaseURL());
				d.setTypeList(result);
				TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						ListBox listBox = (ListBox) flex_table.getWidget(0, 1);
						
						String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_amazon_product/"
				            	+ "?domain_id=" + getDomain().getId()
				            	+ "&domain_name=" + getDomain().getName()
				            	+ "&username="+ getUser().getLogin()
				            	+ "&description=" + listBox.getSelectedItemText();
						Window.open( fileDownloadURL, "_blank",null);
					}
				};
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();			
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void importEcommerce(final LinkedList<Seller> sellerList) {
		mpimpl.getMarketplaceTagList(getDomain(), getUser(), new AsyncCallback<LinkedList<Tag>>() {
			
			@Override
			public void onSuccess(LinkedList<Tag> result) {
				Dialog d = new Dialog("Importar Plantilla Ecommerce","Importar",true,"Cancelar",true,"importEcommerceTemplate")
						.setUrl(GWT.getModuleBaseURL())
						.setTagList(result)
						.setSellerList(sellerList);
				TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						pbd = new ProgressBarDialog(1.0, 1.0) {
						};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.show();
						
						ListBox ecommerceListBox = (ListBox) flex_table.getWidget(0, 1);
						Integer ordinal = Integer.parseInt(ecommerceListBox.getSelectedValue());
						Ecommerce ecommerce = Ecommerce.values()[ordinal];
						
						ListBox sellerListBox = (ListBox) flex_table.getWidget(1, 1);
						Seller seller = new Seller();
						seller.setRegistryName(sellerListBox.getSelectedItemText());
						seller.setId(Integer.parseInt(sellerListBox.getSelectedValue()));
						
						TextBox typeTextBox = (TextBox) flex_table.getWidget(2, 1);
						String type = typeTextBox.getText();
						
						ListBox tagListBox = (ListBox) flex_table.getWidget(3, 1);
						Tag tag = new Tag()
								.setName(tagListBox.getSelectedItemText())
								.setId(Integer.parseInt(tagListBox.getSelectedValue()));
						
						item.executeExcelEcommerce(getDomain(), getUser(), ecommerce, seller, type, tag, new AsyncCallback<Error>() {
							@Override
							public void onSuccess(Error result) {
								pbd.hide();
								Dialog d2 = new Dialog("Importar Plantilla Ecommerce","Aceptar",true,"Cancelar",false,"importResponse");
								d2.setError(result);
								TemplatesDialog popup2 = new TemplatesDialog(getAonData(), d2){

									@Override
									protected void onAccept() {
										hide();			
									}
												
									@Override
									protected void onCancel() {
										hide();
									}
								};
								popup2.addStyleName("gwt-PopupPanel-template");
								popup2.setGlassEnabled(true);
								popup2.show();
							}
											
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				};		
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	//------------------------------ JavaScript Functions
	
	public void product(){
		importProduct();
	}

	public static native void exportProduct(Templates thiz) /*-{
    	$wnd.product = function() {
    		thiz.@com.esferalia.aon.gwt.template.client.Templates::product(*)();
    	}
	}-*/;
	
	public void productx(String code,String name,String category,String tags,String brand,String vat,String retention,String purchaseAccount,String  salesAccount,String  serializable, String inventoriable, String manufactured, String composition,  String statuses, String types
			,String barcode,String serialNumber,String itemSerialDate1, String itemSerialDate2,String detail,String detail2,String detail3,String description,String purchasePrice,String profitPercent,String price,String internet,String itemStatusesStr,String supplierCode
			, String creationUser,String creationDate1, String creationDate2,String modificationUser,String modificationDate1, String modificationDate2){
		String d1 = ""; 
		if(itemSerialDate1 != null && !itemSerialDate1.equals("")) d1 = itemSerialDate1.substring(8,10)+"-"+Utils.getMonth(itemSerialDate1.substring(4,7))+"-"+itemSerialDate1.substring(25);
		String d2 = "";
		if(itemSerialDate2 != null && !itemSerialDate2.equals("")) d2 = itemSerialDate2.substring(8,10)+"-"+Utils.getMonth(itemSerialDate2.substring(4,7))+"-"+itemSerialDate2.substring(25);
		String d3 = ""; 
		if(creationDate1 != null && !creationDate1.equals("")) d3 = creationDate1.substring(8,10)+"-"+Utils.getMonth(creationDate1.substring(4,7))+"-"+creationDate1.substring(25);
		String d4 = "";
		if(creationDate2 != null && !creationDate2.equals("")) d4 = creationDate2.substring(8,10)+"-"+Utils.getMonth(creationDate2.substring(4,7))+"-"+creationDate2.substring(25);
		String d5 = ""; 
		if(modificationDate1 != null && !modificationDate1.equals("")) d5 = modificationDate1.substring(8,10)+"-"+Utils.getMonth(modificationDate1.substring(4,7))+"-"+modificationDate1.substring(25);
		String d6 = "";
		if(modificationDate2 != null && !modificationDate2.equals("")) d6 = modificationDate2.substring(8,10)+"-"+Utils.getMonth(modificationDate2.substring(4,7))+"-"+modificationDate2.substring(25);
		
		ExportInfo ei = new ExportInfo();
		ei.setCode(code);ei.setName(name);ei.setCategory(category);ei.setTags(tags);ei.setBrand(brand);ei.setVat(vat);ei.setRetention(retention);ei.setPurchaseAccount(purchaseAccount);ei.setSalesAccount(salesAccount);ei.setSerializable(serializable);ei.setInventoriable(inventoriable);ei.setManufactured(manufactured);ei.setComposition(composition);ei.setStatuses(statuses);ei.setTypes(types);
		ei.setBarcode(barcode);ei.setSerialNumber(serialNumber);ei.setItemSerialDate1(d1);ei.setItemSerialDate2(d2);ei.setDetail(detail);ei.setDetail2(detail2);ei.setDetail3(detail3);ei.setPurchasePrice(purchasePrice);ei.setProfitPercent(profitPercent);ei.setPrice(price);ei.setInternet(internet);ei.setItemStatuses(itemStatusesStr);ei.setSupplierCode(supplierCode);
		ei.setCreationUser(creationUser);ei.setCreationDate1(d3); ei.setCreationDate2(d4);  ei.setModificationUser(modificationUser) ; ei.setModificationDate1(d5);ei.setModificationDate2(d6);
		
		exportProducts(ei);
	}

	public static native void exportProductx(Templates thiz) /*-{
		$wnd.productx = function(code,name,category,tags,brand,vat,retention,purchaseAccount, salesAccount, serializable, inventoriable,manufactured, composition, statuses, types
								,barcode,serialNumber,itemSerialDate1,itemSerialDate2,detail,detail2,detail3,description,purchasePrice,profitPercent,price,internet,itemStatusesStr,supplierCode
								,creationUser,creationDate1, creationDate2, modificationUser, modificationDate1,modificationDate2) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::productx(*)(code,name,category,tags,brand,vat,retention,purchaseAccount, salesAccount, serializable, inventoriable,manufactured, composition, statuses, types
																			,barcode,serialNumber,itemSerialDate1,itemSerialDate2,detail,detail2,detail3,description,purchasePrice,profitPercent,price,internet,itemStatusesStr,supplierCode
																			,creationUser,creationDate1, creationDate2, modificationUser, modificationDate1,modificationDate2);
		}
	}-*/;
	
	public void stock(String warehouse, String inventoryId){
		importStock(Integer.parseInt(inventoryId), warehouse);
	}
	
	public static native void refreshInventoryDetail() /*-{
		$wnd.refreshInventoryDetail();
	}-*/;
	
	public static native void refreshDelivery() /*-{
		$wnd.refreshDelivery();
	}-*/;
	
	public static native void refreshIncome() /*-{
	$wnd.refreshIncome();
	}-*/;
	
	public static native void refreshProposalDetail() /*-{	
		$wnd.refreshProposalDetail();
	}-*/;
	
	public static native void refreshTransferStock() /*-{
		$wnd.refreshWarehouseTransfer();
	}-*/;
	
	public static native void exportStock(Templates thiz) /*-{
		$wnd.stock = function(warehouse, inventoryId) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stock(*)(warehouse,inventoryId);
		}
	}-*/;
	
	public void stockx(String warehouse, String quantity, String code, String name, String barcode, String provider,String brand, String category,String statuses, String types, String tags){
		ExportInfo ei = new ExportInfo();
		ei.setWarehouse(warehouse);ei.setQuantity(quantity);ei.setCode(code); ei.setName(name);ei.setBarcode(barcode);ei.setProvider(provider);ei.setBrand(brand);ei.setCategory(category);ei.setStatuses(statuses);ei.setTypes(types);ei.setTags(tags);
		ei.setDescription(name);
		eiAux=ei;
		wAux = warehouse;
		item.getWarehouses(getDomain(), getUser(), new AsyncCallback<LinkedList<Warehouse>>() {
			ExportInfo ei = eiAux; String warehouse =  wAux;
			@Override
			public void onSuccess(LinkedList<Warehouse> result) {
				LinkedList<Warehouse> v = new LinkedList<Warehouse>();
				if(!warehouse.equals("") && !warehouse.equals("null") && warehouse != null){
					for (Warehouse w : result) {
						if(w.getId() == Integer.parseInt(warehouse))
							v.add(w);
					}				
				}
				else v = result;
				exportStocks(v,ei, false);
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}
	
	public static native void exportStockx(Templates thiz) /*-{

		$wnd.stockx = function(warehouse, quantity, code,name,barcode, provider, brand, category,statuses, types, tags) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stockx(*)(warehouse, quantity, code,name,barcode, provider, brand, category,statuses, types, tags);
		}
	}-*/;
	
	public void stockx2(String warehouse,String category, String brand,String code,String description,String stock, String inventory){
		ExportInfo ei = new ExportInfo();
		ei.setCategory(category);
		ei.setBrand(brand);
		ei.setCode(code);
		ei.setDescription(description);
		ei.setStock(stock);
		ei.setInventory(inventory);
		
		LinkedList<Warehouse> v = new LinkedList<Warehouse>();
		Warehouse w = new Warehouse();
		w.setName(warehouse);
		v.add(w);
		exportStocks(v,ei, true);		
	}
	
	public static native void exportStockx2(Templates thiz) /*-{
		$wnd.stockx2 = function(warehouse, category, brand, code, description, stock, inventory) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::stockx2(*)(warehouse, category, brand, code, description, stock, inventory);
		}
	}-*/;
	
	public void fee(){
		importFee();
	}

	public static native void exportFee(Templates thiz) /*-{
		$wnd.fee = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::fee(*)();
		}
	}-*/;

	public void feex(){
		exportFee();
	}
	
	public static native void exportFeex(Templates thiz) /*-{
		$wnd.feex = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::feex(*)();
		}
	}-*/;
	
	public void transferStock(String sourceWarehouse,String targetWarehouse, String serie, String number){
		Warehouse w1 = new Warehouse();
		w1.setName(sourceWarehouse);
		Warehouse w2 = new Warehouse();
		w2.setName(targetWarehouse);
		LinkedList<Warehouse> warehouses = new LinkedList<Warehouse>();
		warehouses.add(0,w1);warehouses.add(1,w2);
		LinkedList<Series> series = new LinkedList<Series>();
		Series s = new Series();
		s.setName(serie);
		series.add(s);
		importTransferStock(warehouses,series, Integer.parseInt(number));		
	}

	public static native void exportTransferStock(Templates thiz) /*-{
		$wnd.transferStock = function(source,target,serie, number) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::transferStock(*)(source,target,serie,number);
		}
	}-*/;
	
	public void transferStockx(){
		item.getWarehouses(getDomain(), getUser(), new AsyncCallback<LinkedList<Warehouse>>() {
			
			@Override
			public void onSuccess(LinkedList<Warehouse> result) {
				exportTransferStocks(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}
	
	public static native void exportTransferStockx(Templates thiz) /*-{
		$wnd.transferStockx = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::transferStockx(*)();
		}
	}-*/;
	
	public void cataloguex(){
		exportCatalogue();
	}
	
	public static native void exportCataloguex(Templates thiz) /*-{
		$wnd.cataloguex = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::cataloguex(*)();
		}
	}-*/;
	
	public void proposal(String proposal, String workplace){
		importProposal(Integer.parseInt(proposal), Integer.parseInt(workplace));
	}

	public static native void exportProposal(Templates thiz) /*-{
		$wnd.proposal = function(proposal, workplace) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::proposal(*)(proposal, workplace);
		}
	}-*/;
	
	public void proposalx(String proposal){
		exportProposal(Integer.parseInt(proposal));
	}

	public static native void exportProposalx(Templates thiz) /*-{
		$wnd.proposalx = function(proposal) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::proposalx(*)(proposal);
		}
	}-*/;
	
	public void inventoryx(String closed, String inventoryId){
		exportInventory(closed, inventoryId);
	}
	
	public static native void exportInventoryx(Templates thiz) /*-{	
		$wnd.inventoryx = function(closed, inventoryId) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::inventoryx(*)(closed, inventoryId);
		}
	}-*/;
	
	public void income(){
		//importIncome();
	}

	public static native void exportIncome(Templates thiz) /*-{
		$wnd.proposal = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::income(*)();
		}
	}-*/;
	
	public void incomex(String income){
		exportIncome(income);
	}

	public static native void exportIncomex(Templates thiz) /*-{
		$wnd.incomex = function(income) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::incomex(*)(income);
		}
	}-*/;
	
	public void ecommerce(){
		item.getSellerList(getDomain(), getUser(), new AsyncCallback<LinkedList<Seller>>() {
			
			@Override
			public void onSuccess(LinkedList<Seller> result) {
				importEcommerce(result);				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	public static native void exportEcommerce(Templates thiz) /*-{
		$wnd.ecommerce = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::ecommerce(*)();
		}
	}-*/;
	
	public void ecommercex(){
		
		exportEcommerce();
	}
	
	public static native void exportEcommercex(Templates thiz) /*-{
		$wnd.ecommercex = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::ecommercex(*)();
		}
	}-*/;
	
	public void offerx(String statuses, String target, String seller, String supplier, String project,
			String type, String series, String fromNumber, String toNumber, String fromDate, String toDate,
			String workplace, String scope, String confidential, String signed){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "download_offer_excel/"
				+ "?domain=" + getDomain().getName()
				+ "&domain_id=" + getDomain().getId()
            	+ "&username="+ getUser().getLogin()
            	+ "&statuses=" + statuses
            	+ "&target=" + target
            	+ "&seller=" + seller
            	+ "&supplier=" + supplier
            	+ "&project=" + project
            	+ "&type=" + type
            	+ "&series=" + series
            	+ "&from_number=" + fromNumber
				+ "&to_number=" + toNumber
            	+ "&from_date=" + fromDate
				+ "&to_date=" + toDate
				+ "&workplace=" + workplace
				+ "&scope=" + scope
				+ "&confidential=" + confidential
				+ "&signed=" + signed;
		
		Window.open( fileDownloadURL, "_blank",null);
	}

	public static native void exportOfferx(Templates thiz) /*-{
		$wnd.offerx = function(statuses, target, seller, supplier, project, type, series, fromNumber, toNumber, fromDate, toDate, workplace, scope, confidential, signed) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::offerx(*)(statuses, target, seller, supplier, project, type, series, fromNumber, toNumber, fromDate, toDate, workplace, scope, confidential, signed);
		}
	}-*/;
	
	public void offerpdfx(String id){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "download_offer_pdf/"
				+ "?domain=" + getDomain().getName()
				+ "&domain_id=" + getDomain().getId()
            	+ "&username="+ getUser().getLogin()
            	+ "&id=" + id;
		
		Window.open( fileDownloadURL, "_blank",null);
	}

	public static native void exportOfferPdfx(Templates thiz) /*-{
		$wnd.offerpdfx = function(id) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::offerpdfx(*)(id);
		}
	}-*/;
	
	public void deliveryx(){		
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_amazon_delivery/"
            	+ "?domain_id=" + getDomain().getId()
            	+ "&domain_name=" + getDomain().getName()
            	+ "&username="+ getUser().getLogin();
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	public static native void exportDeliveryx(Templates thiz) /*-{
		$wnd.deliveryx = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::deliveryx(*)();
		}
	}-*/;
	
	public void delivery(){
		importDelivery();
	}

	public static native void exportDelivery(Templates thiz) /*-{
		$wnd.delivery = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::delivery(*)();
		}
	}-*/;
	
	public void projectCommercial(){
		importProjectCommercial();
	}

	public static native void exportProjectCommercial(Templates thiz) /*-{
		$wnd.projectCommercial = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::projectCommercial(*)();
		}
	}-*/;
	
	public void projectCommercialx(String name, String source, String seller, String target, String fromDate,
			String toDate, String comments, String status, String probability){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "download_projectCommercial_excel/"
				+ "?domain=" + getDomain().getName()
				+ "&domain_id=" + getDomain().getId()
            	+ "&username="+ getUser().getLogin()
            	+ "&name=" + name
            	+ "&target=" + target
            	+ "&seller=" + seller
            	+ "&source=" + source
            	+ "&comments=" + comments
            	+ "&status=" + status
            	+ "&probability=" + probability
            	+ "&from_date=" + fromDate
				+ "&to_date=" + toDate;
		
		Window.open( fileDownloadURL, "_blank",null);
	}

	public static native void exportProjectCommercialx(Templates thiz) /*-{
		$wnd.projectCommercialx = function(name, source, seller, target, fromDate, toDate, comments, status, probability) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::projectCommercialx(*)(name, source, seller, target, fromDate, toDate, comments, status, probability);
		}
	}-*/;
	
	public void pgcx(String code, String description, String alias, String active, String costCenter, String entryEnabled){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "download_pgc_excel/"
				+ "?domain=" + getDomain().getName()
				+ "&domain_id=" + getDomain().getId()
            	+ "&username="+ getUser().getLogin()
            	+ "&code=" + code
            	+ "&description=" + description
            	+ "&alias=" + alias
            	+ "&active=" + active
            	+ "&costCenter=" + costCenter
            	+ "&entryEnabled=" + entryEnabled;
		
		Window.open( fileDownloadURL, "_blank",null);
	}

	public static native void exportPGCx(Templates thiz) /*-{
		$wnd.pgcx = function(code, description, alias, active, costCenter, entryEnabled) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::pgcx(*)(code, description, alias, active, costCenter, entryEnabled);
		}
	}-*/;
	
	public void customerIban(){
		importCustomerIban();
	}

	public static native void exportCustomerIban(Templates thiz) /*-{
		$wnd.customerIban = function() {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::customerIban(*)();
		}
	}-*/;
	
	public void fullExpedient(String project, String name, String alias, String registry, String type, String active, String from, String to){
		JSONObject json = new JSONObject();
		if(project != null && !project.isEmpty()) json.put("id", new JSONNumber(Integer.parseInt(project)));
		if(name != null && !name.isEmpty())json.put("name", new JSONString(name));
		if(alias != null && !alias.isEmpty()) json.put("alias", new JSONString(alias));
		if(registry != null && !registry.isEmpty()) json.put("registry", new JSONNumber(Integer.parseInt(registry)));
		if(type != null && !type.isEmpty()) json.put("type", new JSONNumber(Integer.parseInt(type)));
		if(active != null && !active.isEmpty()) json.put("active", new JSONString(active));
		if(from != null && !from.isEmpty()) json.put("from", new JSONString(from));
		if(to != null && !to.isEmpty()) json.put("to", new JSONString(to));
		
		API.getExpedient().downloadFullExpedient(JsonUtils.stringify(json.getJavaScriptObject()));
	}
	
	public static native void exportFullExpedient(Templates thiz) /*-{
		$wnd.fullExpedient = function(project, name, alias, registry, type, active, from, to) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::fullExpedient(*)(project, name, alias, registry, type, active, from, to);
		}
	}-*/;
	
	public void resumeExpedient(String project, String name, String alias, String registry, String type, String active, String from, String to){
		JSONObject json = new JSONObject();
		if(project != null && !project.isEmpty()) json.put("id", new JSONNumber(Integer.parseInt(project)));
		if(name != null && !name.isEmpty())json.put("name", new JSONString(name));
		if(alias != null && !alias.isEmpty()) json.put("alias", new JSONString(alias));
		if(registry != null && !registry.isEmpty()) json.put("registry", new JSONNumber(Integer.parseInt(registry)));
		if(type != null && !type.isEmpty()) json.put("type", new JSONNumber(Integer.parseInt(type)));
		if(active != null && !active.isEmpty()) json.put("active", new JSONString(active));
		if(from != null && !from.isEmpty()) json.put("from", new JSONString(from));
		if(to != null && !to.isEmpty()) json.put("to", new JSONString(to));
		
		API.getExpedient().downloadResumeExpedient(JsonUtils.stringify(json.getJavaScriptObject()));		
	}
	
	public static native void exportResumeExpedient(Templates thiz) /*-{
		$wnd.resumeExpedient = function(project, name, alias, registry, type, active, from, to) {
			thiz.@com.esferalia.aon.gwt.template.client.Templates::resumeExpedient(*)(project, name, alias, registry, type, active, from, to);
		}
	}-*/;

	private void print(Throwable caught){
		item.print(caught.getMessage(), new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
}