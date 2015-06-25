package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;

public class Mod2002014toD2 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<D2DepositHeaderKey,Double> map,Mod2002014 mod200);
	}
	
	private static final IPropertyFiller[] BALANCE_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111000,Mod2002014Key.BA101)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111100,Mod2002014Key.BA102)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111200,Mod2002014Key.BA111)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111300,Mod2002014Key.BA115)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111400,Mod2002014Key.BA118)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111500,Mod2002014Key.BA126)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111600,Mod2002014Key.BA134)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111700,Mod2002014Key.BA135)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112000,Mod2002014Key.BA136)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112100,Mod2002014Key.BA137)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112200,Mod2002014Key.BA138)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112300,Mod2002014Key.BA149)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112380,Mod2002014Key.BA150)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112381,Mod2002014Key.BA151)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112382,Mod2002014Key.BA152)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112370,Mod2002014Key.BA158)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112390,Mod2002014Key.BA159)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112400,Mod2002014Key.BA160)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112500,Mod2002014Key.BA168)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112600,Mod2002014Key.BA176)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112700,Mod2002014Key.BA177)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA110000,Mod2002014Key.BA180)
	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200, D2DepositHeaderKey D2Key, Mod2002014Key mod200Key) {
		DoubleVariable2014 dv = mod200.getVariable(mod200Key);
		ctx.put(D2Key, dv==null?0.0:dv.getValue());
	} 

	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		for (IPropertyFiller filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}

}
