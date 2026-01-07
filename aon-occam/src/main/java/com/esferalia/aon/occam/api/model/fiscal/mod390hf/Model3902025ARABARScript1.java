package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902025ARABARScript1 implements IModelScript<Mod390Key> {
	
	  DVG01 ("IVA devengado",null,TITLE)
	 ,DVG02 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C202	,Mod390Key.AR_C203	,Mod390Key.AR_C204},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 0%
//	 ,DVG03 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C380	,Mod390Key.AR_C381	,Mod390Key.AR_C382},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 2%
	 ,DVG04 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C001	,Mod390Key.AR_C002	,Mod390Key.AR_C003},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 4%
//	 ,DVG05 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C205	,Mod390Key.AR_C206	,Mod390Key.AR_C207},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 5%
//	 ,DVG06 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C383	,Mod390Key.AR_C384	,Mod390Key.AR_C385},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 7,5%
	 ,DVG07 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C804	,Mod390Key.AR_C805	,Mod390Key.AR_C806},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 10%
	 ,DVG08 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C807	,Mod390Key.AR_C808	,Mod390Key.AR_C809},MODEL_INVOICE_VAT_BREAKDOWN) // Régimen General al 21%
	 
	 ,DVG09 ("Modificaci\u00F3n bases y cuotas", new Mod390Key[]{Mod390Key.AR_C351,null,Mod390Key.AR_C352},MODEL_INVOICE_VAT_BREAKDOWN) // Modificación bases y cuotas del régimen general
	 ,DVG10 ("Inversi\u00F3n del sujeto pasivo", new Mod390Key[]{Mod390Key.AR_C019,null,Mod390Key.AR_C020},MODEL_INVOICE_VAT_BREAKDOWN) // Inversión del sujeto pasivo
	 ,DVG11 ("Concurso de acreedores"          , new Mod390Key[]{Mod390Key.AR_C223,null,Mod390Key.AR_C224},NONE) 						// Concurso de acreedores
	 
//	 ,DVG12 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C810	,Mod390Key.AR_C811	,Mod390Key.AR_C812},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 0%
//	 ,DVG13 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C386	,Mod390Key.AR_C387	,Mod390Key.AR_C388},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 0,26% 
	 ,DVG14 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C025	,Mod390Key.AR_C026	,Mod390Key.AR_C027},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 0,50%
//	 ,DVG15 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C819	,Mod390Key.AR_C820	,Mod390Key.AR_C821},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 0,62%
//	 ,DVG16 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C389	,Mod390Key.AR_C390	,Mod390Key.AR_C391},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 1,00%
	 ,DVG17 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C828	,Mod390Key.AR_C829	,Mod390Key.AR_C830},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 1,40%	 
	 ,DVG18 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C034	,Mod390Key.AR_C035	,Mod390Key.AR_C036},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 1,75%
	 ,DVG19 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C831	,Mod390Key.AR_C832	,Mod390Key.AR_C833},MODEL_INVOICE_VAT_BREAKDOWN) // Recargo de equivalencia al 5,20%
	 
	 ,DVG20 ("Modificaciones bases y cuotas del recargo de equivalencia", new Mod390Key[]{Mod390Key.AR_C037	,null				,Mod390Key.AR_C038},MODEL_INVOICE_VAT_BREAKDOWN) // Modificaciones bases y cuotas del recargo de equivalencia
	 ,DVG21 ("Concurso de acreedores"									, new Mod390Key[]{Mod390Key.AR_C239	,null				,Mod390Key.AR_C240},MODEL_INVOICE_VAT_BREAKDOWN) // Concurso de acreedores del recargo de equivalencia
	 
//	 ,DVG22 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C234	,Mod390Key.AR_C235	,Mod390Key.AR_C236},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 0%
//	 ,DVG23 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C392	,Mod390Key.AR_C393	,Mod390Key.AR_C394},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 2%
	 ,DVG24 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C010	,Mod390Key.AR_C011	,Mod390Key.AR_C012},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 4%
	 ,DVG25 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C231	,Mod390Key.AR_C232	,Mod390Key.AR_C233},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 5%
//	 ,DVG26 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C395	,Mod390Key.AR_C396	,Mod390Key.AR_C397},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 7,5%
	 ,DVG27 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C813	,Mod390Key.AR_C814	,Mod390Key.AR_C815},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 10%
	 ,DVG28 ("Adquisiciones intracomunitarias de bienes y servicios", new Mod390Key[]{Mod390Key.AR_C816	,Mod390Key.AR_C817	,Mod390Key.AR_C818},MODEL_INVOICE_VAT_BREAKDOWN) // Adquisiciones intracomunitarias de bienes y servicios al 21%
	 
	 ,DVG29 ("Modificaciones bases y cuotas de adquisiciones intracomunitarias", new Mod390Key[]{Mod390Key.AR_C353	,null				,Mod390Key.AR_C354},MODEL_INVOICE_VAT_BREAKDOWN) // Modificaciones bases y cuotas de adquisiciones intracomunitarias
	 
	 ,DVG30 (Mod390Key.AR_C041.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.AR_C041},COMPUTE) // Total cuota devengada
	 ,DVG31 (Mod390Key.AR_C042.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.AR_C042},NONE)    // Minoración por devolución en régimen de viajeros
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902025ARABARScript1(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod390Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	}
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == DVG01);
	}
	
	@Override
	public int getFieldSize(Mod390Key key) {
		if (key == Mod390Key.AR_C203
//		 || key == Mod390Key.AR_C381				
		 || key == Mod390Key.AR_C002
//		 || key == Mod390Key.AR_C206
//		 || key == Mod390Key.AR_C384
		 || key == Mod390Key.AR_C805
		 || key == Mod390Key.AR_C808
//		 || key == Mod390Key.AR_C811
//		 || key == Mod390Key.AR_C387
		 || key == Mod390Key.AR_C026
//		 || key == Mod390Key.AR_C820
//		 || key == Mod390Key.AR_C390
		 || key == Mod390Key.AR_C829
		 || key == Mod390Key.AR_C035
		 || key == Mod390Key.AR_C832
//		 || key == Mod390Key.AR_C235
//		 || key == Mod390Key.AR_C393
		 || key == Mod390Key.AR_C011
		 || key == Mod390Key.AR_C232
//		 || key == Mod390Key.AR_C396
		 || key == Mod390Key.AR_C814
		 || key == Mod390Key.AR_C817) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.AR_C203
//		 || key == Mod390Key.AR_C381
		 || key == Mod390Key.AR_C002
//		 || key == Mod390Key.AR_C206
//		 || key == Mod390Key.AR_C384
		 || key == Mod390Key.AR_C805
		 || key == Mod390Key.AR_C808
//		 || key == Mod390Key.AR_C811
//		 || key == Mod390Key.AR_C387
		 || key == Mod390Key.AR_C026
//		 || key == Mod390Key.AR_C820
//		 || key == Mod390Key.AR_C390
		 || key == Mod390Key.AR_C829
		 || key == Mod390Key.AR_C035
		 || key == Mod390Key.AR_C832
//		 || key == Mod390Key.AR_C235
//		 || key == Mod390Key.AR_C393
		 || key == Mod390Key.AR_C011
		 || key == Mod390Key.AR_C232
//		 || key == Mod390Key.AR_C396
		 || key == Mod390Key.AR_C814		 
		 || key == Mod390Key.AR_C817) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}
