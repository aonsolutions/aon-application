package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.HashMap;

import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.FreeText;
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
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.google.gwt.user.client.ui.HasTreeItems;

public class MemoryItem {
	
	public static MemoryItem getInstance() {
		return new MemoryItem();
	}
	
	private static final String  ACTIVIDAD_EMPRESA = "Actividad de la empresa";
	private static final String  BASES_PRESENTACION = "Bases de Presentaci\u00F3n de las Cuentas Anuales";
	private static final String  APLICACION_RESULTADOS = "Aplicaci\u00F3n de Resultados";
	private static final String  NORMAS_REGISTRO = "Normas de Registro y Valoraci\u00F3n";
	private static final String  INMOVILIZADO = "Inmovilizado Material, Intangible e Inversiones Inmobiliarias";
	private static final String  ACTIVOS_FINANCIEROS = "Activos Financieros";
	private static final String  PASIVOS_FINANCIEROS = "Pasivos Financieros";
	private static final String  FONDOS_PROPIOS = "Fondos Propios";
	private static final String  SITUACION_FISCAL = "Situaci\u00F3n Fiscal";
	private static final String  INGRESOS_GASTOS = "Ingresos y Gastos";
	private static final String  SUBVENCIONES = "Subvenciones, Donaciones y Legados";
	private static final String  PARTES_VINCULANTES = "Operaciones con Partes Vinculantes";
	private static final String  OTRA_INFORMACION = "Otra Informaci\u00F3n";
	private static final String  MEDIOAMBIENTE = "Informaci\u00F3n sobre el Medio Ambiente";
	private static final String  APLAZAMIENTOS = "Informaci\u00F3n sobre los Aplazamientos de Pago Efectuados a Proveedores";

	@SuppressWarnings("serial")
	private static final HashMap<Integer, Integer> apartados = new HashMap<Integer, Integer>() {{
	    put(2014, 15);
	    put(2015, 15);
	    put(2016, 10);
	}};
	
	/**
	 * Description de los apartados en los ejercios 2014 & 2015
	 */
	private static final String[] apartadosName2014 = new String[]{
    		ACTIVIDAD_EMPRESA,
    		BASES_PRESENTACION,
    		APLICACION_RESULTADOS,
    		NORMAS_REGISTRO,
    		INMOVILIZADO,
    		ACTIVOS_FINANCIEROS,
    		PASIVOS_FINANCIEROS,
    		FONDOS_PROPIOS,
    		SITUACION_FISCAL,
    		INGRESOS_GASTOS,
    		SUBVENCIONES,
    		PARTES_VINCULANTES,
    		OTRA_INFORMACION,
    		MEDIOAMBIENTE,
    		APLAZAMIENTOS
    };
	
	/**
	 * Description de los apartados en los ejercios 2016
	 */
	private static final String[] apartadosName2016 = new String[]{
    		ACTIVIDAD_EMPRESA,
    		BASES_PRESENTACION,
    		NORMAS_REGISTRO,
    		INMOVILIZADO,
    		ACTIVOS_FINANCIEROS,
    		PASIVOS_FINANCIEROS,
    		FONDOS_PROPIOS,
    		SITUACION_FISCAL,
    		PARTES_VINCULANTES,
    		OTRA_INFORMACION,
    };
	
	@SuppressWarnings("serial")
	private static final HashMap<Integer,  String[]> apartadosName = new HashMap<Integer, String[]>() {{
	    put(2014, apartadosName2014);
	    put(2015, apartadosName2014);
	    put(2016, apartadosName2016);
	}};
	
	public Integer getApartadosSize(Integer year) {
		if(year > 2016) return apartados.get(2016);
		return apartados.get(year);
	}
	
	public String getApartadoName(Integer year, Integer pos){
		return "Apartado " + (pos + 1) + ": " + apartadosName.get(year)[pos];
	}
	
