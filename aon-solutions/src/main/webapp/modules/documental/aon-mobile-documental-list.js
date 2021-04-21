import {AonMobileList} from '../../components/aon-mobile-list.js';
import {getDocuments} from '../../services/service.js';

export class AonMobileDocumentalList extends AonMobileList {

  more;

  constructor () {
    super();
    this.more = true;
  }

  connectedCallback () {
    this.init();
    this.addEventListener('more', () => {
			if(this.more)
				this.loadMore()
		});
  }

  loadMore() {
    let filter = this.getFilter();
    if(filter.page) {
      filter.page = filter.page + 1;
      this.setFilter(filter);
      getDocuments(filter).then(documents => {
        if(documents.length == 0)
          this.more = false;
        documents.forEach((doc, i) => this.addRow(doc, i));
      });
    }
  }

  init() {
    let application = this.getApplication();
    let toolbar = this.getElement(application.TOOLBAR);
    toolbar.removeButtons();
    this.build();
    getDocuments(this.getFilter()).then(documents => {
      documents.forEach((doc, i) => this.addRow(doc, i));
    });
  }

  addRow(doc, i) {
    let liValue = {
      aonIcon: this.getTypeIcon(doc.file.type),
      title: doc.title,
      subtitle:  doc.date + ' - ' + doc.size
    }
    this.addLi(liValue, i, () => this.aonDocument(doc, i));
  }

  getTypeIcon(type) {
    if(type.includes('pdf')) {
      return 'aon_pdf';
    } else if(type.includes('powerpoint') || type.includes('presentation')){
      return 'aon_powerpoint'
    } else if(type.includes('excel') || type.includes('spreadsheet')){
      return 'aon_excel'
    } else if(type.includes('word') || type.includes('text')){
      return 'aon_word'
    } else if(type.includes('image')) {
      return 'aon_image'
    } else {
      return 'aon_file'
    }
  }

  aonDocument(doc, i) {
    this.getApplication().setContentHTML(`<aon-mobile-document document='${JSON.stringify(doc)}'> </aon-mobile-invoice>`);
	}
}
window.customElements.define('aon-mobile-documental-list', AonMobileDocumentalList);
