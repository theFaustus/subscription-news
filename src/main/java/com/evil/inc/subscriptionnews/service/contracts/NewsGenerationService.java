package com.evil.inc.subscriptionnews.service.contracts;

import com.evil.inc.subscriptionnews.domain.News;

import java.time.LocalDate;
import java.util.List;

public interface NewsGenerationService {
    List<News> generateHeadersOnlyNewsFor(LocalDate localDate);
}
