package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod349")
public interface Model349Service extends RemoteService {
	
	LinkedList<Mod349> getMod349s(Occam occam) throws AonCoreException;
	Mod349 get(Occam occam,Integer id) throws AonCoreException;
	Mod349Detail getDetail(Occam occam, Integer id) throws AonCoreException;
	void delete(Occam occam,Mod349 mod349) throws AonCoreException;
	Mod349 save(Occam occam,Mod349 mod349) throws AonCoreException;
	Mod349 reset(Occam occam,Mod349 mod349) throws AonCoreException;
	Mod349 initialize(Occam occam, int year, Period period) throws AonCoreException;
	Mod349 saveComments(Occam occam, Mod349 mod349) throws AonCoreException;
	Mod349 changeStatus(Occam occam, Mod349 mod349, FiscalStatus newStatus) throws AonCoreException;
	String getInfo(Occam occam, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod349 duplicate(Occam occam, Mod349 mod349) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
}
