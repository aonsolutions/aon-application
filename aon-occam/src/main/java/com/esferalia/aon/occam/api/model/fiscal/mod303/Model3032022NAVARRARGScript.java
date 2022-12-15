package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_OUT_VAT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032022NAVARRARGScript implements IModelScript<Mod303Key> {
	
	 DVG01 ("IVA devengado",null,null,TITLE)
	,DVG02 (Mod303Key.NF_010.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_010},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 (Mod303Key.NF_001.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_001},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 (Mod303Key.NF_002.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_002},null,NONE)
	,DVG05 (Mod303Key.NF_194.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_194},null,MODEL_OUT_VAT_ACCRUAL_INVOICE)
	,DVG06 (Mod303Key.NF_171.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_171},null,NONE)
	 
	 
	,DVG07 (null					,new Mod303Key[]{Mod303Key.NF_003	,Mod303Key.NF_X03	,Mod303Key.NF_013},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.NF_004	,Mod303Key.NF_X04	,Mod303Key.NF_014},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 (null					,new Mod303Key[]{Mod303Key.NF_005	,Mod303Key.NF_X05	,Mod303Key.NF_015},null,MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG10 (null					,new Mod303Key[]{Mod303Key.NF_006	,Mod303Key.NF_X06	,Mod303Key.NF_016},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 ("Recargo equivalencia"	,new Mod303Key[]{Mod303Key.NF_051	,Mod303Key.NF_X51	,Mod303Key.NF_052},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG12 (null					,new Mod303Key[]{Mod303Key.NF_007	,Mod303Key.NF_X07	,Mod303Key.NF_017},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 (null					,new Mod303Key[]{Mod303Key.NF_008	,Mod303Key.NF_X08	,Mod303Key.NF_018},null,MODEL_INVOICE_VAT_BREAKDOWN)

	,DVG14 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod303Key[]{Mod303Key.NF_009	,null			  	,Mod303Key.NF_019},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG16 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod303Key[]{Mod303Key.NF_172	,null			  	,Mod303Key.NF_173},null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG17 ("Modificaci\u00F3n bases y cuotas"
									,new Mod303Key[]{Mod303Key.NF_176	,null			  	,Mod303Key.NF_177},null,MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG18 (Mod303Key.GP_C016.getDescription() 
									,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_020},null,COMPUTE)
	
	
	,DED01 ("IVA deducible",null,null,TITLE)
	,DED02 ("Por cuotas soportadas en operaciones interiores corrientes"
									,new Mod303Key[]{Mod303Key.NF_031	,null			  	,Mod303Key.NF_041}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED03 ("Por cuotas soportadas en operaciones de bienes de inversi\u00F3n"
									,new Mod303Key[]{Mod303Key.NF_131	,null			  	,Mod303Key.NF_141}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED04 ("Por cuotas soportadas en importaciones"
									,new Mod303Key[]{Mod303Key.NF_032	,null			  	,Mod303Key.NF_042}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED05 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod303Key[]{Mod303Key.NF_039	,null			  	,Mod303Key.NF_049}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED06 ("Compensaciones R\u00E9gimen Especial Agricultura, Ganader\u00EDa y  Pesca"
									,new Mod303Key[]{Mod303Key.NF_170	,null			  	,Mod303Key.NF_043}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED07 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod303Key[]{Mod303Key.NF_174	,null			  	,Mod303Key.NF_175}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED08 ("Modificaci\u00F3n de bases y cuotas"
									,new Mod303Key[]{Mod303Key.NF_178	,null			  	,Mod303Key.NF_179}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED09 (Mod303Key.NF_045.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_045},null,NONE)
	,DED10 (Mod303Key.NF_450.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_450},null,COMPUTE_KEY)
	,DED11 (Mod303Key.NF_050.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_050},null,COMPUTE)
	
	,TOT  ("Resultado",null,null,TITLE)
	,TOT01 (Mod303Key.NF_055.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_055},null,NONE)
	,TOT02 (Mod303Key.NF_061.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_061},null,COMPUTE)
	,TOT03 (Mod303Key.NF_062.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_062},null,COMPUTE_KEY)
	,TOT04 (Mod303Key.NF_063.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.NF_063},null,COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032022NAVARRARGScript(String label, Mod303Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
		this.keyTypes = keyTypes; 
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod303Key[] getKeys() {
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
	public int getFieldSize(Mod303Key key) {
		if (key == Mod303Key.GP_X002 || key == Mod303Key.GP_X004 || key == Mod303Key.GP_X006
		 || key == Mod303Key.GP_X008 || key == Mod303Key.GP_X010 || key == Mod303Key.GP_X012) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.GP_X002 || key == Mod303Key.GP_X004 || key == Mod303Key.GP_X006
		 || key == Mod303Key.GP_X008 || key == Mod303Key.GP_X010 || key == Mod303Key.GP_X012) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}
