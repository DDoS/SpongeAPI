package org.spongepowered.api.service.persistence;

import org.spongepowered.api.service.persistence.data.DataContainer;

import com.google.common.base.Optional;

/**
 * Represents a builder that can take a {@link DataContainer} and create a
 * new instance of a {@link DataSerializable}. The builder should be a
 * singleton and may not exist for every data serializable.
 *
 * @param <T> The type of data serializable this builder can build
 */
public interface DataSerializableBuilder<T extends DataSerializable> {

    /**
     * Attempts to build the provided {@link DataSerializable} from the given
     * {@link DataContainer}. If the {@link DataContainer} is invalid or
     * missing necessary information to complete building the
     * {@link DataSerializable}, {@link Optional#absent()} may be returned.
     *
     * @param container The container containing all necessary data
     * @return The instance of the {@link DataSerializable}, if successful
     */
    Optional<T> build(DataContainer container);

}
