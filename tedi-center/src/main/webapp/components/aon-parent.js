
function build() {
	getCompanies()
	.then(companies => {

		let list = document.getElementById("aon-company-list");
		let html = '';
		for(let i = 0; i < companies.length; i++){
			html = html + `
				<li class="mdl-list__item mdl-list__item--two-line aon-li" style="cursor:pointer;"
						 onclick="companySelection('${companies[i].domain}','${companies[i].id}' )">
					<span class="mdl-list__item-primary-content">
						<i class="material-icons aon-avatar">business</i>
						<span> ${companies[i].name}</span>
						<span class="mdl-list__item-sub-title">${companies[i].document}</span>
					</span>
				</li>
			`;
		}
		list.innerHTML = html;
	});
}

class AonParent extends HTMLElement {
	constructor () {

		build();
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<script>



			</script>

			<ul id="aon-company-list" class="demo-list-two mdl-list">

			</ul>
			`;
 	}


}
window.customElements.define('aon-parent', AonParent);
