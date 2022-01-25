<#assign s=JspTaglibs["/struts-tags"]>
<#assign wpsf=JspTaglibs["/apsadmin-form"]>
<@wpsf.select useTabindexAutoIncrement=true
	name="%{#attributeTracer.getFormFieldName(#attribute)}"
	id="%{attribute_id}"
	headerKey="" headerValue=""
	list="#attribute.mapItems" value="%{#attribute.getText()}" listKey="key" listValue="value" />