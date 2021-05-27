package net.aonsolutions.dump;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class IdsMap {
	
	//MAPA = [NOMBRE_TABLA, [ID_ANTIGUO, (ID_DOMINIO, ORDER, DESCARGADO)]]
	private Map<String, Map<Integer, Threes<Integer, Integer, Boolean>>> idsMap;
	
	public IdsMap() {
		this.idsMap = new HashMap<String, Map<Integer,Threes<Integer, Integer,Boolean>>>();
	}
	
	public IdsMap (Map<String, Map<Integer, Threes<Integer, Integer, Boolean>>> idsMapNew){
		this.idsMap = idsMapNew;
	}
	
	public Boolean isDownloaded (String tableName, Integer oldId){
		return this.idsMap.get(tableName).get(oldId).getThird();
	}
	
	public Boolean containsTable (String tableName){
		return this.idsMap.containsKey(tableName);
	}
	
	public void createTableName (String tableName){
		this.idsMap.put(tableName, null);
	}
	
	public Integer createNewOrder (String tableName, Integer idDomain){
		return (calculateNewOrder(tableName, idDomain) + 1);
	}
	
	public void setTableName (String tableName){
		
		if (this.idsMap.get(tableName) == null)
			this.idsMap.put(tableName, new HashMap<Integer, Threes<Integer, Integer, Boolean>>());
	}
	
	public Threes<Integer, Integer, Boolean> getIdInformation (String tableName, Integer oldId){
		return this.idsMap.get(tableName).get(oldId);
	}
	
	public Map<Integer, Threes<Integer, Integer, Boolean>> getTableInformation (String tableName){
		
		if ( this.idsMap.get(tableName) == null )
			return null;
		
		return Collections.unmodifiableMap(this.idsMap.get(tableName));
	}
	
	public String getVarTableName (String tableName, Integer oldId){
		
		//Order
		Integer idDomain = this.idsMap.get(tableName).get(oldId).getFirst();
		if (idDomain == null)
			return null;
		else{
			//@Nombre_tabla
			return ("@" + tableName.toUpperCase() + "_" + idDomain);
		}
	}
	
	public Field<Integer> getOrder (String tableName, Integer oldId){
		
		//Order
		Integer idDomain = this.idsMap.get(tableName).get(oldId).getFirst();
		Integer order = this.idsMap.get(tableName).get(oldId).getSecond();
		if (order == null || idDomain == null)
			return null;
		else{
			//@Nombre_tabla
			Field<Integer> varId = DSL.field("@" + tableName.toUpperCase() + "_" + idDomain, Integer.class);
			return varId.sub(order);
		}
	}
	
	public Integer setEspecificOrder (String tableName, Integer oldId, Integer newValue, Integer idDomain, Boolean downloaded){
		
		Threes<Integer, Integer, Boolean> three = new Threes<Integer, Integer, Boolean>(idDomain, newValue, downloaded);
		
		this.idsMap.get(tableName).put(oldId, three);
		
		return three.getSecond();
	}
	
	public Integer setOrder (String tableName, Integer oldId, Integer idDomain, Boolean downloaded){
		
		Integer newOrder = calculateNewOrder(tableName, idDomain);
		
		Threes<Integer, Integer, Boolean> third = new Threes<Integer, Integer, Boolean>(idDomain, newOrder + 1, downloaded);
		
		this.idsMap.get(tableName).put(oldId, third);
		
		return third.getSecond();
	}
	
	private Integer calculateNewOrder(String tableName, Integer idDomain) {		
		return (int) this.idsMap.get(tableName).values().stream().filter(t -> t.getFirst().equals(idDomain)).count();
	}

	public void addMap(String name, Map<Integer, Threes<Integer, Integer, Boolean>> idsMapAux) {
		idsMapAux.forEach((i, p) -> idsMap.get(name).put(i, p));
	}

	public void clear(String name) {
		idsMap.get(name).clear();
	}

	
}
