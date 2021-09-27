package com.perfect.filesystem;

import com.perfect.filesystem.File.StoreSource;
import com.perfect.filesystem.Propert.StorageProperties;
import com.perfect.filesystem.Service.AliService;
import com.perfect.filesystem.Service.FastdfsServcice;
import com.perfect.filesystem.Service.MongoService;
import com.perfect.filesystem.Service.QiniuService;
import com.perfect.filesystem.Service.StorageService;
import com.perfect.filesystem.myfs.properties.PerfectFsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

/**
 * @author zhangxh
 */
@SpringBootApplication(scanBasePackages = {"com.perfect.filesystem.*", "com.perfect.filesystem.myfs.*"})
@EntityScan(basePackages = {"com.perfect.*"})
@EnableScheduling
@Slf4j
@EnableTransactionManagement
@EnableConfigurationProperties({PerfectFsProperties.class, StorageProperties.class})
public class FileSystemApplication {

    @Autowired
    private StorageProperties prop;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    AliService aliService;

    @Autowired
    FastdfsServcice fastdfsServcice;

    @Autowired
    MongoService mongoService;

    // @Autowired
    // SeaweedfsService seaweedfsService;

    public static void main(String[] args) {
        log.info("-----------------------启动开始-------------------------");
        SpringApplication.run(FileSystemApplication.class, args);
        log.info("-----------------------启动完毕-------------------------");
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            // storageService.deleteAll();
            storageService.init();
            registerStoreSource();
        };
    }

    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

    public void registerStoreSource() {
        if (prop.isToqiniu()) {
            StoreSource.RegisterListensers(qiniuService);
        }

        if (prop.isToalioss()) {
            StoreSource.RegisterListensers(aliService);
        }

        if (prop.isTofastdfs()) {
            StoreSource.RegisterListensers(fastdfsServcice);
        }

        if (prop.isTomongodb()) {
            StoreSource.RegisterListensers(mongoService);
        }

        if (prop.isToseaweedfs()) {
            // StoreSource.RegisterListensers(seaweedfsService);
        }
    }
}
