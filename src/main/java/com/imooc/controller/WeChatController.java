package com.imooc.controller;


import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WeChatController {

    @Autowired
    private WxMpService wxMpService;



    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){

        //1. 配置
        //2. 调用方法
        String url = "http://sellwy.s1.natapp.cc/sell/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url,WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }

    //重定向的,这个方法可以获取到
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                          @RequestParam("state") String returnUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();

        return "redirect:" + returnUrl + "?openid=" + openId;
    }

}
