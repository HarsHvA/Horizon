package com.iceagestudios.horizon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ImageButton btn;
    public static int RequestPermission =1;
    public static ArrayList<String> favoriteArrayList = new ArrayList<>();
    public static boolean permissionGranted;
    private FirebaseAnalytics firebaseAnalytics;

    public static class PagerAdapter extends FragmentPagerAdapter
    {

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new VideosFrag();

                case 1:
                    return new FoldersFrag();

                default: return null;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0:
                    return "All videos";

                case 1:
                    return "Folders";

                default:
                    return null;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Permissions();
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(),1));
        btn = findViewById(R.id.search_btn);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(viewPager.getCurrentItem() == 0)
                {
                    btn.setVisibility(View.VISIBLE);
                }else
                {
                    btn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void Permissions()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this
                    ,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},RequestPermission);
            //}
        }
        else{
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RequestPermission)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                permissionGranted = true;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
            {
                permissionGranted = false;
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

    public void FavoriteFunction(View view)
    {
        Intent intent = new Intent(this,FavouriteActivity.class);
        startActivity(intent);
    }

    public void InfoFunction(View view)
    {
        Intent intent = new Intent(this,InfoActivity.class);
        startActivity(intent);

    }
    public void HttpFunction(View view)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.online_dialog);
        Objects.requireNonNull(dialog.getWindow()).
                setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        TextView onlineTitle = dialog.findViewById(R.id.online_title);
        onlineTitle.setText("Enter network address");
        dialog.show();
        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        final EditText onlineEditText = dialog.findViewById(R.id.online_sub_url);
        onlineEditText.requestFocus();
        onlineEditText.setShowSoftInputOnFocus(true);
        imm.showSoftInput(onlineEditText, InputMethodManager.SHOW_FORCED);
        final String url = onlineEditText.getText().toString();
        Button cancel = dialog.findViewById(R.id.cancel_online_sub);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button add = dialog.findViewById(R.id.add_online_sub);
        add.setText("Play");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
                    intent.putExtra("VideoPath", url);
                    intent.putExtra("VideoName", "Stream");
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "Invalid url!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

}
