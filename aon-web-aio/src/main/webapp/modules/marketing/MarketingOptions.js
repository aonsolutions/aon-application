import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"
import { WAREHOUSE } from "../../services/app.js"

export const CAMPAIGN = {
  id: CONSTANT.CAMPAIGN.initCap(),
  name: MSG.CAMPAIGNS,
  icon: MATERIAL_ICONS.PRECISION_MANUFACTURING
}

export const TARGET = {
  id: CONSTANT.TARGET.initCap(),
  name: MSG.TARGETS,
  icon: MATERIAL_ICONS.SHOPPING_BAG
}

export const SURVEY = {
  id: CONSTANT.SURVEY.initCap(),
  name: MSG.SURVEYS,
  icon: MATERIAL_ICONS.PRECISION_MANUFACTURING
}

export const QUESTION = {
  id: CONSTANT.QUESTION.initCap(),
  name: MSG.QUESTIONS,
  icon: MATERIAL_ICONS.SHOPPING_BAG
}

export const MarketingSidenav = {
  MARKETING: {
    id: CONSTANT.MARKETING.initCap(),
    name: MSG.MARKETING.toUpperCase(),
    app: WAREHOUSE,
    options: [CAMPAIGN, TARGET]
  },
  SURVEY: {
    id: CONSTANT.SURVEY.initCap(),
    name: MSG.SURVEYS.toUpperCase(),
    app: WAREHOUSE,
    options: [SURVEY, QUESTION]
  }
}
