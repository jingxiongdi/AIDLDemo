package com.jxd.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class IRemouteService extends Service {
    private Person person = new Person(1,"jxd",25,"male");
    private static final String TAG = "jxdaidldemo";
    private List<Person> personList = new ArrayList<>();
    private RemoteCallbackList<IServerSendDataToClient> mListener = new RemoteCallbackList<>();
    private Thread myThread = null;
    public IRemouteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    private IBinder iBinder = new IAidlService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            personList.add(person);
            Log.d(TAG,"接收到基本类型: int "+anInt+" long "+aLong+" boolean "+aBoolean+" float "+aFloat+" double "+aDouble+" string "+aString);
        }

        @Override
        public void OnNewPersonAdd(Person p) throws RemoteException {
            person = p;
            personList.add(p);
            Log.d(TAG,"OnNewPersonAdd "+person.getName()+" "+person.getAge());
        }

        @Override
        public Person getPerson() throws RemoteException {
            Log.d(TAG,"getPerson "+person.getName()+" "+person.getAge());
            return person;
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return personList;
        }

        @Override
        public void registListener(IServerSendDataToClient listener) throws RemoteException {
            mListener.register(listener);
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int n = mListener.beginBroadcast();
                    for (int i = 0; i < n; i++) {
                        IServerSendDataToClient listener = mListener.getBroadcastItem(i);
                        if (listener != null) {
                            try {
                                listener.sendData("data : "+i+" n = "+n);
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.testimghighq);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                               //测试aidl通信能否满足实时视频的传输要求
                                byte[] datas = baos.toByteArray();
                                for(int k = 0;k<100;k++){
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    listener.sendByteArray(datas);
                                    Log.d(TAG,"send byte[] success");
                                }


                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
            myThread.start();
        }

        @Override
        public void unregistListener(IServerSendDataToClient listener) throws RemoteException {
            mListener.finishBroadcast();
            mListener.unregister(listener);
        }
    };
}
