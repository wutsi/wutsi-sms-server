package com.wutsi.platform.sms.service.gateway

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SMSGatewayAWSTest {
    private lateinit var amazonSNS: AmazonSNS
    private lateinit var gateway: SMSGateway

    @BeforeEach
    fun setUp() {
        amazonSNS = mock()
        gateway = SMSGatewayAWS(amazonSNS)
    }

    @Test
    fun `send message`() {
        doReturn(PublishResult().withMessageId("111")).whenever(amazonSNS).publish(any())

        val messageId = gateway.send("+23774511100", "Yo man")

        val request = argumentCaptor<PublishRequest>()
        verify(amazonSNS).publish(request.capture())

        assertEquals("111", messageId)
        assertEquals("+23774511100", request.firstValue.phoneNumber)
        assertEquals("Yo man", request.firstValue.message)
    }

    @Test
    fun `send message with accent`() {
        doReturn(PublishResult().withMessageId("111")).whenever(amazonSNS).publish(any())

        val messageId = gateway.send("+23774511100", "Wutsi: Vous avez reçu un paiement de Hervé")

        val request = argumentCaptor<PublishRequest>()
        verify(amazonSNS).publish(request.capture())

        assertEquals("111", messageId)
        assertEquals("+23774511100", request.firstValue.phoneNumber)
        assertEquals("Wutsi: Vous avez recu un paiement de Herve", request.firstValue.message)
    }

    @Test
    fun `send message with error`() {
        doThrow(RuntimeException::class).whenever(amazonSNS).publish(any())

        assertThrows<SMSException> {
            gateway.send("+23774511100", "Yo man")
        }
    }
}
