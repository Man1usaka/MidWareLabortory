package com.lab.yao.fileclient;


import com.lab.yao.fileclient.RmiConnection;
import com.lab.yao.fileservice.FileService;
import org.springframework.web.bind.annotation.*;
import java.rmi.RemoteException;

@RestController
@RequestMapping("/file")
public class FileController {


    @PostMapping(value = "")
    public Boolean addFile(@RequestParam String fileName) throws RemoteException {

        return
                new RmiConnection<FileService>("File").getT().addFile(fileName);
    }

    @PutMapping(value = "")
    public Boolean modifyFile(@RequestParam String fileName,
                              @RequestParam String oldStr,
                              @RequestParam String replaceStr) throws RemoteException {
        return
                new RmiConnection<FileService>("File").getT().modifyFile(fileName,oldStr,replaceStr);
    }


    @DeleteMapping(value = "")
    public Boolean deleteFile(@RequestParam String fileName) throws RemoteException{
        return
                new RmiConnection<FileService>("File").getT().deleteFile(fileName);
    }


    @GetMapping(value = "/all")
    public String[] listAllFile() throws RemoteException{
        return
        new RmiConnection<FileService>("File").getT().listAllFile();
    }

    @GetMapping
    public String[] listFile() throws RemoteException{
        return
                new RmiConnection<FileService>("File").getT().listFile();
    }

    @GetMapping(value = "/count")
    public Integer countFile() throws RemoteException{
        return
                new RmiConnection<FileService>("File").getT().countFile();
    }

    @GetMapping(value = "/size")
    public String folderSize() throws RemoteException{
        return
                new RmiConnection<FileService>("File").getT().folderSize();
    }
}
