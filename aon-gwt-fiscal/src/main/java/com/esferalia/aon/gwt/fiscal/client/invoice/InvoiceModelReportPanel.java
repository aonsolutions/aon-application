package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HFModuleOptions;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class InvoiceModelReportPanel extends ScrollPanel{
		
	private FlowPanel rootPanel = new FlowPanel();
	private AonDisplayGrid grid = new AonDisplayGrid();
	
	public InvoiceModelReportPanel(InvoiceModelReportModuleOptions options, InvoiceModelReportParams params) {
		setStyleName(AON.CSS.aonScrollArea());
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());

		rootPanel.add(grid);
		setWidget(rootPanel);
		
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);
		
		InvoiceModelReport.FISCAL_MODEL_SERVICE.getInvoicesModels(options.getOccam(), params, new AsyncCallback<LinkedList<InvoiceFiscalModels>>() {
			
			@Override
			public void onSuccess(LinkedList<InvoiceFiscalModels> result) {
				toast.hide();
				paintHeader();
				result
					.stream()
					.forEach(ifm -> {
						AonDisplayGridRow row = addRow(ifm.getInvoice());
						FlowPanel modelsPanel = new FlowPanel();
						modelsPanel.setStyleName(AON.CSS.aonNowrap());
						row.addCell( modelsPanel );
						if (ifm.getModels() != null) {
							ifm.getModels().stream()
							.forEach(model -> {
								InlineLabel modelLabel = new InlineLabel( model.getModelFullName());
								modelLabel.addStyleName(AON.CSS.aonClickableLabel());
								modelLabel.addStyleName(AON.CSS.aonMarginLeft());
								modelLabel.addStyleName(AON.CSS.aonBorder());
								modelLabel.getElement().getStyle().setMarginTop(1.0, Unit.PX);
								modelLabel.getElement().getStyle().setMarginBottom(1.0, Unit.PX);
								modelLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( model.getStatus() ));
								modelLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( model.getStatus() ));
								modelLabel.addStyleName(AON.CSS.aonPaddingLeft());
								modelLabel.addStyleName(AON.CSS.aonPaddingRight());
								modelLabel.addStyleName(AON.CSS.aonNowrap());
								modelLabel.addClickHandler( event ->
								model.getModel().visit(new InvoiceModelReportModelVisitor(options
										, model
										, new AonModuleCallback<IFiscalModel>() {
									private static final long serialVersionUID = 3967210007796642782L;
									@Override public void onChange(IFiscalModel changed) { /* Nothing */ }
									@Override public void onRemove(IFiscalModel removed) {/* Nothing */ }
									@Override public void onExit(IFiscalModel edited) {/* Nothing */ }	
									@Override public void onFailure(Throwable caught) {/* Nothing */ }
								})));
								modelsPanel.add(modelLabel);
							});
						}
					});
				// 
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				toast.hide();
				
			}
			
			private void paintHeader() {
				grid.addHeaderRow()
					.addCell(new Label(""),AON.CSS.aonWidth20())
					.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
					.addCell(new Label("Tran."),AON.CSS.aonWidth40())
					.addCell(new Label("N\u00BA.Doc"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
					.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
					.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
					.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
					.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
					.addCell(new Label("Fec. Imp."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
					.addCell(new Label("Fec. Crea."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
					.addCell(new Label("Modelos"),AON.CSS.aonWidthAuto())
				;
			}
			
			
			public AonDisplayGridRow addRow(Invoice inv) {
				AonDisplayGridRow row = grid.addRow();
				String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
				Label issueDateLabel = new Label(issueDate);
				String taxDate = ensure(inv.getTaxDate(), () -> AON.DATE_FORMAT.format(inv.getTaxDate()), AonStringUtils.EMPTY);
				Label taxDateLabel = new Label(taxDate);
				if (!AonStringUtils.equals(issueDate, taxDate)) {
					taxDateLabel.addStyleName(AON.CSS.aonBackgroundHighlightedOrange());
				}
				String creationDate = ensure(inv.getCreationDate(), () -> AON.DATE_FORMAT.format(inv.getCreationDate()), AonStringUtils.EMPTY);
				Label creationDateLabel = new Label(creationDate);
				AonTableButton viewInvoice = new AonTableButton(AON.MSG.documentViewer(),AON.CSS.aonIconSearch());
				viewInvoice.addClickHandler( event -> showInvoice(options, inv.getId()));
				
				return row
					.addCell(viewInvoice)
					.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
					.addCell(new Label(ensure(inv.getTransaction(),inv.getTransaction()::getTediName)))
					.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
					.addCell(issueDateLabel)
					.addCell(taxDateLabel)
					.addCell(creationDateLabel)
				;
			}

			private String ensure(Object nullable, Supplier<String>  supplier) {
				return ensure(nullable, supplier, "---");
			}
			private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
				return (nullable == null) 
					? defaultValue
					: supplier.get();
			}

		});
		
		
	}

	private void showInvoice(InvoiceModelReportModuleOptions options,Integer invoiceId) {
		InvoiceModelReport.FISCAL_MODEL_SERVICE.getInvoice(options.getOccam(), invoiceId,new AsyncCallback<Invoice>() {
			@Override
			public void onSuccess(Invoice inv) {
				AonCustomPopup dialog = new AonCustomPopup();
				dialog.setWidth((Window.getClientWidth() - 100) + "px");
				dialog.setHeight((Window.getClientHeight() - 100) + "px");
				dialog.setAnimationEnabled(true);
				dialog.setGlassEnabled(true);
				dialog.setModal(true);
				dialog.setCaption(AON.MSG.invoice());
				dialog.add(new AonInvoiceViewer(inv));
				dialog.center();
				dialog.show();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Nothing
			}
		});
	}
	
	private class InvoiceModelReportModelVisitor implements IFiscalModelTypeVisitor {
		
		private static final String ERROR = "Consulta no soportada";

		private InvoiceModelReportModuleOptions opt;
		private IFiscalModel model;
		private AonModuleCallback<IFiscalModel> callback;
		
		public InvoiceModelReportModelVisitor(InvoiceModelReportModuleOptions opt, IFiscalModel model, AonModuleCallback<IFiscalModel> callback) {
			this.opt= opt;
			this.model = model;
			this.callback = callback;
		}
		private AonCustomPopup getModelDialog() {
			return getModelDialog(null);
		}
		private AonCustomPopup getModelDialog(String caption) {
			AonCustomPopup modelDialog = new AonCustomPopup( false );
			modelDialog.setWidth((Window.getClientWidth() - 50) + "px");
			modelDialog.setHeight((Window.getClientHeight() - 50) + "px");
			modelDialog.setAnimationEnabled(true);
			modelDialog.setGlassEnabled(true);
			modelDialog.setModal(true);
			modelDialog.setCaption( AonStringUtils.abbreviate( caption , 60 ));
			return modelDialog;
		}

		@Override 
		public void visitM111() {
			AonCustomPopup modelDialog = getModelDialog();
			try {
				Model111 model111 = new Model111();
				Model111ModuleOptions options = new Model111ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod111>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod111 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod111 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod111 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model111.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}
		
		@Override 
		public void visitM115() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model115 model115 = new Model115();
				Model115ModuleOptions options = new Model115ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod115>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod115 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod115 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod115 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model115.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}
		
		@Override public void visitM123() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model123 model123 = new Model123();
				Model123ModuleOptions options = new Model123ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod123>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod123 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod123 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod123 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model123.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}
		
		@Override 
		public void visitM130() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model130 model130 = new Model130();
				Model130ModuleOptions options = new Model130ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod130>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod130 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod130 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod130 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model130.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);

			}
		}
		
		@Override 
		public void visitM131() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model131 model131 = new Model131();
				Model131ModuleOptions options = new Model131ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod131>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod131 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod131 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod131 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model131.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM202() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model202 model202 = new Model202();
				Model202ModuleOptions options = new Model202ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod202>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod202 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod202 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod202 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model202.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}
		
		@Override 
		public void visitM180()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model180 model180 = new Model180();
				Model180ModuleOptions options = new Model180ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod180>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod180 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod180 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod180 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model180.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM184()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model184 model184 = new Model184();
				Model184ModuleOptions options = new Model184ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod184>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod184 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod184 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod184 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model184.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM190()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model190 model190 = new Model190();
				Model190ModuleOptions options = new Model190ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod190>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod190 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod190 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod190 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model190.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}
		
		@Override 
		public void visitM193()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model193 model193 = new Model193();
				Model193ModuleOptions options = new Model193ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod193>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod193 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod193 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod193 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model193.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM303() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model303 model303 = new Model303();
				Model303ModuleOptions options = new Model303ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod303>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod303 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod303 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod303 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model303.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM347()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model347 model347 = new Model347();
				Model347ModuleOptions options = new Model347ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod347>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod347 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod347 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod347 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model347.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM349()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model349 model349 = new Model349();
				Model349ModuleOptions options = new Model349ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod349>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod349 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod349 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod349 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model349.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM390()  { 
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model390 model390 = new Model390();
				Model390ModuleOptions options = new Model390ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod390>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod390 removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod390 edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod390 changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model390.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override 
		public void visitM390HF() {
			AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
			try {
				Model390HF model390HF = new Model390HF();
				Model390HFModuleOptions options = new Model390HFModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(opt.getConfiguration().getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(opt.getConfiguration().getUser().getLogin());
				options.setConfiguration(opt.getConfiguration());
				options.setFiscalModelId( model.getId() );
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				options.setExternalCallback( new AonModuleCallback<Mod390HF>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onRemove(Mod390HF removed) {
						modelDialog.hide();
						callback.onRemove(removed);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod390HF edited) {
						modelDialog.hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod390HF changed) {
						modelDialog.hide();
						callback.onChange(changed);
					}
					
				} );
				model390HF.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		}

		@Override public void visitM200()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }

	}
	
}
