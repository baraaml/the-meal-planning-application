package com.example.mealflow.database.token

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

fun generateSecretKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    val keySpec = KeyGenParameterSpec.Builder(
        "my_secure_key",
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()

    keyGenerator.init(keySpec)
    return keyGenerator.generateKey()
}
