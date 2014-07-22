package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import java.util.List;

import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200.IValidationMessageSelectioinHandler;
import com.esferalia.aon.gwt.fiscal.shared.mod200.ValidationMessage;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ErrorPage extends ResizeComposite {

	interface ErrorPageBinder extends
			UiBinder<Widget, ErrorPage> {
	}
	
	private static final ErrorPageBinder errorPageBinder = GWT
			.create(ErrorPageBinder.class);

	private ListDataProvider<ValidationMessage> dataProvider;
	
	private IValidationMessageSelectioinHandler handler;
	
	@UiField
	Panel basePanel;
	
	@UiField(provided = true)
	CellTable<ValidationMessage> table;
	
	
	public ErrorPage( ) {
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);
		
		table = new CellTable<ValidationMessage>(1, tableStyle);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		addNavigateColumn();
		addPageColumn();
		addBoxColumn();
		addMessageColumn();
		
		table.setEmptyTableWidget(new HTML(MSG.noData()));
		dataProvider= new ListDataProvider<ValidationMessage>();
		dataProvider.addDataDisplay(table);
		
		Widget ui = errorPageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void addSelectionListener(IValidationMessageSelectioinHandler handler) {
		// From Mod200 dump()
		this.handler = handler;
	}
	
	public void addErrorMsg(List<ValidationMessage> messages) {
		dataProvider.setList(messages);
		table.setPageSize(messages.size());
		table.redraw();
	}
	public void addErrorMsg(String msg) {
		addMessage(msg);
	}
	public void addErrorMsg(Throwable t) {
		String msg = (t.getCause() != null)?t.getCause().getMessage():t.getMessage();
		if (AonUtil.isEmpty(msg)) {
			msg = t.getMessage();
		}
		if (AonUtil.isEmpty(msg)) {
			msg = "Se ha producido un error inesperado. ";
					
		}
		addMessage(msg);
	}
	private void addMessage(String message) {
		addMessage(new ValidationMessage(-1,null,message,null));
	}
	
	private void addMessage(ValidationMessage msg) {
		dataProvider.getList().add(msg);
		table.setPageSize( dataProvider.getList().size() );
		table.redraw();		    		
	}
	
	private void addPageColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getPage()>=0?Integer.toString(errorMessage.getPage()+1):"";
			}
			
		};
		table.addColumn(col,MSG.page());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}
	
	private void addBoxColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getKey() != null? errorMessage.getKey().getCode( Administration.COMMON_TERRITORY) : ""; 
			}
			
		};
		table.addColumn(col,MSG.box());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}
	private void addMessageColumn() {
		Column<ValidationMessage,String> col = new TextColumn<ValidationMessage>() {

			@Override
			public String getValue(ValidationMessage errorMessage) {
				return errorMessage.getMessage();
			}
			
		};
		table.addColumn(col,MSG.message());
		col.setCellStyleNames("aon-icon-error aon-padding-left");
	}

	private void addNavigateColumn() {
		ButtonCell navigateButton = new ButtonCell( new Model200.NavigateButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<ValidationMessage,String> col = new Column<ValidationMessage,String>(navigateButton) {
		  public String getValue(ValidationMessage object) {
		    return MSG.goAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<ValidationMessage, String>() {
		    public void update(int index, ValidationMessage ca, String value) {
		    	if (handler != null) {
		    		handler.validationMessageSelected(ca);
		    	}
		    }
		});		
		table.addColumn(col);
		table.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}
	
	


}
