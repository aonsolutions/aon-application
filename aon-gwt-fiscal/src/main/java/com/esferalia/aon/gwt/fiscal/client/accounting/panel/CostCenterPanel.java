package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModuleOptions;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class CostCenterPanel extends ScrollPanel implements HasSelectionHandlers<ApplicationParameter> {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(CostCenterPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private AccountModuleOptions params;
	private List<ApplicationParameter> costCenters;
	
	private final MutableInt row = new MutableInt(0);
	private SimplePanel container;
	private FlexTable tab;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY		,"20px"  ,AON.CSS.aonTextCenter())
		, DES(AON.MSG.description()		,"auto"  ,null)
		, BUT(AonStringUtils.EMPTY		,"20px"  ,null)
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

	public CostCenterPanel(AccountModuleOptions params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.costCenters = new ArrayList<>();

		container = new SimplePanel();
		setWidget(container);
		
		this.params = params;
		onSearch();
		
	}
	
	private void onSearch() {
		search();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<ApplicationParameter> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	private void search() {
		container.clear();
		tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonGrid());
		
		paintHeader();
		container.setWidget(tab);
		row.setValue(1);
		
		getCostCenters(costCenters -> {
			this.costCenters = costCenters;
			if(costCenters.isEmpty()) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
			}
			else costCenters.forEach(costCenter -> paintRow( params.getDomain(), costCenter ));
		});
	}
	
	private void paintHeader() {
		for ( COLS col : COLS.values()) {
			tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth());	
			tab.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
	}
	
	private void paintRow(int domain, ApplicationParameter costCenter) {
		final int r = row.getValue();
		paintRow(domain, r, costCenter); 
		row.increment();
	}
	
	private void paintRow(int domain, final int r, ApplicationParameter costCenter) {
		int col = 0;
		boolean myAccount =  costCenter == null || AonNumberUtils.equals( costCenter.getDomain() , domain); 
		if (myAccount) {
			paintActiveRow(r,col,costCenter);
		} else {
			paintInactiveRow(r,col,costCenter);
		}
	}

	private void paintInactiveRow(final int r, int col, ApplicationParameter costCenter) {
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonIconLevelTop());
		tab.setWidget(r, col, msg);
		col++;
		
		Label descriptionLabel = new Label(costCenter.getValue());
		tab.setWidget(r, col, descriptionLabel);
		col++;
	}
	
	private void paintActiveRow(final int r, int col, ApplicationParameter costCenter) {
		Label msg = new Label("");
		TextBox descriptionBox = new TextBox();

		ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				costCenter.setValue(descriptionBox.getValue());
				saveCostCenter(costCenter, msg);
			}
		};
		
		descriptionBox.addValueChangeHandler(valueChangeHandler);
		
		msg.setStyleName(AON.CSS.aonTabIcon());
		tab.setWidget(r, col, msg);
		col++;
		
		descriptionBox.setStyleName(AON.CSS.aonBorderNone());
		descriptionBox.addStyleName(AON.CSS.aonWidthAll());
		descriptionBox.setMaxLength(128);
		descriptionBox.setValue(costCenter.getValue());
		tab.setWidget(r, col, descriptionBox);
		col++;
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteButton.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Centro Coste",
						new HTML("Se va a proceder a eliminar el centro de coste <b>" + costCenter.getValue() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}

					@Override
					public void onAccept() {
						deleteCostCenter(costCenter);
					}
				});
				
			}
		});
		
		buttonContainer.add(deleteButton);
		tab.setWidget(r, col, buttonContainer);
		col++;
	}
	
	private void getCostCenters(Consumer<List<ApplicationParameter>> success) {
		COMMON_SERVICE.getCostCenters(params.getDomainName(), params.getDomain(), params.getUser(), new AsyncCallback<List<ApplicationParameter>>() {
			
			@Override
			public void onSuccess(List<ApplicationParameter> customer) {
				success.accept(customer);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	private void deleteCostCenter(ApplicationParameter costCenter) {
		COMMON_SERVICE.deleteCostCenter(params.getDomainName(), params.getDomain(), params.getUser(), costCenter.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				costCenters.remove(costCenter);
				tab.removeRow(getRow(costCenter));
				if(tab.getRowCount() == 1) onSearch();
			}
			
			private Integer getRow(ApplicationParameter costCenter) {
				for(int row = 1; row < tab.getRowCount(); row++) {
					boolean myAccount =  costCenter == null || AonNumberUtils.equals( costCenter.getDomain() , params.getDomain()); 
					String description = "";
					
					if(myAccount) description = ((TextBox) tab.getWidget(row, 1)).getValue();
					else description = ((Label) tab.getWidget(row, 1)).getText();
					
					if(AonStringUtils.equalsIgnoreCase(description, costCenter.getValue())) return row;
				}
				return null;
			}

			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	private void saveCostCenter(ApplicationParameter costCenter, Label msg) {
		COMMON_SERVICE.saveCostCenter(params.getDomainName(), params.getDomain(), params.getUser(), costCenter, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				msg.addStyleName(AON.CSS.aonIconValid());
				new Timer() {
					@Override
					public void run() {
						msg.removeStyleName(AON.CSS.aonIconValid());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	public void createCostCenter(String description) {
		List<Integer> posNames = costCenters.stream().map(costCenter -> Integer.parseInt(costCenter.getName().split("ACC_COST_CENTER_")[1])).collect(Collectors.toList());
		int nextPosName = findFirstMissingNumber(posNames);
		ApplicationParameter costCenter = new ApplicationParameter()
				.setDomain(params.getDomain())
				.setName("ACC_COST_CENTER_" + nextPosName)
				.setValue(description);
		
		createCostCenter(costCenter);
	}
	
	private int findFirstMissingNumber(List<Integer> numbers) {
        Collections.sort(numbers);

        int missingNumber = 1;

        for (int number : numbers) {
            if (number == missingNumber) {
                missingNumber++;
            }
        }

        return missingNumber;
    }
	
	private void createCostCenter(ApplicationParameter costCenter) {
		COMMON_SERVICE.saveCostCenter(params.getDomainName(), params.getDomain(), params.getUser(), costCenter, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}
	
	private void error(String message) {
		AonDialog dialog = new AonDialog("Error", new HTML(message));
		dialog.info();
	}
	
}

