package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.heldev.verso.grpc.processor.common.Utils.optionalToStream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;

//todo nested definitions
@Value.Immutable
public abstract class MessageDefinition {
	public static Builder builder() {
		return ImmutableMessageDefinition.builder();
	}

	public String qualifiedName() {
		return flattenAndJoinWithDots(
				optionalToStream(context().protoPackage()),
				context().outerMessageNames().stream(),
				Stream.of(name()));
	}

	public String qualifiedClass() {
		return flattenAndJoinWithDots(
				optionalToStream(context().javaPackage()),
				optionalToStream(context().buildOuterJavaClassName()),
				Stream.of(name()));
	}

	protected abstract MessageDefinitionContext context();

	protected abstract String name();

	public Optional<MessageField> findFieldById(int fieldId) {
		return fields().stream().filter(field -> field.id() == fieldId).findAny();
	}

	protected abstract List<MessageField> fields();

	@SafeVarargs
	private final String flattenAndJoinWithDots(Stream<String>... streams) {
		return Stream.of(streams)
				.flatMap(identity())
				.collect(joining("."));
	}

	public interface Builder {
		Builder context(MessageDefinitionContext messageDefinitionContext);
		Builder name(String name);
		Builder fields(Iterable<? extends MessageField> fields);

		MessageDefinition build();
	}
}
