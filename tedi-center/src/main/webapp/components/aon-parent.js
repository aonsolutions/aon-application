
class AonParent extends HTMLElement {
	constructor () {
		super();
	}
	
	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>
				.aon-avatar {
					float: left;
					margin-right: 16px;
					height: 40px;
					width: 40px;
					box-sizing: border-box;
					border-radius: 50%;	
					font-size: 40px;
				}
				
				.aon-li {
				    border: 1px solid #ddd;
				}
			</style>
			<ul class="demo-list-two mdl-list">
				<li class="mdl-list__item mdl-list__item--two-line aon-li">
					<span class="mdl-list__item-primary-content">
						<i class="material-icons aon-avatar">business</i>
						<span>AON SOLUTIONS SL</span>
						<span class="mdl-list__item-sub-title">B01487271</span>
					</span>					
				</li>
			</ul>
			`;
  }
}

window.customElements.define('aon-parent', AonParent);
