import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

// Notes
export const getNotes = (data) => get(`${API_URL}/note`, data);
export const getNote = (data) => get(`${API_URL}/note/one`, data);

// Tag
export const getNoteTags = (data) => get(`${API_URL}/note/tags`, data);

// Notes count
export const getNoteCount = (data) => get(`${API_URL}/note/note-count`, data);

// Notes count
export const getNoteTagsCount = (data) => get(`${API_URL}/note/note-tag-count`, data);

// Note
export const saveNote = (data) => post(`${API_URL}/note`, data);

// Tag
export const saveNoteTag = (data) => post(`${API_URL}/note/tag`, data);

// Note
export const deleteNote = (data) => remove(`${API_URL}/note`, data);

// Tag
export const deleteNoteTag = (data) => remove(`${API_URL}/note/tag`, data);
