package org.changmoxi.vhr.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.info.FastDFSInfo;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

/**
 * @author CZS
 * @create 2023-02-20 18:31
 **/
@Slf4j
public class FastDFSUtil {
    /**
     * 用于上传和下载文件
     */
    private static final StorageClient1 STORAGE_CLIENT_1;
    /**
     * FastDFS信息
     */
    private static final FastDFSInfo FASTDFS_INFO = SpringContextHolder.getBean(FastDFSInfo.class);

    /**
     * 初始化操作
     */
    static {
        try {
            // 加载配置文件
            ClientGlobal.initByProperties("fastdfs-client.properties");
            // 创建TrackerClient
            TrackerClient trackerClient = new TrackerClient();
            // 从中获取TrackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            STORAGE_CLIENT_1 = new StorageClient1(trackerServer, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传(小文件)，返回fileId
     *
     * @param file
     * @param nameValuePairs 上传文件的元数据，null表示没有
     * @return
     */
    public static String upload(MultipartFile file, NameValuePair[] nameValuePairs) {
        try {
            String fileId = STORAGE_CLIENT_1.upload_file1(file.getBytes(),
                    StringUtils.substring(file.getOriginalFilename(), StringUtils.lastIndexOf(file.getOriginalFilename(), ".") + 1),
                    nameValuePairs);
            log.info("FastDFS上传文件成功:{}", fileId);
            return fileId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取通过Nginx访问FastDFS服务器的文件的地址
     *
     * @param fileId
     * @return
     */
    public static String getFileAccessUrl(String fileId) {
        try {
            // 时间戳
            int timestamp = (int) Instant.now().getEpochSecond();
            // 不包含开头的group的fileId
            String fileIdWithoutGroup = StringUtils.substring(fileId, StringUtils.indexOf(fileId, "/") + 1);
            // 获取通过Nginx访问FastDFS服务器的文件的令牌token
            String token = ProtoCommon.getToken(fileIdWithoutGroup, timestamp, FASTDFS_INFO.getHttpSecretKey());
            // 返回访问地址
            return FASTDFS_INFO.getNginxHost() + fileId + "?token=" + token + "&ts=" + timestamp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}