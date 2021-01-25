package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.Optional;

public class Contingency_bases {

	private Optional<Double> monthly_amount;
	private Optional<Double> extra_proration_amount;
	
	private Optional<Double> common_cont_base;
	private Optional<Double> common_cont_type;
	private Optional<Double> common_cont_ap_enterprise;
	
	private Optional<Double> professional_cont_base;
	
	private Optional<Double> at_ep_type;
	private Optional<Double> at_ep_ap_enterprise;
	
	private Optional<Double> unemployment_type;
	private Optional<Double> unemployment_ap_enterprise;
	
	private Optional<Double> profes_form_type;
	private Optional<Double> profes_form_ap_enterprise;
	
	private Optional<Double> fogasa_type;
	private Optional<Double> fogasa_ap_enterprise;
	
	private Optional<Double> force_majeure_base;
	private Optional<Double> force_majeure_type;
	private Optional<Double> force_majeure_ap_enterprise;
	
	private Optional<Double> no_struct_base;
	private Optional<Double> no_struct_type;
	private Optional<Double> no_struct_ap_enterprise;
	
	private Optional<Double> irpf_esp;
	private Optional<Double> irpf_retrib_diner;
	private Optional<Double> total;
	
	private Contingency_bases() {}

	public Optional<Double> getMonthly_amount() {return monthly_amount;}
	public Optional<Double> getExtra_proration_amount() {return extra_proration_amount;}
	public Optional<Double> getCommon_cont_base() {return common_cont_base;}
	public Optional<Double> getCommon_cont_type() {return common_cont_type;}
	public Optional<Double> getCommon_cont_ap_enterprise() {return common_cont_ap_enterprise;}
	public Optional<Double> getProfessional_cont_base() {return professional_cont_base;}
	public Optional<Double> getAt_ep_type() {return at_ep_type;}
	public Optional<Double> getAt_ep_ap_enterprise() {return at_ep_ap_enterprise;}
	public Optional<Double> getUnemployment_type() {return unemployment_type;}
	public Optional<Double> getUnemployment_ap_enterprise() {return unemployment_ap_enterprise;}
	public Optional<Double> getProfes_form_type() {return profes_form_type;}
	public Optional<Double> getProfes_form_ap_enterprise() {return profes_form_ap_enterprise;}
	public Optional<Double> getFogasa_type() {return fogasa_type;}
	public Optional<Double> getFogasa_ap_enterprise() {return fogasa_ap_enterprise;}
	public Optional<Double> getForce_majeure_base() {return force_majeure_base;}
	public Optional<Double> getForce_majeure_type() {return force_majeure_type;}
	public Optional<Double> getForce_majeure_ap_enterprise() {return force_majeure_ap_enterprise;}
	public Optional<Double> getNo_struct_base() {return no_struct_base;}
	public Optional<Double> getNo_struct_type() {return no_struct_type;}
	public Optional<Double> getNo_struct_ap_enterprise() {return no_struct_ap_enterprise;}
	public Optional<Double> getIrpf_esp() {return irpf_esp;}
	public Optional<Double> getIrpf_retrib_diner() {return irpf_retrib_diner;}
	public Optional<Double> getTotal() {return total;}
	

	
	public static class Contingency_bases_builder{
		
		private Optional<Double> monthly_amount;
		private Optional<Double> extra_proration_amount;
		
		private Optional<Double> common_cont_base;
		private Optional<Double> common_cont_type;
		private Optional<Double> common_cont_ap_enterprise;
		
		private Optional<Double> professional_cont_base;
		
		private Optional<Double> at_ep_type;
		private Optional<Double> at_ep_ap_enterprise;
		
		private Optional<Double> unemployment_type;
		private Optional<Double> unemployment_ap_enterprise;
		
		private Optional<Double> profes_form_type;
		private Optional<Double> profes_form_ap_enterprise;
		
		private Optional<Double> fogasa_type;
		private Optional<Double> fogasa_ap_enterprise;
		
		private Optional<Double> force_majeure_base;
		private Optional<Double> force_majeure_type;
		private Optional<Double> force_majeure_ap_enterprise;
		
		private Optional<Double> no_struct_base;
		private Optional<Double> no_struct_type;
		private Optional<Double> no_struct_ap_enterprise;
		
		private Optional<Double> irpf_esp;
		private Optional<Double> irpf_retrib_diner;
		private Optional<Double> total;
		
