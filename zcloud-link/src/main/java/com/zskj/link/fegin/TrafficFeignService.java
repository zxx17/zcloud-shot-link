package com.zskj.link.fegin;

import com.zskj.common.util.JsonData;
import com.zskj.link.controller.request.UseTrafficRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name="zcloud-account-service")
public interface TrafficFeignService {

    @PostMapping(value = "/api/traffic/v1/reduce",headers = {"rpc-token=${rpc.token}"})
    JsonData useTraffic(@RequestBody UseTrafficRequest request);


}
