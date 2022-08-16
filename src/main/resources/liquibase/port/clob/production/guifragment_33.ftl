<#assign wp=JspTaglibs["/aps-core"]>

<#if (group.size > group.max)>
	<div class="pagination pagination-centered">
		<ul>
		<#if (1 != group.currItem)>
			<#if (group.advanced)>
				<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >1</@wp.parameter></@wp.url>" title="<@wp.i18n key="PAGER_FIRST" />"><i class="icon-fast-backward"></i></a></li>
				<#if (1 != group.beginItemAnchor)>
					<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${group.currItem - group.offset}</@wp.parameter></@wp.url>" title="<@wp.i18n key="PAGER_STEP_BACKWARD" />&#32;${group.offset}"><i class="icon-step-backward"></i></a></li>
				</#if>
			</#if>
			<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${group.prevItem}</@wp.parameter></@wp.url>"><@wp.i18n key="PAGER_PREV" /></a></li>
		</#if>
		<#list group.items as item>
		<#if (item_index >= (group.beginItemAnchor-1)) && (item_index <= (group.endItemAnchor-1))>
			<#if (item == group.currItem)>
			<li class="active"><a href="#">${item}</a></li>
			<#else>
			<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${item}</@wp.parameter></@wp.url>">${item}</a></li>
			</#if>
		</#if>
		</#list>
		<#if (group.maxItem != group.currItem)>
			<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${group.nextItem}</@wp.parameter></@wp.url>"><@wp.i18n key="PAGER_NEXT" /></a></li>
			<#if (group.advanced)>
				<#if (group.maxItem != group.endItemAnchor)>
					<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${group.currItem + group.offset}</@wp.parameter></@wp.url>" title="<@wp.i18n key="PAGER_STEP_FORWARD" />&#32;${group.offset}"><i class="icon-step-forward"></i></a></li>
				</#if>
				<li><a href="<@wp.url paramRepeat=true escapeAmp=false ><@wp.parameter name="${group.paramItemName}" >${group.maxItem}</@wp.parameter></@wp.url>" title="<@wp.i18n key="PAGER_LAST" />"><i class="icon-fast-forward"></i></a></li>
			</#if>
		</#if>
		</ul>
	</div>
</#if>