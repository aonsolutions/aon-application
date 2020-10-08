class AonFaqs extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonFaqs" title="FAQs"></aon-application>
		`;
		this.build();
 	}

 	build() {
		let aonFaqs = document.getElementById('aonFaqs');

		aonFaqs.addToolbarOption('Add', 'add', () => {this.loadCreate()});

		let options = [
			{
				name: 'Index',
				icon: 'message',
				fn: () => this.loadIndex()
			},
			{
				name: 'Crear',
				icon: 'message',
				fn: () => this.loadCreate()
			},
			{
				name: 'Show',
				icon: 'message',
				fn: () => this.loadShow()
			}
		];
		aonFaqs.addSidenavOptions('OPCIONES', options);
		this.loadIndex();
	}

	loadIndex() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadCreate() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="./create.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadShow() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="./show.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-faqs', AonFaqs);
