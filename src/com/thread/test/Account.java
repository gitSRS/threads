package com.thread.test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by RStreltsov on 23.02.2017.
 */
public class Account {
    private int balance;
    private Lock lock;

    private AtomicInteger failCounter = new AtomicInteger(0);

    public Account(int balance) {
        this.balance = balance;
        lock = new ReentrantLock();
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock l) {
        this.lock = l;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void incFailCounter(){
        this.failCounter.incrementAndGet();
    }

    public int getFailCounter() {
        return failCounter.get();
    }
}
