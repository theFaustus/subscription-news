package com.evil.inc.subscriptionnews.service.contracts;

import com.evil.inc.subscriptionnews.service.dto.News;

import java.time.LocalDate;
import java.util.List;

public interface NewsGenerationService {
    List<News> generateHeadersOnlyNewsFor(LocalDate localDate);
}
