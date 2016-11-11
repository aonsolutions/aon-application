package net.aonsolutions.dump;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IdsMap {
	
	private Map<String, Map<Integer, Pair<Integer, Boolean>>> idsMap;
	
	public IdsMap() {
		this.idsMap = new HashMap<String, Map<Integer,Pair<Integer,Boolean>>>();
	}
	
	public IdsMap (Map<String, Map<Integer, Pair<Integer, Boolean>>> idsMapNew){
		this.idsMap = idsMapNew;
	}
	
	public Integer getOrder (String tableName, Integer oldId){
		Integer order = this.idsMap.get(tableName).get(oldId).getFirst();
		if (order == null)
			return null;
		else
			return order;
	}
	
	public Integer createNewOrder (String tableName){
		return (this.idsMap.get(tableName).size() + 1);
	}
	
	public Map<Integer, Pair<Integer, Boolean>> getTableInformation (String tableName){
		if ( this.idsMap.get(tableName) == null )
			return null;
		return Collections.unmodifiableMap(this.idsMap.get(tableName));
	}
	
	public Boolean isDownloaded (String tableName, Integer oldId){
		return this.idsMap.get(tableName).get(oldId).getSecond();
	}
	
	public void setTableName (String tableName){
		if (this.idsMap.get(tableName) == null)
			this.idsMap.put(tableName, new HashMap<Integer, Pair<Integer, Boolean>>());
	}
	
	public void createTableName (String tableName){
		this.idsMap.put(tableName, null);
	}
	
	public Pair<Integer, Boolean> getIdInformation (String tableName, Integer oldId){
		return this.idsMap.get(tableName).get(oldId);
	}
	
	public Boolean containsTable (String tableName){
		return this.idsMap.containsKey(tableName);
	}
	
	public Integer setOrder (String tableName, Integer oldId, Boolean downloaded){
		
		Integer tableSize = this.idsMap.get(tableName).size();
		
		Pair<Integer, Boolean> pair = new Pair<Integer, Boolean>(tableSize + 1, downloaded);
		
		this.idsMap.get(tableName).put(oldId, pair);
		
		return pair.getFirst();
	}
	
public Integer setEspecificOrder (String tableName, Integer oldId, Integer newValue, Boolean downloaded){
		
		Pair<Integer, Boolean> pair = new Pair<Integer, Boolean>(newValue, downloaded);
		
		this.idsMap.get(tableName).put(oldId, pair);
		
		return pair.getFirst();
	}


	public Integer getSize(String name) {
		return this.idsMap.get(name).size();
	}

	public void addMap(String name, Map<Integer, Pair<Integer, Boolean>> idsMapAux) {
		idsMapAux.forEach((i, p) -> idsMap.get(name).put(i, p));
	}

	public void clear(String name) {
		idsMap.get(name).clear();
	}

	
}
