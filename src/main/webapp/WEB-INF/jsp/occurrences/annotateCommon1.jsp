<%@ include file="/common/taglibs.jsp"%>
        <tr>
            <td valign="top">Comment<input name="10" type="hidden" value="descr"/></td>
            <td colspan="2"><textarea name="comment" class="comment newValues" rows="8" cols="45"></textarea></td>
        </tr>
        <tr>
            <td valign="top">Choose an identity<input name="10" type="hidden" value="descr"/></td>
            <td colspan="2" class="identification">
                <div class="formIdentity"><input type="radio" name="ident" value="name" checked="checked" onClick="$('.creatorInfo').show();"/>
                    <label for="ident">Registered User</label>
                </div>
                <c:set var="occurrenceHtmlUrl" value="http://${pageContext.request.serverName}${pageContext.request.contextPath}/occurrences/${occurrence.id}"/>
                <div class="creatorInfo"><ala:loginStatus returnUrlPath="${occurrenceHtmlUrl}" />
                </div>
                <div class="formIdentity"><input type="radio" name="ident" value="anon" onClick="$('.creatorInfo').hide();"/>
                    <label for="creator">Anonymous</label>
                    <input type="hidden" name="creator" value="guest"/>
                </div>
            </td>
        </tr>