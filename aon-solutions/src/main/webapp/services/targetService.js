import { get, post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

const TARGETS = `${API_URL}/target`;
const REGISTRY_ITEM = `${TARGETS}/ritem`;

// REGISTRY ITEM
export const saveRegistryItem = (data) => post(REGISTRY_ITEM, data);

