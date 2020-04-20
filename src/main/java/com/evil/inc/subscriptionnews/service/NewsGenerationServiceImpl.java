package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.News;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsGenerationServiceImpl implements NewsGenerationService {
    @Override
    public News generateNewsFor(LocalDate localDate) {
        //gather all the news for the localDate
        return new News(localDate, "A lot of interesting things");
    }
}
