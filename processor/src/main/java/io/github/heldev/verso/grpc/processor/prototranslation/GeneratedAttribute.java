package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.processor.Generator;
import org.immutables.value.Value;

@Value.Immutable
public interface GeneratedAttribute {

	static Builder builder() {
		return ImmutableGeneratedAttribute.builder();
	}

	String name();
	Generator generator();

	interface Builder {
		Builder name(String name);
		Builder generator(Generator generator);

		GeneratedAttribute build();
	}
}
