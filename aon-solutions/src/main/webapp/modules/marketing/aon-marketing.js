import { AonElement } from "../../components/AonElement";
import { AonApplication } from "../../components/aon-application";
import { MarketingSidenav, QUESTION, SURVEY } from "./MarketingOptions";
import * as GWT from "../../gwt/gwt.js";
import { TAG, MSG, EVENT } from "../../environments/environments.js";
export class AonMarketing extends AonElement {

    MARKETING;

    constructor () {
		super();
	}

	connectedCallback() {
		this.initialize();
    	this.build();
 	}

     initialize() {
		this.MARKETING =  'aonMarketing';
		this.option = this.option || QUESTION;
	}

 	build() {
		this.createApplication(this.MARKETING, MSG.MARKETING, new AonApplication());
		this.buildSidenav();
		this.selectOption(this.option);
	}

	buildSidenav() {
		if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.WAREHOUSE);
		}
		this.getApplication().addEventListener(EVENT.SELECT_OPTION, 
			(e) => this.selectOption(e.detail));

		this.buildSurveyOptions();
	}

	buildMarketingOptions() {
		this.getApplication().addSidenavOptions3(MarketingSidenav.MARKETING);
	}

	buildSurveyOptions() {
		this.getApplication().addSidenavOptions3(MarketingSidenav.SURVEY);
	}

	selectOption(option) {
		switch(option.id){
		case SURVEY.id:
			this.aonSurvey();
			break;
		case QUESTION.id:
			this.aonQuestion();
			break;
		default:
			this.aonSurvey();
			break;
		}
	}

	setOption(option) {
		this.option = option;
	}

	aonSurvey() {
		this.getApplication().development();
	}

	aonQuestion() {
		GWT.load(GWT.QUESTION, this.getApplication().CONTENT);
	}
}
if(!window.customElements.get(TAG.AON_MARKETING)){
	window.customElements.define(TAG.AON_MARKETING, AonMarketing);
}