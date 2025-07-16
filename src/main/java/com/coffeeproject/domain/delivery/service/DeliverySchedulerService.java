package com.coffeeproject.domain.delivery.service;

import com.coffeeproject.domain.delivery.task.DeliveryBatchTask;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class DeliverySchedulerService {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final DeliveryBatchTask deliveryBatchTask;
    private ScheduledFuture<?> scheduledTask;

    /**
     * 스케줄링 시작 메서드
     */
    public String startScheduler(String cronExpression) {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            return "스케줄러가 이미 실행 중입니다.";
        }
        scheduledTask = taskScheduler.schedule(deliveryBatchTask, new CronTrigger(cronExpression));
        System.out.println("스케줄러 시작됨.");
        return "스케줄러가 성공적으로 시작되었습니다.";
    }

    /**
     * 스케줄링 중지 메서드
     * cancel(true)는 현재 실행 중인 태스크를 인터럽트하여 중지시키려고 시도합니다.
     * false는 현재 실행 중인 태스크는 완료하고 다음 태스크부터 실행하지 않습니다.
     */
    public String stopScheduler() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
            System.out.println("스케줄러 중지됨.");
            return "스케줄러가 성공적으로 중지되었습니다.";
        }
        return "실행 중인 스케줄러가 없습니다.";
    }
}
