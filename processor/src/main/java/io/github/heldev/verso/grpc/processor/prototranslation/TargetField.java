package io.github.heldev.verso.grpc.processor.prototranslation;


import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

//todo it does not represent target fields anymore
@Value.Immutable
public abstract class TargetField {
	public static Builder builder() {
		return ImmutableTargetField.builder();
	}

	public abstract String getter();
	public abstract TypeMirror type();
	public abstract String protobufGetter();
	public abstract TypeMirror protobufType();


	@Value.Check
	protected void check() {
		if (getter().trim().isEmpty()) {
			throw new RuntimeException(this + " has an empty proto message");
		}
	}

	public interface Builder {
		Builder getter(String name);
		Builder type(TypeMirror type);
		Builder protobufGetter(String protobufGetter);
		Builder protobufType(TypeMirror protobufType);
		TargetField build();
	}
}
