package com.wutsi.platform.security.test

import com.auth0.jwt.interfaces.RSAKeyProvider
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Service
class TestRSAKeyProvider : RSAKeyProvider {
    override fun getPublicKeyById(keyId: String): RSAPublicKey {
        val key = getPublicKeyAsString()
        val byteKey = Base64.getDecoder().decode(key.toByteArray())
        val pk = X509EncodedKeySpec(byteKey)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(pk) as RSAPublicKey
    }

    override fun getPrivateKey(): RSAPrivateKey {
        val key = loadKey("/private-key.txt")
        val byteKey = Base64.getDecoder().decode(key.toByteArray())
        val pk = PKCS8EncodedKeySpec(byteKey)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePrivate(pk) as RSAPrivateKey
    }

    override fun getPrivateKeyId(): String {
        return "1"
    }

    fun getPublicKeyAsString(): String =
        loadKey("/public-key.txt")

    private fun loadKey(path: String): String {
        val inputStream = TestRSAKeyProvider::class.java.getResourceAsStream(path)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readLine()!!
    }
}
