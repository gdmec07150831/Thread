package cn.edu.gdmec.s07150831.thread;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView tv1;
    private int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView) findViewById(R.id.tv1);
        Date theLastDay = new Date(117, 5, 23);
        Date toDay = new Date();
        seconds = (int)(theLastDay.getTime() - toDay.getTime())/1000;
    }

    public void anr(View v){
        for (int i = 0; i < 100000; i++){
            //循环读一张图，超时出现ANR（Application Not Responsin)
            BitmapFactory.decodeResource(getResources(), R.drawable.androiddemo);
        }
    }

    public void threadclass(View v){
        class ThreadSample extends Thread{
            Random rm;
            public ThreadSample(String tname){
                super(tname);
                rm = new Random();
            }
            public void run(){
                for (int i = 0; i < 10; i++){
                    System.out.println(i + " " + getName());
                    try {
                        sleep(rm.nextInt(1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(getName() + "完成");
            }
        }
        ThreadSample thread1 = new ThreadSample("线程一");
        thread1.start();
        ThreadSample thread2 = new ThreadSample("线程二");
        thread2.start();
    }

    public void runnableinterrface(View v){
        class RunnableExample implements Runnable{
            Random rm;
            String name;
            public RunnableExample(String tname){
                this.name = tname;
                rm = new Random();
            }
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    System.out.println(i + " " + name);
                    try {
                        Thread.sleep(rm.nextInt(1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(name + "完成");
            }
        }
        Thread thread1 = new Thread(new RunnableExample("线程一"));
        thread1.start();
        Thread thread2 = new Thread(new RunnableExample("线程二"));
        thread2.start();
    }

    public void timertask(View v){
        class MyThread extends TimerTask{
            Random rm;
            String name;
            public MyThread(String tname){
                this.name = tname;
                rm = new Random();
            }
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    System.out.println(i + " " + name);
                    try {
                        Thread.sleep(rm.nextInt(1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(name + "完成");
            }
        }
        Timer timer1 = new Timer();
        Timer timer2 = new Timer();
        MyThread thread1 = new MyThread("线程一");
        MyThread thread2 = new MyThread("线程二");
        timer1.schedule(thread1, 0);
        timer2.schedule(thread2, 0);
    }

    public void handlermessage(View v){
        //创建自己的Handler对象来处理消息，更新UI
        final Handler myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:     //遇到消息类型未1的消息就执行更新界面的方法
                        showmsg(String.valueOf(msg.arg1 + msg.getData().get("attach").toString()));
                }
            }
        };
        //创建MyTask类继承于TimerTask抽象类
        class MyTask extends TimerTask{
            int countdown;
            double achievement1 = 1,achievement2 = 1;
            //构造方法，传入倒计时秒数
            public MyTask(int seconds){
                this.countdown = seconds;
            }
            @Override
            public void run() {
                //obtain()方法直接从消息池中取出一个可用的消息对象，比new Message()方法效率高
                Message msg = Message.obtain();
                //定义消息标志未1
                msg.what = 1;
                //每次运行把countdown减一，arg1和arg2是消息传达消息的高效率方法，但只能传int类型
                msg.arg1 = 1;
                achievement1 = achievement1 * 1.01;
                achievement2 = achievement2 * 1.02;
                //用Handler发送消息到消息队列中，压到消息队列的尾部
                myHandler.sendMessage(msg);
            }
        }
        //创建Timer对象，并把Mytask定时后台执行
        Timer timer = new Timer();
        //Timer.schedule这种参数的方法是无限次执行的
        timer.schedule(new MyTask(seconds), 1, 1000);
    }

    //显示消息的方法
    private void showmsg(String msg) {
        tv1.setText(msg);
    }

    public void asynctask(View v){
        /*
        异步任务 AsyncTask<Params,Progress,Result>
        1、Params:UI线程传过来的参数
        2、Result:返回结果的类型，耗时操作doInBackground的返回结果传给执行之后的参数类型
        */
        class LearHard extends AsyncTask<Long, String, String>{
            private Context context;
            final  int duration = 10;
            int count = 0;

            public LearHard(Activity context){
                this.context = context;
            }

            //耗时操作，后台执行方法，此方法在非UI线程中运行
            @Override
            protected String doInBackground(Long... params) {
                long num = params[0].longValue();
                while(count < duration){
                    num-- ;
                    count++;
                    String status = "离毕业还有" + num + "秒，努力学习" + count + "秒。";
                    //调用pushlishProgress(),触发onProgressUpdate
                    publishProgress(status);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return "这" + duration + "秒有收获，没虚度。";
            }
            //这个方法工作在UI线程可以更新UI
            @Override
            protected void onProgressUpdate(String... values) {
                ((MainActivity)context).tv1.setText(values[0]);
                //showmsg(values[0]);   //这个调用也有效
                super.onProgressUpdate(values);
            }
            //执行耗时操作后处理UI线程事件，接收doInBackground的返回值，这个方法工作UI线程
            @Override
            protected void onPostExecute(String s) {
                showmsg(s);
                super.onPostExecute(s);
            }
        }
        LearHard learHard = new LearHard(this);
        learHard.execute((long)seconds);
    }
}
