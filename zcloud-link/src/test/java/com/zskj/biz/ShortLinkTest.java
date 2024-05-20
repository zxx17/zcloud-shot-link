package com.zskj.biz;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.zskj.common.util.CommonUtil;
import com.zskj.link.LinkApplication;
import com.zskj.link.component.ShortLinkComponent;
import com.zskj.link.manager.ShortLinkManager;
import com.zskj.link.model.ShortLinkDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/28
 * <p>
 *
 * </p>
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class ShortLinkTest {


    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private ShortLinkComponent shortLinkComponent;

    /**
     * 测试murmur算法
     */
    @Test
    public void testShortLinkGenerate() {
        String originUrl = "https://www.baidu.com/";
        HashCode hashCode = Hashing.murmur3_32_fixed().hashUnencodedChars(originUrl);
        long padToLong = hashCode.padToLong();
        log.info(String.valueOf(padToLong));
    }


    /**
     * 测试短链生成
     */
    @Test
    public void testShortLink() {
        // 随机生成长链接转短链
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            String longLink = "https://www.baidu.com/"+random.nextInt(1000000);
            String shortLink = shortLinkComponent.createShortLinkCode(longLink);
            log.info("短链接：{}", shortLink);
        }
    }


    /**
     * 测试保存短链接的方法。
     * 该方法通过生成随机数字构造原始URL，然后调用短链接组件创建短链接码，并将这些信息保存到短链接数据对象中，
     * 最后通过短链接管理器添加这个短链接对象到数据库。
     * 该测试方法不接受任何参数并且没有返回值。
     */
    @Test
    public void testSaveShortLink() {
        // 创建一个随机数生成器
        Random random = new Random();
        // 循环执行10次，以测试多次保存短链接的逻辑
        for (int i = 0; i < 10; i++) {
            // 生成随机数作为原始URL的一部分
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(100000000);
            int num3 = random.nextInt(100000000);
            // 使用随机数构造原始URL字符串
            String originalUrl = num1 + "zlink" + num2 + ".net" + num3;
            // 调用短链接组件，为原始URL创建短链接码
            String shortLinkCode = shortLinkComponent.createShortLinkCode(originalUrl);

            // 创建短链接数据对象，并填充相关信息
            ShortLinkDO shortLinkDO = new ShortLinkDO();
            shortLinkDO.setCode(shortLinkCode);
            shortLinkDO.setAccountNo((long) num3);
            // 使用MD5算法为原始URL生成签名
            shortLinkDO.setSign(CommonUtil.MD5(originalUrl));
            // 设置删除标志为0，表示未删除
            shortLinkDO.setDel(0);
            // 调用短链接管理器，将短链接对象保存到数据库
            shortLinkManager.addShortLink(shortLinkDO);
        }
    }


    /**
     * 测试根据短链接代码查找短链接信息的功能。
     * 该方法没有参数。
     * 没有直接的返回值，但会通过日志输出找到的短链接信息。
     */
    @Test
    public void testFind(){
        // 通过短链接代码查找对应的短链接信息
        ShortLinkDO shortLinCode = shortLinkManager.findByShortLinkCode("11NMPhoa");
        // 输出找到的短链接信息
        log.info(shortLinCode.toString());
    }


}
