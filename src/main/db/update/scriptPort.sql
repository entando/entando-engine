ALTER TABLE pages_metadata_draft ADD COLUMN groupcode character varying(30);
ALTER TABLE pages_metadata_online ADD COLUMN groupcode character varying(30);
UPDATE pages_metadata_online SET groupcode = (SELECT pages.groupcode FROM pages WHERE pages_metadata_online.code = pages.code);
UPDATE pages_metadata_draft SET groupcode = (SELECT pages.groupcode FROM pages WHERE pages_metadata_draft.code = pages.code);
ALTER TABLE pages DROP groupcode;
ALTER TABLE resources ADD COLUMN owner character varying(128);
ALTER TABLE resources ADD COLUMN correlationcode character varying(256);
ALTER TABLE contents ADD COLUMN restriction character varying(40);
ALTER TABLE widgetcatalog ADD COLUMN bundleid character varying(150);
ALTER TABLE widgetcatalog ADD COLUMN configui character varying;
ALTER TABLE widgetcatalog ADD COLUMN readonlydefaultconfig smallint NOT NULL SET DEFAULT 0;
CREATE TABLE userpreferences
( username character varying(80) NOT NULL,
  wizard smallint NOT NULL,
  loadonpageselect smallint NOT NULL,
  translationwarning smallint NOT NULL,
  CONSTRAINT userpreferences_pkey PRIMARY KEY (username)
);
ALTER TABLE widgetcatalog DROP COLUMN readonlydefaultconfig;
ALTER TABLE widgetcatalog ADD COLUMN readonlypagewidgetconfig smallint SET DEFAULT 0;
ALTER TABLE widgetcatalog ADD COLUMN widgetcategory character varying(80);
ALTER TABLE widgetcatalog ADD COLUMN icon character varying(80);