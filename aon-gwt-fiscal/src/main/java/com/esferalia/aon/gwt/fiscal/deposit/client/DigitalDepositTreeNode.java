package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageH1;
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
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class DigitalDepositTreeNode extends TreeNode<D2Deposit> {
	
	private NormalizedMemory normalizedMemory;
	private D2Deposit d2Deposit;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	@Override
	public D2Deposit getTreeObject() {
		return (D2Deposit) getUserObject();
	}


	@Override
	public void select(final Deposit deposit) {
		inma.isDigitalDeposit(d2Deposit.getDomain().getId(), d2Deposit.getYear(), new AsyncCallback<Boolean>(){
			
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
			if(this.getChildCount() <= 0){
				final DigitalDepositTreeNode ddtn = this;
				inma.getSchema(d2Deposit.getEnterprise().getDocument(), d2Deposit.getDomain().getId(), false, d2Deposit.getYear(), new AsyncCallback<Map<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(Map<String, String> result) {
						d2Deposit.setMap(result);
						d2Deposit.setMapDraft(result);
						
						inma.getMemoryFiles(d2Deposit.getDomain().getId(), new AsyncCallback<Vector<MemoryFiles>>() {
							
							@Override
							public void onSuccess(Vector<MemoryFiles> result) {
								isMemory = result.get(0).getBool();
								isMa = result.get(1).getBool();
								items();			
								setState(true);
								normalizedMemory = new NormalizedMemory(ddtn, deposit);
								normalizedMemory.paintHeaderTable("Cuentas Anuales", d2Deposit.getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
								normalizedMemory.setPagesPanel(deposit.getGenericContent(ddtn), true);
								deposit.setContent(normalizedMemory);							
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				});
			}
			else normalizedMemory.setPagesPanel(deposit.getGenericContent(this),true);
		} else{
			normalizedMemory = new NormalizedMemory(true, this, deposit);
			normalizedMemory.paintHeaderTable("Cuentas Anuales","Tipo", d2Deposit.getYear().toString());
			normalizedMemory.setPagesPanel(deposit.getGenericContent(this), true);
		}
		deposit.setContent(normalizedMemory);
	}
	
	public void newFromDeposit(final Deposit deposit, D2Deposit d2, Map<String, String> map){
		isMemory = false;
		isMa = false;
		d2Deposit = d2;
		d2Deposit.setMap(map);
		d2Deposit.setMapDraft(map);
		normalizedMemory = new NormalizedMemory(this, deposit);
		normalizedMemory.getNewButton().setVisible(false);
		normalizedMemory.getSaveButton().setVisible(true);
		normalizedMemory.getSaveButton().setEnabled(false);
		normalizedMemory.getDeleteButton().setVisible(true);
		normalizedMemory.getGenerateFileButton().setVisible(true);		
		normalizedMemory.getImportAllButton().setVisible(true);
		normalizedMemory.paintHeaderTable("Cuentas Anuales", d2Deposit.getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
		normalizedMemory.setPagesPanel(deposit.getGenericContent(this), true);

		deposit.setContent(normalizedMemory);
		
		items();
		setState(true);
		
	}
	
	@Override
	public TreeNode<D2Deposit> render(final HasTreeItems parent,D2Deposit d2DepositTreeObject) {
		InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.digitalDeposit()); 
    	label.addStyleName("aon-icon-registradores");
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(d2DepositTreeObject.getEnterprise());
    	parent.addItem(this);
    	d2Deposit = d2DepositTreeObject;
    	
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
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    			PageH1 header1Page = new PageH1(d2Deposit.getEnterprise(), normalizedMemory);
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
    	
    	if(d2Deposit.getYear() >= 2016){
    		TreeNode<Enterprise> ar = new TreeNode<Enterprise>() {
    		  
    			@Override
    			public void select(Deposit fiscalPanel) {
    				normalizedMemory.paintHeaderTable("Memoria Normalizada", d2Deposit.getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    				PageM3_2 p32 = new PageM3_2(d2Deposit.getEnterprise(), normalizedMemory);
    				normalizedMemory.setPagesPanel(p32);
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
			ar.setText("Aplicaci\u00F3n de resultados");
    		this.addItem(ar);
    	}
 
    	TreeNode<Enterprise> bs = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {	
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    			PageH2 ph2 = new PageH2(d2Deposit.getEnterprise(), normalizedMemory);
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
    			normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    			PageH3 ph3 = new PageH3(d2Deposit.getEnterprise(), normalizedMemory);
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
		
		if(d2Deposit.getYear() < 2016){
			TreeNode<Enterprise> ecpn = new TreeNode<Enterprise>() {
			
				@Override
				public void select(Deposit fiscalPanel) {
    				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    				PageH4 ph4 = new PageH4(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
    			PageH5 ph5 = new PageH5(d2Deposit.getEnterprise(),normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				MemoryDocuments md = new MemoryDocuments(d2Deposit.getEnterprise(), normalizedMemory, memory, ma);

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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF2 pf2 = new PageF2(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF3 pf3 = new PageF3(d2Deposit.getEnterprise(), normalizedMemory);
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

	public D2Deposit getD2Deposit() {
		return d2Deposit;
	}
	
	public void setD2Deposit(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}
	
	public NormalizedMemory getNormalizedMemory() {
		return normalizedMemory;
	}
	
	public void setNormalizedMemory(NormalizedMemory normalizedMemory) {
		this.normalizedMemory = normalizedMemory;
	}
	
	public void itemsMemory() {
		if(!isMemory){
			memory = new TreeNode<Enterprise>() {
				@Override public void select(Deposit fiscalPanel) { }
				@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
				@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
			};
			memory.setText("Memoria");
			for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(getD2Deposit().getYear()); pos++){
				memory.addItem(getTreeNode(pos));
			}
			this.addItem(memory);
		}
	}

	public void itemsMa() {
		if(!isMa){
		ma = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
			//	if(getState()) setState(false); else setState(true);
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1 pf1 = new PageF1(d2Deposit.getEnterprise(), normalizedMemory);
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
		if(d2Deposit.getMapDraft().get(D2DepositFooterKey.A18009050.getCode()) == null || d2Deposit.getMapDraft().get(D2DepositFooterKey.A18009050.getCode()).equals("0")){
		
		TreeNode<Enterprise> ma1 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1A pf1a = new PageF1A(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1B pf1b = new PageF1B(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1C pf1c = new PageF1C(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1D pf1d = new PageF1D(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1E pf1e = new PageF1E(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1F pf1f = new PageF1F(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1G pf1g = new PageF1G(d2Deposit.getEnterprise(), normalizedMemory);
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
				normalizedMemory.paintHeaderTable("Cuentas Anuales", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), d2Deposit.getYear().toString());
				PageF1H pf1h = new PageF1H(d2Deposit.getEnterprise(), normalizedMemory);
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
	
	public TreeNode<Enterprise> getTreeNode(Integer pos){
		Integer year = getD2Deposit().getYear();
		switch (MemoryItem.getInstance().getApartadoName2(year, pos)) {
		case MemoryItem.ACTIVIDAD_EMPRESA:
			return getActividadEmpresa(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.BASES_PRESENTACION:
			return getBasesPresentacion(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.APLICACION_RESULTADOS:
			return getAplicacionResultado(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.NORMAS_REGISTRO:
			return getNormasRegistro(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.INMOVILIZADO:
			return getInmovilizado(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.ACTIVOS_FINANCIEROS:
			return getActivosFinancieros(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.PASIVOS_FINANCIEROS:
			return getPasivosFinancieros(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.FONDOS_PROPIOS:
			return getFondosPropios(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.SITUACION_FISCAL:
			return getSituacionFiscal(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.INGRESOS_GASTOS:
			return getIngresosGastos(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.SUBVENCIONES:
			return getSubvenciones(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.PARTES_VINCULANTES:
			return getPartesVinculantes(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.OTRA_INFORMACION:
			return getOtraInformacion(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.MEDIOAMBIENTE:
			return getMedioAmbiente(MemoryItem.getInstance().getApartadoName(year, pos));
		case MemoryItem.APLAZAMIENTOS:
			return getAplazamientos(MemoryItem.getInstance().getApartadoName(year, pos));
		default:
			return new TreeNode<Enterprise>() {
				@Override public void select(Deposit deposit) {}
				@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
				@Override public Enterprise getTreeObject() {return null;}
			}; 
		}
	}
	
	private TreeNode<Enterprise> getActividadEmpresa(String description){
		TreeNode<Enterprise> ae = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
    			ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph1Page = new FreeText(description, true, "MAT1", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(),false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph1Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		ae.setText(description);
		return ae;
	}
	
	private TreeNode<Enterprise> getBasesPresentacion(String description){
		TreeNode<Enterprise> bpca = new TreeNode<Enterprise>() {
		
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph2Page = new FreeText(description, true, "MAT2", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph2Page);
				fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
	
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		bpca.setText(description);
		return bpca;
	}
	
	public TreeNode<Enterprise> getAplicacionResultado(String description){
		TreeNode<Enterprise> ar = new TreeNode<Enterprise>() {

			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		ar.setText(description);
	
		TreeNode<Enterprise> tl3 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph3_1Page = new FreeText(description, true, "MAT3", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph3_1Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl3.setText("Texto Libre");
		ar.addItem(tl3);
		
		TreeNode<Enterprise> cn3 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM3_2 p32 = new PageM3_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p32);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}	
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn3.setText("Cuadros Normalizados");
		ar.addItem(cn3);
		return ar;
	}
	
	private TreeNode<Enterprise> getNormasRegistro(String description){
		TreeNode<Enterprise> nrv = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph4Page = new FreeText(description, true, "MAT4", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph4Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		nrv.setText(description);		
		return nrv;
	}
	
	private TreeNode<Enterprise> getInmovilizado(String description){
		TreeNode<Enterprise> imiii = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}	
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		
		imiii.setText(description);
		
		TreeNode<Enterprise> tl5 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph5_1Page = new FreeText(description, true, "MAT5", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(),false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph5_1Page);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl5.setText("Texto Libre");
		imiii.addItem(tl5);
			
		TreeNode<Enterprise> cn5 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM5_2 p52 = new PageM5_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p52);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn5.setText("Cuadros Normalizados");
		imiii.addItem(cn5);
		
		return imiii;
	}
	
	private TreeNode<Enterprise> getActivosFinancieros(String description){
		TreeNode<Enterprise> af = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		af.setText(description);
		TreeNode<Enterprise> tl6 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				String page = "MAT6";
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph6_1Page = new FreeText(description, true, page, ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(),false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph6_1Page);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {	return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl6.setText("Texto Libre");
		af.addItem(tl6);	
		TreeNode<Enterprise> cn6 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {	
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM6_2 p62 = new PageM6_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
				ddtn.getNormalizedMemory().setPagesPanel(p62);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}

			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn6.setText("Cuadros Normalizados");
		af.addItem(cn6);
		
		return af;
	}
	
	private TreeNode<Enterprise> getPasivosFinancieros(String description){
		TreeNode<Enterprise> pf = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		pf.setText(description);
		TreeNode<Enterprise> tl7 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				String page = "MAT7";
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph7_1Page = new FreeText(description, true, page, ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph7_1Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());	
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl7.setText("Texto Libre");
		pf.addItem(tl7);
			
		TreeNode<Enterprise> cn7 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit deposit) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM7_2 p72 = new PageM7_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p72);
				deposit.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn7.setText("Cuadros Normalizados");
		pf.addItem(cn7);
		
		return pf;
	}
	
	private TreeNode<Enterprise> getFondosPropios(String description){
		TreeNode<Enterprise> fp = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph8Page = new FreeText(description, true, "MAT8", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph8Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}	
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		fp.setText(description);
		
		return fp;
	}
	
	private TreeNode<Enterprise> getSituacionFiscal(String description){
		TreeNode<Enterprise> sf = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph9Page = new FreeText(description, true, "MAT9", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph9Page);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		sf.setText(description);
		
		return sf;
	}
	
	private TreeNode<Enterprise> getIngresosGastos(String description){
		TreeNode<Enterprise> ig = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM10 p10 = new PageM10(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p10);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		ig.setText(description);
		
		return ig;
	}
	
	private TreeNode<Enterprise> getSubvenciones(String description){
		TreeNode<Enterprise> sdl = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		sdl.setText(description);
		TreeNode<Enterprise> tl11 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph11_1Page = new FreeText(description, true, "MAT11", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph11_1Page);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl11.setText("Texto Libre");
		sdl.addItem(tl11);
			
		TreeNode<Enterprise> cn11 = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM11_2 p112 = new PageM11_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p112);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn11.setText("Cuadros Normalizados");
		sdl.addItem(cn11);
		return sdl;
	}
	
	private TreeNode<Enterprise> getPartesVinculantes(String description){
		TreeNode<Enterprise> opv = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}	
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		opv.setText(description);
		TreeNode<Enterprise> tl12 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph12_1Page = new FreeText(description, true, "MAT12", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph12_1Page);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl12.setText("Texto Libre");
		opv.addItem(tl12);
			
		TreeNode<Enterprise> cn12 = new TreeNode<Enterprise>() {
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM12_2 p122 = new PageM12_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p122);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn12.setText("Cuadros Normalizados");
		opv.addItem(cn12);
		
		return opv;
	}
	
	private TreeNode<Enterprise> getOtraInformacion(String description){
		TreeNode<Enterprise> oi = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		oi.setText(description);
		TreeNode<Enterprise> tl13 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph13_1Page = new FreeText(description, true, "MAT13", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph13_1Page);
				fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl13.setText("Texto Libre");
		oi.addItem(tl13);
			
		TreeNode<Enterprise> cn13 = new TreeNode<Enterprise>() {
		
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM13_2 p132 = new PageM13_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p132);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn13.setText("Cuadros Normalizados");
		oi.addItem(cn13);
		
		return oi;
	}

	private TreeNode<Enterprise> getMedioAmbiente(String description){
		TreeNode<Enterprise> ima = new TreeNode<Enterprise>() {
			@Override public void select(Deposit fiscalPanel) {}
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		ima.setText(description);
		TreeNode<Enterprise> tl14 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				FreeText paragraph14_1Page = new FreeText(description, true, "MAT14", ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), false);
				ddtn.getNormalizedMemory().setPagesPanel(paragraph14_1Page);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
				
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		tl14.setText("Texto Libre");
		ima.addItem(tl14);
			
		TreeNode<Enterprise> cn14 = new TreeNode<Enterprise>() {
				
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM14_2 p142 = new PageM14_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p142);
	    		fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		cn14.setText("Cuadros Normalizados");
		ima.addItem(cn14);
		
		return ima;
	}
	
	private TreeNode<Enterprise> getAplazamientos(String description){
		TreeNode<Enterprise> iapep = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM15 p15 = new PageM15(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory());
				ddtn.getNormalizedMemory().setPagesPanel(p15);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		iapep.setText(description);
		
		return iapep;
	}
}
