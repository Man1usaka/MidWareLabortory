package com.lab.yao.serviceimpl;

import com.lab.yao.service.FileService;

import java.io.*;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Y Jiang
 * @date  2019/3/17
 */
public class FileServiceImpl implements FileService {

    private static String filePath = "G:\\test\\";
    private final String fileType = ".txt";

    public FileServiceImpl() throws RemoteException {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    @Override
    public Boolean addFile(String fileName)  {
        File myFile = new File(filePath, fileName + fileType);
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
                System.out.println("Add file: " + fileName);
            } catch (IOException e) {
                System.out.println("Add file Error");
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean modifyFile(String fileName,String oldStr,String replaceStr) {
        String temp = "";
        try {
            File file = new File(filePath+fileName+fileType);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            while ((temp = br.readLine()) != null) {
                temp = temp.replaceAll(oldStr,replaceStr);
                buf = buf.append(temp);
                buf = buf.append(System.getProperty("line.separator"));
            }

            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            pw.close();
            System.out.println("Modify file:"+fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public Boolean deleteFile(String fileName) {
        File myFile = new File(filePath, fileName);
        if(myFile.exists()){
            if(myFile.isFile()){
                myFile.delete();
                System.out.println("Delete file: "+fileName);
                return true;
            }else{
                deleteDir(myFile.getPath());
                System.out.println("Delete path: "+fileName);
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] listAllFile() {
        ArrayList<String> result = new ArrayList<String>();
        LinkedList<String> folderList = new LinkedList<String>();
        folderList.add(filePath);
        while (folderList.size() > 0) {

            File file = new File(folderList.peek());
            folderList.removeFirst();
            File[] files = file.listFiles();
            ArrayList<File> fileList = new ArrayList<File>();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        folderList.add(files[i].getPath());
                    } else {
                        fileList.add(files[i]);
                    }
                }
            }
            for (File f : fileList) {
                result.add(f.getName());
            }
        }
        System.out.println("List All file");
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] listFile() {
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                result.add(f.getName());
            }
        }
        System.out.println("List File");
        return result.toArray(new String[result.size()]);
    }

    @Override
    public Integer countFile() {
        System.out.println("Count File");
        return listFile().length;
    }

    @Override
    public String folderSize() {

        long total=0;
        LinkedList<String> folderList = new LinkedList<String>();
        folderList.add(filePath);
        while (folderList.size() > 0) {

            File file = new File(folderList.peek());
            folderList.removeFirst();
            File[] files = file.listFiles();
            ArrayList<File> fileList = new ArrayList<File>();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        folderList.add(files[i].getPath());
                    } else {
                        fileList.add(files[i]);
                    }
                }
                for (File f : fileList) {
                    total+=f.length();
                }
            }
        }
        System.out.println("Get folder size: "+readableFileSize(total));
        return readableFileSize(total);
    }

    private static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void deleteDir(String path){
        LinkedList<String> folderList = new LinkedList<String>();
        folderList.add(path);
        while (folderList.size() > 0) {
            File file = new File(folderList.peek());
            folderList.removeFirst();
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDir(files[i].getPath());
                    } else {
                        files[i].delete();
                    }
                }
            }
            file.delete();
        }

    }
}
