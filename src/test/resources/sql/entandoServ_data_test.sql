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


