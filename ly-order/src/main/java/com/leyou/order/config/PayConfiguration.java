package com.leyou.order.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author bystander
 * @date 2018/10/5
 */
@Configuration
public class PayConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "ly.pay")
    public PayConfig payConfig() {
        return new PayConfig();
    }
    @Bean
    public WXPay wxPay(PayConfig payConfig){
        return  new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }
}
