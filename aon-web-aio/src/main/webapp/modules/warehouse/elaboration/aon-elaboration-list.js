import { CONSTANT, EVENT, MSG, TAG } from '../../../environments/environments.js';
import { AonList } from '../../../components/aon-list.js';
import { getElaboration, getElaborations } from '../../../services/warehouseService.js';
import { AonDateUtils } from '../../utils/AonDateUtils.js';
import { AonMobileElaboration } from './aon-mobile-elaboration.js';

export class AonElaborationList extends AonList {

    constructor () {
        super();
    }

    ELABORATION_SEARCH_OPTIONS = [
    {
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
    },
    // {
    //   type: CONSTANT.SELECT,
    //   name: "status",
    //   id: "status",
    //   title: MSG.STATUS
    // },
  ];

    initialize() {
        this.id = this.id || 'aonElaborationList';
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page:1,
            perPage:50
        }

        const btnSearch = this.getApplication().addSearchOption();
        btnSearch.addEventListener(EVENT.SEARCH_NEW, (event) => this.search(event.detail));
        btnSearch.buildOptionsFilter(this.ELABORATION_SEARCH_OPTIONS);
        // this.getElement('status').setOptions([
        //     { name: "-", value: undefined },
        //     { name: MSG.PENDING, value: "PENDING" },
        //     { name: MSG.INVOICED, value: "INVOICED" },
        //     { name: MSG.IN_PREPARATION, value: "IN_PREPARATION" },
        // ]);
        
        this.more = this.elaborations ? false : true;
        this.columns = [
            { name: MSG.DATE, type: 'date', id: 'dateTable', width: '120px' },
            { name: MSG.REFERENCE, type: 'string', id: 'reference', width: '120px' },
            { name: MSG.DESCRIPTION, type: 'string', id: 'description', width: 'auto' },
            { name: MSG.QUANTITY, type: 'number', id: 'quantity', width: '120px' }, 
            { name: MSG.WAREHOUSE, type: 'string', id: 'warehouseName', width: '120px' },
            { name: '', type: 'icons', id: 'icons', width: '50px' }
        ];
    }

    aonObject(object, i) {
       getElaboration(object.id).then(el => {
            let aonElaboration = new AonMobileElaboration();
            aonElaboration.setElaboration(el);
            aonElaboration.back = () => this.getApplication().setContent(new AonElaborationList());
            this.getApplication().setContent(aonElaboration);
        });
    }

    getObjects() {
        return new Promise((resolve, reject) => {
            getElaborations(this.getFilter())
            .then(objects => {
                resolve(objects.map(r => {
                    r.dateTable = AonDateUtils.formatDate(r.date);
                    r.warehouseName = r.warehouse.name;
                    r.icons = [];
                    return r;
                }));
            })
            .catch(e => reject(e));
        });
    }
}

if(!window.customElements.get(TAG.AON_ELABORATION_LIST)) {
    window.customElements.define(TAG.AON_ELABORATION_LIST, AonElaborationList);
}