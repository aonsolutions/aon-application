package com.code.aon.csb.fd0.model.MOD190.data;

/**
 * A receiver
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Receiver {

	/**
	 * The code
	 */
	private String code;
	/**
	 * The manages code
	 */
	private String managerCode;
	/**
	 * The name
	 */
	private String name;
	/**
	 * The province
	 */
	private Integer province;
	/**
	 * The key
	 */
	private String key;
	/**
	 * The subkey
	 */
	private Integer subkey;
	/**
	 * The received money quantity
	 */
	private Double receibedMoney = new Double(0.0);
	/**
	 * The withholded money quantity
	 */
	private Double withholdedMoney = new Double(0.0);
	/**
	 * The receibed spice
	 */
	private Double receibedSpice = new Double(0.0);
	/**
	 * The payed cash
	 */
	private Double payEfect = new Double(0.0);
	/**
	 * The payed repercuted
	 */
	private Double payReperc = new Double(0.0);
	/**
	 * The year  
	 */
	private Integer year;
	/**
	 * Is ceuta or melilla
	 */
	private Integer ceuMel = new Integer(0);
	/**
	 * The born year  
	 */
	private Integer bornYear;
	/**
	 * The  family status
	 */
	private Integer familyStatus;
	/**
	 * The marriage code
	 */
	private String marriageCode;
	/**
	 * The discapacity
	 */
	private Integer discapacity;
	/**
	 * The relation
	 */
	private Integer relation;
	/**
	 * The laboral extension
	 */
	private Integer laboralExtension;
	/**
	 * The geographical movility
	 */
	private Integer geoMovility;
	/**
	 * The reductions
	 */
	private Double reductions;
	/**
	 * The costs  
	 */
	private Double costs;
	/**
	 * The pension
	 */
	private Double pension;
	/**
	 * The food
	 */
	private Double food;
	/**
	 * The children code 1 quantity  
	 */
	private Integer children1;
	/**
	 * The children code 2 quantity 
	 */
	private Integer children2;
	/**
	 * The children code 3 quantity  
	 */
	private Integer children3;
	/**
	 * The children code 4 quantity
	 */
	private Integer children4;
	/**
	 * The children handicapped code 1 quantity
	 */
	private Integer childHandicapped1;
	/**
	 * The children handicapped code 2 quantity
	 */
	private Integer childHandicapped2;
	/**
	 * The children handicapped code 3 quantity
	 */
	private Integer childHandicapped3;
	/**
	 * The children handicapped code 4 quantity
	 */
	private Integer childHandicapped4;
	/**
	 * The children handicapped code 5 quantity
	 */
	private Integer childHandicapped5;
	/**
	 * The children handicapped code 6 quantity
	 */
	private Integer childHandicapped6;
	/**
	 * The parents code 1 quantity
	 */
	private Integer parents1;
	/**
	 * The parents code 2 quantity
	 */
	private Integer parents2;
	/**
	 * The parents code 3 quantity
	 */
	private Integer parents3;
	/**
	 * The parents code 4 quantity
	 */
	private Integer parents4;
	/**
	 * The parents Handicapped code 1 quantity
	 */
	private Integer parentsHandicapped1;
	/**
	 * The parents Handicapped code 2 quantity
	 */
	private Integer parentsHandicapped2;
	/**
	 * The parents Handicapped code 3 quantity
	 */
	private Integer parentsHandicapped3;
	/**
	 * The parents Handicapped code 4 quantity
	 */
	private Integer parentsHandicapped4;
	/**
	 * The parents Handicapped code 5 quantity
	 */
	private Integer parentsHandicapped5;
	/**
	 * The parents Handicapped code 6 quantity
	 */
	private Integer parentsHandicapped6;
	
	/**
	 * @return the bornYear
	 */
	public Integer getBornYear() {
		return bornYear;
	}
	/**
	 * @param bornYear the bornYear to set
	 */
	public void setBornYear(Integer bornYear) {
		this.bornYear = bornYear;
	}
	/**
	 * @return the ceuMel
	 */
	public Integer getCeuMel() {
		return ceuMel;
	}
	/**
	 * @param ceuMel the ceuMel to set
	 */
	public void setCeuMel(Integer ceuMel) {
		this.ceuMel = ceuMel;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the costs
	 */
	public Double getCosts() {
		return costs;
	}
	/**
	 * @param costs the costs to set
	 */
	public void setCosts(Double costs) {
		this.costs = costs;
	}
	/**
	 * @return the discapacity
	 */
	public Integer getDiscapacity() {
		return discapacity;
	}
	/**
	 * @param discapacity the discapacity to set
	 */
	public void setDiscapacity(Integer discapacity) {
		this.discapacity = discapacity;
	}
	/**
	 * @return the familyStatus
	 */
	public Integer getFamilyStatus() {
		return familyStatus;
	}
	/**
	 * @param familyStatus the familyStatus to set
	 */
	public void setFamilyStatus(Integer familyStatus) {
		this.familyStatus = familyStatus;
	}
	/**
	 * @return the food
	 */
	public Double getFood() {
		return food;
	}
	/**
	 * @param food the food to set
	 */
	public void setFood(Double food) {
		this.food = food;
	}
	/**
	 * @return the geoMovility
	 */
	public Integer getGeoMovility() {
		return geoMovility;
	}
	/**
	 * @param geoMovility the geoMovility to set
	 */
	public void setGeoMovility(Integer geoMovility) {
		this.geoMovility = geoMovility;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the laboralExtension
	 */
	public Integer getLaboralExtension() {
		return laboralExtension;
	}
	/**
	 * @param laboralExtension the laboralExtension to set
	 */
	public void setLaboralExtension(Integer laboralExtension) {
		this.laboralExtension = laboralExtension;
	}
	/**
	 * @return the managerCode
	 */
	public String getManagerCode() {
		return managerCode;
	}
	/**
	 * @param managerCode the managerCode to set
	 */
	public void setManagerCode(String managerCode) {
		this.managerCode = managerCode;
	}
	/**
	 * @return the marriageCode
	 */
	public String getMarriageCode() {
		return marriageCode;
	}
	/**
	 * @param marriageCode the marriageCode to set
	 */
	public void setMarriageCode(String marriageCode) {
		this.marriageCode = marriageCode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the payEfect
	 */
	public Double getPayEfect() {
		return payEfect;
	}
	/**
	 * @param payEfect the payEfect to set
	 */
	public void setPayEfect(Double payEfect) {
		this.payEfect = payEfect;
	}
	/**
	 * @return the payReperc
	 */
	public Double getPayReperc() {
		return payReperc;
	}
	/**
	 * @param payReperc the payReperc to set
	 */
	public void setPayReperc(Double payReperc) {
		this.payReperc = payReperc;
	}
	/**
	 * @return the pension
	 */
	public Double getPension() {
		return pension;
	}
	/**
	 * @param pension the pension to set
	 */
	public void setPension(Double pension) {
		this.pension = pension;
	}
	/**
	 * @return the province
	 */
	public Integer getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(Integer province) {
		this.province = province;
	}
	/**
	 * @return the receibedMoney
	 */
	public Double getReceibedMoney() {
		return receibedMoney;
	}
	/**
	 * @param receibedMoney the receibedMoney to set
	 */
	public void setReceibedMoney(Double receibedMoney) {
		this.receibedMoney = receibedMoney;
	}
	/**
	 * @return the receibedSpice
	 */
	public Double getReceibedSpice() {
		return receibedSpice;
	}
	/**
	 * @param receibedSpice the receibedSpice to set
	 */
	public void setReceibedSpice(Double receibedSpice) {
		this.receibedSpice = receibedSpice;
	}
	/**
	 * @return the reductions
	 */
	public Double getReductions() {
		return reductions;
	}
	/**
	 * @param reductions the reductions to set
	 */
	public void setReductions(Double reductions) {
		this.reductions = reductions;
	}
	/**
	 * @return the relation
	 */
	public Integer getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(Integer relation) {
		this.relation = relation;
	}
	/**
	 * @return the subkey
	 */
	public Integer getSubkey() {
		return subkey;
	}
	/**
	 * @param subkey the subkey to set
	 */
	public void setSubkey(Integer subkey) {
		this.subkey = subkey;
	}
	/**
	 * @return the withholdedMoney
	 */
	public Double getWithholdedMoney() {
		return withholdedMoney;
	}
	/**
	 * @param withholdedMoney the withholdedMoney to set
	 */
	public void setWithholdedMoney(Double withholdedMoney) {
		this.withholdedMoney = withholdedMoney;
	}
	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the children1
	 */
	public Integer getChildren1() {
		return children1;
	}
	/**
	 * @param children1 the children1 to set
	 */
	public void setChildren1(Integer children1) {
		this.children1 = children1;
	}
	/**
	 * @return the children2
	 */
	public Integer getChildren2() {
		return children2;
	}
	/**
	 * @param children2 the children2 to set
	 */
	public void setChildren2(Integer children2) {
		this.children2 = children2;
	}
	/**
	 * @return the children3
	 */
	public Integer getChildren3() {
		return children3;
	}
	/**
	 * @param children3 the children3 to set
	 */
	public void setChildren3(Integer children3) {
		this.children3 = children3;
	}
	/**
	 * @return the children4
	 */
	public Integer getChildren4() {
		return children4;
	}
	/**
	 * @param children4 the children4 to set
	 */
	public void setChildren4(Integer children4) {
		this.children4 = children4;
	}
	
	/**
	 * @return the childHandicapped1
	 */
	public Integer getChildHandicapped1() {
		return childHandicapped1;
	}
	/**
	 * @param childHandicapped1 the childHandicapped1 to set
	 */
	public void setChildHandicapped1(Integer childHandicapped1) {
		this.childHandicapped1 = childHandicapped1;
	}
	/**
	 * @return the childHandicapped2
	 */
	public Integer getChildHandicapped2() {
		return childHandicapped2;
	}
	/**
	 * @param childHandicapped2 the childHandicapped2 to set
	 */
	public void setChildHandicapped2(Integer childHandicapped2) {
		this.childHandicapped2 = childHandicapped2;
	}
	/**
	 * @return the childHandicapped3
	 */
	public Integer getChildHandicapped3() {
		return childHandicapped3;
	}
	/**
	 * @param childHandicapped3 the childHandicapped3 to set
	 */
	public void setChildHandicapped3(Integer childHandicapped3) {
		this.childHandicapped3 = childHandicapped3;
	}
	/**
	 * @return the childHandicapped4
	 */
	public Integer getChildHandicapped4() {
		return childHandicapped4;
	}
	/**
	 * @param childHandicapped4 the childHandicapped4 to set
	 */
	public void setChildHandicapped4(Integer childHandicapped4) {
		this.childHandicapped4 = childHandicapped4;
	}
	/**
	 * @return the childHandicapped5
	 */
	public Integer getChildHandicapped5() {
		return childHandicapped5;
	}
	/**
	 * @param childHandicapped5 the childHandicapped5 to set
	 */
	public void setChildHandicapped5(Integer childHandicapped5) {
		this.childHandicapped5 = childHandicapped5;
	}
	/**
	 * @return the childHandicapped6
	 */
	public Integer getChildHandicapped6() {
		return childHandicapped6;
	}
	/**
	 * @param childHandicapped6 the childHandicapped6 to set
	 */
	public void setChildHandicapped6(Integer childHandicapped6) {
		this.childHandicapped6 = childHandicapped6;
	}
	
	/**
	 * @return the parents1
	 */
	public Integer getParents1() {
		return parents1;
	}
	/**
	 * @param parents1 the parents1 to set
	 */
	public void setParents1(Integer parents1) {
		this.parents1 = parents1;
	}
	/**
	 * @return the parents2
	 */
	public Integer getParents2() {
		return parents2;
	}
	/**
	 * @param parents2 the parents2 to set
	 */
	public void setParents2(Integer parents2) {
		this.parents2 = parents2;
	}
	/**
	 * @return the parents3
	 */
	public Integer getParents3() {
		return parents3;
	}
	/**
	 * @param parents3 the parents3 to set
	 */
	public void setParents3(Integer parents3) {
		this.parents3 = parents3;
	}
	/**
	 * @return the parents4
	 */
	public Integer getParents4() {
		return parents4;
	}
	/**
	 * @param parents4 the parents4 to set
	 */
	public void setParents4(Integer parents4) {
		this.parents4 = parents4;
	}
	
	/**
	 * @return the parentsHandicapped1
	 */
	public Integer getParentsHandicapped1() {
		return parentsHandicapped1;
	}
	/**
	 * @param parentsHandicapped1 the parentsHandicapped1 to set
	 */
	public void setParentsHandicapped1(Integer parentsHandicapped1) {
		this.parentsHandicapped1 = parentsHandicapped1;
	}
	/**
	 * @return the parentsHandicapped2
	 */
	public Integer getParentsHandicapped2() {
		return parentsHandicapped2;
	}
	/**
	 * @param parentsHandicapped2 the parentsHandicapped2 to set
	 */
	public void setParentsHandicapped2(Integer parentsHandicapped2) {
		this.parentsHandicapped2 = parentsHandicapped2;
	}
	/**
	 * @return the parentsHandicapped3
	 */
	public Integer getParentsHandicapped3() {
		return parentsHandicapped3;
	}
	/**
	 * @param parentsHandicapped3 the parentsHandicapped3 to set
	 */
	public void setParentsHandicapped3(Integer parentsHandicapped3) {
		this.parentsHandicapped3 = parentsHandicapped3;
	}
	/**
	 * @return the parentsHandicapped4
	 */
	public Integer getParentsHandicapped4() {
		return parentsHandicapped4;
	}
	/**
	 * @param parentsHandicapped4 the parentsHandicapped4 to set
	 */
	public void setParentsHandicapped4(Integer parentsHandicapped4) {
		this.parentsHandicapped4 = parentsHandicapped4;
	}
	/**
	 * @return the parentsHandicapped5
	 */
	public Integer getParentsHandicapped5() {
		return parentsHandicapped5;
	}
	/**
	 * @param parentsHandicapped5 the parentsHandicapped5 to set
	 */
	public void setParentsHandicapped5(Integer parentsHandicapped5) {
		this.parentsHandicapped5 = parentsHandicapped5;
	}
	/**
	 * @return the parentsHandicapped6
	 */
	public Integer getParentsHandicapped6() {
		return parentsHandicapped6;
	}
	/**
	 * @param parentsHandicapped6 the parentsHandicapped6 to set
	 */
	public void setParentsHandicapped6(Integer parentsHandicapped6) {
		this.parentsHandicapped6 = parentsHandicapped6;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "RECEIVER ";
		description += "CODE ";
		description += code == null?"NULL ":"'"+code+"''";
		description += "MANAGER ";
		description += managerCode == null?"NULL ":"'"+managerCode+"'";
		description += "NAME ";
		description += name == null?"NULL ":"'"+name+"'; ";
		return description;
	}

}
