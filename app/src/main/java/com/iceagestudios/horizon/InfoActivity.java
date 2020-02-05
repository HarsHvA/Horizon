package com.iceagestudios.horizon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class InfoActivity extends AppCompatActivity {
private ListView listView;
private ArrayList<String> arrayList;
private ArrayAdapter<String> adapter;
private FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        listView = findViewById(R.id.infoList);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
        PopulateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i)
                {
                    case 0:
                        Toast.makeText(InfoActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("RemoveAds","1");
                        firebaseAnalytics.logEvent("Buy_Premium",bundle);
                        break;

                    case 1:
                        ShowPolicyDialog();
                        //Toast.makeText(InfoActivity.this, "PrivacyPolicy", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        final LibsBuilder libs =  new LibsBuilder();
                        libs.withActivityStyle(Libs.ActivityStyle.LIGHT)
                                .withFields(R.string.class.getFields())
                                .withLibraries("Glide","Constraint Layout","Exoplayer")
                                .start(InfoActivity.this);
                        Toast.makeText(InfoActivity.this, "Licenses", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        ShareApp();
                        //Toast.makeText(InfoActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        break;

                    case 4:
                        RateApp();
                        //Toast.makeText(InfoActivity.this, "Rate", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void PopulateList()
    {
        arrayList.add("Remove Ads");
        arrayList.add("Privacy Policy");
        arrayList.add("Licenses");
        arrayList.add("Share App");
        arrayList.add("Rate App");
        adapter.notifyDataSetChanged();
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

    private void ShowPolicyDialog(){
        Dialog dialog  = new Dialog(this);
        dialog.setContentView(R.layout.policy_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        TextView textView = dialog.findViewById(R.id.policyTextView);
        textView.setText(PrivacyPolicy());
        dialog.show();
    }

    // "This privacy policy page was created at privacypolicytemplate.net and modified/generated by App Privacy Policy Generator\n"
}
