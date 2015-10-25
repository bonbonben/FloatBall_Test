package com.example.skyle.promise_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Skyle on 2015/10/24.
 */

public class ExplorerActivity extends AppCompatActivity {

    public class Item {
        public String name;
        public String path;
        public boolean is_dir;
    }

    final static String MY_ACTION = "testActivity.MY_ACTION";

    private ArrayList<Item> Items = new ArrayList<>();
    ListViewAdapter myAdapter;
    ListView listViewExplorer;
    String token;
    MyReceiver myReceiver;

    public Date String2Date(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(s);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    List<File> files;

    private List<File> getListFiles(File parentDir, Date start, Date end) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file, start, end));
            } else {
                Date d = new Date(file.lastModified());
                if (file.getName().endsWith(".jpg") && d.after(start) && d.before(end)) {
                    inFiles.add(file);
                    //Log.i("Tag", file.getPath());
                    //Log.i("Tag", d.toString());
                    //Log.i("Tag", start.toString());
                }
            }
        }
        return inFiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listViewExplorer = (ListView) findViewById(R.id.listViewExplorer);
        token = this.getIntent().getStringExtra("Token");



        /*
        final String dir = "/test/";
        files = getListFiles(new File("/sdcard/DCIM/"), String2Date("2015-10-20 15:25:00"), String2Date("2015-10-29 00:00:00"));
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i = 0; i < files.size(); i++){
                    Promise p = new Promise(dir + files.get(i).getName(), token);
                    p.Request(p.File2byteArray(files.get(i).getPath()));
                    //Log.i("tag", "" + p.getResponseCode());
                    final int notifyID = 1; // 通知的識別號碼

                    final int progressMax = 100; // 進度條的最大值，通常都是設為100。若是設為0，且indeterminate為false的話，表示不使用進度條
                    final int progress = (i+1)/files.size()*100; // 進度值
                    final boolean indeterminate = false; // 是否為不確定的進度，如果不確定的話，進度條將不會明確顯示目前的進度。若是設為false，且progressMax為0的話，表示不使用進度條

                    final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
                    final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("內容標題").setContentText("內容文字").setProgress(progressMax, progress, indeterminate).build(); // 建立通知
                    notificationManager.notify(notifyID, notification);
                    Log.i("tag", "" + p.Response());
                    p.Disconnect();
                }
            }
        }.start();
        */

        myAdapter = new ListViewAdapter();
        listViewExplorer.setAdapter(myAdapter);
        LoadData("/");
    }

    @Override
    protected void onStart() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        Intent intent = new Intent(this, UFOButton.class);
        startService(intent);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            long[] time = arg1.getExtras().getLongArray("time");
            String name = arg1.getStringExtra("name");
            //long timestamp = arg1.getLongExtra("timestamp", 0);
            //long curtime = System.currentTimeMillis();
            //long delay = curtime - timestamp;
            Toast.makeText(ExplorerActivity.this, "ya\n" + new Date(time[0]).toString() + "\n" + new Date(time[1]).toString() + "\n" + name, Toast.LENGTH_SHORT).show();
            /*Log.i("aaa", String.valueOf(timestamp)
                    + " : " + String.valueOf(curtime)
                    + " delay " + String.valueOf(delay)
                    + "(ms)");*/
        }

    }

    public void LoadData(final String path) {
        Items.clear();
        Log.i("tag", path);
        String s = path.replaceAll("\\/", "/");
        if (s == "") s = "/";
        if (s.equals("/") == false) {
            Item it = new Item();
            it.is_dir = true;
            it.name = "..";
            it.path = s.substring(0, s.lastIndexOf("/"));
            Items.add(it);
            //Log.i("tag", it.path);
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                Promise p = new Promise(path, token, Promise.URL_METADATA);
                try {
                    JSONObject json = (new JSON(p.Response())).json;
                    Log.i("tag", json.toString());
                    JSONArray array = json.getJSONArray("contents");
                    //Toast.makeText(this, "" + array.length(), Toast.LENGTH_SHORT).show();
                    p.Disconnect();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json1 = array.getJSONObject(i);
                        Item it = new Item();
                        it.path = json1.getString("path");
                        it.name = it.path.substring(it.path.lastIndexOf("/") + 1);
                        it.is_dir = json1.getBoolean("is_dir");
                        Items.add(it);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(Items, new Comparator<Item>() {
                    @Override
                    public int compare(Item lhs, Item rhs) {
                        if (lhs.is_dir == rhs.is_dir)
                            return lhs.name.compareTo(rhs.name);
                        else
                            return lhs.is_dir ? -1 : 1;
                    }
                });

                runOnUiThread(new Runnable() {
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                        listViewExplorer.invalidateViews();
                        listViewExplorer.refreshDrawableState();
                    }
                });
            }
        }.start();
        //Log.i("tag", "" + p.getResponseCode());
        //p.DownloadFile("/sdcard/json.txt");
        //p.Disconnect();


    }

    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout =
                    (LinearLayout) ExplorerActivity.this.getLayoutInflater().inflate(R.layout.browser_item, null);
            ImageView imageViewItemType = (ImageView) linearLayout.findViewById(R.id.imageViewItemType);
            TextView textViewItemName = (TextView) linearLayout.findViewById(R.id.textViewItemName);
            if (Items.get(position).is_dir) { //TODO: set item image
                imageViewItemType.setImageResource(R.drawable.icon_folder);
            } else {
                imageViewItemType.setImageResource(R.drawable.icon_image);
            }
            textViewItemName.setText(Items.get(position).name);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Items.get(position).is_dir) {
                        LoadData(Items.get(position).path);
                    } else {
                        Intent it = new Intent(ExplorerActivity.this, BrowserActivity.class);
                        it.putExtra("Token", token);
                        it.putExtra("Path", Items.get(position).path);
                        ExplorerActivity.this.startActivity(it);
                    }
                }
            });
            return linearLayout;
        }


    }
}
