package com.lab.yao.fileserver;

import com.lab.yao.fileservice.FileService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class FileServerMain {
    public static void main(String[] argv){
        try{
            FileService fileService = new FileServiceImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("File",fileService);

            System.out.println("Server Start");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
