package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MarketingActionTargetPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingActionTargetPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private MarketingActionTargetParams params;
	private Seller seller;
	private Workgroup workgroup;
	
	private boolean isTablet = false;
	
	private static enum COLS {
		DES("Cliente Potencial"						,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, COM(AON.MSG.comments()					,"20rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AON.MSG.status()						,"6rem" 			,"")
		, BUT(AonStringUtils.EMPTY					,"4rem"  			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	public MarketingActionTargetPanel(MarketingActionTargetParams params, Seller seller,  Workgroup workgroup) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.seller = seller;
		this.workgroup = workgroup;
		this.isTablet = Window.getClientWidth() <= 980;

		container = new SimplePanel();
		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		
		paintHeader();
		container.setWidget(tab);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(marketingActionTargets -> {
			boolean something = false;
			
			for(MarketingActionTarget marketingActionTarget : marketingActionTargets) {
				something = true;
				paintRow(marketingActionTarget);
			}
			
			if (marketingActionTargets.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + marketingActionTargets.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}

	private void paintRow(MarketingActionTarget marketingActionTarget) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		if(marketingActionTarget.hasProjectCommercial()) {
			AonTableButton deleteProjectCommercial;
			deleteProjectCommercial = new AonTableButton("Eliminar Operaci\u00f3n Comercial", AON.CSS.aonIconWorkOff());
			if(!isTablet) deleteProjectCommercial.addStyleName(AON.CSS.aonCustomRowButtom());
			deleteProjectCommercial.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					deleteProjectCommercial.setEnabled(false);
					AonDialog dialog = new AonDialog("Eliminaci\u00f3n Operaci\u00f3n Comercial",
							new HTML("Se va a proceder a eliminar la operaci\u00f3n comercial del cliente potencial <b>" + marketingActionTarget.getName() + "</b> de la acci\u00f3n <b>" + marketingActionTarget.getMarketingAction().getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
					
					dialog.confirm(new AonAcceptDialogCallback() {
	
						@Override
						public void onCancel() {
							deleteProjectCommercial.setEnabled(true);
						}
	
						@Override
						public void onAccept() {
							deleteProjectCommercial(marketingActionTarget);
						}
					});
				}
			});
			buttonContainer.add(deleteProjectCommercial);
		} else {
			AonTableButton createProjectCommercial;
			createProjectCommercial = new AonTableButton("Crear Operaci\u00f3n Comercial", AON.CSS.aonIconWorkAdd());
			createProjectCommercial.setEnabled(null != this.seller);
			if(null == this.seller) createProjectCommercial.setTitle("No se puede crear una operaci\u00f3n comercial si la acci\u00f3n no esta asignada a nadie");
			if(!isTablet) createProjectCommercial.addStyleName(AON.CSS.aonCustomRowButtom());
			createProjectCommercial.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					createProjectCommercial.setEnabled(false);
					AonDialog dialog = new AonDialog("Crear Operaci\u00f3n Comercial",
							new HTML("Se va a proceder a crear una operaci\u00f3n comercial para el cliente potencial <b>" + marketingActionTarget.getName() + "</b> de la acci\u00f3n <b>" + marketingActionTarget.getMarketingAction().getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la creaci\u00f3n\u003f."));
					
					dialog.confirm(new AonAcceptDialogCallback() {
	
						@Override
						public void onCancel() {
							createProjectCommercial.setEnabled(true);
						}
	
						@Override
						public void onAccept() {
							createProjectCommercial(marketingActionTarget, seller, workgroup);
						}
					});
				}
			});
			buttonContainer.add(createProjectCommercial);
		}
		
		AonTableButton button = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		if(!isTablet) button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cliente Potencial",
						new HTML("Se va a proceder a eliminar al cliente potencial <b>" + marketingActionTarget.getName() + "</b> de la acci\u00f3n <b>" + marketingActionTarget.getMarketingAction().getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(marketingActionTarget);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		
		Label name = new Label(marketingActionTarget.getName());
		name.setTitle(marketingActionTarget.getName());
		tab.addInlineStyle(name, COLS.DES.getCellStyleClass());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		HTMLPanel commentsPanel = new HTMLPanel("");
		commentsPanel.setStyleName(AON.CSS.aonItemFlex());
		commentsPanel.getElement().getStyle().setProperty("justify-content", "space-between");
		
		Label comments = new Label(marketingActionTarget.getComments());
		comments.setTitle(marketingActionTarget.getComments());
		commentsPanel.add(comments);
		
		if(AonStringUtils.isNotBlank(marketingActionTarget.getComments()) && marketingActionTarget.getComments().length() > 100) {
			AonTableButton showFullComment = new AonTableButton("Ver comentario completo", AON.CSS.aonIconComment());
			showFullComment.addClickHandler(e -> {
        		AonDialog dialog = new AonDialog(marketingActionTarget.getName() + " (Comentarios)", new Label(marketingActionTarget.getComments()));
    			dialog.info();
			});
			commentsPanel.add(showFullComment);
		}
		
		tab.addInlineStyle(comments, COLS.COM.getCellStyleClass());
		tab.addRow(row, commentsPanel, COLS.COM.getColWidth());
		
		tab.addRow(row, new Label(getActionStatus(marketingActionTarget.getActionTargetStatus())), COLS.STA.getColWidth());
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private String getActionStatus(Byte actionTargetStatus) {
		switch (actionTargetStatus) {
			case 0:
				return "Pendiente";
			case 1:
				return "Ausente";
			case 2:
				return "Incorrecto";
			case 3:
				return "Reintentar";
			case 4:
				return "Anular";
			case 5:
				return "Finalizado";
			case 6:
				return "Enviado";
			default:
				return "Desconocido";
		}
	}
	
	private void getList(Consumer<List<MarketingActionTarget>> success) {
		COMMON_SERVICE.getMarketingActionTargets(params, new AsyncCallback<List<MarketingActionTarget>>() {
			
			@Override
			public void onSuccess(List<MarketingActionTarget> marketingActionTargets) {
				success.accept(marketingActionTargets);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(MarketingActionTarget marketingActionTarget) {
		COMMON_SERVICE.deleteMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget.getActionTargetId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				resetSearchOffset();
				reloadMarketingAction();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}

	private void deleteProjectCommercial(MarketingActionTarget marketingActionTarget) {
		COMMON_SERVICE.deleteProjectCommercial(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget.getProject().getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				
				marketingActionTarget.setActionTargetStatus((byte)0); // Pendiente
				COMMON_SERVICE.saveMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget, new AsyncCallback<MarketingActionTarget>() {
					
					@Override
					public void onSuccess(MarketingActionTarget marketingActionTarget) {	
						resetSearchOffset();
						reloadMarketingAction();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error saveMarketingActionTarget(): " + caught.getMessage());
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void createProjectCommercial(MarketingActionTarget marketingActionTarget, Seller seller, Workgroup workgroup) {
		if(marketingActionTarget.getActionTargetStatus() == (byte)0) { // Pendiente
			
			if(seller == null) {
				onShowLoadingMessage("Creando operaciones comerciales para cada lead de la acción " + marketingActionTarget.getName() + " ...");
				
				COMMON_SERVICE.getNextLinealSellerByWorkgroup(params.getDomainName(), params.getDomain(), params.getUser(), workgroup.getId(), new AsyncCallback<Seller>() {
					
					@Override
					public void onSuccess(Seller seller) {
						ProjectCommercial projectCommercial = new ProjectCommercial()
								.copy(new Project()
									.setDomain(marketingActionTarget.getDomain())
									.setRegistry(marketingActionTarget.get())
									.setName(marketingActionTarget.getMarketingAction().getDescription())
									.setDate(new Date())
									.setTas(false)
									.setCommercial(true)
									.setReservation(false)
									.setActive(true)
								)
								.setTarget(marketingActionTarget.getId())
								.setSeller(seller.getId())
								.setComments(marketingActionTarget.getComments())
								.setSource((byte)3)
								.setStatus((byte)0)
								.setStatusDate(new Date())
								.setProbability(0);
						
						COMMON_SERVICE.saveProjectCommercial(params.getDomainName(), params.getDomain(), params.getUser(), projectCommercial, new AsyncCallback<ProjectCommercial>() {
							
							@Override
							public void onSuccess(ProjectCommercial projectCommercial) {
								marketingActionTarget.setActionTargetStatus((byte)6); // Enviado
								marketingActionTarget.setProject(new Project().setId(projectCommercial.getId()));
								COMMON_SERVICE.saveMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget, new AsyncCallback<MarketingActionTarget>() {
									
									@Override
									public void onSuccess(MarketingActionTarget marketingActionTarget) {
										onShowSuccessMessage("Operaci\u00f3n comercial creada correctamente");
										reloadMarketingAction();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										onShowErrorMessage("Error saveMarketingActionTarget(): " + caught.getMessage());
									}
								});
							}
				
							@Override
							public void onFailure(Throwable caught) {
								onShowErrorMessage("Error saveProjectCommercial(): " + caught.getMessage());
							}
							
						} );
					}
	
					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error getNextLinealSellerByWorkgroup(): " + caught.getMessage());
					}
					
				});	
			} else {
				onShowLoadingMessage("Creando operaciones comerciales para cada lead de la acción " + marketingActionTarget.getName() + " ...");
			
				ProjectCommercial projectCommercial = new ProjectCommercial()
						.copy(new Project()
							.setDomain(marketingActionTarget.getDomain())
							.setRegistry(marketingActionTarget.get())
							.setName(marketingActionTarget.getMarketingAction().getDescription())
							.setDate(new Date())
							.setTas(false)
							.setCommercial(true)
							.setReservation(false)
							.setActive(true)
						)
						.setTarget(marketingActionTarget.getId())
						.setSeller(seller.getId())
						.setComments(marketingActionTarget.getComments())
						.setSource((byte)3)
						.setStatus((byte)0)
						.setStatusDate(new Date())
						.setProbability(0);
				
				COMMON_SERVICE.saveProjectCommercial(params.getDomainName(), params.getDomain(), params.getUser(), projectCommercial, new AsyncCallback<ProjectCommercial>() {
					
					@Override
					public void onSuccess(ProjectCommercial projectCommercial) {
						marketingActionTarget.setActionTargetStatus((byte)6); // Enviado
						marketingActionTarget.setProject(new Project().setId(projectCommercial.getId()));
						COMMON_SERVICE.saveMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget, new AsyncCallback<MarketingActionTarget>() {
							
							@Override
							public void onSuccess(MarketingActionTarget marketingActionTarget) {
								onShowSuccessMessage("Operaci\u00f3n comercial creada correctamente");
								reloadMarketingAction();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								onShowErrorMessage("Error saveMarketingActionTarget(): " + caught.getMessage());
							}
						});
					}
		
					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error saveProjectCommercial(): " + caught.getMessage());
					}
					
				} );
				
			}
		}
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}
	
	protected abstract void reloadMarketingAction();
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onShowSuccessMessage(String successMessage);
	
}

