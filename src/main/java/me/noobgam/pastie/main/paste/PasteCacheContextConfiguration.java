package me.noobgam.pastie.main.paste;

import me.noobgam.pastie.main.background.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(PasteDaoContextConfiguration.class)
public class PasteCacheContextConfiguration {
    @Bean
    public PasteCache pasteCache(
            PasteDao pasteDao,
            Scheduler scheduler
    ) {
        return new PasteCache(scheduler, pasteDao);
    }
}
