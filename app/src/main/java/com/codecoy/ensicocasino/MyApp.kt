package com.codecoy.ensicocasino

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure
import com.codecoy.ensicocasino.ui.LoginScreen
import com.codecoy.ensicocasino.ui.MainScreen

class MyApp : Application() {

    companion object {
        @JvmStatic
       fun logout(context: Context){
           AwesomeWarningDialog(context)
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
                       exitCasino(context)

                   }
               })
               .show()
       }

        @JvmStatic
        fun exitCasino(context: Context) {
            val sp = context.getSharedPreferences("MyPref", MODE_PRIVATE)
            sp.edit().putString("username","").apply()
            Toast.makeText(context, "logout successfully!", Toast.LENGTH_LONG).show()
            val intent = Intent(context, LoginScreen::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        @JvmStatic
        fun mainScreen(context: Context){
            val intent = Intent(context, MainScreen::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun showToast(context: Context,message:String){
            Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show()
        }



    }

    override fun onCreate() {
        super.onCreate()

    }

}