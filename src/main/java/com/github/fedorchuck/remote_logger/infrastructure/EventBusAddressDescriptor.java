package com.github.fedorchuck.remote_logger.infrastructure;

/**
 * This interface allows to convert event bus address enum to real address string
 */
public interface EventBusAddressDescriptor {
    String address();
}
