package com.lab.yao.fileclient;

import java.rmi.Naming;

/**
 * @author Y Jiang
 * @param <T>
 */
public class RmiConnection<T> {
    private T t;
    RmiConnection(String name){
        try {
            t=(T) Naming.lookup(name);
        }
        catch (Exception e){
            System.out.println("Error find Service");
            e.printStackTrace();
        }
    }
    public T getT() {
        return t;
    }
}
