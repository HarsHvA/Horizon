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

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iceagestudios.horizon.FetchVideos.Constant;
import com.iceagestudios.horizon.FetchVideos.Method;
import com.iceagestudios.horizon.FetchVideos.StorageUtil;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity" ;
    private TabLayout tabLayout;
    private File storage;
    private String[] storagePaths;
    private ImageButton btn;
    public static boolean permissionGranted;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rate_btn:
                RateApp();
                break;

            case R.id.share_btn:
                ShareApp();
                break;

            case R.id.license_btn:
                final LibsBuilder libs =  new LibsBuilder();
                libs.withActivityStyle(Libs.ActivityStyle.LIGHT)
                        .withFields(R.string.class.getFields())
                        .withLibraries("Glide","Constraint Layout","Exoplayer")
                        .start(MainActivity.this);
                Toast.makeText(MainActivity.this, "Licenses", Toast.LENGTH_SHORT).show();
                break;

            case R.id.http_btn:
                HttpFunction();
                break;

            case R.id.policy_btn:
                ShowPolicyDialog();
                break;

        }
    }

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
                    return new FoldersFrag();

                case 1:
                    return new VideosFrag();

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
                    return "Home";

                case 1:
                    return "All Videos";

                default:
                    return null;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(permissionGranted)
        {
            FetchVideoFiles();
        }
        LinearLayout linearLayout = findViewById(R.id.background_main_activity);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

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
                    btn.setVisibility(View.INVISIBLE);
                }else
                {
                    btn.setVisibility(View.VISIBLE);
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

    public void InfoFunction(View view)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        bottomSheetDialog.show();
        RelativeLayout relativeLayout = bottomSheetDialog.findViewById(R.id.bottom_sheet_rel_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        ImageButton http_btn = bottomSheetDialog.findViewById(R.id.http_btn);
        ImageButton privacy_btn = bottomSheetDialog.findViewById(R.id.policy_btn);
        ImageButton rate_btn = bottomSheetDialog.findViewById(R.id.rate_btn);
        ImageButton share_btn = bottomSheetDialog.findViewById(R.id.share_btn);
        ImageButton license_btn = bottomSheetDialog.findViewById(R.id.license_btn);
        http_btn.setOnClickListener(this);
        privacy_btn.setOnClickListener(this);
        rate_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        license_btn.setOnClickListener(this);
    }

    public void FavoriteFunction(View view)
    {
        Intent intent = new Intent(this,FavouriteActivity.class);
        startActivity(intent);
    }
    public void HttpFunction()
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
        Button cancel = dialog.findViewById(R.id.cancel_online_sub);
        cancel.setOnClickListener(view -> dialog.dismiss());
        Button add = dialog.findViewById(R.id.add_online_sub);
        add.setText("Play");
        add.setOnClickListener(view -> {
            String url = onlineEditText.getText().toString();
                Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
                intent.putExtra("VideoPath", url);
                intent.putExtra("VideoName", "Stream");
                startActivity(intent);
            dialog.dismiss();
        });

    }
    private void FetchVideoFiles()
    {
        storagePaths = StorageUtil.getStorageDirectories(this);
        Constant.allMediaList.clear();
        Constant.allMediaFoldersList.clear();
        for (String path : storagePaths) {
            storage = new File(path);
            Method.load_Directory_Files(storage);
        }
    }

    private void ShowPolicyDialog(){
        Dialog dialog  = new Dialog(this);
        dialog.setContentView(R.layout.policy_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        TextView textView = dialog.findViewById(R.id.policyTextView);
        textView.setText(PrivacyPolicy());
        dialog.show();
    }

    private void RateApp()
    {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.iceagestudios.horizon")));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ShareApp()
    {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Horizon");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private String PrivacyPolicy()
    {
        return
                "Privacy Policy\n" +
                        "\n" +
                        "IceAge developers built the Horizon app as an Ad Supported app. This SERVICE is provided by at no cost and is intended for use as is.\n" +
                        "\n" +
                        "This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.\n" +
                        "\n" +
                        "If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                        "\n" +
                        "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Horizon unless otherwise defined in this Privacy Policy.\n" +
                        "\n" +
                        "Information Collection and Use\n" +
                        "\n" +
                        "For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information. The information that I request will be retained on your device and is not collected by me in any way.\n" +
                        "\n" +
                        "The app does use third party services that may collect information used to identify you.\n" +
                        "\n" +
                        "Link to privacy policy of third party service providers used by the app\n" +
                        "\n" +
                        "    Google Play Services\n" +
                        "    AdMob\n" +
                        "    Firebase Analytics\n" +
                        "    Firebase Crashlytics\n" +
                        "\n" +
                        "Log Data\n" +
                        "\n" +
                        "I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.\n" +
                        "\n" +
                        "Cookies\n" +
                        "\n" +
                        "Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.\n" +
                        "\n" +
                        "This Service does not use these “cookies” explicitly. However, the app may use third party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                        "\n" +
                        "Service Providers\n" +
                        "\n" +
                        "I may employ third-party companies and individuals due to the following reasons:\n" +
                        "\n" +
                        "    To facilitate our Service;\n" +
                        "    To provide the Service on our behalf;\n" +
                        "    To perform Service-related services; or\n" +
                        "    To assist us in analyzing how our Service is used.\n" +
                        "\n" +
                        "I want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                        "\n" +
                        "Security\n" +
                        "\n" +
                        "I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.\n" +
                        "\n" +
                        "Links to Other Sites\n" +
                        "\n" +
                        "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                        "\n" +
                        "Children’s Privacy\n" +
                        "\n" +
                        "These Services do not address anyone under the age of 13. I do not knowingly collect personally identifiable information from children under 13. In the case I discover that a child under 13 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do necessary actions.\n" +
                        "\n" +
                        "Changes to This Privacy Policy\n" +
                        "\n" +
                        "I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page. These changes are effective immediately after they are posted on this page.\n" +
                        "\n" +
                        "Contact Us\n" +
                        "\n" +
                        "If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me at iceagestud@gmail.com.\n" +
                        "\n" ;
    }
}
