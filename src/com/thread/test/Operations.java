package com.thread.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by RStreltsov on 23.02.2017.
 */
public class Operations {

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transfer(a,b,500);
                    System.out.println("a -> b : Success!");
                } catch (InsufficientFoundsException e) {
                    System.out.printf("Not enough money 1");
                } catch (InterruptedException e) {
                    System.out.printf("Interrupt 1");
                }
            }
        }, "thread1"

        ).start();

        try {
            transfer(b,a,300);
            System.out.println("b -> a : Success!");
        } catch (InsufficientFoundsException e) {
            System.out.printf("Not enough money 2");
        } catch (InterruptedException e) {
            System.out.printf("Interrupt 2");
        }
    }

    static void transfer(Account acc1, Account acc2, int amount) throws InsufficientFoundsException, InterruptedException {


        if(acc1.getLock().tryLock(10, TimeUnit.SECONDS)) {
            try {
                if(acc1.getBalance() < amount) {
                    throw new InsufficientFoundsException();
                }
                if(acc2.getLock().tryLock(10, TimeUnit.SECONDS)) {
                    try{
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                    } finally {
                        acc2.getLock().unlock();
                    }
                } else {
                    System.out.println("ERROR with lock acc2");
                    acc2.incFailCounter();
                }
            } finally {
                acc1.getLock().unlock();
            }
        } else {
            System.out.println("ERROR with lock acc1");
            acc1.incFailCounter();
        }


        /*
        synchronized (acc1){
           System.out.println("Lock on acc1 on thread: "+Thread.currentThread().getName());
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (acc2){
                System.out.println("Lock on acc2 on thread: "+Thread.currentThread().getName());
                acc1.withdraw(amount);
                acc2.deposit(amount);
            }
        }
        */

    }


}
