package com.coffeeproject.domain.delivery.api;

import com.coffeeproject.domain.delivery.service.DeliverySchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/vi/admin/scheduler")
@RequiredArgsConstructor
public class ApiV1DeliverySchedulerController {
    private final DeliverySchedulerService schedulerControlService;

    /**
     * Cron표현식을 인자로 받아 해당 주기마다 배치 작업을 수행합니다. 기본값은 "0 0 14 * * ?" 으로 매일 오후2 시입니다.
     */
    @GetMapping("/start")
    public String startDeliveryScheduler(@RequestParam(defaultValue = "0 0 14 * * ?") String cronExpression) {
        return schedulerControlService.startScheduler(cronExpression);
    }

    @GetMapping("/stop")
    public String stopDeliveryScheduler() {
        return schedulerControlService.stopScheduler();
    }
}
