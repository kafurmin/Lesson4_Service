package com.example.kif.lesson4_service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import static com.example.kif.lesson4_service.ImageIntentService.EXTRA_RECIVE_URI;
import static com.example.kif.lesson4_service.ImageIntentService.EXTRA_RECIVE_URI_PRES;

public class MainActivity extends AppCompatActivity {

    private static final java.lang.String URI_KEY = "com.example.kif.lesson4_service.URI_KEY";
    public static int[] image_resourse = {R.drawable.numbers_1,R.drawable.number_2,R.drawable.number_3,R.drawable.number_4,R.drawable.number_5};
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //TODO Edit: You should call setReceiver(this) in onResume and setReceiver(null) in onPause() to avoid leaks.
    private DialogFragment dfragment_image;
    private ViewPager mViewPager;
    public ImageView imageView;
    public Uri uri_for_save;
    public MyReceiver receiver;
    private Boolean flagStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        receiver = new MyReceiver(new Handler());
        final ImageView imageViewPres= (ImageView) findViewById(R.id.image_view_Fragment);
        //STOP
        final FloatingActionButton fabstop = (FloatingActionButton) findViewById(R.id.fabstop);
        fabstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageIntentService.shouldContinue = false;


                fabstop.setVisibility(View.INVISIBLE);


            }
        });
        //START
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

                Iterator<ActivityManager.RunningAppProcessInfo> iter = runningAppProcesses.iterator();

                while(iter.hasNext()){
                    ActivityManager.RunningAppProcessInfo next = iter.next();

                    String pricessName = getPackageName() + ":service";

                    if(next.processName.equals(pricessName)){
                        android.os.Process.killProcess(next.pid);
                        break;
                    }
                }

                ImageIntentService.startActionPresentation(MainActivity.this, image_resourse, receiver);
                fabstop.setVisibility(View.VISIBLE);


                receiver.setReceiver(new MyReceiver.Receiver() {
                    @Override
                    public void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == RESULT_FIRST_USER) {
                            Uri uri = resultData.getParcelable(EXTRA_RECIVE_URI_PRES);
                            dfragment_image = BlankFragment.newInstance(uri);
                            dfragment_image.show(getSupportFragmentManager(), "dfr_image");

                            if (dfragment_image.isHidden()){
                                fabstop.setVisibility(View.INVISIBLE);
                                ImageIntentService.shouldContinue = false;

                                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

                                Iterator<ActivityManager.RunningAppProcessInfo> iter = runningAppProcesses.iterator();

                                while(iter.hasNext()){
                                    ActivityManager.RunningAppProcessInfo next = iter.next();

                                    String pricessName = getPackageName() + ":service";

                                    if(next.processName.equals(pricessName)){
                                        android.os.Process.killProcess(next.pid);
                                        break;
                                    }
                                }

                            }

                        }
                    }
                });




            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*

    @Override
    protected void onResume() {
        super.onResume();
        receiver.setReceiver((MyReceiver.Receiver) receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.setReceiver(null);
    }
*/


    public class SectionsPagerAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        public SectionsPagerAdapter(Context context) {

            this.context = context;
        }


        @Override
        public int getCount() {

            return image_resourse.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == (LinearLayout)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View item_view = layoutInflater.inflate(R.layout.swipe_layout,container,false);
            imageView = (ImageView) item_view.findViewById(R.id.imageview);
            TextView textView = (TextView) item_view.findViewById(R.id.image_count);


            Uri imageURI = Uri.parse("android.resource://com.example.kif.lesson4_service/"+image_resourse[position]);

            ImageIntentService.startActionSetImage(context, imageURI, receiver);
            //Intent intent = new Intent(context, ImageIntentService.class);
            //intent.putExtra(EXTRA_RECIVER, receiver);

            receiver.setReceiver(new MyReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == RESULT_OK) {
                        Uri uri = resultData.getParcelable(EXTRA_RECIVE_URI);
                        imageView.setImageURI(uri);
                        uri_for_save = uri;
                    }
                }
            });
            textView.setText("Image : " +(position+1));

           // imageView.setImageURI(imageURI);

            //imageView.setImageURI(receiver.onReceiveResult(););

            container.addView(item_view);
            return item_view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }

    }
/*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        imageView.setImageURI(Uri.parse(savedInstanceState.getString(URI_KEY)));
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putString(URI_KEY, uri_for_save.toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(out);
    }*/
}
