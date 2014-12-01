package com.esferalia.aon.gwt.fiscal.shared.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C1;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C2;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C3;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C4;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C6;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C7;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C8;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0C9;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E1;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E2;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E3;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E4;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E5;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E6;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E7;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E8;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CP0E9;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CPC10;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CPC11;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CPC12;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CPE11;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.CPE12;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200LQ554Key implements Serializable, IsSerializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	C0001(new Mod200Key[]{CP0C1,CP0E1},"1. Ingresos computables")
	,C0002(new Mod200Key[]{CP0C2,CP0E2},"2. Gastos espec\u00EDficos")
	,C0003(new Mod200Key[]{CP0C3,CP0E3},"3. Gastos generales imputados")
	,C0004(new Mod200Key[]{CP0C4,CP0E4},"4. Gastos Fondo de Educaci\u00F3n y Promoci\u00F3n") 
	,C0005(new Mod200Key[]{null ,CP0E5},"5. Incrementos y disminuciones patrimoniales")
	,C0006(new Mod200Key[]{CP0C6,CP0E6},"6. Resultado (1 - 2 - 3 - 4 + 5)")
	,C0007(new Mod200Key[]{CP0C7,CP0E7},"7. Aumentos (ajustes positivos)")
	,C0008(new Mod200Key[]{CP0C8,CP0E8},"8. Disminuciones (ajustes negativos)")
	,C0009(new Mod200Key[]{CP0C9,CP0E9},"9. 50% Dotaci\u00F3n obligatoria F.R.O. (art. 16.5 Ley 20/1990)")
	,C0010(new Mod200Key[]{CPC10,null },"10. Reserva para inversiones en Canarias (Ley 19/1994)")
	,C0011(new Mod200Key[]{CPC11,CPE11},"11. Factor de agotamiento")
	,C0012(new Mod200Key[]{CPC12,CPE12},"12. Base imponible (6 + 7 - 8 - 9 + 10 + 11)")
	;
	 
    private String description;
    private Mod200Key[] keys;

	private Mod200LQ554Key(Mod200Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod200Key[] getKeys() {
		return keys; 
	}
}

