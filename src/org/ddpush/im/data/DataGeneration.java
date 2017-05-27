package org.ddpush.im.data;

import org.ddpush.im.v1.client.appserver.Pusher;

import java.security.MessageDigest;

/**
 * Created by liuji on 2017/5/26.
 */
public class DataGeneration {

    private static final String SERVER_IP = "10.4.252.176";
    private static final String TCP_PORT = "9966";
    private static final String PUSH_PORT = "9999";
    private static final String USER01 = "01";//温度随机值
    private static final String USER02 = "02";//湿度随机值

    public static void main(String[] args){
        while(true){
            try {
                Thread.sleep(2500);
                int x1 = CreateRandomValue1();
                send0x11(x1,USER01);
                Thread.sleep(2500);
                int x2 = CreateRandomValue2();
                send0x11(x2,USER02);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private static int CreateRandomValue1(){
        int i = (int)(Math.random()*10);
        System.out.println(i);
        return i;
    }

    private static int CreateRandomValue2(){
        int i = (int)(Math.random()*100);
        System.out.println(i);
        return i;
    }

    protected static void send0x11(int value,String userid){
        long msg;
        try{
            msg = (long)value;
        }catch(Exception e){
            System.out.println("数字格式错误");
            return;
        }
        int port;
        try{
            port = Integer.parseInt(PUSH_PORT);
        }catch(Exception e){
            System.out.println("推送端口格式错误："+PUSH_PORT);
            return;
        }
        byte[] uuid = null;
        try{
            uuid = md5Byte(userid);
        }catch(Exception e){
            System.out.println("错误："+e.getMessage());
            return;
        }
        Thread t = new Thread(new send0x11Task(SERVER_IP,port,uuid,msg));
        t.start();
    }

    public static byte[] md5Byte(String encryptStr) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptStr.getBytes("UTF-8"));
        return md.digest();
    }

    static class send0x11Task implements Runnable{
        private String serverIp;
        private int port;
        private byte[] uuid;
        private long msg;

        public send0x11Task( String serverIp, int port, byte[] uuid, long msg){
            this.serverIp = serverIp;
            this.port = port;
            this.uuid = uuid;
            this.msg = msg;
        }

        public void run(){
            Pusher pusher = null;
            try{
                boolean result;
                pusher = new Pusher(serverIp,port, 1000*5);
                result = pusher.push0x11Message(uuid,msg);
                if(result){
                    System.out.println("发送成功");
                }else{
                    System.out.println("发送失败");
                }
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("发送失败");
            }finally{
                if(pusher != null){
                    try{pusher.close();}catch(Exception e){};
                }
            }
        }
    }

}
