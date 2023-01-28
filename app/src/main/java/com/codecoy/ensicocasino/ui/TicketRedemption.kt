package com.codecoy.ensicocasino.ui


import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.codecoy.ensicocasino.MyApp
import com.codecoy.ensicocasino.MyApp.Companion.mainScreen
import com.codecoy.ensicocasino.R
import com.codecoy.ensicocasino.databinding.ActivityTicketRedemptionBinding
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.repository.MainRepository
import com.codecoy.ensicocasino.retrofit.RetrofitAPI
import com.codecoy.ensicocasino.retrofit.RetrofitService
import com.codecoy.ensicocasino.viewmodels.MainViewModel
import com.codecoy.ensicocasino.viewmodels.MyViewModelFactory
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.DecimalFormat

class TicketRedemption : AppCompatActivity() {
    private lateinit var binding: ActivityTicketRedemptionBinding
    lateinit var viewModel: MainViewModel
    private lateinit var retrofitService: RetrofitAPI
    private lateinit var hud: KProgressHUD
    private lateinit var BASE_URL: String
    private lateinit var client: String
    private lateinit var sp: SharedPreferences
    var format: DecimalFormat = DecimalFormat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketRedemptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hudProgress()
        format.isDecimalSeparatorAlwaysShown = false

        sp = getSharedPreferences("MyPref", MODE_PRIVATE)
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()
        client = sp.getString("client", "ExampleMobileClient2").toString()

        binding.textView.text = "Amount (in ${sp.getString("currency", "EUR").toString()})"

        retrofitService = RetrofitService.getInstance().RetrofitServices(BASE_URL)

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        viewModel.user.observe(this, Observer {
            when (it) {
                is APIResponse.Loading -> {
                    hud.show()
                }
                is APIResponse.Error -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    MyApp.showToast(this, "$it.message")
                }
                is APIResponse.Success<*> -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    //  Log.d(TAG, "onCreate   viewModel.movieList.observe: $it")
                    val response = it.data as LoginResponse
                    when (response.responseCode) {
                        0 -> {
                            MyApp.exitCasino(this)
                        }
                        else -> println("I don't know anything about it")
                    }
                }
            }
        })

        viewModel.ticketRedemption.observe(this, Observer {
            when (it) {
                is APIResponse.Loading -> {
                    hud.show()
                }
                is APIResponse.Error -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    Log.d("TAG", "API Error :" + it.message)
                    MyApp.showToast(this, "API Error :" + it.message)
                }
                is APIResponse.Success<*> -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    //  Log.d(TAG, "onCreate   viewModel.movieList.observe: $it")
                    val response = it.data as TicketRedemptionResponse
                    if (response.type.equals("ticketRedemptionNack")) {
                        // nothing is selected and user go out from main menu on success

                        when (response.responseCode) {
                            0 -> {
                                mainScreen(this)
                                finishAffinity()
                            }
                            else -> {}
                        }

                    } else {
                        when (response.responseCode) {
                            0 -> {
                                binding.tvMessage.setText("ticket redemed successfully.")
                            }
                            1 -> {
                                binding.tvMessage.setText("ticket unknown!.")
                            }
                            2 -> { binding.tvMessage.setText("ticket already redeemed") }
                            3 -> binding.tvMessage.setText("ticket expired. This ticket has expired")
                            4 -> binding.tvMessage.setText("ticket not valid. ${response.reason}")
                            100 -> binding.tvMessage.setText("general Server error. ${response.reason}")

                            else -> {}

                        }
                    }

                }
            }
        })




        binding.ticket.setOnClickListener(View.OnClickListener {
            viewModel.ticketRedemption(
                TicketRedemptionRequest(
                    System.currentTimeMillis().toString(),
                    client,
                    "ticketRedemptionAck",
                    intent.getStringExtra("ticketNo").toString()
                )
            )
        })





        binding.etTicketNo.setText(intent.getStringExtra("ticketNo"))

        val amont:Double = intent.getIntExtra("ticketAmount", 0).toDouble().div(100.0)

     //   MyApp.showToast(this,intent.getIntExtra("ticketAmount", 0).toString())

        binding.etTicketAmount.setText("${format.format(amont)}")

        binding.toolbar.btnMain.setOnClickListener(View.OnClickListener {
            viewModel.ticketRedemption(
                TicketRedemptionRequest(
                    System.currentTimeMillis().toString(),
                    client,
                    "ticketRedemptionNack",
                    intent.getStringExtra("ticketNo").toString()
                )
            )
        })

        binding.toolbar.btnLogout.setOnClickListener(View.OnClickListener {
            logout()
        })
    }

    private fun logout() {
        AwesomeWarningDialog(this)
            .setTitle(R.string.app_name)
            .setMessage(R.string.exit)
            .setColoredCircle(R.color.purple_500)
            .setDialogIconAndColor(R.drawable.exit, R.color.white)
            .setCancelable(true)
            .setButtonText("Exit")
            .setButtonTextColor(R.color.white)
            .setButtonBackgroundColor(R.color.purple_500)
            .setButtonText("Exit")
            .setWarningButtonClick(object : Closure {
                override fun exec() {
                    viewModel.logoutUser(
                        LoginRequest(
                            System.currentTimeMillis().toString(),
                            client,
                            "attendantLogout",
                            sp.getString("username", "").toString()
                        )
                    )
                }
            })
            .show()
    }

    private fun hudProgress() {
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }
}