package edu.skku.cs.bitcointothemoon.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import edu.skku.cs.bitcointothemoon.MainActivity
import edu.skku.cs.bitcointothemoon.databinding.FragmentIntroBinding


class IntroFragment : Fragment() {
    companion object {
        const val EXT_USERNAME = "extra_key_username"
        const val EXT_NICKNAME = "extra_key_nickname"
    }
    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        binding.ivKaKaoLogin.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) { // 로그인 실패
                    if (container != null) {
                        Toast.makeText(container.context, "카카오 로그인 실패!", Toast.LENGTH_LONG).show()
                    }
                } else if (token != null) { // 로그인 성공
                    if (container != null) {
                        UserApiClient.instance.me { user, error ->
                            if (user != null) {
                                val intent = Intent(container.context, MainActivity::class.java).apply{
                                    putExtra(EXT_USERNAME, "${user.kakaoAccount?.email}")
                                    putExtra(EXT_NICKNAME, "${user.kakaoAccount?.profile?.nickname}")
                                }
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            if (container != null) {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(container.context)) {
                    UserApiClient.instance.loginWithKakaoTalk(container.context) { token, error ->
                        if (error != null) {
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }
                            UserApiClient.instance.loginWithKakaoAccount(
                                container.context,
                                callback = callback
                            )
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(container.context, callback = callback)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}