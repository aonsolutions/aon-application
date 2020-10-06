

class AonDialog extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get title() {
		return this.getAttribute('title');
	}

	set title(title) {
		this.setAttribute('title', title);
	}

	get type() {
		return this.getAttribute('type');
	}

	set type(type) {
		this.setAttribute('type', type);
	}

	constructor () {
		super();
	}

	connectedCallback () {

		this.innerHTML = `
		<div id="${this.getAttribute('id') + 'Dialog'}" class="aonDialog">
		<!-- Modal content -->
			<div id="${this.getAttribute('id') + 'DialogContent'}" class="aonDialogContent">

			</div>
			<!-- <div id="${this.getAttribute('id') + 'DialogAction'}">


			</div> -->
		</div>
		`;
		this.build();
  }

	build() {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog');
		window.onclick = (event) => {
  		if (event.target == dialog) {
				this.close();
  		}
		}
	}

	open(){
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog')
		dialog.style.display = 'block';
	}

	close() {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog')
		dialog.style.display = 'none';
	}

	setContent(widget) {
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.innerHTML = '';
		content.appendChild(widget);
	}

	setContentHtml(html) {
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.innerHTML = html;
	}

	// addCancelAction(fn) {
	// 	let cancel = document.getElementById(this.getAttribute('id') + 'ActionsCancelButton');
	// 	cancel.addEventListener('click', fn);
	// }
	//
	// addAcceptAction(fn) {
	// 	let accept = document.getElementById(this.getAttribute('id') + 'ActionsAcceptButton');
	// 	accept.addEventListener('click', fn);
	// }
}

window.customElements.define('aon-dialog', AonDialog);
