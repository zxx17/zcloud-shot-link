package com.zskj.biz;

import com.zskj.link.LinkApplication;
import com.zskj.link.manager.DomainManager;
import com.zskj.link.model.DomainDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/2
 * <p>
 *
 * </p>
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class DomainTest {

    @Autowired
    private DomainManager domainManager;

    @Test
    public void testDomain() {
        List<DomainDO> domainDOS = domainManager.listOfficialDomain();
        log.info("{}", domainDOS);
    }
}
