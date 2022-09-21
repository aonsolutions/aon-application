package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Collection;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class ConsoleDomainTable extends SimpleLayoutPanel implements HasSelectionHandlers<Domain>{
	
	interface ConsoleDomainTableCallback {
		public void showError(String message);
		public void showInfo(String message);
		public void onDelete(Domain domain, AsyncCallback<Boolean> cbk);
		public void onChangeActive(Domain domain, AsyncCallback<Domain> cbk);
		public void onChangeExpirationDate(Domain domain, AsyncCallback<Domain> cbk);
		public void onValidate(Domain domain, AsyncCallback<Boolean> cbk);
	}
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private boolean running;
	
	protected ConsoleDomainTable(Collection<Domain> domains, ConsoleDomainTableCallback callback) {
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		tableDockLayout.add(centerLayoutPanel);
		setWidget(tableDockLayout);
		paint(domains, callback);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Domain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}


	private enum Columns {
		  CHK(""					, 20 ,AON.CSS.aonTextCenter())
		, TYP(AON.MSG.type()		, 75 ,AON.CSS.aonTextCenter())
	    , STA("Act."				, 20 ,AON.CSS.aonTextCenter())
	    , MNG("Crea"				, 20 ,AON.CSS.aonTextCenter())
	    , PAR("Padre"				, 50 ,AON.CSS.aonTextCenter())
	    , HER("Her"					, 20 ,AON.CSS.aonTextCenter())
	    , AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextCenter())
	    , DES(AON.MSG.description() , 150,AON.CSS.aonTextCenter())
		, ACC("\u00FAlt. Acceso"	, 75 ,AON.CSS.aonTextCenter())
		, EXP("Expira"				, 75 ,AON.CSS.aonTextCenter())
		, CMD(""					, 100,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private Columns(String headerLabel,int colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public int getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	private void paint(Collection<Domain> domains, ConsoleDomainTableCallback callback) {
		container.clear();
		if ( domains == null || domains.isEmpty()) {
			FlowPanel line = new FlowPanel();
			InlineLabel label = new InlineLabel(AON.MSG.noData());
			line.add(label);
			container.add(line);
		} else {
			AonDisplayGrid tab = new AonDisplayGrid();
			tab.addStyleName(AON.CSS.aonNoPadding());
			tab.addStyleName(AON.CSS.aonBlockCenter());
			tab.addStyleName(AON.CSS.aonWidthAlmostAll());
			container.add(tab);
			paintHeader( tab );
			domains
				.stream()
				.forEach(d -> paintRow( callback, d , tab.addRow()));
		}
	}

	private void paintHeader(AonDisplayGrid tab) {
		AonDisplayGridHeaderRow headerRow = tab.addHeaderRow();
		for ( Columns col : Columns.values()) {
			Label label = new Label( col.getHeaderLabel());
			AonDisplayGridCell headerCell = headerRow.addCell(col.getCellStyleClass());
			if (col == Columns.AUTO ) {
				headerCell.addStyleName(AON.CSS.aonFlexGrow1());
			} else {
				headerCell.setWidth(col.getColWidth()  + "px");
			}
			headerCell.add(label);
		}
	}

	private void paintRow(ConsoleDomainTableCallback callback, Domain domain, AonDisplayGridRow row) {
		CheckBox checkBox = new CheckBox();
		checkBox.addClickHandler(e -> SelectionEvent.fire(ConsoleDomainTable.this, domain));
		
		InlineLabel typeLabel = new InlineLabel( domain.getDomainType()==null?"":domain.getDomainType().getName() );
		InlineLabel parentIdLabel = new InlineLabel(AonNumberUtils.toString( domain.getParentId()));
		InlineLabel nameLabel = new InlineLabel(domain.getName());
		InlineLabel lastAccessLabel = new InlineLabel( domain.getLastAccessDate()==null?"":AON.TIME_FORMAT.format(domain.getLastAccessDate()));
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());		 

		AonTableButton active = new AonTableButton(
			domain.isActive()?"Activo":"Inactivo"
			,domain.isActive()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff()		
		);
		
		active.addClickHandler(e -> {
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Se va a proceder al cambio de estado del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						running = true;
						callback.onChangeActive( domain , new AsyncCallback<Domain>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo cambiar el estado del dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Domain result) {
								running = false;
								domain.setActive(result.isActive());
								deleteButton.setEnabled(!result.isActive());
								if (result.isActive()) {
									active.removeStyleName(AON.CSS.aonIconToggleOff());
									active.addStyleName(AON.CSS.aonIconToggleOn());
									active.setTitle("Activo");
								} else {
									active.removeStyleName(AON.CSS.aonIconToggleOn());
									active.addStyleName(AON.CSS.aonIconToggleOff());
									active.setTitle("Inactivo");
								}
							}
							
						});
					}
				}
			});
		});
		
		InlineLabel domManagement = new InlineLabel();
		domManagement.setTitle( "Puede crear dominios" );
		domManagement.setStyleName(AON.CSS.aonIconLabel());
		domManagement.addStyleName( domain.isDomainManagement()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );
		
		InlineLabel heredity = new InlineLabel();
		heredity.setTitle( "Herencia de datos" );
		heredity.setStyleName(AON.CSS.aonIconLabel());
		heredity.addStyleName( domain.isEnableHeredity()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );

		InlineLabel descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
		descriptionLabel.setTitle(domain.getDescription());
		
		AonDateBox expiredDateBox = new AonDateBox();
		expiredDateBox.setValue(domain.getExpirationDate());
		expiredDateBox.addValueChangeHandler(e ->{
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Se va a proceder al cambio de fecha de expiraci\u00F3n " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						running = true;
						domain.setExpirationDate( expiredDateBox.getValue() );
						callback.onChangeExpirationDate( domain , new AsyncCallback<Domain>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo cambiar la fecha de expiraci\u00F3n del dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Domain result) {
								running = false;
								domain.setExpirationDate(result.getExpirationDate());
								expiredDateBox.setValue(result.getExpirationDate());
								expiredDateBox.addStyleName(AON.CSS.aonValueChanged());
								new Timer() {
									@Override
									public void run() {
										expiredDateBox.removeStyleName(AON.CSS.aonValueChanged());
									}
								}.schedule(3000);
							}
						});
					}
				}
			});
		});

		AonDisplayTable buttons = new AonDisplayTable();
		deleteButton.setEnabled(!domain.isActive());
		deleteButton.addClickHandler(e -> {
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("\u00A1\u00A1ESTE PROCESO ES IRREVERSIBLE!!",
				"Se va a proceder al BORRADO del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();
						running = true;
						callback.onDelete( domain , new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								running = false;
								callback.showError( "No se pudo borrar el dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Boolean result) {
								popup.hide();
								running = false;
								if (result == null || !result ) {
									callback.showInfo( "No se pudo borrar el dominio. (Unknown cause)");
								} else {
									typeLabel.addStyleName(AON.CSS.aonTextLineThrough());
									parentIdLabel.addStyleName(AON.CSS.aonTextLineThrough());
									nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
									lastAccessLabel.addStyleName(AON.CSS.aonTextLineThrough());
									expiredDateBox.setEnabled(false);
									active.setEnabled(false);
									buttons.clear();
									Label deletedLabel = new Label("BORRADO");
									deletedLabel.setStyleName(AON.CSS.aonColorRed());
									deletedLabel.addStyleName(AON.CSS.aonBold());
									buttons.add(deletedLabel);
								}
							}
							
						});
					}
				}
			});
		});
		
		AonTableButton validateButton = new AonTableButton(AON.MSG.validateAction(), AON.CSS.aonIconValid());
		validateButton.addClickHandler(e -> {
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm("Proceder con la validaci\u00F3n de integridad referencial del dominio " + domain.getId() + " - " + domain.getName() + "("+ domain.getDescription() +")."					
				, new AonConfirmDialogCallback() {
				@Override 
				public void onCancel() {
					// Nothing
				}
				
				@Override
				public void onAccept() {
					if (!running) {
						callback.onValidate(domain , new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								running = false;
								callback.showError( "No se pudo validar el dominio. ("+ caught.getMessage() +")");
							}
			
							@Override
							public void onSuccess(Boolean result) {
								running = false;
							}
						});
					} else {
						AonMessageDialog.show("AVISO","Hay una validaci\u00F3n ejecut\u00E1ndose. Un momento, por favor.");
					}
				}
			});
		});
		buttons.addRow()
			.addCell(deleteButton)
			.addCell(validateButton);
		
		row.addStyleName(AON.CSS.aonClickable());
		row .addCell( checkBox , AON.CSS.aonTextCenter())
			.addCell( typeLabel , AON.CSS.aonTextCenter())
			.addCell( active , AON.CSS.aonTextCenter())
			.addCell( domManagement , AON.CSS.aonTextCenter())
			.addCell( parentIdLabel, AON.CSS.aonTextCenter())
			.addCell( heredity , AON.CSS.aonTextCenter())
			.addCell( nameLabel )
			.addCell( descriptionLabel )
			.addCell( lastAccessLabel )
			.addCell( expiredDateBox )
			.addCell( buttons )
			;
	}
}
