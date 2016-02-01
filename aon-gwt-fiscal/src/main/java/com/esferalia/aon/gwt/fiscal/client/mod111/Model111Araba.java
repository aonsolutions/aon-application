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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111Araba extends Model111Base {
	 
	public static enum ModelScript implements IModelScript {
		 AR_CX0 ("Rendimientos procedentes del trabajo o servicios que se presten en el Territorio Hist\u00F3rico de \u00C1lava"
				,new Mod111Key[]{Mod111Key.AR_C50,Mod111Key.AR_C60,Mod111Key.AR_C70}
				,ENABLED,SALARY)
		,AR_CX1 ("Pensiones"
				,new Mod111Key[]{Mod111Key.AR_C51,Mod111Key.AR_C61,Mod111Key.AR_C71}
				,ENABLED,NONE)
		,AR_CX2 ("Retribuciones de los miembros del Consejo de Administraci\u00F3n y Juntas que hagan sus veces de entidades con domicilio fiscal en \u00C1lava"
				,new Mod111Key[]{Mod111Key.AR_C52,Mod111Key.AR_C62,Mod111Key.AR_C72}
				,ENABLED,NONE)
		,AR_CX3 ("Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributan en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del volumen de operaciones)"
				,new Mod111Key[]{Mod111Key.AR_C53,Mod111Key.AR_C63,Mod111Key.AR_C73}
				,ENABLED,NONE)
		,AR_CX4 ("Rendimientos satisfechos por contraprestaciones profesionales"
				,new Mod111Key[]{Mod111Key.AR_C54,Mod111Key.AR_C64,Mod111Key.AR_C74}
				,ENABLED,INVOICE)
		,AR_CX8 ("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad signos, \u00EDndices o m\u00F3dulos"
				,new Mod111Key[]{Mod111Key.AR_C58,Mod111Key.AR_C68,Mod111Key.AR_C78}
				,ENABLED,INVOICE)
		,AR_CX5 ("Rendimientos satisfechos por contraprestaciones de actividades agr\u00EDcolas, ganaderas y forestales"
				,new Mod111Key[]{Mod111Key.AR_C55,Mod111Key.AR_C65,Mod111Key.AR_C75}
				,ENABLED,INVOICE)
		,AR_CX6 ("Premios satisfechos (en met\u00E1lico y en especie)"
				,new Mod111Key[]{Mod111Key.AR_C56,Mod111Key.AR_C66,Mod111Key.AR_C76}
				,ENABLED,NONE)
		,AR_CX7 ("Retribuciones en especie y otras, excepto las correspondientes a Premios"
				,new Mod111Key[]{Mod111Key.AR_C57,Mod111Key.AR_C67,Mod111Key.AR_C77}
				,ENABLED,SALARY)
		,AR_CXX ("Total"
				,new Mod111Key[]{Mod111Key.AR_C82}
				,DISABLED,COMPUTE)
		,AR_C84("Recargo pr\u00F3rroga"
				,new Mod111Key[]{Mod111Key.AR_C84}
				,ENABLED,NONE)
		,AR_C85("Intereses de demora"
				,new Mod111Key[]{Mod111Key.AR_C85}
				,ENABLED,NONE)
		,AR_C87("Deuda tributaria a ingresar"
				,new Mod111Key[]{Mod111Key.AR_C87}
				,DISABLED,COMPUTE)
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
	
	public Model111Araba(IFiscalModelCallback<Mod111> callback) {
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
				 "Formulario Papel." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D110.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668896&ssbinary=true"));
		panel.add(getAnchorPanel(
				"Orden Foral 54 de 31 de enero de 2007."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+54+de+31+de+enero+de+2007.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668897&ssbinary=true"));
		panel.add(getAnchorPanel(
				"Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668898&ssbinary=true"));
		panel.add(getAnchorPanel(
				"Resoluci\u00F3n 135 de 27 de enero de 2015."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DResoluci%C3%B3n+135+de+27+de+enero+de+2015.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668899&ssbinary=true"));
		panel.add(getAnchorPanel(
				"Orden Foral 104 de 17 de febrero de 2014."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668900&ssbinary=true"));
		return panel;
	}
	
}
