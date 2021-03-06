package io.github.heldev.verso.grpc.processor.prototranslation;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.immutables.value.Value;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static io.github.heldev.verso.grpc.processor.common.Utils.panic;

@Value.Immutable
public interface TargetType {
	static Builder builder() {
		return ImmutableTargetType.builder();
	}

	TypeMirror type();
	TypeMirror builderType();
	Name builderFactoryMethod();
	List<TargetField> attributes();
	List<GeneratedAttribute> generatedAttributes();

	String protoMessage();

	@Value.Check
	default void check() {
		if (protoMessage().trim().isEmpty()) {
			panic(this + " has an empty proto message");
		}
	}

	default String javaPackage() {
		return ((ClassName) TypeName.get(type())).packageName();
	}

	interface Builder {
		Builder type(TypeMirror type);
		Builder protoMessage(String protoMessage);
		Builder builderType(TypeMirror builderType);
		Builder builderFactoryMethod(Name builderFactoryMethod);
		Builder attributes(Iterable<? extends TargetField> fields);
		Builder generatedAttributes(Iterable<? extends GeneratedAttribute> fields);
		TargetType build();
	}
}
