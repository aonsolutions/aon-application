(function() {

	class AonCard extends HTMLElement {

		static get observedAttributes() {
			return ['visible'];
		}

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

		get visible() {
			return this.getAttribute('visible');
		}

		set visible(visible) {
			this.setAttribute('visible', visible);
		}

		attributeChangedCallback(name, oldValue, newValue) {
			if('visible' === name){
				if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
					this.style.display = 'none';
				} else {
					this.style.display = 'block';
				}
			}
		}

		constructor () {
			super();
		}

		connectedCallback () {
			this.innerHTML = `
				<div class="aonCard">
					<div id="${this.getId() + 'Title'}">

					</div>
					<div id="${this.getId() + 'Content'}">

					</div>

				</div>
			`;
			this.build();
		}

		build() {
			let title = document.getElementById(this.getId() + 'Title')
			title.innerHTML = this.getTitle();
		}

		setContent(el) {
			let content = document.getElementById(this.getId() + 'Content');
			content.appendChild(el);
		}

		setContentHTML(html) {
			let content = document.getElementById(this.getId() + 'Content');
			content.innerHTML = html;
		}

		setVisible(visible) {
			this.setAttribute('visible', visible)
		}

		getId() {
			return this.getAttribute('id');
		}

		getTitle() {
			return this.getAttribute('title');
		}
	}

	window.customElements.define('aon-card',  AonCard);

})();
