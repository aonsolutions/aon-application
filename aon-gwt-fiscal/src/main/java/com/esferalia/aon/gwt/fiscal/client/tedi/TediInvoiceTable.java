package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

public class TediInvoiceTable extends CellTable<TediResult> implements HasSelectionHandlers<TediResult> {
	private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);

	interface EditButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-row-selector\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class EditButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static EditButtonTemplate template;

		protected EditButtonSafeHtmlTemplates() {
			template = GWT.create(EditButtonTemplate.class);
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

	private MultiSelectionModel<TediResult> model;
	
	public TediInvoiceTable(ProvidesKey<TediResult> providesKey) {
		super(1, TABLE_STYLE, providesKey);
		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addEditColumn();
		addSelectedColumn();
		addStatusColumn();
		addInvoiceTypeColumn();
		addAttachColumn();
		addIssueDateColumn();
		addNumberColumn();
		addDocumentColumn();
		addNameColumn();
		addTotalColumn();
		model = new MultiSelectionModel<TediResult>(providesKey);
		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
	}

	private void addSelectedColumn() {
		final Column<TediResult, Boolean> selectedColumn = new Column<TediResult, Boolean>(
				new CheckboxCell(false,true)){

			@Override
			public Boolean getValue(TediResult result) {
				return model.isSelected(result);
			}
		};
		selectedColumn.setFieldUpdater(new FieldUpdater<TediResult, Boolean>() {
			
			@Override
			public void update(int index, TediResult object, Boolean value) {
				model.setSelected(object, value);
			}
		});
		this.addColumn(selectedColumn);
		this.setColumnWidth(selectedColumn, 20, Unit.PX);
	}
	
	private void addEditColumn() {
		ButtonCell button = new ButtonCell(new EditButtonSafeHtmlTemplates()) {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
				  sb.append(data);
			  }
		};
		final Column<TediResult, String> editColumn = new Column<TediResult, String>(button){

			@Override
			public String getValue(TediResult object) {
				return "EDIT";
			}
		};
		editColumn.setFieldUpdater( new FieldUpdater<TediResult, String>() {
			
			@Override
			public void update(int index, TediResult object, String value) {
				SelectionEvent.fire(TediInvoiceTable.this, object);
			}
		});
		this.addColumn(editColumn);
		this.setColumnWidth(editColumn, 30, Unit.PX);
	}

	private void addStatusColumn() {
		final Column<TediResult, ImageResource> statusColumn = new Column<TediResult, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(TediResult result) {
				TediLevel curLevel = result.getMoreSeriousLevel();
				if (curLevel == TediLevel.INF) {
					return AON.AON_RESOURCES.aonIconPointLightGreen();
				} else if (curLevel == TediLevel.WRN) {
					return AON.AON_RESOURCES.aonIconPointOrange();
				} else if (curLevel == TediLevel.ERR) {
					return AON.AON_RESOURCES.aonIconPointRed();
				}
				return AON.AON_RESOURCES.aonIconPointGreen();
			}
		};
		this.addColumn(statusColumn);
		this.setColumnWidth(statusColumn, 20, Unit.PX);
	}
	
	private void addInvoiceTypeColumn() {
		final TextColumn<TediResult> invoiceTypeColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().getType().getDescription();
			}
		};
		this.addColumn(invoiceTypeColumn, AON.MSG.type());
		this.setColumnWidth(invoiceTypeColumn, 80, Unit.PX);
	}

	private void addAttachColumn() {
		final Column<TediResult, ImageResource> attachColumn = new Column<TediResult, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(TediResult result) {
				return result.hasAttach() ? AON.AON_RESOURCES.aonIconAttach() : null;
			}
		};
		this.addColumn(attachColumn);
		this.setColumnWidth(attachColumn, 20, Unit.PX);
	}

	private void addIssueDateColumn() {
		final TextColumn<TediResult> issueDateColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().getIssueDate() != null
						? AON.DATE_FORMAT.format(result.getInvoice().getIssueDate())
						: "";
			}
		};
		this.addColumn(issueDateColumn, AON.MSG.date());
		issueDateColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		this.setColumnWidth(issueDateColumn, 50, Unit.PX);
	}

	private void addTotalColumn() {
		final TextColumn<TediResult> amountColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return AON.FMT.format(result.getInvoice().getTotal());
			}
		};
		this.addColumn(amountColumn, AON.MSG.total());
		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
		this.setColumnWidth(amountColumn, 100, Unit.PX);
	}

	private void addNumberColumn() {
		final TextColumn<TediResult> nameColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getInvoice().isSales()
						? AonStringUtils.appendIfMissing(result.getInvoice().getSeries(), "/")
								+ result.getInvoice().getNumber()
						: result.getInvoice().getReferenceCode();
			}
		};
		this.addColumn(nameColumn, AON.MSG.invoiceNumber());
		this.setColumnWidth(nameColumn, 150, Unit.PX);
	}

	private void addDocumentColumn() {
		final TextColumn<TediResult> documentColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getTedi().getRegistry() != null ? result.getTedi().getRegistry().getDocument() : "";
			}
		};
		this.addColumn(documentColumn, AON.MSG.document());
		this.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addNameColumn() {
		final TextColumn<TediResult> nameColumn = new TextColumn<TediResult>() {
			@Override
			public String getValue(TediResult result) {
				return result.getTedi().getRegistry() != null ? result.getTedi().getRegistry().getName() : "";
			}
		};
		this.addColumn(nameColumn, AON.MSG.name());
		this.setColumnWidth(nameColumn, "auto");
	}

	public Set<TediResult> getSelectedSet() {
		return model.getSelectedSet();
	}

	public void clearSelection() {
		model.clear();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<TediResult> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}


}
