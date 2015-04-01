package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.fiscal.client.tree.EpigraphSelectionPanel.SelectionCallBack;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.IntegerStringPair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityForm extends ResizeComposite implements FiscalNodeWidget<FiscalActivity>{
	
	public static class ActivityInput {
		private String label;
		private Widget widget;
		private Integer infoKey;
		private Integer infoKeyType;
		
		public String getLabel() {
			return this.label;
		}
		public ActivityInput setLabel(String label) {
			this.label = label;
			return this;
		}

		public Widget getWidget() {
			return this.widget;
		}
		public ActivityInput setWidget(Widget widget) {
			this.widget = widget;
			return this;
		}
		public Integer getInfoKey() {
			return infoKey;
		}
		public ActivityInput setInfoKey(Integer infoKey) {
			this.infoKey = infoKey;
			return this;
		}
		public Integer getInfoKeyType() {
			return infoKeyType;
		}
		public ActivityInput setInfoKeyType(Integer infoKeyType) {
			this.infoKeyType = infoKeyType;
			return this;
		}
		public String getValue() {
			if (widget instanceof TextBox) {
				return ((TextBox) widget).getValue();	
			} else if (widget instanceof ListBox) {
				return ((ListBox) widget).getSelectedValue();
			}
			Window.alert("Unknown widget");
			return null;
		}
		public void setValue(String v) {
			if (widget instanceof TextBox) {
				((TextBox) widget).setValue(v);	
			} else if (widget instanceof ListBox) {
				ListBox l = (ListBox) widget;
				for (int i = 0; i < l.getItemCount(); i++) {
					if (AonStringUtils.equals(l.getValue(i),v)) {
						l.setSelectedIndex(i);
						break;
					}
				}
			} else {
				Window.alert("Unknown widget");
			}
		}
	}

	interface ActivityFormBinder extends
			UiBinder<Widget, ActivityForm> {
	}

	private static final ActivityFormBinder panelBinder = GWT
			.create(ActivityFormBinder.class);
	
	TreeNode<FiscalActivity> node;
	EpigraphSelectionPanel epigraphSelection; 
	
	private LinkedHashMap<Integer,LinkedHashMap<Integer,ActivityInput>> inputs 
		= new LinkedHashMap<Integer, LinkedHashMap<Integer,ActivityInput>>();	
	
	@UiField
	Button saveButton;
	@UiField
	Button calculateButton;
	
	@UiField
	Button epigraphButton;
	
	@UiField
	Label year;
	@UiField
	Label epigraph;
	@UiField
	Label description;
//	@UiField
//	CheckBox farmer;
	@UiField
	Label maxPerson;
	@UiField
	Label maxImport;
	@UiField
	Label vatPercent;
	
	@UiField
	TabPanel tab;
	@UiField
	SimplePanel infoContainer;
	@UiField
	SimplePanel moduleIRPFContainer;
	@UiField
	SimplePanel infoIRPFContainer;
	@UiField
	SimplePanel moduleIVAContainer;
	@UiField
	SimplePanel infoIVAContainer;

	public ActivityForm() {
		
		epigraphSelection = new EpigraphSelectionPanel(new SelectionCallBack() {
			
			@Override
			public void onSelect(Epigraph epigraph) {
				if (node.getWidget() != null && node.getWidget() instanceof Label) {
					Label label = (Label) node.getWidget();
					label.setText(epigraph.getEpigraph() + " - " + AonStringUtils.abbreviate(epigraph.getDescription(), 40));
				}
				node.getTreeObject().setEpigraph( epigraph.getEpigraph());
				node.getTreeObject().setDescription( epigraph.getDescription());
				node.getTreeObject().setMaxPerson( epigraph.getLimPers() );
				node.getTreeObject().setMaxImport( epigraph.getLimExceso());
				node.getTreeObject().setVatPercent( epigraph.getPorcMin() );
				
				FiscalTree.FISCAL_SERVICE.getFiscalActivityFor(FiscalTree.getCurrentDomainName()
						,epigraph,node.getTreeObject()
						,new AsyncCallback<FiscalActivity>() {

							@Override
							public void onSuccess(FiscalActivity result) {
								populate(result);
							}
							@Override
							public void onFailure(Throwable caught) {
								DialogMessages.alertErrorWidget(caught.getMessage());
							}
				});
				
			}
			
			@Override
			public void onClose() {
			}
		});
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
	}
	
	@Override
	public void select(TreeNode<FiscalActivity> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getFiscalActivity(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<FiscalActivity>() {

						@Override
						public void onSuccess(FiscalActivity result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(node.getTreeObject());
			if ( AonStringUtils.isBlank( node.getTreeObject().getEpigraph() )) {
				onEpigraphButtonClick(null);
			}
		}
	}

	private void populate(FiscalActivity fiscalActivity) {
		node.setTreeObject( fiscalActivity );
		saveButton.setEnabled(isEnabled(fiscalActivity));
		calculateButton.setEnabled(isEnabled(fiscalActivity));
		year.setText(AonNumberUtils.toString(fiscalActivity.getYear()));
		epigraph.setText(fiscalActivity.getEpigraph());
		if (AonStringUtils.isNotBlank(fiscalActivity.getEpigraph())) {
			description.setText(fiscalActivity.getDescription());
			maxPerson.setText(AON.FMT.format(fiscalActivity.getMaxPerson()));
			maxImport.setText(AON.FMT.format(fiscalActivity.getMaxImport()));
			vatPercent.setText(AON.FMT.format(fiscalActivity.getVatPercent()));
		} else {
			description.setText(null);
			maxPerson.setText(null);
			maxImport.setText(null);
			vatPercent.setText(null);
		}
		tab.setVisible(fiscalActivity.hasInfoOrModules());
		if ( tab.isVisible() ) {
			if (fiscalActivity.hasInfo()) {
				infoContainer.setWidget(getInfoTable(fiscalActivity,fiscalActivity.getMap().get(
								FiscalActivityInfoKeyType.INFO.ordinal())));
			}
			if ( fiscalActivity.hasIRPFModules() ) {
				moduleIRPFContainer.setWidget( getModuleTable(fiscalActivity,fiscalActivity.getMap().get(
						FiscalActivityInfoKeyType.IRPF_MODULE.ordinal())));
			}
			if ( fiscalActivity.hasIRPFInfo() ) {
				infoIRPFContainer.setWidget( getInfoTable(fiscalActivity,fiscalActivity.getMap().get(
						FiscalActivityInfoKeyType.IRPF_INFO.ordinal())));
			}
			if ( fiscalActivity.hasVATModules() ) {
				moduleIVAContainer.setWidget( getModuleTable(fiscalActivity,fiscalActivity.getMap().get(
						FiscalActivityInfoKeyType.VAT_MODULE.ordinal())));
			}
			if ( fiscalActivity.hasVATInfo() ) {
				infoIVAContainer.setWidget( getInfoTable(fiscalActivity,fiscalActivity.getMap().get(
						FiscalActivityInfoKeyType.VAT_INFO.ordinal())));
			}
			tab.selectTab(0);
		}
	}
	

	private boolean isEnabled(FiscalActivity fiscalActivity) {
		return ( fiscalActivity.getYear() >= 2015 );
	}

	private FlexTable getInfoTable(FiscalActivity fiscalActivity,Map<Integer,FiscalActivityInfo> map) {
		if (map != null && map.size() > 0) {
			FlexTable table = new FlexTable();
			table.setStyleName(AON.AON_CSS.aonPanelGrid());
			table.addStyleName(AON.AON_CSS.aonWidthAll());
			table.addStyleName(AON.AON_CSS.aonMarginTop());
			table.setCellSpacing(0);
			int row = 0;
			for (FiscalActivityInfo info : map.values()) {
				Label desc = new Label(info.getInfoKey().getDescription() );
				table.setWidget(row, 0, desc);
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
				table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
				table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
				table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth150());
				if (info.getInfoKey().isChoice()) {
					ListBox listBox = new ListBox();
					listBox.setEnabled(isEnabled(fiscalActivity));
					int i = 0;
					for (IntegerStringPair pair : info.getInfoKey().getOptions() ) {
						String value = pair.getKey().toString();
						listBox.addItem(pair.getValue(), value);
						if (AonStringUtils.equals(value, info.getValue())) {
							listBox.setSelectedIndex(i);
						}
						i++;
					}
					table.setWidget(row, 1, listBox);
					listBox.addChangeHandler(new ChangeHandler() {
						
						@Override
						public void onChange(ChangeEvent event) {
							calculate();
						}
					});
					put(info.getInfoType().ordinal(),info.getInfoKey().ordinal()
						, new ActivityInput()
							.setLabel(desc.getText())
							.setInfoKey(info.getInfoKey().ordinal())
							.setInfoKeyType(info.getInfoType().ordinal())
							.setWidget(listBox));
				} else {
					TextBox text = new TextBox();
					text.setWidth("100px");
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							
						}
					});
					text.setValue(info.getValue());
					text.setStyleName(AON.AON_CSS.aonInputText());
					text.setEnabled(isEnabled(fiscalActivity));
					table.setWidget(row, 1, text);
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							calculate();
						}
					});
					put(info.getInfoType().ordinal(),info.getInfoKey().ordinal()
						, new ActivityInput()
							.setLabel(desc.getText())
							.setInfoKey(info.getInfoKey().ordinal())
							.setInfoKeyType(info.getInfoType().ordinal())
							.setWidget(text));
				}
				table.getFlexCellFormatter().setStyleName(row,1, AON.AON_CSS.aonPanelGridEven());
				++row;
			}
			return table;
		}
		return null;
	}

	private void put(Integer infoType,Integer infoKey, ActivityInput widget) {
		if (inputs.get(infoType) == null) {
			inputs.put(infoType, new LinkedHashMap<Integer, ActivityForm.ActivityInput>());
		}
		inputs.get(infoType).put(infoKey, widget);
	}
	
	private FlexTable getModuleTable(FiscalActivity fiscalActivity,Map<Integer,FiscalActivityInfo> map) {
		if (map != null && map.size() > 0) {
			FlexTable table = new FlexTable();
			table.setStyleName(AON.AON_CSS.aonPanelGrid());
			table.addStyleName(AON.AON_CSS.aonWidthAll());
			table.addStyleName(AON.AON_CSS.aonMarginTop());
			table.setCellSpacing(0);
			int row = 0;
			for (final FiscalActivityInfo info : map.values()) {
				Label desc = new Label(info.getInfoKey().getDescription() );
				table.addStyleName(AON.AON_CSS.aonWidthAll());
				table.setWidget(row, 0, desc);
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
				table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
				
				table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
				table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth130());
				
				table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
				table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth20());
				
				table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonWidth130());
				
				table.getFlexCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextRight());
				table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonWidth130());
				
				table.getFlexCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
				table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
				table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonWidth150());

				final TextBox text = new TextBox();
				text.setWidth("100px");
				text.setValue(info.getValue());
				text.setStyleName(AON.AON_CSS.aonInputText());
				text.setEnabled(!info.getInfoKey().hasDetails() && isEnabled(fiscalActivity));
				table.setWidget(row, 1, text);
				put(info.getInfoType().ordinal(),info.getInfoKey().ordinal()
					, new ActivityInput()
						.setLabel(desc.getText())
						.setInfoKey(info.getInfoKey().ordinal())
						.setInfoKeyType(info.getInfoType().ordinal())
						.setWidget(text));
				
				// *************
				if (info.getInfoKey().hasDetails()) {
					Button detailButton = new Button();
					detailButton.setStyleName(AON.AON_CSS.aonIconButton());
					detailButton.addStyleName(AON.AON_CSS.aonIconLoupe());
					final CustomDialog detailDialog = new CustomDialog();
					detailDialog.setVisible(false);
					detailDialog.setAnimationEnabled(true);
					detailDialog.setGlassEnabled(true);
					detailDialog.setModal(true);
					detailDialog.setCaption( info.getInfoKey().getDescription() );

					LinkedHashMap<Integer,FiscalActivityInfo> detailMap = new LinkedHashMap<Integer, FiscalActivityInfo>();
					for (final FiscalActivityInfoKey detailKey : info.getInfoKey().getDetailKeys()) {
						detailMap.put(detailKey.ordinal()
							, fiscalActivity.getMap().get(FiscalActivityInfoKeyType.MODULE_DETAIL.ordinal()).get(detailKey.ordinal()));
					}
					FlexTable detailTable = getInfoTable(fiscalActivity, detailMap); 
					detailDialog.add( detailTable );
					Button accept = new Button();
					accept.setText(AON.MSG.accept());
					accept.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							detailDialog.hide();
						}
					});
					int detailRow = detailTable.getRowCount();
					detailTable.setWidget(detailRow, 0, accept);
					detailTable.getFlexCellFormatter().setColSpan(detailRow, 0, 2);
					detailTable.getFlexCellFormatter().setStyleName(detailRow, 0, AON.AON_CSS.aonTextCenter());
					detailButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							detailDialog.center();
							detailDialog.show();
						}
					});
					table.setWidget(row, 2, detailButton);
				}
				// *************

				
				Label unit = new Label(info.getUnit() );
				table.setWidget(row, 3, unit);
				
				Label factor = new Label( AON.FMT.format( info.getFactor()) );
				table.setWidget(row, 4, factor);
				
				final TextBox base = new TextBox();
				base.setWidth("100px");
				base.setValue(AON.FMT.format( info.getBase()));
				base.setStyleName(AON.AON_CSS.aonInputText());
				base.setEnabled(false);
				table.setWidget(row, 5, base);

				text.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						double value = AonNumberUtils.todouble(text.getValue());
						base.setValue(AON.FMT.format( AonMathUtils.round(value * info.getBase())));
						calculate();
					}
				});

				++row;
			}
			return table;
		}
		return null;
	}
	
	private void populateTreeObject() {
		for (Integer type : inputs.keySet()) {
			for (Integer key : inputs.get(type).keySet()) {
				ActivityInput input = inputs.get(type).get(key);
				node.getTreeObject().setValue(input.getInfoKeyType(),input.getInfoKey(),input.getValue());
			}
		}
	}
	
	private void populateTreeInputs(FiscalActivity fa) {
		node.setTreeObject(fa);
		for (Integer type : inputs.keySet()) {
			for (Integer key : inputs.get(type).keySet()) {
				ActivityInput input = inputs.get(type).get(key);
				String v = fa.getValue(input.getInfoKeyType(),input.getInfoKey());
				input.setValue(v);
			}
		}
	}
	private void calculate() {
		populateTreeObject();
		FiscalTree.FISCAL_SERVICE.calculate(FiscalTree.getCurrentDomainName()
				,node.getTreeObject()
				,new AsyncCallback<FiscalActivity>() {

					@Override
					public void onSuccess(FiscalActivity result) {
						populateTreeInputs(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(caught.getMessage());								
					}

		});
	}
	
	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.calculateAction())) {
			calculate();
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			populateTreeObject();
			FiscalTree.FISCAL_SERVICE.save(FiscalTree.getCurrentDomainName(), node.getTreeObject()
					,new AsyncCallback<FiscalActivity>() {
	
						@Override
						public void onSuccess(FiscalActivity result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
	
			});
		}
	}

	@UiHandler("epigraphButton")
	void onEpigraphButtonClick(ClickEvent event) {
		epigraphSelection.center();
		epigraphSelection.show();
	}

}
