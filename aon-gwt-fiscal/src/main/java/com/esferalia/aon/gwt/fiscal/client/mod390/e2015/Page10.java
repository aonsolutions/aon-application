package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.cell.SizableTextInputCell;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.Prorrata;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Page10 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface CnaeButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-lookup\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class CnaeButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static CnaeButtonTemplate template;

		protected CnaeButtonSafeHtmlTemplates() {
			template = GWT.create(CnaeButtonTemplate.class);
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

	interface DeleteButtonTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"button\" value=\"&nbsp;\" class=\"aon-icon-delete\" style=\"border: medium none !important;\">")
		SafeHtml render(String option);
	}
	static class DeleteButtonSafeHtmlTemplates implements SafeHtmlRenderer<String> {

		private static DeleteButtonTemplate template;

		protected DeleteButtonSafeHtmlTemplates() {
			template = GWT.create(DeleteButtonTemplate.class);
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

	interface PageBinder extends UiBinder<Widget, Page10> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902015CallBack cbk;
	
	private Cnae2009Panel cnaePanel;
	
	private ListDataProvider<Prorrata> dataProvider;
	@UiField(provided = true)
	CellTable<Prorrata> table;

	@UiField
	Button newProrrata;
	
	public Page10(Mod3902015 m390) {
		cnaePanel = new Cnae2009Panel();

		table = new CellTable<Prorrata>(50, AON.AON_CELL_TABLE_STYLE);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider = new ListDataProvider<Prorrata>();
		dataProvider.addDataDisplay(table);
		addCnaePanelColumn();
		addCnaeColumn();
		addActivityColumn();
		addTypeColumn();
		addAmountColumn();
		addAmountWithRightColumn();
		addPercentColumn();
		addRemoveColumn();

		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
	}

	private void addCnaePanelColumn() {
		
		ButtonCell removeButton = new ButtonCell( new CnaeButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<Prorrata,String> col = new Column<Prorrata,String>(removeButton) {
		  public String getValue(Prorrata object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, final Prorrata pro, String value) {
		    	cnaePanel = new Cnae2009Panel( new Cnae2009Panel.SelectionCallBack() {
					@Override
					public void onSelect(CNAE2009 selected) {
						pro.setCnae( selected.getCode());
						pro.setActivity( selected.getDescription());
						table.redraw();
					}
					@Override
					public void onClose() {
						// Nothing
					}
				});		    	
		    	cnaePanel.onShow();
		    }
		});		
		table.addColumn(col);
		table.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addCnaeColumn() {
		SizableTextInputCell input = new SizableTextInputCell(5);
		Column<Prorrata, String> col = new Column<Prorrata, String>(
				input) {
			@Override
			public String getValue(Prorrata ca) {
				return ca.getCnae();
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	dataProvider.getList().get(index).setCnae(value);
		    }
		});		
		table.addColumn(col, "C.N.A.E.");
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addActivityColumn() {
		SizableTextInputCell input = new SizableTextInputCell(40);
		Column<Prorrata, String> col = new Column<Prorrata, String>(
				input) {
			@Override
			public String getValue(Prorrata ca) {
				return ca.getActivity();
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	dataProvider.getList().get(index).setActivity(value);
		    }
		});		
		table.addColumn(col, AON.MSG.activityDescription());
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addTypeColumn() {
		SizableTextInputCell input = new SizableTextInputCell(2);
		Column<Prorrata, String> col = new Column<Prorrata, String>(
				input) {
			@Override
			public String getValue(Prorrata ca) {
				return ca.getType();
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	dataProvider.getList().get(index).setType(value);
		    }
		});		
		table.addColumn(col, AON.MSG.type() + " E/G");
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}


	private void addAmountColumn() {
		SizableTextInputCell input = new SizableTextInputCell(15);
		Column<Prorrata, String> col = new Column<Prorrata, String>(input) {
			@Override
			public String getValue(Prorrata ca) {
				return Double.toString(ca.getAmount());
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	double val = 0;
		    	try {
		    		val = Double.parseDouble(value);
		    	} catch (NumberFormatException e) {
		    		// Nothing.
		    	}
		    	dataProvider.getList().get(index).setAmount(val);
		    }
		});		
		table.addColumn(col, AON.MSG.operationsAmount());
		col.setCellStyleNames(AON.AON_CSS.aonTextRight());
	}

	private void addAmountWithRightColumn() {
		SizableTextInputCell input = new SizableTextInputCell(15);
		Column<Prorrata, String> col = new Column<Prorrata, String>(input) {
			@Override
			public String getValue(Prorrata ca) {
				return Double.toString(ca.getAmountWithRight());
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	double val = 0;
		    	try {
		    		val = Double.parseDouble(value);
		    	} catch (NumberFormatException e) {
		    		// Nothing.
		    	}
		    	dataProvider.getList().get(index).setAmountWithRight(val);
		    }
		});		
		table.addColumn(col, AON.MSG.operationsAmountWithRight());
		col.setCellStyleNames(AON.AON_CSS.aonTextRight());
	}
	
	private void addPercentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(7);
		Column<Prorrata, String> col = new Column<Prorrata, String>(input) {
			@Override
			public String getValue(Prorrata ca) {
				return Double.toString(ca.getPercent());
			}
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata ca, String value) {
		    	double val = 0;
		    	try {
		    		val = Double.parseDouble(value);
		    	} catch (NumberFormatException e) {
		    		// Nothing.
		    	}
		    	dataProvider.getList().get(index).setPercent(val);
		    }
		});		
		table.addColumn(col, AON.MSG.percent());
		col.setCellStyleNames(AON.AON_CSS.aonTextRight());
	}

	private void addRemoveColumn() {
		
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<Prorrata,String> col = new Column<Prorrata,String>(removeButton) {
		  public String getValue(Prorrata object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<Prorrata, String>() {
		    public void update(int index, Prorrata lr, String value) {
		    	if (Window.confirm(AON.MSG.confirmDeleteAction())) {
		    		dataProvider.getList().remove(index);
		    		table.redraw();
		    	}
		    }
		});		
		table.addColumn(col);
		table.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newProrrata")
	void onNewProrrata(ClickEvent event) {
		dataProvider.getList().add(new Prorrata());
		table.redraw();		    		
	}
	
	private void setValue(Mod3902015 m390) {
		dataProvider = new ListDataProvider<Prorrata>(m390.getProrratas());
		dataProvider.addDataDisplay(table);
		table.redraw();
	}

	@Override
	public void populate(Mod3902015 mod390) {
		// Nothing
	}
	
	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.cbk = callback;
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		setValue(m390);
	}
}
