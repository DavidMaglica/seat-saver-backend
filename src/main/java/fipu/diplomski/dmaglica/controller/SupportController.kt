package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.SupportService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Paths.SUPPORT)
class SupportController(
    private val supportService: SupportService
) {

    @PostMapping(Paths.SEND_EMAIL)
    fun sendEmail(
        @RequestParam("receiver") receiver: String,
        @RequestParam("subject") subject: String,
        @RequestParam("body") body: String
    ) = supportService.sendEmail(receiver, subject, body)
}