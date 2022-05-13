package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

public enum Mod2002021CorrectionKey implements Serializable, IMod200KeysProvider {

	 C01(Mod2002021Key.I0355,Mod2002021Key.D0356, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2504, Mod2002021KeyDC.DC2501, Mod2002021KeyDC.DC2502, Mod2002021KeyDC.DC2503, Mod2002021KeyDC.DC2505}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2509, Mod2002021KeyDC.DC2506, Mod2002021KeyDC.DC2507, Mod2002021KeyDC.DC2508, Mod2002021KeyDC.DC2510}, "Cambio de criterios contables (art. 11.3.2\u00BA LIS)") 
	,C02(Mod2002021Key.I0357,Mod2002021Key.D0358, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2514, Mod2002021KeyDC.DC2511, Mod2002021KeyDC.DC2512, Mod2002021KeyDC.DC2513, Mod2002021KeyDC.DC2515}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2519, Mod2002021KeyDC.DC2516, Mod2002021KeyDC.DC2517, Mod2002021KeyDC.DC2518, Mod2002021KeyDC.DC2520}, "Operaciones a plazos (art. 11.4 LIS)")
    ,C03(Mod2002021Key.I0359,Mod2002021Key.D0360, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2524, Mod2002021KeyDC.DC2521, Mod2002021KeyDC.DC2522, Mod2002021KeyDC.DC2523, Mod2002021KeyDC.DC2525}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2529, Mod2002021KeyDC.DC2526, Mod2002021KeyDC.DC2527, Mod2002021KeyDC.DC2528, Mod2002021KeyDC.DC2530}, "Reversi\u00F3n del deterioro del valor de los elementos patrimoniales (art. 11.6 LIS)") 
	,C04(Mod2002021Key.I0225,Mod2002021Key.D0226, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2534, Mod2002021KeyDC.DC2531, Mod2002021KeyDC.DC2532, Mod2002021KeyDC.DC2533, Mod2002021KeyDC.DC2535}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2539, Mod2002021KeyDC.DC2536, Mod2002021KeyDC.DC2537, Mod2002021KeyDC.DC2538, Mod2002021KeyDC.DC2540}, "Rentas negativas arts. 11.9, 11.10 LIS")
	,C05(Mod2002021Key.I1514,Mod2002021Key.D0272, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2544, Mod2002021KeyDC.DC2541, Mod2002021KeyDC.DC2542, Mod2002021KeyDC.DC2543, Mod2002021KeyDC.DC2545}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2549, Mod2002021KeyDC.DC2546, Mod2002021KeyDC.DC2547, Mod2002021KeyDC.DC2548, Mod2002021KeyDC.DC2550}, "Ajustes por rentas derivadas de operaciones con quita o espera (art. 11.13 LIS) ")
	,C06(Mod2002021Key.I0361,Mod2002021Key.D0362, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2554, Mod2002021KeyDC.DC2551, Mod2002021KeyDC.DC2552, Mod2002021KeyDC.DC2553, Mod2002021KeyDC.DC2555}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2559, Mod2002021KeyDC.DC2556, Mod2002021KeyDC.DC2557, Mod2002021KeyDC.DC2558, Mod2002021KeyDC.DC2560}, "Otras diferencias de imputaci\u00F3n temporal de ingresos y gastos (art. 11 LIS)")
	,C07(Mod2002021Key.I0303,Mod2002021Key.D0304, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2564, Mod2002021KeyDC.DC2561, Mod2002021KeyDC.DC2562, Mod2002021KeyDC.DC2563, Mod2002021KeyDC.DC2565}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2569, Mod2002021KeyDC.DC2566, Mod2002021KeyDC.DC2567, Mod2002021KeyDC.DC2568, Mod2002021KeyDC.DC2570}, "Diferencias entre amortizaci\u00F3n contable y fiscal (art. 12.1 LIS)")
	,C08(null 				,Mod2002021Key.D0505, null                                                                                                                                   	   , new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2579, null                  , null                  , Mod2002021KeyDC.DC2578, Mod2002021KeyDC.DC2580}, "Deducci\u00F3n del 30% importe gastos de amortiz. contable (excluidas emp. reducida dimensi\u00F3n) (art. 7 Ley 16/2012)")
	,C09(Mod2002021Key.I1005,Mod2002021Key.D1006, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2584, Mod2002021KeyDC.DC2581, Mod2002021KeyDC.DC2582, Mod2002021KeyDC.DC2583, Mod2002021KeyDC.DC2585}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2589, Mod2002021KeyDC.DC2586, Mod2002021KeyDC.DC2587, Mod2002021KeyDC.DC2588, Mod2002021KeyDC.DC2590}, "Amortizaci\u00F3n del inmovilizado intangible y fondo de comercio (art. 12.2 LIS) y amortizaci\u00F3n de la DT 13\u00AA.1 LIS") 
	,C10(Mod2002021Key.I0305,Mod2002021Key.D0306, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2594, Mod2002021KeyDC.DC2591, Mod2002021KeyDC.DC2592, Mod2002021KeyDC.DC2593, Mod2002021KeyDC.DC2595}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2599, Mod2002021KeyDC.DC2596, Mod2002021KeyDC.DC2597, Mod2002021KeyDC.DC2598, Mod2002021KeyDC.DC2600}, "Amortizaci\u00F3n de inmovilizado afecto a actividades de investigaci\u00F3n y desarrollo (art. 12.3 b) LIS)")
	,C11(Mod2002021Key.I0307,Mod2002021Key.D0308, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2604, Mod2002021KeyDC.DC2601, Mod2002021KeyDC.DC2602, Mod2002021KeyDC.DC2603, Mod2002021KeyDC.DC2605}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2609, Mod2002021KeyDC.DC2606, Mod2002021KeyDC.DC2607, Mod2002021KeyDC.DC2608, Mod2002021KeyDC.DC2610}, "Libertad de amortizaci\u00F3n de gastos de investigaci\u00F3n y desarrollo (art. 12.3 c) LIS)")
	,C12(Mod2002021Key.I1003,Mod2002021Key.D1004, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2614, Mod2002021KeyDC.DC2611, Mod2002021KeyDC.DC2612, Mod2002021KeyDC.DC2613, Mod2002021KeyDC.DC2615}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2619, Mod2002021KeyDC.DC2616, Mod2002021KeyDC.DC2617, Mod2002021KeyDC.DC2618, Mod2002021KeyDC.DC2620}, "Libertad de amortizaci\u00F3n inmovilizado material nuevo (art. 12.3 e) LIS)")
	,C13(Mod2002021Key.I0309,Mod2002021Key.D0310, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2624, Mod2002021KeyDC.DC2621, Mod2002021KeyDC.DC2622, Mod2002021KeyDC.DC2623, Mod2002021KeyDC.DC2625}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2629, Mod2002021KeyDC.DC2626, Mod2002021KeyDC.DC2627, Mod2002021KeyDC.DC2628, Mod2002021KeyDC.DC2630}, "Otros supuestos de libertad de amortizaci\u00F3n (art. 12.3 a) y d) y DA 16\u00AA LIS)")
	,C14(Mod2002021Key.I0514,Mod2002021Key.D0509, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2634, Mod2002021KeyDC.DC2631, Mod2002021KeyDC.DC2632, Mod2002021KeyDC.DC2633, Mod2002021KeyDC.DC2635}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2639, Mod2002021KeyDC.DC2636, Mod2002021KeyDC.DC2637, Mod2002021KeyDC.DC2638, Mod2002021KeyDC.DC2640}, "Libertad de amortizaci\u00F3n con mantenimiento de empleo (RDL 6/2010 y DT 13\u00AA.2)") 
	,C15(Mod2002021Key.I0516,Mod2002021Key.D0551, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2644, Mod2002021KeyDC.DC2641, Mod2002021KeyDC.DC2642, Mod2002021KeyDC.DC2643, Mod2002021KeyDC.DC2645}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2649, Mod2002021KeyDC.DC2646, Mod2002021KeyDC.DC2647, Mod2002021KeyDC.DC2648, Mod2002021KeyDC.DC2650}, "Libertad de amortizaci\u00F3n sin mantenimiento de empleo (RDL 13/2010 y DT 13\u00AA.2)")
	,C16(Mod2002021Key.I0321,Mod2002021Key.D0322, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2654, Mod2002021KeyDC.DC2651, Mod2002021KeyDC.DC2652, Mod2002021KeyDC.DC2653, Mod2002021KeyDC.DC2655}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2659, Mod2002021KeyDC.DC2656, Mod2002021KeyDC.DC2657, Mod2002021KeyDC.DC2658, Mod2002021KeyDC.DC2660}, "P\u00E9rdidas por deterioro del art. 13.1 LIS no afectada por el art. 11.12 ni por DT 33\u00AA.1 LIS")
	,C17(Mod2002021Key.I0415,Mod2002021Key.D0211, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2664, Mod2002021KeyDC.DC2661, Mod2002021KeyDC.DC2662, Mod2002021KeyDC.DC2663, Mod2002021KeyDC.DC2665}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2669, Mod2002021KeyDC.DC2666, Mod2002021KeyDC.DC2667, Mod2002021KeyDC.DC2668, Mod2002021KeyDC.DC2670}, "P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS) a los que se refiere el art. 11.12 y DT 33\u00AA.1 LIS.") 
	,C18(Mod2002021Key.I0331,Mod2002021Key.D0332, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2674, Mod2002021KeyDC.DC2671, Mod2002021KeyDC.DC2672, Mod2002021KeyDC.DC2673, Mod2002021KeyDC.DC2675}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2679, Mod2002021KeyDC.DC2676, Mod2002021KeyDC.DC2677, Mod2002021KeyDC.DC2678, Mod2002021KeyDC.DC2680}, "P\u00E9rdidas por deterioro de IM, inversiones inmobiliarias e II, incluido el fondo de comercio (art. 13.2 a) y DT 15 LIS)")
	,C19(Mod2002021Key.I0325,Mod2002021Key.D0326, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2684, Mod2002021KeyDC.DC2681, Mod2002021KeyDC.DC2682, Mod2002021KeyDC.DC2683, Mod2002021KeyDC.DC2685}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2689, Mod2002021KeyDC.DC2686, Mod2002021KeyDC.DC2687, Mod2002021KeyDC.DC2688, Mod2002021KeyDC.DC2690}, "Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (art. 13.2 b) LIS)")
