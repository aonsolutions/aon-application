class AonParent extends HTMLElement {

	companies;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<ul id="aon-company-list" class="demo-list-two mdl-list aonCompanyList"></ul>
		`;
		this.init();
 	}

	init() {
    getCompanies().then( r => {
			  if(!r.end){
          this.init();
        }
        this.build(r.companies)
      }, () => this.closeSession()
    );
  }

 	build(companies) {
		let list = document.getElementById("aon-company-list");
		list.innerHTML = '';
		for(let i = 0; i < companies.length; i++){
			list.appendChild(this.buildLi(companies[i], (i === 0 || i%2 === 0) ? '#f1f1f1' : 'transparent'));
		}


	}

	buildLi(company, color) {
		let li = document.createElement('li');
		li.className = 'mdl-list__item mdl-list__item--two-line aonLi';
		li.style.backgroundColor = color;
		li.addEventListener('click', () => {
			companySelection(company);
		});

		li.addEventListener('mouseover', () => {
			li.style.backgroundColor = '#ddd';
		});

		li.addEventListener('mouseleave', () => {
			li.style.backgroundColor = color;
		});

		let span = document.createElement('span');
		span.className = 'mdl-list__item-primary-content';

		let i = document.createElement('i');
		i.className = 'material-icons aonAvatar';
		i.innerHTML = 'business';

		let span2 = document.createElement('span');
		span2.innerHTML = company.name;

		let span3 = document.createElement('span');
		span3.className = 'mdl-list__item-sub-title';
		span3.innerHTML = company.document;

		span.appendChild(i);
		span.appendChild(span2);
		span.appendChild(span3);
		li.appendChild(span);
		return li;
	}

}
window.customElements.define('aon-parent', AonParent);
