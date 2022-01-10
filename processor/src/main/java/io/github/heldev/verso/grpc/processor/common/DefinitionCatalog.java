package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


@Value.Immutable
public abstract class DefinitionCatalog {
	public static DefinitionCatalog of(Collection<MessageDefinition> definitions) {
		return ImmutableDefinitionCatalog.builder()
				.messagesByQualifiedName(indexByQualifiedName(definitions))
				.build();
	}

	private static Map<String, MessageDefinition> indexByQualifiedName(Collection<MessageDefinition> definitions) {
		return definitions.stream().collect(toMap(MessageDefinition::qualifiedName, identity()));
	}

	public Optional<MessageDefinition> findByName(String qualifiedName) {
		return Optional.ofNullable(messagesByQualifiedName().get(qualifiedName));
	}

	protected abstract Map<String, MessageDefinition> messagesByQualifiedName();
}

