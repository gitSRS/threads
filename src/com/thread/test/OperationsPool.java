package com.thread.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by RStreltsov on 27.02.2017.
 */
public class OperationsPool {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1); //latch for threads
        final Account a = new Account(1000);
        final Account b = new Account(2000);
        Random rnd = new Random();
        List<Future> list = new ArrayList<>();


        // another thread for counter
        ScheduledExecutorService sService = Executors.newSingleThreadScheduledExecutor();
        sService.scheduleAtFixedRate( new Runnable(){
            public void run(){
                System.out.println("fail = "+a.getFailCounter());
            }
        }, 2, 1, TimeUnit.SECONDS);


        ExecutorService service = Executors.newFixedThreadPool(3);


        for(int i =0; i < 10; i++) {
            Thransfer tr = new Thransfer(a, b, rnd.nextInt(500), latch);
            System.out.println("thread created");
            //service.submit(tr);

            list.add(service.submit(tr));
            /*
            Future f = service.submit(tr);
            try {
                System.out.println("Operation result = "+f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                //System.out.println("111 = "+e.getLocalizedMessage());
                if(e.getCause().toString().equals("com.thread.test.InsufficientFoundsException")){
                    System.out.println("Operation result = InsufficientFoundsException");
                }
            }
            */
            System.out.println("thread = "+tr.getId());
            System.out.println("a= "+a.getBalance());
            System.out.println("b= "+b.getBalance());
        }

        latch.countDown(); //start of all threads

        System.out.println("list size = "+list.size());
        if(list.size() > 0) {
            for (Future f : list) {
                try {
                    System.out.println("Operation result = " + f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    //System.out.println("111 = "+e.getLocalizedMessage());
                    if (e.getCause().toString().equals("com.thread.test.InsufficientFoundsException")) {
                        System.out.println("Operation result = InsufficientFoundsException");
                    }
                }
            }
        }

        service.shutdown();


        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        sService.shutdown();
    }

}
