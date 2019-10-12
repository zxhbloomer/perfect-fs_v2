package com.perfect.filesystem.Controller;

import com.perfect.filesystem.Entity.Diskfile;
import com.perfect.filesystem.Entity.DiskfileRepository;
import com.perfect.filesystem.Exception.StorageFileNotFoundException;
import com.perfect.filesystem.File.FileListener;
import com.perfect.filesystem.File.StoreSource;
import com.perfect.filesystem.File.UploadFileExt;
import com.perfect.filesystem.File.UploadResult;
import com.perfect.filesystem.Propert.StorageProperties;
import com.perfect.filesystem.Service.FileSystemStorageService;
import com.perfect.filesystem.Utils.HttpHelper;
import com.perfect.filesystem.myfs.bean.JSONResult;
import com.perfect.filesystem.myfs.bean.pojo.UploadFileResultPojo;
import com.perfect.filesystem.myfs.util.ResultUtil;
import com.perfect.filesystem.myfs.util.UuidUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zxh
 */
@Controller
public class FileUploadDownloadController {
    public static final String FS_DISK = "disk";
    public static final String FS_MONGODB = "mongodb";
    public static final String FS_QINIU = "qiniu";
    public static final String FS_FASTDFS = "fastdfs";
    public static final String FS_ALIOSS = "alioss";

    @Autowired
    private FileSystemStorageService storageService;
    @Autowired
    private StorageProperties prop;
    @Autowired
    private DiskfileRepository diskfileRepository;

    /**
     * 现阶段使用的方法
     * @param request
     * @param appid
     * @param username
     * @param groupid
     * @return
     */
    @ApiOperation(value = "用于外接Post上传请求，不重定向")
    @PostMapping("/api/v1/file/upload")
    public ResponseEntity<JSONResult<UploadFileResultPojo>> upload(MultipartHttpServletRequest request, @RequestParam int appid,
        @RequestParam String username, @RequestParam String groupid) {
        Iterator<String> itr = request.getFileNames();
        // 只取一个文件，不取多个
        MultipartFile file = request.getFile(itr.next());
        String fileName = file.getOriginalFilename();

        if (prop.isRename()) {
            fileName = username + "_" + file.getOriginalFilename();
            if (groupid != null && !groupid.isEmpty()) {
                fileName = groupid + "_" + file.getOriginalFilename();
            }
        }

        final String finalFilename = fileName;

        String fileUuid = UuidUtil.randomUUID();

        Map<String,Object> map = doUpload(fileUuid, file, finalFilename);
        Diskfile dbFile = dbSave(map, fileUuid, appid, username, groupid, file, fileName);

        String fsType = FS_DISK;
        String fsUri = "";
        String fsType2Url = "";
        String appContext = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+ request.getServerPort() + appContext + "/";
        fsType2Url = basePath + dbFile.getUrldisk();
        if (prop.isToqiniu()) {
            fsType=FS_QINIU;
            fsUri = dbFile.getUrlqiniu();
            fsType2Url = dbFile.getUrlqiniu();
        }
        if (prop.isToalioss()) {
            fsType=FS_ALIOSS;
            fsUri = dbFile.getUrlalioss();
            fsType2Url = dbFile.getUrlalioss();
        }

        if (prop.isTofastdfs()) {
            fsType=FS_FASTDFS;
            fsUri = dbFile.getUrlfastdfs();
            fsType2Url = basePath + dbFile.getUrlalioss();
        }

        if (prop.isTomongodb()) {
            fsType=FS_MONGODB;
            fsUri = basePath + dbFile.getUrlmongodb();
        }



        UploadFileResultPojo uploadFileResultPojo = new UploadFileResultPojo();
        uploadFileResultPojo.setFileName(dbFile.getFileName());
        uploadFileResultPojo.setFile_size(dbFile.getFileSize().longValue());
        uploadFileResultPojo.setFileUuid(dbFile.getFileid());

        uploadFileResultPojo.setUriDisk(dbFile.getUrldisk());
        uploadFileResultPojo.setFsType(fsType);

        uploadFileResultPojo.setUriFs(fsUri);
        uploadFileResultPojo.setFsType2Url(prop.getFsBaseUrl() + dbFile.getFileUrl());


        return ResponseEntity.ok().body(ResultUtil.success(uploadFileResultPojo));
    }

