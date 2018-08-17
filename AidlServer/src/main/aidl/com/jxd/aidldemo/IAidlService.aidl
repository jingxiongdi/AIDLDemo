// IAidlService.aidl
package com.jxd.aidldemo;

// Declare any non-default types here with import statements
import com.jxd.aidldemo.Person;
import com.jxd.aidldemo.IServerSendDataToClient;
interface IAidlService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void OnNewPersonAdd(in Person p);

    Person getPerson();

    List<Person> getPersonList();

    void registListener(IServerSendDataToClient listener);

    void unregistListener(IServerSendDataToClient listener);
}