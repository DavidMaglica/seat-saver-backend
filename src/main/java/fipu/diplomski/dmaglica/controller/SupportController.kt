package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.service.SupportService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for handling customer support operations.
 *
 * Provides endpoints related to support ticket management and communication.
 * All endpoints are prefixed with the base path defined in [Paths.SUPPORT].
 *
 * @property supportService The service handling support-related business logic
 */
@RestController
@RequestMapping(Paths.SUPPORT)
class SupportController(
    private val supportService: SupportService
) {

    /**
     * Processes and forwards a user support request via email.
     *
     * Sends an email to the configured support address with:
     * - Subject: "Support Ticket from [userEmail] - [subject]"
     * - Body: The user's original message content
     *
     * @param userEmail The email address of the requester
     * @param subject Brief description of the support issue
     * @param body Detailed description of the support request
     * @return BasicResponse with:
     *   - success: true if email was queued successfully
     *   - message: Delivery status notification
     *
     */
    @PostMapping(Paths.SEND_EMAIL)
    fun sendEmail(
        @RequestParam("userEmail") userEmail: String,
        @RequestParam("subject") subject: String,
        @RequestParam("body") body: String
    ): BasicResponse = supportService.sendEmail(userEmail, subject, body)
}