    @ApiOperation(value = "文件上传后在上传页面展示文件")
    @GetMapping("/files")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files",
            storageService.loadAll()
                .map(path -> MvcUriComponentsBuilder
                    .fromMethodName(FileUploadDownloadController.class, "serveFile", path.getFileName().toString())
                    .build().toString())
                .collect(Collectors.toList()));

        return "file/" + prop.getTemplate() + "/uploadForm";
    }

    @ApiOperation(value = "通过HttpHeaders下载文件")
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ApiOperation(value = "文件上传Demo,用于上传测试，上传后将重定向")
    @PostMapping("/fileUpload")
    public String handleFileUpload(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes,
        @RequestParam int appid, @RequestParam String username, @RequestParam String groupid) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = request.getFile(itr.next()); // 只取一个文件，不取多个
        String fileName = file.getOriginalFilename();

        if (prop.isRename()) {
            fileName = username + "_" + file.getOriginalFilename();
            if (groupid != null && !groupid.isEmpty()) {
                fileName = groupid + "_" + file.getOriginalFilename();
            }
        }

        final String finalFilename = fileName;

        String fileUuid = UuidUtil.randomUUID();
        Map<String,Object> map = doUpload(fileUuid, file, finalFilename);
        dbSave(map, fileUuid, appid, username, groupid, file, fileName);

        redirectAttributes.addFlashAttribute("message", "上传成功: " + file.getOriginalFilename());

        return "redirect:/files";
    }

    @ApiOperation(value = "用于外接Post上传请求，不重定向")
    @PostMapping("/fileUploadPost")
    public ResponseEntity<JSONResult<UploadFileResultPojo>> handleFileUploadPost(MultipartHttpServletRequest request, @RequestParam int appid,
        @RequestParam String username, @RequestParam String groupid) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = request.getFile(itr.next()); // 只取一个文件，不取多个
        String fileName = file.getOriginalFilename();

        if (prop.isRename()) {
            fileName = username + "_" + file.getOriginalFilename();
            if (groupid != null && !groupid.isEmpty()) {
                fileName = groupid + "_" + file.getOriginalFilename();
            }
        }

        final String finalFilename = fileName;

        String fileUuid = UuidUtil.randomUUID();

        Map<String,Object> map = doUpload(fileUuid, file, finalFilename);
        Diskfile dbFile = dbSave(map, fileUuid, appid, username, groupid, file, fileName);

        UploadFileResultPojo uploadFileResultPojo = new UploadFileResultPojo();
        uploadFileResultPojo.setFileName(dbFile.getFileName());
        uploadFileResultPojo.setFile_size(dbFile.getFileSize().longValue());
        uploadFileResultPojo.setFileUuid(dbFile.getFileid());
        return ResponseEntity.ok().body(ResultUtil.success(uploadFileResultPojo));
    }

    public Map<String,Object> doUpload(String fileUuid, MultipartFile file, final String finalFilename) {
        Map<String,Object> rtn = new HashMap<>();

        // 磁盘存储
        if (prop.isTodisk()) {
            String filePath = storageService.store(fileUuid, file, finalFilename);
            rtn.put("local",filePath);
        }

        // 第三方存储
        UploadFileExt ufe;
        try {
            ufe = new UploadFileExt(finalFilename, file.getBytes(), file.getContentType(), file.getSize());

            if (ufe != null) {
                for (FileListener fl : StoreSource.getListensers()) {
                    UploadResult uploadResult = fl.Store(ufe);
                    rtn.put("other",uploadResult);
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return rtn;
    }

    public Diskfile dbSave(Map<String,Object> map, String uuid, int appid, String username, String groupid, MultipartFile file, String fileName) {
        // 数据库存储
        Diskfile dbFile = new Diskfile();
        String fileId = uuid;

        dbFile.setFileid(fileId);
        dbFile.setAppid(appid);
        dbFile.setFileExt(file.getContentType());
        dbFile.setFileFlag("1");
        dbFile.setFileName(fileName);
        dbFile.setFileSize(BigInteger.valueOf(file.getSize()));
        dbFile.setIspublic("1");
        dbFile.setUploadDate(new Date());
        dbFile.setUploadUser(username);
        dbFile.setUrldisk(map.get("local").toString());
        dbFile.setFileUrl(storageService.getFileUrl());

        if (prop.isToqiniu()) {
            dbFile.setUrlqiniu(prop.getQiniuprefix() + fileName);
        }

        if (prop.isTomongodb()) {
            dbFile.setUrlmongodb(fileId);
        }

        diskfileRepository.save(dbFile);

        return dbFile;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "文件下载")
    @GetMapping("/downloadByFilename")
    public ResponseEntity<Boolean> downloadByFilename(String filename) throws IOException {

        Boolean downloadSuccess = false;
        downloadSuccess =
            HttpHelper.executeDownloadFile(HttpHelper.createHttpClient(), prop.getDownloadurl() + filename, // 服务器文件
                prop.getDownloadto() + filename, // 下载到本地的文件
                "UTF-8", true);

        return new ResponseEntity<Boolean>(downloadSuccess, HttpStatus.OK);
    }

    @ApiOperation(value = "文件下载，通过七牛云下载")
    @GetMapping("/downloadQiniu")
    public String downloadQiniu(String fileId) throws IOException {

        return "";
    }

    @ApiOperation(value = "文件下载，通过阿里云下载")
    @GetMapping("/downloadAli")
    public String downloadAli(String fileId) throws IOException {

        return "";
    }

    @ApiOperation(value = "文件下载，通过FastDFS下载")
    @GetMapping("/downloadFastDFS")
    public String downloadFastDFS(String fileId) throws IOException {

        return "";
    }

    @ApiOperation(value = "文件下载，通过MongoDB下载")
    @GetMapping("/downloadMongoDB")
    public String downloadMongoDB(String fileId) throws IOException {

        return "";
    }

    @ApiOperation(value = "文件下载，通过SeaweedFS下载")
    @GetMapping("/downloadSeaweedFS")
    public String downloadSeaweedFS(String fileId) throws IOException {

        return "";
    }

}
