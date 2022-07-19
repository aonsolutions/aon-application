package net.aonsolutions.aon.gwt.ccaa.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.FreeText;
import net.aonsolutions.aon.gwt.ccaa.shared.DepositMenu;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryTemplate;



public class DepositTextMode extends DockLayoutPanel {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	DockLayoutPanel d2Content;
	DepositHeader header;
	ScrollPanel page;
	
	API API;
	AonData aonData;
	Company company;
	Map<String, String> deposit;
	Integer id;
	String name;
	DepositMenu depositMenu;
	DepositWest depositWest;
	
	List<MemoryTemplate> memoryTemplates;
	AonToolbar toolbarPanel;
	AonToolbarButton undoButton;
	AonToolbarButton redoButton;
	AonToolbarButton newButton;
	AonToolbarButton removeButton;
	
	Stack<Map<String, String>> undoStack = new Stack<Map<String, String>>();
	Stack<Map<String, String>> redoStack = new Stack<Map<String, String>>();
	
	DepositTextMode thiz = this;
	
	public DepositTextMode(AonData aonData) {
		super(Unit.PX);
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
		init();
		getInma().getTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
			
			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {
				setMemoryTemplates(result);
				if(!result.isEmpty()) {
					setId(result.get(0).getId());
					setName(result.get(0).getName());
				}
				startApplication();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void init() {
		setName("");
	}
	
	private void startApplication() {
		addNorth(header(), DepositHeader.HEIGTH);
		addNorth(toolbar(), AonToolbar.HEIGTH);
		addWest(menu(), DepositWest.WIDTH);
		add(content());
	}
	
	private Widget header() {
		setHeader(new DepositHeader("Plantillas"));
		return getHeader();
	}
	
	private Widget toolbar() {
		toolbarPanel = new AonToolbar(getName());
		
		undoButton = new AonToolbarButton("Deshacer", AON.CSS.aonIconUndo());
		undoButton.addClickHandler(undoClickHandler());
		toolbarPanel.add(undoButton);

		redoButton = new AonToolbarButton("Anular", AON.CSS.aonIconRedo());
		redoButton.addClickHandler(redoClickHandler());
		toolbarPanel.add(redoButton);
		
		newButton = new AonToolbarButton("Nuevo", AON.CSS.aonIconAdd());
		newButton.addClickHandler(createClickHandler());
		toolbarPanel.add(newButton);
		
		removeButton = new AonToolbarButton("Borrar", AON.CSS.aonIconDelete());
		removeButton.addClickHandler(deleteClickHandler());
		toolbarPanel.add(removeButton);
		
		return toolbarPanel;
	}
	
	private Widget menu() {
		depositWest = new DepositWest(thiz, getMemoryTemplates());
		return depositWest;
	}
	
	private void addMenuTemplate(MemoryTemplate template) {
		depositWest.addTemplate(template);
	}
	    
	private Widget content() {
		deposit();
		return getPage();
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
		toolbarPanel.setTitle(getName());
	}
	
	public void updatePage(DepositMenu depositMenu) {
		setDepositMenu(depositMenu);
		if(depositMenu == null) getPage().setWidget(new Label("No existe Ninguna plantilla"));
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
				TextBox name = new TextBox();
				name.setTitle("Nombre");
				AonDialog dialog = new AonDialog("Crear Plantilla", name);
				dialog.setAutoHideEnabled(true);
				dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept() {
						dialog.hide();
						getInma().createSchemaTextMode(getAonData(), name.getValue(), new AsyncCallback<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								MemoryTemplate template = new MemoryTemplate()
								.setId(result)
								.setName(name.getValue());
								memoryTemplates.add(template);
								setId(template.getId());
								setName(template.getName());
								getInma().getSchemaTextMode(getAonData(), getId(), new AsyncCallback<Map<String, String>>() {
									@Override
									public void onSuccess(Map<String, String> result) {
										setDeposit(result);
										updateHeader();
										updatePage(DepositMenu.AE);
										addMenuTemplate(template);
									}
									
									@Override public void onFailure(Throwable caught) {}
								});
	
							}
							
							@Override public void onFailure(Throwable caught) {}
						});	
					}
				});
			}
		};
	}
	
	private ClickHandler deleteClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonDialog dialog = new AonDialog("Borrar Plantilla", new Label("Estas seguro de borrar la plantilla " + getId() + " " + getName() + "."));
				dialog.setAutoHideEnabled(true);
				dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept() {
						dialog.hide();
						getInma().deleteSchemaTextMode(getAonData(), getId(), new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								getInma().getTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
									
									@Override
									public void onSuccess(Vector<MemoryTemplate> result) {
										setMemoryTemplates(result);
										if(!result.isEmpty()){
											setId(result.get(0).getId());
											setName(result.get(0).getName());
											getInma().getSchemaTextMode(getAonData(), getId(), new AsyncCallback<Map<String, String>>() {
												@Override
												public void onSuccess(Map<String, String> result) {
													setDeposit(result);
													updateHeader();
													updatePage(DepositMenu.AE);
													depositWest.reload(getMemoryTemplates());
												}
												
												@Override public void onFailure(Throwable caught) {}
											});
										} else {
											setId(null);
											setName("");
											setDeposit(new HashMap<>());
											updateHeader();
											updatePage(null);
											depositWest.reload(getMemoryTemplates());
										}
									}
								
									@Override public void onFailure(Throwable caught) {}
								});
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				});
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
	
	public DepositHeader getHeader() {
		return header;
	}
	
	public void setHeader(DepositHeader header) {
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
	
	public List<MemoryTemplate> getMemoryTemplates() {
		return memoryTemplates;
	}
	
	public void setMemoryTemplates(List<MemoryTemplate> memoryTemplates) {
		this.memoryTemplates = memoryTemplates;
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
