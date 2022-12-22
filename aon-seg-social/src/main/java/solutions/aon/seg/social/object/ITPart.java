package solutions.aon.seg.social.object;

import java.util.Date;
import java.util.Optional;

import solutions.aon.seg.social.SistemaRED.PartType;

public class ITPart {

	private Date receptionDate;
	private String naf;
	private Date workLeaveDate;
	private Date workRestartDate;
	private Date partDate;
	private Integer partNum;
	private String partType;
	private String ccc;
	private Integer numberHealth;
	private String typeProcess;
	private String entity;
	private String situation;

	private String nameEmployee;
	private String ipf;
	private String directionEmployee;
	private String occupation;

	private String nameEnterprise;
	private String directionEnterprise;
	
	private String collegiateNumber;
	private String  cias;
	private String  contingency;
	private String  causeRestart;
	private Date    confirmationDate;
	private Integer durationDays;
	private Date    accDate;
	private Date    bjPrevDate;
	private Date    bjInitDate;
	private String  typeAcc;
	private String  typeAssist;
	private Date    nextMedicalDate;
	private Boolean relapse;
	
	private Float  	baseCtz;
	private Integer daysCtz;
	private Float   hoursCtzExtr;
	private Float   sumBCtz;
	private Integer daysSumCtz;
	private Float   hoursCrzOther;
	private String  gpCtz;
	private String  catProf;
	private String  typeCto;
	private Integer lack;

	public ITPart() { /* TODO document why this constructor is empty */ }
	
	public ITPart setBaseCtz(Float baseCtz) {
		this.baseCtz = baseCtz;
		return this;
	}

	public ITPart setDaysCtz(Integer daysCtz) {
		this.daysCtz = daysCtz;
		return this;
	}

	public ITPart setHoursCtzExtr(Float hoursCtzExtr) {
		this.hoursCtzExtr = hoursCtzExtr;
		return this;
	}

	public ITPart setSumBCtz(Float sumBCtz) {
		this.sumBCtz = sumBCtz;
		return this;
	}

	public ITPart setDaysSumCtz(Integer daysSumCtz) {
		this.daysSumCtz = daysSumCtz;
		return this;
	}

	public ITPart setHoursCrzOther(Float hoursCrzOther) {
		this.hoursCrzOther = hoursCrzOther;
		return this;
	}

	public ITPart setGpCtz(String gpCtz) {
		this.gpCtz = gpCtz;
		return this;
	}

	public ITPart setCatProf(String catProf) {
		this.catProf = catProf;
		return this;
	}

	public ITPart setTypeCto(String typeCto) {
		this.typeCto = typeCto;
		return this;
	}

	public ITPart setLack(Integer lack) {
		this.lack = lack;
		return this;
	}
	
	public ITPart setCias(String cias) {
		this.cias = cias;
		return this;
	}

	public ITPart setCauseRestart(String causeRestart) {
		this.causeRestart = causeRestart;
		return this;
	}
	
	public ITPart setCollegiateNumber(String collegiateNumber) {
		this.collegiateNumber = collegiateNumber;
		return this;
	}

	public ITPart  setContingency(String contingency) {
		this.contingency = contingency;
		return this;
	}

	public ITPart  setConfirmationDate(Date confirmationDate) {
		this.confirmationDate = confirmationDate;
		return this;
	}
	
	public ITPart  setRelapse(Boolean relapse) {
		this.relapse = relapse;
		return this;
	}
	
	public ITPart  setDurationDays(Integer durationDays) {
		this.durationDays = durationDays;
		return this;
	}

	public ITPart setAccDate(Date accDate) {
		this.accDate = accDate;
		return this;
	}

	public ITPart setBjPrevDate(Date bjPrevDate) {
		this.bjPrevDate = bjPrevDate;
		return this;
	}

	public ITPart  setBjInitDate(Date bjInitDate) {
		this.bjInitDate = bjInitDate;
		return this;
	}

	public ITPart setTypeAcc(String typeAcc) {
		this.typeAcc = typeAcc;
		return this;
	}

	public ITPart setTypeAssist(String typeAssist) {
		this.typeAssist = typeAssist;
		return this;
	}

	public ITPart setNextMedicalDate(Date nextMedicalDate) {
		this.nextMedicalDate = nextMedicalDate;
		return this;
	}
	
