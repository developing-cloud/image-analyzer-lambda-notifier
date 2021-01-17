package cloud.developing.imageanalyzer.notifier;

import static java.lang.System.getenv;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class Notifier {

	private final static String TOPIC_ARN = getenv("TOPIC_ARN");

	private final static String RECIPIENTS = getenv("RECIPIENTS");

	private final static String FROM = getenv("FROM");

	private final static String SUBJECT = "Zidentifikowano fajne obiekty";

	private final static String CHARSET = "UTF-8";

	private final static String BODY_HTML = "<html><head></head><body>"
			+ "Hej, poniżej znajduje się link do nowej wiadomości głosowej:<br>"
			+ "<a href=\"${link}\">Wysłuchaj komunikat...</a></body></html>";

	private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();

	private final AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClientBuilder.standard()
			.build();

	public void send(String linkAsMessage) throws Exception {
		sns.publish(TOPIC_ARN, "Wiadomość dźwiękowa została wysłana do Ciebie mailem.");

		for (var recipient : RECIPIENTS.split(",")) {
			ses.sendEmail(createEmailRequest(linkAsMessage, recipient));
		}
	}

	private static SendEmailRequest createEmailRequest(String link, String recipient) throws Exception {
		return new SendEmailRequest().withDestination(new Destination().withToAddresses(recipient))
				.withMessage(new Message()
						.withBody(new Body().withHtml(
								new Content().withCharset(CHARSET).withData(BODY_HTML.replace("${link}", link))))
						.withSubject(new Content().withCharset(CHARSET).withData(SUBJECT)))
				.withSource(FROM);
	}

}