//	,C20(Mod2002021Key.I1518,Mod2002021Key.D0394, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2694, Mod2002021KeyDC.DC2691, Mod2002021KeyDC.DC2692, Mod2002021KeyDC.DC2693, Mod2002021KeyDC.DC2695}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2699, Mod2002021KeyDC.DC2696, Mod2002021KeyDC.DC2697, Mod2002021KeyDC.DC2698, Mod2002021KeyDC.DC2700}, "Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (DT 16\u00AA.1 y 2 LIS)")
	,C21(Mod2002021Key.I0327,Mod2002021Key.D0328, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2714, Mod2002021KeyDC.DC2711, Mod2002021KeyDC.DC2712, Mod2002021KeyDC.DC2713, Mod2002021KeyDC.DC2715}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2719, Mod2002021KeyDC.DC2716, Mod2002021KeyDC.DC2717, Mod2002021KeyDC.DC2718, Mod2002021KeyDC.DC2720}, "P\u00E9rdidas por deterioro de valores representativos de deuda (art. 13.2 c) LIS y DT 15\u00AA LIS)")
	,C22(Mod2002021Key.I0416,Mod2002021Key.D0543, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2724, Mod2002021KeyDC.DC2721, Mod2002021KeyDC.DC2722, Mod2002021KeyDC.DC2723, Mod2002021KeyDC.DC2725}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2729, Mod2002021KeyDC.DC2726, Mod2002021KeyDC.DC2727, Mod2002021KeyDC.DC2728, Mod2002021KeyDC.DC2730}, "Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS)")
	,C23(Mod2002021Key.I0335,Mod2002021Key.D0336, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2734, Mod2002021KeyDC.DC2731, Mod2002021KeyDC.DC2732, Mod2002021KeyDC.DC2733, Mod2002021KeyDC.DC2735}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2739, Mod2002021KeyDC.DC2736, Mod2002021KeyDC.DC2737, Mod2002021KeyDC.DC2738, Mod2002021KeyDC.DC2740}, "Gastos y provisiones por pensiones no afectados por el art. 11.12 LIS (arts. 14.1, 14.6 y 14.8 LIS)")
	,C24(Mod2002021Key.I0337,Mod2002021Key.D0338, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2744, Mod2002021KeyDC.DC2741, Mod2002021KeyDC.DC2742, Mod2002021KeyDC.DC2743, Mod2002021KeyDC.DC2745}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2749, Mod2002021KeyDC.DC2746, Mod2002021KeyDC.DC2747, Mod2002021KeyDC.DC2748, Mod2002021KeyDC.DC2750}, "Otras provisiones no deducibles fiscalmente (art. 14 LIS) no afectadas por el art. 11.12 LIS")
	,C25(null				,Mod2002021Key.D0368, null                                                                                                                                         , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2756, null                  , null                  , null                  }, "Subvenciones p\u00FAblicas incluidas en el resultado del ejercicio, no integrables en la base imponible (art. 14.8 LIS)")
	,C26(Mod2002021Key.I1002,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2761, null                  , null                  , null                  }, null                                                                                                                                   , "Gastos no deducibles por considerarse retribuci\u00F3n de fondos propios (art. 15 a) LIS)")
	,C27(Mod2002021Key.I1815,null               , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2771, null                  , null                  , null                  }, null                                                                                                                                   , "Multas, sanciones y otros (art. 15 c) LIS)")
	,C28(Mod2002021Key.I0343,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2781, null                  , null                  , null                  }, null                                                                                                                                   , "P\u00E9rdidas del juego (art. 15 d) LIS)")
	,C29(Mod2002021Key.I0339,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2791, null                  , null                  , null                  }, null                                                                                                                                   , "Gastos por donativos y liberalidades (art. 15 e) LIS)")
	,C30(Mod2002021Key.I1816,null               , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2801, null                  , null                  , null                  }, null                                                                                                                                   , "Gastos de actuaciones contrarias al ordenamiento jur\u00EDdico (art. 15 f) LIS)")
	,C31(Mod2002021Key.I0341,Mod2002021Key.D0342, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2814, Mod2002021KeyDC.DC2811, Mod2002021KeyDC.DC2812, Mod2002021KeyDC.DC2813, Mod2002021KeyDC.DC2815}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2819, Mod2002021KeyDC.DC2816, Mod2002021KeyDC.DC2817, Mod2002021KeyDC.DC2818, Mod2002021KeyDC.DC2820}, "Operaciones realizadas con jurisdicciones no cooperativas (art. 15 g) LIS)")
	,C32(Mod2002021Key.I0508,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2821, null                  , null                  , null                  }, null                                                                                                                                   , "Gastos financieros derivados de deudas con entidades del grupo (art. 15 h) LIS)")
	,C33(Mod2002021Key.I1817,null               , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2831, null                  , null                  , null                  }, null                                                                                                                                   , "Gastos derivados de la extinci\u00F3n de la relaci\u00F3n laboral o mercantil (art. 15 i) LIS)")	
	,C34(Mod2002021Key.I2469,Mod2002021Key.D2470, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2574, Mod2002021KeyDC.DC2571, Mod2002021KeyDC.DC2572, Mod2002021KeyDC.DC2573, Mod2002021KeyDC.DC2575}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2754, Mod2002021KeyDC.DC2751, Mod2002021KeyDC.DC2752, Mod2002021KeyDC.DC2753, Mod2002021KeyDC.DC2755}, "Asimetr\u00EDas h\u00EDbridas (art. 15 bis LIS)")	
	,C35(Mod2002021Key.I1807,Mod2002021Key.D1811, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2854, Mod2002021KeyDC.DC2851, Mod2002021KeyDC.DC2852, Mod2002021KeyDC.DC2853, Mod2002021KeyDC.DC2855}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2859, Mod2002021KeyDC.DC2856, Mod2002021KeyDC.DC2857, Mod2002021KeyDC.DC2858, Mod2002021KeyDC.DC2860}, "P\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (art. 15 k) LIS)")
	,C36(Mod2002021Key.I1808,Mod2002021Key.D1812, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2864, Mod2002021KeyDC.DC2861, Mod2002021KeyDC.DC2862, Mod2002021KeyDC.DC2863, Mod2002021KeyDC.DC2865}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2869, Mod2002021KeyDC.DC2866, Mod2002021KeyDC.DC2867, Mod2002021KeyDC.DC2868, Mod2002021KeyDC.DC2870}, "Disminuci\u00F3n de valor originada por criterio de valor razonable (art. 15 l) LIS)")
	,C37(Mod2002021Key.I1813,Mod2002021Key.D1814, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2874, Mod2002021KeyDC.DC2871, Mod2002021KeyDC.DC2872, Mod2002021KeyDC.DC2873, Mod2002021KeyDC.DC2875}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2879, Mod2002021KeyDC.DC2876, Mod2002021KeyDC.DC2877, Mod2002021KeyDC.DC2878, Mod2002021KeyDC.DC2880}, "Deuda tributaria de actos jur\u00EDdicos documentados (ITP y AJD) (art. 15 m) LIS)")
	,C38(Mod2002021Key.I2311,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC3241, null                  , null                  , null                  }, null 																																	  , "Gastos que sean objeto de la deducci\u00F3n por inversiones realizadas por las autoridades portuarias (art. 15 n) LIS)")
	,C39(Mod2002021Key.I0363,Mod2002021Key.D0364, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2884, Mod2002021KeyDC.DC2881, Mod2002021KeyDC.DC2882, Mod2002021KeyDC.DC2883, Mod2002021KeyDC.DC2885}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2889, Mod2002021KeyDC.DC2886, Mod2002021KeyDC.DC2887, Mod2002021KeyDC.DC2888, Mod2002021KeyDC.DC2890}, "Ajustes por la limitaci\u00F3n en la deducibilidad en gastos financieros (art. 16 LIS)")
	,C40(Mod2002021Key.I0345,Mod2002021Key.D0346, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2894, Mod2002021KeyDC.DC2891, Mod2002021KeyDC.DC2892, Mod2002021KeyDC.DC2893, Mod2002021KeyDC.DC2895}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2899, Mod2002021KeyDC.DC2896, Mod2002021KeyDC.DC2897, Mod2002021KeyDC.DC2898, Mod2002021KeyDC.DC2900}, "Revalorizaciones contables (art. 17.1 LIS)")
	,C41(Mod2002021Key.I1818,Mod2002021Key.D1819, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2904, Mod2002021KeyDC.DC2901, Mod2002021KeyDC.DC2902, Mod2002021KeyDC.DC2903, Mod2002021KeyDC.DC2905}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2909, Mod2002021KeyDC.DC2906, Mod2002021KeyDC.DC2907, Mod2002021KeyDC.DC2908, Mod2002021KeyDC.DC2910}, "Operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos (art. 17.2 LIS)")	
	,C42(Mod2002021Key.I0371,null				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2911, null                  , null                  , null                  }, null                                                                                                                                   , "SICAV: Reducciones de capital y distribuci\u00F3n de la prima de emisi\u00F3n (art. 17.6 LIS)")	
	,C43(Mod2002021Key.I0347,Mod2002021Key.D0348, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2924, Mod2002021KeyDC.DC2921, Mod2002021KeyDC.DC2922, Mod2002021KeyDC.DC2923, Mod2002021KeyDC.DC2925}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2929, Mod2002021KeyDC.DC2926, Mod2002021KeyDC.DC2927, Mod2002021KeyDC.DC2928, Mod2002021KeyDC.DC2930}, "Transmisiones lucrativas y societarias: aplicaci\u00F3n del valor de mercado (art. 17.4 LIS)")
	,C44(Mod2002021Key.I1011,Mod2002021Key.D1012, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2934, Mod2002021KeyDC.DC2931, Mod2002021KeyDC.DC2932, Mod2002021KeyDC.DC2933, Mod2002021KeyDC.DC2935}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2939, Mod2002021KeyDC.DC2936, Mod2002021KeyDC.DC2937, Mod2002021KeyDC.DC2938, Mod2002021KeyDC.DC2940}, "Operaciones vinculadas: aplicaci\u00F3n del valor de mercado (art. 18 LIS )")
	,C45(Mod2002021Key.I1572,Mod2002021Key.D1573, new Mod2002021KeyDC[]{null				  , Mod2002021KeyDC.DC1674, null				  , null				  , null				  }, new Mod2002021KeyDC[]{null					 , Mod2002021KeyDC.DC1675, null					 , null					 , null					 }, "Cambio de residencia a Estados miembros de la Uni\u00F3n Europea o EEE (art. 19.1 LIS)")
	,C46(Mod2002021Key.I1574,Mod2002021Key.D1575, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC1676, Mod2002021KeyDC.DC1677, Mod2002021KeyDC.DC1678, Mod2002021KeyDC.DC1679, Mod2002021KeyDC.DC1680}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC1681, Mod2002021KeyDC.DC1682, Mod2002021KeyDC.DC1686, Mod2002021KeyDC.DC1687, Mod2002021KeyDC.DC1688}, "Operaciones del art. 19 LIS distintas del cambio de residencia a Estados miembros de la Uni\u00F3n Europea o EEE")
	,C47(Mod2002021Key.I1015,Mod2002021Key.D1016, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2954, Mod2002021KeyDC.DC2951, Mod2002021KeyDC.DC2952, Mod2002021KeyDC.DC2953, Mod2002021KeyDC.DC2955}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2959, Mod2002021KeyDC.DC2956, Mod2002021KeyDC.DC2957, Mod2002021KeyDC.DC2958, Mod2002021KeyDC.DC2960}, "Efectos de la valoraci\u00F3n contable diferente a la fiscal (art. 20 LIS)")
	,C48(null				,Mod2002021Key.D0370, null                                                                                                                                   	   , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2966, null                  , null                  , null                  }, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades residentes (art. 21.1, 21.10 y DT 40\u00AA LIS)")
	,C49(null				,Mod2002021Key.D2181, null                                                                                                                                         , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2976, null                  , null                  , null                  }, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades no residentes (art. 21.1, 21.10 y DT 40\u00AA LIS)")
	,C50(null				,Mod2002021Key.D1764, null                                                                                                                                         , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2699, null                  , null                  , null                  }, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades residentes (art. 21.11 LIS)")
	,C51(null				,Mod2002021Key.D1765, null                                                                                                                                         , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC2844, null                  , null                  , null                  }, "Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades no residentes (art. 21.11 LIS)")
	,C52(Mod2002021Key.I2182,Mod2002021Key.D2183, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2984, Mod2002021KeyDC.DC2981, Mod2002021KeyDC.DC2982, Mod2002021KeyDC.DC2983, Mod2002021KeyDC.DC2985}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2989, Mod2002021KeyDC.DC2986, Mod2002021KeyDC.DC2987, Mod2002021KeyDC.DC2988, Mod2002021KeyDC.DC2990}, "Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades residentes (art. 21.3, 21.10 y DT 40\u00AA LIS)")
	,C53(Mod2002021Key.I2184,Mod2002021Key.D2185, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2994, Mod2002021KeyDC.DC2991, Mod2002021KeyDC.DC2992, Mod2002021KeyDC.DC2993, Mod2002021KeyDC.DC2995}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC2999, Mod2002021KeyDC.DC2996, Mod2002021KeyDC.DC2997, Mod2002021KeyDC.DC2998, Mod2002021KeyDC.DC3000}, "Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades no residentes (art. 21.3, 21.10 y DT 40\u00AA LIS)")
	,C54(Mod2002021Key.I2186,Mod2002021Key.D2187, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3004, Mod2002021KeyDC.DC3001, Mod2002021KeyDC.DC3002, Mod2002021KeyDC.DC3003, Mod2002021KeyDC.DC3005}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3009, Mod2002021KeyDC.DC3006, Mod2002021KeyDC.DC3007, Mod2002021KeyDC.DC3008, Mod2002021KeyDC.DC3010}, "Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3, 21.10 y DT 40\u00AA LIS distintos a transmisiones de valores entidades residentes")
	,C55(Mod2002021Key.I2188,Mod2002021Key.D2189, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3014, Mod2002021KeyDC.DC3011, Mod2002021KeyDC.DC3012, Mod2002021KeyDC.DC3013, Mod2002021KeyDC.DC3015}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3019, Mod2002021KeyDC.DC3016, Mod2002021KeyDC.DC3017, Mod2002021KeyDC.DC3018, Mod2002021KeyDC.DC3020}, "Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3, 21.10 y DT 40\u00AA LIS distintos a transmisiones de valores entidades no residentes")
	,C56(Mod2002021Key.I0256,Mod2002021Key.D0278, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3024, Mod2002021KeyDC.DC3021, Mod2002021KeyDC.DC3022, Mod2002021KeyDC.DC3023, Mod2002021KeyDC.DC3025}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3029, Mod2002021KeyDC.DC3026, Mod2002021KeyDC.DC3027, Mod2002021KeyDC.DC3028, Mod2002021KeyDC.DC3030}, "Exenci\u00F3n de rentas en el extranjero (art. 22 LIS)")
	,C57(Mod2002021Key.I1822,Mod2002021Key.D0372, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3034, Mod2002021KeyDC.DC3031, Mod2002021KeyDC.DC3032, Mod2002021KeyDC.DC3033, Mod2002021KeyDC.DC3035}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3039, Mod2002021KeyDC.DC3036, Mod2002021KeyDC.DC3037, Mod2002021KeyDC.DC3038, Mod2002021KeyDC.DC3040}, "Reducci\u00F3n de rentas procedentes de determinados activos intangibles (art. 23 y DT 20\u00AA LIS)")
	,C58(Mod2002021Key.I0373,Mod2002021Key.D0374, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3044, Mod2002021KeyDC.DC3041, Mod2002021KeyDC.DC3042, Mod2002021KeyDC.DC3043, Mod2002021KeyDC.DC3045}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3049, Mod2002021KeyDC.DC3046, Mod2002021KeyDC.DC3047, Mod2002021KeyDC.DC3048, Mod2002021KeyDC.DC3050}, "Obra ben\u00E9fico-social de las cajas de ahorro y fundaciones bancarias (art. 24 LIS)")
	,C59(Mod2002021Key.I0340,Mod2002021Key.D1589, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3054, Mod2002021KeyDC.DC3051, Mod2002021KeyDC.DC3052, Mod2002021KeyDC.DC3053, Mod2002021KeyDC.DC3055}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3059, Mod2002021KeyDC.DC3056, Mod2002021KeyDC.DC3057, Mod2002021KeyDC.DC3058, Mod2002021KeyDC.DC3060}, "Impuesto extranjero soportado por el contribuyente, no deducible por afectar a rentas con deducci\u00F3n por doble imposici\u00F3n (art. 31.2 LIS)")
	,C60(Mod2002021Key.I0351,null 				, new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC3061, null                  , null                  , null                  }, null                                                                                                                                         , "Impuesto extranjero sobre los beneficios con cargo a los cuales se pagan los dividendos objeto de deducci\u00F3n por doble imposici\u00F3n internacional (art. 32.1 LIS)")
	,C61(Mod2002021Key.I0375,Mod2002021Key.D0376, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3074, Mod2002021KeyDC.DC3071, Mod2002021KeyDC.DC3072, Mod2002021KeyDC.DC3073, Mod2002021KeyDC.DC3075}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3079, Mod2002021KeyDC.DC3076, Mod2002021KeyDC.DC3077, Mod2002021KeyDC.DC3078, Mod2002021KeyDC.DC3080}, "Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico (Cap. II, T\u00EDt. VII LIS)")
	,C62(Mod2002021Key.I1320,Mod2002021Key.D1321, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3084, Mod2002021KeyDC.DC3081, Mod2002021KeyDC.DC3082, Mod2002021KeyDC.DC3083, Mod2002021KeyDC.DC3085}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3089, Mod2002021KeyDC.DC3086, Mod2002021KeyDC.DC3087, Mod2002021KeyDC.DC3088, Mod2002021KeyDC.DC3090}, "Uni\u00F3n temporal de empresas, ajustes del art. 45.1 LIS")
	,C63(Mod2002021Key.I0184,Mod2002021Key.D0544, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3094, Mod2002021KeyDC.DC3091, Mod2002021KeyDC.DC3092, Mod2002021KeyDC.DC3093, Mod2002021KeyDC.DC3095}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3099, Mod2002021KeyDC.DC3096, Mod2002021KeyDC.DC3097, Mod2002021KeyDC.DC3098, Mod2002021KeyDC.DC3100}, "Uni\u00F3n temporal de empresas, ajustes por rentas exentas de UTE que opera en el extranjero (art. 45.2 LIS)")
	,C64(Mod2002021Key.I1022,Mod2002021Key.D1023, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3104, Mod2002021KeyDC.DC3101, Mod2002021KeyDC.DC3102, Mod2002021KeyDC.DC3103, Mod2002021KeyDC.DC3105}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3109, Mod2002021KeyDC.DC3106, Mod2002021KeyDC.DC3107, Mod2002021KeyDC.DC3108, Mod2002021KeyDC.DC3110}, "Uni\u00F3n temporal de empresas, ajustes por rentas exentas por participar en el extranjero en f\u00F3rmulas de colaboraci\u00F3n an\u00E1logas a las UTE (art. 45.2 LIS)")
	,C65(Mod2002021Key.I1018,Mod2002021Key.D1019, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3114, Mod2002021KeyDC.DC3111, Mod2002021KeyDC.DC3112, Mod2002021KeyDC.DC3113, Mod2002021KeyDC.DC3115}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3119, Mod2002021KeyDC.DC3116, Mod2002021KeyDC.DC3117, Mod2002021KeyDC.DC3118, Mod2002021KeyDC.DC3120}, "Uni\u00F3n temporal de empresas, ajustes por criterios de imputaci\u00F3n temporal (art. 46.2 LIS)")
	,C66(Mod2002021Key.I1275,Mod2002021Key.D1276, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3124, Mod2002021KeyDC.DC3121, Mod2002021KeyDC.DC3122, Mod2002021KeyDC.DC3123, Mod2002021KeyDC.DC3125}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3129, Mod2002021KeyDC.DC3126, Mod2002021KeyDC.DC3127, Mod2002021KeyDC.DC3128, Mod2002021KeyDC.DC3130}, "Bases imp. negativas generadas dentro del grupo fi scal por la ent. transmitida y que hayan sido compensadas (art. 62.2 LIS)")
	,C67(Mod2002021Key.I0377,Mod2002021Key.D0378, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3134, Mod2002021KeyDC.DC3131, Mod2002021KeyDC.DC3132, Mod2002021KeyDC.DC3133, Mod2002021KeyDC.DC3135}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3139, Mod2002021KeyDC.DC3136, Mod2002021KeyDC.DC3137, Mod2002021KeyDC.DC3138, Mod2002021KeyDC.DC3140}, "Sociedades y fondos de capital-riesgo y sociedades de desarrollo industrial regional (cap\u00EDtulo IV del t\u00EDtulo VII LIS)")
	,C68(Mod2002021Key.I0379,Mod2002021Key.D0380, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3144, Mod2002021KeyDC.DC3141, Mod2002021KeyDC.DC3142, Mod2002021KeyDC.DC3143, Mod2002021KeyDC.DC3145}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3149, Mod2002021KeyDC.DC3146, Mod2002021KeyDC.DC3147, Mod2002021KeyDC.DC3148, Mod2002021KeyDC.DC3150}, "Valoraci\u00F3n de bienes y derechos. R\u00E9gimen especial operaciones reestructuraci\u00F3n (cap\u00EDtulo VII del t\u00EDtulo VII LIS)")
	,C69(Mod2002021Key.I0381,Mod2002021Key.D0382, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3154, Mod2002021KeyDC.DC3151, Mod2002021KeyDC.DC3152, Mod2002021KeyDC.DC3153, Mod2002021KeyDC.DC3155}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3159, Mod2002021KeyDC.DC3156, Mod2002021KeyDC.DC3157, Mod2002021KeyDC.DC3158, Mod2002021KeyDC.DC3160}, "Miner\u00EDa e hidrocarburos: factor agotamiento (arts. 91 y 95 LIS)")
	,C70(Mod2002021Key.I0383,Mod2002021Key.D0384, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3164, Mod2002021KeyDC.DC3161, Mod2002021KeyDC.DC3162, Mod2002021KeyDC.DC3163, Mod2002021KeyDC.DC3165}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3169, Mod2002021KeyDC.DC3166, Mod2002021KeyDC.DC3167, Mod2002021KeyDC.DC3168, Mod2002021KeyDC.DC3170}, "Hidrocarburos: Amortizaci\u00F3n de inversiones intangibles y gastos de investigaci\u00F3n (art. 99 LIS)") 
	,C71(Mod2002021Key.I0387,Mod2002021Key.D0388, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3174, Mod2002021KeyDC.DC3171, Mod2002021KeyDC.DC3172, Mod2002021KeyDC.DC3173, Mod2002021KeyDC.DC3175}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3179, Mod2002021KeyDC.DC3176, Mod2002021KeyDC.DC3177, Mod2002021KeyDC.DC3178, Mod2002021KeyDC.DC3180}, "Transparencia fiscal internacional (art. 100 LIS)")
	,C72(Mod2002021Key.I0311,Mod2002021Key.D0312, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3184, Mod2002021KeyDC.DC3181, Mod2002021KeyDC.DC3182, Mod2002021KeyDC.DC3183, Mod2002021KeyDC.DC3185}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3189, Mod2002021KeyDC.DC3186, Mod2002021KeyDC.DC3187, Mod2002021KeyDC.DC3188, Mod2002021KeyDC.DC3190}, "Empresas de reducida dimensi\u00F3n: libertad de amortizaci\u00F3n (art. 102 LIS)")  
	,C73(Mod2002021Key.I0313,Mod2002021Key.D0314, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3194, Mod2002021KeyDC.DC3191, Mod2002021KeyDC.DC3192, Mod2002021KeyDC.DC3193, Mod2002021KeyDC.DC3195}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3199, Mod2002021KeyDC.DC3196, Mod2002021KeyDC.DC3197, Mod2002021KeyDC.DC3198, Mod2002021KeyDC.DC3200}, "Empresas de reducida dimensi\u00F3n: amortizaci\u00F3n acelerada (art. 103 LIS)")
	,C74(Mod2002021Key.I0323,Mod2002021Key.D0324, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3204, Mod2002021KeyDC.DC3201, Mod2002021KeyDC.DC3202, Mod2002021KeyDC.DC3203, Mod2002021KeyDC.DC3205}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3209, Mod2002021KeyDC.DC3206, Mod2002021KeyDC.DC3207, Mod2002021KeyDC.DC3208, Mod2002021KeyDC.DC3210}, "Empresas de reducida dimensi\u00F3n: p\u00E9rdidas por deterioro cr\u00E9ditos insolvencias (art. 104 LIS)") 
	,C75(Mod2002021Key.I0317,Mod2002021Key.D0318, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3214, Mod2002021KeyDC.DC3211, Mod2002021KeyDC.DC3212, Mod2002021KeyDC.DC3213, Mod2002021KeyDC.DC3215}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3219, Mod2002021KeyDC.DC3216, Mod2002021KeyDC.DC3217, Mod2002021KeyDC.DC3218, Mod2002021KeyDC.DC3220}, "Arrendamiento financiero: r\u00E9gimen especial (art. 106 LIS)")
	,C76(Mod2002021Key.I0385,Mod2002021Key.D0386, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3224, Mod2002021KeyDC.DC3221, Mod2002021KeyDC.DC3222, Mod2002021KeyDC.DC3223, Mod2002021KeyDC.DC3225}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3229, Mod2002021KeyDC.DC3226, Mod2002021KeyDC.DC3227, Mod2002021KeyDC.DC3228, Mod2002021KeyDC.DC3230}, "R\u00E9gimen fiscal entidades de tenencia de valores extranjeros (cap\u00EDtulo XIII del t\u00EDtulo VII LIS)")
	,C77(Mod2002021Key.I0389,Mod2002021Key.D0390, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3234, Mod2002021KeyDC.DC3231, Mod2002021KeyDC.DC3232, Mod2002021KeyDC.DC3233, Mod2002021KeyDC.DC3235}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3239, Mod2002021KeyDC.DC3236, Mod2002021KeyDC.DC3237, Mod2002021KeyDC.DC3238, Mod2002021KeyDC.DC3240}, "R\u00E9gimen de entidades parcialmente exentas (cap\u00EDtulo XIV del t\u00EDtulo VII LIS)")
	,C78(null				,Mod2002021Key.D0396, null                                                                                                                                   	   , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC3246, null                  , null                  , null                  }, "Montes vecinales en mano com\u00FAn (cap\u00EDtulo XV del t\u00EDtulo VII LIS)")
	,C79(Mod2002021Key.I0397,Mod2002021Key.D0398, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3254, Mod2002021KeyDC.DC3251, Mod2002021KeyDC.DC3252, Mod2002021KeyDC.DC3253, Mod2002021KeyDC.DC3255}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3259, Mod2002021KeyDC.DC3256, Mod2002021KeyDC.DC3257, Mod2002021KeyDC.DC3258, Mod2002021KeyDC.DC3260}, "R\u00E9gimen de entidades navieras en funci\u00F3n del tonelaje (cap\u00EDtulo XVI del t\u00EDtulo VII LIS)")
	,C80(Mod2002021Key.I0250,Mod2002021Key.D0251, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3264, Mod2002021KeyDC.DC3261, Mod2002021KeyDC.DC3262, Mod2002021KeyDC.DC3263, Mod2002021KeyDC.DC3265}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3269, Mod2002021KeyDC.DC3266, Mod2002021KeyDC.DC3267, Mod2002021KeyDC.DC3268, Mod2002021KeyDC.DC3270}, "Aportaciones y colaboraci\u00F3n a favor de entidades sin fines lucrativos")
	,C81(Mod2002021Key.I0391,Mod2002021Key.D0392, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3274, Mod2002021KeyDC.DC3271, Mod2002021KeyDC.DC3272, Mod2002021KeyDC.DC3273, Mod2002021KeyDC.DC3275}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3279, Mod2002021KeyDC.DC3276, Mod2002021KeyDC.DC3277, Mod2002021KeyDC.DC3278, Mod2002021KeyDC.DC3280}, "R\u00E9gimen fiscal entidades sin fines lucrativos (Ley 49/2002)")
	,C82(null				,Mod2002021Key.D0400, null                                                                                                                                   	   , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC3286, null                  , null                  , null                  }, "Cooperativas: Fondo de reserva obligatorio (Ley 20/1990)")
	,C83(Mod2002021Key.I0403,Mod2002021Key.D0404, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3294, Mod2002021KeyDC.DC3291, Mod2002021KeyDC.DC3292, Mod2002021KeyDC.DC3293, Mod2002021KeyDC.DC3295}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3299, Mod2002021KeyDC.DC3296, Mod2002021KeyDC.DC3297, Mod2002021KeyDC.DC3298, Mod2002021KeyDC.DC3300}, "Reserva para inversiones en Canarias (Ley 19/1994)")
	,C84(Mod2002021Key.I0518,Mod2002021Key.D0519, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3304, Mod2002021KeyDC.DC3301, Mod2002021KeyDC.DC3302, Mod2002021KeyDC.DC3303, Mod2002021KeyDC.DC3305}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3309, Mod2002021KeyDC.DC3306, Mod2002021KeyDC.DC3307, Mod2002021KeyDC.DC3308, Mod2002021KeyDC.DC3310}, "Exenci\u00F3n transmisi\u00F3n bienes inmuebles (DA 6\u00AA LIS)")
	,C85(null				,Mod2002021Key.D1824, null                                                                                                                                   	   , new Mod2002021KeyDC[]{null                  , Mod2002021KeyDC.DC3316, null                  , null                  , null                  }, "Rentas procedentes de transmisi\u00F3n de inmovilizado obtenidas por las Autoridades Portuarias (DA 68\u00AA Ley 6/2018)")
	,C86(Mod2002021Key.I2312,Mod2002021Key.D2313, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3284, Mod2002021KeyDC.DC3281, Mod2002021KeyDC.DC3282, Mod2002021KeyDC.DC3283, Mod2002021KeyDC.DC3285}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3314, Mod2002021KeyDC.DC3311, Mod2002021KeyDC.DC3312, Mod2002021KeyDC.DC3313, Mod2002021KeyDC.DC3315}, "UEFA Women Champions League 2020 (Ley 10/2021)")
	,C87(Mod2002021Key.I0510,Mod2002021Key.D0512, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3324, Mod2002021KeyDC.DC3321, Mod2002021KeyDC.DC3322, Mod2002021KeyDC.DC3323, Mod2002021KeyDC.DC3325}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3329, Mod2002021KeyDC.DC3326, Mod2002021KeyDC.DC3327, Mod2002021KeyDC.DC3328, Mod2002021KeyDC.DC3330}, "Operaciones a plazos (DT 1\u00AA LIS)")
	,C88(Mod2002021Key.I0329,Mod2002021Key.D0330, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3334, Mod2002021KeyDC.DC3331, Mod2002021KeyDC.DC3332, Mod2002021KeyDC.DC3333, Mod2002021KeyDC.DC3335}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3339, Mod2002021KeyDC.DC3336, Mod2002021KeyDC.DC3337, Mod2002021KeyDC.DC3338, Mod2002021KeyDC.DC3340}, "Adquisici\u00F3n de participaciones en entidades no residentes (DT 14\u00AA LIS) (adquiridas hasta el 21/12/07)")
	,C89(Mod2002021Key.I0365,Mod2002021Key.D1026, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3344, Mod2002021KeyDC.DC3341, Mod2002021KeyDC.DC3342, Mod2002021KeyDC.DC3343, Mod2002021KeyDC.DC3345}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3349, Mod2002021KeyDC.DC3346, Mod2002021KeyDC.DC3347, Mod2002021KeyDC.DC3348, Mod2002021KeyDC.DC3350}, "Reinversi\u00F3n de beneficios extraordinarios (DT 24\u00AA LIS)")
	,C90(Mod2002021Key.I0409,Mod2002021Key.D0410, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3364, Mod2002021KeyDC.DC3361, Mod2002021KeyDC.DC3362, Mod2002021KeyDC.DC3363, Mod2002021KeyDC.DC3365}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3369, Mod2002021KeyDC.DC3366, Mod2002021KeyDC.DC3367, Mod2002021KeyDC.DC3368, Mod2002021KeyDC.DC3370}, "Entidades en r\u00E9g. de atribuci\u00F3n de rentas const. en el extranj. con presencia en territ. espa\u00F1ol (art. 38 TRLIRNR)") 
	,C91(Mod2002021Key.I0411,Mod2002021Key.D0412, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3374, Mod2002021KeyDC.DC3371, Mod2002021KeyDC.DC3372, Mod2002021KeyDC.DC3373, Mod2002021KeyDC.DC3375}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3379, Mod2002021KeyDC.DC3376, Mod2002021KeyDC.DC3377, Mod2002021KeyDC.DC3378, Mod2002021KeyDC.DC3380}, "Correcciones espec\u00EDficas de entidades sometidas a la normativa foral")
	,C92(Mod2002021Key.I1027,Mod2002021Key.D1028, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3384, Mod2002021KeyDC.DC3381, Mod2002021KeyDC.DC3382, Mod2002021KeyDC.DC3383, Mod2002021KeyDC.DC3385}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3389, Mod2002021KeyDC.DC3386, Mod2002021KeyDC.DC3387, Mod2002021KeyDC.DC3388, Mod2002021KeyDC.DC3390}, "Eliminaciones pendientes de incorporar de sociedades que dejen de pertenecer a un grupo")
	,C93(Mod2002021Key.I0413,Mod2002021Key.D0414, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3394, Mod2002021KeyDC.DC3391, Mod2002021KeyDC.DC3392, Mod2002021KeyDC.DC3393, Mod2002021KeyDC.DC3395}, new Mod2002021KeyDC[]{Mod2002021KeyDC.DC3399, Mod2002021KeyDC.DC3396, Mod2002021KeyDC.DC3397, Mod2002021KeyDC.DC3398, Mod2002021KeyDC.DC3400}, "Otras correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	;
		 
    private String description;
    private Mod2002021Key increase;
    private Mod2002021Key decrease;
    private Mod2002021KeyDC[] detailIncrease;
    private Mod2002021KeyDC[] detailDecrease;
    
    private Mod2002021Key[] keys;
    private CorrectionDetail[] detail; 

	private Mod2002021CorrectionKey(Mod2002021Key increase, Mod2002021Key decrease, Mod2002021KeyDC[] detailIncrease, Mod2002021KeyDC[] detailDecrease, String description) {
		this.increase = increase;
		this.decrease = decrease;
		this.detailIncrease = detailIncrease;
		this.detailDecrease = detailDecrease;
		this.description = description;
		
		keys = new Mod2002021Key[]{increase,decrease};
		
		// Añadimos las casillas de detalle, de cada una de las casillas de las correcciones al resultado contable
		detail = new CorrectionDetail[CorrectionDetail.DETAIL_DESCRIPTIONS.length];
		for (int i=0; i<CorrectionDetail.DETAIL_DESCRIPTIONS.length; i++) {
			
			Mod2002021KeyDC ik = null;
			Mod2002021KeyDC dk = null;

			if (increase != null && detailIncrease != null) {		
				ik = detailIncrease[i];
			}
			
			if (decrease != null && detailDecrease != null) {				
				dk = detailDecrease[i];				
			}			

			detail[i] = new CorrectionDetail();
			if (ik!=null) detail[i].setIncrease(ik);
			if (dk!=null) detail[i].setDecrease(dk);					
			detail[i].setDescription(CorrectionDetail.DETAIL_DESCRIPTIONS[i]);				
		}
	}
	
	public CorrectionDetail[] getDetail() {
		return detail;
	}		
	
	public String getDescription() {
		return description;
	}
	
	public Mod2002021Key getIncrease() {
		return increase;
	}
	
	public Mod2002021Key getDecrease() {
		return decrease;
	}
	
	public Mod2002021KeyDC[] getDetailIncrease() {
		return detailIncrease;
	}

	public Mod2002021KeyDC[] getDetailDecrease() {
		return detailDecrease;
	}

	public boolean isIncreaseEnabled() {
		return (getIncrease() != null);
	}
	
	public boolean isDecreaseEnabled() {
		return (getDecrease() != null);
	}
	
	@Override
	public Mod2002021Key[] getKeys() {
		return keys; 
	}

