package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.Header1;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.MemoryDocuments;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.NormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1A;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1B;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1C;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1D;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1E;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1F;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1G;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF1H;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageF3;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageH2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageH3;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageH4;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageH5;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM10;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM11_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM12_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM13_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM14_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM15;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM3_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM5_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM6_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM7_2;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class DigitalDepositTreeNode extends TreeNode<D2DepositTreeObject> {
	
	private NormalizedMemory normalizedMemory;
	private D2DepositTreeObject d2Deposit2014;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	@Override
	public D2DepositTreeObject getTreeObject() {
		return (D2DepositTreeObject) getUserObject();
	}


	@Override
	public void select(final Deposit deposit) {
		
		inma.isDigitalDeposit(d2Deposit2014.getDomain(), d2Deposit2014.getYear(), new AsyncCallback<Boolean>(){
			
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Boolean isD2) {

				digitalDepositMenu(isD2, deposit);
			}
		});
	}
	
	public void digitalDepositMenu(Boolean result, final Deposit deposit){
		
		if(result){
			//if(getState()) setState(false); else setState(true);
			if(this.getChildCount() <= 0){
				final DigitalDepositTreeNode ddtn = this;
				inma.getSchema(d2Deposit2014.getEnterprise().getDocument(), d2Deposit2014.getDomain(), false, d2Deposit2014.getYear(), new AsyncCallback<Map<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(Map<String, String> result) {
						d2Deposit2014.setMap(result);
						d2Deposit2014.setMapDraft(result);
						
						inma.getMemoryFiles(d2Deposit2014.getDomain(), new AsyncCallback<Vector<MemoryFiles>>() {
							
							@Override
							public void onSuccess(Vector<MemoryFiles> result) {
								isMemory = result.get(0).getBool();
								isMa = result.get(1).getBool();
								
								items();			
								setState(true);
								normalizedMemory = new NormalizedMemory(ddtn, deposit);
								normalizedMemory.paintHeaderTable("Cuentas Anuales", d2Deposit2014.getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
								normalizedMemory.setPagesPanel(deposit.getGenericContent(ddtn), true);
								deposit.setContent(normalizedMemory);							}
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				});
			}
			else normalizedMemory.setPagesPanel(deposit.getGenericContent(this),true);
		}
		else{
			normalizedMemory = new NormalizedMemory(true, this, deposit);
			normalizedMemory.paintHeaderTable("Cuentas Anuales","Tipo", d2Deposit2014.getYear().toString());
			normalizedMemory.setPagesPanel(deposit.getGenericContent(this), true);
		}
		deposit.setContent(normalizedMemory);
	}
	
	public void newFromDeposit(final Deposit deposit, D2DepositTreeObject d2, Map<String, String> map){
		isMemory = false;
		isMa = false;
		d2Deposit2014 = d2;
		d2Deposit2014.setMap(map);
		d2Deposit2014.setMapDraft(map);
		normalizedMemory = new NormalizedMemory(this, deposit);
		normalizedMemory.getNewButton().setVisible(false);
		normalizedMemory.getSaveButton().setVisible(true);
		normalizedMemory.getSaveButton().setEnabled(false);
		normalizedMemory.getDeleteButton().setVisible(true);
		normalizedMemory.getGenerateFileButton().setVisible(true);		
		normalizedMemory.getImportAllButton().setVisible(true);
		normalizedMemory.paintHeaderTable("Cuentas Anuales", d2Deposit2014.getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
		normalizedMemory.setPagesPanel(deposit.getGenericContent(this), true);

		deposit.setContent(normalizedMemory);


		
		//normalizedMemory.setPagesPanel(Deposit.getGenericContent(this));
		items();
		setState(true);
		
	}
	
	@Override
	public TreeNode<D2DepositTreeObject> render(final HasTreeItems parent,D2DepositTreeObject d2DepositTreeObject) {
		InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.digitalDeposit()); 
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(d2DepositTreeObject.getEnterprise());
    	parent.addItem(this);
    	d2Deposit2014 = d2DepositTreeObject;
    	/*inma.isDigitalDeposit(d2DepositTreeObject.getDomain(), d2Deposit2014.getYear(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Boolean result) {	
				if(result){
					inma.getSchema(d2Deposit2014.getEnterprise().getDocument(), d2Deposit2014.getDomain(), false, d2Deposit2014.getYear(), new AsyncCallback<Map<String, String>>() {

						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Map<String, String> result) {
							d2Deposit2014.setMap(result);
							d2Deposit2014.setMapDraft(result);
							
							inma.getMemoryFiles(d2Deposit2014.getDomain(), new AsyncCallback<Vector<MemoryFiles>>() {
								
								@Override
								public void onSuccess(Vector<MemoryFiles> result) {
									isMemory = result.get(0).getBool();
									isMa = result.get(1).getBool();
								}
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
					});
					
				}
			}
		
    	});*/
    	
    	return this;
	}
	
	Boolean isMemory;
	Boolean isMa;
	
	
	
	public Boolean getIsMemory() {
		return isMemory;
	}


	public void setIsMemory(Boolean isMemory) {
		this.isMemory = isMemory;
	}


	public Boolean getIsMa() {
		return isMa;
	}


	public void setIsMa(Boolean isMa) {
		this.isMa = isMa;
	}

	DigitalDepositTreeNode ddtn;
	TreeNode<Enterprise> memory = null;
	TreeNode<Enterprise> ma = null;
	
	
	
	public TreeNode<Enterprise> getMemory() {
		return memory;
	}


	public void setMemory(TreeNode<Enterprise> memory) {
		this.memory = memory;
	}


	public TreeNode<Enterprise> getMa() {
		return ma;
	}


	public void setMa(TreeNode<Enterprise> ma) {
		this.ma = ma;
	}


	public void items() {
		ddtn = this;
    	TreeNode<Enterprise> his = new TreeNode<Enterprise>() {
  
    		@Override
    		public void select(Deposit fiscalPanel) {
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
    			Header1 header1Page = new Header1(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
    			normalizedMemory.setPagesPanel(header1Page);
    			fiscalPanel.setContent(normalizedMemory);	
    		}

			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}

			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				setTreeObject(t);
				return this;
			}
		};
    	his.setText("Hoja Identificativa de la Sociedad");
    	this.addItem(his);

 
    	
    	TreeNode<Enterprise> bs = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {	
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
    			PageH2 ph2 = new PageH2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
    			normalizedMemory.setPagesPanel(ph2);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				setTreeObject(t);
				return this;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		};
    	bs.setText("Balance de Situaci\u00F3n");
    	this.addItem(bs);
    	
    	TreeNode<Enterprise> cpg = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
    			PageH3 ph3 = new PageH3(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
    			normalizedMemory.setPagesPanel(ph3);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		};
		cpg.setText("Cuenta de P\u00e9rdidas y Ganancias");
		this.addItem(cpg);
		
		if(d2Deposit2014.getYear() < 2016){
			TreeNode<Enterprise> ecpn = new TreeNode<Enterprise>() {
			
				@Override
				public void select(Deposit fiscalPanel) {
    				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
    				PageH4 ph4 = new PageH4(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
    				normalizedMemory.setPagesPanel(ph4);
    				fiscalPanel.setContent(normalizedMemory);
				}
			
				@Override
				public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
					return null;
				}
			
				@Override
				public Enterprise getTreeObject() {
					return (Enterprise) getUserObject();
				}
			};
			ecpn.setText("Estado de Cambios en el Patrimonio Neto");
			this.addItem(ecpn);
		}
		
		TreeNode<Enterprise> dm = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
    			PageH5 ph5 = new PageH5(d2Deposit2014.getEnterprise(),normalizedMemory);
    			normalizedMemory.setPagesPanel(ph5);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		};
		dm.setText("Declaraci\u00F3n Medioambiental");
		this.addItem(dm);
		itemsMemory();
		itemsMa();

		TreeNode<Enterprise> documents = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit deposit) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				MemoryDocuments md = new MemoryDocuments(d2Deposit2014.getEnterprise(), normalizedMemory, memory, ma, d2Deposit2014.getYear());

				normalizedMemory.setPagesPanel(md);
    			deposit.setContent(normalizedMemory);
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		};
		documents.setText("Documentos");
		this.addItem(documents);
		if(!isMa) this.addItem(ma);

		TreeNode<Enterprise> ip = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF2 pf2 = new PageF2(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf2);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		};
		ip.setText("Instancia de Presentaci\u00F3n");
		this.addItem(ip);
		
		TreeNode<Enterprise> chd = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF3 pf3 = new PageF3(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf3);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		chd.setText("Certificaci\u00F3n de la Huella Digital");
		this.addItem(chd);
	}

	public D2DepositTreeObject getD2Deposit2014() {
		return d2Deposit2014;
	}
	
	public void setD2Deposit2014(D2DepositTreeObject d2Deposit2014) {
		this.d2Deposit2014 = d2Deposit2014;
	}
	
	
	
	public void itemsMemory() {
		if(!isMemory){
			memory = new TreeNode<Enterprise>() {
				
				@Override
				public void select(Deposit fiscalPanel) {
					//if(getState()) setState(false); else setState(true);
				}
				
				@Override
				public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
					return null;
				}
				
				@Override
				public Enterprise getTreeObject() {
					return (Enterprise) getUserObject();
				}
			};
			memory.setText("Memoria");
				
				TreeNode<Enterprise> ae = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						String page =  "MAT1";
		    			normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						FreeText paragraph1Page = new FreeText("Apartado 1: Actividad de la empresa", true, page, d2Deposit2014.getEnterprise(), normalizedMemory,false);
						normalizedMemory.setPagesPanel(paragraph1Page);
		    			fiscalPanel.setContent(normalizedMemory);	
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				ae.setText("Apartado 1:<<Actividad de la Empresa>>");
				memory.addItem(ae);
				
				TreeNode<Enterprise> bpca = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						String page = "MAT2";
						normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						FreeText paragraph2Page = new FreeText("Apartado 2: Bases de presentaci\u00F3n de las cuentas anuales", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
						normalizedMemory.setPagesPanel(paragraph2Page);
		    			fiscalPanel.setContent(normalizedMemory);	
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				bpca.setText("Apartado 2:<<Bases de Presentaci\u00F3n de las Cuentas Anuales>>");
				memory.addItem(bpca);
				
				
				TreeNode<Enterprise> ar = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				ar.setText("Apartado 3:<<Aplicaci\u00F3n de Resultados>>");
				
					
					TreeNode<Enterprise> tl3 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page = "MAT3";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph3_1Page = new FreeText("Apartado 3: Aplicaci\u00F3n de resultados", true, page, d2Deposit2014.getEnterprise(), normalizedMemory,false);
							normalizedMemory.setPagesPanel(paragraph3_1Page);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl3.setText("Texto Libre");
					ar.addItem(tl3);
					
					TreeNode<Enterprise> cn3 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM3_2 p32 = new PageM3_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p32);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn3.setText("Cuadros Normalizados");
					ar.addItem(cn3);
				memory.addItem(ar);
				
				TreeNode<Enterprise> nrv = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						String page =  "MAT4";
						normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						FreeText paragraph4Page = new FreeText("Apartado 4: Normas de registro y valoraci\u00F3n", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
						normalizedMemory.setPagesPanel(paragraph4Page);
		    			fiscalPanel.setContent(normalizedMemory);	
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				nrv.setText("Apartado 4:<<Normas de Registro y Valoraci\u00F3n>>");
				memory.addItem(nrv);
				
				TreeNode<Enterprise> imiii = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				imiii.setText("Apartado 5:<<Inmovilizado Material, Intangible e Inversiones Inmobiliarias>>");
				
					
					TreeNode<Enterprise> tl5 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page =  "MAT5";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph5_1Page = new FreeText("Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias", true, page, d2Deposit2014.getEnterprise(), normalizedMemory,false);
							normalizedMemory.setPagesPanel(paragraph5_1Page);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl5.setText("Texto Libre");
					imiii.addItem(tl5);
					
					TreeNode<Enterprise> cn5 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM5_2 p52 = new PageM5_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p52);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn5.setText("Cuadros Normalizados");
					imiii.addItem(cn5);
				memory.addItem(imiii);
				
				
				TreeNode<Enterprise> af = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				af.setText("Apartado 6:<<Activos Financieros");
				
					
					TreeNode<Enterprise> tl6 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page = "MAT6";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph6_1Page = new FreeText("Apartado 6: Activos financieros", true, page, d2Deposit2014.getEnterprise(), normalizedMemory,false);
							normalizedMemory.setPagesPanel(paragraph6_1Page);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl6.setText("Texto Libre");
					af.addItem(tl6);
					
					TreeNode<Enterprise> cn6 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {	
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM6_2 p62 = new PageM6_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p62);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn6.setText("Cuadros Normalizados");
					af.addItem(cn6);
				memory.addItem(af);
				
				TreeNode<Enterprise> pf = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				pf.setText("Apartado 7:<<Pasivos Financieros>>");
				
					
					TreeNode<Enterprise> tl7 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page = "MAT7";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph7_1Page = new FreeText("Apartado 7: Pasivos financieros", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
							normalizedMemory.setPagesPanel(paragraph7_1Page);
			    			fiscalPanel.setContent(normalizedMemory);	
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl7.setText("Texto Libre");
					pf.addItem(tl7);
					
					TreeNode<Enterprise> cn7 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit deposit) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM7_2 p72 = new PageM7_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p72);
							deposit.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn7.setText("Cuadros Normalizados");
					pf.addItem(cn7);
				memory.addItem(pf);
				
				
				TreeNode<Enterprise> fp = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						String page =  "MAT8";
						FreeText paragraph8Page = new FreeText("Apartado 8: Fondos propios", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
						normalizedMemory.setPagesPanel(paragraph8Page);
		    			fiscalPanel.setContent(normalizedMemory);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				fp.setText("Apartado 8:<<Fondos Propios>>");
				memory.addItem(fp);
				
				TreeNode<Enterprise> sf = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						String page = "MAT9";
						normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						FreeText paragraph9Page = new FreeText("Apartado 9: Situaci\u00F3n fiscal", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
						normalizedMemory.setPagesPanel(paragraph9Page);
		    			fiscalPanel.setContent(normalizedMemory);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				sf.setText("Apartado 9:<<Situaci\u00F3n Fiscal>>");
				memory.addItem(sf);
				
				TreeNode<Enterprise> ig = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						PageM10 p10 = new PageM10(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
						normalizedMemory.setPagesPanel(p10);
		    			fiscalPanel.setContent(normalizedMemory);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				ig.setText("Apartado 10:<<Ingresos y Gastos>>");
				memory.addItem(ig);
				
				TreeNode<Enterprise> sdl = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				sdl.setText("Apartado 11:<<Subvenciones, Donaciones y Legados>>");
				
					
					TreeNode<Enterprise> tl11 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page =  "MAT11";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph11_1Page = new FreeText("Apartado 11: Subvenciones, donaciones y legados", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
							normalizedMemory.setPagesPanel(paragraph11_1Page);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl11.setText("Texto Libre");
					sdl.addItem(tl11);
					
					TreeNode<Enterprise> cn11 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM11_2 p112 = new PageM11_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p112);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn11.setText("Cuadros Normalizados");
					sdl.addItem(cn11);
				memory.addItem(sdl);
				
				TreeNode<Enterprise> opv = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				opv.setText("Apartado 12:<<Operaciones con Partes Vinculantes>>");
				
					
					TreeNode<Enterprise> tl12 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page = "MAT12";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph12_1Page = new FreeText("Apartado 12: Operaciones con partes vinculadas", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
							normalizedMemory.setPagesPanel(paragraph12_1Page);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl12.setText("Texto Libre");
					opv.addItem(tl12);
					
					TreeNode<Enterprise> cn12 = new TreeNode<Enterprise>() {
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM12_2 p122 = new PageM12_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p122);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn12.setText("Cuadros Normalizados");
					opv.addItem(cn12);
				memory.addItem(opv);
				
				TreeNode<Enterprise> oi = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				oi.setText("Apartado 13:<<Otra Informaci\u00F3n>>");
				
					
					TreeNode<Enterprise> tl13 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page =  "MAT13";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph13_1Page = new FreeText("Apartado 13: Otra informaci\u00F3n", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
							normalizedMemory.setPagesPanel(paragraph13_1Page);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl13.setText("Texto Libre");
					oi.addItem(tl13);
					
					TreeNode<Enterprise> cn13 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM13_2 p132 = new PageM13_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p132);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn13.setText("Cuadros Normalizados");
					oi.addItem(cn13);
				memory.addItem(oi);
				
				TreeNode<Enterprise> ima = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						//if(getState()) setState(false); else setState(true);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				ima.setText("Apartado 14:<<Informaci\u00F3n sobre el Medio Ambiente>>");
				
					
					TreeNode<Enterprise> tl14 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							String page = "MAT14";
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							FreeText paragraph14_1Page = new FreeText("Apartado 14: Informaci\u00F3n sobre medio ambiente", true, page, d2Deposit2014.getEnterprise(), normalizedMemory, false);
							normalizedMemory.setPagesPanel(paragraph14_1Page);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					tl14.setText("Texto Libre");
					ima.addItem(tl14);
					
					TreeNode<Enterprise> cn14 = new TreeNode<Enterprise>() {
						
						@Override
						public void select(Deposit fiscalPanel) {
							normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
							PageM14_2 p142 = new PageM14_2(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
							normalizedMemory.setPagesPanel(p142);
			    			fiscalPanel.setContent(normalizedMemory);
						}
						
						@Override
						public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
							return null;
						}
						
						@Override
						public Enterprise getTreeObject() {
							return (Enterprise) getUserObject();
						}
					};
					cn14.setText("Cuadros Normalizados");
					ima.addItem(cn14);
				memory.addItem(ima);
				
				TreeNode<Enterprise> iapep = new TreeNode<Enterprise>() {
					
					@Override
					public void select(Deposit fiscalPanel) {
						normalizedMemory.paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
						PageM15 p15 = new PageM15(d2Deposit2014.getEnterprise(), normalizedMemory, d2Deposit2014.getYear());
						normalizedMemory.setPagesPanel(p15);
		    			fiscalPanel.setContent(normalizedMemory);
					}
					
					@Override
					public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
						return null;
					}
					
					@Override
					public Enterprise getTreeObject() {
						return (Enterprise) getUserObject();
					}
				};
				iapep.setText("Apartado 15:<<Informaci\u00F3n sobre los Aplazamientos de Pago Efectuados a Proveedores>>");
				memory.addItem(iapep);
			this.addItem(memory);
			}
	}
	
	
	public void itemsMa() {
		if(!isMa){
		ma = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
			//	if(getState()) setState(false); else setState(true);
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1 pf1 = new PageF1(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma.setText("Modelo de Autocartera");
		if(d2Deposit2014.getMapDraft().get(D2DepositFooterKey.A18009050.getCode()) == null || d2Deposit2014.getMapDraft().get(D2DepositFooterKey.A18009050.getCode()).equals("0")){
		
		TreeNode<Enterprise> ma1 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1A pf1a = new PageF1A(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1a);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma1.setText(AON.MSG.autocarteraModelA1());
		ma.addItem(ma1);
		
		TreeNode<Enterprise> ma2 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1B pf1b = new PageF1B(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1b);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma2.setText(AON.MSG.autocarteraModelA11());
		ma.addItem(ma2);
		
		TreeNode<Enterprise> ma3 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1C pf1c = new PageF1C(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1c);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma3.setText(AON.MSG.autocarteraModelA2());
		ma.addItem(ma3);
		
		TreeNode<Enterprise> ma4 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1D pf1d = new PageF1D(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1d);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma4.setText(AON.MSG.autocarteraModelA3());
		ma.addItem(ma4);
		
		TreeNode<Enterprise> ma5 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1E pf1e = new PageF1E(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1e);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma5.setText(AON.MSG.autocarteraModelA4());
		ma.addItem(ma5);
		
		TreeNode<Enterprise> ma6 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1F pf1f = new PageF1F(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1f);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma6.setText(AON.MSG.autocarteraModelA5());
		ma.addItem(ma6);
		
		
		TreeNode<Enterprise> ma7 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1G pf1g = new PageF1G(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1g);
    			fiscalPanel.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma7.setText(AON.MSG.autocarteraModelA6());
		ma.addItem(ma7);
		
		TreeNode<Enterprise> ma8 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit deposit) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit2014().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit2014.getYear().toString());
				PageF1H pf1h = new PageF1H(d2Deposit2014.getEnterprise(), normalizedMemory);
				normalizedMemory.setPagesPanel(pf1h);
    			deposit.setContent(normalizedMemory);	
			}
			
			@Override
			public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {
				return null;
			}
			
			@Override
			public Enterprise getTreeObject() {
				return (Enterprise) getUserObject();
			}
		}; 
		ma8.setText(AON.MSG.autocarteraModelA7());
		
		ma.addItem(ma8);
		}
		}
	}
}
