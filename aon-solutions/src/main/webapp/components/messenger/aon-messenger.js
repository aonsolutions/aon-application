class AonMessenger extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON MESSENGER TOOLBAR -->
			<aon-toolbar id="aonMessenger" title="MESSENGER"></aon-toolbar>
			<!-- AON MESSENGER MENU (SIDENAV) -->
			<div id="aonMessengerSidenav" class="sidenav">
				<ul class="aonClip">
					<li id="aonMessengerIndex" class="aonAppMenuSidenavList aonOpacity" >
						<span class="aonMenuItemSpan"> Index </span>
						</li>

					<li id="aonMessengerCreate" class="aonAppMenuSidenavList aonOpacity" >
						<span class="aonMenuItemSpan"> Create	</span>
					</li>

					<li id="aonMessengerShow" class="aonAppMenuSidenavList aonOpacity" >
						<span class="aonMenuItemSpan"> Show	</span>
					</li>
				</ul>
			</div>

			<!-- AON MESSENGER CONTENT -->
			<div id="aonMessengerContent" class="aonContent">

			</div>

		`;
    this.build();
 	}

 	build() {
		let listIds = ['aonMessengerIndex', 'aonMessengerCreate', 'aonMessengerShow'];
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
window.customElements.define('aon-messenger', AonMessenger);
