import './aon-company.js'
import { getCompanies} from  '../../services/service.js';


(function() {

	const html = `
	<table class="mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp aonTable">
	  <thead>
	    <tr>
	      <th class="mdl-data-table__cell--non-numeric">Razón Social</th>
	      <th class="mdl-data-table__cell--non-numeric">CIF</th>
	      <th class="mdl-data-table__cell--non-numeric"></th>
	    </tr>
	  </thead>
	  <tbody id="company-tbody">

	  </tbody>
	</table>
	`;

class AonCompanyList extends HTMLElement {

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	constructor () {
		super();
		this.innerHTML = html;
		let parentId = parseInt(this.getAttribute('company'));
		getCompanies().then(r => {
      this.build(r.companies.filter(f => !f.parent && f.parentId === parentId ))
		});
	}

	connectedCallback () {

 	}

 	build(companies) {
		let tbody = document.getElementById('company-tbody');
		for(let i = 0; i < companies.length; i++) {
			let company = companies[i];
			let event = new CustomEvent('select', { 'detail': company });
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';

			let td1 = document.createElement('td');
			td1.className = 'mdl-data-table__cell--non-numeric';
			td1.innerHTML = company.name ? company.name : '';
			td1.addEventListener('click', () => this.dispatchEvent(event));

			let td2 = document.createElement('td');
			td2.className = 'mdl-data-table__cell--non-numeric';
			td2.innerHTML = company.document ? company.document : '';
			td2.addEventListener('click', () => this.dispatchEvent(event));

			// TODO:
			let td5 = document.createElement('td');
			td5.className = 'mdl-data-table__cell--non-numeric';
			td5.innerHTML = '<aon-icon-button id="aonCompanyListSecurityButton-'+ company.id +'" icon="edit"></aon-icon-button>';
			td5.addEventListener('click', () => this.dispatchEvent(event));

			tr.appendChild(td1);
			tr.appendChild(td2);
			tr.appendChild(td5);


			tbody.appendChild(tr);

			let aonCompanyListSecurityButton = document.getElementById('aonCompanyListSecurityButton-' + company.id);
			aonCompanyListSecurityButton.addEventListener('click', () => {
				let content = document.getElementById('aonConfigurationContent');
				content.innerHTML = '<aon-company id="aonCompany-' + company.id + '" ><aon-company>';
				let aonCompany = document.getElementById('aonCompany-' + company.id);
				aonCompany.style.display = "flex";
				aonCompany.style.width = "100%";
				aonCompany.setAttribute('company', JSON.stringify(company));
			})
		}

	}

}
window.customElements.define('aon-company-list', AonCompanyList);

})();
