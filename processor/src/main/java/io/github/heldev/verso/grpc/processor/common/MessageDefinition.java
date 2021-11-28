package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface MessageDefinition {
	static Builder builder() {
		return ImmutableMessageDefinition.builder();
	}

	String protoPackage();

	String protoFile();

	String javaPackage();

	String name();

	List<MessageField> fields();

	interface Builder {
		Builder protoPackage(String protoPackage);

		Builder protoFile(String protoFile);

		Builder javaPackage(String javaPackage);

		Builder name(String name);

		Builder fields(Iterable<? extends MessageField> fields);

		MessageDefinition build();
	}
}
