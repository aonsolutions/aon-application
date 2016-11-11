package com.esferalia.aon.gwt.dump.shared;



public interface DSIImportService {

	public static final String URL = "/aon-aio/aon_gwt_connect/dsiimport";

	public static final String POST_ZIP_FILE_PART = "zip-file-part";

	public static final String GET_DB_PARAM = "get-db-param";
	public static final String GET_EMPRES_PARAM = "get-empres-param";
	
	public static final String GET_FROM_PARAM = "get-from-param";
	public static final String GET_COMMIT_PARAM = "get-commit-param";
	public static final String GET_REPLACE_PARAM = "get-replace-param";

	public static final String GET_ACTION_PARAM = "get-action-param";
	
	public static interface GetActionHandler<T, I, E extends Throwable> {
		void doCancel(T t, I i) throws E;

		void doImport(T t, I i) throws E;

		void doListEmpress(T t, I i) throws E;
		
	}

	public static enum GetAction {
		CANCEL {
			@Override
			public <T, I, E extends Throwable> void handle(
					GetActionHandler<T, I, E> handler, T t, I i) throws E {
				handler.doCancel(t, i);
			}
		},
		IMPORT {
			@Override
			public <T, I, E extends Throwable> void handle(
					GetActionHandler<T, I, E> handler, T t, I i) throws E {
				handler.doImport(t, i);
			}
		},
		LIST_EMPREES {
			@Override
			public <T, I, E extends Throwable> void handle(
					GetActionHandler<T, I, E> handler, T t, I i) throws E {
				handler.doListEmpress(t, i);
			}
		};
		

		public abstract <T, I, E extends Throwable> void handle(
				GetActionHandler<T, I, E> handler, T t, I i) throws E;
	}

}
