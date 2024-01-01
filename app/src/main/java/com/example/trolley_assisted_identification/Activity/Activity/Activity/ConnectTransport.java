package com.example.trolley_assisted_identification.Activity.Activity.Activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity.TAG;

import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.XcApplication;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

//底层通信和控制

/**
 * Socket数据处理类
 */
public class ConnectTransport {
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    public byte[] rbyte = new byte[50];
    private Handler reHandler;

    public short TYPEONE = 0x55;
    public short TYPE = 0xAA;
    public short TYPE2 = 0xBB;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM = 0x00;

    private int port = 60000;
    private static OutputStream SerialOutputStream;
    private InputStream SerialInputStream;
    private boolean Firstdestroy = false;

    public void destory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                bInputStream.close();
                Log.e("1", "11");
                bOutputStream.close();
                Log.e("2", "22");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
        }
    }

    public void connect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            Firstdestroy = false;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            Log.e("1", "111");
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            Log.e("2", "222");
            if (!inputDataState) {
                reThread();
            }
            EventBus.getDefault().post(new DataRefreshBean(3));
        } catch (SocketException ignored) {
            EventBus.getDefault().post(new DataRefreshBean(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    byte[] serialreadbyte = new byte[50];

    class SerialRunnable implements Runnable {
        @Override
        public void run() {
            while (SerialInputStream != null) {
                try {
                    int num = SerialInputStream.read(serialreadbyte);
                    // String  readserialstr =new String(serialreadbyte);
                    String readserialstr = new String(serialreadbyte, 0, num, "utf-8");
                    Log.e("----serialreadbyte----", "******" + readserialstr);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = serialreadbyte;
                    reHandler.sendMessage(msg);
                    Log.e(TAG, "指令" + serialreadbyte);
                    Log.e(TAG, "接收" + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean inputDataState = false;

    private void reThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket != null && !socket.isClosed()) {
                    if (Firstdestroy == true)  //Firstactivity 已销毁了
                    {
                        break;
                    }
                    try {
                        inputDataState = true;
                        bInputStream.read(rbyte);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = rbyte;
                        reHandler.sendMessage(msg);
                        Log.e(TAG, "指令" + rbyte);
                        Log.e(TAG, "接收" + msg.obj);
                    } catch (SocketException ignored) {
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataRefreshBean(4));
                        destory();
                        inputDataState = false;
                    } catch (UnsupportedOperationException ignored) {
                        inputDataState = false;
                    }
                }
            }
        }).start();

    }

    private void send() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendSecend() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE2, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    // 沉睡
    public void yanchi(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendQR(final String string) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            final byte[] sbytes = {(byte) 0xDD};
            XcApplication.executorServicetor.execute(new Runnable() {
                byte[] sbyteQR;

                {
                    try {
                        sbyteQR = string.getBytes("ascii");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                byte[] bytes = new byte[sbytes.length + sbyteQR.length];

                @Override
                public void run() {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            System.arraycopy(sbytes, 0, bytes, 0, sbytes.length);
                            System.arraycopy(sbyteQR, 0, bytes, sbytes.length, sbyteQR.length);
                            for (int x = 0; x < 3; x++) {
                                bOutputStream.write(bytes, 0, bytes.length);
                                bOutputStream.flush();
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void sendShape(final String string) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            final byte[] sbytes = {(byte) 0xEB};
            XcApplication.executorServicetor.execute(new Runnable() {
                byte[] sbyteQR;
                {
                    try {
                        sbyteQR = string.getBytes("ascii");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                byte[] bytes = new byte[sbytes.length + sbyteQR.length];
                @Override
                public void run() {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            System.arraycopy(sbytes, 0, bytes, 0, sbytes.length);
                            System.arraycopy(sbyteQR, 0, bytes, sbytes.length, sbyteQR.length);
                            bOutputStream.write(bytes, 0, bytes.length);
                            bOutputStream.flush();
                            Log.d("车牌识别数据发送","发送:"+string);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void colorRGB(int i) {
        //   MainActivity.Wifi_Flag = true;
        byte temp = (byte) TYPE;
        TYPE = 0xAA;
        MAJOR = 0x40;
        if (i == 1) {
            FIRST = 0x01;
        } else if (i == 2) {
            FIRST = 0x02;
        } else if (i == 3) {
            FIRST = 0x03;
        }
        SECOND = 0x00;
        THRID = 0x00;
        //MainActivity.toastUtil.ShowToast("开始发送");
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = temp;
    }


    public void trafficMark(int i) {
        //   MainActivity.Wifi_Flag = true;
        byte temp = (byte) TYPE;
        TYPE = 0xAA;
        MAJOR = 0x48;
        switch (i) {
            case 0:
                FIRST = 0x05;
                break;
            case 1:
                FIRST = 0x04;
                break;

            case 2:
                FIRST = 0x03;
                break;

            case 3:
                FIRST = 0x06;
                break;

            case 4:
                FIRST = 0x01;
                break;
            case 5:
                FIRST = 0x02;
                break;
        }
        SECOND = 0x00;
        THRID = 0x00;
        //MainActivity.toastUtil.ShowToast("开始发送");
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = temp;
    }


    public void GatContent(byte i) {
        byte temp = (byte) TYPE;
        TYPE = 0xAA;
        MAJOR = 0xFF;
        FIRST = i;
        SECOND = 0x00;
        THRID = 0x00;
        //MainActivity.toastUtil.ShowToast("开始发送");
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = temp;
    }


    public void SendNull(byte i) {
        byte temp = (byte) TYPE;
        TYPE = 0xAA;
        MAJOR = 0x00;
        FIRST = i;
        SECOND = 0x00;
        THRID = 0x00;
        //MainActivity.toastUtil.ShowToast("开始发送");
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = temp;
    }


    public void SendCarNum(String str) {
        Log.e("send", "车牌");
        byte[] bytes = str.getBytes();
        byte temp = (byte) TYPE;
        if (bytes.length == 6) {
            TYPE = 0xAA;
            MAJOR = 0x45;
            FIRST = bytes[0];
            SECOND = bytes[1];
            THRID = bytes[2];
            for (int x = 0; x < 3; x++) {
                send();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MAJOR = 0x46;
            FIRST = bytes[3];
            SECOND = bytes[4];
            THRID = bytes[5];
            for (int x = 0; x < 3; x++) {
                send();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TYPE = temp;
        }
    }


    //  文字识别发送
    public void Text_Spend(String str) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            final byte[] sbytes = {(byte) 0xAA};

            XcApplication.executorServicetor.execute(new Runnable() {

                byte[] sbyteTest;

                {
                    try {
                        sbyteTest = str.getBytes("GBK");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                byte[] bytes = new byte[sbytes.length + sbyteTest.length];

                @Override
                public void run() {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            System.arraycopy(sbytes, 0, bytes, 0, sbytes.length);
                            System.arraycopy(sbyteTest, 0, bytes, sbytes.length, sbyteTest.length);
//                            for (int x = 0 ; x<3 ; x++) {
                            bOutputStream.write(bytes, 0, bytes.length);
                            bOutputStream.flush();
                            Log.d("文字识别", "发送成功");
                            Log.d("文字识别", "发送:" + bytes);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    //语音播报
    public void voiceController(String str) {
//        if (XcApplication.isserial == XcApplication.Mode.SOCKET)
        {
//            final byte[] sbytes = {(byte) 0xAA};
            // TODO Auto-generated method stub

            try {
                byte[] sbyte = bytesend(str.getBytes("GBK"));
                MainActivity.Connect_Transport.send_voice(sbyte);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void send_voice(final byte[] textbyte) {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if (socket != null && !socket.isClosed()) {

                        bOutputStream.write(textbyte, 0, textbyte.length);
                        bOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private byte[] bytesend(byte[] sbyte) {
        byte[] textbyte = new byte[sbyte.length + 5];
        textbyte[0] = (byte) 0xFD;
        textbyte[1] = (byte) (((sbyte.length + 2) >> 8) & 0xff);
        textbyte[2] = (byte) ((sbyte.length + 2) & 0xff);
        textbyte[3] = 0x01;// 合成语音命令
        textbyte[4] = (byte) 0x01;// 编码格式
        for (int i = 0; i < sbyte.length; i++) {
            textbyte[i + 5] = sbyte[i];
        }

        return textbyte;
    }

    public void Android_Contor_Car_Go(int Go) {
        TYPE =  0x7A;
        MAJOR =  (byte)Go;
        FIRST =  0x00;
        SECOND = 0x00;
        THRID = 0x00;
        for (int i = 0; i < 2; i++) {   //发送3次启动程序
            send();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = 0xAA;
    }

    //    启动指令
    public void autoDrive() {
        TYPE = 0x6A;
        MAJOR = 0x00;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        for (int i = 0; i < 3; i++) {   //发送3次启动程序
            send();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TYPE = 0xAA;
    }

    //     重启标志物
    public void RestartMarker() {
        TYPE = 0x05;
        MAJOR = 0x09;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        for (int i = 0; i < 3; i++) {
            send();
            TYPE = 0xAA;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //     识别结果为空
    public void sendNull() {
        TYPE = 0xAA;
        MAJOR = 0x00;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        for (int i = 0; i < 3; i++) {
            send();
            Log.d("车牌识别数据发送","结果为空");
            TYPE = 0xAA;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    /****************************     识别数据包    **********************************/

    //红绿灯结果发送
    public void traffic_control(int type, int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
        TYPE = temp;
    }

    //交通标志物结果发送

    public void traffic_signs(int type, int major, int first, int num) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) num;
        THRID = 0x00;
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
        TYPE = temp;
    }

    //车型识别结果发送

    public void Models_cart(int type, int major, int first, int num) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) num;
        THRID = 0x00;
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
        TYPE = temp;
    }


    //口罩行人识别结果发送

    public void Mask_Person(int type, int major, int first, int num) {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) num;
        THRID = 0x00;
        for (int x = 0; x < 3; x++) {
            send();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
        TYPE = temp;
    }


    /****************************     识别数据包EN    **********************************/


    // 启动
    public void SendGO(final String string) {
        if (string == "启动") {
            if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
                final byte[] sbytes = {(byte) 0x6A};
                XcApplication.executorServicetor.execute(new Runnable() {
                    byte[] sbyteSend;

                    {
                        try {
                            sbyteSend = string.getBytes("GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    byte[] bytes = new byte[sbytes.length + sbyteSend.length];

                    @Override
                    public void run() {
                        try {
                            if (socket != null && !socket.isClosed()) {
                                System.arraycopy(sbytes, 0, bytes, 0, sbytes.length);
                                System.arraycopy(sbyteSend, 0, bytes, sbytes.length, sbyteSend.length);
                                bOutputStream.write(bytes, 0, bytes.length);
                                bOutputStream.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}

