package io.github.heldev.verso.grpc.processor.prototranslation;


import com.squareup.javapoet.TypeName;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.zip.ZipFile;

@Value.Immutable
public interface TargetTranslatorViewModel {
	static Builder builder() {
		return ImmutableTargetTranslatorViewModel.builder();
	}

	String javaPackage();
	String name();
	TypeMirror targetBuilderType();
	TypeMirror targetType();
	TypeMirror sourceType();
	Map<String, String> fieldSources();

	interface Builder {
		Builder javaPackage(String javaPackage);
		Builder name(String name);
		Builder targetBuilderType(TypeMirror targetBuilderType);
		Builder targetType(TypeMirror targetType);
		Builder sourceType(TypeMirror sourceType);
		Builder fieldSources(Map<String, ? extends String> filedSources);
		TargetTranslatorViewModel build();
	}
}
