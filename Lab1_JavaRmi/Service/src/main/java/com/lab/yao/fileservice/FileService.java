package com.lab.yao.fileservice;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * @author Y Jiang
 * @date  2019/3/13
 */
public interface FileService extends Remote {

    /**
     * 增加文件（文本文件）
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean addFile(String fileName) throws RemoteException;

    /**
     * 修改文件（文本文件）
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean modifyFile(String fileName,String oldStr,String replaceStr) throws RemoteException ;

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean deleteFile(String fileName) throws RemoteException;

    /**
     * 列出文件(包括子目录)
     * @return String[]
     */
    String[] listAllFile() throws RemoteException ;

    /**
     * 列出文件(当前目录)
     * @return String[]
     */
    String[] listFile() throws RemoteException;

    /**
     * 计算文件数量(包括子目录)
     * @return String[]
     */
    Integer countFile() throws RemoteException;

    /**
     * 占用磁盘空间总大小
     * @return String[]
     */
    String folderSize() throws RemoteException;

}
