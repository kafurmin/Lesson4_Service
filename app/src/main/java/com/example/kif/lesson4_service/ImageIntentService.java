package com.example.kif.lesson4_service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;

public class ImageIntentService extends IntentService {
    public static volatile boolean shouldContinue = true;

    private static final String ACTION_SET_IMAGE = "com.example.kif.lesson4_service.action.SET_IMAGE";
    private static final String ACTION_PRESENTATION = "com.example.kif.lesson4_service.action.PRESENTATION";
    private static final String ACTION_STOP = "com.example.kif.lesson4_service.action.STOP";

    // TODO: Rename parameters
    private static final String EXTRA_URI = "com.example.kif.lesson4_service.extra.URI";
    public static final String EXTRA_MYRECIVER = "com.example.kif.lesson4_service.extra.MYRECIVER";
    public static final String EXTRA_RECIVER_PRESENT = "com.example.kif.lesson4_service.extra.RECIVER_PRESENT";

    public static final String EXTRA_RECIVE_URI = "com.example.kif.lesson4_service.extra.MESSAGE";
    private static final String EXTRA_IMAGE_RESOURSE = "com.example.kif.lesson4_service.extra.IMAGE_RESOURSE" ;
    public static final String EXTRA_RECIVE_URI_PRES = "com.example.kif.lesson4_service.extra.RECIVE_URI_PRES";

    public ImageIntentService() {
        super("ImageIntentService");
    }

    public static void startActionSetImage(Context context, Uri uri, MyReceiver receiver) {
        Intent intent = new Intent(context, ImageIntentService.class);
        intent.setAction(ACTION_SET_IMAGE);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_MYRECIVER, receiver);
     //   intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
    public static void startActionPresentation(Context context, int[] image_resourse, MyReceiver receiver) {
        Intent intent = new Intent(context, ImageIntentService.class);
        intent.setAction(ACTION_PRESENTATION);
        intent.putExtra(EXTRA_IMAGE_RESOURSE, image_resourse);
        intent.putExtra(EXTRA_RECIVER_PRESENT, receiver);
        context.startService(intent);
    }
    public static void stopActionPresentation(Context context) {
        Intent intent = new Intent(context, ImageIntentService.class);
        intent.setAction(ACTION_PRESENTATION);

        context.stopService(intent);
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ResultReceiver receiver;
            final String action = intent.getAction();

            if (ACTION_SET_IMAGE.equals(action)) {
                receiver = intent.getParcelableExtra(EXTRA_MYRECIVER);
                Uri uri = intent.getParcelableExtra(EXTRA_URI);

                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_RECIVE_URI, uri);

                receiver.send(Activity.RESULT_OK, bundle);

            } else if (ACTION_PRESENTATION.equals(action)) {

                receiver = intent.getParcelableExtra(EXTRA_RECIVER_PRESENT);
                int[] image_resourse = intent.getIntArrayExtra(EXTRA_IMAGE_RESOURSE);
                Bundle bundle = new Bundle();
               // Uri uri=null;

                if (shouldContinue == false) {
                    stopSelf();
                    return;
                }

                while(shouldContinue == true){


                    for(int i=0; i<image_resourse.length;i++){
                       Uri uri = Uri.parse("android.resource://com.example.kif.lesson4_service/"+image_resourse[i]);

                        bundle.putParcelable(EXTRA_RECIVE_URI_PRES, uri);
                        receiver.send(Activity.RESULT_FIRST_USER, bundle);

                        if (shouldContinue == false) {
                            stopSelf();
                            return;
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }



            }
        }
    }

}
