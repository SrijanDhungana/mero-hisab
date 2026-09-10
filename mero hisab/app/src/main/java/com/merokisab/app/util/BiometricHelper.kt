package com.merokisab.app.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricHelper {
    private const val PREFS_NAME = "mero_kisab_prefs"
    private const val KEY_LOCK_ENABLED = "lock_enabled"

    fun isBiometricAvailable(context: Context): Boolean {
        val canAuthenticate = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isLockEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOCK_ENABLED, false)

    fun setLockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOCK_ENABLED, enabled)
            .apply()
    }

    fun showBiometricPrompt(
        activity: Activity,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError()
                }
            },
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Mero Kisab")
            .setSubtitle("Authenticate to unlock your ledger")
            .setNegativeButtonText("Cancel")
            .build()
        prompt.authenticate(info)
    }
}