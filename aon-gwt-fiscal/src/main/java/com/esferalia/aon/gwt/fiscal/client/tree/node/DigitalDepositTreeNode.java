package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.Header1;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.NormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageF1;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageF2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageF3;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageH2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageH3;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageH4;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageH5;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM10;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM11_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM12_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM13_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM14_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM15;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM3_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM5_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM6_2;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.PageM7_2;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class DigitalDepositTreeNode extends TreeNode<Enterprise> {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}


	@Override
	public void select(final FiscalTree fiscalTree) {
		
		inma.isDigitalDeposit(enterpriseAux.getDomain(), new AsyncCallback<Boolean>(){
			
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Boolean result) {
				
				digitalDepositMenu(result, fiscalTree);
			}
		});
	}
	
	public void digitalDepositMenu(Boolean result, FiscalTree fiscalTree){
		NormalizedMemory nm;
		if(result)
			nm = new NormalizedMemory(enterpriseAux,"main",this, fiscalTree);
		else nm = new NormalizedMemory(true, this, enterpriseAux);
		nm.setPagesPanel(fiscalTree.getGenericContent(this));
		fiscalTree.setContent(nm);
	}
	
	Enterprise enterpriseAux;
	@Override
	public TreeNode<Enterprise> render(final HasTreeItems parent,Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.digitalDeposit()); 
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
    	enterpriseAux = enterprise;

    	inma.isDigitalDeposit(enterprise.getDomain(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Boolean result) {		
				if(result)
					items();				
			}
		
    	});
    	
    	return this;
	}
	DigitalDepositTreeNode ddtn;
	public void items() {
		ddtn = this;
    	TreeNode<Enterprise> his = new TreeNode<Enterprise>() {
  
    		@Override
    		public void select(FiscalTree fiscalPanel) {
    			String page = (isPymes() == true) ? "IDP" : "IDA";
    			NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
    			nm.paintHeaderTable("Cuentas Anuales");
    			Header1 header1Page = new Header1(enterpriseAux, nm);
    			header1Page.dump(new D2DepositTreeObject(), page);
    			nm.setPagesPanel(header1Page);
    			fiscalPanel.setContent(nm);	
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
			public void select(FiscalTree fiscalPanel) {
				
				String page = (isPymes() == true) ? "BP" : "BA";
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
    			nm.paintHeaderTable("Cuentas Anuales");
    			//Header2 header2Page = new Header2(enterpriseAux, nm);
    			PageH2 ph2 = new PageH2(enterpriseAux, nm);
    			ph2.dump(new D2DepositTreeObject(), page);
    			nm.setPagesPanel(ph2);
    			fiscalPanel.setContent(nm);	
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
			public void select(FiscalTree fiscalPanel) {
				String page = (isPymes() == true) ? "PP" : "PA";
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
    			nm.paintHeaderTable("Cuentas Anuales");
    			//Header3 header3Page = new Header3(enterpriseAux, nm);
    			PageH3 ph3 = new PageH3(enterpriseAux, nm);
    			ph3.dump(new D2DepositTreeObject(), page);
    			nm.setPagesPanel(ph3);
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
		cpg.setText("Cuenta de P\u00e9rdidas y Ganancias");
		this.addItem(cpg);
		
		TreeNode<Enterprise> ecpn = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				String page = (isPymes() == true) ? "PNP" : "PNA";
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
    			nm.paintHeaderTable("Cuentas Anuales");
    			//Header4 header4Page = new Header4(enterpriseAux, nm);
    			PageH4 ph4 = new PageH4(enterpriseAux, nm);
    			ph4.dump(new D2DepositTreeObject(), page);
    			nm.setPagesPanel(ph4);
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
		ecpn.setText("Estado de Cambios en el Patrimonio Neto");
		this.addItem(ecpn);
		
		TreeNode<Enterprise> dm = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				String page = (isPymes() == true) ? "IMP" : "IMA";
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
    			nm.paintHeaderTable("Cuentas Anuales");
    			//Header5 header5Page = new Header5(enterpriseAux, nm);
    			PageH5 ph5 = new PageH5(enterpriseAux,nm);
    			ph5.dump(new D2DepositTreeObject(), page);
    			nm.setPagesPanel(ph5);
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
		dm.setText("Declaraci\u00F3n Medioambiental");
		this.addItem(dm);
		
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
		memory.setText("Memoria");
			
			TreeNode<Enterprise> ae = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT1" : "MAT1";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
	    			nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph1Page = new FreeText("Apartado 1: Actividad de la empresa", true, page, enterpriseAux, nm,false);
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
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT2" : "MAT2";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph2Page = new FreeText("Apartado 2: Bases de presentaci\u00F3n de las cuentas anuales", true, page, enterpriseAux, nm, false);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT3" : "MAT3";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph3_1Page = new FreeText("Apartado 3: Aplicaci\u00F3n de resultados", true, page, enterpriseAux, nm,false);
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
				
				TreeNode<Enterprise> cn3 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP3" : "MA3";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM3_2 p32 = new PageM3_2(enterpriseAux, nm);
						p32.dump(new D2DepositTreeObject(), page);
//						Paragraph3_2 p32 = new Paragraph3_2(enterpriseAux, nm);
						nm.setPagesPanel(p32);
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
				cn3.setText("Cuadros Normalizados");
				ar.addItem(cn3);
			memory.addItem(ar);
			
			TreeNode<Enterprise> nrv = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT4" : "MAT4";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph4Page = new FreeText("Apartado 4: Normas de registro y valoraci\u00F3n", true, page, enterpriseAux, nm, false);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT5" : "MAT5";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph5_1Page = new FreeText("Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias", true, page, enterpriseAux, nm,false);
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
				
				TreeNode<Enterprise> cn5 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP5" : "MA5";
		    			NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM5_2 p52 = new PageM5_2(enterpriseAux, nm);
						p52.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p52);
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
				cn5.setText("Cuadros Normalizados");
				imiii.addItem(cn5);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT6" : "MAT6";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph6_1Page = new FreeText("Apartado 6: Activos financieros", true, page, enterpriseAux, nm,false);
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
				
				TreeNode<Enterprise> cn6 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP6" : "MA6";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM6_2 p62 = new PageM6_2(enterpriseAux, nm);
						p62.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p62);
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
				cn6.setText("Cuadros Normalizados");
				af.addItem(cn6);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT7" : "MAT7";
						
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph7_1Page = new FreeText("Apartado 7: Pasivos financieros", true, page, enterpriseAux, nm, false);
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
				
				TreeNode<Enterprise> cn7 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP7" : "MA7";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM7_2 p72 = new PageM7_2(enterpriseAux, nm);
						p72.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p72);
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
				cn7.setText("Cuadros Normalizados");
				pf.addItem(cn7);
			memory.addItem(pf);
			
			
			TreeNode<Enterprise> fp = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT8" : "MAT8";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux,page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph8Page = new FreeText("Apartado 8: Fondos propios", true, page, enterpriseAux, nm, false);
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
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MPT9" : "MAT9";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					FreeText paragraph9Page = new FreeText("Apartado 9: Situaci\u00F3n fiscal", true, page, enterpriseAux, nm, false);
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
			
			TreeNode<Enterprise> ig = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MP10" : "MA10";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux,page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					PageM10 p10 = new PageM10(enterpriseAux, nm);
					p10.dump(new D2DepositTreeObject(), page);
					nm.setPagesPanel(p10);
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
			ig.setText("Apartado 10:<<Ingresos y Gastos>>");
			memory.addItem(ig);
			
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT11" : "MAT11";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph11_1Page = new FreeText("Apartado 11: Subvenciones, donaciones y legados", true, page, enterpriseAux, nm, false);
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
				
				TreeNode<Enterprise> cn11 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP11" : "MA11";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM11_2 p112 = new PageM11_2(enterpriseAux, nm);
						p112.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p112);
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
				cn11.setText("Cuadros Normalizados");
				sdl.addItem(cn11);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT12" : "MAT12";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph12_1Page = new FreeText("Apartado 12: Operaciones con partes vinculadas", true, page, enterpriseAux, nm, false);
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
				
				TreeNode<Enterprise> cn12 = new TreeNode<Enterprise>() {
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP12" : "MA12";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM12_2 p122 = new PageM12_2(enterpriseAux, nm);
						p122.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p122);
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
				cn12.setText("Cuadros Normalizados");
				opv.addItem(cn12);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT13" : "MAT13";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph13_1Page = new FreeText("Apartado 13: Otra informaci\u00F3n", true, page, enterpriseAux, nm, false);
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
				
				TreeNode<Enterprise> cn13 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP13" : "MA13";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM13_2 p132 = new PageM13_2(enterpriseAux, nm);
						p132.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p132);
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
				cn13.setText("Cuadros Normalizados");
				oi.addItem(cn13);
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
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MPT14" : "MAT14";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						FreeText paragraph14_1Page = new FreeText("Apartado 14: Informaci\u00F3n sobre medio ambiente", true, page, enterpriseAux, nm, false);
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
				
				TreeNode<Enterprise> cn14 = new TreeNode<Enterprise>() {
					
					@Override
					public void select(FiscalTree fiscalPanel) {
						String page = (isPymes() == true) ? "MP14" : "MA14";
						NormalizedMemory nm = new NormalizedMemory(enterpriseAux, page, ddtn,  fiscalPanel);
						nm.paintHeaderTable("Memoria Normalizada");
						PageM14_2 p142 = new PageM14_2(enterpriseAux, nm);
						p142.dump(new D2DepositTreeObject(), page);
						nm.setPagesPanel(p142);
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
				cn14.setText("Cuadros Normalizados");
				ima.addItem(cn14);
			memory.addItem(ima);
			
			TreeNode<Enterprise> iapep = new TreeNode<Enterprise>() {
				
				@Override
				public void select(FiscalTree fiscalPanel) {
					String page = (isPymes() == true) ? "MP15" : "MA15";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux,page, ddtn,  fiscalPanel);
					nm.paintHeaderTable("Memoria Normalizada");
					PageM15 p15 = new PageM15(enterpriseAux, nm);
					p15.dump(new D2DepositTreeObject(), page);
					nm.setPagesPanel(p15);
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
			iapep.setText("Apartado 15:<<Informaci\u00F3n sobre los Aplazamientos de Pago Efectuados a Proveedores>>");
			memory.addItem(iapep);
		this.addItem(memory);
		
		TreeNode<Enterprise> ma = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux, "A", ddtn,  fiscalPanel);
				nm.paintHeaderTable("Cuentas Anuales");
				PageF1 pf1 = new PageF1(enterpriseAux, nm);
				pf1.dump(new D2DepositTreeObject(), "A");
				nm.setPagesPanel(pf1);
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
		ma.setText("Modelo de Autocartera");
		this.addItem(ma);
		
		TreeNode<Enterprise> ip = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux,"PR", ddtn,  fiscalPanel);
				nm.paintHeaderTable("Cuentas Anuales");
				PageF2 pf2 = new PageF2(enterpriseAux, nm);
				pf2.dump(new D2DepositTreeObject(), "PR");
				nm.setPagesPanel(pf2);
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
		ip.setText("Instancia de Presentaci\u00F3n");
		this.addItem(ip);
		
		TreeNode<Enterprise> chd = new TreeNode<Enterprise>() {
			
			@Override
			public void select(FiscalTree fiscalPanel) {
				NormalizedMemory nm = new NormalizedMemory(enterpriseAux,"H", ddtn,  fiscalPanel);
				nm.paintHeaderTable("Cuentas Anuales");
				PageF3 pf3 = new PageF3(enterpriseAux, nm);
				pf3.dump(new D2DepositTreeObject(), "H");
				nm.setPagesPanel(pf3);
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
		chd.setText("Certificaci\u00F3n de la Huella Digital");
		this.addItem(chd);
	}
	
	private static boolean isPymes() {
		return true;
	}
	
}
