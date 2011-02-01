--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: audit; Type: SCHEMA; Schema: -; Owner: dbuser
--

CREATE SCHEMA audit;


ALTER SCHEMA audit OWNER TO dbuser;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = audit, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: action; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE "action" (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    application integer NOT NULL
);


ALTER TABLE audit."action" OWNER TO dbuser;

--
-- Name: action_execution; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE action_execution (
    id integer NOT NULL,
    executiondate timestamp without time zone NOT NULL,
    action_id integer NOT NULL,
    session_id integer NOT NULL
);


ALTER TABLE audit.action_execution OWNER TO dbuser;

--
-- Name: application; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE application (
    id integer NOT NULL,
    name character varying(64) NOT NULL
);


ALTER TABLE audit.application OWNER TO dbuser;

--
-- Name: domain; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE "domain" (
    id integer NOT NULL,
    enable_audit boolean NOT NULL,
    name character varying(64) NOT NULL
);


ALTER TABLE audit."domain" OWNER TO dbuser;

--
-- Name: domain_application; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE domain_application (
    id integer NOT NULL,
    audit_level integer NOT NULL,
    application integer NOT NULL,
    "domain" integer NOT NULL
);


ALTER TABLE audit.domain_application OWNER TO dbuser;

--
-- Name: seq_action; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_action
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_action OWNER TO dbuser;

--
-- Name: seq_action_execution; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_action_execution
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_action_execution OWNER TO dbuser;

--
-- Name: seq_application; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_application
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_application OWNER TO dbuser;

--
-- Name: seq_domain; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_domain
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_domain OWNER TO dbuser;

--
-- Name: seq_domain_application; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_domain_application
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_domain_application OWNER TO dbuser;

--
-- Name: seq_session; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_session
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_session OWNER TO dbuser;

--
-- Name: seq_user; Type: SEQUENCE; Schema: audit; Owner: dbuser
--

CREATE SEQUENCE seq_user
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE audit.seq_user OWNER TO dbuser;

--
-- Name: session; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE "session" (
    id integer NOT NULL,
    enddate timestamp without time zone,
    session_id character varying(32) NOT NULL,
    startdate timestamp without time zone NOT NULL,
    application_id integer NOT NULL,
    user_id integer NOT NULL,
    remote_address character varying(15) NOT NULL,
    remote_host character varying(64) NOT NULL
);


ALTER TABLE audit."session" OWNER TO dbuser;

--
-- Name: user; Type: TABLE; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE TABLE "user" (
    id integer NOT NULL,
    "login" character varying(16) NOT NULL,
    "domain" integer NOT NULL
);


ALTER TABLE audit."user" OWNER TO dbuser;

--
-- Name: action_execution_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY action_execution
    ADD CONSTRAINT action_execution_pkey PRIMARY KEY (id);


--
-- Name: action_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY "action"
    ADD CONSTRAINT action_pkey PRIMARY KEY (id);


--
-- Name: application_name_key; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY application
    ADD CONSTRAINT application_name_key UNIQUE (name);


--
-- Name: application_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY application
    ADD CONSTRAINT application_pkey PRIMARY KEY (id);


--
-- Name: domain_application_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY domain_application
    ADD CONSTRAINT domain_application_pkey PRIMARY KEY (id);


--
-- Name: domain_name_key; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY "domain"
    ADD CONSTRAINT domain_name_key UNIQUE (name);


--
-- Name: domain_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY "domain"
    ADD CONSTRAINT domain_pkey PRIMARY KEY (id);


--
-- Name: session_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY "session"
    ADD CONSTRAINT session_pkey PRIMARY KEY (id);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: audit; Owner: dbuser; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: IDX_ACTION; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE UNIQUE INDEX "IDX_ACTION" ON "action" USING btree (application, name);


--
-- Name: IDX_DOMAIN_APPLICATION; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE UNIQUE INDEX "IDX_DOMAIN_APPLICATION" ON domain_application USING btree (application, "domain");


--
-- Name: idx_action_name; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE INDEX idx_action_name ON "action" USING btree (name);


--
-- Name: idx_application_name; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE INDEX idx_application_name ON application USING btree (name);


--
-- Name: idx_domain_name; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE INDEX idx_domain_name ON "domain" USING btree (name);


--
-- Name: idx_session_session_id; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE INDEX idx_session_session_id ON "session" USING btree (session_id);


--
-- Name: idx_user_login; Type: INDEX; Schema: audit; Owner: dbuser; Tablespace: 
--

CREATE INDEX idx_user_login ON "user" USING btree ("login");


--
-- Name: fk_action_application; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY "action"
    ADD CONSTRAINT fk_action_application FOREIGN KEY (application) REFERENCES application(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_action_execution_action; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY action_execution
    ADD CONSTRAINT fk_action_execution_action FOREIGN KEY (action_id) REFERENCES "action"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_action_execution_session; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY action_execution
    ADD CONSTRAINT fk_action_execution_session FOREIGN KEY (session_id) REFERENCES "session"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_domain_application_application; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY domain_application
    ADD CONSTRAINT fk_domain_application_application FOREIGN KEY (application) REFERENCES application(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_domain_application_domain; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY domain_application
    ADD CONSTRAINT fk_domain_application_domain FOREIGN KEY ("domain") REFERENCES "domain"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_session_application; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY "session"
    ADD CONSTRAINT fk_session_application FOREIGN KEY (application_id) REFERENCES application(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_session_user; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY "session"
    ADD CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_user_domain; Type: FK CONSTRAINT; Schema: audit; Owner: dbuser
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT fk_user_domain FOREIGN KEY ("domain") REFERENCES "domain"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

