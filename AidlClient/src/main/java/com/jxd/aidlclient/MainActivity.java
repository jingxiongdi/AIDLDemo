package com.jxd.aidlclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jxd.aidldemo.IAidlService;
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

    private ServiceConnection conn = new ServiceConnection() {

        //绑定服务，回调onBind()方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            iAidlService = IAidlService.Stub.asInterface(service);
            try {
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
        unbindService(conn);
    }
}
