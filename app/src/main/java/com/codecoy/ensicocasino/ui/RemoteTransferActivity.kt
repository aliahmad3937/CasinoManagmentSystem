package com.codecoy.ensicocasino.ui

import android.R.attr.editable
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.codecoy.ensicocasino.CurrencyTextWatcher
import com.codecoy.ensicocasino.DigitCardFormatWatcher
import com.codecoy.ensicocasino.MyApp
import com.codecoy.ensicocasino.MyApp.Companion.mainScreen
import com.codecoy.ensicocasino.R
import com.codecoy.ensicocasino.adapters.SpinnerAdapter
import com.codecoy.ensicocasino.databinding.ActivityRemoteTransferBinding
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.repository.MainRepository
import com.codecoy.ensicocasino.retrofit.RetrofitAPI
import com.codecoy.ensicocasino.retrofit.RetrofitService
import com.codecoy.ensicocasino.viewmodels.MainViewModel
import com.codecoy.ensicocasino.viewmodels.MyViewModelFactory
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.DecimalFormat
import java.text.NumberFormat


class RemoteTransferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoteTransferBinding
    lateinit var viewModel: MainViewModel
    private lateinit var retrofitService: RetrofitAPI
    private lateinit var hud: KProgressHUD
    private lateinit var BASE_URL: String
    private lateinit var client: String
    private lateinit var sp: SharedPreferences
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var selectedMachine: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoteTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hudProgress()

        sp = getSharedPreferences("MyPref", MODE_PRIVATE)
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()
        client = sp.getString("client", "ExampleMobileClient2").toString()


        binding.textView.text = "Amount (in ${sp.getString("currency", "EUR").toString()})"



       binding.etAmount.addTextChangedListener(DigitCardFormatWatcher(binding.etAmount))
     //   binding.etAmount.addTextChangedListener(CurrencyTextWatcher(binding.etAmount))


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

        viewModel.machineList.observe(this, Observer {
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
                    val response = it.data as MachineResponse

                    // we pass our item list and context to our Adapter.

                    // we pass our item list and context to our Adapter.
                    spinnerAdapter = SpinnerAdapter(this, response.machines)
                    binding.spinner.adapter = spinnerAdapter
//                    response.machines.forEach {
//                        MyApp.showToast(this,it)
//                    }

                }
            }
        })

        viewModel.cashIn.observe(this, Observer {
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
                    val response = it.data as CashInRequest
                    when (response.responseCode) {
                        0 -> {
                            MyApp.showToast(this,"succesful transfer")
                        }
                        1 -> {
                            MyApp.showToast(this,"unsuccesful transfer reason: ${response.reason}")
                        }
                        2 -> {
                            MyApp.showToast(this,"error: ${response.reason}")
                        }
                            else -> println("I don't know anything about it")
                    }
                }
            }
        })

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                // It returns the clicked item.
                selectedMachine = parent.getItemAtPosition(position) as String

                //  Toast.makeText(MainActivity.this, name + " selected", Toast.LENGTH_SHORT).show();
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }




        binding.layoutCash.setOnClickListener(View.OnClickListener {

            if (binding.radioCashIn.isChecked) {
                // cash in
                val amount: String = binding.etAmount.text.toString()
                if (amount.isEmpty()) {
                   // MyApp.showToast(this, "Please Enter Amount")
                    binding.etAmount.error = "enter amount!"
                    return@OnClickListener
                }
                val cleanString = amount.replace(",", "").toDouble()
                viewModel.cashIn(
                    CashInRequest(
                        date = System.currentTimeMillis().toString(),
                        clientName = client,
                        type = "remoteCashIn",
                        machine = selectedMachine,
                        amount = (cleanString * 100.0).toInt()
                    )
                )

            } else {

                // cash out
                viewModel.cashIn(
                    CashInRequest(
                        date = System.currentTimeMillis().toString(),
                        clientName = client,
                        type = "remoteCashOut",
                        machine = selectedMachine
                    )
                )
            }
        })


        binding.toolbar.btnMain.setOnClickListener(View.OnClickListener {
            mainScreen(this)
            finishAffinity()
        })

        binding.toolbar.btnLogout.setOnClickListener(View.OnClickListener {
            logout()
        })

        viewModel.machineList(
            MachineRequest(
                System.currentTimeMillis().toString(),
                client,
                "machineList",
                ""
            )
        )
        // Get radio group selected item using on checked change listener
        binding.radioOperation.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)

                binding.tvCash.text = radio.text

                if (radio == binding.radioCashIn) {
                    binding.layoutPayment.visibility = View.VISIBLE
                } else {
                    binding.layoutPayment.visibility = View.GONE
                }

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
                            "ExampleMobileClient2",
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