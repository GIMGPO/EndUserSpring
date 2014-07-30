<!-- Below form added by Chaya Somanchi for TDP 1.1b requirement 5.1.3.Improve existing user feedback visibility -->
<%@ page import="java.util.ResourceBundle, java.util.Enumeration, java.util.Locale, org.apache.commons.lang3.LocaleUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
ResourceBundle bundle = ResourceBundle.getBundle("appStr", LocaleUtils.toLocale((String) session.getAttribute("lang")));
String textAreaDefaultText = bundle.getString("feedback.textbox.text"); 
String yes = bundle.getString("feedback.yes");
String no = bundle.getString("feedback.no");
String browsing = bundle.getString("feedback.browsing");
%>
<form name="feedback" id="feedback" method="post" action="sendFeedback">
<br><br>
<hr width="80%" align="left" noshade="noshade" size="1"/>  
<div id="ConAndBut">	
<br><br>
	<p><h4><b><fmt:message key="feedback.main.text" /></b></h4></p>
	<table>
		<tr>
			<!--  <td><textarea cols="50" rows="5" name=txtFeedback value=""><fmt:message key="feedback.textbox.text" /></textarea></td>-->
			<td><textarea name="txtFeedback" cols="50" rows="5" onblur="if (this.value == '') {this.value = '<%=textAreaDefaultText %>';}"   onfocus="if (this.value == '<%=textAreaDefaultText %>') {this.value = '';}"><%=textAreaDefaultText %></textarea></td>
			<td width="50px"></td>
			<td>
				<p align="center"><fmt:message key="feedback.excellent" /></p>
				<input type="radio" name=rating value="++"/><label> + +</label><br>
				<input type="radio" name=rating value="+" /><label> +</label><br>
				<input type="radio" name=rating value="+-"/><label> + -</label><br>
				<input type="radio" name=rating value="-" /><label> -</label><br>
				<input type="radio" name=rating value="--"/><label> - -</label><br>
				<p align="center"><fmt:message key="feedback.poor" /></p>
			</td>
		</tr>
		<tr>
			<td colspan=3>
				<table>
					<tr>
						<td><fmt:message key="feedback.problemsolved"/></td>
						<td><input type="radio" name="problemsolved" value="<%=yes %>"/><label><fmt:message key="feedback.yes"/></label></td>
						<td><input type="radio" name="problemsolved" value="<%=no %>"/><label><fmt:message key="feedback.no"/></label></td>
						<td><input type="radio" name="problemsolved" value="<%=browsing %>"/><label><fmt:message key="feedback.browsing"/></label></td>
					</tr>
				</table>
			</td>
		</tr>		
	</table>	
	</div>	
	<hr width="80%" align="left" noshade="noshade" size="1"/>  	
</form>