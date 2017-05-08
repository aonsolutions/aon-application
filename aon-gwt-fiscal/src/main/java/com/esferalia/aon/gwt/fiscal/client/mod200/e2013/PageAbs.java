package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Behaviour.BEHAVIOUR_KEYS_MAP;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2013.Mod2002013Object.IMod200ChangeListener;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class PageAbs extends ResizeComposite {

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

	protected Mod2002013Object mod200Object;

	private HashMap<Mod2002013Key, DoubleBox> inputs = new HashMap<Mod2002013Key, DoubleBox>();
	private HashMap<Mod2002013Key, BoxLabel> labels = new HashMap<Mod2002013Key, BoxLabel>();

	@UiField
	Panel basePanel;

	@UiField(provided = true)
	FlexTable table;
	
	public PageAbs() {
		table = new FlexTable();
	}
	
	public void dump(Mod2002013Object mod200Object) {
		this.mod200Object = mod200Object;
		this.mod200Object.register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002013 mod200) {
				for (Mod2002013Key key : mod200.getDraftMap().keySet()) {
					if (inputs.containsKey(key)) {
						DoubleBox input = inputs.get(key);
						DoubleVariable2013 var = mod200.getDraftMap().get(key);
						if (!var.isChangedByUser()) {
							input.setValue(var.getValue()); ;
						}
						input.addStyleName(AON.AON_CSS.aonChanged());
					}
					if (labels.containsKey(key)) {
						BoxLabel label = labels.get(key);
						label.removeErrorState();
					}
				}
			}
			
		});
		initializeTable();
	}
	
	public Map<Mod2002013Key, DoubleBox> getInputs() {
		return inputs;
	}
	public Map<Mod2002013Key, BoxLabel> getLabels() {
		return labels;
	}
	
	protected int paintKey(final Mod2002013Key key,int row) {
		return paintKey(table,key,row);
	}
	
	protected int paintKey(FlexTable tab,final Mod2002013Key key,int row) {
		Label desc = new Label(key.getDescription() );
		if (isTitle(key)) {
			desc.setStyleName(AON.AON_CSS.aonBold());
		}
		tab.setWidget(row, 0, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
		paintKeyField(tab,key,row,1);	
		return ++row;
	}
	protected void paintKeyField(FlexTable tab,final Mod2002013Key key,int row, int col) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode( mod200Object.getAdministration());
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId);
			getLabels().put(key, code);
			panel.add(code);
		}
		

		final DoubleBox text = new DoubleBox();
		text.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					mod200Object.doubleValueChanged(key, d );
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		getInputs().put(key, text);
		if (!isTitle(key)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	protected boolean isDisabled(Mod2002013Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}

	protected boolean isTitle(Mod2002013Key key) {
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return (behaviour != null && behaviour[0]); 
	}

	protected int paintKeyBreakdown(FlexTable tab,int row,final String label, IMod200KeysProvider[] keysProvider,String[] headers) {
		
		FlexTable tableDetail = getFlexTable(tab,row,label,headers);
		int r = 1;
		int col = 0;
		for (IMod200KeysProvider key : keysProvider) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final Mod2002013Key k : key.getKeys() ) {
				if (k != null){
					Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k.toString());
					boolean disabled = behaviour != null && behaviour[1];
	
					FlowPanel panel = new FlowPanel();
					BoxLabel code = new BoxLabel(k.getCode( mod200Object.getAdministration() ));
					panel.add(code);
					getLabels().put(k, code);
					
					final DoubleBox text = new DoubleBox(12);
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							try {
								Double d = text.getValueOrThrow();
								text.addStyleName(AON.AON_CSS.aonChanged());
								mod200Object.doubleValueChanged(k, d);
							} catch (ParseException e) {
								// nothing.
							}
						}
					});
					text.setValue(mod200Object.getDoubleValue(k));
					text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
					text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
					text.setEnabled(!disabled);
					panel.add(text);
					
					getInputs().put(k, text);
					tableDetail.setWidget(r, col, panel);
					tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonTextRight());
					tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonNowrap());
				}
				++col;
			}
			++r;
		}
		return ++row;
	}
	
	protected FlexTable getFlexTable(FlexTable tab,int row,final String label, String[] headers) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(AON.AON_CSS.aonGroup());
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.addStyleName(AON.AON_CSS.aonGroupTitle());
		container.add(titleContainer);
		final InlineLabel titleLabel = new InlineLabel();
		titleLabel.addStyleName(AON.AON_CSS.aonClickable());
		titleContainer.add(titleLabel);
		titleLabel.setText(label + " \u25BA");
		
		final DisclosurePanel bodyContainer = new DisclosurePanel();
		bodyContainer.addStyleName(AON.AON_CSS.aonWidthAll());
		titleLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bodyContainer.setOpen(!bodyContainer.isOpen());
			}
		});
		bodyContainer.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BC");
				bodyContainer.addStyleName(AON.AON_CSS.aonFiscalInnerGroupBody());
			}
		});
		bodyContainer.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BA");
				bodyContainer.removeStyleName(AON.AON_CSS.aonFiscalInnerGroupBody());
			}
		});
		bodyContainer.addStyleName(AON.AON_CSS.aonGroupBody());
		container.add(bodyContainer);
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(AON.AON_CSS.aonWidthAll());
		bodyContainer.add(tableDetail);
		int r = 0;
		int col = 0;
		if (headers != null) {
			for (String  headerText : headers ) {
				Label headerLabel = new Label(headerText);
				tableDetail.setWidget(r, col, headerLabel);
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBold());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonBorderBottom());
				tableDetail.getFlexCellFormatter().addStyleName(r, col, AON.AON_CSS.aonTextCenter());
				if (col>0) {
					tableDetail.getColumnFormatter().setWidth(col, "160px");
				}
				++col;
			}
		}
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		return tableDetail;
	}

	protected abstract void initializeTable();
}
