package com.esferalia.aon.gwt.fiscal.shared.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.NORMAL;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.TITLE_DISABLED;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.TITLE_ENABLED;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.BALANCE_ACTIVE;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.BALANCE_PASIVE;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.CORRECTION_DECREASE;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.CORRECTION_INCREASE;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.DEDUCIBLE_LIMITATION;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.ECPN_CHANGE;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.ECPN_INCOME;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.INCOME_DISTRIBUTION;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.LIQUIDATION;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.LIQUIDATION_II;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.LIQUIDATION_III;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.LIQUIDATION_IV;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.OLD_Mod200KeyType.PYG;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.google.gwt.user.client.rpc.IsSerializable;

public enum OLD_Mod200Key implements Serializable, IsSerializable {
	BA101(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C101"}
		,"ACTIVO NO CORRIENTE (N, A, P)"
		,"BA102 + BA111 + BA115 + BA118 + BA126 + BA134 + BA135"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA102(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C102"}
		,"Inmovilizado intangible (N, A, P)"
		,"BA103 + BA104 + BA105 + BA106 + BA107 + BA108 + BA109 + BA700 + BA701 + BA110"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA103(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C103"}
		,"Desarrollo (N)"
		,null
		,"sd({201})-sa({2801,2901})"
		,"C0050"
		,NORMAL)
	,BA104(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C104"}
		,"Concesiones (N)"
		,null
		,"sd({202})-sa({2802,2902})"
		,"C0050"
		,NORMAL)
	,BA105(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C105"}
		,"Patentes, licencias, marcas y similares (N)"
		,null
		,"sd({203})-sa({2803,2903})"
		,"C0050"
		,NORMAL)
	,BA106(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C106"}
		,"Fondo de comercio (N, A, P)"
		,null
		,"sd({204})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA107(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C107"}
		,"Aplicaciones informáticas (N)"
		,null
		,"sd({206})-sa({2806,2906})"
		,"C0050"
		,NORMAL)
	,BA108(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C108"}
		,"Investigación (N)"
		,null
		,"sd({200})-sa({2800})"
		,"C0050"
		,NORMAL)
	,BA700(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C700"}
		,"Propiedad intelectual (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA701(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C701"}
		,"Derechos de emisión de gases de efecto invernadero (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA109(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C109"}
		,"Otro inmovilizado intangible (N)"
		,null
		,"sd({205,209})-sa({2805,2905})"
		,"C0050"
		,NORMAL)
	,BA110(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C110"}
		,"Resto (A, P)"
		,null
		,null
		,"C0051 || C0052"
		,NORMAL)
	,BA111(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C111"}
		,"Inmovilizado material (N, A, P)"
		,"BA112 + BA113 + BA114"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA112(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C112"}
		,"Terrenos y construcciones (N)"
		,null
		,"sd({210,211})-sa({2811,2910,2911})"
		,"C0050"
		,NORMAL)
	,BA113(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C113"}
		,"Instalaciones técnicas y otro inmovilizado material (N)"
		,null
		,"sd({212,213,214,215,216,217,218,219})-sa({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919})"
		,"C0050"
		,NORMAL)
	,BA114(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C114"}
		,"Inmovilizado en curso y anticipos (N)"
		,null
		,"sd({23})"
		,"C0050"
		,NORMAL)
	,BA115(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C115"}
		,"Inversiones inmobiliarias (N, A, P)"
		,"BA116 + BA117"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA116(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C116"}
		,"Terrenos (N)"
		,null
		,"sd({220})-sa({2920})"
		,"C0050"
		,NORMAL)
	,BA117(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C117"}
		,"Construcciones (N)"
		,null
		,"sd({221})-sa({282,2921})"
		,"C0050"
		,NORMAL)
	,BA118(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C118"}
		,"Inversiones en empresas del grupo y asociadas a largo plazo (N, A, P)"
		,"BA119+BA120+BA121+BA122+BA123+BA124+BA125"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA119(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C119"}
		,"Instrumentos de patrimonio (N, A, P)"
		,null
		,"sd({2403,2404})-sa({2493,2494,293})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA120(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C120"}
		,"Créditos a empresas (N)"
		,null
		,"sd({2423,2424})-sa({2953,2954})"
		,"C0050"
		,NORMAL)
	,BA121(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C121"}
		,"Valores representativos de deuda (N)"
		,null
		,"sd({2413,2414})-sa({2943,2944})"
		,"C0050"
		,NORMAL)
	,BA122(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C122"}
		,"Derivados (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA123(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C123"}
		,"Otros activos financieros (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA124(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C124"}
		,"Otras inversiones (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA125(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C125"}
		,"Resto (A, P)"
		,null
		,null
		,"C0051 || C0052"
		,NORMAL)
	,BA126(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C126"}
		,"Inversiones financieras a largo plazo (N, A, P)"
		,"BA127+BA128+BA129+BA130+BA131+BA132+BA133"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA127(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C127"}
		,"Instrumentos de patrimonio (N, A, P)"
		,null
		,"sd({2405,250})-sa({2495,259})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA128(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C128"}
		,"Créditos a terceros (N)"
		,null
		,"sd({2425,252,253,54})-sa({2955,298})"
		,"C0050"
		,NORMAL)
	,BA129(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C129"}
		,"Valores representativos de deuda (N)"
		,null
		,"sd({2415,251})-sa({2945,297})"
		,"C0050"
		,NORMAL)
	,BA130(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C130"}
		,"Derivados (N)"
		,null
		,"sd({255})"
		,"C0050"
		,NORMAL)
	,BA131(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C131"}
		,"Otros activos financieros (N)"
		,null
		,"sd({258,26})"
		,"C0050"
		,NORMAL)
	,BA132(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C132"}
		,"Otras inversiones (N)"
		,null
		,"sd({257})"
		,"C0050"
		,NORMAL)
	,BA133(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C133"}
		,"Resto (A, P)"
		,null
		,null
		,"C0051 || C0052"
		,NORMAL)
	,BA134(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C134"}
		,"Activos por impuesto diferido (N, A, P)"
		,null
		,"sd({474})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA135(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C135"}
		,"Deudores comerciales no corrientes (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA136(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C136"}
		,"ACTIVO CORRIENTE (N, A, P)"
		,"BA137 + BA138 + BA149 + BA160 + BA168 + BA176 + BA177"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA137(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C137"}
		,"Activos no corrientes mantenidos para la venta (N, A)"
		,null
		,"sd({580,581,582,583,584,599})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,BA138(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C138"}
		,"Existencias (N, A, P)"
		,"BA139+BA140+BA141+BA142+BA143+BA144+BA145+BA146+BA147+BA148"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA139(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C139"}
		,"Comerciales (N)"
		,null
		,"sd({30})-sa({390})"
		,"C0050"
		,NORMAL)
	,BA140(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C140"}
		,"Materias primas y otros aprovisionamientos (N)"
		,null
		,"sd({31,32})-sa({391,392})"
		,"C0050"
		,NORMAL)
	,BA141(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C141"}
		,"Productos en curso (N)"
		,null
		,"sd({33,34})-sa({393,394})"
		,"C0050"
		,NORMAL)
	,BA142(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C142"}
		,"De ciclo largo de producción (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA143(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C143"}
		,"De ciclo corto de producción (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA144(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C144"}
		,"Productos terminados (N)"
		,null
		,"sd({35})-sa({395})"
		,"C0050"
		,NORMAL)
	,BA145(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C145"}
		,"De ciclo largo de producción (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA146(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C146"}
		,"De ciclo corto de producción (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA147(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C147"}
		,"Subproductos, residuos y materiales recuperados (N)"
		,null
		,"sd({36})-sa({396})"
		,"C0050"
		,NORMAL)
	,BA148(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C148"}
		,"Anticipos a proveedores (N)"
		,null
		,"sd({407})"
		,"C0050"
		,NORMAL)
	,BA149(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C149"}
		,"Deudores comerciales y otras cuentas a cobrar (N, A, P)"
		,"BA150 + BA151 + BA152 + BA153 + (C0050?(BA154 + BA155 + BA156 + BA157 + BA158):(BA159))"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA150(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C150"}
		,"Clientes por ventas y prestaciones de servicios (N, A, P)"
		,null
		,"sd({430,431,432,435,436})-sa({437,490,4935})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA151(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C151"}
		,"Clientes por ventas y prestaciones de servicios a largo plazo (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA152(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C152"}
		,"Clientes por ventas y prestaciones de servicios a corto plazo (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA153(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C153"}
		,"Clientes empresas del grupo y asociadas (N)"
		,null
		,"sd({433,434})-sa({4933,4934})"
		,"C0050"
		,NORMAL)
	,BA154(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C154"}
		,"Deudores varios (N)"
		,null
		,"sd({44})"
		,"C0050"
		,NORMAL)
	,BA155(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C155"}
		,"Personal (N)"
		,null
		,"sd({460,544})"
		,"C0050"
		,NORMAL)
	,BA156(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C156"}
		,"Activos por impuesto corriente (N)"
		,null
		,"sd({4709})"
		,"C0050"
		,NORMAL)
	,BA157(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C157"}
		,"Otros créditos con las Administraciones públicas (N)"
		,null
		,"sd({4700,4708,471,472})"
		,"C0050"
		,NORMAL)
	,BA158(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C158"}
		,"Accionistas (socios) por desembolsos exigidos (N, A, P)"
		,null
		,"sd({5580})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA159(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C159"}
		,"Otros deudores (A, P)"
		,null
		,"sd({44,460,470,471,472,544})"
		,"C0051 || C0052"
		,NORMAL)
	,BA160(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C160"}
		,"Inversiones en empresas del grupo y asociadas a corto plazo (N, A, P)"
		,"BA161+BA162+BA163+BA164+BA165+BA166+BA167"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA161(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C161"}
		,"Instrumentos de patrimonio (N, A, P)"
		,null
		,"sd({5303,5304})-sa({5393,5394,593})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA162(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C162"}
		,"Créditos a empresas (N)"
		,null
		,"sd({5323,5324,5343,5344})-sa({5953,5954})"
		,"C0050"
		,NORMAL)
	,BA163(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C163"}
		,"Valores representativos de deuda (N)"
		,null
		,"sd({5313,5314,5333,5334})-sa({5943,5944})"
		,"C0050"
		,NORMAL)
	,BA164(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C164"}
		,"Derivados (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA165(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C165"}
		,"Otros activos financieros (N)"
		,null
		,"sd({5353,5354,5523,5524})"
		,"C0050"
		,NORMAL)
	,BA166(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C166"}
		,"Otras inversiones (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA167(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C167"}
		,"Resto (A, P)"
		,null
		,null
		,"C0051 || C0052"
		,NORMAL)
	,BA168(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C168"}
		,"Inversiones financieras a corto plazo (N, A, P)"
		,"BA169+BA170+BA171+BA172+BA173+BA174+BA175"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA169(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C169"}
		,"Instrumentos de patrimonio (N, A, P)"
		,null
		,"sd({5305,540})-sa({5395,549})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BA170(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C170"}
		,"Créditos a empresas (N)"
		,null
		,"sd({5325,5345,542,543,547})-sa({5955,598})"
		,"C0050"
		,NORMAL)
	,BA171(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C171"}
		,"Valores representativos de deuda (N)"
		,null
		,"sd({5315,5335,541,546})-sa({5945,597})"
		,"C0050"
		,NORMAL)
	,BA172(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C172"}
		,"Derivados (N)"
		,null
		,"sd({5590,5593})"
		,"C0050"
		,NORMAL)
	,BA173(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C173"}
		,"Otros activos financieros (N)"
		,null
		,"sd({5355,545,548,5525,565,566})+sdPositivo({551})"
		,"C0050"
		,NORMAL)
	,BA174(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C174"}
		,"Otras inversiones (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BA175(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C175"}
		,"Resto (A, P)"
		,null
		,null
		,"C0051 || C0052"
		,NORMAL)
	,BA176(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C176"}
		,"Periodificaciones a corto plazo (N, A, P)"
		,null
		,"sd({480,567})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA177(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C177"}
		,"Efectivo y otros activos líquidos equivalentes (N, A, P)"
		,"BA178+BA179"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BA178(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C178"}
		,"Tesorería (N)"
		,null
		,"sd({570,571,572,573,574,575})"
		,"C0050"
		,NORMAL)
	,BA179(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C179"}
		,"Otros activos líquidos equivalentes (N)"
		,null
		,"sd({576})"
		,"C0050"
		,NORMAL)
	,BA180(BALANCE_ACTIVE
		,new String[]{null,null,null,null,"C180"}
		,"TOTAL ACTIVO (N, A, P)"
		,"BA101 + BA136"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP185(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C185"}
		,"PATRIMONIO NETO (N, A, P)"
		,"BP186 + BP202 + BP208 + BP209"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP186(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C186"}
		,"Fondos propios (N, A, P)"
		,"BP187 + BP190 + BP191 + BP194 + BP195 + BP198 + BP199 + BP200 + BP201"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP187(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C187"}
		,"Capital (N, A, P)"
		,"BP188 + BP189"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP188(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C188"}
		,"Capital escriturado (N, A, P)"
		,null
		,"sa({100,101,102})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP189(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C189"}
		,"(Capital no exigido) (N, A, P)"
		,null
		,"sd({1030,1040})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP190(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C190"}
		,"Prima de emisión (N, A, P)"
		,null
		,"sa({110})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP191(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C191"}
		,"Reservas (N, A, P)"
		,"BP192+BP193+BP702"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP192(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C192"}
		,"Legal y estatutarias (N)"
		,null
		,"sa({112,1141})"
		,"C0050"
		,NORMAL)
	,BP193(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C193"}
		,"Otras reservas (N)"
		,null
		,"sa({113,1140,1142,1143,1144,115,119})"
		,"C0050"
		,NORMAL)
	,BP702(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C702"}
		,"Reservas de revalorización (Ley 16/2012 de 27 de diciembre) (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BP194(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C194"}
		,"(Acciones y participaciones en patrimonio propias) (N, A, P)"
		,null
		,"sd({108,109})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP195(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C195"}
		,"Resultados de ejercicios anteriores (N, A, P)"
		,"BP196+BP197"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP196(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C196"}
		,"Remanente (N)"
		,null
		,"sa({120})"
		,"C0050"
		,NORMAL)
	,BP197(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C197"}
		,"(Resultados negativos de ejercicios anteriores) (N)"
		,null
		,"sa({121})"
		,"C0050"
		,NORMAL)
	,BP198(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C198"}
		,"Otras aportaciones	 de socios (N, A, P)"
		,null
		,"sa({118})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP199(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C199"}
		,"Resultado del ejercicio (N, A, P)"
		,null
		,"sa({129})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP200(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C200"}
		,"(Dividendo a cuenta) (N, A, P)"
		,null
		,"sd({557})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP201(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C201"}
		,"Otros instrumentos de patrimonio neto (N, A)"
		,null
		,"sa({111})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,BP202(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C202"}
		,"Ajustes por cambio de valor (N, A)"
		,"BP203+BP204+BP205+BP206+BP207"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,BP203(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C203"}
		,"Activos financieros disponibles para la venta (N)"
		,null
		,"sa({133})"
		,"C0050"
		,NORMAL)
	,BP204(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C204"}
		,"Operaciones de cobertura (N)"
		,null
		,"sa({1340})"
		,"C0050"
		,NORMAL)
	,BP205(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C205"}
		,"Activos no corrientes y pasivos vinculados, mantenidos para la venta (N)"
		,null
		,"sa({136})"
		,"C0050"
		,NORMAL)
	,BP206(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C206"}
		,"Diferencia de conversión (N)"
		,null
		,"sa({135})"
		,"C0050"
		,NORMAL)
	,BP207(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C207"}
		,"Otros (N)"
		,null
		,"sa({137})"
		,"C0050"
		,NORMAL)
	,BP208(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C208"}
		,"Ajustes en patrimonio neto (P)"
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,BP209(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C209"}
		,"Subvenciones, donaciones y legados recibidos (N, A, P)"
		,null
		,"sa({130,131,132})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP210(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C210"}
		,"PASIVO NO CORRIENTE (N, A, P)"
		,"BP211 + BP216 + BP223 + BP224 + BP225 + BP226 + BP227"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP211(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C211"}
		,"Provisiones a largo plazo (N, A, P)"
		,"BP212+BP213+BP214+BP215"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP212(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C212"}
		,"Obligaciones por prestaciones a largo plazo al personal (N)"
		,null
		,"sa({140})"
		,"C0050"
		,NORMAL)
	,BP213(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C213"}
		,"Actuaciones medioambientales (N)"
		,null
		,"sa({145})"
		,"C0050"
		,NORMAL)
	,BP214(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C214"}
		,"Provisiones por reestructuración (N)"
		,null
		,"sa({146})"
		,"C0050"
		,NORMAL)
	,BP215(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C215"}
		,"Otras provisiones (N)"
		,null
		,"sa({141,142,143,147})"
		,"C0050"
		,NORMAL)
	,BP216(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C216"}
		,"Deudas a largo plazo (N, A, P)"
		,"BP217+BP218+BP219+BP220+BP221+BP222"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP217(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C217"}
		,"Obligaciones y otros valores negociables (N)"
		,null
		,"sa({177,178,179})"
		,"C0050"
		,NORMAL)
	,BP218(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C218"}
		,"Deudas con entidades de crédito (N, A, P)"
		,null
		,"sa({1605,170})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP219(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C219"}
		,"Acreedores por arrendamiento financiero (N, A, P)"
		,null
		,"sa({1625,174})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP220(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C220"}
		,"Derivados (N)"
		,null
		,"sa({176})"
		,"C0050"
		,NORMAL)
	,BP221(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C221"}
		,"Otros pasivos financieros (N)"
		,null
		,"sa({1615,1635,171,172,173,175,180,185,189})"
		,"C0050"
		,NORMAL)
	,BP222(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C222"}
		,"Otras deudas a largo plazo (A, P)"
		,null
		,"sa({1615,1635,171,172,173,175,176,177,178,179,180,185,189})"
		,"C0051 || C0052"
		,NORMAL)
	,BP223(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C223"}
		,"Deudas con empresas del grupo y asociadas a largo plazo (N, A, P)"
		,null
		,"sa({1603,1604,1613,1614,1623,1624,1633,1634})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP224(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C224"}
		,"Pasivos por impuesto diferido (N, A, P)"
		,null
		,"sa({479})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP225(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C225"}
		,"Periodificaciones a largo plazo (N, A, P)"
		,null
		,"sa({181})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP226(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C226"}
		,"Acreedores comerciales no corrientes (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP227(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C227"}
		,"Deuda con características especiales a largo plazo (N, A, P)"
		,null
		,"sa({15})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP228(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C228"}
		,"PASIVO CORRIENTE (N, A, P)"
		,"BP229 + BP230 + BP231 + BP238 + BP239 + BP250 + BP251"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP229(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C229"}
		,"Pasivos vinculados con activos no corr. mantenidos para la venta(N, A)"
		,null
		,"sa({585,586,587,588,589})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,BP230(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C230"}
		,"Provisiones a corto plazo (N, A, P)"
		,"BP703+BP704"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP703(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C703"}
		,"Provisiones por derechos de emisión de gases de efecto invernadero (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,BP704(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C704"}
		,"Otras provisiones (N)"
		,null
		,"sa({499,529})"
		,"C0050"
		,NORMAL)
	,BP231(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C231"}
		,"Deudas a corto plazo (N, A, P)"
		,"BP232+BP233+BP234+BP235+BP236+BP237"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP232(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C232"}
		,"Obligaciones y otros valores negociables (N)"
		,null
		,"sa({500,501,505,506})"
		,"C0050"
		,NORMAL)
	,BP233(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C233"}
		,"Deudas con entidades de crédito (N, A, P)"
		,null
		,"sa({5105,520,527})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP234(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C234"}
		,"Acreedores por arrendamiento financiero (N, A, P)"
		,null
		,"sa({5125,524})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP235(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C235"}
		,"Derivados (N)"
		,null
		,"sa({5595,5598})"
		,"C0050"
		,NORMAL)
	,BP236(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C236"}
		,"Otros pasivos financieros (N)"
		,null
		,"sa({194,509,5115,5135,5145,521,522,523,525,526,528,5525,555,5565,5566,560,561,569})+saPositivo({551})-sd({1034,1044,190,192})"
		,"C0050"
		,NORMAL)
	,BP237(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C237"}
		,"Otras deudas a corto plazo (A, P)"
		,null
		,"sa({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,525,555,5565,5566,5595,5598,560,561,569})+saPositivo({551})-sd({1034,1044,190,192})"
		,"C0051 || C0052"
		,NORMAL)
	,BP238(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C238"}
		,"Deudas con empresas del grupo y asociadas a corto plazo (N, A, P)"
		,null
		,"sa({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP239(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C239"}
		,"Acreedores comerciales y otras cuentas a pagar (N, A, P)"
		,"BP240+BP241+BP242+(C0050?(BP243+BP244+BP245+BP246+BP247+BP248):(BP249))"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP240(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C240"}
		,"Proveedores (N, A, P)"
		,null
		,"sa({400,401,405})-sd({406})"
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP241(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C241"}
		,"Proveedores a largo plazo (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP242(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C242"}
		,"Proveedores a corto plazo (N, A, P)"
		,null
		,null
		,"C0050 || C0051 || C0052"
		,NORMAL)
	,BP243(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C243"}
		,"Proveedores, empresas del grupo y asociadas (N)"
		,null
		,"sa({403,404})"
		,"C0050"
		,NORMAL)
	,BP244(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C244"}
		,"Acreedores varios (N)"
		,null
		,"sa({41})"
		,"C0050"
		,NORMAL)
	,BP245(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C245"}
		,"Personal (remuneraciones pendientes de pago) (N)"
		,null
		,"sa({465,466})"
		,"C0050"
		,NORMAL)
	,BP246(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C246"}
		,"Pasivos por impuesto corriente (N)"
		,null
		,"sa({4752})"
		,"C0050"
		,NORMAL)
	,BP247(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C247"}
		,"Otras deudas con las Administraciones públicas (N)"
		,null
		,"sa({4750,4751,4758,476,477})"
		,"C0050"
		,NORMAL)
	,BP248(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C248"}
		,"Anticipos de clientes (N)"
		,null
		,"sa({438})"
		,"C0050"
		,NORMAL)
	,BP249(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C249"}
		,"Otros acreedores (A, P)"
		,null
		,"sa({41,438,465,466,475,476,477})"
		,"C0051 || C0052"
		,NORMAL)
	,BP250(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C250"}
		,"Periodificaciones a corto plazo (N, A, P)"
		,null
		,"sa({485,568})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP251(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C251"}
		,"Deuda con características especiales a corto plazo (N, A, P)"
		,null
		,"sa({502,507})"
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,BP252(BALANCE_PASIVE
		,new String[]{null,null,null,null,"C252"}
		,"TOTAL PATRIMONIO NETO Y PASIVO (N, A, P)"
		,"BP185 + BP210 + BP228"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,PG001(PYG
		,new String[]{null,null,null,null,"C255"}
		,"Importe neto de la cifra de negocios (N, A, P)"
		,null
		,"sa({700,701,702,703,704,705,706,708,709})+PG705+PG706+PG707+PG708"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG002(PYG
		,new String[]{null,null,null,null,"C256"}
		,"Ventas (N)"
		,null
		,"sa({700,701,702,703,704,706,708,709})"
		,"C0055"
		,NORMAL)
	,PG003(PYG
		,new String[]{null,null,null,null,"C257"}
		,"Prestaciones de servicios (N)"
		,null
		,"sa({705})"
		,"C0055"
		,NORMAL)
	,PG705(PYG
		,new String[]{null,null,null,null,"C705"}
		,"Ingresos de carácter financiero de las sociedades holding (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG706(PYG
		,new String[]{null,null,null,null,"C706"}
		,"De participaciones en instrumentos de patrimonio (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG707(PYG
		,new String[]{null,null,null,null,"C707"}
		,"De valores negociables y otros instrumentos financieros (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG708(PYG
		,new String[]{null,null,null,null,"C708"}
		,"Resto (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG004(PYG
		,new String[]{null,null,null,null,"C258"}
		,"Variación de existencias de productos terminados y en curso de fabricación (N, A, P)"
		,null
		,"sa({71,7930,6930})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG005(PYG
		,new String[]{null,null,null,null,"C259"}
		,"Trabajos realizados por la empresa para su activo (N, A, P)"
		,null
		,"sa({73})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG006(PYG
		,new String[]{null,null,null,null,"C260"}
		,"Aprovisionamientos (N, A, P)"
		,null
		,"sa({606,608,609,61,7931,7932,7933,600,601,602,607,6931,6932,6933})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG007(PYG
		,new String[]{null,null,null,null,"C261"}
		,"Consumo de mercaderías (N)"
		,null
		,"sa({6060,6080,6090,610,600})"
		,"C0055"
		,NORMAL)
	,PG008(PYG
		,new String[]{null,null,null,null,"C262"}
		,"Consumo de materias primas y otras materias consumibles (N)"
		,null
		,"sa({6061,6062,6081,6082,6091,6092,611,612,601,602})"
		,"C0055"
		,NORMAL)
	,PG009(PYG
		,new String[]{null,null,null,null,"C263"}
		,"Trabajos realizados por otras empresas (N)"
		,null
		,"sa({607})"
		,"C0055"
		,NORMAL)
	,PG010(PYG
		,new String[]{null,null,null,null,"C264"}
		,"Deterioro de mercaderías, materias primas y otros aprovisionamientos (N)"
		,null
		,"sa({7931,7932,7933,6931,6932,6933})"
		,"C0055"
		,NORMAL)
	,PG011(PYG
		,new String[]{null,null,null,null,"C265"}
		,"Otros ingresos de explotación (N, A, P)"
		,null
		,"sa({740,747,75})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG012(PYG
		,new String[]{null,null,null,null,"C266"}
		,"Ingresos accesorios y otros de gestión corriente (N, A, P)"
		,null
		,"sa({75})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG013(PYG
		,new String[]{null,null,null,null,"C267"}
		,"Ingresos por arrendamientos (N, A, P)"
		,null
		,"sa({752})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG014(PYG
		,new String[]{null,null,null,null,"C268"}
		,"Resto (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG015(PYG
		,new String[]{null,null,null,null,"C269"}
		,"Subvenciones de explotación incorporadas al resultado del ejercicio (N, A, P)"
		,null
		,"sa({740,747})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG016(PYG
		,new String[]{null,null,null,null,"C270"}
		,"Gastos de personal (N, A, P)"
		,null
		,"sa({7950,7957,640,641,6450,642,643,649,644,6457})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG017(PYG
		,new String[]{null,null,null,null,"C271"}
		,"Sueldos y salarios (N, A, P)"
		,null
		,"sa({640})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG018(PYG
		,new String[]{null,null,null,null,"C273"}
		,"Indemnizaciones (N, A, P)"
		,null
		,"sa({641})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG019(PYG
		,new String[]{null,null,null,null,"C274"}
		,"Seguridad Social a cargo de la empresa (N, A, P)"
		,null
		,"sa({642})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG020(PYG
		,new String[]{null,null,null,null,"C275"}
		,"Retribuciones a largo plazo mediante sistemas de aportaciones o prestación definida (N, A, P)"
		,null
		,"sa({643})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG021(PYG
		,new String[]{null,null,null,null,"C276"}
		,"Retribuciones mediante instrumentos de patrimonio (N, A, P)"
		,null
		,"sa({6450})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG022(PYG
		,new String[]{null,null,null,null,"C277"}
		,"Otros gastos sociales (N, A, P)"
		,null
		,"sa({649})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG023(PYG
		,new String[]{null,null,null,null,"C278"}
		,"Provisiones (N, A)"
		,null
		,"sa({7950,7957,644,6457})"
		,"C0053 || C0054"
		,NORMAL)
	,PG024(PYG
		,new String[]{null,null,null,null,"C279"}
		,"Otros gastos de explotación (N, A, P)"
		,null
		,"sa({636,639,794,7954,62,631,634,65,694,695})+PG709"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG025(PYG
		,new String[]{null,null,null,null,"C280"}
		,"Servicios exteriores (N)"
		,null
		,"sa({62})"
		,"C0055"
		,NORMAL)
	,PG026(PYG
		,new String[]{null,null,null,null,"C281"}
		,"Tributos (N)"
		,null
		,"sa({636,639,631,634})"
		,"C0055"
		,NORMAL)
	,PG027(PYG
		,new String[]{null,null,null,null,"C282"}
		,"Pérdidas, deterioro y variación de provisiones por operaciones comerciales (N)"
		,null
		,"sa({794,7954,650,694,695})"
		,"C0055"
		,NORMAL)
	,PG028(PYG
		,new String[]{null,null,null,null,"C283"}
		,"Otros gastos de gestión corriente (N)"
		,null
		,"sa({651,659})"
		,"C0055"
		,NORMAL)
	,PG709(PYG
		,new String[]{null,null,null,null,"C709"}
		,"Gastos por emisión de gases de efecto invernadero (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG029(PYG
		,new String[]{null,null,null,null,"C284"}
		,"Amortización del inmovilizado (N, A, P)"
		,null
		,"sa({68})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG030(PYG
		,new String[]{null,null,null,null,"C285"}
		,"Imputación de subvenciones de inmovilizado no financiero y otras (N, A, P)"
		,null
		,"sa({746})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG031(PYG
		,new String[]{null,null,null,null,"C286"}
		,"Excesos de provisiones (N, A, P)"
		,null
		,"sa({7951,7952,7955,7956})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG032(PYG
		,new String[]{null,null,null,null,"C287"}
		,"Deterioro y resultado por enajenaciones del inmovilizado (N, A, P)"
		,null
		,"sa({770,771,772,790,791,792,670,671,672,690,691,692})+PG710"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG033(PYG
		,new String[]{null,null,null,null,"C288"}
		,"Deterioro y pérdidas (N, A, P)"
		,null
		,"sa({790,791,792,690,691,692})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG034(PYG
		,new String[]{null,null,null,null,"C289"}
		,"Deterioros (N, A, P)"
		,null
		,"sa({690,691,692})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG035(PYG
		,new String[]{null,null,null,null,"C290"}
		,"Reversión de deterioros (N, A, P)"
		,null
		,"sa({790,791,792})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG036(PYG
		,new String[]{null,null,null,null,"C291"}
		,"Resultados por enajenaciones y otras (N, A, P)"
		,null
		,"sa({770,771,772,670,671,672})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG037(PYG
		,new String[]{null,null,null,null,"C292"}
		,"Beneficios (N, A, P)"
		,null
		,"sa({770,771,772})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG038(PYG
		,new String[]{null,null,null,null,"C293"}
		,"Pérdidas (N, A, P)"
		,null
		,"sa({670,671,672})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG710(PYG
		,new String[]{null,null,null,null,"C710"}
		,"Deterioro y resultados por enajenaciones del inmovilizado de las sociedades holding (N)"
		,null
		,null
		,"C0053"
		,NORMAL)
	,PG039(PYG
		,new String[]{null,null,null,null,"C294"}
		,"Diferencia negativa de combinaciones de negocio (N, A)"
		,null
		,"sa({774})"
		,"C0053 || C0054"
		,TITLE_ENABLED)
	,PG040(PYG
		,new String[]{null,null,null,null,"C295"}
		,"Otros resultados (N, A, P)"
		,null
		,"sa({778,678})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG041(PYG
		,new String[]{null,null,null,null,"C296"}
		,"RESULTADO DE EXPLOTACIÓN (N, A, P)"
		,"PG001 + PG004 + PG005 + PG006 + PG011 + PG016 + PG024 + PG029 + PG030 + PG031 + PG032 + PG039 + PG040"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG042(PYG
		,new String[]{null,null,null,null,"C297"}
		,"Ingresos financieros (N, A, P)"
		,null
		,"sa({746,760,761,762,767,769})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG043(PYG
		,new String[]{null,null,null,null,"C298"}
		,"De participaciones en instrumentos de patrimonio (N, A, P)"
		,"PG044 + PG045"
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG044(PYG
		,new String[]{null,null,null,null,"C299"}
		,"En empresas del grupo y asociadas (N, A, P)"
		,null
		,"sa({7600,7601})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG045(PYG
		,new String[]{null,null,null,null,"C300"}
		,"En terceros (N, A, P)"
		,null
		,"sa({7602,7603})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG046(PYG
		,new String[]{null,null,null,null,"C301"}
		,"De valores negociables y otros instrumentos financieros (N, A, P)"
		,"PG047 + PG048"
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG047(PYG
		,new String[]{null,null,null,null,"C302"}
		,"De empresas del grupo y asociadas (N, A, P)"
		,null
		,"sa({7610,7611,76200,76201,76210,76211})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG048(PYG
		,new String[]{null,null,null,null,"C303"}
		,"De terceros (N, A, P)"
		,null
		,"sa({7612,7613,76202,76203,76212,76213,767,769})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG049(PYG
		,new String[]{null,null,null,null,"C304"}
		,"Imputación de subvenciones, donaciones y legados de carácter financiero (N, A, P)"
		,null
		,"sa({746})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG050(PYG
		,new String[]{null,null,null,null,"C305"}
		,"Gastos financieros (N, A, P)"
		,null
		,"sa({660,661,662,664,665,669})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG051(PYG
		,new String[]{null,null,null,null,"C306"}
		,"Por deudas con empresas del grupo y asociadas (N, A, P)"
		,null
		,"sa({6610,6611,6615,6616,6620,6621,6640,6641,6650,6651,6654,6655})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG052(PYG
		,new String[]{null,null,null,null,"C307"}
		,"Por deudas con terceros (N, A, P)"
		,null
		,"sa({6612,6613,6617,6618,6622,6623,6624,6642,6643,6652,6653,6656,6657,669})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG053(PYG
		,new String[]{null,null,null,null,"C308"}
		,"Por actualización de provisiones (N, A, P)"
		,null
		,"sa({660})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG054(PYG
		,new String[]{null,null,null,null,"C309"}
		,"Variación del valor razonable en instrumentos financieros (N, A, P)"
		,null
		,"sa({763,663})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG055(PYG
		,new String[]{null,null,null,null,"C310"}
		,"Cartera de negociación y otros (N)"
		,null
		,"sa({7630,7631,7633,6630,6631,6633})"
		,"C0055"
		,NORMAL)
	,PG056(PYG
		,new String[]{null,null,null,null,"C311"}
		,"Imputación al resultado del ejercicio por activos financieros disponibles para la venta (N)"
		,null
		,"sa({7632,6632})"
		,"C0055"
		,NORMAL)
	,PG057(PYG
		,new String[]{null,null,null,null,"C312"}
		,"Diferencias de cambio (N, A, P)"
		,null
		,"sa({768,668})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG058(PYG
		,new String[]{null,null,null,null,"C313"}
		,"Deterioro y resultado por enajenación de instrumentos financieros (N, A, P)"
		,null
		,"sa({766,773,775,796,797,798,799,666,667,673,675,696,697,698,699})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG059(PYG
		,new String[]{null,null,null,null,"C314"}
		,"Deterioros y pérdidas (N, A, P)"
		,null
		,"sa({796,797,798,799,696,697,698,699})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG060(PYG
		,new String[]{null,null,null,null,"C315"}
		,"Deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG061(PYG
		,new String[]{null,null,null,null,"C316"}
		,"Deterioros, Otras empresas (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG062(PYG
		,new String[]{null,null,null,null,"C317"}
		,"Reversión de deterioros, empresas del grupo y asociadas a largo plazo (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG063(PYG
		,new String[]{null,null,null,null,"C318"}
		,"Reversión de deterioros, otras empresas (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG064(PYG
		,new String[]{null,null,null,null,"C319"}
		,"Resultados por enajenación y otras (N, A, P)"
		,null
		,"sa({766,773,775,666,667,673,675})"
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG065(PYG
		,new String[]{null,null,null,null,"C320"}
		,"Beneficios, empresas del grupo y asociadas a largo plazo (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG066(PYG
		,new String[]{null,null,null,null,"C321"}
		,"Beneficios, otras empresas (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG067(PYG
		,new String[]{null,null,null,null,"C322"}
		,"Pérdidas, empresas del grupo y asociadas a largo plazo (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG068(PYG
		,new String[]{null,null,null,null,"C323"}
		,"Pérdidas, otras empresas (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG069(PYG
		,new String[]{null,null,null,null,"C329"}
		,"Otros ingresos y gastos de carácter financiero (N, A, P)"
		,"PG070 + PG071 + PG072"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG070(PYG
		,new String[]{null,null,null,null,"C330"}
		,"Incorporación al activo de gastos financieros (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG071(PYG
		,new String[]{null,null,null,null,"C331"}
		,"Ingresos financieros derivados de convenios de acreedores (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG072(PYG
		,new String[]{null,null,null,null,"C332"}
		,"Resto de ingresos y gastos (N, A, P)"
		,null
		,null
		,"C0053 || C0054 || C0055"
		,NORMAL)
	,PG073(PYG
		,new String[]{null,null,null,null,"C324"}
		,"RESULTADO FINANCIERO (N, A, P)"
		,"PG042 + PG050 + PG054 + PG057 + PG058 + PG069"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG074(PYG
		,new String[]{null,null,null,null,"C325"}
		,"RESULTADO ANTES DE IMPUESTOS (N, A, P)"
		,"PG041 + PG073"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG075(PYG
		,new String[]{null,null,null,null,"C326"}
		,"Impuestos sobre beneficios (N, A, P)"
		,null
		,"sa({6301,638,6300,633})"
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG076(PYG
		,new String[]{null,null,null,null,"C327"}
		,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS (N, A, P) (2)"
		,"PG074 + PG075"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,PG077(PYG
		,new String[]{null,null,null,null,"C328"}
		,"RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES INTERRUMPIDAS NETO DE IMPUESTOS (N)"
		,null
		,null
		,"C0053"
		,TITLE_ENABLED)
	,PG078(PYG
		,new String[]{null,null,null,null,"C500"}
		,"RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS (N, A, P)"
		,"PG076 + PG077"
		,null
		,"C0053 || C0054 || C0055"
		,TITLE_ENABLED)
	,T0001(ECPN_INCOME
		,new String[]{null,null,null,null,"C500"}
		,"RESULTADO DE LA CUENTA DE PÉRDIDAS Y GANANCIAS (N, A)"
		,"PG078"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0002(ECPN_INCOME
		,new String[]{null,null,null,null,"C336"}
		,"Por valoración de instrumentos financieros (N, A)"
		,null
		,"sa({900,991,992})+sd({800,89})+T0004"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0003(ECPN_INCOME
		,new String[]{null,null,null,null,"C337"}
		,"Activos financieros disponibles para la venta (N)"
		,null
		,"sa({900,991,992})+sd({800,89})"
		,"C0050"
		,NORMAL)
	,T0004(ECPN_INCOME
		,new String[]{null,null,null,null,"C338"}
		,"Otros ingresos/gastos (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,T0005(ECPN_INCOME
		,new String[]{null,null,null,null,"C339"}
		,"Por coberturas de flujos de efectivo (N, A)"
		,null
		,"sa({910})+sd({810})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0006(ECPN_INCOME
		,new String[]{null,null,null,null,"C340"}
		,"Subvenciones, donaciones y legados recibidos (N, A)"
		,null
		,"sa({94})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0007(ECPN_INCOME
		,new String[]{null,null,null,null,"C341"}
		,"Por ganancias y pérdidas actuariales y otros ajustes (N, A)"
		,null
		,"sa({95})+sd({85})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0008(ECPN_INCOME
		,new String[]{null,null,null,null,"C342"}
		,"Por activos no corrientes y pasivos vinculados, mantenidos pra la venta (N, A)"
		,null
		,"sa({900})+sd({860})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0009(ECPN_INCOME
		,new String[]{null,null,null,null,"C343"}
		,"Diferencias de conversión (N, A)"
		,null
		,"sa({920})+sd({820})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0010(ECPN_INCOME
		,new String[]{null,null,null,null,"C344"}
		,"Efecto impositivo (N, A)"
		,null
		,"sa({8301,834,835,838})+sd({8300,833})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0011(ECPN_INCOME
		,new String[]{null,null,null,null,"C345"}
		,"Total ingresos y gastos imputados directamente en el patrimonio neto (N, A)"
		,"T0002 + T0005 + T0006 + T0007 + T0008 + T0009 + T0010"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0012(ECPN_INCOME
		,new String[]{null,null,null,null,"C346"}
		,"Por valoración de instrumentos financieros (N, A)"
		,null
		,"sa({902,993,994})+sd({802})+T0014"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0013(ECPN_INCOME
		,new String[]{null,null,null,null,"C347"}
		,"Activos financieros disponibles para la venta (N)"
		,null
		,"sa({902,993,994})+sd({802})"
		,"C0050"
		,NORMAL)
	,T0014(ECPN_INCOME
		,new String[]{null,null,null,null,"C348"}
		,"Otros ingresos/gastos (N)"
		,null
		,null
		,"C0050"
		,NORMAL)
	,T0015(ECPN_INCOME
		,new String[]{null,null,null,null,"C349"}
		,"Por coberturas de flujo de efectivo (N, A)"
		,null
		,"sa({912})+sd({812})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0016(ECPN_INCOME
		,new String[]{null,null,null,null,"C350"}
		,"Subvenciones, donaciones y legados recibidos (N, A)"
		,null
		,"sd({84})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0017(ECPN_INCOME
		,new String[]{null,null,null,null,"C351"}
		,"Por activos no corrientes y pasivos vinculados, mantenidos para la venta (N, A)"
		,null
		,"sa({902})+sd({862})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0018(ECPN_INCOME
		,new String[]{null,null,null,null,"C352"}
		,"Diferencias de conversión (N, A)"
		,null
		,"sa({921})+sd({821})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0019(ECPN_INCOME
		,new String[]{null,null,null,null,"C353"}
		,"Efecto impositivo (N, A)"
		,null
		,"sa({8301})+sd({836,837})"
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0020(ECPN_INCOME
		,new String[]{null,null,null,null,"C354"}
		,"Total transferencias a la cuenta de pérdidas y ganancias (N, A)"
		,"T0012 + T0015 + T0016 + T0017 + T0018 + T0019"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,T0021(ECPN_INCOME
		,new String[]{null,null,null,null,"C355"}
		,"TOTAL DE INGRESOS Y GASTOS RECONOCIDOS (N, A)"
		,"T0001 + T0011 + T0020"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC001(ECPN_CHANGE
		,new String[]{null,null,null,null,"C380"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC002(ECPN_CHANGE
		,new String[]{null,null,null,null,"C381"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC003(ECPN_CHANGE
		,new String[]{null,null,null,null,"C382"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC004(ECPN_CHANGE
		,new String[]{null,null,null,null,"C383"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC005(ECPN_CHANGE
		,new String[]{null,null,null,null,"C384"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC006(ECPN_CHANGE
		,new String[]{null,null,null,null,"C385"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC007(ECPN_CHANGE
		,new String[]{null,null,null,null,"C386"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC134(ECPN_CHANGE
		,new String[]{null,null,null,null,"C387"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC135(ECPN_CHANGE
		,new String[]{null,null,null,null,"C388"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC136(ECPN_CHANGE
		,new String[]{null,null,null,null,"C389"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC137(ECPN_CHANGE
		,new String[]{null,null,null,null,"C390"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC138(ECPN_CHANGE
		,new String[]{null,null,null,null,"C391"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC139(ECPN_CHANGE
		,new String[]{null,null,null,null,"C392"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC140(ECPN_CHANGE
		,new String[]{null,null,null,null,"C393"}
		,null
		,"TC001+TC002+TC003+TC004+TC005+TC006+TC007+TC134+TC135+TC136+TC137+TC138+TC139"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC008(ECPN_CHANGE
		,new String[]{null,null,null,null,"C394"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC009(ECPN_CHANGE
		,new String[]{null,null,null,null,"C395"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC010(ECPN_CHANGE
		,new String[]{null,null,null,null,"C396"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC011(ECPN_CHANGE
		,new String[]{null,null,null,null,"C397"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC012(ECPN_CHANGE
		,new String[]{null,null,null,null,"C398"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC013(ECPN_CHANGE
		,new String[]{null,null,null,null,"C399"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC014(ECPN_CHANGE
		,new String[]{null,null,null,null,"C400"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC141(ECPN_CHANGE
		,new String[]{null,null,null,null,"C401"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC142(ECPN_CHANGE
		,new String[]{null,null,null,null,"C402"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC143(ECPN_CHANGE
		,new String[]{null,null,null,null,"C403"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC144(ECPN_CHANGE
		,new String[]{null,null,null,null,"C404"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC145(ECPN_CHANGE
		,new String[]{null,null,null,null,"C405"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC146(ECPN_CHANGE
		,new String[]{null,null,null,null,"C406"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC147(ECPN_CHANGE
		,new String[]{null,null,null,null,"C407"}
		,null
		,"TC008+TC009+TC010+TC011+TC012+TC013+TC014+TC141+TC142+TC143+TC144+TC145+TC146"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC015(ECPN_CHANGE
		,new String[]{null,null,null,null,"C408"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC016(ECPN_CHANGE
		,new String[]{null,null,null,null,"C409"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC017(ECPN_CHANGE
		,new String[]{null,null,null,null,"C410"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC018(ECPN_CHANGE
		,new String[]{null,null,null,null,"C411"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC019(ECPN_CHANGE
		,new String[]{null,null,null,null,"C412"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC020(ECPN_CHANGE
		,new String[]{null,null,null,null,"C413"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC021(ECPN_CHANGE
		,new String[]{null,null,null,null,"C414"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC148(ECPN_CHANGE
		,new String[]{null,null,null,null,"C415"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC149(ECPN_CHANGE
		,new String[]{null,null,null,null,"C416"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC150(ECPN_CHANGE
		,new String[]{null,null,null,null,"C417"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC151(ECPN_CHANGE
		,new String[]{null,null,null,null,"C418"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC152(ECPN_CHANGE
		,new String[]{null,null,null,null,"C419"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC153(ECPN_CHANGE
		,new String[]{null,null,null,null,"C420"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC154(ECPN_CHANGE
		,new String[]{null,null,null,null,"C421"}
		,null
		,"TC015+TC016+TC017+TC018+TC019+TC020+TC021+TC148+TC149+TC150+TC151+TC152+TC153"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC022(ECPN_CHANGE
		,new String[]{null,null,null,null,"C422"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC023(ECPN_CHANGE
		,new String[]{null,null,null,null,"C423"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC024(ECPN_CHANGE
		,new String[]{null,null,null,null,"C424"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC025(ECPN_CHANGE
		,new String[]{null,null,null,null,"C425"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC026(ECPN_CHANGE
		,new String[]{null,null,null,null,"C426"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC027(ECPN_CHANGE
		,new String[]{null,null,null,null,"C427"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC028(ECPN_CHANGE
		,new String[]{null,null,null,null,"C428"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC155(ECPN_CHANGE
		,new String[]{null,null,null,null,"C429"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC156(ECPN_CHANGE
		,new String[]{null,null,null,null,"C430"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC157(ECPN_CHANGE
		,new String[]{null,null,null,null,"C431"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC158(ECPN_CHANGE
		,new String[]{null,null,null,null,"C432"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC159(ECPN_CHANGE
		,new String[]{null,null,null,null,"C433"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC160(ECPN_CHANGE
		,new String[]{null,null,null,null,"C434"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC161(ECPN_CHANGE
		,new String[]{null,null,null,null,"C435"}
		,null
		,"TC022+TC023+TC024+TC025+TC026+TC027+TC028+TC155+TC156+TC157+TC158+TC159+TC160"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC029(ECPN_CHANGE
		,new String[]{null,null,null,null,"C436"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC030(ECPN_CHANGE
		,new String[]{null,null,null,null,"C437"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC031(ECPN_CHANGE
		,new String[]{null,null,null,null,"C438"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC032(ECPN_CHANGE
		,new String[]{null,null,null,null,"C439"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC033(ECPN_CHANGE
		,new String[]{null,null,null,null,"C440"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC034(ECPN_CHANGE
		,new String[]{null,null,null,null,"C441"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC035(ECPN_CHANGE
		,new String[]{null,null,null,null,"C442"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC162(ECPN_CHANGE
		,new String[]{null,null,null,null,"C443"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC163(ECPN_CHANGE
		,new String[]{null,null,null,null,"C444"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC164(ECPN_CHANGE
		,new String[]{null,null,null,null,"C445"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC165(ECPN_CHANGE
		,new String[]{null,null,null,null,"C446"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC166(ECPN_CHANGE
		,new String[]{null,null,null,null,"C448"}
		,null
		,null
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC167(ECPN_CHANGE
		,new String[]{null,null,null,null,"C449"}
		,null
		,"TC029+TC030+TC031+TC032+TC033+TC034+TC035+TC162+TC163+TC164+TC165+TC166"
		,null
		,"C0050 || C0051"
		,TITLE_ENABLED)
	,TC036(ECPN_CHANGE
		,new String[]{null,null,null,null,"C450"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC037(ECPN_CHANGE
		,new String[]{null,null,null,null,"C451"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC038(ECPN_CHANGE
		,new String[]{null,null,null,null,"C452"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC039(ECPN_CHANGE
		,new String[]{null,null,null,null,"C453"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC040(ECPN_CHANGE
		,new String[]{null,null,null,null,"C454"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC041(ECPN_CHANGE
		,new String[]{null,null,null,null,"C455"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC042(ECPN_CHANGE
		,new String[]{null,null,null,null,"C456"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC168(ECPN_CHANGE
		,new String[]{null,null,null,null,"C457"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC169(ECPN_CHANGE
		,new String[]{null,null,null,null,"C458"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC170(ECPN_CHANGE
		,new String[]{null,null,null,null,"C461"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC171(ECPN_CHANGE
		,new String[]{null,null,null,null,"C462"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC172(ECPN_CHANGE
		,new String[]{null,null,null,null,"C463"}
		,null
		,"TC036+TC037+TC038+TC039+TC040+TC041+TC042+TC168+TC169+TC170+TC171"
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC043(ECPN_CHANGE
		,new String[]{null,null,null,null,"C464"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC044(ECPN_CHANGE
		,new String[]{null,null,null,null,"C465"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC045(ECPN_CHANGE
		,new String[]{null,null,null,null,"C466"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC046(ECPN_CHANGE
		,new String[]{null,null,null,null,"C467"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC047(ECPN_CHANGE
		,new String[]{null,null,null,null,"C468"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC048(ECPN_CHANGE
		,new String[]{null,null,null,null,"C469"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC049(ECPN_CHANGE
		,new String[]{null,null,null,null,"C470"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC173(ECPN_CHANGE
		,new String[]{null,null,null,null,"C471"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC174(ECPN_CHANGE
		,new String[]{null,null,null,null,"C472"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC175(ECPN_CHANGE
		,new String[]{null,null,null,null,"C475"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC176(ECPN_CHANGE
		,new String[]{null,null,null,null,"C476"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC177(ECPN_CHANGE
		,new String[]{null,null,null,null,"C477"}
		,null
		,"TC043+TC044+TC045+TC046+TC047+TC048+TC049+TC173+TC174+TC175+TC176"
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC050(ECPN_CHANGE
		,new String[]{null,null,null,null,"C478"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC051(ECPN_CHANGE
		,new String[]{null,null,null,null,"C479"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC052(ECPN_CHANGE
		,new String[]{null,null,null,null,"C480"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC053(ECPN_CHANGE
		,new String[]{null,null,null,null,"C481"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC054(ECPN_CHANGE
		,new String[]{null,null,null,null,"C482"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC055(ECPN_CHANGE
		,new String[]{null,null,null,null,"C483"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC056(ECPN_CHANGE
		,new String[]{null,null,null,null,"C484"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC178(ECPN_CHANGE
		,new String[]{null,null,null,null,"C485"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC179(ECPN_CHANGE
		,new String[]{null,null,null,null,"C486"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC180(ECPN_CHANGE
		,new String[]{null,null,null,null,"C489"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC181(ECPN_CHANGE
		,new String[]{null,null,null,null,"C490"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC182(ECPN_CHANGE
		,new String[]{null,null,null,null,"C491"}
		,null
		,"TC050+TC051+TC052+TC053+TC054+TC055+TC056+TC178+TC179+TC180+TC181"
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC057(ECPN_CHANGE
		,new String[]{null,null,null,null,"C492"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC058(ECPN_CHANGE
		,new String[]{null,null,null,null,"C493"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC059(ECPN_CHANGE
		,new String[]{null,null,null,null,"C494"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC060(ECPN_CHANGE
		,new String[]{null,null,null,null,"C495"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC061(ECPN_CHANGE
		,new String[]{null,null,null,null,"C496"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC062(ECPN_CHANGE
		,new String[]{null,null,null,null,"C497"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC063(ECPN_CHANGE
		,new String[]{null,null,null,null,"C498"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC183(ECPN_CHANGE
		,new String[]{null,null,null,null,"C499"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC184(ECPN_CHANGE
		,new String[]{null,null,null,null,"C502"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC185(ECPN_CHANGE
		,new String[]{null,null,null,null,"C503"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC186(ECPN_CHANGE
		,new String[]{null,null,null,null,"C504"}
		,null
		,null
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC187(ECPN_CHANGE
		,new String[]{null,null,null,null,"C505"}
		,null
		,"TC057+TC058+TC059+TC060+TC061+TC062+TC063+TC183+TC184+TC185+TC186"
		,null
		,"C0052"
		,TITLE_ENABLED)
	,TC064(ECPN_CHANGE
		,new String[]{null,null,null,null,"C506"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC065(ECPN_CHANGE
		,new String[]{null,null,null,null,"C507"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC066(ECPN_CHANGE
		,new String[]{null,null,null,null,"C508"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC067(ECPN_CHANGE
		,new String[]{null,null,null,null,"C509"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC068(ECPN_CHANGE
		,new String[]{null,null,null,null,"C510"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC069(ECPN_CHANGE
		,new String[]{null,null,null,null,"C511"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC070(ECPN_CHANGE
		,new String[]{null,null,null,null,"C512"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC188(ECPN_CHANGE
		,new String[]{null,null,null,null,"C513"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC189(ECPN_CHANGE
		,new String[]{null,null,null,null,"C514"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC190(ECPN_CHANGE
		,new String[]{null,null,null,null,"C515"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC191(ECPN_CHANGE
		,new String[]{null,null,null,null,"C516"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC192(ECPN_CHANGE
		,new String[]{null,null,null,null,"C517"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC193(ECPN_CHANGE
		,new String[]{null,null,null,null,"C518"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC194(ECPN_CHANGE
		,new String[]{null,null,null,null,"C519"}
		,null
		,"TC064+TC065+TC066+TC067+TC068+TC069+TC070+TC188+TC189+TC190+TC191+TC192+TC193"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC071(ECPN_CHANGE
		,new String[]{null,null,null,null,"C520"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC072(ECPN_CHANGE
		,new String[]{null,null,null,null,"C521"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC073(ECPN_CHANGE
		,new String[]{null,null,null,null,"C522"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC074(ECPN_CHANGE
		,new String[]{null,null,null,null,"C523"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC075(ECPN_CHANGE
		,new String[]{null,null,null,null,"C524"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC076(ECPN_CHANGE
		,new String[]{null,null,null,null,"C525"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC077(ECPN_CHANGE
		,new String[]{null,null,null,null,"C526"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC195(ECPN_CHANGE
		,new String[]{null,null,null,null,"C527"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC196(ECPN_CHANGE
		,new String[]{null,null,null,null,"C528"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC197(ECPN_CHANGE
		,new String[]{null,null,null,null,"C529"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC198(ECPN_CHANGE
		,new String[]{null,null,null,null,"C530"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC199(ECPN_CHANGE
		,new String[]{null,null,null,null,"C531"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC200(ECPN_CHANGE
		,new String[]{null,null,null,null,"C532"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC201(ECPN_CHANGE
		,new String[]{null,null,null,null,"C533"}
		,null
		,"TC071+TC072+TC073+TC074+TC075+TC076+TC077+TC195+TC196+TC197+TC198+TC199+TC200"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC078(ECPN_CHANGE
		,new String[]{null,null,null,null,"C534"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC079(ECPN_CHANGE
		,new String[]{null,null,null,null,"C535"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC080(ECPN_CHANGE
		,new String[]{null,null,null,null,"C536"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC081(ECPN_CHANGE
		,new String[]{null,null,null,null,"C537"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC082(ECPN_CHANGE
		,new String[]{null,null,null,null,"C538"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC083(ECPN_CHANGE
		,new String[]{null,null,null,null,"C539"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC084(ECPN_CHANGE
		,new String[]{null,null,null,null,"C540"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC202(ECPN_CHANGE
		,new String[]{null,null,null,null,"C541"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC203(ECPN_CHANGE
		,new String[]{null,null,null,null,"C542"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC204(ECPN_CHANGE
		,new String[]{null,null,null,null,"C543"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC205(ECPN_CHANGE
		,new String[]{null,null,null,null,"C544"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC206(ECPN_CHANGE
		,new String[]{null,null,null,null,"C545"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC207(ECPN_CHANGE
		,new String[]{null,null,null,null,"C546"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC208(ECPN_CHANGE
		,new String[]{null,null,null,null,"C547"}
		,null
		,"TC078+TC079+TC080+TC081+TC082+TC083+TC084+TC202+TC203+TC204+TC205+TC206+TC207"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC085(ECPN_CHANGE
		,new String[]{null,null,null,null,"C548"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC086(ECPN_CHANGE
		,new String[]{null,null,null,null,"C549"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC087(ECPN_CHANGE
		,new String[]{null,null,null,null,"C550"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC088(ECPN_CHANGE
		,new String[]{null,null,null,null,"C551"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC089(ECPN_CHANGE
		,new String[]{null,null,null,null,"C552"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC090(ECPN_CHANGE
		,new String[]{null,null,null,null,"C553"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC091(ECPN_CHANGE
		,new String[]{null,null,null,null,"C554"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC209(ECPN_CHANGE
		,new String[]{null,null,null,null,"C555"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC210(ECPN_CHANGE
		,new String[]{null,null,null,null,"C556"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC211(ECPN_CHANGE
		,new String[]{null,null,null,null,"C557"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC212(ECPN_CHANGE
		,new String[]{null,null,null,null,"C558"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC213(ECPN_CHANGE
		,new String[]{null,null,null,null,"C560"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC214(ECPN_CHANGE
		,new String[]{null,null,null,null,"C561"}
		,null
		,"TC085+TC086+TC087+TC088+TC089+TC090+TC091+TC209+TC210+TC211+TC212+TC213"
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC092(ECPN_CHANGE
		,new String[]{null,null,null,null,"C562"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC093(ECPN_CHANGE
		,new String[]{null,null,null,null,"C563"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC094(ECPN_CHANGE
		,new String[]{null,null,null,null,"C564"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC095(ECPN_CHANGE
		,new String[]{null,null,null,null,"C565"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC096(ECPN_CHANGE
		,new String[]{null,null,null,null,"C566"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC097(ECPN_CHANGE
		,new String[]{null,null,null,null,"C567"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC098(ECPN_CHANGE
		,new String[]{null,null,null,null,"C568"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC215(ECPN_CHANGE
		,new String[]{null,null,null,null,"C569"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC216(ECPN_CHANGE
		,new String[]{null,null,null,null,"C570"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC217(ECPN_CHANGE
		,new String[]{null,null,null,null,"C571"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC218(ECPN_CHANGE
		,new String[]{null,null,null,null,"C572"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC219(ECPN_CHANGE
		,new String[]{null,null,null,null,"C574"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC220(ECPN_CHANGE
		,new String[]{null,null,null,null,"C575"}
		,null
		,"TC092+TC093+TC094+TC095+TC096+TC097+TC098+TC215+TC216+TC217+TC218+TC219"
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC099(ECPN_CHANGE
		,new String[]{null,null,null,null,"C576"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC100(ECPN_CHANGE
		,new String[]{null,null,null,null,"C577"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC101(ECPN_CHANGE
		,new String[]{null,null,null,null,"C578"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC102(ECPN_CHANGE
		,new String[]{null,null,null,null,"C579"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC103(ECPN_CHANGE
		,new String[]{null,null,null,null,"C580"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC104(ECPN_CHANGE
		,new String[]{null,null,null,null,"C581"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC105(ECPN_CHANGE
		,new String[]{null,null,null,null,"C582"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC221(ECPN_CHANGE
		,new String[]{null,null,null,null,"C583"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC222(ECPN_CHANGE
		,new String[]{null,null,null,null,"C584"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC223(ECPN_CHANGE
		,new String[]{null,null,null,null,"C585"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC224(ECPN_CHANGE
		,new String[]{null,null,null,null,"C586"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC225(ECPN_CHANGE
		,new String[]{null,null,null,null,"C588"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC226(ECPN_CHANGE
		,new String[]{null,null,null,null,"C589"}
		,null
		,"TC099+TC100+TC101+TC102+TC103+TC104+TC105+TC221+TC222+TC223+TC224+TC225"
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC106(ECPN_CHANGE
		,new String[]{null,null,null,null,"C590"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC107(ECPN_CHANGE
		,new String[]{null,null,null,null,"C591"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC108(ECPN_CHANGE
		,new String[]{null,null,null,null,"C592"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC109(ECPN_CHANGE
		,new String[]{null,null,null,null,"C593"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC110(ECPN_CHANGE
		,new String[]{null,null,null,null,"C594"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC111(ECPN_CHANGE
		,new String[]{null,null,null,null,"C595"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC112(ECPN_CHANGE
		,new String[]{null,null,null,null,"C596"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC227(ECPN_CHANGE
		,new String[]{null,null,null,null,"C597"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC228(ECPN_CHANGE
		,new String[]{null,null,null,null,"C598"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC229(ECPN_CHANGE
		,new String[]{null,null,null,null,"C599"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC230(ECPN_CHANGE
		,new String[]{null,null,null,null,"C600"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC231(ECPN_CHANGE
		,new String[]{null,null,null,null,"C602"}
		,null
		,null
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC232(ECPN_CHANGE
		,new String[]{null,null,null,null,"C603"}
		,null
		,"TC106+TC107+TC108+TC109+TC110+TC111+TC112+TC227+TC228+TC229+TC230+TC231"
		,null
		,"C0050"
		,TITLE_ENABLED)
	,TC113(ECPN_CHANGE
		,new String[]{null,null,null,null,"C604"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC114(ECPN_CHANGE
		,new String[]{null,null,null,null,"C605"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC115(ECPN_CHANGE
		,new String[]{null,null,null,null,"C606"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC116(ECPN_CHANGE
		,new String[]{null,null,null,null,"C607"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC117(ECPN_CHANGE
		,new String[]{null,null,null,null,"C608"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC118(ECPN_CHANGE
		,new String[]{null,null,null,null,"C609"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC119(ECPN_CHANGE
		,new String[]{null,null,null,null,"C610"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC233(ECPN_CHANGE
		,new String[]{null,null,null,null,"C611"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC234(ECPN_CHANGE
		,new String[]{null,null,null,null,"C612"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC235(ECPN_CHANGE
		,new String[]{null,null,null,null,"C613"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC236(ECPN_CHANGE
		,new String[]{null,null,null,null,"C614"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC237(ECPN_CHANGE
		,new String[]{null,null,null,null,"C615"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC238(ECPN_CHANGE
		,new String[]{null,null,null,null,"C616"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC239(ECPN_CHANGE
		,new String[]{null,null,null,null,"C617"}
		,null
		,"TC113+TC114+TC115+TC116+TC117+TC118+TC119+TC233+TC234+TC235+TC236+TC237+TC238"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC120(ECPN_CHANGE
		,new String[]{null,null,null,null,"C618"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC121(ECPN_CHANGE
		,new String[]{null,null,null,null,"C619"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC122(ECPN_CHANGE
		,new String[]{null,null,null,null,"C620"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC123(ECPN_CHANGE
		,new String[]{null,null,null,null,"C621"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC124(ECPN_CHANGE
		,new String[]{null,null,null,null,"C622"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC125(ECPN_CHANGE
		,new String[]{null,null,null,null,"C623"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC126(ECPN_CHANGE
		,new String[]{null,null,null,null,"C624"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC240(ECPN_CHANGE
		,new String[]{null,null,null,null,"C625"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC241(ECPN_CHANGE
		,new String[]{null,null,null,null,"C626"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC242(ECPN_CHANGE
		,new String[]{null,null,null,null,"C627"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC243(ECPN_CHANGE
		,new String[]{null,null,null,null,"C628"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC244(ECPN_CHANGE
		,new String[]{null,null,null,null,"C629"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC245(ECPN_CHANGE
		,new String[]{null,null,null,null,"C630"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC246(ECPN_CHANGE
		,new String[]{null,null,null,null,"C631"}
		,null
		,"TC120+TC121+TC122+TC123+TC124+TC125+TC126+TC240+TC241+TC242+TC243+TC244+TC245"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC700(ECPN_CHANGE
		,new String[]{null,null,null,null,"C715"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC701(ECPN_CHANGE
		,new String[]{null,null,null,null,"C716"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC702(ECPN_CHANGE
		,new String[]{null,null,null,null,"C717"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC703(ECPN_CHANGE
		,new String[]{null,null,null,null,"C718"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC704(ECPN_CHANGE
		,new String[]{null,null,null,null,"C719"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC705(ECPN_CHANGE
		,new String[]{null,null,null,null,"C720"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC706(ECPN_CHANGE
		,new String[]{null,null,null,null,"C721"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC707(ECPN_CHANGE
		,new String[]{null,null,null,null,"C722"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC708(ECPN_CHANGE
		,new String[]{null,null,null,null,"C723"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC709(ECPN_CHANGE
		,new String[]{null,null,null,null,"C724"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC710(ECPN_CHANGE
		,new String[]{null,null,null,null,"C725"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC711(ECPN_CHANGE
		,new String[]{null,null,null,null,"C726"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC712(ECPN_CHANGE
		,new String[]{null,null,null,null,"C727"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC713(ECPN_CHANGE
		,new String[]{null,null,null,null,"C728"}
		,null
		,"TC700+TC701+TC702+TC703+TC704+TC705+TC706+TC707+TC708+TC709+TC710+TC711+TC712"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC714(ECPN_CHANGE
		,new String[]{null,null,null,null,"C729"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC715(ECPN_CHANGE
		,new String[]{null,null,null,null,"C730"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC716(ECPN_CHANGE
		,new String[]{null,null,null,null,"C731"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC717(ECPN_CHANGE
		,new String[]{null,null,null,null,"C732"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC718(ECPN_CHANGE
		,new String[]{null,null,null,null,"C733"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC719(ECPN_CHANGE
		,new String[]{null,null,null,null,"C734"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC720(ECPN_CHANGE
		,new String[]{null,null,null,null,"C735"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC721(ECPN_CHANGE
		,new String[]{null,null,null,null,"C736"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC722(ECPN_CHANGE
		,new String[]{null,null,null,null,"C737"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC723(ECPN_CHANGE
		,new String[]{null,null,null,null,"C738"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC724(ECPN_CHANGE
		,new String[]{null,null,null,null,"C739"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC725(ECPN_CHANGE
		,new String[]{null,null,null,null,"C740"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC726(ECPN_CHANGE
		,new String[]{null,null,null,null,"C741"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC727(ECPN_CHANGE
		,new String[]{null,null,null,null,"C742"}
		,null
		,"TC714+TC715+TC716+TC717+TC718+TC719+TC720+TC721+TC722+TC723+TC724+TC725+TC726"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC127(ECPN_CHANGE
		,new String[]{null,null,null,null,"C632"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC128(ECPN_CHANGE
		,new String[]{null,null,null,null,"C633"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC129(ECPN_CHANGE
		,new String[]{null,null,null,null,"C634"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC130(ECPN_CHANGE
		,new String[]{null,null,null,null,"C635"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC131(ECPN_CHANGE
		,new String[]{null,null,null,null,"C636"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC132(ECPN_CHANGE
		,new String[]{null,null,null,null,"C637"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC133(ECPN_CHANGE
		,new String[]{null,null,null,null,"C638"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC247(ECPN_CHANGE
		,new String[]{null,null,null,null,"C639"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC248(ECPN_CHANGE
		,new String[]{null,null,null,null,"C640"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC249(ECPN_CHANGE
		,new String[]{null,null,null,null,"C641"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC250(ECPN_CHANGE
		,new String[]{null,null,null,null,"C642"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC251(ECPN_CHANGE
		,new String[]{null,null,null,null,"C643"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC252(ECPN_CHANGE
		,new String[]{null,null,null,null,"C644"}
		,null
		,null
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,TC253(ECPN_CHANGE
		,new String[]{null,null,null,null,"C645"}
		,null
		,"TC127+TC128+TC129+TC130+TC131+TC132+TC133+TC247+TC248+TC249+TC250+TC251+TC252"
		,null
		,"C0050 || C0051 || C0052"
		,TITLE_ENABLED)
	,LQ001(LIQUIDATION
		,new String[]{null,null,null,null,"C500"}
		,"Resultado de la cuenta de pérdidas y ganancias"
		,"PG078"
		,null
		,null
		,TITLE_ENABLED)
	,LQ002(LIQUIDATION
		,new String[]{null,null,null,null,"C301"}
		,"Correcciones por Impuesto sobre Sociedades. Aumentos."
		,null
		,null
		,null
		,NORMAL)
	,LQ003(LIQUIDATION
		,new String[]{null,null,null,null,"C302"}
		,"Correcciones por Impuesto sobre Sociedades. Disminuciones."
		,null
		,null
		,null
		,NORMAL)
	,LQ004(LIQUIDATION
		,new String[]{null,null,null,null,"C501"}
		,"Resultado de la cuenta de pérdidas y ganancias antes de Impuesto sobre Sociedades"
		,"LQ001 + LQ002 - LQ003"
		,null
		,null
		,TITLE_ENABLED)
	,I0001(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C303"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0002(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C504"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0003(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C305"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0004(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C307"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0005(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C514"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0006(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C516"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0007(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C309"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0008(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C311"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0009(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C313"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0010(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C315"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0011(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C317"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0012(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C319"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0013(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C321"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0014(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C323"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0015(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C325"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0016(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C327"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0017(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C329"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0018(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C331"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0019(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C333"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0020(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C335"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0021(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C337"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0022(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C339"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0023(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C341"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0024(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C508"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0025(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C510"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0026(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C512"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0027(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C343"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0028(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C184"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0029(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C345"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0030(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C347"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0031(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C349"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0032(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C355"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0033(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C357"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0034(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C359"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0035(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C225"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0036(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C415"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0037(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C361"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0038(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C363"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0039(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C365"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0040(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C367"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0041(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C369"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0042(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C256"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0043(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C373"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0044(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C375"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0045(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C377"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0046(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C379"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0047(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C381"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0048(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C383"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0049(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C385"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0050(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C387"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0051(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C389"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0052(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C250"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0053(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C391"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0054(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C397"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0055(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C403"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0056(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C405"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0057(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C409"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0058(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C411"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0059(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C518"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0060(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C340"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0061(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C351"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0062(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C371"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0063(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C413"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,I0064(CORRECTION_INCREASE
		,new String[]{null,null,null,null,"C417"}
		,null
		,"I0001+I0002+I0003+I0004+I0005+I0006+I0007+I0008+I0009+I0010+I0011+I0012+I0013+I0014+I0015+I0016+I0017+I0018+I0019+I0020+I0021+I0022+I0023+I0024+I0025+I0026+I0027+I0028+I0029+I0030+I0031+I0032+I0033+I0034+I0035+I0036+I0037+I0038+I0039+I0040+I0041+I0042+I0043+I0044+I0045+I0046+I0047+I0048+I0049+I0050+I0051+I0052+I0053+I0054+I0055+I0056+I0057+I0058+I0059+I0060+I0061+I0062+I0063"
		,null
		,null
		,TITLE_ENABLED)
	,D0001(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C304"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0002(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C505"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0003(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C306"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0004(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C308"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0005(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C509"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0006(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C551"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0007(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C310"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0008(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C312"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0009(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C314"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0010(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C316"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0011(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C318"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0012(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C320"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0013(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C322"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0014(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C324"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0015(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C326"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0016(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C328"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0017(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C330"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0018(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C332"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0019(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C334"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0020(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C336"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0021(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C338"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0022(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C342"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0023(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C511"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0024(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C513"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0025(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C346"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0026(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C348"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0027(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C350"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0028(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C352"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0029(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C354"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0030(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C356"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0031(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C358"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0032(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C360"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0033(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C226"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0034(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C416"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0035(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C362"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0036(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C364"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0037(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C370"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0038(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C278"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0039(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C372"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0040(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C374"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0041(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C376"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0042(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C378"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0043(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C380"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0044(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C382"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0045(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C384"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0046(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C386"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0047(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C388"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0048(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C390"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0049(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C251"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0050(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C392"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0051(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C396"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0052(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C398"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0053(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C400"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0054(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C404"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0055(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C406"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0056(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C410"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0057(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C412"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0058(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C519"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0059(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C368"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0060(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C414"}
		,null
		,null
		,null
		,null
		,NORMAL)
	,D0061(CORRECTION_DECREASE
		,new String[]{null,null,null,null,"C418"}
		,null
		,"D0001+D0002+D0003+D0004+D0005+D0006+D0007+D0008+D0009+D0010+D0011+D0012+D0013+D0014+D0015+D0016+D0017+D0018+D0019+D0020+D0021+D0022+D0023+D0024+D0025+D0026+D0027+D0028+D0029+D0030+D0031+D0032+D0033+D0034+D0035+D0036+D0037+D0038+D0039+D0040+D0041+D0042+D0043+D0044+D0045+D0046+D0047+D0048+D0049+D0050+D0051+D0052+D0053+D0054+D0055+D0056+D0057+D0058+D0059+D0060"
		,null
		,null
		,TITLE_ENABLED)
	,LQ005(LIQUIDATION_II
		,new String[]{null,null,null,null,"C578"}
		,"Base imponible de actividades o rentas que tributen en régimen general"
		,"LQ004 + I0064 - D0061"
		,null
		,"C0022"
		,NORMAL)
	,LQ006(LIQUIDATION_II
		,new String[]{null,null,null,null,"C579"}
		,"Base imponible derivada de la aplicación del régimen especial"
		,null
		,null
		,"C0022"
		,NORMAL)
	,LQ007(LIQUIDATION_II
		,new String[]{null,null,null,null,"C550"}
		,"Base imponible antes de la compensación de bases imponibles negativas"
		,"C0022?(LQ004 + I0064 - D0061):((LQ005<=0)?(LQ006):(LQ005+LQ006)):"
		,null
		,null
		,NORMAL)
	,LQ008(LIQUIDATION_II
		,new String[]{null,null,null,null,"C547"}
		,"Compensación de bases imponibles negativas de períodos anteriores"
		,null
		,null
		,null
		,NORMAL)
	,LQ009(LIQUIDATION_II
		,new String[]{null,null,null,null,"C552"}
		,"Base imponible"
		,"LQ007 - LQ008"
		,null
		,null
		,TITLE_ENABLED)
	,LQ010(LIQUIDATION_II
		,new String[]{null,null,null,null,"C553"}
		,"Resultados cooperativos"
		,null
		,null
		,"C0017 || C0018 || C0019"
		,NORMAL)
	,LQ011(LIQUIDATION_II
		,new String[]{null,null,null,null,"C554"}
		,"Resultados extracooperativos"
		,null
		,null
		,"C0017 || C0018 || C0019"
		,NORMAL)
	,LQ012(LIQUIDATION_II
		,new String[]{null,null,null,null,"C555"}
		,"Socios residentes"
		,null
		,null
		,"C0013"
		,NORMAL)
	,LQ013(LIQUIDATION_II
		,new String[]{null,null,null,null,"C556"}
		,"Socios no residentes"
		,null
		,null
		,"C0013"
		,NORMAL)
	,LQ014(LIQUIDATION_II
		,new String[]{null,null,null,null,"C559"}
		,"Base imponible a tipo de gravamen especial"
		,null
		,null
		,"C0015"
		,NORMAL)
	,LQ015(LIQUIDATION_II
		,new String[]{null,null,null,null,"C520"}
		,"Parte de la base imponible del periodo impositivo que tributa al tipos general"
		,null
		,null
		,"C0012"
		,NORMAL)
	,LQ016(LIQUIDATION_II
		,new String[]{null,null,null,null,"C521"}
		,"Parte de la base imponible del periodo impositivo que tributa al tipos especial"
		,null
		,null
		,null
		,NORMAL)
	,LQ017(LIQUIDATION_II
		,new String[]{null,null,null,null,"C522"}
		,"Parte de la base imponible de periodos anteriores que tributa este periodo al tipo especial"
		,null
		,null
		,"C0012"
		,NORMAL)
	,LQ018(LIQUIDATION_II
		,new String[]{null,null,null,null,"C523"}
		,"Parte de la base imponible del periodo impositivo que no tributa en este periodo impositivo (Beneficios no distribuidos)"
		,null
		,null
		,"C0012"
		,NORMAL)
	,LQ019(LIQUIDATION_II
		,new String[]{null,null,null,null,"C558"}
		,"Tipo de gravamen"
		,"(C0001?10.0:(C0002?25.0:(C0003?1.0:(C0004?1.0:(C0005?25.0:((C0006 && !C0034)?25.0:((C0006 &&  C0034)?35.0:((C0006 &&  C056)?20.0:(C0012?30.0:(C0015?4.0:(C0017?20.0:(C0018?20.0:(C0034?35.0:(C0036?25.0:(C0038?30.0:(C0046?30.0:(C0048?0.0:(C0056?20.0:(C0057?20.0:(C0058?25.0:(C0063?15.0:20.0)))))))))))))))))))))"
		,null
		,null
		,NORMAL)
	,LQ020(LIQUIDATION_II
		,new String[]{null,null,null,null,"C560"}
		,"Cuota íntegra previa"
		,null
		,null
		,null
		,NORMAL)
	,LQ021(LIQUIDATION_II
		,new String[]{null,null,null,null,"C561"}
		,"Compensación de cuotas por pérdidas de cooperativas"
		,null
		,null
		,null
		,NORMAL)
	,LQ022(LIQUIDATION_II
		,new String[]{null,null,null,null,"C562"}
		,"Cuota íntegra"
		,"LQ009<=0?0.0:(LQ009 * LQ019 / 100)"
		,null
		,null
		,TITLE_ENABLED)
	,BN001(LIQUIDATION_III
		,new String[]{null,null,null,null,"C567"}
		,"Bonificación por rentas obtenidas en Ceuta y Melilla (art. 33 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN002(LIQUIDATION_III
		,new String[]{null,null,null,null,"C568"}
		,"Bonificaciones actividades exportadoras y de prestación de servicios (art. 34 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN003(LIQUIDATION_III
		,new String[]{null,null,null,null,"C563"}
		,"Bonificación rendimientos por ventas bienes corporales producidos en Canarias (art. 26 Ley 19/1994)"
		,null
		,null
		,null
		,NORMAL)
	,BN004(LIQUIDATION_III
		,new String[]{null,null,null,null,"C566"}
		,"Bonificaciones Sociedades Cooperativas (Ley 20/1990)"
		,null
		,null
		,null
		,NORMAL)
	,BN005(LIQUIDATION_III
		,new String[]{null,null,null,null,"C576"}
		,"Bonificaciones entidades dedicadas al arrendamiento de viviendas (Capítulo III Título VII L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN006(LIQUIDATION_III
		,new String[]{null,null,null,null,"C569"}
		,"Otras bonificaciones"
		,null
		,null
		,null
		,NORMAL)
	,BN007(LIQUIDATION_III
		,new String[]{null,null,null,null,"C570"}
		,"D.I. interna de períodos anteriores aplicada en el ejercicio (art. 30 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN008(LIQUIDATION_III
		,new String[]{null,null,null,null,"C571"}
		,"D.I. interna generada y aplicada en el ejercicio actual (art. 30 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN009(LIQUIDATION_III
		,new String[]{null,null,null,null,"C564"}
		,"Deducciones socios SOCIMI (art. 10 Ley 11/2009)"
		,null
		,null
		,null
		,NORMAL)
	,BN010(LIQUIDATION_III
		,new String[]{null,null,null,null,"C572"}
		,"D.I. internacional de períodos anteriores aplicada en el ejercicio (art. 31 y 32 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN011(LIQUIDATION_III
		,new String[]{null,null,null,null,"C573"}
		,"D.I. internacional generada y aplicada en el ejercicio actual (art. 31 y 32 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN012(LIQUIDATION_III
		,new String[]{null,null,null,null,"C575"}
		,"Transparencia fiscal internacional (art. 107.9 L.I.S.)"
		,null
		,null
		,null
		,NORMAL)
	,BN013(LIQUIDATION_III
		,new String[]{null,null,null,null,"C577"}
		,"D.I. interna intersocietaria al 5/10% (cooperativas)"
		,null
		,null
		,null
		,NORMAL)
	,BN014(LIQUIDATION_III
		,new String[]{null,null,null,null,"C581"}
		,"Bonificaciones empresas navieras en Canarias (art. 76 Ley 19/1994)"
		,null
		,null
		,null
		,NORMAL)
	,BN015(LIQUIDATION_III
		,new String[]{null,null,null,null,"C582"}
		,"Cuota íntegra ajustada positiva"
		,"LQ009<=0?0.0:(LQ022 - (BN001 + BN002 + BN003 + BN004 + BN005 + BN006 + BN007 + BN008 + BN009 + BN010 + BN011 + BN012 + BN013 + BN014))"
		,null
		,null
		,TITLE_ENABLED)
	,BN016(LIQUIDATION_III
		,new String[]{null,null,null,null,"C583"}
		,"Apoyo fiscal a la inversión y otras deducciones"
		,null
		,null
		,null
		,NORMAL)
	,BN017(LIQUIDATION_III
		,new String[]{null,null,null,null,"C585"}
		,"Deducción art. 42 L.I.S. y art. 36 ter Ley 43/95"
		,null
		,null
		,null
		,NORMAL)
	,BN018(LIQUIDATION_III
		,new String[]{null,null,null,null,"C584"}
		,"Deducciones disposición transitoria octava L.I.S."
		,null
		,null
		,null
		,NORMAL)
	,BN019(LIQUIDATION_III
		,new String[]{null,null,null,null,"C588"}
		,"Deducciones con límite del Capítulo IV Título VI L.I.S."
		,null
		,null
		,null
		,NORMAL)
	,BN020(LIQUIDATION_III
		,new String[]{null,null,null,null,"C565"}
		,"Deducción donaciones a entidades sin fines de lucro (Ley 49/2002)"
		,null
		,null
		,null
		,NORMAL)
	,BN021(LIQUIDATION_III
		,new String[]{null,null,null,null,"C390"}
		,"Deducciones Inversión Canarias (Ley 20/1991)"
		,null
		,null
		,null
		,NORMAL)
	,BN022(LIQUIDATION_III
		,new String[]{null,null,null,null,"C399"}
		,"Deducciones específicas de las entidades sometidas a normativa foral"
		,null
		,null
		,null
		,NORMAL)
	,BN023(LIQUIDATION_III
		,new String[]{null,null,null,null,"C592"}
		,"Cuota líquida positiva"
		,"LQ009<=0?0.0:(BN015 - (BN016 + BN017 + BN018 + BN019 + BN020 + BN021 + BN022))"
		,null
		,null
		,TITLE_ENABLED)
	,BN024(LIQUIDATION_III
		,new String[]{null,null,null,null,"C595"}
		,"Retenciones e ingresos a cuenta / pagos a cuenta participaciones I.I.C."
		,null
		,null
		,null
		,NORMAL)
	,BN025(LIQUIDATION_III
		,new String[]{null,null,null,null,"C596"}
		,"Retenciones e ingresos a cuenta / pagos a cuenta participaciones I.I.C. imputados por agrupaciones de interés económico y uniones temporales de empresas"
		,null
		,null
		,null
		,NORMAL)
	,BN026(LIQUIDATION_III
		,new String[]{null,null,null,null,"C597"}
		,"Retenciones sobre los premios de determinadas loterías y apuestas (sólo para períodos impositivos que finalicen a partir de 31/12/2012)"
		,null
		,null
		,null
		,NORMAL)
	,BN027(LIQUIDATION_III
		,new String[]{null,null,null,null,"C599"}
		,"Cuota del ejercicio a ingresar o a devolver (Estado)"
		,"(100 / 100) * (BN023 - BN024 - BN025 - BN026)"
		,null
		,null
		,TITLE_ENABLED)
	,BN029(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C601"}
		,"1er pago fraccionado"
		,null
		,null
		,null
		,NORMAL)
	,BN031(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C603"}
		,"2o pago fraccionado"
		,null
		,null
		,null
		,NORMAL)
	,BN033(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C605"}
		,"3er pago fraccionado"
		,null
		,null
		,null
		,NORMAL)
	,BN035(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C611"}
		,"Cuota diferencial"
		,"BN027 - (BN029 + BN031 + BN033)"
		,null
		,null
		,TITLE_ENABLED)
	,BN037(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C615"}
		,"Incremento por pérdida beneficios fiscales períodos anteriores"
		,null
		,null
		,null
		,NORMAL)
	,BN039(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C633"}
		,"Incremento por incumplimiento de requisitos SOCIMI"
		,null
		,null
		,null
		,NORMAL)
	,BN041(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C617"}
		,"Intereses de demora"
		,null
		,null
		,null
		,NORMAL)
	,BN043(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C619"}
		,"Importe ingreso / devolución efectuada de la declaración originaria"
		,null
		,null
		,null
		,NORMAL)
	,BN045(LIQUIDATION_IV
		,new String[]{null,null,null,null,"C621"}
		,"Líquido a ingresar o a devolver"
		,"BN035 + BN037 + BN039 + BN041 + BN043"
		,null
		,null
		,TITLE_ENABLED)
	,ID001(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C650"}
		,"Pérdidas y ganancias"
		,"PG078"
		,null
		,null
		,NORMAL)
	,ID002(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C651"}
		,"Remanente"
		,"BP196"
		,null
		,null
		,NORMAL)
	,ID003(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C652"}
		,"Reservas"
		,"BP191"
		,null
		,null
		,NORMAL)
	,ID004(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C653"}
		,"Total"
		,"ID001 + ID002 + ID003"
		,null
		,null
		,TITLE_ENABLED)
	,ID005(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C654"}
		,"A reservas"
		,null
		,null
		,null
		,NORMAL)
	,ID006(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C655"}
		,"Intereses aportaciones al capital (Cooperativas)"
		,null
		,null
		,null
		,NORMAL)
	,ID007(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C656"}
		,"A dividendos"
		,null
		,null
		,null
		,NORMAL)
	,ID008(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C658"}
		,"A dotación O.S. (Cajas de ahorro)"
		,null
		,null
		,null
		,NORMAL)
	,ID009(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C659"}
		,"A F.R.O. y dotaciones voluntarias al F.E.P. (Cooperativas)"
		,null
		,null
		,null
		,NORMAL)
	,ID010(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C660"}
		,"A retornos cooperativos (Cooperativas)"
		,null
		,null
		,null
		,NORMAL)
	,ID011(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C662"}
		,"Partícipes (IIC)"
		,null
		,null
		,null
		,NORMAL)
	,ID012(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C664"}
		,"A remanente y otros"
		,null
		,null
		,null
		,NORMAL)
	,ID013(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C665"}
		,"A compensación de pérdidas de ejercicios anteriores"
		,null
		,null
		,null
		,NORMAL)
	,ID014(INCOME_DISTRIBUTION
		,new String[]{null,null,null,null,"C666"}
		,"Total"
		,"ID005 + ID006 + ID007 + ID008 + ID009 + ID010 + ID011 + ID012 + ID013"
		,null
		,null
		,TITLE_ENABLED)
	,LM001(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"175"}
		,"a). Resultado de explotación (signo igual a Cuenta de Pérd. y Gan.)"
		,null
		,null
		,null
		,NORMAL)
	,LM002(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"176"}
		,"b). Amortización del inmovilizado (signo igual a Cuenta de Pérd. y Gan.)"
		,null
		,null
		,null
		,NORMAL)
	,LM003(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"177"}
		,"c). Imputación de subvenciones de inmovilizado no financiero y otras (signo igual a Cuenta de Pérd. y Gan.)"
		,null
		,null
		,null
		,NORMAL)
	,LM004(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"178"}
		,"d). Deterioro y resultado por enajenaciones del inmovilizado (signo igual a Cuenta de Pérd. y Gan.)"
		,null
		,null
		,null
		,NORMAL)
	,LM005(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"179"}
		,"e). Ingresos financieros de participaciones en instrumentos de patrimonio(*) (signo igual a Cuenta de Pérd. y Gan.)"
		,null
		,null
		,null
		,NORMAL)
	,LM006(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"043"}
		,"f). Límite a la deducción de gastos financieros netos (= 30%* [a-b-c-d+e], con un mínimo de 1 millón de euros)"
		,null
		,null
		,null
		,NORMAL)
	,LM007(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"049"}
		,"g). Adición por límite beneficio operativo no aplicado en los cinco ejercicios anteriores"
		,null
		,null
		,null
		,NORMAL)
	,LM008(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"249"}
		,"h). Gastos financieros del período impositivo excluidos aquellos a que se refiere el art. 14.1.h) LIS (**) (sin signo)"
		,null
		,null
		,null
		,NORMAL)
	,LM009(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"252"}
		,"i). Ingresos financieros del período impositivo derivados de la cesión a terceros de capitales propios"
		,null
		,null
		,null
		,NORMAL)
	,LM010(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"253"}
		,"j). Gastos financieros netos del período (= [ h - i ])"
		,null
		,null
		,null
		,NORMAL)
	,LM011(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"254"}
		,"k). Gastos financieros netos del período deducibles (< = [ j ])"
		,null
		,null
		,null
		,NORMAL)
	,LM012(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"255"}
		,"l). Gastos financieros netos del período no deducibles (< = [ j ])"
		,null
		,null
		,null
		,NORMAL)
	,LM013(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"258"}
		,"m). Gastos financieros netos pendientes de deducir de periodos anteriores aplicados"
		,null
		,null
		,null
		,NORMAL)
	,LM014(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"259"}
		,"n). Total gastos financieros netos deducibles en el período (= [k + m], < = [f + g])"
		,null
		,null
		,null
		,NORMAL)
	,LM015(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"260"}
		,"ñ). Total gastos financieros deducibles en el período"
		,null
		,null
		,null
		,NORMAL)
	,LM016(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"969"}
		,"2012 - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM017(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"970"}
		,"2012 - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM018(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"971"}
		,"2012 - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM019(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"261"}
		,"2013 (*) - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM020(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"262"}
		,"2013 (*) - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM021(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"263"}
		,"2013 (*) - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM022(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"264"}
		,"2013 (**) - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM023(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"265"}
		,"2013 (**) - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM024(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"266"}
		,"2013 (**) - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM025(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"267"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM026(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"268"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM027(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"269"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM028(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"503"}
		,"2012 - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM029(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"522"}
		,"2012 - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM030(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"523"}
		,"2012 - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM031(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"270"}
		,"2013 (*) - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM032(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"271"}
		,"2013 (*) - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM033(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"272"}
		,"2013 (*) - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM034(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"273"}
		,"2013 (**) - Importe generado. Pendiente de aplicación a principio del período"
		,null
		,null
		,null
		,NORMAL)
	,LM035(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"274"}
		,"2013 (**) - Aplicado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM036(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"537"}
		,"2013 (**) - Pendiente de aplicación en períodos futuros"
		,null
		,null
		,null
		,NORMAL)
	,LM037(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"538"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM038(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"539"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM039(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"546"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM040(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"173"}
		,"2011 - Integrado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM041(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"227"}
		,"2012 - Integrado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM042(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"291"}
		,"2013 (*) - Integrado en esta liquidación"
		,null
		,null
		,null
		,NORMAL)
	,LM043(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"344"}
		,"Total"
		,null
		,null
		,null
		,NORMAL)
	,LM044(DEDUCIBLE_LIMITATION
		,new String[]{null,null,null,null,"393"}
		,"Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria (**). Importe del crédito exigible"
		,null
		,null
		,null
		,NORMAL)
	;
	
	private OLD_Mod200KeyType type;
	private String[] codes;
	private String description;
	private String computeExpression;
	private String initialExpression;
	private String activeExpression;
	private Mod200KeyBehaviour behaviour;

	private OLD_Mod200Key(OLD_Mod200KeyType type, String[] codes, String description,
			String computeExpression, String initialExpression,
			String activeExpression, Mod200KeyBehaviour behaviour) {
		this.type = type;
		this.codes = codes;
		this.computeExpression = computeExpression;
		this.activeExpression = activeExpression;
		this.initialExpression = initialExpression;
		this.description = description;
		this.behaviour = behaviour;
	}

	public OLD_Mod200KeyType getType() {
		return type;
	}

	public String[] getCodes() {
		return codes;
	}

	public String getDescription() {
		return description;
	}

	public String getComputeExpression() {
		return computeExpression;
	}
	public String getInitialExpression() {
		return initialExpression;
	}

	public Mod200KeyBehaviour getBehaviour() {
		return behaviour;
	}

	public boolean isPresent(Administration administration) {
		boolean retValue = (getCodes() == null) ? false
				: (getCodes()[administration.ordinal()] != null);
		return retValue;
	}

	public String getCode(Administration administration) {
		return (getCodes() == null) ? null : getCodes()[administration
				.ordinal()];
	}

	public String getCode() {
		return this.toString();
	}

	public Object getDefaultValue() {
		return new Double(0);
	}

	public String getActiveExpression() {
		return activeExpression;
	}

	public boolean isTitle() {
		return behaviour == TITLE_ENABLED || behaviour == TITLE_DISABLED;
	}

	public boolean isDisabled() {
		return behaviour == TITLE_DISABLED;
	}
	/*
	public static void main(String[] args) throws IOException {
		String path = "/home/ecastellano/trunk/trunk/aon-gwt-fiscal/src/main/java/com/esferalia/aon/gwt/fiscal/shared/mod200/Mod200Constants2.java";
		PrintWriter writer = new PrintWriter( new FileWriter(path));
		
		writer.println("package com.esferalia.aon.gwt.fiscal.shared.mod200;");
		writer.println();
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.BALANCE_ACTIVE;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.BALANCE_PASIVE;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.PYG;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.ECPN_INCOME;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.ECPN_CHANGE;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.LIQUIDATION;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.CORRECTION_INCREASE;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.CORRECTION_DECREASE;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.LIQUIDATION_II;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.LIQUIDATION_III;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.LIQUIDATION_IV;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.INCOME_DISTRIBUTION;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyType.DEDUCIBLE_LIMITATION;");
		writer.println();
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.NORMAL;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.TITLE_ENABLED;");
		writer.println("import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200KeyBehaviour.TITLE_DISABLED;");
		writer.println();
		writer.println("import java.io.Serializable;");
		writer.println("import java.util.HashMap;");
		writer.println("import java.util.Map;");
		writer.println();
		writer.println("import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;");
		writer.println("import com.google.gwt.user.client.rpc.IsSerializable;");
		writer.println();
		writer.println("public class Mod200Constants {");
		writer.println();
		writer.println("\tpublic static Map<String,String> ACTIVE_EXPRESSION_MAP = new HashMap<String,String>();");
		writer.println("\tpublic static Map<String,String> COMPUTE_EXPRESSION_MAP = new HashMap<String,String>();");
		writer.println("\tpublic static Map<String,String> INITIALIZE_EXPRESSION_MAP = new HashMap<String,String>();");
		writer.println();
		writer.println("\tpublic static enum Mod200Key implements Serializable, IsSerializable {");
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			writer.println("\t\t"+(key.ordinal()>0?",":"") + key.toString() 
				+ "(" 
					+"new String[]{null,null,null,null,\""+key.getCodes()[4]+"\"}"
					+","+(key.getDescription()==null?"null":("\""+key.getDescription()+"\""))
				+ ")");
		}
		writer.println("\t\t;");
		writer.println("\t\tprivate String description;");
		writer.println("\t\tprivate Mod200Key(String description) {");
		writer.println("\t\t\tthis.description = description;");
		writer.println("\t\t}");
		writer.println("\t}");
		writer.println();
		writer.println("\tstatic {");	
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			if (key.getInitialExpression() != null) {
				writer.println("\t\tINITIALIZE_EXPRESSION_MAP.put(" 
								+ "Mod200Key." + key + ".toString()" 
								+",\""
								+key.getInitialExpression()
								+"\");");		
			}
		}
		writer.println("\t}");	

		writer.println();
		writer.println("\tstatic {");	
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			if (key.getComputeExpression() != null) {
				writer.println("\t\tCOMPUTE_EXPRESSION_MAP.put(" 
								+ "Mod200Key." + key + ".toString()" 
								+",\""
								+key.getComputeExpression()
								+"\");");		
			}
		}
		writer.println("\t}");	

		writer.println();
		writer.println("\tstatic {");	
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			if (key.getActiveExpression() != null) {
				writer.println("\t\tACTIVE_EXPRESSION_MAP.put(" 
								+ "Mod200Key." + key + ".toString()" 
								+",\""
								+key.getActiveExpression()
								+"\");");		
			}
		}
		writer.println("\t}");
		writer.println();
		
		for (Mod200KeyType type : Mod200KeyType.values()) {
			writer.print("\tpublic static Mod200Key[] " + type +"_KEYS = new Mod200Key[]{");
			boolean first = true;
			for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
				if (key.getType() == type) {
					writer.print((first?"":",") + "Mod200Key.");
					writer.print(key);
					first = false;
				}
			}
			writer.println("};");
		}
		writer.println();
		
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			if (key.isDisabled()) {
				writer.println("\t\tDISABLED_KEYS_MAP.put(" 
						+ "Mod200Key." + key + ".toString()" 
						+",true"+"}"
						+");");		
			}
		}
		
		for (OLD_Mod200Key key : OLD_Mod200Key.values()) {
			if (key.isTitle()) {
				writer.println("\t\tTITLE_KEYS_MAP.put(" 
						+ "Mod200Key." + key + ".toString()" 
						+",true"+"}"
						+");");
			}
		}

		writer.println("}");
		writer.println();
		writer.flush();
		writer.close();
	}
	*/
}
