package com.evil.inc.subscriptionnews.fixture;

import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NewsFixture {
    private LocalDate date;
    private String author;
    private String title;
    private String content;
    private String source;
    private NewsType type;


    public static NewsFixture fullNews() {
        return NewsFixture.builder().content("Full").date(LocalDate.parse("1970-01-01")).type(NewsType.FULL).build();
    }

    public static NewsFixture headerOnlyNews() {
        return NewsFixture.builder().content("Headers").date(LocalDate.parse("1970-01-01")).type(NewsType.HEADERS_ONLY).build();
    }

    public News get() {
        return new News(date, author, title, content, source, type);
    }
}
