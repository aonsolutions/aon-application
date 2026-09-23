import {getCustomers, getCustomer, getScopes, getTarget, downloadRegistryExcel} from '../../../services/service.js';
import { EVENT, TAG} from '../../../environments/environments.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonCustomer } from './aon-customer.js';
import * as ACTION from '../../actions.js';
import { OfficeUtils } from '../../office/OfficeUtils.js';
import { getProjectTypes } from '../../../services/projectService.js';
import { OfficeEnums } from '../../office/OfficeEnums.js';

import * as LS from '../../../services/localStorageService.js';

export class AonCustomerList extends AonRegistryList {

	parent;
	office;
	clientFile;
	
	// checkbox 5 + 10 + 24 + 13 + 9 + 9 + 9 + 6 + 10 + 5 = 100
	columnWidths = {
		document: '10%',
		name:     '24%',
		alias:    '13%',
		status:   '9%',
		link:     '6%'
	};

	constructor(parent) {
		super();
		this.parent = parent;
	}

	build(){
		this.buildDur().then(() => {
			super.build();
		});
	}
	
	async getRegistries() {
		this.filter = { ...this.filter, additional_info: ["MEDIA", "STATUS_NOTE"] };
		let customers = await getCustomers(this.filter);

		(customers || []).forEach(c => {
			c.contact = this.buildContactCell(c.media);

			// F. Estado: fecha de la ultima nota de estado o, si no la hay,
			// la creacion del registro (ya viene resuelto desde el back)
			c.statusDateText = this.formatShortDate(c.statusDate);

			c.info = this.buildInfoCell(c.statusReason);

			// OJO AL ORDEN: se calcula antes de tocar c.status, que es lo que
			// distingue un bloqueo programado de uno ya efectivo
			c.blockDate = this.buildBlockDate(c);

			// La columna Estado usa MSG[c.status]. Ponemos el estado efectivo
			// para que lo que se ve coincida con lo que filtra: un BLOCKED con
			// fecha futura se lista como ACTIVE.
			// Es seguro sobrescribirlo: buildRegistry() vuelve a pedir el
			// cliente completo al servidor usando solo el id.
			if (c.effectiveStatus) c.status = c.effectiveStatus;
		});

		return customers;
	}
 
	/**
	 * Devuelve la fecha solo cuando el bloqueo esta programado y aun no ha
	 * llegado. Si ya vencio, el cliente sale como Bloqueado y la fecha sobra.
	 */
	buildBlockDate(customer) {
		const scheduled = customer.status === "BLOCKED"
				&& customer.effectiveStatus === "ACTIVE";
 
		return scheduled ? this.formatShortDate(customer.expirationDate) : '';
	}
 
	/** 'yyyy-MM-dd' -> 'dd/MM/yyyy' */
	formatShortDate(value) {
		if (!value) return '';
 
		const parts = String(value).split("-");
		if (parts.length !== 3) return String(value);
 
		return `${parts[2]}/${parts[1]}/${parts[0]}`;
	}
 
	addColumnsAfterStatus() {
		this.TABLE.addColumn('F. Estado', 'string', 'statusDateText', '9%');
		this.TABLE.addColumn('F. Bloqueo', 'string', 'blockDate', '9%');
	}

	addCustomColumns() {
		this.TABLE.addColumn('Contacto', 'html', 'contact', '10%');
		this.TABLE.addColumn('', 'html', 'info', '5%');   // sin titulo
	}
	
	buildContactCell(media) {
	    const list = Array.isArray(media) ? media : (media ? [media] : []);
	    const find = type => list.find(m => (m?.media || '').toLowerCase() === type);
	
	    const wrap = document.createElement('span');
	    wrap.style.display = 'inline-flex';
	    wrap.style.gap = '8px';
	
	    const slot = (m, iconName) => {
	        const i = document.createElement('i');
	        i.className = 'material-icons';
	        i.textContent = iconName;
	        i.style.width = '24px';          // ancho fijo => alineación entre filas
	        i.style.textAlign = 'center';
	        if (m) {
	            i.style.color = '#5f6368';
	            i.title = m.value;           // el value en el tooltip
	        } else {
	            i.style.visibility = 'hidden'; // reserva el hueco sin pintar icono
	        }
	        return i;
	    };
	
	    wrap.appendChild(slot(find('cellular'), 'phone'));
	    wrap.appendChild(slot(find('email'), 'email'));
	    return wrap;
	}

