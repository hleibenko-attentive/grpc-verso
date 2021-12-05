package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface MessageDefinition {
	static Builder builder() {
		return ImmutableMessageDefinition.builder();
	}

	String protoPackage();

	String protoFile();

	String javaPackage();

	String name();

	default String qualifiedName() {
		//todo nested messages
		return javaPackage() + "." + name();
	}

	List<MessageField> fields();

	default Optional<MessageField> findFieldById(int fieldId) {
		return fields().stream().filter(field -> field.id() == fieldId).findAny();
	}

	interface Builder {
		Builder protoPackage(String protoPackage);

		Builder protoFile(String protoFile);

		Builder javaPackage(String javaPackage);

		Builder name(String name);

		Builder fields(Iterable<? extends MessageField> fields);

		MessageDefinition build();
	}
}
