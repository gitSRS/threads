package com.thread.test;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by RStreltsov on 27.02.2017.
 */
public class Thransfer implements Callable<Boolean> {
    private Account acc1;
    private Account acc2;
    private int amount;
    private static AtomicInteger id = new AtomicInteger(0);
    private CountDownLatch latch;

    @Override
    public Boolean call() throws Exception {

        System.out.println("waiting for start");
        latch.await();// await for current thread
        Random rd = new Random();

        /*
        try {
            Operations.transfer(this.acc1, this.acc2, this.amount);
        } catch(InterruptedException e) {
            return Boolean.FALSE;
        } catch(InsufficientFoundsException e) {
            System.out.println("Low balance!");
          throw e;
        }

        return Boolean.TRUE;
        */
        System.out.println("start = "+ LocalTime.now());
        if(acc1.getLock().tryLock(2, TimeUnit.SECONDS)) {
            try {
                if(acc1.getBalance() < amount) {
                    System.out.println("Low balance at acc1");
                    acc1.incFailCounter();
                    throw new InsufficientFoundsException();
                }
                if(acc2.getLock().tryLock(2, TimeUnit.SECONDS)) {
                    try{
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                        System.out.println("Transfer Success!");
                        Thread.sleep(1000);
                    } finally {
                        acc2.getLock().unlock();
                    }
                } else {
                    System.out.println("ERROR with lock acc2");
                    acc2.incFailCounter();
                    return false;
                }
            } finally {
                acc1.getLock().unlock();
            }
        } else {
            System.out.println("ERROR with lock acc1");
            acc1.incFailCounter();
            return false;
        }
        return true;

    }

    public Thransfer(Account acc1, Account acc2, int amount, CountDownLatch latch) {
        this.acc1 = acc1;
        this.acc2 = acc2;
        this.amount = amount;
        this.id.incrementAndGet();
        this.latch = latch;
    }

    public int getId() {
        return id.get();
    }
}
