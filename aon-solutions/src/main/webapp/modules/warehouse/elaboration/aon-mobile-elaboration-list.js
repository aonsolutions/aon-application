import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS, TAG } from '../../../environments/environments.js';
import { getElaboration, getElaborations } from '../../../services/warehouseService.js';
import { AonMobileElaboration } from './aon-mobile-elaboration.js';

export class AonMobileElaborationList extends AonMobileList {

    more;

    constructor () {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };


    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        this.more = true;
        this.filter = this.filter || {
            page:1,
            perPage:30
        }
    }

    loadMore() {
        let filter = this.getFilter();
        if(filter.page) {
            filter.page = filter.page + 1;
            this.setFilter(filter);
            getElaborations(filter).then(elaborations => {
                if(elaborations.length == 0)
                    this.more = false;
                elaborations.forEach((elaboration, i) => this.addRow(elaboration, i));
            });
        }
    }

    init() {
        this.build();
        getElaborations({
            page:1,
            perPage:30
        }).then(elaborations => {
            if(elaborations.length == 0){   
                this.empty();
            }
            elaborations.forEach((elaboration, i) => this.addRow(elaboration, i));
        });        
    }

    addRow(elaboration, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PRECISION_MANUFACTURING,
            title: elaboration.reference,
            subtitle: elaboration.description
        }
        this.addLi(liValue, i, () => this.aonElaboration(elaboration, i));
    }

    aonElaboration(elaboration, i) {
        getElaboration(elaboration.id).then(el => {
            let aonElaboration = new AonMobileElaboration();
            aonElaboration.setElaboration(el);
            this.getApplication().setContent(aonElaboration);
        });
    }
    
    getFilter() {
		return this.filter || {
            page:1,
            perPage:30
        };
	}

    setFilter(filter) {		
		this.filter = JSON.stringify(filter);
        this.init();
	}
}
if(!window.customElements.get(TAG.AON_MOBILE_ELABORATION_LIST)){
    window.customElements.define(TAG.AON_MOBILE_ELABORATION_LIST, AonMobileElaborationList);
}