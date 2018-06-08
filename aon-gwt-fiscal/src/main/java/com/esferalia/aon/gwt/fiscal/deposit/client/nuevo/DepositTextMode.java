package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.deposit.shared.DepositMenu;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.vaadin.VaadinUploadElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;



public class DepositTextMode extends AonTemplate2 {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	DockLayoutPanel d2Content;
	FlexTable header;
	ScrollPanel page;
	
	API API;
	AonData aonData;
	Company company;
	Map<String, String> deposit;
	Integer id;
	String name;
	DepositMenu depositMenu;
	
	Stack<Map<String, String>> undoStack = new Stack<Map<String, String>>();
	Stack<Map<String, String>> redoStack = new Stack<Map<String, String>>();
	
	DepositTextMode thiz = this;
	
	public DepositTextMode(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperIconButtonElement.SRC,
				AonComboBoxElement.SRC,
				VaadinUploadElement.SRC
		));
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			getInma().getTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
				
				@Override
				public void onSuccess(Vector<MemoryTemplate> result) {
					if(result.size()> 0) {
						setId(result.get(0).getId());
						setName(result.get(0).getName());
					}
					startApplication(result);
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
			return null;
		});
	}
	
	private void startApplication(Vector<MemoryTemplate> result) {
		toolbar();
		westContent(result);
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Cuentas Anuales");
		toolbar.addButton("Deshacer","aon-icon-undo").addClickHandler(undoClickHandler());
		toolbar.addButton("Rehacer","aon-icon-redo").addClickHandler(redoClickHandler());
		toolbar.addButton("Nuevo",AON.AON_CSS.aonIconReset()).addClickHandler(createClickHandler());
		toolbar.addButton("Borrar",AON.AON_CSS.aonIconDelete()).addClickHandler(deleteClickHandler());
		setToolbar(toolbar);
	}
	
	private void westContent(Vector<MemoryTemplate> result) {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		setWestContent(menu(result));
	}
  

	private VerticalPanel menu(Vector<MemoryTemplate> templates) {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		Boolean first = true;
		for(MemoryTemplate mt : templates) {
			PaperItem ej = buildItem(mt.getName(), first ? "arrow-drop-down":"arrow-drop-up", true);
			VerticalPanel ejContent = buildSubEjercicio(first, mt.getId(), mt.getName());
			ej.addClickHandler(submenuClickHandler(ej, ejContent));
			vp.add(ej);
			vp.add(ejContent);
			if(first) first = false;
		}

		return vp;
	}
	
	public PaperItem buildItem(String text, String icon, Boolean title){
		PaperItem pi = new PaperItem();
		pi.setTitle(text);
		if(icon != null) {
			IronIcon ironIcon = new IronIcon();
			ironIcon.setIcon(icon);
			ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
			pi.add(ironIcon);
		}
		pi.add(new Label(text));
		pi.setStyle("min-height:24px;font-size:12px;padding:0px;cursor:pointer;" + (title ? "font-weight:bold;" : "")); 
		return pi;
	}

	    
    public VerticalPanel buildSubEjercicio(Boolean visible, Integer id, String name){
    	VerticalPanel vp = new VerticalPanel();
	    	
    	PaperItem ae = buildItem(DepositMenu.AE.getDescription(), null, false);
    	ae.addClickHandler(menuClickHandler(DepositMenu.AE, id, name));
    	vp.add(ae);
    	
    	PaperItem bp = buildItem(DepositMenu.BP.getDescription(), null, false);
    	bp.addClickHandler(menuClickHandler(DepositMenu.BP, id, name));
    	vp.add(bp);
    	
    	PaperItem ar = buildItem(DepositMenu.AR.getDescription(), null, false);
    	ar.addClickHandler(menuClickHandler(DepositMenu.AR, id, name));
    	vp.add(ar);
    	
    	PaperItem nrv = buildItem(DepositMenu.NRV.getDescription(), null, false);
    	nrv.addClickHandler(menuClickHandler(DepositMenu.NRV, id, name));
    	vp.add(nrv);
    	
    	PaperItem imiii = buildItem(DepositMenu.IMIII.getDescription(), null, false);
    	imiii.addClickHandler(menuClickHandler(DepositMenu.IMIII_TL, id, name));
    	vp.add(imiii);
    	
    	PaperItem af = buildItem(DepositMenu.AF.getDescription(), null, false);
    	af.addClickHandler(menuClickHandler(DepositMenu.PF_TL, id, name));
    	vp.add(af);
    
    	PaperItem pf = buildItem(DepositMenu.PF.getDescription(), null, false);
    	pf.addClickHandler(menuClickHandler(DepositMenu.PF_TL, id, name));
    	vp.add(pf);
    	
    	PaperItem fp = buildItem(DepositMenu.FP.getDescription(), null, false);
    	fp.addClickHandler(menuClickHandler(DepositMenu.FP, id, name));
    	vp.add(fp);
    	
    	PaperItem sf = buildItem(DepositMenu.SF.getDescription(), null, false);
    	sf.addClickHandler(menuClickHandler(DepositMenu.SF, id, name));
    	vp.add(sf);
    	
    	PaperItem sdl = buildItem(DepositMenu.SDL.getDescription(), null, false);
    	sdl.addClickHandler(menuClickHandler(DepositMenu.SDL_TL, id, name));
    	vp.add(sdl);
    	
    	PaperItem opv = buildItem(DepositMenu.OPV.getDescription(), null, false);
    	opv.addClickHandler(menuClickHandler(DepositMenu.OPV_TL, id, name));
    	vp.add(opv);
    	
    	PaperItem oi = buildItem(DepositMenu.OI.getDescription(), null, false);
    	oi.addClickHandler(menuClickHandler(DepositMenu.OI_TL, id, name));
    	vp.add(oi);
    	
    	PaperItem im = buildItem(DepositMenu.IM.getDescription(), null, false);
    	im.addClickHandler(menuClickHandler(DepositMenu.IM_TL, id, name));
    	vp.add(im);

    	vp.setVisible(visible);
    	vp.setWidth("100%");
    	vp.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	return vp;	
    }
	
	private void content() {
		header();
		deposit();
		setD2Content(new DockLayoutPanel(Unit.PX));
		getD2Content().addNorth(getHeader(), 55);
		getD2Content().add(getPage());
		setContent(getD2Content());
	}
	
	private void header() {
		setHeader(new FlexTable());
		getHeader().setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(AON.AON_CSS.aonRegistroMercantilImage());

		getHeader().setWidget(0, 0, image);
		getHeader().getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		getHeader().getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		getHeader().setWidget(0, 1, new Label("Cuentas Anuales"));
		getHeader().getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		getHeader().getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFiscalRegistroMercantil2());
		getHeader().getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		Label typeLabel = new Label("Plantilla");
		getHeader().setWidget(0, 2, typeLabel);
		getHeader().getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		getHeader().getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalRegistroMercantil2());
		
		getHeader().setWidget(1, 0, new Label(getName()));
		getHeader().getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		getHeader().getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalRegistroMercantil2());
	}
	
	private void deposit() {
		setPage(new ScrollPanel());
		inma.getSchemaTextMode(getAonData(), getId(), new AsyncCallback<Map<String, String>>() {
			@Override
			public void onSuccess(Map<String, String> result) {
				setDeposit(result);
				updatePage(DepositMenu.AE);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void refreshPage() {
		updatePage(getDepositMenu());
	}
	
	public void updateHeader() {
		getHeader().setWidget(1, 0, new Label(getName()));
	}
	
	public void updatePage(DepositMenu depositMenu) {
		setDepositMenu(depositMenu);
		if(DepositMenu.AE.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.AE.getDescription(), "MAT1", true));
		if(DepositMenu.BP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.BP.getDescription(), "MAT2", true));
		if(DepositMenu.AR_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.AR.getDescription(), "MAT3", true));
		if(DepositMenu.NRV.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.NRV.getDescription(), "MAT4", true));
		if(DepositMenu.IMIII_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.IMIII.getDescription(), "MAT5", true));
		if(DepositMenu.AF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.AF.getDescription(), "MAT6", true));
		if(DepositMenu.PF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.PF.getDescription(), "MAT7", true));
		if(DepositMenu.FP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.FP.getDescription(), "MAT8", true));
		if(DepositMenu.SF.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.SF.getDescription(), "MAT9", true));
		if(DepositMenu.SDL_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.SDL.getDescription(), "MAT11", true));
		if(DepositMenu.OPV_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.OPV.getDescription(), "MAT12", true));
		if(DepositMenu.OI_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.OI.getDescription(), "MAT13", true));
		if(DepositMenu.IM_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, DepositMenu.IM.getDescription(), "MAT14", true));		
	}
	
	/***** CLICK HANDLER *****/
	
    private ClickHandler menuClickHandler(DepositMenu depositMenu, Integer id, String name) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(id.equals(getId())) {
					updatePage(depositMenu);
				} else {
					getUndoStack().clear();
					getRedoStack().clear();
					setId(id);
					setName(name);
					getInma().getSchemaTextMode(getAonData(), getId(), new AsyncCallback<Map<String, String>>() {
						@Override
						public void onSuccess(Map<String, String> result) {
							setDeposit(result);
							updateHeader();
							updatePage(depositMenu);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		};
	}
	
	private ClickHandler submenuClickHandler(PaperItem item, VerticalPanel content) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				content.setVisible(!content.isVisible());
				IronIcon ironIcon = (IronIcon) item.getWidget(0);
				ironIcon.setIcon(content.isVisible() ? "arrow-drop-down" : "arrow-drop-up");
			}
		};
	}

	private ClickHandler undoClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(!getUndoStack().isEmpty()) {
					Map<String, String> m = new HashMap<String, String>();
					for (String k : getDeposit().keySet()) {
						m.put(k, getDeposit().get(k));
					}
					getRedoStack().push(m);
					setDeposit(getUndoStack().pop());
					inma.saveDepositTextMode(getAonData(), getDeposit(), getId(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							refreshPage();
						}
						
						@Override public void onFailure(Throwable caught) {}
					});	
				}
			}
		};
	}
	
	private ClickHandler redoClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(!getRedoStack().isEmpty()) {
					Map<String, String> m = new HashMap<String, String>();
					for (String k : getDeposit().keySet()) {
						m.put(k, getDeposit().get(k));
					}
					getUndoStack().push(m);
					setDeposit(getRedoStack().pop());
					inma.saveDepositTextMode(getAonData(), getDeposit(), getId(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							refreshPage();
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		};
	}
	
	private ClickHandler createClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PaperInput name = new PaperInput();
				name.setTitle("Nombre");
				name.setLabel("Nombre");
				AonDialog dialog = new AonDialog("Crear Plantilla", name) {
					
					@Override 
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						getInma().createSchemaTextMode(getAonData(), name.getValue(), new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								setId(result);
								setName(name.getValue());
								getInma().getSchemaTextMode(getAonData(), getId(), new AsyncCallback<Map<String, String>>() {
									@Override
									public void onSuccess(Map<String, String> result) {
										setDeposit(result);
										updateHeader();
										updatePage(DepositMenu.AE);

										PaperItem ej = buildItem(getName(), "arrow-drop-down", true);
										VerticalPanel ejContent = buildSubEjercicio(true, getId(), getName());
										ej.addClickHandler(submenuClickHandler(ej, ejContent));
										VerticalPanel vp = (VerticalPanel) getWestContent().getWidget();
										vp.add(ej);
										vp.add(ejContent);
									}
									
									@Override public void onFailure(Throwable caught) {}
								});
	
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
			}
		};
	}
	
	private ClickHandler deleteClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				AonDialog dialog = new AonDialog("Borrar Plantilla", new Label("Estas seguro de borrar la plantilla.")) {
					
					@Override 
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						getInma().deleteSchemaTextMode(getAonData(), getId(), new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								getInma().getTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
									
									@Override
									public void onSuccess(Vector<MemoryTemplate> result) {
										if(result.size()> 0) {
											setId(result.get(0).getId());
											setName(result.get(0).getName());
										}
										updateHeader();
										updatePage(DepositMenu.AE);
										westContent(result);
									}
								
									@Override public void onFailure(Throwable caught) {}
								});
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
			}
		};
	}
	
	
	/***** GETTERS & SETTERS *****/
	
	public INormalizedMemoryAsync getInma() {
		return inma;
	}
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public void setAonData(AonData aonData) {
		this.aonData = aonData;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Map<String, String> getDeposit() {
		return deposit;
	}
	
	public void setDeposit(Map<String, String> deposit) {
		this.deposit = deposit;
	}
	
	public DockLayoutPanel getD2Content() {
		return d2Content;
	}

	public void setD2Content(DockLayoutPanel d2Content) {
		this.d2Content = d2Content;
	}
	
	public FlexTable getHeader() {
		return header;
	}
	
	public void setHeader(FlexTable header) {
		this.header = header;
	}
	
	public ScrollPanel getPage() {
		return page;
	}
	
	public void setPage(ScrollPanel page) {
		this.page = page;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public DepositMenu getDepositMenu() {
		return depositMenu;
	}
	
	public void setDepositMenu(DepositMenu depositMenu) {
		this.depositMenu = depositMenu;
	}
	
	public Stack<Map<String, String>> getUndoStack() {
		return undoStack;
	}
	
	public void setUndoStack(Stack<Map<String, String>> undoStack) {
		this.undoStack = undoStack;
	}
	
	public Stack<Map<String, String>> getRedoStack() {
		return redoStack;
	}
	
	public void setRedoStack(Stack<Map<String, String>> redoStack) {
		this.redoStack = redoStack;
	}
}