		public Contingency_bases_builder setMonthly_amount(Optional<Double> monthly_amount) {
			this.monthly_amount = monthly_amount; 
			return this;
		}
		public Contingency_bases_builder setExtra_proration_amount(Optional<Double> extra_proration_amount) {
			this.extra_proration_amount = extra_proration_amount;
			return this;
		}
		public Contingency_bases_builder setCommon_cont_base(Optional<Double> common_cont_base) {
			this.common_cont_base = common_cont_base;
			return this;
		}
		public Contingency_bases_builder setCommon_cont_type(Optional<Double> common_cont_type) {
			this.common_cont_type = common_cont_type;
			return this;
		}
		public Contingency_bases_builder setCommon_cont_ap_enterprise(Optional<Double> common_cont_ap_enterprise) {
			this.common_cont_ap_enterprise = common_cont_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setProfessional_cont_base(Optional<Double> professional_cont_base) {
			this.professional_cont_base = professional_cont_base;
			return this;
		}
		public Contingency_bases_builder setAt_ep_type(Optional<Double> at_ep_type) {
			this.at_ep_type = at_ep_type;
			return this;
		}
		public Contingency_bases_builder setAt_ep_ap_enterprise(Optional<Double> at_ep_ap_enterprise) {
			this.at_ep_ap_enterprise = at_ep_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setUnemployment_type(Optional<Double> unemployment_type) {
			this.unemployment_type = unemployment_type;
			return this;
		}
		public Contingency_bases_builder setUnemployment_ap_enterprise(Optional<Double> unemployment_ap_enterprise) {
			this.unemployment_ap_enterprise = unemployment_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setProfes_form_type(Optional<Double> profes_form_type) {
			this.profes_form_type = profes_form_type;
			return this;
		}
		public Contingency_bases_builder setProfes_form_ap_enterprise(Optional<Double> profes_form_ap_enterprise) {
			this.profes_form_ap_enterprise = profes_form_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setFogasa_type(Optional<Double> fogasa_type) {
			this.fogasa_type = fogasa_type;
			return this;
		}
		public Contingency_bases_builder setFogasa_ap_enterprise(Optional<Double> fogasa_ap_enterprise) {
			this.fogasa_ap_enterprise = fogasa_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setForce_majeure_base(Optional<Double> force_majeure_base) {
			this.force_majeure_base = force_majeure_base;
			return this;
		}
		public Contingency_bases_builder setForce_majeure_type(Optional<Double> force_majeure_type) {
			this.force_majeure_type = force_majeure_type;
			return this;
		}
		public Contingency_bases_builder setForce_majeure_ap_enterprise(Optional<Double> force_majeure_ap_enterprise) {
			this.force_majeure_ap_enterprise = force_majeure_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setNo_struct_base(Optional<Double> no_struct_base) {
			this.no_struct_base = no_struct_base;
			return this;
		}
		public Contingency_bases_builder setNo_struct_type(Optional<Double> no_struct_type) {
			this.no_struct_type = no_struct_type;
			return this;
		}
		public Contingency_bases_builder setNo_struct_ap_enterprise(Optional<Double> no_struct_ap_enterprise) {
			this.no_struct_ap_enterprise = no_struct_ap_enterprise;
			return this;
		}
		public Contingency_bases_builder setIrpf_esp(Optional<Double> irpf_esp) {
			this.irpf_esp = irpf_esp;
			return this;
		}
		public Contingency_bases_builder setIrpf_retrib_diner(Optional<Double> irpf_retrib_diner) {
			this.irpf_retrib_diner = irpf_retrib_diner;
			return this;
		}
		public Contingency_bases_builder setTotal(Optional<Double> total) {
			this.total = total;
			return this;
		}
		
		public Contingency_bases build() {
			
			Contingency_bases c = new Contingency_bases();
			
			c.monthly_amount = 				this.monthly_amount;
			c.extra_proration_amount = 		this.extra_proration_amount;
			c.common_cont_base = 			this.common_cont_base;
			c.common_cont_type = 			this.common_cont_type;
			c.common_cont_ap_enterprise = 	this.common_cont_ap_enterprise;
			c.professional_cont_base = 		this.professional_cont_base;
			c.at_ep_type = 					this.at_ep_type;
			c.at_ep_ap_enterprise = 		this.at_ep_ap_enterprise;
			c.unemployment_type = 			this.unemployment_type;
			c.unemployment_ap_enterprise = 	this.unemployment_ap_enterprise;
			c.profes_form_type = 			this.profes_form_type;
			c.profes_form_ap_enterprise = 	this.profes_form_ap_enterprise;
			c.fogasa_type = 				this.fogasa_type;
			c.fogasa_ap_enterprise = 		this.fogasa_ap_enterprise;
			c.force_majeure_base = 			this.force_majeure_base;
			c.force_majeure_type = 			this.force_majeure_type;
			c.force_majeure_ap_enterprise = this.force_majeure_ap_enterprise;
			c.no_struct_base =				this.no_struct_base;
			c.no_struct_ap_enterprise =		this.no_struct_ap_enterprise;
			c.no_struct_type =				this.no_struct_type;
			c.irpf_esp =					this.irpf_esp;
			c.irpf_retrib_diner = 			this.irpf_retrib_diner;
			c.total =						this.total;
			
			return c;
		}
		
	}
}


