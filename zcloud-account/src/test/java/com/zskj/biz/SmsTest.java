package com.zskj.biz;

import com.zskj.account.AccountApplication;
import com.zskj.account.component.SmsComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/22
 * <p>
 *
 * </p>
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class SmsTest {
    @Autowired
    private SmsComponent smsComponent;

    @Test
    public  void testSendSms(){
        smsComponent.send("13960486856","6666");
    }
}
