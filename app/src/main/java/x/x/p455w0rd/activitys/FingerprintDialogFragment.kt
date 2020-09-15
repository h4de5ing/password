package x.x.p455w0rd.activitys

import android.app.DialogFragment
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import x.x.p455w0rd.R
import javax.crypto.Cipher

@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintDialogFragment : DialogFragment() {
    private var fingerprintManager: FingerprintManager? = null
    private var mCancellationSignal: CancellationSignal? = null
    private var mCipher: Cipher? = null
    private var mActivity: BaseMaskActivity? = null
    private var errorMsg: TextView? = null

    /**
     * 标识是否是用户主动取消的认证。
     */
    private var isSelfCancelled = false
    fun setCipher(cipher: Cipher?) {
        mCipher = cipher
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as BaseMaskActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fingerprintManager = context.getSystemService(FingerprintManager::class.java)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        val v = inflater.inflate(R.layout.fingerprint_dialog, container, false)
        errorMsg = v.findViewById(R.id.error_msg)
        val cancel = v.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dismiss()
            stopListening()
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        // 开始指纹认证监听
        startListening(mCipher)
    }

    override fun onPause() {
        super.onPause()
        // 停止指纹认证监听
        stopListening()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun startListening(cipher: Cipher?) {
        isSelfCancelled = false
        mCancellationSignal = CancellationSignal()
        fingerprintManager!!.authenticate(
            FingerprintManager.CryptoObject(cipher!!),
            mCancellationSignal,
            0,
            object : FingerprintManager.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!isSelfCancelled) {
                        errorMsg!!.text = errString
                        if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                            Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                    errorMsg!!.text = helpString
                }

                override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                    Toast.makeText(mActivity, "指纹认证成功", Toast.LENGTH_SHORT).show()
                    dismiss()
                    mActivity!!.onAuthenticated()
                }

                override fun onAuthenticationFailed() {
                    errorMsg!!.text = "指纹认证失败，请再试一次"
                }
            },
            null
        )
    }

    private fun stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal!!.cancel()
            mCancellationSignal = null
            isSelfCancelled = true
        }
    }
}