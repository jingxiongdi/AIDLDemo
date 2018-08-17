// IServerSendDataToClient.aidl
package com.jxd.aidldemo;

// Declare any non-default types here with import statements

interface IServerSendDataToClient {
   void sendData(String ccc);
   //in表示从客户端到服务器
   //  out表示服务器到客户端
   void sendByteArray(inout byte[] data);
}
