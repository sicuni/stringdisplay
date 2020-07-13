package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public class Controller {
    private volatile int count=0;
    private volatile int semaphoreCount=0;
    @FXML
    private Button confirm,clear;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private TextField string1,string2,string3,str1,str2,str3;
    @FXML
    private TextField allString;

    TextField[] textField = new TextField[6]; //用于存储TextField 便于操作


    //开始执行
    public void waitNotify(){
        if(string1.getText().equals("")||string2.getText().equals("")||string3.getText().equals("")){
            allString.setText("请在上面3个输入框中均输入信息");
        }else{
            //设置控件不可操作
            confirm.setDisable(true);
            clear.setDisable(true);
            toggleGroup.getToggles().forEach(e -> {
                Node node = (Node) e;
                node.setDisable(true);
            });
            allString.setText("");
            //判断所选的ToggleGroup
            if(toggleGroup.getSelectedToggle().getUserData().equals("0") ){
                textField[0]=string1;
                textField[1]=string2;
                textField[2]=string3;
                textField[3]=str1;
                textField[4]=str2;
                textField[5]=str3;
                MyThread[] myThread = new MyThread[3];
                myThread[0] = new MyThread(0);
                myThread[1] = new MyThread(1);
                myThread[2] = new MyThread(2);
                for (MyThread thread:myThread) {
                    thread.start();
                }
                for (MyThread thread:myThread) {
                    thread.notify();
                }

            } else if(toggleGroup.getSelectedToggle().getUserData().equals("1")){
                CountDownLatch latch = new CountDownLatch(3);
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            while (latch.getCount() != 3) {
                                this.wait(200);
                            }
                            str1.setText(string1.getText());
                            Thread.sleep((long) Math.random() * 1000 + 1000);
                            latch.countDown();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            while (latch.getCount() != 2) {
                                this.wait(200);
                            }
                            str2.setText(string2.getText());
                            Thread.sleep((long) Math.random() * 1000 + 1000);
                            latch.countDown();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            while (latch.getCount() != 1) {
                                this.wait(200);
                            }
                            str3.setText(string3.getText());
                            Thread.sleep((long) Math.random() * 1000 + 1000);
                            latch.countDown();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(() -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    allString.setText(string1.getText()+" "+string2.getText()+" "+string3.getText());
                    clear.setDisable(false);
                    confirm.setDisable(false);
                    toggleGroup.getToggles().forEach(e -> {
                        Node node = (Node) e;
                        node.setDisable(false);
                    });

                }).start();
            } else if(toggleGroup.getSelectedToggle().getUserData().equals("2")){
                Semaphore semaphore = new Semaphore(1);
                clear.setDisable(true);
                confirm.setDisable(true);
                toggleGroup.getToggles().forEach(e -> {
                    Node node = (Node) e;
                    node.setDisable(true);
                });
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            semaphore.acquire();
                            str1.setText(string1.getText());
                            semaphoreCount++;
                            Thread.sleep(Math.random() > 0.5 ? 1000 : 2000);
                            semaphore.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            semaphore.acquire();
                            str2.setText(string2.getText());
                            semaphoreCount++;
                            Thread.sleep(Math.random() > 0.5 ? 1000 : 2000);
                            semaphore.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(() -> {
                    synchronized (this) {
                        try {
                            semaphore.acquire();
                            str3.setText(string3.getText());
                            semaphoreCount++;
                            Thread.sleep(Math.random() > 0.5 ? 1000 : 2000);
                            semaphore.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(() -> {
                    synchronized (this) {
                        while (semaphoreCount != 3) {
                            try {
                                this.wait(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        allString.setText(string1.getText()+" "+string2.getText()+" "+string3.getText());
                        clear.setDisable(false);
                        confirm.setDisable(false);
                        toggleGroup.getToggles().forEach(e -> {
                            Node node = (Node) e;
                            node.setDisable(false);
                        });
                    }
                }).start();
            }
        }

        return;
    }

    //清除所有信息
    public void init(){
        string1.setText("");
        string2.setText("");
        string3.setText("");
        str1.setText("");
        str2.setText("");
        str3.setText("");
        allString.setText("");
        return;
    }

    class MyThread extends Thread{
        public int num;
        MyThread(int num){
            this.num=num;
        }
        @Override
        public void run() {
            try {
                while (count != 3) {
                    synchronized (this) {
                        try {
                            this.wait(new Random().nextInt(2000)+1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textField[num + 3].setText(textField[num].getText());
                        count++;
                        if(count==3){
                            allString.setText(string1.getText()+" " + string2.getText() +" "+ string3.getText());
                            confirm.setDisable(false);
                            clear.setDisable(false);
                            toggleGroup.getToggles().forEach(e -> {
                                Node node = (Node) e;
                                node.setDisable(false);
                            });
                        }
                        MyThread.sleep(1000);
                        break;
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
