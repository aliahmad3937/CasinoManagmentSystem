package com.codecoy.ensicocasino.ui

import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.codecoy.ensicocasino.MyApp
import com.codecoy.ensicocasino.biometrics.common.BiometricAuthListener
import com.codecoy.ensicocasino.databinding.ActivityLoginBinding

import com.codecoy.ensicocasino.models.APIResponse
import com.codecoy.ensicocasino.models.LoginRequest
import com.codecoy.ensicocasino.models.LoginResponse
import com.codecoy.ensicocasino.retrofit.RetrofitService
import com.codecoy.ensicocasino.biometrics.util.BiometricUtil
import com.kaopiz.kprogresshud.KProgressHUD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreen : AppCompatActivity(),
    BiometricAuthListener {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sp: SharedPreferences

    private lateinit var BASE_URL: String
    private lateinit var hud: KProgressHUD

    val user = MutableLiveData<APIResponse>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.statusBarColor = Color.WHITE
//        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hudProgress()



        sp = getSharedPreferences("MyPref", MODE_PRIVATE)
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()

        binding.etBaseUrl.setText(BASE_URL)
        (if(sp.getString("client", "").toString().isEmpty())
            android.os.Build.MODEL
        else
            sp.getString("client", "").toString()
                ).also {
            binding.etClientName.setText(it)
            }


        binding.ivBaseUrl.setOnClickListener {
            onOffBaseURLVisibility()
        }

        user.observe(this, Observer {
            when (it) {
                is APIResponse.Loading -> {
                    hud.show()
                }
                is APIResponse.Error -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    MyApp.showToast(this, "${it.message}")
                    binding.tvError.text = it.message
                }
                is APIResponse.Success<*> -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    //  Log.d(TAG, "onCreate   viewModel.movieList.observe: $it")
                    val response = it.data as LoginResponse
                    when (response.responseCode) {
                        0 -> {
                            sp.edit().apply {
                                putString("clientName", response.clientName)
                                putString("username", response.username)
                                putString("currency", response.currency.uppercase())
                            }.apply()
                            MyApp.showToast(this, "log in succesfull")

                            val intent = Intent(
                                this,
                                MainScreen::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        10 -> {
                            binding.tvError.text = "username not recognized"
                            MyApp.showToast(this, "username not recognized")
                        }
                        11 -> {
                            binding.tvError.text = "password incorrect for this username"
                            MyApp.showToast(this, "password incorrect for this username")
                        }
                        12 -> {
                            binding.tvError.text =
                                "user has incorrect user type or missing permissions"
                            MyApp.showToast(
                                this,
                                "user has incorrect user type or missing permissions"
                            )
                        }
                        13 -> {
                            binding.tvError.text =
                                "the client has incorrect client type or clientName is already in use"
                            MyApp.showToast(
                                this,
                                "the client has incorrect client type or clientName is already in use"
                            )

                        }
                        14 -> {
                            binding.tvError.text = "other error"
                            MyApp.showToast(this, "other error")
                        }
                        else -> println("I don't know anything about it")
                    }
                }
            }
        })


        binding.btnLogin.setOnClickListener(View.OnClickListener {
            sp.edit().putString("client", binding.etClientName.text.toString()).apply()
            if (validation()) {
                checkUser(
                    LoginRequest(
                        System.currentTimeMillis().toString(),
                        sp.getString("client", "ExampleMobileClient2").toString(),
                        "attendantLogin",
                        binding.etUsername.text.toString(),
                        binding.etPassword.text.toString(),
                        ""
                    )
                )
            } else {
                MyApp.showToast(this, "please enter Username/Password!")
            }
        })

        binding.ivDone.setOnClickListener(View.OnClickListener {
            if (binding.etBaseUrl.text.endsWith('/')) {
                sp.edit().putString("baseUrl", binding.etBaseUrl.text.toString()).apply()
                sp.edit().putString("client", binding.etClientName.text.toString()).apply()
                onOffBaseURLVisibility()
                MyApp.showToast(this, "Changed Successfully!")
            } else {
                binding.etBaseUrl.error = "BaseUrl must end with '/'"
                binding.tvError.text = "BaseUrl must end with '/'"
            }

        })

    }

    private fun onOffBaseURLVisibility() {
        if (binding.urlLayout.visibility == View.VISIBLE) {
            binding.urlLayout.visibility = View.INVISIBLE
            binding.layoutClient.visibility = View.GONE
        } else {
            binding.urlLayout.visibility = View.VISIBLE
            binding.layoutClient.visibility = View.VISIBLE
        }
    }


    // this request is for login with new base url
    // every time the user allow to change its base url before login
    private fun checkUser(body: LoginRequest) {
        if (binding.etBaseUrl.text.endsWith('/')) {
            user.postValue(APIResponse.Loading)
            RetrofitService.getInstance().RetrofitServices(
                sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()
            ).checkUser(body).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        user.postValue(APIResponse.Success(response.body()))
                    } else {
                        user.postValue(APIResponse.Error("URL is not valid!"))
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    user.postValue(APIResponse.Error(t.localizedMessage))
                }
            })
        } else {
            binding.etBaseUrl.error = "BaseUrl must end with '/'"
            binding.tvError.text = "BaseUrl must end with '/'"
        }
    }

    private fun validation(): Boolean {
        return !(binding.etUsername.text.toString().isEmpty() || binding.etPassword.text.toString()
            .isEmpty())
    }

    private fun hudProgress() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }

    override fun onResume() {
        super.onResume()
        if (!sp.getString("username", "").toString().isEmpty()) {
            showBiometricLoginOption()
        }
    }

    // check whether your device support biometric verification or not
    private fun showBiometricLoginOption() {
        binding.buttonBiometricsLogin.visibility =
            if (BiometricUtil.isBiometricReady(this)) View.VISIBLE
            else View.GONE
    }

    fun onClickBiometrics(view: View) {
        BiometricUtil.showBiometricPrompt(
            activity = this,
            listener = this,
            cryptoObject = null,
            allowDeviceCredential = true
        )
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        //  navigateToListActivity()

        val intent = Intent(
            this,
            MainScreen::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(this, "Biometric login failed. Error: $errorMessage", Toast.LENGTH_SHORT)
            .show()
        binding.tvError.text = "Biometric login failed. Error: $errorMessage"

    }


}