package com.ssafy.nighttrip.course.courseAi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.nighttrip.course.courseAi.dto.CourseAnalyzeResponse;
import com.ssafy.nighttrip.course.courseAi.dto.RecommendCourseRequest;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.review.analysis.service.GmsReviewAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseAnalyzeService {

    private final ObjectMapper objectMapper;
    private final GmsReviewAnalyzer gmsReviewAnalyzer;

    // TODO: 기존 리뷰 분석에서 사용한 GMS 호출 클라이언트로 교체
    // private final GmsClient gmsClient;

    public CourseAnalyzeResponse analyze(RecommendCourseRequest request) {
        try {
            log.info("코스 분석 시작 city={}, date={}, content={}",
                    request.getCity(),
                    request.getDate(),
                    request.getContent()
            );

            String prompt = buildPrompt(request);

            log.info("GMS 코스 분석 호출 시작");

            String gmsResponse = gmsReviewAnalyzer.chat(prompt);

            log.info("GMS 코스 분석 원본 응답={}", gmsResponse);

            String json = extractJson(gmsResponse);

            log.info("GMS 코스 분석 JSON 추출 결과={}", json);

            return objectMapper.readValue(
                    json,
                    CourseAnalyzeResponse.class
            );

        } catch (BusinessException e) {
            log.warn("코스 분석 비즈니스 예외 발생. code={}, message={}",
                    e.getErrorCode().getCode(),
                    e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error("코스 분석 실패", e);
            throw new BusinessException(ErrorCode.COURSE_ANALYSIS_FAILED);
        }
    }

//    private String callGms(String prompt) {
//        /*
//         * 임시 코드.
//         * 기존 GMS 연동 클래스 연결 전에는 테스트용으로 고정 JSON을 반환해도 됨.
//         */
//        return """
//                {
//                  "city": "부산",
//                  "date": "2026-06-12",
//                  "startPoint": "부산역",
//                  "categoryCount": 3,
//                  "data": [
//                    {
//                      "number": 0,
//                      "category": "식당",
//                      "tags": ["식사", "한식", "해산물"],
//                      "banTags": []
//                    },
//                    {
//                      "number": 1,
//                      "category": "관광지",
//                      "tags": ["야경", "해안", "산책", "오션뷰"],
//                      "banTags": ["실내"]
//                    },
//                    {
//                      "number": 2,
//                      "category": "숙소",
//                      "tags": ["호텔", "오션뷰"],
//                      "banTags": []
//                    }
//                  ]
//                }
//                """;
//    }

    private String buildPrompt(RecommendCourseRequest request) {
        return """
  너는 야간 여행 코스 분석 API 서버의 JSON 생성기다.

            반드시 유효한 JSON 객체 하나만 반환한다.
            설명 문장, 마크다운, 코드블록, 주석, 사과문, 추가 질문은 절대 출력하지 않는다.
            사용자의 요청이 부족하더라도 반드시 기본값을 추론해서 JSON을 반환한다.

            요청 도시와 요청 날짜는 절대 변경하지 않는다.

            요청 도시:
            {city}

            요청 날짜:
            {date}

            사용 가능한 카테고리:
            - 관광지
            - 식당
            - 숙소
            - 술집

            사용 가능한 태그:
            야경, 전망, 해수욕장, 해안, 공원, 체험, 실내, 실외, 문화공간, 시장, 야시장, 스파, 산책, 노을, 먹거리, 사찰, 주차, 식사, 한식, 양식, 일식, 한정식, 육류, 해산물, 생선회, 고기구이, 분식, 뷔페, 술집, 카페, 호텔, 모텔, 펜션, 리조트, 게스트하우스, 한옥, 캠핑, 글램핑, 풀빌라, 오션뷰, 시티뷰

            규칙:
            1. category는 반드시 사용 가능한 카테고리 중 하나만 사용한다.
            2. tags와 banTags는 반드시 사용 가능한 태그 중에서만 선택한다.
            3. 사용자가 싫다고 한 조건은 banTags에 넣는다.
            4. 사용자가 저녁 식사, 밥, 맛집을 원하면 식당을 포함한다.
            5. 사용자가 관광, 야경, 산책, 바다, 전망을 원하면 관광지를 포함한다.
            6. 사용자가 숙박, 자고 감, 1박, 호텔을 원하면 마지막 category는 숙소로 한다.
            7. 사용자가 술, 포차, 맥주, 칵테일, 한잔을 원하면 술집을 포함할 수 있다.
            8. number는 0부터 순서대로 작성한다.
            9. categoryCount는 data 배열의 개수와 같아야 한다.
            10. startPoint를 명확히 알 수 없으면 null로 반환한다.

            사용자 요청:
            {content}

            반드시 아래 JSON 형식 그대로 반환한다.

            {
              "city": "{city}",
              "date": "{date}",
              "startPoint": null,
              "categoryCount": 3,
              "data": [
                {
                  "number": 0,
                  "category": "식당",
                  "tags": ["식사", "한식"],
                  "banTags": []
                },
                {
                  "number": 1,
                  "category": "관광지",
                  "tags": ["야경", "해안", "산책"],
                  "banTags": []
                },
                {
                  "number": 2,
                  "category": "숙소",
                  "tags": ["호텔", "오션뷰"],
                  "banTags": []
                }
              ]
            }
            """
                .replace("{city}", request.getCity())
                .replace("{date}", request.getDate().toString())
                .replace("{content}", request.getContent()
        );
    }

    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            log.warn("GMS 코스 분석 응답이 비어있습니다.");
            throw new BusinessException(ErrorCode.COURSE_INVALID_ANALYSIS_RESULT);
        }

        String trimmed = response.trim();

        int start = trimmed.indexOf("{");
        int end = trimmed.lastIndexOf("}");

        if (start == -1 || end == -1 || start > end) {
            log.warn("GMS 코스 분석 응답에서 JSON 객체를 찾지 못했습니다. response={}", trimmed);
            throw new BusinessException(ErrorCode.COURSE_INVALID_ANALYSIS_RESULT);
        }

        return trimmed.substring(start, end + 1);
    }
}