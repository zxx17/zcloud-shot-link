package com.zskj.account.controller;

import com.zskj.account.controller.request.TrafficPageRequest;
import com.zskj.account.controller.request.UseTrafficRequest;
import com.zskj.account.service.TrafficService;
import com.zskj.account.vo.TrafficVO;
import com.zskj.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Xinxuan Zhuo
 * @version 2024/5/22
 * <p>
 *
 * </p>
 */

@RestController
@RequestMapping("/api/traffic/v1")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @Value("${rpc.token}")
    private String rpcToken;

    /**
     * 使用流量包API
     *
     * @param useTrafficRequest request
     * @param request http request
     * @return jsonData
     */
    @PostMapping("/reduce")
    public JsonData useTraffic(@RequestBody UseTrafficRequest useTrafficRequest, HttpServletRequest request){
        String requestToken = request.getHeader("rpc-token");
        if(rpcToken.equalsIgnoreCase(requestToken)){
            //具体使用流量包逻辑
            return trafficService.reduce(useTrafficRequest);
        }else {
            return JsonData.buildError("非法访问");
        }

    }



    /**
     * 分页查询流量包列表，查看可用的流量包
     * @param request  request
     * @return JsonData
     */
    @RequestMapping("/page")
    public JsonData pageAvailable(@RequestBody TrafficPageRequest request){
        Map<String,Object> pageMap = trafficService.pageAvailable(request);
        return JsonData.buildSuccess(pageMap);
    }


    /**
     * 查找某个流量包详情
     * @param trafficId 流量包id
     * @return JsonData
     */
    @GetMapping("/detail/{trafficId}")
    public JsonData detail(@PathVariable("trafficId") long trafficId){
        TrafficVO trafficVO = trafficService.detail(trafficId);
        return JsonData.buildSuccess(trafficVO);
    }




}
