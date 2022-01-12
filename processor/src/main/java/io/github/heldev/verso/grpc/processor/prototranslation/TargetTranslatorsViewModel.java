package io.github.heldev.verso.grpc.processor.prototranslation;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.util.List;


@Value.Immutable
public interface TargetTranslatorsViewModel {
	static Builder builder() {
		return ImmutableTargetTranslatorsViewModel.builder();
	}

	String javaPackage();
	String name();
	TypeMirror targetBuilderType();

	default String builderFactoryMethod() {
		return "builder";
	}

	TypeMirror targetType();
	TypeMirror sourceType();
	List<AttributeViewModel> optionalAttributesWithNonOptionalArguments();
	List<GeneratedAttribute> generatedAttributes();
	List<AttributeViewModel> regularAttributes();

	interface Builder {
		Builder javaPackage(String javaPackage);
		Builder name(String name);
		Builder targetBuilderType(TypeMirror targetBuilderType);
		Builder targetType(TypeMirror targetType);
		Builder sourceType(TypeMirror sourceType);
		Builder optionalAttributesWithNonOptionalArguments(Iterable<? extends AttributeViewModel> attributes);
		Builder generatedAttributes(Iterable<? extends GeneratedAttribute> attributes);
		Builder regularAttributes(Iterable<? extends AttributeViewModel> attributes);
		TargetTranslatorsViewModel build();
	}
}
