package com.zskj.link.component;

import com.zskj.common.util.CommonUtil;
import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.stereotype.Component;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/28
 * <p>
 * 短链生成组件
 * </p>
 */

@Component
public class ShortLinkComponent {

    /**
     * 62个字符
     */
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 创建短链压缩码
     *
     * @param longLink 长链
     * @return 短链
     */
    public String createShortLinkCode(String longLink) {
        long murmurHash32 = CommonUtil.murmurHash32(longLink);
        // 进制转换
        return encodeToBase62(murmurHash32);
    }

    /**
     * 将10进制数转换为62进制
     *
     * @param num 10进制数
     * @return 62进制数
     */
    private String encodeToBase62(long num) {
        // StringBuffer线程安全，StringBuilder线程不安全 [方法内调用，不会出现线程安全问题，sb效率更高]
        StringBuilder sb = new StringBuilder();
        do {
            int i = (int) (num % 62);
            sb.append(CHARS.charAt(i));
            num = num / 62;
        } while (num > 0);

        return sb.reverse().toString();

    }


}
