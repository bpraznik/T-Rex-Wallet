package crypto.wallet.bittrex;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ActivityChart extends AppCompatActivity {
    Bundle extras;
    String currencyName;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        extras = getIntent().getExtras();

        try {
            if(extras !=null)
            {
                currencyName = extras.getString("Name");
                this.setTitle(currencyName);
            } else {
                System.out.println("Nič ni v extras!");
            }
        }catch (Exception ex){
            Log.d("ERROR" , ex.toString());
        }

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ChartFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ChartFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChartFragment newInstance(int sectionNumber, String currencyName) {
            ChartFragment fragment = new ChartFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("Name", currencyName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_chart, container, false);

            WebView tradeView = rootView.findViewById(R.id.chartView);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1)
                loadchart(tradeView, "30", getArguments().getString("Name"));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2)
                loadchart(tradeView, "D", getArguments().getString("Name"));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 3)
                loadchart(tradeView, "W", getArguments().getString("Name"));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ChartFragment (defined as a static inner class below).
            return ChartFragment.newInstance(position + 1, currencyName);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public static void loadchart(WebView tradeView, String interval, String currencyName){
        String tradeString = "<!-- TradingView Widget BEGIN -->\n" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "new TradingView.widget({\n" +
                "  \"autosize\": true,\n" +
                "  \"symbol\": \"" + currencyName+"\",\n" +
                "  \"interval\": \"" + interval + "\",\n" +
                "  \"timezone\": \"Etc/UTC\",\n" +
                "  \"theme\": \"Light\",\n" +
                "  \"style\": \"1\",\n" +
                "  \"locale\": \"en\",\n" +
                "  \"toolbar_bg\": \"#f1f3f6\",\n" +
                "  \"enable_publishing\": false,\n" +
                "  \"hide_top_toolbar\": true,\n" +
                "  \"save_image\": false,\n" +
                "  \"hideideas\": true\n" +
                "});\n" +
                "</script>\n" +
                "<!-- TradingView Widget END --> ";
        tradeView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        WebSettings webSettings = tradeView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tradeView.loadData(tradeString, "text/html", "utf-8");
    }
}
