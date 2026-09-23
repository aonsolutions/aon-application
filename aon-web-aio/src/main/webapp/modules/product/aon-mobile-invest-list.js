import { AonMobileList } from '../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { InvestAssetRegime, InvestAssetType } from '../../models/enums.js';
import { getInvestAssets } from '../../services/productService.js';
import { addList, setIndex, setList, setFilter as setCacheFilter } from './cache.js';
import { AonMobileInvest } from './aon-mobile-invest.js';

export class AonMobileInvestList extends AonMobileList {

    PER_PAGE = 30;

    more;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
        this.buildSearch();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore();
    };

    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        this.id = this.id || 'aonMobileInvestList';
        super.initialize();
        this.more = true;
    }

    buildSearch() {
        const btnSearch = this.getApplication().addSearchOption();
        let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
    }

    search(detail) {
        let filter = this.getFilter();
        filter.value = detail.search;
        super.setFilter(filter);
        this.init();
    }

    loadMore() {
        let filter = this.getFilter();
        if(!filter.page) return;
        this.more = false;
        filter.page = filter.page + 1;
        super.setFilter(filter);
        getInvestAssets(filter).then(investAssets => {
            addList(investAssets);
            if(investAssets.length > 0)
                this.more = true;
            investAssets.forEach((investAsset, i) => this.addRow(investAsset, i));
        });
    }

    init() {
        let filter = this.getFilter();
        filter.page = 1;
        filter.perPage = filter.perPage || this.PER_PAGE;
        super.setFilter(filter);
        this.more = true;

        this.build();
        getInvestAssets(filter).then(investAssets => {
            setList(investAssets);
            if(investAssets.length == 0){
                this.empty();
            }
            investAssets.forEach((investAsset, i) => this.addRow(investAsset, i));
        });
    }

    addRow(investAsset, i) {
        let liValue = {
            icon: MATERIAL_ICONS.BUSINESS_CENTER,
            title: investAsset.description,
            subtitle: this.getSubtitle(investAsset)
        }
        this.addLi(liValue, i, () => this.aonInvest(investAsset, i));
    }

    getSubtitle(investAsset) {
        let type = investAsset.type && InvestAssetType[investAsset.type]
            ? InvestAssetType[investAsset.type].name : '';
        let regime = investAsset.regime && InvestAssetRegime[investAsset.regime]
            ? InvestAssetRegime[investAsset.regime].name : '';
        return [type, regime].filter(value => value).join(' - ');
    }

    aonInvest(investAsset, i) {
        setIndex(i);
        setCacheFilter(this.getFilter());
        let aonInvest = new AonMobileInvest();
        aonInvest.setInvestAsset(investAsset);
        this.getApplication().setContent(aonInvest);
    }

    get filter() {
        return this.getFilter();
    }

    set filter(filter) {
        super.setFilter(filter);
    }

    getFilter() {
        if(!this.hasAttribute(CONSTANT.FILTER)) return {};
        try {
            return JSON.parse(this.getAttribute(CONSTANT.FILTER));
        } catch(e) {
            return {};
        }
    }

    setFilter(filter) {
        super.setFilter(filter);
        this.init();
    }
}
if(!window.customElements.get(TAG.AON_MOBILE_INVEST_LIST)){
    window.customElements.define(TAG.AON_MOBILE_INVEST_LIST, AonMobileInvestList);
}
