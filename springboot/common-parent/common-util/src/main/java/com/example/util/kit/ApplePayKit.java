package com.example.util.kit;

import com.alibaba.fastjson.JSON;
import com.example.util.http.HttpRestUtils;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplePayKit {

    private static final HttpRestUtils HTTP_REST_UTILS = HttpRestUtils.getDefaultInstance();

    //购买凭证验证地址
    private static final String RECEIPT_VERIFY_URL = "https://buy.itunes.apple.com/verifyReceipt";

    //测试的购买凭证验证地址
    private static final String TEST_RECEIPT_VERIFY_URL = "https://sandbox.itunes.apple.com/verifyReceipt";


    public static void main(String[] args) throws IOException {

        FileInputStream fileInputStream = new FileInputStream("d:/receipt.txt");
        String receipt = IOUtils.toString(fileInputStream);
        System.out.println(receipt);


        /**
         * 官方建议无论如何按照正式环境校验 如果正式环境无法通过则通过测试环境校验
         */
        ApplePayReceiptVerifyResult aaa = verifyReceipt(receipt, false);
        if (aaa != null && "21007".equals(aaa.getStatus())) {
            aaa = verifyReceipt(receipt, true);
        }

        System.out.println(JSON.toJSONString(aaa));
        fileInputStream.close();
    }
    /**
     * 校验参数
     * @param receipt
     * @param isSandbox true代表沙箱环境验证false代表正式环境
     * @return
     */
    public static ApplePayReceiptVerifyResult verifyReceipt(String receipt, boolean isSandbox) {
        String url = RECEIPT_VERIFY_URL;
        if (isSandbox) {
            url = TEST_RECEIPT_VERIFY_URL;
        }

        Map<String, String> params = new HashMap<>();
        params.put("receipt-data", receipt);
        Pair<Integer, ApplePayReceiptVerifyResult> result = HTTP_REST_UTILS.post(url, params, null, ApplePayReceiptVerifyResult.class);
        return result.getRight();
    }


    @Data
    public static class ApplePayReceiptVerifyResult {
        private String environment;

        /**
         * 0     成功
         * 21000 App Store无法读取你提供的JSON数据
         * 21002 收据数据不符合格式
         * 21003 收据无法被验证
         * 21004 你提供的共享密钥和账户的共享密钥不一致
         * 21005 收据服务器当前不可用
         * 21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中
         * 21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证
         * 21008 收据信息是产品环境中使用，但却被发送到测试环境中验证
         */
        private String status;

        private ReceiptInfo receipt;


        @Data
        public static class ReceiptInfo {

            private List<InAppInfo> in_app;

            private String adam_id;
            private String receipt_creation_date;
            private String original_application_version;
            private String app_item_id;
            private String original_purchase_date_ms;
            private String request_date_ms;
            private String original_purchase_date_pst;
            private String original_purchase_date;
            private String receipt_creation_date_pst;
            private String receipt_type;
            private String bundle_id;
            private String receipt_creation_date_ms;
            private String version_external_identifier;
            private String request_date_pst;
            private String download_id;
            private String application_version;
            @Data
            public static class InAppInfo{
                /**
                 * Apple的订单号
                 */
                private String transaction_id;
                private String original_purchase_date;
                private String quantity;
                private String original_transaction_id;
                private String purchase_date_pst;
                private String original_purchase_date_ms;
                private String purchase_date_ms;
                private String product_id;
                private String original_purchase_date_pst;
                private String is_trial_period;
                private String purchase_date;

                /**
                 * 转为中国时间
                 * @return
                 */
                public Date getChinaDate() {

                    return new Date(Long.parseLong(purchase_date_ms));
                }

            }


        }


    }


}
