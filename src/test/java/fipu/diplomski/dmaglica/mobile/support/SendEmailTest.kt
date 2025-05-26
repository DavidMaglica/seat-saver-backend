package fipu.diplomski.dmaglica.mobile.support

import fipu.diplomski.dmaglica.configuration.MailSenderConfiguration
import fipu.diplomski.dmaglica.service.SupportService
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles


@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class SendEmailTest {

    @Mock
    private lateinit var mailSender: JavaMailSender

    @Mock
    private lateinit var mailSenderConfiguration: MailSenderConfiguration

    private lateinit var supportService: SupportService

    companion object {
        const val USER_EMAIL = "test@test.com"
        const val SUBJECT = "Test Subject"
        const val BODY = "This is a test email body."
    }

    @BeforeEach
    fun setUp() {
        supportService = SupportService(mailSender, mailSenderConfiguration)
    }

    @Test
    fun `should return failure response when email sending fails`() {
        `when`(mailSender.send(any<SimpleMailMessage>())).thenThrow(RuntimeException::class.java)

        val result = supportService.sendEmail(USER_EMAIL, SUBJECT, BODY)

        result.success `should be` false
        result.message `should be equal to` "There was an error while sending the email. Please try again later."
    }

    @Test
    fun `should send email successfully`() {
        doNothing().`when`(mailSender).send(any<SimpleMailMessage>())

        val result = supportService.sendEmail(USER_EMAIL, SUBJECT, BODY)

        result.success `should be equal to` true
        result.message `should be equal to` "Email sent successfully."
    }
}