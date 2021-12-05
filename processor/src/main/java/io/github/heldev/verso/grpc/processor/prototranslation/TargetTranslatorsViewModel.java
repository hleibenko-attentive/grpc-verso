package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.processor.prototranslation.field.FieldSource;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.util.Map;


@Value.Immutable
public interface TargetTranslatorsViewModel {
	static Builder builder() {
		return ImmutableTargetTranslatorViewModel.builder();
	}

	String javaPackage();
	String name();
	TypeMirror targetBuilderType();
	TypeMirror targetType();
	TypeMirror sourceType();
	Map<String, FieldSource> fieldSources();

	interface Builder {
		Builder javaPackage(String javaPackage);
		Builder name(String name);
		Builder targetBuilderType(TypeMirror targetBuilderType);
		Builder targetType(TypeMirror targetType);
		Builder sourceType(TypeMirror sourceType);
		Builder fieldSources(Map<String, ? extends FieldSource> filedSources);
		TargetTranslatorsViewModel build();
	}
}
