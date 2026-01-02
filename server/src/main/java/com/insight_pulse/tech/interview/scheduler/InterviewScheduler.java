package com.insight_pulse.tech.interview.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.interview.domain.Interview;
import com.insight_pulse.tech.interview.domain.InterviewRepository;
import com.insight_pulse.tech.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewScheduler {

    private final InterviewRepository interviewRepository;
    private final NotificationService notificationService;
    
    @Scheduled(cron = "0 * * * * *")
    public void scanAndRemind() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindow = now.plusMinutes(15);
        List<Interview> upcomingInterview = interviewRepository.findAllByScheduleAtBetweenAndReminderSentFalse(now, reminderWindow);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM");
        if(upcomingInterview.isEmpty()) return;
        for(Interview interview : upcomingInterview) {
            String formattedTime = interview.getScheduleAt().format(formatter);
            notificationService.createNotification(
                "Sắp đến giờ phỏng vấn", 
                "Ứng viên " + interview.getSubmission().getFullname() + " phỏng vấn lúc " + formattedTime,
                interview.getUser().getId(),
                "INTERVIEW",
                "/campaigns/interviews/" + interview.getId()
            );
            interview.setReminderSent(true);
            interviewRepository.save(interview);
        }
    }
    
}