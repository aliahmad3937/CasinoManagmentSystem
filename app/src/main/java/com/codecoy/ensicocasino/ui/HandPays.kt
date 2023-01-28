package com.codecoy.ensicocasino.ui

import android.content.SharedPreferences
import android.icu.number.IntegerWidth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.codecoy.ensicocasino.DigitCardFormatWatcher
import com.codecoy.ensicocasino.MyApp
import com.codecoy.ensicocasino.R
import com.codecoy.ensicocasino.adapters.HandpaysAdapter
import com.codecoy.ensicocasino.callbacks.HandpayInterface
import com.codecoy.ensicocasino.databinding.ActivityHandPaysBinding
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.repository.MainRepository
import com.codecoy.ensicocasino.retrofit.RetrofitAPI
import com.codecoy.ensicocasino.retrofit.RetrofitService
import com.codecoy.ensicocasino.viewmodels.MainViewModel
import com.codecoy.ensicocasino.viewmodels.MyViewModelFactory
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.ArrayList

class HandPays : AppCompatActivity(), HandpayInterface {

    private lateinit var binding: ActivityHandPaysBinding

    lateinit var viewModel: MainViewModel
    private lateinit var retrofitService: RetrofitAPI
    private lateinit var hud: KProgressHUD
    private lateinit var BASE_URL: String
    private lateinit var sp: SharedPreferences
    private lateinit var rvAdapter: HandpaysAdapter
    private lateinit var handpay: Handpays
    //   private var handpayList:MutableList<Handpays> = mutableListOf<Handpays>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandPaysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hudProgress()


        binding.amount.addTextChangedListener(DigitCardFormatWatcher(binding.amount))



        sp = getSharedPreferences("MyPref", MODE_PRIVATE)
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()
        retrofitService = RetrofitService.getInstance().RetrofitServices(BASE_URL)

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        retreiveHandpayList()

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

        viewModel.handpayUnlock.observe(this, Observer {
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

                    val response = it.data as HandpayUnlockResponse
                    //   MyApp.showToast(this,response.reason)
                    when (response.responseCode) {
                        0 -> {
                            MyApp.showToast(this, "succesful unlock")
                            binding.unlockHandpays.visibility = View.GONE
                            retreiveHandpayList()
                        }
                        1 -> {
                            MyApp.showToast(this, response.reason)
                        }
                        2 -> {
                            MyApp.showToast(this, response.reason)
                        }
                        else -> println("I don't know anything about it")
                    }
                }
            }
        })


        viewModel.handpayList.observe(this, Observer {
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
                    val response = it.data as HandpaysResponse
                    when (response.responseCode) {
                        0 -> {
                            if (response.handpays.isEmpty()) {
                                binding.tvNothing.visibility = View.VISIBLE
                            } else {
                                binding.tvNothing.visibility = View.GONE
                                setRecyclerView(response.handpays)
                            }
                        }
                        else -> println("I don't know anything about it")
                    }

                }
            }
        })



        binding.toolbar.btnMain.setOnClickListener(View.OnClickListener {
            MyApp.mainScreen(this)
            finishAffinity()
        })

        binding.toolbar.btnLogout.setOnClickListener(View.OnClickListener {
            logout()
        })

        binding.ticket.setOnClickListener(View.OnClickListener {
            //   unlock Hanpay
            val amount: String = binding.amount.text.toString()
            if (amount.isEmpty()) {
                // MyApp.showToast(this, "Please Enter Amount")
                binding.amount.error = "enter amount!"
                return@OnClickListener
            }
            val cleanString = amount.replace(",", "").toDouble()
            viewModel.handpayUnlock(
                HandpayUnlockRequest(
                    System.currentTimeMillis().toString(),
                    sp.getString("client", "ExampleMobileClient2").toString(),
                    "handpayRequestUnlock",
                    Handpays(
                        handpay.gm,
                        handpay.type,
                        (cleanString * 100.0).toInt()
                    )
                )
            )

        })

    }

    private fun retreiveHandpayList() {
        viewModel.handpayList(
            HandpaysRequest(
                System.currentTimeMillis().toString(),
                sp.getString("client", "ExampleMobileClient2").toString(),
                "handpayList",
                "all"
            )
        )
    }

    private fun setRecyclerView(handpays: ArrayList<Handpays>) {
        // create  layoutManager
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)

        // pass it to rvLists layoutManager
        binding.rvHandpayList.setLayoutManager(layoutManager)

        // initialize the adapter,
        // and pass the required argument

        rvAdapter = HandpaysAdapter(handpays, this, this, sp)

        // attach adapter to the recycler view
        binding.rvHandpayList.adapter = rvAdapter
        // rvAdapter.updateList(response.handpays)
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
                            sp.getString("client", "ExampleMobileClient2").toString(),
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

    override fun handpayClick(handpay: Handpays, amoun: String) {
        binding.unlockHandpays.visibility = View.VISIBLE
        binding.gm.setText(handpay.gm)
        //   val amont: Double = handpay.amount?.toDouble()?.div(100.0)?.toDouble() ?: 1.0

        binding.amount.setText(amoun)
        binding.currency.setText("${sp.getString("currency", "EUR").toString()}")


        binding.type.setText(handpay.type)
        this.handpay = handpay
        binding.ticket.isEnabled = true
    }
}