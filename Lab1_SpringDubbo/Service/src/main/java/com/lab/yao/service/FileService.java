package com.lab.yao.service;

/**
 * @author Y Jiang
 * @date  2019/3/17
 */
public interface FileService{

    /**
     * 增加文件（文本文件）
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean addFile(String fileName);

    /**
     * 修改文件（文本文件）
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean modifyFile(String fileName,String oldStr,String replaceStr);

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 成功true、失败false
     */
    Boolean deleteFile(String fileName);

    /**
     * 列出文件(包括子目录)
     * @return String[]
     */
    String[] listAllFile();

    /**
     * 列出文件(当前目录)
     * @return String[]
     */
    String[] listFile();

    /**
     * 计算文件数量(包括子目录)
     * @return String[]
     */
    Integer countFile();

    /**
     * 占用磁盘空间总大小
     * @return String[]
     */
    String folderSize();

}
