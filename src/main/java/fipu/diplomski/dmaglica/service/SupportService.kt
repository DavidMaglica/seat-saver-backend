package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.configuration.MailSenderConfiguration
import fipu.diplomski.dmaglica.model.response.BasicResponse
import io.klogging.Klogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class SupportService(
    private val mailSender: JavaMailSender,
    private val mailSenderConfiguration: MailSenderConfiguration
) {

    companion object : Klogging

    suspend fun sendEmail(receiver: String, subject: String, body: String): BasicResponse {

        val message = SimpleMailMessage().apply {
            from = receiver
            setTo(mailSenderConfiguration.getUsername())
            setSubject(subject)
            text = body
        }

        return try {
            mailSender.send(message)
            BasicResponse(true, "Email sent successfully")
        } catch (e: Exception) {
            logger.e("Error while sending email: ${e.message}")
            return BasicResponse(false, "There was an error while sending the email")
        }
    }
}