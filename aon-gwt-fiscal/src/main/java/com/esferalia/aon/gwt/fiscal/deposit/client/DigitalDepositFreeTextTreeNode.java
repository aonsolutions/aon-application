package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.NormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Domain;
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
	public void select(final Deposit deposit) {
		digitalDepositMenu(deposit);	
	}
	
	public void digitalDepositMenu(Deposit deposit){
		NormalizedMemory nm =  new NormalizedMemory(true, this, enterpriseAux);
		nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
		nm.setPagesPanel(deposit.getGenericContent(this));
		deposit.setContent(nm);
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

    	inma.getDigitalDepositTemplates(domainId, null, new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {		
				for (MemoryTemplate memoryTemplate : result) {
					memoryTemplate.getD2Deposit().setEnterprise(enterpriseAux);
					memoryTemplate.getD2Deposit().setDomain(new Domain().setId(enterprise.getDomain()));
					items(memoryTemplate);
				}						
			}
    	});
    	
    	return this;
	}
	
	

	
	MemoryTemplate memoryTemplate;
	DigitalDepositFreeTextTreeNode ddtn;
	public void items(MemoryTemplate mt) {
		memoryTemplate = mt;
		ddtn = this;
		TreeNode<Integer> memory = new TreeNode<Integer>() {
			
			@Override
			public void select(Deposit deposit) {
				NormalizedMemory nm =  new NormalizedMemory(true, ddtn, enterpriseAux);
				nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
				nm.setPagesPanel(deposit.getGenericContent(this));
				deposit.setContent(nm);
			}
			
			@Override
			public TreeNode<Integer> render(HasTreeItems parent, Integer t) {
				return null;
			}
			
			@Override
			public Integer getTreeObject() {
				return memoryTemplate.getId();
			}
		};
		memory.setTitle(mt.getId().toString());
		memory.setText("Memoria - " + mt.getName());
			
			TreeNode<Enterprise> ae = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT1"; 
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph1Page = new FreeText("Actividad de la empresa", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph1Page);
	    			deposit.setContent(nm);	
					
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
			ae.setText("Actividad de la Empresa");
			memory.addItem(ae);
			
			TreeNode<Enterprise> bpca = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT2";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph2Page = new FreeText("Bases de presentaci\u00F3n de las cuentas anuales", true, page, enterpriseAux, nm,true, mt.getId().toString());
					paragraph2Page.dumpFT(mt.getD2Deposit());
					nm.setPagesPanel(paragraph2Page);
					deposit.setContent(nm);
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
			bpca.setText("Bases de Presentaci\u00F3n de las Cuentas Anuales");
			memory.addItem(bpca);
			
			
			TreeNode<Enterprise> ar = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page =  "MAT3";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph3_1Page = new FreeText("Aplicaci\u00F3n de resultados", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph3_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph3_1Page);
	    			deposit.setContent(nm);	
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
			ar.setText("Aplicaci\u00F3n de Resultados");
			memory.addItem(ar);
			
			TreeNode<Enterprise> nrv = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT4";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada","Plantilla","20XX");
					FreeText paragraph4Page = new FreeText("Normas de registro y valoraci\u00F3n", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph4Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph4Page);
	    			deposit.setContent(nm);	
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
			nrv.setText("Normas de Registro y Valoraci\u00F3n");
			memory.addItem(nrv);
			
			TreeNode<Enterprise> imiii = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT5";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph5_1Page = new FreeText("Inmovilizado material, intangible, e inversiones inmobiliarias", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph5_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph5_1Page);
	    			deposit.setContent(nm);	
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
			imiii.setText("Inmovilizado Material, Intangible e Inversiones Inmobiliarias");		
			memory.addItem(imiii);
			
			
			TreeNode<Enterprise> af = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page = "MAT6";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph6_1Page = new FreeText("Activos financieros", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph6_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph6_1Page);
	    			deposit.setContent(nm);	
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
			af.setText("Activos Financieros");		
			memory.addItem(af);
			
			TreeNode<Enterprise> pf = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page = "MAT7";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph7_1Page = new FreeText("Pasivos financieros", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph7_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph7_1Page);
	    			deposit.setContent(nm);	
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
			pf.setText("Pasivos Financieros");
			memory.addItem(pf);
			
			
			TreeNode<Enterprise> fp = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT8";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph8Page = new FreeText("Fondos propios", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph8Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph8Page);
	    			deposit.setContent(nm);
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
			fp.setText("Fondos Propios");
			memory.addItem(fp);
			
			TreeNode<Enterprise> sf = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;
				@Override
				public void select(Deposit deposit) {
					String page = "MAT9";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux,  mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph9Page = new FreeText("Situaci\u00F3n fiscal", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph9Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph9Page);
	    			deposit.setContent(nm);
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
			sf.setText("Situaci\u00F3n Fiscal");
			memory.addItem(sf);
			
			
			
			TreeNode<Enterprise> sdl = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page = "MAT11";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph11_1Page = new FreeText("Subvenciones, donaciones y legados", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph11_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph11_1Page);
	    			deposit.setContent(nm);
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
			sdl.setText("Subvenciones, Donaciones y Legados");
			memory.addItem(sdl);
			
			TreeNode<Enterprise> opv = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page = "MAT12";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph12_1Page = new FreeText("Operaciones con partes vinculadas", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph12_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph12_1Page);
	    			deposit.setContent(nm);
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
			opv.setText("Operaciones con Partes Vinculantes");
			memory.addItem(opv);
			
			TreeNode<Enterprise> oi = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page =  "MAT13";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph13_1Page = new FreeText("Otra informaci\u00F3n", true, page, enterpriseAux, nm,true, mt.getId().toString());
	    			paragraph13_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph13_1Page);
	    			deposit.setContent(nm);
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
			oi.setText("Otra Informaci\u00F3n");
			memory.addItem(oi);
			
			TreeNode<Enterprise> ima = new TreeNode<Enterprise>() {
				MemoryTemplate mt = memoryTemplate;

				@Override
				public void select(Deposit deposit) {
					String page =  "MAT14";
					NormalizedMemory nm = new NormalizedMemory(enterpriseAux, mt, ddtn);
					nm.paintHeaderTable("Memoria Normalizada", "Plantilla","20XX");
					FreeText paragraph14_1Page = new FreeText("Informaci\u00F3n sobre medio ambiente", true, page, enterpriseAux, nm, true, mt.getId().toString());
	    			paragraph14_1Page.dumpFT(mt.getD2Deposit());
	    			nm.setPagesPanel(paragraph14_1Page);
	    			deposit.setContent(nm);
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
			ima.setText("Informaci\u00F3n sobre el Medio Ambiente");
			memory.addItem(ima);
		this.addItem(memory);
	}
}
