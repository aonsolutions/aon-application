class AonSignin extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON SIGNIN TOOLBAR -->
			<aon-toolbar id="aonSignin" title="FICHAJE"></aon-toolbar>
			<!-- AON SIGNIN MENU (SIDENAV) -->
			<div id="aonSigninSidenav" class="sidenav">
				<ul class="aonClip">
					<li id="aonMessengerAdmin" class="aonAppMenuSidenavList aonOpacity" >
						<span class="aonMenuItemSpan"> Administradores </span>
					</li>

					<li id="aonSigninEmployee" class="aonAppMenuSidenavList aonOpacity" >
						<span class="aonMenuItemSpan"> Empleados	</span>
					</li>
				</ul>
			</div>

			<!-- AON SIGNIN CONTENT -->
			<div id="aonSigninContent" class="aonContent">

			</div>

		`;
    this.build();
 	}

 	build() {
		let listIds = ['aonMessengerAdmin', 'aonSigninEmployee'];
		listIds.forEach((id, i) => {
				let el = document.getElementById(id);

				el.addEventListener('mouseover', () => {
					if(!this.selected || this.selected !== id)
						el.style.backgroundColor = '#f1f1f1';
				});
				el.addEventListener('mouseleave', () => {
					if(!this.selected || this.selected !== id)
						el.style.backgroundColor = 'transparent';
				});
				el.addEventListener('click', () => {
					listIds.forEach((id, i) => {
						let el1 = document.getElementById(id);
						el1.style.backgroundColor = 'transparent';
					});
					this.selected = id;
					el.style.backgroundColor = '#ddd';
				});
		});
	}
}
window.customElements.define('aon-signin', AonSignin);
