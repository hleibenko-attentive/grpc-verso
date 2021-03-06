package io.github.heldev.verso.grpc.processor.prototranslation;


import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.common.Utils.panic;

//todo it does not represent target fields anymore
@Value.Immutable
public abstract class TargetField {
	public static Builder builder() {
		return ImmutableTargetField.builder();
	}

	public abstract String attribute();
	public abstract TypeMirror type();
	public abstract Optional<OptionalAttributeType> optionalAttributeType();
	public abstract String protobufGetter();
	public abstract TypeMirror protobufType();

	public abstract Optional<String> presenceCheckingMethod();


	@Value.Check
	protected void check() {
		if (attribute().trim().isEmpty()) {
			panic(this + " has an empty proto message");
		} else if (protobufGetter().trim().isEmpty()) {
			panic(this + " has an empty proto getter");
		}
	}


	public interface Builder {
		Builder attribute(String name);
		Builder type(TypeMirror type);
		Builder optionalAttributeType(OptionalAttributeType optionalAttributeType);
		Builder protobufGetter(String protobufGetter);
		Builder protobufType(TypeMirror protobufType);
		Builder presenceCheckingMethod(String method);
		TargetField build();
	}
}
