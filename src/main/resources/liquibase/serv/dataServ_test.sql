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