	/**
	 * Icono de info con el motivo del ultimo estado. Sin motivo no se pinta
	 * icono, pero hay que devolver siempre un nodo: AonTable hace appendChild
	 * directo sobre el valor de las columnas 'html'.
	 */
	buildInfoCell(reason) {
		const wrap = document.createElement('span');
		if (!reason) return wrap;

		const i = document.createElement('i');
		i.className = 'material-icons';
		i.textContent = 'info';
		i.style.color = '#5f6368';
		i.title = reason;
		wrap.appendChild(i);

		return wrap;
	}

	async getCustomerCustom(registry){
		if(registry && registry.id){
			let additional_info = ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RSEGMENT', 'STATUS_NOTE'];
			if(this.isOffice()) {
				additional_info.push('REGISTRY_COMPANY');
				additional_info.push('RRELATIONSHIP');
			}
			let data = {
				id: registry.id,
				additional_info
			};

			return this.filter.type == "false" ? getTarget(data) : getCustomer(data);
		}
		
		return null;
	}

	buildRegistry(registry) {
		this.getCustomerCustom(registry)
		.then(r => {
			let aonCustomer = new AonCustomer();
			aonCustomer.id = this.getApplication().id + 'Customer';
			aonCustomer.setCustomer(r);
			aonCustomer.setCustomerList(this);
			aonCustomer.setOffice(this.office);
			aonCustomer.setClientFile(this.clientFile);
			this.getApplication().setContent(aonCustomer);
		});
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addToolbarOption2(ACTION.ADD, () => this.buildRegistry());
		if(this.getDur()) {
			const isUdapa = this.getDur().getDomain().getName().includes("udapa") || this.getDur().getDomain().getName().includes("paturpat");
			if(isUdapa) this.getApplication().addToolbarOption2(ACTION.DOWNLOAD_EXCEL, () => this.downloadExcel('customer'));
		}
		this.buildSearch();
	}

