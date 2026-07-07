package com.dot.controller;

import com.dot.dto.RetrospectDto;
import com.dot.service.RetrospectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/retrospects")
@RequiredArgsConstructor
public class RetrospectController {

    private final RetrospectService retrospectService;

    // 월별 캘린더 데이터
    @GetMapping("/calendar")
    public ResponseEntity<List<RetrospectDto.CalendarItem>> getCalendar(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(retrospectService.getCalendarData(userId, year, month));
    }

    // 특정 날짜 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<RetrospectDto.Response> getByDate(
            @AuthenticationPrincipal Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        RetrospectDto.Response response = retrospectService.getByDate(userId, date);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    // ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<RetrospectDto.Response> getById(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(retrospectService.getById(userId, id));
    }

    // 생성
    @PostMapping
    public ResponseEntity<RetrospectDto.Response> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RetrospectDto.Request request) {
        return ResponseEntity.ok(retrospectService.create(userId, request));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<RetrospectDto.Response> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RetrospectDto.Request request) {
        return ResponseEntity.ok(retrospectService.update(userId, id, request));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        retrospectService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
