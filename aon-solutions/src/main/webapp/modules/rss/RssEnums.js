import { MATERIAL_ICONS } from "../../environments/environments.js";


const RSS_VIEWS = {
  AON_RSS_ADD: 'aonRssAdd',
  AON_RSS_PANEL: 'aonRssPanel',
  AON_RSS_LIST: 'aonRssList',
};

const ACTION_RSS = {
  PUBLISH_RSS:{
    name: 'Publicar RSS',
    icon: "rss_feed",
    id: 'publishRssIcon',
  },
  BACK: {
    id: 'Previous',
    name: 'Volver',
    icon: MATERIAL_ICONS.ARROW_BACK
  }
}


export const RssEnums = {
  RSS_VIEWS,
  ACTION_RSS
};