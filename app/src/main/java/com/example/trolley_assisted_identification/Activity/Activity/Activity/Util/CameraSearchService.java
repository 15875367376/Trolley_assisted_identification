package com.example.trolley_assisted_identification.Activity.Activity.Activity.Util;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class CameraSearchService extends IntentService {

    public CameraSearchService() {
        super("CameraSearchService");
    }

    //摄像头ͷIP
    private String IP = null;

    @Override
    protected void onHandleIntent(Intent intent) {
        SearchCameraUtil searchCameraUtil;
        for (int i = 0;i < 3 && IP == null;i++) {
            searchCameraUtil = new SearchCameraUtil();
            IP = searchCameraUtil !=null ? searchCameraUtil.send(): null;
            Log.e("IP","下一步");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent mintent = new Intent(CameraConnectUtil.A_S);
        mintent.putExtra("IP", XcApplication.cameraip);
        mintent.putExtra("IP", IP + ":81");
        mintent.putExtra("pureip", IP);
        sendBroadcast(mintent);
    }

    class SearchCameraUtil {
        private String IP = "";
        private String TAG = "Service";
        private final int PORT = 3565;
        private final int SERVER_PORT = 8600;
        private byte[] mbyte = new byte[]{68, 72, 1, 1};
        private DatagramSocket dSocket = null;
        private byte[] msg = new byte[1024];
        boolean isConn = false;

        public SearchCameraUtil() {
        }

        public String send() {
            InetAddress local = null;

            try {
                local = InetAddress.getByName("255.255.255.255");
                Log.e(this.TAG, "已找到服务器,连接中...");
            } catch (UnknownHostException var7) {
                Log.e(this.TAG, "未找到服务器.");
                var7.printStackTrace();
                return null;
            }

            try {
                if (this.dSocket != null) {
                    this.dSocket.close();
                    this.dSocket = null;
                }
                // 第一次连接没有报错，第二次开始报这个错误。字面意思看出是由于端口被占用，未释放导致。
                // 虽然程序貌似已经退出，个人猜测是由于系统还没有及时释放导致的。
//				this.dSocket = new DatagramSocket(3565);
                if(dSocket == null){
                    dSocket = new DatagramSocket(null);
                    dSocket.setReuseAddress(true);
                    dSocket.bind(new InetSocketAddress(PORT));
                }
                Log.e(this.TAG, "正在连接服务器...");
            } catch (SocketException var6) {
                var6.printStackTrace();
                Log.e(this.TAG, "服务器连接失败.");
                return null;
            }

            DatagramPacket sendPacket = new DatagramPacket(this.mbyte, 4, local, SERVER_PORT);
            DatagramPacket recPacket = new DatagramPacket(this.msg, this.msg.length);

            try {
                this.dSocket.send(sendPacket);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isConn){
                            dSocket.close();
                            this.cancel();
                        }
                        isConn = true;
                    }
                }, 0, 1000);
                this.dSocket.receive(recPacket);
                timer.cancel();
                String text = new String(this.msg, 0, recPacket.getLength());
                if (text.substring(0, 2).equals("DH")) {    
                    this.getIP(text);
                }

                Log.e("IP值", this.IP);
                this.dSocket.close();
                Log.e(this.TAG, "消息发送成功!");
            } catch (IOException var5) {
                var5.printStackTrace();
                Log.e(this.TAG, "消息发送失败.");
                return null;
            }

            return this.IP;
        }

        private void getIP(String text) {
            try {
                byte[] ipbyte = text.getBytes("UTF-8");

                for(int i = 4; i < 22 && ipbyte[i] != 0; ++i) {
                    if (ipbyte[i] == 46) {
                        this.IP = this.IP + ".";
                    } else {
                        this.IP = this.IP + (ipbyte[i] - 48);
                    }
                }
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
            }

        }

    }
}