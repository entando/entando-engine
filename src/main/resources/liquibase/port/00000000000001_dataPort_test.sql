INSERT INTO categories (catcode, parentcode, titles) VALUES ('home', 'home', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Home</property>
</properties>
');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('cat1', 'home', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Animal</property>
<property key="it">Animali</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('evento', 'home', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Event</property>
<property key="it">Evento</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('resource_root', 'home', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Root Risorse</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('Attach', 'resource_root', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Attach</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('Image', 'resource_root', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Image</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('general', 'home', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">General</property>
<property key="it">Generale</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('general_cat1', 'general', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Category 1</property>
<property key="it">Categoria 1</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('general_cat2', 'general', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Category 2</property>
<property key="it">Categoria 2</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('resCat2', 'Image', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Images Resource Category 2</property>
<property key="it">Categoria Risorsa Immagine 2</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('resCat1', 'Image', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Images Resource Category 1</property>
<property key="it">Categoria Risorse Immagine 1</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('resCat3', 'Image', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Images Resource Category 3</property>
<property key="it">Categoria Risorse Immagine 3</property>
</properties>

');
INSERT INTO categories (catcode, parentcode, titles) VALUES ('general_cat3', 'general', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Category 3</property>
<property key="it">Categoria 3</property>
</properties>

');




INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE', 'en', 'page');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE', 'it', 'pagina');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE_MODEL', 'en', 'page template');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE_MODEL', 'it', 'modello pagina');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE_TITLE', 'en', 'page title');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('PAGE_TITLE', 'it', 'titolo pagina');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_fullname', 'it', 'fullname');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_email', 'it', 'email');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_birthdate', 'it', 'birthdate');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_language', 'it', 'language');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_boolean1', 'it', 'boolean 1');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PFL_boolean2', 'it', 'boolean 2');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('LABEL_WITH_PARAMS', 'it', 'Benvenuto ${name} ${surname} (${:username} - ${name}.${surname})');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('LABEL_WITH_PARAMS', 'en', 'Welcome ${surname} ${name} (${:username} - ${name}.${surname})');




INSERT INTO pagemodels (code, descr, frames, plugincode) VALUES ('home', 'Modello home page', '<frames>
	<frame pos="0"><descr>Box sinistra alto</descr></frame>
	<frame pos="1"><descr>Box sinistra basso</descr></frame>
	<frame pos="2"><descr>Box centrale 1</descr></frame>
	<frame pos="3" main="true"><descr>Box centrale 2</descr></frame>
	<frame pos="4"><descr>Box destra alto</descr></frame>
	<frame pos="5"><descr>Box destra basso</descr></frame>
</frames>', NULL);
INSERT INTO pagemodels (code, descr, frames, plugincode) VALUES ('service', 'Modello pagine di servizio', '<frames>
	<frame pos="0"><descr>Navigazione orizzontale</descr></frame>
	<frame pos="1"><descr>Lingue</descr></frame>
	<frame pos="2"><descr>Navigazione verticale sinistra</descr></frame>
	<frame pos="3" main="true"><descr>Area principale</descr></frame>
</frames>', NULL);
INSERT INTO pagemodels (code, descr, frames, plugincode) VALUES ('internal', 'Internal Page', '<frames>
	<frame pos="0">
		<descr>Choose Language</descr>
	</frame>
	<frame pos="1">
		<descr>Search Form</descr>
	</frame>
	<frame pos="2">
		<descr>Breadcrumbs</descr>
	</frame>
	<frame pos="3">
		<descr>First Column: Box 1</descr>
		<defaultWidget code="leftmenu">
			<properties>
				<property key="navSpec">code(homepage).subtree(1)</property>
			</properties>
		</defaultWidget>
	</frame>
	<frame pos="4">
		<descr>First Column: Box 2</descr>
	</frame>
	<frame pos="5" main="true">
		<descr>Main Column: Box 1</descr>
	</frame>
	<frame pos="6">
		<descr>Main Column: Box 2</descr>
	</frame>
	<frame pos="7">
		<descr>Third Column: Box 1</descr>
	</frame>
	<frame pos="8">
		<descr>Third Column: Box 2</descr>
	</frame>
</frames>', NULL);

INSERT INTO pages (code, parentcode, pos) VALUES ('service', 'homepage', 1);
INSERT INTO pages (code, parentcode, pos) VALUES ('primapagina', 'service', 1);
INSERT INTO pages (code, parentcode, pos) VALUES ('notfound', 'service', 2);
INSERT INTO pages (code, parentcode, pos) VALUES ('login', 'service', 3);
INSERT INTO pages (code, parentcode, pos) VALUES ('homepage', 'homepage', -1);
INSERT INTO pages (code, parentcode, pos) VALUES ('errorpage', 'service', 5);
INSERT INTO pages (code, parentcode, pos) VALUES ('customers_page', 'homepage', 5);
INSERT INTO pages (code, parentcode, pos) VALUES ('coach_page', 'homepage', 4);
INSERT INTO pages (code, parentcode, pos) VALUES ('administrators_page', 'homepage', 6);
INSERT INTO pages (code, parentcode, pos) VALUES ('customer_subpage_2', 'customers_page', 2);
INSERT INTO pages (code, parentcode, pos) VALUES ('pagina_12', 'pagina_1', 2);
INSERT INTO pages (code, parentcode, pos) VALUES ('pagina_11', 'pagina_1', 1);
INSERT INTO pages (code, parentcode, pos) VALUES ('customer_subpage_1', 'customers_page', 1);
INSERT INTO pages (code, parentcode, pos) VALUES ('pagina_1', 'homepage', 2);
INSERT INTO pages (code, parentcode, pos) VALUES ('contentview', 'service', 4);
INSERT INTO pages (code, parentcode, pos) VALUES ('pagina_2', 'homepage', 3);
INSERT INTO pages (code, parentcode, pos) VALUES ('pagina_draft', 'homepage', 7);



INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('service', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Nodo pagine di servizio</property>
</properties>
', 'service', 0, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('primapagina', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Nodo pagine di servizio</property>
</properties>', 'service', 0, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('notfound', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina non trovata</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('login', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina di login</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('homepage', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Start Page</property>
<property key="it">Pagina iniziale</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('errorpage', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina di errore</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customers_page', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customers Page</property>
<property key="it">Pagina gruppo Customers</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('coach_page', 'coach', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Coach Page</property>
<property key="it">Pagina gruppo Coach</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('administrators_page', 'administrators', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Administrators Page</property>
<property key="it">Pagina gruppo Amministratori</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customer_subpage_2', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customer SubPage 2</property>
<property key="it">Customer SubPage 2</property>
</properties>', 'home', 0, '<?xml version="1.0" encoding="UTF-8"?>
<config />', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_12', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1-2</property>
<property key="it">Pagina 1-2</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_11', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1-1</property>
<property key="it">Pagina 1-1</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customer_subpage_1', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customer SubPage 1</property>
<property key="it">Customer SubPage 1</property>
</properties>', 'home', 0, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
  <extragroups>
    <group name="coach" />
  </extragroups>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_1', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1</property>
<property key="it">Pagina 1</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('contentview', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Content Publishing</property>
<property key="it">Publicazione Contenuto</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_online (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_2', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 2</property>
<property key="it">Pagina 2</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');



INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('service', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Nodo pagine di servizio</property>
</properties>
', 'service', 0, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('primapagina', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Nodo pagine di servizio</property>
</properties>', 'service', 0, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('notfound', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina non trovata</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('login', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina di login</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('homepage', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Start Page</property>
<property key="it">Pagina iniziale</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('errorpage', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="it">Pagina di errore</property>
</properties>', 'service', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customers_page', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customers Page</property>
<property key="it">Pagina gruppo Customers</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('coach_page', 'coach', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Coach Page</property>
<property key="it">Pagina gruppo Coach</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('administrators_page', 'administrators', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Administrators Page</property>
<property key="it">Pagina gruppo Amministratori</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customer_subpage_2', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customer SubPage 2</property>
<property key="it">Customer SubPage 2</property>
</properties>', 'home', 0, '<?xml version="1.0" encoding="UTF-8"?>
<config />', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_12', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1-2</property>
<property key="it">Pagina 1-2</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_11', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1-1</property>
<property key="it">Pagina 1-1</property>
</properties>', 'home', 1, NULL, '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('customer_subpage_1', 'customers', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Customer SubPage 1</property>
<property key="it">Customer SubPage 1</property>
</properties>', 'home', 0, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
  <extragroups>
    <group name="coach" />
  </extragroups>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_1', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 1</property>
<property key="it">Pagina 1</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('contentview', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Content Publishing</property>
<property key="it">Publicazione Contenuto</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_2', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page 2</property>
<property key="it">Pagina 2</property>
</properties>', 'home', 1, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>false</useextratitles>
</config>', '2017-02-17 13:06:24');
INSERT INTO pages_metadata_draft (code, groupcode, titles, modelcode, showinmenu, extraconfig, updatedat) VALUES ('pagina_draft', 'free', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Page Draft</property>
<property key="it">Pagina Draft</property>
</properties>', 'home', 0, '<?xml version="1.0" encoding="UTF-8"?>
<config>
  <useextratitles>true</useextratitles>
</config>', '2017-02-17 13:06:24');


INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory, icon) VALUES ('login_form', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Login Widget</property>
<property key="it">Widget di Login</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL, 1, 'system','font-awesome:fa-sign-in');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('messages_system', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">System Messages</property>
<property key="it">Messaggi di Sistema</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL, 1, 'system');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Internal Servlet</property>
<property key="it">Invocazione di una Servlet Interna</property>
</properties>', '<config>
	<parameter name="actionPath">
		Path relativo di una action o una Jsp
	</parameter>
	<action name="configSimpleParameter"/>
</config>', NULL, NULL, NULL, 1, NULL, 1, 'system');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Vertical Navigation Menu</property>
<property key="it">Menu di navigazione verticale</property>
</properties>', '<config>
	<parameter name="navSpec">Rules for the Page List auto-generation</parameter>
	<action name="navigatorConfig" />
</config>', NULL, NULL, NULL, 1, NULL, 1, 'navigation');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('entando_apis', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">APIs</property>
<property key="it">APIs</property>
</properties>
', NULL, NULL, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/ExtStr2/do/Front/Api/Resource/list.action</property>
</properties>
', 1, 'free', 1, 'system');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('logic_type', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Logic type for test</property>
<property key="it">Tipo logico per test</property>
</properties>', NULL, NULL, 'leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="navSpec">code(homepage)</property>
</properties>', 0, NULL, 0, 'logic');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, readonlypagewidgetconfig, widgetcategory) VALUES ('parent_widget', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Parent Widget</property>
<property key="it">Parent Widget em Italiano</property>
</properties>', '<config>
	<parameter name="parentCode">
		Description of the Widget Parameter
	</parameter>
	<action name="configSimpleParameter"/>
</config>', NULL, NULL, NULL, 1, NULL, 1, 'parentWidgetType');



INSERT INTO guifragment (code, widgettypecode, plugincode, gui, defaultgui, locked) VALUES ('login_form', 'login_form', NULL, NULL, '<#assign wp=JspTaglibs["/aps-core"]>
<h1><@wp.i18n key="RESERVED_AREA" /></h1>
<#if (Session.currentUser.username != "guest") >
	<p><@wp.i18n key="WELCOME" />, <em>${Session.currentUser}</em>!</p>
	<#if (Session.currentUser.entandoUser) >
	<table class="table table-condensed">
		<tr>
			<th><@wp.i18n key="USER_DATE_CREATION" /></th>
			<th><@wp.i18n key="USER_DATE_ACCESS_LAST" /></th>
			<th><@wp.i18n key="USER_DATE_PASSWORD_CHANGE_LAST" /></th>
		</tr>
		<tr>
			<td>${Session.currentUser.creationDate?default("-")}</td>
			<td>${Session.currentUser.lastAccess?default("-")}</td>
			<td>${Session.currentUser.lastPasswordChange?default("-")}</td>
		</tr>
	</table>
		<#if (!Session.currentUser.credentialsNotExpired) >
		<div class="alert alert-block">
			<p><@wp.i18n key="USER_STATUS_EXPIRED_PASSWORD" />: <a href="<@wp.info key="systemParam" paramName="applicationBaseURL" />do/editPassword.action"><@wp.i18n key="USER_STATUS_EXPIRED_PASSWORD_CHANGE" /></a></p>
		</div>
		</#if>
	</#if>
	<@wp.ifauthorized permission="enterBackend">
	<div class="btn-group">
		<a href="<@wp.info key="systemParam" paramName="applicationBaseURL" />do/main.action?request_locale=<@wp.info key="currentLang" />" class="btn"><@wp.i18n key="ADMINISTRATION" /></a>
	</div>
	</@wp.ifauthorized>
	<p class="pull-right"><a href="<@wp.info key="systemParam" paramName="applicationBaseURL" />do/logout.action" class="btn"><@wp.i18n key="LOGOUT" /></a></p>
	<@wp.pageWithWidget widgetTypeCode="userprofile_editCurrentUser" var="userprofileEditingPageVar" listResult=false />
	<#if (userprofileEditingPageVar??) >
	<p><a href="<@wp.url page="${userprofileEditingPageVar.code}" />" ><@wp.i18n key="userprofile_CONFIGURATION" /></a></p>
	</#if>
<#else>
	<#if (accountExpired?? && accountExpired == true) >
	<div class="alert alert-block alert-error">
		<p><@wp.i18n key="USER_STATUS_EXPIRED" /></p>
	</div>
	</#if>
	<#if (wrongAccountCredential?? && wrongAccountCredential == true) >
	<div class="alert alert-block alert-error">
		<p><@wp.i18n key="USER_STATUS_CREDENTIALS_INVALID" /></p>
	</div>
	</#if>
	<form action="<@wp.url/>" method="post" class="form-horizontal margin-medium-top">
		<#if (RequestParameters.returnUrl??) >
		<input type="hidden" name="returnUrl" value="${RequestParameters.returnUrl}" />
		</#if>
		<div class="control-group">
			<label for="username" class="control-label"><@wp.i18n key="USERNAME" /></label>
			<div class="controls">
				<input id="username" type="text" name="username" class="input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label for="password" class="control-label"><@wp.i18n key="PASSWORD" /></label>
			<div class="controls">
				<input id="password" type="password" name="password" class="input-xlarge" />
			</div>
		</div>
		<div class="form-actions">
			<input type="submit" value="<@wp.i18n key="SIGNIN" />" class="btn btn-primary" />
		</div>
	</form>
</#if>', 1);




INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config) VALUES ('pagina_1', 2, 'leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="navSpec">abs(1).subtree(2)</property>
</properties>

');
INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config) VALUES ('contentview', 1, 'login_form', NULL);
INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config) VALUES ('contentview', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');
INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config) VALUES ('pagina_2', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');
INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config) VALUES ('coach_page', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');


INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('pagina_1', 2, 'leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="navSpec">abs(1).subtree(2)</property>
</properties>

');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('contentview', 1, 'login_form', NULL);
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('contentview', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('pagina_2', 0, 'leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="navSpec">abs(1).subtree(2)</property>
</properties>

');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('pagina_2', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>

');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('pagina_draft', 1, 'leftmenu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="navSpec">abs(1).subtree(2)</property>
</properties>');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('pagina_draft', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');
INSERT INTO widgetconfig_draft (pagecode, framepos, widgetcode, config) VALUES ('coach_page', 2, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/do/login</property>
</properties>');


INSERT INTO sysconfig (version, item, descr, config) VALUES ('test', 'langs', 'Definition of the system languages', '<?xml version="1.0" encoding="UTF-8"?>
<Langs>
  <Lang>
    <code>it</code>
    <descr>Italiano</descr>
    <default>true</default>
  </Lang>
  <Lang>
    <code>en</code>
    <descr>English</descr>
  </Lang>
</Langs>

');
INSERT INTO sysconfig (version, item, descr, config) VALUES ('test', 'params', 'Configuration params. Tags other than "Param" are ignored', '<?xml version="1.0" encoding="UTF-8"?>
<Params>
	<Param name="urlStyle">classic</Param>
	<Param name="hypertextEditor">fckeditor</Param>
	<Param name="treeStyle_page">classic</Param>
	<Param name="treeStyle_category">classic</Param>
	<Param name="startLangFromBrowser">false</Param>
	<Param name="firstTimeMessages">true</Param>
	<Param name="baseUrl">request</Param>
	<Param name="baseUrlContext">true</Param>
	<Param name="useJsessionId">true</Param>
	<Param name="editEmptyFragmentEnabled">false</Param>
	<SpecialPages>
		<Param name="notFoundPageCode">notfound</Param>
		<Param name="homePageCode">homepage</Param>
		<Param name="errorPageCode">errorpage</Param>
		<Param name="loginPageCode">login</Param>
	</SpecialPages>
	<ExtendendPrivacyModule>
		<Param name="extendedPrivacyModuleEnabled">false</Param>
		<Param name="maxMonthsSinceLastAccess">6</Param>
		<Param name="maxMonthsSinceLastPasswordChange">3</Param>
	</ExtendendPrivacyModule>
</Params>');
INSERT INTO sysconfig (version, item, descr, config) VALUES ('test', 'userProfileTypes', 'User Profile Types Definitions', '<profiletypes>
	<profiletype typecode="PFL" typedescr="Default user profile type" >
		<attributes>
			<attribute name="fullname" attributetype="Monotext" searchable="true" >
				<validations>
					<required>true</required>
				</validations>
				<roles>
					<role>userprofile:fullname</role>
				</roles>
			</attribute>
			<attribute name="email" attributetype="Monotext" searchable="true" >
				<validations>
					<required>true</required>
					<regexp><![CDATA[.+@.+.[a-z]+]]></regexp>
				</validations>
				<roles>
					<role>userprofile:email</role>
				</roles>
			</attribute>
			<attribute name="birthdate" attributetype="Date" required="true" searchable="true"/>
			<attribute name="language" attributetype="Monotext" required="true"/>
			<attribute name="boolean1" attributetype="Boolean" searchable="true"/>
			<attribute name="boolean2" attributetype="Boolean" searchable="true"/>
		</attributes>
	</profiletype>
	<profiletype typecode="OTH" typedescr="Other user profile" >
		<attributes>
			<attribute name="firstname" attributetype="Monotext" searchable="true" >
				<validations>
					<required>true</required>
				</validations>
				<roles>
					<role>userprofile:firstname</role>
				</roles>
			</attribute>
			<attribute name="surname" attributetype="Monotext" searchable="true" >
				<validations>
					<required>true</required>
				</validations>
				<roles>
					<role>userprofile:surname</role>
				</roles>
			</attribute>
			<attribute name="email" attributetype="Email" searchable="true" >
				<validations>
					<required>true</required>
				</validations>
				<roles>
					<role>userprofile:email</role>
				</roles>
			</attribute>
			<attribute name="profilepicture" attributetype="Monotext" >
                <roles>
                    <role>userprofile:profilepicture</role>
                </roles>
            </attribute>
		</attributes>
	</profiletype>
	<profiletype typecode="ALL" typedescr="Profile type with all attribute types">
		<attributes>
			<attribute name="Boolean" attributetype="Boolean" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="CheckBox" attributetype="CheckBox" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Date" attributetype="Date" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Date2" attributetype="Date">
				<validations>
					<rangestart attribute="Date" />
					<rangeend>25/11/2026</rangeend>
				</validations>
			</attribute>
			<attribute name="Enumerator" attributetype="Enumerator" separator=",">
				<validations><required>true</required></validations>
                                <![CDATA[a,b,c]]>
                        </attribute>
			<attribute name="EnumeratorMap" attributetype="EnumeratorMap" separator=";">
                            <validations><required>true</required></validations>
                            <![CDATA[01=Value 1;02=Value 2;03=Value 3]]>
                        </attribute>
			<attribute name="Hypertext" attributetype="Hypertext" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Longtext" attributetype="Longtext" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Email" attributetype="Email" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Monotext" attributetype="Monotext" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Monotext2" attributetype="Monotext">
				<validations>
                                        <required>true</required>
					<minlength>15</minlength>
					<maxlength>30</maxlength>
					<regexp><![CDATA[.+@.+.[a-z]+]]></regexp>
				</validations>
			</attribute>
			<attribute name="Number" attributetype="Number" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Number2" attributetype="Number">
				<validations>
                                        <required>true</required>
					<rangestart>50</rangestart>
					<rangeend>300</rangeend>
				</validations>
			</attribute>
			<attribute name="Text" attributetype="Text">
                                <validations><required>true</required></validations>
				<roles>
					<role>jacms:title</role>
				</roles>
			</attribute>
			<attribute name="Text2" attributetype="Text">
				<validations>
                                        <required>true</required>
					<minlength>15</minlength>
					<maxlength>30</maxlength>
					<regexp><![CDATA[.+@.+.[a-z]+]]></regexp>
				</validations>
			</attribute>
			<attribute name="ThreeState" attributetype="ThreeState" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Timestamp" attributetype="Timestamp" >
				<validations><required>true</required></validations>
			</attribute>
			<attribute name="Composite" attributetype="Composite">
                                <validations><required>true</required></validations>
				<attributes>
					<attribute name="Boolean" attributetype="Boolean" />
					<attribute name="CheckBox" attributetype="CheckBox" />
					<attribute name="Date" attributetype="Date">
						<validations>
							<rangestart attribute="Date" />
							<rangeend>10/10/2030</rangeend>
						</validations>
					</attribute>
					<attribute name="Enumerator" attributetype="Enumerator" separator="," />
					<attribute name="Hypertext" attributetype="Hypertext" />
					<attribute name="Longtext" attributetype="Longtext" />
					<attribute name="Monotext" attributetype="Monotext" />
					<attribute name="Number" attributetype="Number">
						<validations>
							<expression evalOnValuedAttribute="true">
								<ognlexpression><![CDATA[#entity.getAttribute(''Number'').value == null || (#entity.getAttribute(''Number'').value != null && value > #entity.getAttribute(''Number'').value)]]></ognlexpression>
								<errormessage><![CDATA[Value has to be upper then ''Number'' attribute]]></errormessage>
								<helpmessage><![CDATA[If ''Number'' valued attribute, Value has to be upper]]></helpmessage>
							</expression>
						</validations>
					</attribute>
					<attribute name="Text" attributetype="Text" />
					<attribute name="ThreeState" attributetype="ThreeState" />
					<attribute name="Timestamp" attributetype="Timestamp" />
				</attributes>
			</attribute>
			<attribute name="MARKER" attributetype="Monotext">
				<validations>
					<required>true</required>
				</validations>
			</attribute>
		</attributes>
	</profiletype>
</profiletypes>');



INSERT INTO uniquekeys (id, keyvalue) VALUES (1, 200);
