import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js';
import { elaborationStatuses } from '../../../models/elaboration/elaborationStatus.js';
import { getElaboration, getElaborations, getWarehouses } from '../../../services/warehouseService.js';
import { AonMobileElaboration } from './aon-mobile-elaboration.js';

export class AonMobileElaborationList extends AonMobileList {

    more;
    filtro;
    constructor () {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
        this.buildSearch();
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
        this.filtro = this.filtro || {
            page:1,
            perPage:30
        }
    }

	buildSearch(){
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

		getWarehouses().then(warehouses => {
			let options = [{
				type: CONSTANT.DATE,
				name: "startDate",
				id: "startDate",
				title: MSG.FROM,
			  },
			  {
				type: CONSTANT.DATE,
				name: "endDate",
				id: "endDate",
				title: MSG.TO,
			  },{
				type: CONSTANT.SELECT,
				name: "status",
				id: "status",
				title: MSG.STATUS,
				options: JSON.stringify(elaborationStatuses)
			  }];
			if(warehouses.length > 1) {
				options.push({
					type: CONSTANT.SELECT,
					name: "warehouse",
					id: "warehouse",
					title: MSG.WAREHOUSE,
					options: JSON.stringify(warehouses.map(r => {
						let w = {
							value: r.id,			
							name: r.name
						};
						return w;
					}))
				})
			}
			btnSearch.buildOptionsFilter(options);
		});
    }

	search(detail) {
		this.filtro.value = detail.search;
		this.filtro.warehouse = detail.warehouse;
		this.filtro.from = detail.startDate;
		this.filtro.to = detail.endDate; 
		this.filtro.status = detail.status;
		this.filtro.page = 1;
		this.filtro.perPage = 30;
		this.init();
	}

    loadMore() {
        if(this.filtro.page) {
            this.filtro.page = this.filtro.page + 1;
            getElaborations(this.filtro).then(elaborations => {
                if(elaborations.length == 0)
                    this.more = false;
                elaborations.forEach((elaboration, i) => this.addRow(elaboration, i));
            });
        }
    }

    init() {
        this.build();
        getElaborations(this.filtro).then(elaborations => {
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
}
if(!window.customElements.get(TAG.AON_MOBILE_ELABORATION_LIST)){
    window.customElements.define(TAG.AON_MOBILE_ELABORATION_LIST, AonMobileElaborationList);
}