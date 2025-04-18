
CREATE TABLE IF NOT EXISTS FS_CONSENT (
  CONSENT_ID            VARCHAR(255) NOT NULL,
  RECEIPT               BLOB NOT NULL,
  CREATED_TIME          BIGINT NOT NULL,
  UPDATED_TIME          BIGINT NOT NULL,
  CLIENT_ID             VARCHAR(255) NOT NULL,
  ORG_ID                VARCHAR(255) DEFAULT 'DEFAULT_ORG',
  CONSENT_TYPE          VARCHAR(64) NOT NULL,
  CURRENT_STATUS        VARCHAR(64) NOT NULL,
  CONSENT_FREQUENCY     INT,
  VALIDITY_TIME         BIGINT,
  RECURRING_INDICATOR   BOOLEAN,
  PRIMARY KEY (CONSENT_ID)
);

CREATE TABLE IF NOT EXISTS FS_CONSENT_AUTH_RESOURCE (
  AUTH_ID           VARCHAR(255) NOT NULL,
  CONSENT_ID        VARCHAR(255) NOT NULL,
  AUTH_TYPE         VARCHAR(255) NOT NULL,
  USER_ID           VARCHAR(255),
  AUTH_STATUS       VARCHAR(255) NOT NULL,
  UPDATED_TIME      BIGINT NOT NULL,
  PRIMARY KEY(AUTH_ID),
  CONSTRAINT FK_ID_FS_CONSENT_AUTH_RESOURCE FOREIGN KEY (CONSENT_ID) REFERENCES FS_CONSENT (CONSENT_ID)
);

CREATE TABLE IF NOT EXISTS FS_CONSENT_MAPPING (
  MAPPING_ID        VARCHAR(255) NOT NULL,
  AUTH_ID           VARCHAR(255) NOT NULL,
  MAPPING_STATUS    VARCHAR(255) NOT NULL,
  RESOURCE               BLOB NOT NULL,
  PRIMARY KEY(MAPPING_ID),
  CONSTRAINT FK_FS_CONSENT_MAPPING FOREIGN KEY (AUTH_ID) REFERENCES FS_CONSENT_AUTH_RESOURCE (AUTH_ID)
);

CREATE TABLE IF NOT EXISTS FS_CONSENT_STATUS_AUDIT (
  STATUS_AUDIT_ID   VARCHAR(255) NOT NULL,
  CONSENT_ID        VARCHAR(255) NOT NULL,
  CURRENT_STATUS    VARCHAR(255) NOT NULL,
  ACTION_TIME       BIGINT NOT NULL,
  REASON            VARCHAR(255),
  ACTION_BY         VARCHAR(255),
  PREVIOUS_STATUS   VARCHAR(255),
  PRIMARY KEY(STATUS_AUDIT_ID),
  CONSTRAINT FK_FS_CONSENT_STATUS_AUDIT FOREIGN KEY (CONSENT_ID) REFERENCES FS_CONSENT (CONSENT_ID)
);

CREATE TABLE IF NOT EXISTS FS_CONSENT_ATTRIBUTE (
  CONSENT_ID        VARCHAR(255) NOT NULL,
  ATT_KEY           VARCHAR(255) NOT NULL,
  ATT_VALUE         LONGTEXT NOT NULL,
  PRIMARY KEY(CONSENT_ID, ATT_KEY),
  CONSTRAINT FK_FS_CONSENT_ATTRIBUTE FOREIGN KEY (CONSENT_ID) REFERENCES FS_CONSENT (CONSENT_ID)
);

CREATE TABLE IF NOT EXISTS FS_CONSENT_HISTORY (
  TABLE_ID VARCHAR(10) NOT NULL,
  RECORD_ID VARCHAR(255) NOT NULL,
  HISTORY_ID VARCHAR(255) NOT NULL,
  CHANGED_VALUES BLOB NOT NULL,
  REASON VARCHAR(255) NOT NULL,
  EFFECTIVE_TIMESTAMP BIGINT NOT NULL,
PRIMARY KEY (TABLE_ID,RECORD_ID,HISTORY_ID)
);