//	public static void main(String[] args) {
//		int i = 0;
//		int d = 0;
//		for (Mod2002020CorrectionKey k : Mod2002020CorrectionKey.values()) {
//			System.out.println(k.getIncrease()+"   "+k.getDecrease());
//			
//			CorrectionDetail[] p = k.getDetail();
//			if (p!=null)
//			for (int j=0;j<p.length;j++) {
//				if (p[j]!=null)
//				  System.out.println(p[j].getKeys()[0]+ " " +p[j].getKeys()[1]+ " "+ p[j].getDescription());
//			}
//			
//			if (k.getIncrease() != null) i++;
//			if (k.getDecrease() != null) d++;
//		}
//		System.out.println( "I ..: " + i);
//		System.out.println( "D ..: " + d);
//	}

}

final class CorrectionDetail implements Serializable, IMod200KeysProvider {
	
	private static final long serialVersionUID = 791258471554632660L;
	
	public static String[] DETAIL_DESCRIPTIONS = new String[] {			
			 "Saldo pendiente de correcciones temporarias a principio de ejercicio"
			,"Correcciones del ejercicio - Permanentes"
			,"Correcciones del ejercicio - Temporarias (con origen en el ejercicio)" 
			,"Correcciones del ejercicio - Temporarias (con origen en ejercicios anteriores)"
			,"Saldo pendiente de correcciones temporarias a fin de ejercicio"
	};	
	
	private Mod2002021KeyDC increase;
	private Mod2002021KeyDC decrease;
	private String description;

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public IMod200Key[] getKeys() {
		return new Mod2002021KeyDC[]{increase,decrease};
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setIncrease(Mod2002021KeyDC increase) {
		this.increase = increase;
	}

	public void setDecrease(Mod2002021KeyDC decrease) {
		this.decrease = decrease;
	}

	
}


