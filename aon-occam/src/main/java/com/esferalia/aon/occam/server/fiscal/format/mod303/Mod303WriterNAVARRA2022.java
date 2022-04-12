package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303WriterNAVARRA2022 implements IMod303Writer{

	private enum Mod303File {
		R00 ( mod -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("0")
			,(wr, mod) -> wr.append("F69")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod303Key.NF_I00), mod.getDocument()),9))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod303Key.NF_I01), mod.getName()),40))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(52))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(1, 8))	// Registro tipo 1
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0, 8))	// Registro tipo 2
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0, 8))	// Registro tipo 3
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0, 8))	// Registro tipo 4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0, 8))	// Registro tipo 5
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0, 8))	// Registro tipo 6
			,(wr, mod) -> wr.append("T")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPhone(),9))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPerson(),40))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(30))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(13))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})				
		,R01 ( mod -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("1")
			,(wr, mod) -> wr.append("F69")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			,(wr, mod) -> wr.append("00")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(6))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getName(),40))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(14))
			,(wr, mod) -> wr.append("T")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPhone(),9))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPerson(),40))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(13))
			,(wr, mod) -> wr.append(" ")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isReplacement()?"S":" ",1))
			,(wr, mod) -> wr.append(mod.isReplacement()
	   			?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
	   			:AonFiscalFileUtils.spaces(13))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceCCC(),20))
			,(wr, mod) -> wr.append((mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK)?"7":"0")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(22))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(8))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(28))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(13))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})				
		
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303);
		}
		private void fillPage(Mod303 mod303, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod303);
			}
		}
	}

	public void fillWriter(Mod303 mod, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File format : Mod303File.values()) {
			if (format.accept(mod)) {
				format.fillPage(mod, wr);
				filled = true;
			}
		}
		if (filled) {
			int i = 0;
			for (Mod303Key key : KEYS ) {
				if (AonMathUtils.isNotZero(mod.getAmount(key))) {
					if (i == 0) {
						wr.append("2")
							.append("F69")
							.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
							.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
							.append("00")
							.append(AonFiscalFileUtils.zeros(6))
							.append(AonFiscalFileUtils.text(mod.getDocument(),9));
					}
					wr.append("0")
						.append(key.getBoxAsString())
						.append(AonFiscalFileUtils.signedSpace(mod.getAmount(key),16,2));
					i++;
					if (i==10) {
						wr.append(AonFiscalFileUtils.spaces(24))
							.append(AonStringUtils.CR_LF);
						i = 0;
					}
				}
			}
			if (i > 0) {
				for (;i<10;i++) {
					wr.append("0000")
					  .append(AonFiscalFileUtils.signedSpace(0.0,16,2));
				}
				wr.append(AonFiscalFileUtils.spaces(24))
					.append(AonStringUtils.CR_LF);
			}
			
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

	private static final Mod303Key[] KEYS = new Mod303Key[] {
		 Mod303Key.NF_010
		,Mod303Key.NF_001
		,Mod303Key.NF_002
		,Mod303Key.NF_194
		,Mod303Key.NF_171
		,Mod303Key.NF_003
		,Mod303Key.NF_013
		,Mod303Key.NF_004
		,Mod303Key.NF_014
		,Mod303Key.NF_005
		,Mod303Key.NF_015
		,Mod303Key.NF_006
		,Mod303Key.NF_016
		,Mod303Key.NF_051
		,Mod303Key.NF_052
		,Mod303Key.NF_007
		,Mod303Key.NF_017
		,Mod303Key.NF_008
		,Mod303Key.NF_018
		,Mod303Key.NF_009
		,Mod303Key.NF_019
		,Mod303Key.NF_172
		,Mod303Key.NF_173
		,Mod303Key.NF_176
		,Mod303Key.NF_177
		,Mod303Key.NF_020
		,Mod303Key.NF_031
		,Mod303Key.NF_041
		,Mod303Key.NF_131
		,Mod303Key.NF_141
		,Mod303Key.NF_032
		,Mod303Key.NF_042
		,Mod303Key.NF_039
		,Mod303Key.NF_049
		,Mod303Key.NF_170
		,Mod303Key.NF_043
		,Mod303Key.NF_174
		,Mod303Key.NF_175
		,Mod303Key.NF_178
		,Mod303Key.NF_179
		,Mod303Key.NF_045
		,Mod303Key.NF_450
		,Mod303Key.NF_050
		,Mod303Key.NF_061
		,Mod303Key.NF_062
		,Mod303Key.NF_063
	};	
	
}
