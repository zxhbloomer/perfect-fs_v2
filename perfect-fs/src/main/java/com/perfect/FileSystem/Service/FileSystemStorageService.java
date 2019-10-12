package com.perfect.filesystem.Service;

import com.perfect.filesystem.Exception.StorageException;
import com.perfect.filesystem.Exception.StorageFileNotFoundException;
import com.perfect.filesystem.Propert.StorageProperties;
import com.perfect.filesystem.Utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author zhangxh
 */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final String strRootPath;

    private String fileUrl;

    @Autowired
    private StorageProperties prop;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.strRootPath = properties.getLocation();
    }

    @Override
    public String store(String fileUuid, MultipartFile multipartFile, String fileName) {
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        if (prop.isRename()) {
            filename = fileName;
        }

        try {
            if (multipartFile.isEmpty()) {
                throw new StorageException("空文件保存错误： " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException("保存的相对路径发生错误：" + filename);
            }
//            try (InputStream inputStream = file.getInputStream()) {
//                Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
//            }
            // 获取路径
            String filePath = getServerAbsolutePath(strRootPath, fileUuid, fileName);
            File tmpFile = new File(filePath);
            File parentDir = tmpFile.getParentFile();
            if ((parentDir != null) && (!parentDir.exists())) {
                parentDir.mkdirs();
            }
            multipartFile.transferTo(new File(tmpFile.getAbsolutePath()).toPath());
            return filePath;
        } catch (IOException e) {
            throw new StorageException("保存文件发生错误：" + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        // TODO Auto-generated method stub

    }

    /**
     * 建立文件
     * 
     * @param uploadDir
     * @param filename
     * @return
     * @throws IOException
     */
    public File getAndCreateSFile(String uploadDir, String filename) throws IOException {
        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    /**
     * 获取保存路径
     */
    public String generateServerPath(String fileUuid, String fileName) {
        if (fileUuid == null) {
            return null;
        }
        int modulus = getFileIdModulus(fileUuid);
        String today = DateTimeUtil.dateTime();
        String thisYear = DateTimeUtil.getYear();

        StringBuffer resumePath = new StringBuffer();
        resumePath.append(thisYear).append(File.separator).append(today).append(File.separator).append(modulus)
            .append(File.separator).append(fileUuid).append(File.separator).append(fileName);

        return resumePath.toString();
    }

    /**
     * 文件url
     * @return
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     *
     * @param fileId
     * @return
     */
    private int getFileIdModulus(String fileId) {
        int hashCode = fileId.hashCode();
        int modulus = Math.abs(hashCode % 1000);

        return modulus;
    }

    public String getServerAbsolutePath(String filePath,String fileUuid, String fileName) {
        String absoluteHostPath = filePath + File.separator + generateServerPath(fileUuid, fileName);
        fileUrl = prop.getFsName() + "/" + generateServerPath(fileUuid, fileName).replace(File.separator, "/");
        return absoluteHostPath;
    }
}
