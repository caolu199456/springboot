package com.example.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.example.pay.sdk.wx.IWXPayDomain;
import com.example.pay.sdk.wx.WXPayConfig;
import com.example.pay.sdk.wx.WXPayConstants;
import com.example.util.spring.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.*;
import java.net.URL;

@Configuration
public class PayConfig {
    /**
     * 多例提高性能
     * @return
     */
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                SpringUtils.getConfigValue("pay.zfb.appId"),
                SpringUtils.getConfigValue("pay.zfb.privateKey"),
                "json",
                "UTF-8",
                SpringUtils.getConfigValue("pay.zfb.publicKey"),
                "RSA2");
    }

    private static byte[] h5CertBytes = null;
    /**
     * 然后使用new WXPay()
     * @return
     */
    @Bean("wxH5Pay")
    public WXPayConfig wxH5Pay() {
        if (h5CertBytes == null) {
            String h5CertPath = SpringUtils.getConfigValue("pay.wx.h5.certPath");
            if (StringUtils.isNotBlank(h5CertPath)) {
                if (h5CertPath.startsWith("classpath")) {
                    String filePath = h5CertPath.substring(h5CertPath.indexOf(":") + 1);
                    try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filePath)){
                        h5CertBytes = IOUtils.toByteArray(resourceAsStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    File certFile = new File(h5CertPath);
                    try (FileInputStream fis = new FileInputStream(certFile)) {
                        h5CertBytes = IOUtils.toByteArray(fis);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new WXPayConfig() {
            @Override
            public String getAppID() {
                return SpringUtils.getConfigValue("pay.wx.h5.appId");
            }

            @Override
            public String getSecret() {
                return SpringUtils.getConfigValue("pay.wx.h5.secret");
            }

            @Override
            public String getMchID() {
                return SpringUtils.getConfigValue("pay.wx.h5.mchId");
            }

            @Override
            public String getKey() {
                return SpringUtils.getConfigValue("pay.wx.h5.key");
            }

            @Override
            public InputStream getCertStream() {
                return new ByteArrayInputStream(h5CertBytes);
            }

            @Override
            public IWXPayDomain getWXPayDomain() {
                return new IWXPayDomain() {
                    @Override
                    public void report(String domain, long elapsedTimeMillis, Exception ex) {

                    }
                    @Override
                    public DomainInfo getDomain(WXPayConfig config) {
                        return new DomainInfo(WXPayConstants.DOMAIN_API, true);
                    }
                };
            }
        };
    }

    private static byte[] appCertBytes = null;
    /** 然后使用new WXPay()
     * @return
     */
    @Bean("wxAppPay")
    public WXPayConfig wxAppPay() {
        if (appCertBytes == null) {
            String appCertPath = SpringUtils.getConfigValue("pay.wx.app.certPath");
            if (StringUtils.isNotBlank(appCertPath)) {
                if (appCertPath.startsWith("classpath")) {
                    String filePath = appCertPath.substring(appCertPath.indexOf(":") + 1);
                    try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filePath)){
                        h5CertBytes = IOUtils.toByteArray(resourceAsStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    File certFile = new File(appCertPath);
                    try (FileInputStream fis = new FileInputStream(certFile)) {
                        h5CertBytes = IOUtils.toByteArray(fis);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new WXPayConfig() {
            @Override
            public String getAppID() {
                return SpringUtils.getConfigValue("pay.wx.app.appId");
            }
            @Override
            public String getSecret() {
                return SpringUtils.getConfigValue("pay.wx.app.secret");
            }
            @Override
            public String getMchID() {
                return SpringUtils.getConfigValue("pay.wx.app.mchId");
            }

            @Override
            public String getKey() {
                return SpringUtils.getConfigValue("pay.wx.app.key");
            }

            @Override
            public InputStream getCertStream() {
                return new ByteArrayInputStream(appCertBytes);
            }

            @Override
            public IWXPayDomain getWXPayDomain() {
                return new IWXPayDomain() {
                    @Override
                    public void report(String domain, long elapsedTimeMillis, Exception ex) {

                    }
                    @Override
                    public DomainInfo getDomain(WXPayConfig config) {
                        return new DomainInfo(WXPayConstants.DOMAIN_API, true);
                    }
                };
            }
        };
    }


}
