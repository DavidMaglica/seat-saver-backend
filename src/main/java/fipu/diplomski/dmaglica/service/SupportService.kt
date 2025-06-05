package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.configuration.MailSenderConfiguration
import fipu.diplomski.dmaglica.model.response.BasicResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class SupportService(
    private val mailSender: JavaMailSender,
    private val mailSenderConfiguration: MailSenderConfiguration
) {

    companion object {
        private val logger = KotlinLogging.logger(SupportService::class.java.name)
    }

    fun sendEmail(userEmail: String, subject: String, body: String): BasicResponse {
        val message = SimpleMailMessage()
        message.setTo(mailSenderConfiguration.getUsername())
        message.subject = "Support Ticket from $userEmail - $subject"
        message.text = body

        return try {
            mailSender.send(message)
            BasicResponse(true, "Email sent successfully.")
        } catch (e: Exception) {
            logger.error { "There was an error while sending the email: ${e.message}" }
            BasicResponse(false, "There was an error while sending the email. Please try again later.")
        }
    }
}