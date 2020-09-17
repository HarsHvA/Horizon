package com.iceagestudios.horizon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.iceagestudios.horizon.FetchVideos.Constant;
import com.iceagestudios.horizon.FetchVideos.Method;
import com.iceagestudios.horizon.FetchVideos.StorageUtil;

import java.io.File;

public class Splash extends AppCompatActivity {
    public static int RequestPermission =1;
    private boolean permission = false;
    private File storage;
    private String[] storagePaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Permissions();
        if(permission)
        {
            FetchVideoFiles();
            new Handler().postDelayed(() -> {
                    final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
            },4000);

        }
    }

    public boolean FetchVideoFiles()
    {
        storagePaths = StorageUtil.getStorageDirectories(this);
        Constant.allMediaList.clear();
        Constant.allMediaFoldersList.clear();
        for (String path : storagePaths) {
            storage = new File(path);
            Method.load_Directory_Files(storage);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RequestPermission)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                MainActivity.permissionGranted = true;
                permission = true;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
            {
                MainActivity.permissionGranted = false;
                permission = false;
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
                new AlertDialog
                        .Builder(this,R.style.AlertDialogStyle).setTitle("Permission not Granted!")
                        .setMessage("We cannot list the videos if permission is not granted")
                        .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    public void Permissions()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this
                    ,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},RequestPermission);
            //}
        }
        else{
            MainActivity.permissionGranted = true;
            permission = true;
        }
    }
}