	public TreeNode<Enterprise> getTreeNode(DigitalDepositTreeNode ddtn, Integer pos){
		Integer year = ddtn.getD2Deposit().getYear();
		switch (apartadosName.get(year)[pos]) {
		case ACTIVIDAD_EMPRESA:
			return getActividadEmpresa(ddtn, getApartadoName(year, pos));
		case BASES_PRESENTACION:
			return getBasesPresentacion(ddtn, getApartadoName(year, pos));
		case APLICACION_RESULTADOS:
			return getAplicacionResultado(ddtn, getApartadoName(year, pos));
		case NORMAS_REGISTRO:
			return getNormasRegistro(ddtn, getApartadoName(year, pos));
		case INMOVILIZADO:
			return getInmovilizado(ddtn, getApartadoName(year, pos));
		case ACTIVOS_FINANCIEROS:
			return getActivosFinancieros(ddtn, getApartadoName(year, pos));
		case PASIVOS_FINANCIEROS:
			return getPasivosFinancieros(ddtn, getApartadoName(year, pos));
		case FONDOS_PROPIOS:
			return getFondosPropios(ddtn, getApartadoName(year, pos));
		case SITUACION_FISCAL:
			return getSituacionFiscal(ddtn, getApartadoName(year, pos));
		case INGRESOS_GASTOS:
			return getIngresosGastos(ddtn, getApartadoName(year, pos));
		case SUBVENCIONES:
			return getSubvenciones(ddtn, getApartadoName(year, pos));
		case PARTES_VINCULANTES:
			return getPartesVinculantes(ddtn, getApartadoName(year, pos));
		case OTRA_INFORMACION:
			return getOtraInformacion(ddtn, getApartadoName(year, pos));
		case MEDIOAMBIENTE:
			return getMedioAmbiente(ddtn, getApartadoName(year, pos));
		case APLAZAMIENTOS:
			return getAplazamientos(ddtn, getApartadoName(year, pos));
		default:
			return new TreeNode<Enterprise>() {
				@Override public void select(Deposit deposit) {}
				@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
				@Override public Enterprise getTreeObject() {return null;}
			}; 
		}
	}
	
	private TreeNode<Enterprise> getActividadEmpresa(DigitalDepositTreeNode ddtn, String description){
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
	
	private TreeNode<Enterprise> getBasesPresentacion(DigitalDepositTreeNode ddtn, String description){
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
	
	public TreeNode<Enterprise> getAplicacionResultado(DigitalDepositTreeNode ddtn, String description){
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
				PageM3_2 p32 = new PageM3_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getNormasRegistro(DigitalDepositTreeNode ddtn, String description){
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
	
	private TreeNode<Enterprise> getInmovilizado(DigitalDepositTreeNode ddtn, String description){
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
				PageM5_2 p52 = new PageM5_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getActivosFinancieros(DigitalDepositTreeNode ddtn, String description){
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
	
	private TreeNode<Enterprise> getPasivosFinancieros(DigitalDepositTreeNode ddtn, String description){
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
				PageM7_2 p72 = new PageM7_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getFondosPropios(DigitalDepositTreeNode ddtn, String description){
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
	
	private TreeNode<Enterprise> getSituacionFiscal(DigitalDepositTreeNode ddtn, String description){
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
	
	private TreeNode<Enterprise> getIngresosGastos(DigitalDepositTreeNode ddtn, String description){
		TreeNode<Enterprise> ig = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM10 p10 = new PageM10(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
				ddtn.getNormalizedMemory().setPagesPanel(p10);
    			fiscalPanel.setContent(ddtn.getNormalizedMemory());
			}
			
			@Override public TreeNode<Enterprise> render(HasTreeItems parent, Enterprise t) {return null;}
			@Override public Enterprise getTreeObject() {return (Enterprise) getUserObject();}
		};
		ig.setText(description);
		
		return ig;
	}
	
	private TreeNode<Enterprise> getSubvenciones(DigitalDepositTreeNode ddtn, String description){
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
				PageM11_2 p112 = new PageM11_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getPartesVinculantes(DigitalDepositTreeNode ddtn, String description){
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
				PageM12_2 p122 = new PageM12_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getOtraInformacion(DigitalDepositTreeNode ddtn, String description){
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
				PageM13_2 p132 = new PageM13_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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

	private TreeNode<Enterprise> getMedioAmbiente(DigitalDepositTreeNode ddtn, String description){
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
				PageM14_2 p142 = new PageM14_2(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
	
	private TreeNode<Enterprise> getAplazamientos(DigitalDepositTreeNode ddtn, String description){
		TreeNode<Enterprise> iapep = new TreeNode<Enterprise>() {
			
			@Override
			public void select(Deposit fiscalPanel) {
				ddtn.getNormalizedMemory().paintHeaderTable("Memoria Normalizada", ddtn.getD2Deposit().getMap().get(D2DepositConstants.DEPOSIT_TYPE), ddtn.getD2Deposit().getYear().toString());
				PageM15 p15 = new PageM15(ddtn.getD2Deposit().getEnterprise(), ddtn.getNormalizedMemory(), ddtn.getD2Deposit().getYear());
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