	public ITPart setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
		return this;
	}
	
	public ITPart setNaf(String naf) {
		if(naf != null && !naf.trim().equals("")) this.naf = naf;
		else this.naf =null; 
		return this;
	}
	
	public ITPart setWorkLeaveDate(Date workLeaveDate) {
		this.workLeaveDate = workLeaveDate;
		return this;
	}
	
	public ITPart setWorkRestartDate(Date workRestartDate) {
		this.workRestartDate = workRestartDate;
		return this;
	}
	
	public ITPart setPartNum(Integer partNum) {
		this.partNum = partNum;
		return this;
	}
	
	public ITPart setPartType(String partType) {
		if(partType != null && !partType.trim().equals("")) this.partType = partType;
		else this.partType =null; 
		return this;
	}

	public ITPart setCcc(String d) {
		this.ccc = d;
		return this;
	}

	public ITPart setTypeProcess(String d) {
		this.typeProcess = d;
		return this;
	}

	public ITPart setEntity(String d) {
		this.entity = d;
		return this;
	}
	
	public ITPart setSituation(String d) {
		this.situation = d;
		return this;
	}
	
	public ITPart setNumberHealth(Integer d) {
		this.numberHealth = d;
		return this;
	}
	
	public ITPart setNameEmployee(String d) {
		this.nameEmployee = d;
		return this;
	}

	public ITPart setIpf(String d) {
		this.ipf = d;
		return this;
	}

	public ITPart setDirectionEmployee(String d) {
		this.directionEmployee = d;
		return this;
	}

	public ITPart setOccupation(String d) {
		this.occupation = d;
		return this;
	}

	public ITPart setDirectionEnterprise(String d) {
		this.directionEnterprise = d;
		return this;
	}

	public ITPart setNameEnterprise(String d) {
		this.nameEnterprise = d;
		return this;
	}
	
	public Date getReceptionDate() {
		return receptionDate;
	}
	
	public Optional<String> getNaf() {
		return Optional.ofNullable(naf);
	}
	
	public String getCcc() {
		return ccc;
	}
	
	public Optional<Date> getWorkLeaveDate() {
		return Optional.ofNullable(workLeaveDate);
	}
	
	public String getPartType() {
		return partType;
	}
	
	public PartType getType() {
		if(partType!=null) {
			String p = partType.toLowerCase();
			if(p.contains("alta") || p.contains("pa")) {
				return PartType.ALTA;
			} else if(p.contains("baja") || p.contains("pb")) {
				return PartType.BAJA;
			} else if(p.contains("confirmaci") || p.contains("pc")) {
				return PartType.CONFIRMACION;
			}
		}
		return null;
	}
	
	public Optional<Integer> getNumberHealth() {
		return Optional.ofNullable(numberHealth);
	}
	
	
	public Optional<String> getTypeProcess() {
		return Optional.ofNullable(typeProcess);
	}
	
	public Optional<String> getEntity() {
		return Optional.ofNullable(entity);
	}
	
	public Optional<String> getSituation() {
		return Optional.ofNullable(situation);
	}

	public Optional<String> getNameEmployee() {
		return Optional.ofNullable(nameEmployee);
	}
	
	public Optional<String> getIpf() {
		return Optional.ofNullable(ipf);
	}
	
	public Optional<String> getDirectionEmployee() {
		return Optional.ofNullable(directionEmployee);
	}
	
	public Optional<String> getOccupation() {
		return Optional.ofNullable(occupation);
	}

	public Optional<String> getNameEnterprise() {
		return Optional.ofNullable(nameEnterprise);
	}
	
	public Optional<String> getDirectionEnterprise() {
		return Optional.ofNullable(directionEnterprise);
	}
	
	public Optional<Date> getPartDate() {
		PartType p = getType();
		Date date = null;
		if(p!=null) {
			if(p.equals(PartType.BAJA) && workLeaveDate!=null) {
				date = workLeaveDate;
			} else if(p.equals(PartType.ALTA) && workRestartDate!=null) {
				date = workRestartDate;
			} else if(p.equals(PartType.CONFIRMACION) && confirmationDate!=null) {
				date = confirmationDate;
			}
		}
		return Optional.ofNullable(date);
	}
	
	public Optional<Integer> getPartNum() {
		return Optional.ofNullable(partNum);
	}
	
	public Optional<String> getCollegiateNumber() {
		return Optional.ofNullable(collegiateNumber);
	}
	
	public Optional<String> getCias() {
		return Optional.ofNullable(cias);
	}
	
	public Optional<String> getContingency() {
		return Optional.ofNullable(contingency);
	}
	
	public Optional<Date> getConfirmationDate() {
		return Optional.ofNullable(confirmationDate);
	}
	
	public Integer getDurationDays() {
		return durationDays;
	}
	
	public Optional<Date> getAccDate() {
		return Optional.ofNullable(accDate);
	}
	
	public Optional<Date> getBjPrevDate() {
		return Optional.ofNullable(bjPrevDate);
	}
	
	public Optional<Date> getBjInitDate() {
		return Optional.ofNullable(bjInitDate);
	}
	
	public Optional<Date> getNextMedicalDate() {
		return Optional.ofNullable(nextMedicalDate);
	}
	
	public Optional<String> getCauseRestart() {
		return Optional.ofNullable(causeRestart);
	}
	
	public Optional<String> getTypeAcc() {
		return Optional.ofNullable(typeAcc); // Leve, Grave, Muy grave
	}
	
	public Optional<String> getTypeAssist() {
		return Optional.ofNullable(typeAssist); // Ambulatorio, Hospitalario
	}
	
	public Optional<Date> getWorkRestartDate() {
		return Optional.ofNullable(workRestartDate);
	}

	public Boolean getRelapse() {
		return relapse;
	}
	
	public Optional<Float> getBaseCtz() {
		return Optional.ofNullable(baseCtz);
	}
	
	public Optional<Integer> getDaysCtz() {
		return Optional.ofNullable(daysCtz);
	}
	
	public Optional<Float> getHoursCtzExtr() {
		return Optional.ofNullable(hoursCtzExtr);
	}
	
	public Optional<Float> getSumBCtz() {
		return Optional.ofNullable(sumBCtz);
	}
	
	public Integer getDaysSumCtz() {
		return daysSumCtz;
	}
	
	public Optional<Float> getHoursCrzOther() {
		return Optional.ofNullable(hoursCrzOther);
	}
	
	public Optional<String> getGpCtz() {
		return Optional.ofNullable(gpCtz);
	}
	
	public Optional<String> getCatProf() {
		return Optional.ofNullable(catProf);
	}
	
	public Optional<String> getTypeCto() {
		return Optional.ofNullable(typeCto); //Fijos discontinuo/Tiempo parcial, Resto
	}
	
	public Optional<Integer> getLack() {
		return Optional.ofNullable(lack);
	}
	
	public Optional<Float> getDailyBaseCgc() {
		return Optional.ofNullable(null!=baseCtz && baseCtz>0 ? baseCtz : sumBCtz);
	}
	
	public Integer getCauseNumber() {
		String cause = null;
		if(getPartType().contains("baja") && getContingency().isPresent())
			cause = getContingency().get();
		else if(getCauseRestart().isPresent()) 
			cause = getCauseRestart().get();
		
		if(null!=cause)
			return Integer.parseInt(cause.replaceAll("[^\\d]", ""));
		return 0;
	}
	
	public boolean checkForType(PartType type, Date date) {
		 Optional<Date> pDate = getPartDate();
		 return pDate.isPresent() ? date.compareTo(pDate.get()) == 0 : false;
	}

	public void accept(Visitor visitor) {
		if(receptionDate != null) visitor.visitReceptionDate(receptionDate);
		if(naf != null) visitor.visitNaf(naf);
		if(ccc != null) visitor.visitCcc(ccc);
		if(workLeaveDate != null) visitor.visitWorkLeaveDate(workLeaveDate);
		if(workRestartDate != null) visitor.visitWorkRestartDate(workRestartDate);
		if(confirmationDate != null) visitor.visitConfirmationDate(confirmationDate);
		if(partDate != null) visitor.visitPartDate(partDate);
		if(partNum != null) visitor.visitPartNum(partNum);
		if(partType != null) visitor.visitPartType(partType);
	}
	
	public static interface Visitor{
		void visitReceptionDate(Date receptionDate);
		void visitCcc(String ccc);
		void visitConfirmationDate(Date confirmationDate);
		void visitNaf(String naf);
		void visitWorkLeaveDate(Date workLeaveDate);
		void visitWorkRestartDate(Date workRestartDate);
		void visitPartDate(Date partDate);
		void visitPartNum(Integer partNum);
		void visitPartType(String partType);
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		accept(new Visitor() {
			
			@Override
			public void visitConfirmationDate(Date confirmationDate) {
				stringBuffer.append(String.format(" confirmationDate : \"%s\" ", confirmationDate));
			}
			
			@Override
			public void visitWorkRestartDate(Date workRestartDate) {
				stringBuffer.append(String.format(" workRestartDate : \"%s\" ", workRestartDate));
			}
			
			@Override
			public void visitWorkLeaveDate(Date workLeaveDate) {
				stringBuffer.append(String.format(" workLeaveDate : \"%s\" ", workLeaveDate));
			}
			
			@Override
			public void visitReceptionDate(Date receptionDate) {
				stringBuffer.append(String.format(" receptionDate : \"%s\" ", receptionDate));
			}
			
			@Override
			public void visitPartType(String partType) {
				stringBuffer.append(String.format(" partType : \"%s\" ", partType));
			}
			
			@Override
			public void visitPartNum(Integer partNum) {
				stringBuffer.append(String.format(" partNum : \"%s\" ", partNum));
			}
			
			@Override
			public void visitPartDate(Date partDate) {
				stringBuffer.append(String.format(" partDate : \"%s\" ", partDate));
			}
			
			@Override
			public void visitNaf(String naf) {
				stringBuffer.append(String.format(" naf : \"%s\" ", naf));
			}
			
			@Override
			public void visitCcc(String ccc) {
				stringBuffer.append(String.format(" ccc : \"%s\" ", ccc));
			}
		});
		
		getIpf().ifPresent(d-> stringBuffer.append(String.format(" dni : \"%s\" ", d)) );
		
		getContingency().ifPresent(d-> stringBuffer.append(String.format(" contingency : \"%s\" ", d)) );
		
		getCauseRestart().ifPresent(d-> stringBuffer.append(String.format(" causeRestart : \"%s\" ",d)) );
		
		getBaseCtz().ifPresent(d->  stringBuffer.append(String.format(" baseCtz : \"%s\" ",d)) );
		
		getSumBCtz().ifPresent(d->  stringBuffer.append(String.format(" sumaBCtz : \"%s\" ",d)) );
		
		getDaysCtz().ifPresent(d->  stringBuffer.append(String.format(" daysCtz : \"%s\" ",d)) );
		
		getNameEmployee().ifPresent(d->  stringBuffer.append(String.format(" nameEmployee : \"%s\" ", d)) );

		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((naf == null) ? 0 : naf.hashCode());
		result = prime * result + ((partDate == null) ? 0 : partDate.hashCode());
		result = prime * result + ((partNum == null) ? 0 : partNum.hashCode());
		result = prime * result + ((partType == null) ? 0 : partType.hashCode());
		result = prime * result + ((receptionDate == null) ? 0 : receptionDate.hashCode());
		result = prime * result + ((workLeaveDate == null) ? 0 : workLeaveDate.hashCode());
		result = prime * result + ((workRestartDate == null) ? 0 : workRestartDate.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ITPart other = (ITPart) obj;
		if (naf == null) {
			if (other.naf != null)
				return false;
		} else if (!naf.equals(other.naf))
			return false;
		if (partDate == null) {
			if (other.partDate != null)
				return false;
		} else if (!partDate.equals(other.partDate))
			return false;
		if (partNum == null) {
			if (other.partNum != null)
				return false;
		} else if (!partNum.equals(other.partNum))
			return false;
		if (partType == null) {
			if (other.partType != null)
				return false;
		} else if (!partType.equals(other.partType))
			return false;
		if (receptionDate == null) {
			if (other.receptionDate != null)
				return false;
		} else if (!receptionDate.equals(other.receptionDate))
			return false;
		if (workLeaveDate == null) {
			if (other.workLeaveDate != null)
				return false;
		} else if (!workLeaveDate.equals(other.workLeaveDate))
			return false;
		if (workRestartDate == null) {
			if (other.workRestartDate != null)
				return false;
		} else if (!workRestartDate.equals(other.workRestartDate))
			return false;
		return true;
	}
}


