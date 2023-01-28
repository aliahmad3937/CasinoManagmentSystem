package com.codecoy.ensicocasino.ui;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.codecoy.ensicocasino.MyApp;
import com.codecoy.ensicocasino.R;
import com.codecoy.ensicocasino.models.APIResponse;
import com.codecoy.ensicocasino.models.LoginRequest;
import com.codecoy.ensicocasino.models.LoginResponse;
import com.codecoy.ensicocasino.repository.MainRepository;
import com.codecoy.ensicocasino.retrofit.RetrofitAPI;
import com.codecoy.ensicocasino.retrofit.RetrofitService;
import com.codecoy.ensicocasino.viewmodels.MainViewModel;
import com.codecoy.ensicocasino.viewmodels.MyViewModelFactory;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;

public class TicketScan extends AppCompatActivity {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private Button mainScreen;
    private Button logou;
    private String barcodeData;

    private SharedPreferences sp;
    MainViewModel viewModel;
    RetrofitAPI retrofitService;
    private String  BASE_URL;
    private KProgressHUD hud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_scan);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        mainScreen = findViewById(R.id.btn_main);
        logou = findViewById(R.id.btn_logout);

        hudProgress();

        sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        BASE_URL = sp.getString("baseUrl", "https://93.103.81.142:51120/").toString();


        retrofitService = RetrofitService.getInstance().RetrofitServices(BASE_URL);
        viewModel = new ViewModelProvider(this, new MyViewModelFactory(new MainRepository(retrofitService))).get(MainViewModel.class);

        viewModel.getUser().observe(this, new Observer<APIResponse>() {
            @Override
            public void onChanged(APIResponse apiResponse) {
           if(apiResponse instanceof APIResponse.Loading){
               hud.show();
           }else if(apiResponse instanceof APIResponse.Error){
               if (hud.isShowing())
                   hud.dismiss();

               MyApp.showToast(TicketScan.this, ""+((APIResponse.Error) apiResponse).getMessage());
           }else if(apiResponse instanceof APIResponse.Success){
               if (hud.isShowing())
                   hud.dismiss();
               //  Log.d(TAG, "onCreate   viewModel.movieList.observe: $it")
               LoginResponse response = (LoginResponse) ((APIResponse.Success<?>) apiResponse).getData();
               switch (response.getResponseCode()) {
                   case 0:
                       MyApp.exitCasino(TicketScan.this);
                   break;
                   default:
                       break;
               }
           }

            }
        });


        mainScreen.setOnClickListener((v) -> {
                MyApp.mainScreen(this);
        });

        logou.setOnClickListener((v) -> {
          logout();
        });

        initialiseDetectorsAndSources();
    }


    Dialog settingDialog;
    @SuppressLint({"ResourceAsColor", "NewApi"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Case 1. Permission is granted.
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (ContextCompat.checkSelfPermission(TicketScan.this, Manifest.permission.CAMERA)
//                        == PackageManager.PERMISSION_GRANTED) {
//                    // Before navigating, I still check one more time the permission for good practice.
//
//
//
//                }
//            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(settingDialog != null && settingDialog.isShowing())
                    settingDialog.dismiss();
                recreate();
            }
            else { // Case 2. Permission was refused
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // Case 2.1. shouldShowRequest... returns true because the
                    // permission was denied before. If it is the first time the app is running we will
                    // end up in this part of the code. Because he need to deny at least once to get
                    // to onRequestPermissionsResult.
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.rellayout_container), R.string.you_must_verify_permissions_to_send_media, Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(R.color.white);
                    snackbar.setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(TicketScan.this
                                    , new String[]{Manifest.permission.CAMERA}
                                    , REQUEST_CAMERA_PERMISSION);
                        }
                    }).setActionTextColor(TicketScan.this.getColor(R.color.white));
                    snackbar.show();
                }
                else {
                    // Case 2.2. Permission was already denied and the user checked "Never ask again".
                    // Navigate user to settings if he choose to allow this time.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.instructions_to_turn_on_storage_permission)
                            .setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    settingsIntent.setData(uri);
                                    startActivityForResult(settingsIntent, 7);
                                }
                            })
                            .setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    settingDialog = builder.create();
                    settingDialog.show();
                }
            }
        }

    }


    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(TicketScan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(TicketScan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText("if"+barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(""+barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                Intent intent = new Intent();
                                intent.putExtra("barcode",barcodeData+"");
                                setResult(72,intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
//        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }

    private void logout(){
        new AwesomeWarningDialog(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.exit)
                .setColoredCircle(R.color.purple_500)
                .setDialogIconAndColor(R.drawable.exit, R.color.white)
                .setCancelable(true)
                .setButtonText("Exit")
                .setButtonTextColor(R.color.white)
                .setButtonBackgroundColor(R.color.purple_500)
                .setButtonText("Exit")
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        viewModel.logoutUser(
                               new LoginRequest(
                                        String.valueOf(System.currentTimeMillis()),
                                        "ExampleMobileClient2",
                                        "attendantLogout",
                                        sp.getString("username", "").toString()
                                )
                        );
                    }
                })
            .show();
    }

    private void hudProgress() {
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(settingDialog != null && settingDialog.isShowing())
            settingDialog.dismiss();
    }
}