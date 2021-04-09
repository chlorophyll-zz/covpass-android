package com.ibm.health.common.vaccination.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

/**
 * Storage that provides an encryption functionality.
 * Can be used for store any objects that should be encrypted.
 */
public class EncryptedKeyValueStore(context: Context, preferencesName: String) {

    /**
     * Interface for accessing and modifying data in storage.
     */
    public val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            preferencesName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Sets key-value pair to the storage.
     * @param key Will be used for access to the obj
     * @param obj The value that will be stored. Must be annotated with compatible with kotlinx.serialization
     */
    public inline fun <reified T> set(key: String, obj: T) {
        prefs.edit().putString(key, Base64.encodeToString(Cbor.encodeToByteArray(obj), Base64.DEFAULT)).apply()
    }

    /**
     * @param key Used for access to the [T] object
     * @return an object from the storage by provided [key], null otherwise.
     */
    public inline fun <reified T : Any> get(key: String): T? =
        prefs.getString(key, null)?.let { Cbor.decodeFromByteArray<T>(Base64.decode(it, Base64.DEFAULT)) }

    /**
     * Checks if provided [key] contains in the storage.
     */
    public fun contains(key: String): Boolean = prefs.contains(key)

    /**
     * Removes provided [key] from the storage.
     */
    public fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * Clears the shared preferences.
     */
    public fun clear() {
        prefs.edit().clear().apply()
    }
}