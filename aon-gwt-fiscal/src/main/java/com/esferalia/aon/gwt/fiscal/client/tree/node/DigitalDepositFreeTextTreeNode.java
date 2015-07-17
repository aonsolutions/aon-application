package com.esferalia.aon.gwt.fiscal.client.tree.node;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.NormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class DigitalDepositFreeTextTreeNode extends TreeNode<Integer> {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
	}


	@Override
	public void select(final FiscalTree fiscalTree) {
		/*inma.getDigitalDepositTemplates(enterpriseAux.getDomain(), new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {		
				for (MemoryTemplate memoryTemplate : result) {
					digitalDepositMenu(fiscalTree);
				}						
			}
		});*/
		digitalDepositMenu(fiscalTree);
		
	
	}
	
	public void digitalDepositMenu(FiscalTree fiscalTree){
		NormalizedMemory nm =  new NormalizedMemory(true, this, enterpriseAux);
		nm.setPagesPanel(fiscalTree.getGenericContent(this));
		fiscalTree.setContent(nm);
	}
	
	Enterprise enterpriseAux;
	@Override
	public TreeNode<Integer> render(final HasTreeItems parent,Integer domainId) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.digitalDepositFreeText()); 
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(domainId);
    	parent.addItem(this);
    	Enterprise enterprise = new Enterprise();
    	enterprise.setDomain(domainId);
    	enterpriseAux = enterprise;

    	inma.getDigitalDepositTemplates(domainId, new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {		
				for (MemoryTemplate memoryTemplate : result) {
					items(memoryTemplate);
				}
									
			}
		
    	});
    	
    	return this;
	}
	
	
	MemoryTemplate memoryTemplate;
	public void items(MemoryTemplate mt) {
		memoryTemplate = mt;
		TreeNode<Enterprise> memory = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				
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
		memory.setText("Memoria - " + mt.getName());
			
			TreeNode<Enterprise> ae = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT1" : "MAT1";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph1Page = new FreeText("Apartado 1: Actividad de la empresa", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph1Page.dump(new D2DepositTreeObject(), page);
	    			nm.setPagesPanel(paragraph1Page);
	    			fiscalPanel.setContent(nm);	
					
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
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT2" : "MAT2";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph2Page = new FreeText("Apartado 2: Bases de presentaci\u00F3n de las cuentas anuales", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph2Page.dump(new D2DepositTreeObject(), page);
	    			nm.setPagesPanel(paragraph2Page);
	    			fiscalPanel.setContent(nm);	
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
				public void select(FiscalTree fiscalPanel) {
					// APARTADO 3 CARPETA
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT3" : "MAT3";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph3_1Page = new FreeText("Apartado 3: Aplicaci\u00F3n de resultados", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph3_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph3_1Page);
		    			fiscalPanel.setContent(nm);	
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
			memory.addItem(ar);
			
			TreeNode<Enterprise> nrv = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT4" : "MAT4";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph4Page = new FreeText("Apartado 4: Normas de registro y valoraci\u00F3n", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph4Page.dump(new D2DepositTreeObject(), page);
	    			nm.setPagesPanel(paragraph4Page);
	    			fiscalPanel.setContent(nm);	
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
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT5" : "MAT5";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph5_1Page = new FreeText("Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph5_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph5_1Page);
		    			fiscalPanel.setContent(nm);	
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
				
			memory.addItem(imiii);
			
			
			TreeNode<Enterprise> af = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT6" : "MAT6";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph6_1Page = new FreeText("Apartado 6: Activos financieros", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph6_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph6_1Page);
		    			fiscalPanel.setContent(nm);	
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
				
			memory.addItem(af);
			
			TreeNode<Enterprise> pf = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT7" : "MAT7";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph7_1Page = new FreeText("Apartado 7: Pasivos financieros", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph7_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph7_1Page);
		    			fiscalPanel.setContent(nm);	
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
				
				
			memory.addItem(pf);
			
			
			TreeNode<Enterprise> fp = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT8" : "MAT8";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph8Page = new FreeText("Apartado 8: Fondos propios", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph8Page.dump(new D2DepositTreeObject(), page);
	    			nm.setPagesPanel(paragraph8Page);
	    			fiscalPanel.setContent(nm);
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
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT9" : "MAT9";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux,  mt);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph9Page = new FreeText("Apartado 9: Situaci\u00F3n fiscal", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph9Page.dump(new D2DepositTreeObject(), page);
	    			nm.setPagesPanel(paragraph9Page);
	    			fiscalPanel.setContent(nm);
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
			
			
			
			TreeNode<Enterprise> sdl = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT11" : "MAT11";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph11_1Page = new FreeText("Apartado 11: Subvenciones, donaciones y legados", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph11_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph11_1Page);
		    			fiscalPanel.setContent(nm);
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
				
				
			memory.addItem(sdl);
			
			TreeNode<Enterprise> opv = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT12" : "MAT12";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph12_1Page = new FreeText("Apartado 12: Operaciones con partes vinculadas", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph12_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph12_1Page);
		    			fiscalPanel.setContent(nm);
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
				
				
			memory.addItem(opv);
			
			TreeNode<Enterprise> oi = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT13" : "MAT13";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph13_1Page = new FreeText("Apartado 13: Otra informaci\u00F3n", true, page, enterpriseAux, nm,true, mt.getId().toString());
		    			paragraph13_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph13_1Page);
		    			fiscalPanel.setContent(nm);
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
				
			memory.addItem(oi);
			
			TreeNode<Enterprise> ima = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					
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
					MemoryTemplate mt = memoryTemplate;
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT14" : "MAT14";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph14_1Page = new FreeText("Apartado 14: Informaci\u00F3n sobre medio ambiente", true, page, enterpriseAux, nm, true, mt.getId().toString());
		    			paragraph14_1Page.dump(new D2DepositTreeObject(), page);
		    			nm.setPagesPanel(paragraph14_1Page);
		    			fiscalPanel.setContent(nm);
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
				
			memory.addItem(ima);
			
		this.addItem(memory);
	}
	
	private static boolean isPymes() {
		return true;
	}
	
}
