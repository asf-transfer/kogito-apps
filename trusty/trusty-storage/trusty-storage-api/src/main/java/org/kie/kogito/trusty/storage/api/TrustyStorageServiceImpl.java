package org.kie.kogito.trusty.storage.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;

@ApplicationScoped
public class TrustyStorageServiceImpl implements TrustyStorageService {

    private static final String DECISIONS_STORAGE = "decisions";

    @Inject
    StorageService storageService;

    @Override
    public Storage<String, Decision> getDecisionsStorage() {
        return storageService.getCache(DECISIONS_STORAGE, Decision.class);
    }
}
