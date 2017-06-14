package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
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
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.PageM3_2;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
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
								deposit.setContent(normalizedMemory);							}
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
    			Header1 header1Page = new Header1(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
    				PageM3_2 p32 = new PageM3_2(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
    			PageH2 ph2 = new PageH2(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
    			PageH3 ph3 = new PageH3(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
    				PageH4 ph4 = new PageH4(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
    			PageH5 ph5 = new PageH5(d2Deposit.getEnterprise(),normalizedMemory, d2Deposit.getYear());
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
				MemoryDocuments md = new MemoryDocuments(d2Deposit.getEnterprise(), normalizedMemory, memory, ma, d2Deposit.getYear());

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
				PageF2 pf2 = new PageF2(d2Deposit.getEnterprise(), normalizedMemory, d2Deposit.getYear());
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
			MemoryItem mi = new MemoryItem();
			for(Integer pos = 0; pos < mi.getApartadosSize(getD2Deposit().getYear()); pos++){
				memory.addItem(mi.getTreeNode(this, pos));
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
}
