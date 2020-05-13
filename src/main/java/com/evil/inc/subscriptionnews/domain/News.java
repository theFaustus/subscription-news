package com.evil.inc.subscriptionnews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private LocalDate date;
    private String author;
    private String title;
    private String content;
    private String source;
    private NewsType type;
}
