package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;


@Value.Immutable
public abstract class DefinitionCatalog {
	public static DefinitionCatalog of(Iterable<MessageDefinition> messageDefinitions) {
		return ImmutableDefinitionCatalog.builder()
				.messageDefinitions(messageDefinitions)
				.build();
	}

	public Optional<String> findFieldByMessageAndId(String messageQualifiedName, int fieldId) {
		return messageDefinitions().stream()
				.filter(message -> message.qualifiedName().equals(messageQualifiedName))
				.findAny()
				.flatMap(message -> message.findFieldById(fieldId).map(MessageField::name));
	}

	protected abstract List<MessageDefinition> messageDefinitions();
}

