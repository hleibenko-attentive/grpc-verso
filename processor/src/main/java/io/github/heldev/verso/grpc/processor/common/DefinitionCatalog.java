package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;


@Value.Immutable
public interface DefinitionCatalog {
	static DefinitionCatalog of(Iterable<MessageDefinition> messageDefinitions) {
		return ImmutableDefinitionCatalog.builder()
				.messageDefinitions(messageDefinitions)
				.build();
	}

	List<MessageDefinition> messageDefinitions();
}

