<#assign s=JspTaglibs["/struts-tags"]>
<#assign wp=JspTaglibs["/aps-core"]>
<#assign wpsa=JspTaglibs["/apsadmin-core"]>
<#assign wpsf=JspTaglibs["/apsadmin-form"]>

<@s.set var="add_label"><@wp.i18n key="userprofile_ADDITEM_LIST" /></@s.set>

<@wpsa.actionParam action="addListElement" var="actionName" >
	<@wpsa.actionSubParam name="attributeName" value="%{#attribute.name}" />
	<@wpsa.actionSubParam name="listLangCode" value="%{#lang.code}" />
</@wpsa.actionParam>
<@s.set var="iconImagePath" id="iconImagePath"><@wp.resourceURL/>administration/common/img/icons/list-add.png</@s.set>
<@wpsf.submit
	cssClass="btn"
	useTabindexAutoIncrement=true
	action="%{#actionName}"
	value="%{add_label}"
	title="%{i18n_attribute_name}%{': '}%{add_label}" />