package org.spongepowered.api.service.persistence.data;

import com.google.common.base.Optional;

public class MemoryDataContainer extends MemoryDataView implements DataContainer {

    @Override
    public Optional<DataView> getParent() {
        return Optional.absent();
    }

    @Override
    public final DataContainer getContainer() {
        return this;
    }
}
