package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.ValidationMessage2015;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
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

	interface NavigateButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-go\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class NavigateButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static NavigateButtonTemplate template;

		protected NavigateButtonSafeHtmlTemplates() {
			template = GWT.create(NavigateButtonTemplate.class);
		}
		
		@Override
		public SafeHtml render(String object) {
			return template.render(object);
		}

		@Override
		public void render(String object, SafeHtmlBuilder builder) {
			builder.append( template.render(object) );
		}
		
	}

	interface ErrorPageBinder extends
			UiBinder<Widget, ErrorPage> {
	}
	
	private static final ErrorPageBinder errorPageBinder = GWT
			.create(ErrorPageBinder.class);

	private ListDataProvider<ValidationMessage2015> dataProvider;
	
	private Model2002015 handler;
	
	@UiField
	Panel basePanel;
	
	@UiField(provided = true)
	CellTable<ValidationMessage2015> table;
	
	
	public ErrorPage( ) {
		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);
		
		table = new CellTable<ValidationMessage2015>(1, tableStyle);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		addNavigateColumn();
		addPageColumn();
		addBoxColumn();
		addMessageColumn();
		
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider= new ListDataProvider<ValidationMessage2015>();
		dataProvider.addDataDisplay(table);
		
		Widget ui = errorPageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void addSelectionListener(Model2002015 handler) {
		// From Mod200 dump()
		this.handler = handler;
	}
	
	public void addErrorMsg(List<ValidationMessage2015> messages) {
		dataProvider.setList(messages);
		table.setPageSize(messages.size());
		table.redraw();
	}
	public void addErrorMsg(String msg) {
		addMessage(msg);
	}
	public void addErrorMsg(Throwable t) {
		String msg = (t.getCause() != null)?t.getCause().getMessage():t.getMessage();
		if (AonStringUtils.isEmpty(msg)) {
			msg = t.getMessage();
		}
		if (AonStringUtils.isEmpty(msg)) {
			msg = "Se ha producido un error inesperado. ";
					
		}
		addMessage(msg);
	}
	private void addMessage(String message) {
		addMessage(new ValidationMessage2015(-1,null,message,null));
	}
	
	private void addMessage(ValidationMessage2015 msg) {
		dataProvider.getList().add(msg);
		table.setPageSize( dataProvider.getList().size() );
		table.redraw();		    		
	}
	
	private void addPageColumn() {
		Column<ValidationMessage2015,String> col = new TextColumn<ValidationMessage2015>() {

			@Override
			public String getValue(ValidationMessage2015 errorMessage) {
				return errorMessage.getPage()>=0?Integer.toString(errorMessage.getPage()+1):"";
			}
			
		};
		table.addColumn(col,AON.MSG.page());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}
	
	private void addBoxColumn() {
		Column<ValidationMessage2015,String> col = new TextColumn<ValidationMessage2015>() {

			@Override
			public String getValue(ValidationMessage2015 errorMessage) {
				return errorMessage.getKey() != null? errorMessage.getKey().getCode( Administration.COMMON_TERRITORY) : ""; 
			}
			
		};
		table.addColumn(col,AON.MSG.box());
		table.setColumnWidth(col, 70, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}
	private void addMessageColumn() {
		Column<ValidationMessage2015,String> col = new TextColumn<ValidationMessage2015>() {

			@Override
			public String getValue(ValidationMessage2015 errorMessage) {
				return errorMessage.getMessage();
			}
			
		};
		table.addColumn(col,AON.MSG.message());
		col.setCellStyleNames(AON.AON_CSS.aonIconError());
	}

	private void addNavigateColumn() {
		ButtonCell navigateButton = new ButtonCell( new NavigateButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<ValidationMessage2015,String> col = new Column<ValidationMessage2015,String>(navigateButton) {
		  public String getValue(ValidationMessage2015 object) {
		    return AON.MSG.goAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<ValidationMessage2015, String>() {
		    public void update(int index, ValidationMessage2015 ca, String value) {
		    	if (handler != null) {
		    		handler.validationMessageSelected(ca);
		    	}
		    }
		});		
		table.addColumn(col);
		table.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}
	
	


}
