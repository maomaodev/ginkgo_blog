package com.ginkgoblog.commons.feign;

import com.ginkgoblog.commons.config.FeignConfig;
import com.ginkgoblog.commons.vo.FileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Picture远程接口
 *
 * @author maomao
 * @date 2021-01-20
 */
@FeignClient(name = "ginkgo-picture", configuration = FeignConfig.class)
public interface PictureFeignClient {

    /**
     * 获取文件的信息接口
     *
     * @param fileIds
     * @param code
     * @return
     */
    @GetMapping("/file/getPicture")
    String getPicture(@RequestParam("fileIds") String fileIds, @RequestParam("code") String code);

    /**
     * 通过URL List上传图片
     *
     * @param fileVO
     * @return
     */
    @PostMapping("/file/uploadPicsByUrl2")
    String uploadPicsByUrl(FileVO fileVO);
}
