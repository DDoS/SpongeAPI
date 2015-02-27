package org.spongepowered.api.service.persistence;

import org.spongepowered.api.service.persistence.data.DataContainer;

/**
 * A service that manages {@link DataSerializableBuilder}s and sometimes the
 * deserialization of various {@link DataSerializable}s.
 */
public interface SerializationSerive {

    /**
     * Registers a {@link DataSerializableBuilder} that will dynamically build
     * the given {@link DataSerializable} from a {@link DataContainer}.
     *
     * <p>Builders may not always exist for a given {@link DataSerializable},
     * nor is it guaranteed that a provided builder will function with all
     * {@link DataContainer}s.
     * </p>
     *
     * @param clazz The class of the {@link DataSerializable}
     * @param builder The builder that can build the data serializable
     * @param <T> The type of data serializable
     */
    <T extends DataSerializable> void registerBuilder(Class<T> clazz,
            DataSerializableBuilder<T> builder);

}
