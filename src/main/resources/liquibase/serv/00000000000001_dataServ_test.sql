INSERT INTO authgroups (groupname, descr) VALUES ('administrators', 'Amministratori');
INSERT INTO authgroups (groupname, descr) VALUES ('coach', 'Coach');
INSERT INTO authgroups (groupname, descr) VALUES ('customers', 'Customers');
INSERT INTO authgroups (groupname, descr) VALUES ('free', 'Accesso Libero');
INSERT INTO authgroups (groupname, descr) VALUES ('helpdesk', 'Helpdesk');
INSERT INTO authgroups (groupname, descr) VALUES ('management', 'Management');


INSERT INTO authroles (rolename, descr) VALUES ('admin', 'Tutte le funzioni');
INSERT INTO authroles (rolename, descr) VALUES ('editor', 'Gestore di Contenuti e Risorse');
INSERT INTO authroles (rolename, descr) VALUES ('supervisor', 'Supervisore di Contenuti');
INSERT INTO authroles (rolename, descr) VALUES ('pageManager', 'Gestore di Pagine');
INSERT INTO authroles (rolename, descr) VALUES ('reviewer', 'Recensore');


INSERT INTO authpermissions (permissionname, descr) VALUES ('superuser', 'All functions');
INSERT INTO authpermissions (permissionname, descr) VALUES ('validateContents', 'Content Supervision');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageResources', 'Asset Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('managePages', 'Operations on Pages');
INSERT INTO authpermissions (permissionname, descr) VALUES ('enterBackend', 'Access to Administration Area');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageCategories', 'Operations on Categories');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editContents', 'Content Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('viewUsers', 'View Users and Profiles');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUsers', 'User Management');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUserProfile', 'User Profile Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageReview', 'Review Management');
INSERT INTO authpermissions (permissionname, descr) VALUES ('enterECR', 'ECR Access Permission');


INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('admin', 'superuser');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('pageManager', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('pageManager', 'managePages');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'validateContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'manageResources');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('reviewer', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('reviewer', 'manageReview');


INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('pageManagerCoach', 'coach', 'pageManager');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('pageManagerCustomers', 'customers', 'pageManager');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('supervisorCoach', 'coach', 'supervisor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('supervisorCustomers', 'customers', 'supervisor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('editorCoach', 'coach', 'editor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('editorCustomers', 'customers', 'editor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('supervisorCoach', 'customers', 'supervisor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('editorCoach', 'customers', 'editor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('mainEditor', 'administrators', 'editor');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('pageManagerCoach', 'customers', 'pageManager');
INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('admin', 'administrators', 'admin');


INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('supervisorCoach', '{bcrypt}$2a$10$zy1zkH5mP09rGv.iSYQiPunsc7F9Rd/TpZXm03YtSfZVeHK9Nddw2', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('mainEditor', '{bcrypt}$2a$10$WUtgtTwdhJdD0hTBu0aIlOgjdgv5wZ7W1BD9Nh.woEzmEfq3m1CT.', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('pageManagerCoach', '{bcrypt}$2a$10$NIhSwtsre0H9tVDVpcs86eN/vR816tJxEPJwbtU4XeJOoFfvOYX6m', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('supervisorCustomers', '{bcrypt}$2a$10$pAmySl8JN1jYKRO9A88sEeesmrWiTOPndbgvifWjKW0BMD7zFk0JK', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('pageManagerCustomers', '{bcrypt}$2a$10$eAFQsWoQG9k9.D6mo0aQJu/aAXGJE/dwuOBj8sbXPL7CH3YiWRVyG', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('editorCustomers', '{bcrypt}$2a$10$6mbu1yVQ/jdgPnuqFMvbYOQklHY6VmIBUZTeYaY3OhxiGx0Yjbx3K', '2008-09-25 00:00:00', '2009-07-02 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('editorCoach', '{bcrypt}$2a$10$8KYc6sUA7fiC2Pia20J4ouMk3Meb.zW3qk0QBD8EZ0vQiI0jqysMa', '2008-09-25 00:00:00', '2009-07-02 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', '{bcrypt}$2a$10$E9R2sHNZ/YXlDn188lpdyeoBl2iSF4E5LE8FNvxbdZbqnqlNP2mL2', '2008-09-25 00:00:00', '2009-12-16 00:00:00', NULL, 1);


INSERT INTO api_oauth_consumers (consumerkey, consumersecret, name, description, callbackurl, scope, authorizedgranttypes, expirationdate, issueddate) VALUES ('test1_consumer', '{bcrypt}$2a$10$PYqyHKEZIrI19ADxqzmlfO8Za3X5oADPoL7nhbtxLeVe8TJyO//Zu', 'Test 1 Consumer', 'Test 1 Consumer Description', 'http://localhost/login', 'read,write,trust', 'password,authorization_code,refresh_token,implicit', '2028-10-10 00:00:00', '2008-01-01 00:00:00');
INSERT INTO api_oauth_consumers (consumerkey, consumersecret, name, description, callbackurl, scope, authorizedgranttypes, expirationdate, issueddate) VALUES ('test2_consumer', '{bcrypt}$2a$10$PYqyHKEZIrI19ADxqzmlfO8Za3X5oADPoL7nhbtxLeVe8TJyO//Zu', 'Test 2 Consumer', 'Test 2 Consumer Description', 'http://localhost/login', 'read,write,trust', 'authorization_code,implicit', '2028-10-10 00:00:00', '2008-01-01 00:00:00');


INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('editorCustomers', 'PFL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="editorCustomers" typecode="PFL" typedescr="Default user profile"><descr /><groups /><categories />
	<attributes>
		<attribute name="fullname" attributetype="Monotext"><monotext>Sean Red</monotext></attribute>
		<attribute name="birthdate" attributetype="Date"><date>19520521</date></attribute>
		<attribute name="email" attributetype="Monotext"><monotext>sean.red@mailinator.com</monotext></attribute>
		<attribute name="language" attributetype="Monotext"><monotext>it</monotext></attribute>
		<attribute name="boolean1" attributetype="Boolean"><boolean>false</boolean></attribute>
		<attribute name="boolean2" attributetype="Boolean"><boolean>false</boolean></attribute>
	</attributes>
</profile>', 0);
INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('mainEditor', 'PFL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="mainEditor" typecode="PFL" typedescr="Default user profile"><descr /><groups /><categories />
	<attributes>
		<attribute name="fullname" attributetype="Monotext"><monotext>Amanda Chedwase</monotext></attribute>
		<attribute name="birthdate" attributetype="Date"><date>19471124</date></attribute>
		<attribute name="email" attributetype="Monotext"><monotext>amanda.chedwase@mailinator.com</monotext></attribute>
		<attribute name="language" attributetype="Monotext"><monotext>it</monotext></attribute>
		<attribute name="boolean1" attributetype="Boolean"><boolean>false</boolean></attribute>
		<attribute name="boolean2" attributetype="Boolean"><boolean>false</boolean></attribute>
	</attributes>
</profile>', 0);
INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('pageManagerCoach', 'PFL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="pageManagerCoach" typecode="PFL" typedescr="Default user profile"><descr /><groups /><categories />
	<attributes>
		<attribute name="fullname" attributetype="Monotext"><monotext>Raimond Stevenson</monotext></attribute>
		<attribute name="birthdate" attributetype="Date"><date>20000904</date></attribute>
		<attribute name="email" attributetype="Monotext"><monotext>raimond.stevenson@mailinator.com</monotext></attribute>
		<attribute name="language" attributetype="Monotext"><monotext>it</monotext></attribute>
		<attribute name="boolean1" attributetype="Boolean"><boolean>false</boolean></attribute>
		<attribute name="boolean2" attributetype="Boolean"><boolean>false</boolean></attribute>
	</attributes>
</profile>', 0);
INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('editorCoach', 'PFL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="editorCoach" typecode="PFL" typedescr="Default user profile"><descr /><groups /><categories />
	<attributes>
		<attribute name="fullname" attributetype="Monotext"><monotext>Rick Bobonsky</monotext></attribute>
		<attribute name="email" attributetype="Monotext"><monotext>rick.bobonsky@mailinator.com</monotext></attribute>
		<attribute name="birthdate" attributetype="Date"><date>19450301</date></attribute>
		<attribute name="language" attributetype="Monotext"><monotext>it</monotext></attribute>
		<attribute name="boolean1" attributetype="Boolean"><boolean>false</boolean></attribute>
		<attribute name="boolean2" attributetype="Boolean"><boolean>false</boolean></attribute>
	</attributes>
</profile>', 0);
INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('supervisorCoach', 'ALL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="supervisorCoach" typecode="ALL" typedescr="Content type with all attribute types"><descr /><groups /><categories />
<attributes>
<attribute name="Boolean" attributetype="Boolean"><boolean>true</boolean></attribute>
<attribute name="CheckBox" attributetype="CheckBox" />
<attribute name="Date" attributetype="Date"><date>20100321</date></attribute>
<attribute name="Date2" attributetype="Date"><date>20120321</date></attribute>
<attribute name="Enumerator" attributetype="Enumerator"><monotext>a</monotext></attribute>
<attribute name="EnumeratorMap" attributetype="EnumeratorMap"><key>02</key><value>Value 2 Old</value></attribute>
<attribute name="Hypertext" attributetype="Hypertext"><hypertext lang="it"><![CDATA[<p>text Hypertext</p>]]></hypertext></attribute>
<attribute name="Longtext" attributetype="Longtext"><text lang="it">text Longtext</text></attribute>
<attribute name="Email" attributetype="Email"><monotext>test.test@entando.com</monotext></attribute>
<attribute name="Monotext" attributetype="Monotext"><monotext>text Monotext</monotext></attribute>
<attribute name="Mail" attributetype="Mail"><monotext>test.test@entando.com</monotext></attribute>
<attribute name="Monotext2" attributetype="Monotext"><monotext>aaaa@entando.com</monotext></attribute>
<attribute name="Number" attributetype="Number"><number>25</number></attribute>
<attribute name="Number2" attributetype="Number"><number>85</number></attribute>
<attribute name="Text" attributetype="Text"><text lang="it">text Text</text></attribute>
<attribute name="Text2" attributetype="Text"><text lang="it">bbbb@entando.com</text></attribute>
<attribute name="ThreeState" attributetype="ThreeState"><boolean>false</boolean></attribute>
<attribute name="Timestamp" attributetype="Timestamp"><timestamp>2020-09-25 17:46:20:123</timestamp></attribute>
<composite name="Composite" attributetype="Composite">
<attribute name="Boolean" attributetype="Boolean"><boolean>true</boolean></attribute>
<attribute name="CheckBox" attributetype="CheckBox" />
<attribute name="Date" attributetype="Date"><date>20100328</date></attribute>
<attribute name="Timestamp" attributetype="Timestamp"><date>20100328</date></attribute>
<attribute name="Enumerator" attributetype="Enumerator" />
<attribute name="Hypertext" attributetype="Hypertext"><hypertext lang="it"><![CDATA[text Hypertext of Composite]]></hypertext></attribute>
<attribute name="Longtext" attributetype="Longtext"><text lang="it">text Longtext of Composite</text></attribute>
<attribute name="Monotext" attributetype="Monotext"><monotext>text Monotext of Composite</monotext></attribute>
<attribute name="Number" attributetype="Number"><number>89</number></attribute>
<attribute name="Text" attributetype="Text"><text lang="it">text Text of Composite</text></attribute>
<attribute name="ThreeState" attributetype="ThreeState"><boolean>true</boolean></attribute>
<attribute name="Timestamp" attributetype="Timestamp"><timestamp>1972-09-25 17:46:20:123</timestamp></attribute>
</composite><attribute name="MARKER" attributetype="Monotext"><monotext>MARKER</monotext></attribute>
</attributes></profile>', 0);


INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCoach', 'fullname', 'Rick Bobonsky', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCoach', 'email', 'rick.bobonsky@mailinator.com', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCoach', 'birthdate', NULL, '1945-03-01 00:00:00', NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCoach', 'boolean1', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCoach', 'boolean2', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCustomers', 'fullname', 'Sean Red', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCustomers', 'email', 'sean.red@mailinator.com', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCustomers', 'birthdate', NULL, '1952-05-21 00:00:00', NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCustomers', 'boolean1', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('editorCustomers', 'boolean2', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('mainEditor', 'fullname', 'Amanda Chedwase', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('mainEditor', 'email', 'amanda.chedwase@mailinator.com', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('mainEditor', 'birthdate', NULL, '1947-11-24 00:00:00', NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('mainEditor', 'boolean1', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('mainEditor', 'boolean2', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('pageManagerCoach', 'fullname', 'Raimond Stevenson', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('pageManagerCoach', 'email', 'raimond.stevenson@mailinator.com', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('pageManagerCoach', 'birthdate', NULL, '2000-09-04 00:00:00', NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('pageManagerCoach', 'boolean1', 'false', NULL, NULL, NULL);
INSERT INTO authuserprofilesearch (username, attrname, textvalue, datevalue, numvalue, langcode) VALUES ('pageManagerCoach', 'boolean2', 'false', NULL, NULL, NULL);




INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('editorCoach', 'fullname', 'userprofile:fullname');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('editorCustomers', 'fullname', 'userprofile:fullname');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('mainEditor', 'fullname', 'userprofile:fullname');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('pageManagerCoach', 'fullname', 'userprofile:fullname');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('editorCoach', 'email', 'userprofile:email');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('editorCustomers', 'email', 'userprofile:email');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('mainEditor', 'email', 'userprofile:email');
INSERT INTO authuserprofileattrroles (username, attrname, rolename) VALUES ('pageManagerCoach', 'email', 'userprofile:email');