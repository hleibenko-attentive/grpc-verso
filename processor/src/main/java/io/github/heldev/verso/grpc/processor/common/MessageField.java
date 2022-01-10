package io.github.heldev.verso.grpc.processor.common;

import io.github.heldev.verso.grpc.processor.common.type.ProtoType;
import org.immutables.value.Value;

@Value.Immutable
public interface MessageField {
	static Builder builder() {
		return ImmutableMessageField.builder();
	}

	int id();
	String name();

	@Value.Default
	default boolean isOptional() {
		return false;
	}

	ProtoType protoType();

	interface Builder {
		Builder id(int id);
		Builder name(String name);
		Builder isOptional(boolean isOptional);
		Builder protoType(ProtoType type);

		MessageField build();
	}
}
