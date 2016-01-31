package com.esferalia.aon.gwt.fiscal.client.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model110Bizkaia extends Model111Base {

	private static enum ModelScript implements IModelScript {
		 AR_CX00 ("Rendimientos procedentes de trabajos o servicios que se presten en Bizkaia"
				 ,new Mod111Key[]{Mod111Key.BZ_C01,Mod111Key.BZ_C12,Mod111Key.BZ_C23},ENABLED,SALARY)
		,AR_CX01 ("Retribuciones de miembros de Consejos de Administraci\u00F3n y Juntas que hagan sus veces de empresas o entidades con domicilio fiscal en Bizkaia"
				,new Mod111Key[]{Mod111Key.BZ_C02,Mod111Key.BZ_C13,Mod111Key.BZ_C24},ENABLED,NONE)
		,AR_CX02 ("Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributen en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del porcentaje)"
				,new Mod111Key[]{Mod111Key.BZ_C03,Mod111Key.BZ_C14,Mod111Key.BZ_C25},ENABLED,NONE)
		,AR_CX03 ("Rendimientos de trabajo en per\u00EDodos inferiores al a\u00F1o, trabajos de temporada o trabajos circunstanciales"
				,new Mod111Key[]{Mod111Key.BZ_C04,Mod111Key.BZ_C15,Mod111Key.BZ_C26},ENABLED,NONE)
		,AR_CX04 ("Prestaciones por desempleo"
				,new Mod111Key[]{Mod111Key.BZ_C05,Mod111Key.BZ_C16,Mod111Key.BZ_C27},ENABLED,NONE)
		,AR_CX05 ("Pensiones y haberes pasivos"
				,new Mod111Key[]{Mod111Key.BZ_C06,Mod111Key.BZ_C17,Mod111Key.BZ_C28},ENABLED,NONE)
		,AR_CX06 ("Rendimientos satisfechos por contraprestaciones profesionales, art\u00EDsticas o deportivas y retribuciones de comisionistas, agentes comerciales, agentes de seguros y subagentes"
				,new Mod111Key[]{Mod111Key.BZ_C07,Mod111Key.BZ_C18,Mod111Key.BZ_C29},ENABLED,INVOICE)
		,AR_CX07 ("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad de signos, \u00EDndices o m\u00F3dulos"
				,new Mod111Key[]{Mod111Key.BZ_C50,Mod111Key.BZ_C51,Mod111Key.BZ_C52},ENABLED,INVOICE)
		,AR_CX08 ("Retenciones sobre rendimientos de actividades agr\u00EDcolas, ganaderas y forestales"
				,new Mod111Key[]{Mod111Key.BZ_C08,Mod111Key.BZ_C19,Mod111Key.BZ_C30},ENABLED,INVOICE)
		,AR_CX09 ("Retribuciones en especie"
				,new Mod111Key[]{Mod111Key.BZ_C09,Mod111Key.BZ_C20,Mod111Key.BZ_C31},ENABLED,SALARY)
		,AR_CX10 ("Premios"
				,new Mod111Key[]{Mod111Key.BZ_C10,Mod111Key.BZ_C21,Mod111Key.BZ_C32},ENABLED,NONE)
		,AR_CX11 ("Rendimientos no comprendidos en apartados anteriores"
				,new Mod111Key[]{Mod111Key.BZ_C11,Mod111Key.BZ_C22,Mod111Key.BZ_C33},ENABLED,NONE)
		,AR_CX12 ("Totales",new Mod111Key[]{Mod111Key.BZ_C34T,Mod111Key.BZ_C35T,Mod111Key.BZ_C36T},DISABLED,COMPUTE)
		,AR_CX13 ("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.BZ_C39},DISABLED,COMPUTE)
		;
		
		private String label;
		private Mod111Key[] keys;
		private boolean enabled;
		private Mod111KeyInfo infoKey;
		
		private ModelScript(String label, Mod111Key[] keys,boolean enabled,Mod111KeyInfo infoKey) {
			this.label = label;
			this.keys = keys;
			this.enabled = enabled;
			this.infoKey = infoKey;
		}

		@Override
		public String getLabel() {
			return label;
		}
		@Override
		public Mod111Key[] getKeys() {
			return keys;
		}
		@Override
		public boolean isEnabled() {
			return enabled;
		}
		@Override
		public Mod111KeyInfo getInfoKey() {
			return infoKey;
		};
	}
	
	public Model110Bizkaia(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	protected void paintDeclaration(final Mod111 mod111) {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		defineTable();
		paintHeader();
		
		for (ModelScript ms : ModelScript.values()) {
			paintRow(mod111,ms);	
		}
	}

	@Override
	public Widget getInfoPanel() {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(
				 "Impreso Rellenable. [pdf]" 
				,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=0E681A22B56829AFDBC3ED6DE62B26D17C12C3911BAF83BAC7253A40FDAC1106&Tam=171&Ext=application/pdf&Tem_Codigo=2093"));
		panel.add(getAnchorPanel(
				"ORDEN FORAL 1452/2007, de 23 de mayo."
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2007/06/20070604a109.pdf#page=34"));
		panel.add(getAnchorPanel(
				"Enlace al programa de ayuda"
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1992&idioma=CA&dpto_biz=5&codpath_biz=5|3587|1933|1948|1992"));
		panel.add(getAnchorPanel(
				"Enlace a las fechas de vencimiento en el a?o vigente"
				,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=245&Age_Codigo=29/01/2016&Tem_Codigo=5346"));
		panel.add(getAnchorPanel(
				"Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
				,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return panel;
	}

	private FlowPanel getAnchorPanel(String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(AON.AON_CSS.aonIconPdfPreview());
		p.add(a);
		return p;
	}
}
