package com.dot.service;

import com.dot.dto.RetrospectDto;
import com.dot.entity.Retrospect;
import com.dot.entity.User;
import com.dot.repository.RetrospectRepository;
import com.dot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectService {

    private final RetrospectRepository retrospectRepository;
    private final UserRepository userRepository;

    // 월별 캘린더 데이터 (날짜 + 점수만)
    public List<RetrospectDto.CalendarItem> getCalendarData(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return retrospectRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(r -> RetrospectDto.CalendarItem.builder()
                        .id(r.getId())
                        .date(r.getDate())
                        .score(r.getScore())
                        .colorTheme(r.getColorTheme())
                        .build())
                .toList();
    }

    // 특정 날짜 회고 조회
    public RetrospectDto.Response getByDate(Long userId, LocalDate date) {
        return retrospectRepository.findByUserIdAndDate(userId, date)
                .map(RetrospectDto.Response::from)
                .orElse(null);
    }

    // 특정 ID 회고 조회
    public RetrospectDto.Response getById(Long userId, Long id) {
        Retrospect retrospect = retrospectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));
        validateOwner(retrospect, userId);
        return RetrospectDto.Response.from(retrospect);
    }

    // 생성
    @Transactional
    public RetrospectDto.Response create(Long userId, RetrospectDto.Request request) {
        // 같은 날짜에 이미 있으면 예외
        if (retrospectRepository.findByUserIdAndDate(userId, request.getDate()).isPresent()) {
            throw new RuntimeException("해당 날짜에 이미 회고가 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Retrospect retrospect = Retrospect.builder()
                .user(user)
                .date(request.getDate())
                .keep(request.getKeep())
                .problem(request.getProblem())
                .tryContent(request.getTryContent())
                .score(request.getScore() != null ? request.getScore() : 5)
                .colorTheme(request.getColorTheme() != null ? request.getColorTheme() : "default")
                .build();

        return RetrospectDto.Response.from(retrospectRepository.save(retrospect));
    }

    // 수정
    @Transactional
    public RetrospectDto.Response update(Long userId, Long id, RetrospectDto.Request request) {
        Retrospect retrospect = retrospectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));
        validateOwner(retrospect, userId);

        retrospect.setKeep(request.getKeep());
        retrospect.setProblem(request.getProblem());
        retrospect.setTryContent(request.getTryContent());
        if (request.getScore() != null) retrospect.setScore(request.getScore());
        if (request.getColorTheme() != null) retrospect.setColorTheme(request.getColorTheme());

        return RetrospectDto.Response.from(retrospectRepository.save(retrospect));
    }

    // 삭제
    @Transactional
    public void delete(Long userId, Long id) {
        Retrospect retrospect = retrospectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));
        validateOwner(retrospect, userId);
        retrospectRepository.delete(retrospect);
    }

    private void validateOwner(Retrospect retrospect, Long userId) {
        if (!retrospect.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
    }
}
