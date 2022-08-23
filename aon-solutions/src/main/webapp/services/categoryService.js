import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

const saveCategory = (data) => post(`${API_URL}/category`, data);
const deleteCategory = (data) => remove(`${API_URL}/category`, data);
const getCategorys = (data) => get(`${API_URL}/category`, data);

export const CategoryService = {
    saveCategory,
    deleteCategory,
    getCategorys
}