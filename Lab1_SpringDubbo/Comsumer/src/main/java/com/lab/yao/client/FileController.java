package com.lab.yao.client;


import com.lab.yao.service.FileService;
import org.springframework.web.bind.annotation.*;

/**
 * @author Y Jiang
 * @date  2019/3/17
 */
@RestController
@RequestMapping("/file")
public class FileController {


    @PostMapping(value = "")
    public Boolean addFile(@RequestParam String fileName){

        return
                new Consumer<FileService>("FileService").getT().addFile(fileName);
    }

    @PutMapping(value = "")
    public Boolean modifyFile(@RequestParam String fileName,
                              @RequestParam String oldStr,
                              @RequestParam String replaceStr) {
        return
               new Consumer<FileService>("FileService").getT().modifyFile(fileName,oldStr,replaceStr);
    }


    @DeleteMapping(value = "/{fileName}")
    public Boolean deleteFile(@PathVariable String fileName){
        System.out.println(fileName);
        return
               new Consumer<FileService>("FileService").getT().deleteFile(fileName);
    }


    @GetMapping(value = "/all")
    public String[] listAllFile(){
        return
               new Consumer<FileService>("FileService").getT().listAllFile();
    }

    @GetMapping
    public String[] listFile() {
        return
               new Consumer<FileService>("FileService").getT().listFile();
    }

    @GetMapping(value = "/count")
    public Integer countFile(){
        return
               new Consumer<FileService>("FileService").getT().countFile();
    }

    @GetMapping(value = "/size")
    public String folderSize(){
        return
               new Consumer<FileService>("FileService").getT().folderSize();
    }
}

