package trisoftdp.core;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class DynMail {

	public static void postMail( String recipients[ ], String subject, String message , String from, String contentType) throws MessagingException, UnsupportedEncodingException {
		boolean debug = false;
		Properties props = new Properties();
		int maxNumber = Integer.parseInt(CoreConstants.appPropsMap.get("EMAIL_MAX_NUMBER"));
		int rl = (recipients.length > maxNumber) ? maxNumber : recipients.length;		
		props.put(CoreConstants.SMTP_HOST, CoreConstants.MAIL_HUB_HOST);
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);
		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);
		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < rl; i++)
			addressTo[i] = new InternetAddress(recipients[i]);
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		msg.setSentDate(new Date());
		msg.addHeader("MyHeaderName", "myHeaderValue");
		msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		//msg.setSubject(subject);
		if(contentType == null)
			msg.setContent(message, "text/plain; charset=utf-8");
		else
			msg.setContent(message, contentType);
		Transport.send(msg);
	}

}
