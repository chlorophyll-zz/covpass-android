package com.ibm.health.vaccination.app.certchecker.scanner

import androidx.lifecycle.LifecycleObserver
import com.google.zxing.ResultPoint
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.vaccination.app.dialog.DialogAction
import com.ibm.health.common.vaccination.app.dialog.DialogListener
import com.ibm.health.common.vaccination.app.dialog.DialogModel
import com.ibm.health.common.vaccination.app.dialog.showDialog
import com.ibm.health.common.vaccination.app.scanner.QRScannerFragment
import com.ibm.health.vaccination.app.certchecker.R
import com.ibm.health.vaccination.sdk.android.dependencies.sdkDeps
import com.ibm.health.vaccination.sdk.android.qr.models.ValidationCertificate
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.parcelize.Parcelize

/**
 * QR Scanner Fragment extending from QRScannerFragment to intercept qr code scan result.
 */
@Parcelize
class ValidationQRScannerFragmentNav : FragmentNav(ValidationQRScannerFragment::class)

class ValidationQRScannerFragment : QRScannerFragment(), LifecycleObserver, DialogListener {

    override val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            barcodeView.pause()
            // FIXME this is just a provisionally implementation
            showValidation(sdkDeps.qrCoder.decodeValidationCert(result.text))
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onDialogAction(tag: String, action: DialogAction) {
        if (tag == CERTIFICATE_PREVIEW_DIALOG_TAG) {
            finishScanning()
        }
    }

    fun showValidation(validationCertificate: ValidationCertificate) {
        val dialogModel = DialogModel(
            titleRes = null,
            messageRes = R.string.validation_certificate_preview_dialog_message,
            messageParameter = validationCertificate.toString(),
            positiveButtonTextRes = R.string.validation_certificate_preview_dialog_positive,
            tag = CERTIFICATE_PREVIEW_DIALOG_TAG
        )
        showDialog(dialogModel, childFragmentManager)
    }

    private companion object {
        private const val CERTIFICATE_PREVIEW_DIALOG_TAG = "certificate_preview_dialog"
    }
}