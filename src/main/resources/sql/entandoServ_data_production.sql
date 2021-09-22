INSERT INTO authgroups (groupname, descr) VALUES ('administrators', 'Administrators');
INSERT INTO authgroups (groupname, descr) VALUES ('free', 'Free Access');


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



INSERT INTO authroles (rolename, descr) VALUES ('admin', 'Administrator');
INSERT INTO authroles (rolename, descr) VALUES ('editor', 'Editor');
INSERT INTO authroles (rolename, descr) VALUES ('approver', 'Approver');
INSERT INTO authroles (rolename, descr) VALUES ('reviewer', 'Reviewer');



INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('admin', 'superuser');

INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'manageResources');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'manageCategories');

INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'managePages');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'manageResources');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'manageCategories');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('approver', 'validateContents');

INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('reviewer', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('reviewer', 'manageReview');



INSERT INTO authusergrouprole (username, groupname, rolename) VALUES ('admin', 'administrators', 'admin');

INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', '{bcrypt}$2a$10$TMRaAmZE4w5LEeELdmpJguuSuJc2D9hUelMGmsJyK35K3PBiePqXu', '2008-10-10 00:00:00', '2011-02-05 00:00:00', NULL, 1);



INSERT INTO api_oauth_consumers (consumerkey, consumersecret, name, description, callbackurl, scope, authorizedgranttypes, expirationdate, issueddate) VALUES ('appbuilder', '{bcrypt}$2a$10$axXuJXKHzgdmwQzBR3wvh.oSiiJp6On1pHxJgaBmwkRXnXqYqjhkK', 'Entando AppBuilder', 'Default Entando AppBuilder Consumer', NULL, 'read,write,trust', 'password,authorization_code,refresh_token,implicit', '2028-10-10 00:00:00', '2008-01-01 00:00:00');