	buildSearch(){
		let timeOut = null;

		let btnSearch = this.getApplication().addSearchOption(true);
		this.btnSearch = btnSearch;
		
		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.filter = {
					...this.filter,
					value:detail.search,
					scope:detail.scope,
					projectType: detail.projectType,
					rrelationship: detail.rrelationship,
					status: OfficeUtils.getCustomerStatus(detail),
					statusDateFrom: detail.statusDateFrom,
					statusDateTo: detail.statusDateTo,
					blockDateFrom: detail.blockDateFrom,
					blockDateTo: detail.blockDateTo,
					type : detail.type,
					page:1,
					isSig: this.isSig()
					
				}
				this.setFilter(this.filter);
				this.parent.setFilterCustomers(this.filter);
				let filterCount = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchCountFilter");
				filterCount.style.display = 'none';
			}, 300);
		});

		btnSearch.addEventListener(EVENT.RESET_FILTER, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
				this.filter = {
					page: 1,
					perPage: 50,
					status: ["ACTIVE", "BLOCKED"],
					isSig: this.isSig()
				}
				this.setFilter(this.filter);
				this.parent.setFilterCustomers(this.filter);
				this.setSearchValues();	
				
				let filterCount = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchCountFilter");
				if (filterCount) filterCount.style.display = 'none';
			}, 300);
		});

		let searchInput = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchSearchInput");
		searchInput.placeholder = "Buscar por nombre, documento o alias";
		searchInput.focus();

		btnSearch.buildOptionsFilter(OfficeEnums.getCustomerFilter());//INPUTS
		this.setSearchValues();	
    }

    setSearchValues(){
		let searchInput = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearchSearchInput");
		let searchValue = this.filter.value;
		searchInput.value = searchValue ? searchValue : '';
		
		let scopeEl = this.getElement("scope");
		
		getScopes().then(scopes => {
		    const options = [
		        { name: 'Sin ámbito', value: -1 },
		        ...scopes.map(c => ({ ...c, value: c.id }))
		    ];
		
		    scopeEl.setOptions(options);
		    scopeEl.setValue(this.filter.scope);
		});

        let projectTypeEl = this.getElement("projectType");
        getProjectTypes({})
        .then(t => {
            let types = (t || []).map((r) => ({...r, name: r.description, value: r.id}));

            projectTypeEl.setOptions(types);

            const value = this.filter.projectType;
			projectTypeEl.setValue(value);
        })

        const rrelationshipEl = this.getElement("rrelationship");
        rrelationshipEl.setOptions([
			{name:'Con empresa', value:true},
			{name:'Sin empresa', value:false}
		]);

		const rrelationship = this.filter.rrelationship;
		let input = rrelationshipEl.getInput();
		if(rrelationship){
			input.value = rrelationship == "false" ? "Sin empresa" : "Con empresa";
			rrelationshipEl.setValue("'" + rrelationship + "'");
		} else {
			input.value = '';
			rrelationshipEl.setValue('');
		}

        let active = this.getElement("active");
		if((this.filter.status ||  []).includes("ACTIVE")){
			active.value = true;
		} else {
			this.clearFilterComponent(active);
		}

		let inactive = this.getElement("inactive");
		if((this.filter.status ||  []).includes("INACTIVE")){
			inactive.value = true;
		} else {
			this.clearFilterComponent(inactive);
		}

		let blocked = this.getElement("blocked");
        if((this.filter.status ||  []).includes("BLOCKED")){
			blocked.value = true;
		} else {
			this.clearFilterComponent(blocked);
		}

		const typeEl = this.getElement("type");
        typeEl.setOptions([
			{name:'Cliente', value:true},
			{name:'Cliente Potencial', value:false}
		]);
		const type = this.filter.type;
		let typeInput = typeEl.getInput();
		if(type){
			typeInput.value = type == "false" ? "Cliente Potencial" : "Cliente";
			typeEl.setValue(type);
		}else {
			typeInput.value = '';
			typeEl.setValue('');
		}
		
		// Los rangos de fecha no se reconstruyen: buildOptionsFilter solo se
		// llama una vez y el nodo es el mismo, asi que hay que repoblarlos
		// a mano igual que el resto de campos
		this.setDateFilterValues();

		// Target no tiene ni notas de estado ni expiracion
		typeEl.addEventListener(EVENT.SELECT, () => this.toggleDateFilters(typeEl.value));
		this.toggleDateFilters(type);
	}
	
	/**
	 * Los filtros de fecha solo aplican a clientes. Al ocultarlos se limpian:
	 * getValues() serializa el formulario entero y arrastraria valores que el
	 * usuario ya no ve.
	 */
	toggleDateFilters(type) {
		const hidden = String(type) === "false";

		["statusDateRange", "blockDateRange"].forEach(id => {
			const wrap = this.getElement(id);
			if (wrap) wrap.style.display = hidden ? 'none' : '';
		});

		if (hidden) {
			["statusDateFrom", "statusDateTo", "blockDateFrom", "blockDateTo"]
			.forEach(name => {
				const el = this.getElement(name);
				if (el && el.clear) el.clear();
			});
		}
	}
	
	setDateFilterValues() {
		["statusDateFrom", "statusDateTo", "blockDateFrom", "blockDateTo"]
		.forEach(name => {
			const el = this.getElement(name);
			if (!el) return;

			const value = this.filter[name];

			if (value) {
				if (typeof el.setDate === 'function') el.setDate(value);
				else el.value = value;
			} else {
				this.clearFilterComponent(el);
			}
		});
	}
	
	/**
	 * Reset visual de un componente del filtro. No basta con clear(): en varios
	 * componentes solo resetea el estado interno y el input pintado se queda
	 * con el valor anterior (clearFormData de AonSearch usa setDate('') y
	 * checked = false por este mismo motivo).
	 */
	clearFilterComponent(el) {
		if (!el) return;

		if (typeof el.setDate === 'function') el.setDate('');
		else if (typeof el.clear === 'function') el.clear();

		if ('checked' in el) el.checked = false;

		// Ultimo recurso: el input real, que es lo que ve el usuario
		el.querySelectorAll('input').forEach(input => {
			if (input.type === 'checkbox' || input.type === 'radio') input.checked = false;
			else input.value = '';
		});
	}

	downloadExcel(type) {
		let data = {
		  domainId: LS.getDomainId(),
		  domainName: LS.getDomainName(),
		  domainLogin: LS.getDomainLogin(),
		  type
		};
		let json = btoa(JSON.stringify(data));
		downloadRegistryExcel(json);
	}
	
	isOffice() {
		return this.office;
	}

	setOffice(office) {
		this.office = office;
	}
	
	isClientFile() {
		return this.clientFile;
	}

	setClientFile(clientFile) {
		this.clientFile = clientFile;
	}

}

if(!window.customElements.get(TAG.AON_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_CUSTOMER_LIST, AonCustomerList);
}
