package com.codecoy.ensicocasino.ui



import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.codecoy.ensicocasino.MyApp
import com.codecoy.ensicocasino.R
import com.codecoy.ensicocasino.databinding.ActivityMainBinding
import com.codecoy.ensicocasino.models.*
import com.codecoy.ensicocasino.repository.MainRepository
import com.codecoy.ensicocasino.retrofit.RetrofitAPI
import com.codecoy.ensicocasino.retrofit.RetrofitService
import com.codecoy.ensicocasino.viewmodels.MainViewModel
import com.codecoy.ensicocasino.viewmodels.MyViewModelFactory
import com.kaopiz.kprogresshud.KProgressHUD


class MainScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sp: SharedPreferences
    lateinit var viewModel: MainViewModel
    private lateinit var retrofitService: RetrofitAPI
    private lateinit var BASE_URL: String
    private lateinit var client: String
    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hudProgress()

        sp = getSharedPreferences("MyPref", MODE_PRIVATE)
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString()
        client = sp.getString("client", "ExampleMobileClient2").toString()



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
                    Log.d("TAG","API Error :" + it.message)
                    MyApp.showToast(this,"API Error :" + it.message )
                }
                is APIResponse.Success<*> -> {
                    if (hud.isShowing)
                        hud.dismiss()
                    //  Log.d(TAG, "onCreate   viewModel.movieList.observe: $it")
                    val response = it.data as TicketRedemptionResponse
                    when (response.responseCode) {
                        0 -> { // MyApp.showToast(this,"ticket is valid and can be redeemed." )
                            val intent1 = Intent(this, TicketRedemption::class.java)
                                intent1.run {
                                    putExtra("ticketNo",response.barcode)
                                    putExtra("ticketAmount",response.amount)
                                }
                            startActivity(intent1) }
                        1 -> { errorDialog("This ticket unknown.\n------ try other one ------") }
                        2 -> { errorDialog("This ticket already redeemed. \n------ try other one ------") }
                        3 -> errorDialog("This ticket has expired.\n------ try other one ------" )
                        4 -> errorDialog("This ticket is not valid. ${response.reason}\n------ try other one ------")
                        100 -> errorDialog("general CMS error. ${response.reason}\n------ try other one ------")
                        else -> {}

                    }
                }
            }
        })

        binding.ticket.setOnClickListener(View.OnClickListener {
            // Scan barcode now
            val intent = Intent(this, TicketScan::class.java)
            startActivityForResult(intent, 72)

        })

        binding.handpays.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HandPays::class.java)
            startActivity(intent)
        })

        binding.remoteTransfer.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RemoteTransferActivity::class.java)
            startActivity(intent)
        })

        binding.toolbar.btnLogout.setOnClickListener(View.OnClickListener {
           logout()
        })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 72) {
            if (data != null) {
            //    MyApp.showToast(this,"barcode :" + data.getStringExtra("barcode"))

                viewModel.ticketRedemption(
                    TicketRedemptionRequest(
                        System.currentTimeMillis().toString(),
                        clientName = client,
                        "ticketRedemption",
                        data.getStringExtra("barcode").toString()
                    )
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        binding.toolbar.btnMain.visibility = View.GONE
        super.onResume()
    }


    private fun logout(){
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
            .setWarningButtonClick {
                viewModel.logoutUser(
                    LoginRequest(
                        System.currentTimeMillis().toString(),
                        client,
                        "attendantLogout",
                        sp.getString("username", "").toString()
                    )
                )
            }
            .show()
    }

    private fun errorDialog(msg:String){
        AwesomeErrorDialog(this)
            .setTitle("Ticket Redemption Error!")
            .setMessage(msg)
            .setColoredCircle(com.awesomedialog.blennersilva.awesomedialoglibrary.R.color.dialogErrorBackgroundColor)
            .setDialogIconAndColor(com.awesomedialog.blennersilva.awesomedialoglibrary.R.drawable.ic_dialog_error, R.color.white)
            .setCancelable(true).setButtonText(getString(com.awesomedialog.blennersilva.awesomedialoglibrary.R.string.dialog_ok_button))
            .setButtonBackgroundColor(com.awesomedialog.blennersilva.awesomedialoglibrary.R.color.dialogErrorBackgroundColor)
            .setButtonText(getString(com.awesomedialog.blennersilva.awesomedialoglibrary.R.string.dialog_ok_button))
            .setErrorButtonClick{
               //
            }
            .show()
    }

}