package me.noobgam.pastie.main.paste;

import me.noobgam.pastie.main.background.Scheduler;
import me.noobgam.pastie.main.background.cache.SimpleMapCache;
import me.noobgam.pastie.utils.FastUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PasteCache extends SimpleMapCache<String, Paste> {

    private final PasteDao pasteDao;

    PasteCache(Scheduler scheduler, PasteDao pasteDao) {
        super(scheduler);
        this.pasteDao = pasteDao;
    }

    @Override
    public Optional<Map<String, Paste>> calculateCurrent() {
        List<Paste> all = pasteDao.findAll().join();
        HashMap<String, Paste> pastes =
                new HashMap<>(FastUtil.initalCapacityForHashmap(all.size()));
        for (Paste paste : all) {
            pastes.put(paste.getId(), paste);
        }
        return Optional.of(pastes);
    }

    @Override
    public Duration defaultDelay() {
        return Duration.ofMinutes(20);
    }

    @Override
    protected boolean cacheOnStartup() {
        return true;
    }
}
