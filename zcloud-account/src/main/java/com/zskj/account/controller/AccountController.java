package com.zskj.account.controller;


import com.zskj.account.controller.request.AccountLoginRequest;
import com.zskj.account.controller.request.AccountRegisterRequest;
import com.zskj.account.service.AccountService;
import com.zskj.account.storage.IFileStorage;
import com.zskj.common.enums.BizCodeEnum;
import com.zskj.common.util.CommonUtil;
import com.zskj.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 账号服务前端控制器
 * </p>
 *
 * @author Xinxuan Zhuo
 * @since 2024-04-22
 */
@RestController
@RequestMapping("/api/account/v1")
public class AccountController {

    @Autowired
    private IFileStorage fileStorage;

    @Autowired
    private AccountService accountService;


    /**
     * 文件上传 最大默认1M
     * 文件格式、拓展名等判断
     *
     * @param file 用户头像
     * @return jsonData
     */
    @PostMapping("/upload")
    public JsonData uploadUserImg(@RequestPart("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()){
            //获取文件原始名称 xxx.jpg
            String originalFilename = file.getOriginalFilename();
            //jdk8语法日期格式
            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            // user/2022/12/12/sdsdwe/
            String folder = pattern.format(ldt);
            String fileName = CommonUtil.generateUUID();
            assert originalFilename != null;
            String extendsion = originalFilename.substring(originalFilename.lastIndexOf("."));
            //在oss上的bucket创建文件夹
            String key = "user/" + folder + "/" + fileName + extendsion;
            // 上传
            String result = fileStorage.uploadFile(key, inputStream, file.getSize());
            return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
        } catch (Exception e) {
            return JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
        }
    }


    /**
     * 用户注册
     * @param request 注册表单
     * @return res
     */
    @PostMapping("/register")
    public JsonData register(@RequestBody @Validated AccountRegisterRequest request){
        return accountService.register(request);
    }

    /**
     * 用户登录
     * @param request 登录表单数据
     * @return res
     */
    @PostMapping("/login")
    public JsonData login(@RequestBody @Validated AccountLoginRequest request){
        return accountService.login(request);
    }

}

