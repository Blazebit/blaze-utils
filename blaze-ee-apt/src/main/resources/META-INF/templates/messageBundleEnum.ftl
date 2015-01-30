/**
 * Generated, Do Not Modify
 */

package ${packageName};

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

@BaseName("${baseName}")
@LocaleData(defaultCharset = "UTF-8", value = {
    <#list locales as locale>
    	@Locale("${locale}")<#if locale_has_next>,</#if>
    </#list>
})
public enum ${enumName} {
<#if enumKeys?has_content>	
	<#list enumKeys as enumKey>
	${enumKey}<#if enumKey_has_next>,<#else>;</#if>
    </#list>
<#else>;</#if>
}