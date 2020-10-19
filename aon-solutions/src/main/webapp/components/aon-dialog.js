

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
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');

		if(this.isTypeMenu()){
			dialog.style.backgroundColor = 'transparent';
			dialog.style.paddingTop = '0px';

			content.style.position = 'absolute';
			content.style.width = '200px';
			content.style.padding = '0px';
			content.style.right = '100px';
			content.style.top = '90px';
		}

		window.onclick = (event) => {
  		if (event.target == dialog) {
				this.close();
  		}
		}

	}

	isTypeMenu() {
		return this.hasAttribute('type') && 'menu' === this.getAttribute('type');
	}

	open(){
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog')
		dialog.style.display = 'block';
	}

	close() {
		let dialog = document.getElementById(this.getAttribute('id') + 'Dialog')
		dialog.style.display = 'none';
	}

	getContent() {
		return document.getElementById(this.getAttribute('id') + 'DialogContent');
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

	setMenuOptions(options) {
		let content = document.getElementById(this.getAttribute('id') + 'DialogContent');
		content.innerHTML = '';
		let ul = document.createElement('ul');
		content.appendChild(ul);
		options.forEach((item, i) => {
			let li = document.createElement('li');
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);

			let ic = document.createElement('i');
			ic.className = 'material-icons';
			ic.style.verticalAlign = 'middle';
			ic.innerHTML = item.icon;
			li.appendChild(ic);

			let span = document.createElement('span');
			span.style.marginLeft = '5px';
			span.innerHTML = item.name;
			li.appendChild(span);
			li.addEventListener('click', () => {
				this.close();
				item.fn();
			});
		});
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
