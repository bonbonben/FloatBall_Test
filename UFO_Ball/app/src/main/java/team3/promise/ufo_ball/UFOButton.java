package team3.promise.ufo_ball;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by plaslab on 2015/10/24.
 */
public class UFOButton extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;
    private long startTime,stopTime;
    boolean iscase1enable=false;
    String category_text = "";
    int PrevX;
    int PrevY;
    boolean ismoving;

    HashMap<String,String> item  = new HashMap<>();
    List<Map<String,String>> items = new ArrayList<>();
    List listCity = new ArrayList();;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        int get = intent.getIntExtra("caceled", 1);
        Log.d("onBind",get+"");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listCity.add("start");
        listCity.add("stop");
        listCity.add("photo");

//        item.put("text", "Start");
//        listCity.add(item);
//        item.put("text", "Stop");
//        listCity.add(item);
//        item.put("text","photo");
//        listCity.add(item);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageButton(this);
        chatHead.setImageResource(R.drawable.ic_camera_enhance_white_48dp);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);


        try {

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator animator1 = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1.0f);
                ValueAnimator animator2 = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1.0f);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1, animator2);
                animatorSet.setDuration(250);
                animatorSet.setInterpolator(new OvershootInterpolator());
                //animatorSet.setInterpolator(new BounceInterpolator());
                animatorSet.start();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        ismoving = false;

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        PrevX = initialX;
                        PrevY = initialY;
                        return true;
                    case MotionEvent.ACTION_UP:
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);

                        if (Math.abs(PrevX - params.x)<=10 && Math.abs(PrevY - params.y) <= 10)
                            initiatePopupWindow(chatHead);
                        Log.d("TAG","x: "+params.x+"y: "+params.y+"Prevx: "+PrevX+"Prevy: "+PrevY);
                        PrevX = initialX;
                        PrevY = initialY;
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);

                            windowManager.updateViewLayout(chatHead, params);


                            ismoving = true;
                            Log.d("TAG","x: "+params.x+"y: "+params.y);
                        return true;

                }
                return false;
            }
        });

        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    private void initiatePopupWindow(View anchor) {
        try {

//            SimpleAdapter simpleAdapter;

            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            ListPopupWindow popup = new ListPopupWindow(this);
            popup.setAnchorView(anchor);

//            popup.setWidth((int) (display.getWidth() / (1.5)));
            popup.setVerticalOffset(200);
            //ArrayAdapter<String> arrayAdapter =
            //new ArrayAdapter<String>(this,R.layout.list_item, myArray);


            popup.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    listCity));
//            simpleAdapter = new SimpleAdapter(this,
//                    items, R.layout.drop_item, new String[]{"text"},
//                    new int[]{R.id.text1});
//
//            popup.setAdapter(simpleAdapter);
                    popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View view, int position, long id3) {
                            ValueAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f);
                            ValueAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(animator1, animator2);
                            animatorSet.setDuration(250);
                            animatorSet.setInterpolator(new OvershootInterpolator());
                            //animatorSet.setInterpolator(new BounceInterpolator());
                            animatorSet.start();

                            Toast.makeText(UFOButton.this, "" + position + ": " + listCity.get(position), Toast.LENGTH_SHORT).show();


                            switch (position) {
                                case 0:
                                    //start
                                    final int notifyID = 1;

                                    final Intent intent = new Intent().setClass(getApplicationContext(), UFOButton.class);
                                    int flags = PendingIntent.FLAG_CANCEL_CURRENT;
                                    final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flags); // ??PendingIntent

                                    final Intent cancelIntent = new Intent(getApplicationContext(), CancelNotificationReceiver.class); // ??????Intent
                                    cancelIntent.putExtra("cancel_notify_id", notifyID);
                                    flags = PendingIntent.FLAG_ONE_SHOT;
                                    final PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, flags); // ??PendingIntent

                                    final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // ?????????
                                    final Notification notification = new Notification.Builder(getApplicationContext()).
                                            setSmallIcon(R.drawable.ic_camera_enhance_white_48dp).
                                            setContentTitle("照相分類中").
                                            setContentText("選擇是否繼續").
                                            addAction(R.drawable.
                                                            ic_camera_enhance_white_48dp,
                                                    "繼續分類",
                                                    pendingIntent).
                                            addAction(android.R.drawable.ic_menu_close_clear_cancel,
                                                    "停止分類",
                                                    pendingCancelIntent).
                                            build();
                                    notification.flags = Notification.FLAG_NO_CLEAR;
                                    notificationManager.notify(notifyID, notification);

                                    startTime = System.currentTimeMillis();
                                    iscase1enable=true;
                                    break;

                                case 1:
                                    if(!iscase1enable)
                                        Toast.makeText(UFOButton.this, "", Toast.LENGTH_SHORT).show();
                                    else
                                        stop();
//                                    Toast.makeText(UFOButton.this, "total time:" + (stopTime - startTime), Toast.LENGTH_SHORT).show();
                                    break;

                                case 2:

                                    try {
//                                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        PackageManager manager = getApplicationContext().getPackageManager();
                                        Intent i = manager.getLaunchIntentForPackage("com.asus.camera");
                                        startActivity(i);


                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }

                                    break;
                            }

                        }
                    });
            if (popup.isShowing())
                popup.dismiss();
            else
                popup.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long[] stop(){

        stopTime = System.currentTimeMillis();
        iscase1enable = false;
        Toast.makeText(UFOButton.this, "2total time:" + (stopTime - startTime), Toast.LENGTH_SHORT).show();

        final EditText editText = new EditText(UFOButton.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        editText.setLayoutParams(lp);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        AlertDialog.Builder category = new AlertDialog.Builder(this);
        category.setView(editText);
        category.setTitle("test").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                category_text = editText.getText().toString();
            }
        }).create();
        AlertDialog alert = category.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//設定提示框為系統提示框
        alert.show();

        Intent intent = new Intent(this, ExplorerActicity.class);
        long[] ret = {this.startTime,this.stopTime};
        intent.putExtra("time",ret);
        intent.putExtra("name",category_text);

        startActivity(intent);

            return new long[]{this.startTime,this.stopTime};
        }
//    public void showDialog(int title,String message){
//        Log.i("service","show dialog function");
//        TextView errmsg = (TextView) layout.findViewById(R.id.errmsg);
//        Log.i("service", "dialog error msg:"+message);
//        errmsg.setText(Html.fromHtml(message));
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//設定提示框為系統提示框
//        alert.show();
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null)
            windowManager.removeView(chatHead);
    }
//    @Override
//    public void onReceive(final Context context, final Intent intent) {
//        final int notifyID = intent.getIntExtra("cancel_notify_id", 0);
//        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
//        notificationManager.cancel(notifyID);
//    }



}
