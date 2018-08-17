package com.jxd.aidlclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;

import com.jxd.aidldemo.IAidlService;
import com.jxd.aidldemo.IServerSendDataToClient;
import com.jxd.aidldemo.Person;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private IAidlService iAidlService = null;
    private static final String TAG = "jxdaidldemo";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate");
        Intent intent1 = new Intent();
        intent1.setAction("com.jxd.aidlserver.IRemouteService");
        intent1.setPackage("com.jxd.aidldemo");
        bindService(intent1,conn,BIND_AUTO_CREATE);
    }

    IServerSendDataToClient serverSendDataToClient = new IServerSendDataToClient.Stub() {

        @Override
        public void sendData(String ccc) throws RemoteException {
            Log.d(TAG,"ccc : "+ccc);
        }

        @Override
        public void sendByteArray(byte[] data) throws RemoteException {
            Log.d(TAG,"getbyte[] success length : "+data.length);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = (ImageView) findViewById(R.id.picture);
                    imageView.setImageBitmap(bitmap);
                }
            });

        }
    };

    private ServiceConnection conn = new ServiceConnection() {

        //绑定服务，回调onBind()方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            iAidlService = IAidlService.Stub.asInterface(service);

            try {
                iAidlService.registListener(serverSendDataToClient);
                iAidlService.basicTypes(1,20000000,true,32.1f,4.12345,"basicTypes ok");
                Person p = iAidlService.getPerson();
                p = new Person(1,"bingchao",23,"male");
                iAidlService.OnNewPersonAdd(p);
                p = iAidlService.getPerson();
                Log.d(TAG,"onServiceConnected "+ p.getName()+" "+ p.getAge());

                List<Person> personList = new ArrayList<>();
                personList = iAidlService.getPersonList();
                for(Person pp :personList){
                    Log.d(TAG,"getPersonList "+pp.getName()+" "+pp.getAge());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
            iAidlService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if (conn != null && serverSendDataToClient.asBinder().isBinderAlive()) {
            try {
                iAidlService.unregistListener(serverSendDataToClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(conn);
        }

    }
}
