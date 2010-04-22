<%@ include file="/common/taglibs.jsp"%>
        <tr>
            <td valign="top">Comment<input name="10" type="hidden" value="descr"/></td>
            <td colspan="2"><textarea name="comment" class="comment newValues" rows="8" cols="45"></textarea></td>
        </tr>
        <tr>
            <td valign="top">Choose an identity<input name="10" type="hidden" value="descr"/></td>
            <td colspan="2" class="identification">
                <div class="formIdentity"><input type="radio" name="ident" value="name" checked="checked" onClick="$('.creatorInfo').show();"/>
                    <label for="ident">Name/Email</label>
                </div>
                <div class="creatorInfo">
                    <label for="creator-name">Name</label> <input type="text" size="25" name="creator-name" id="creator-name" value=""/><br/>
                    <label for="creator-email">Email</label> <input type="text" size="25" name="creator-email" id="creator-email" value=""/>
                </div>
                <div class="formIdentity"><input type="radio" name="ident" value="anon" onClick="$('.creatorInfo').hide();"/>
                    <label for="creator">Anonymous</label>
                    <input type="hidden" name="creator" value="guest"/>
                </div>
            </td>
        </tr>