package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.News;

import java.time.LocalDate;
import java.util.List;

public interface NewsGenerationService {
    News generateNewsFor(LocalDate localDate);
}
