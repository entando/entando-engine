<#assign s=JspTaglibs["/struts-tags"]>
<#assign wpsf=JspTaglibs["/apsadmin-form"]>

<@wpsf.textarea
	useTabindexAutoIncrement=true
	cols="50"
	rows="3"
	id="%{#attribute_id}"
	name="%{#attributeTracer.getFormFieldName(#attribute)}"
	value="%{#attribute.textMap[#lang.code]}"  />